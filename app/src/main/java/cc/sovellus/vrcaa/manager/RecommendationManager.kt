/*
 * Copyright (C) 2026. Nyabsi <nyabsi@sovellus.cc>
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *         http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package cc.sovellus.vrcaa.manager

import android.util.Log
import cc.sovellus.vrcaa.BuildConfig
import cc.sovellus.vrcaa.api.vrchat.http.models.Instance
import cc.sovellus.vrcaa.base.BaseManager
import cc.sovellus.vrcaa.api.vrchat.http.models.World
import cc.sovellus.vrcaa.manager.ApiManager.api
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import java.time.LocalDateTime

object RecommendationManager : BaseManager<RecommendationManager.RecommendationListener>() {

    data class CurrentLocation(
        val instance: Instance,
        val enteredAt: Long
    )

    private val locationLock = Any()
    private var currentLocation: CurrentLocation? = null
    private val locationStateFlow = MutableStateFlow<CurrentLocation?>(null)

    val locationState: StateFlow<CurrentLocation?> = locationStateFlow.asStateFlow()

    interface RecommendationListener {
        suspend fun onLocationChanged(location: CurrentLocation?)
    }

    suspend fun updateLocation(instance: Instance?) {
        val previous = currentLocation

        when {
            previous == null && instance != null -> enter(instance)
            previous != null && instance == null -> leave(previous)
            previous != null && instance != null && previous.instance.worldId != instance.worldId -> {
                leave(previous)
                enter(instance)
            }
            else -> return
        }

        publishLocation()
    }

    private fun enter(instance: Instance) {
        val now = System.currentTimeMillis() / 1000
        currentLocation = CurrentLocation(instance, now)

        val isFirstVisit = DatabaseManager.readLocationByWorldId(instance.worldId) == null

        if (BuildConfig.DEBUG)
            Log.d(TAG, "enter: worldId=${instance.worldId} name=${instance.world.name} firstVisit=$isFirstVisit at=$now")

        if (isFirstVisit) {
            DatabaseManager.writeLocation(buildHistory(instance, 0L))
        }
    }

    private fun leave(previous: CurrentLocation) {
        val now = System.currentTimeMillis() / 1000
        val timeSpent = now - previous.enteredAt

        if (BuildConfig.DEBUG)
            Log.d(TAG, "leave: worldId=${previous.instance.worldId} name=${previous.instance.world.name} timeSpent=${timeSpent}s")

        DatabaseManager.writeLocation(buildHistory(previous.instance, timeSpent))
        currentLocation = null
    }

    private fun buildHistory(instance: Instance, timeSpent: Long): DatabaseManager.LocationHistory {
        return DatabaseManager.LocationHistory(
            worldId = instance.worldId,
            worldName = instance.world.name,
            tags = instance.world.tags,
            authorId = instance.world.authorId,
            authorName = instance.world.authorName,
            thumbnailImageUrl = instance.world.thumbnailImageUrl,
            heat = instance.world.heat,
            popularity = instance.world.popularity,
            favorites = instance.world.favorites,
            visits = instance.world.visits,
            capacity = instance.world.capacity,
            recommendedCapacity = instance.world.recommendedCapacity,
            releaseStatus = instance.world.releaseStatus,
            publicationDate = instance.world.publicationDate,
            timeSpent = timeSpent,
            lastVisited = LocalDateTime.now()
        )
    }

    private suspend fun publishLocation() {
        synchronized(locationLock) {
            locationStateFlow.value = currentLocation
        }
        val snapshot = locationStateFlow.value
        getListeners().forEach { it.onLocationChanged(snapshot) }
    }

    suspend fun recommendWorlds(limit: Int = 20): List<World> {
        val topLocations = DatabaseManager.readTopLocations(limit = 50)
        val bouncedLocations = DatabaseManager.readBouncedLocations(maxTimeSpent = 500, limit = 50)
        val visitedIds = DatabaseManager.readVisitedWorldIds()

        val positiveTagWeights = mutableMapOf<String, Long>()
        for (location in topLocations) {
            for (tag in location.tags) {
                positiveTagWeights.merge(tag, location.timeSpent) { a, b -> a + b }
            }
        }

        val negativeTagCounts = mutableMapOf<String, Int>()
        for (location in bouncedLocations) {
            for (tag in location.tags) {
                negativeTagCounts.merge(tag, 1) { a, b -> a + b }
            }
        }

        val positiveAuthorTags = positiveTagWeights.entries
            .filter { it.key.startsWith("author_tag_") }

        val positiveCutoff = if (positiveAuthorTags.isNotEmpty()) {
            positiveAuthorTags.sumOf { it.value } / positiveAuthorTags.size
        } else 0L

        val bestTags = positiveAuthorTags
            .asSequence()
            .filter { it.value >= positiveCutoff }
            .sortedByDescending { it.value }
            .map { it.key }
            .toSet()

        val negativeAuthorTags = negativeTagCounts.entries
            .filter { it.key.startsWith("author_tag_") }

        val negativeCutoff = if (negativeAuthorTags.isNotEmpty()) {
            negativeAuthorTags.sumOf { it.value.toLong() } / negativeAuthorTags.size
        } else 0L

        val worstTags = negativeAuthorTags
            .asSequence()
            .filter { it.value >= negativeCutoff }
            .filter { it.key !in bestTags }
            .sortedByDescending { it.value }
            .map { it.key }
            .toList()

        if (BuildConfig.DEBUG)
            Log.d(TAG, "recommendWorlds: positiveCutoff=$positiveCutoff negativeCutoff=$negativeCutoff bestTags=$bestTags worstTags=$worstTags visitedCount=${visitedIds.size}")

        val perTagFetch = (limit * 2).coerceAtMost(100)
        val candidates = mutableMapOf<String, World>()

        if (bestTags.isEmpty()) {
            api.worlds.fetchWorldsByName(
                query = "",
                sort = "heat",
                tags = emptyList(),
                notags = worstTags,
                n = (limit * 3).coerceAtMost(100)
            ).forEach { candidates[it.id] = it }
        } else {
            for (tag in bestTags) {
                api.worlds.fetchWorldsByName(
                    query = "",
                    sort = "random",
                    tags = listOf(tag),
                    notags = worstTags,
                    n = perTagFetch
                ).forEach { candidates[it.id] = it }
            }
        }

        if (BuildConfig.DEBUG)
            Log.d(TAG, "recommendWorlds: candidates=${candidates.size}")

        return candidates.values
            .filter { it.id !in visitedIds }
            .sortedWith(
                compareByDescending<World> { world -> world.tags.count { it in bestTags } }
                    .thenByDescending { it.heat }
                    .thenByDescending { it.popularity }
            )
            .take(limit)
    }

    private const val TAG = "RecommendationManager"
}