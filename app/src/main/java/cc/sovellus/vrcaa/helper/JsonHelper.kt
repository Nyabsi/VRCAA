/*
 * Copyright (C) 2025. Nyabsi <nyabsi@sovellus.cc>
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

package cc.sovellus.vrcaa.helper

import com.google.gson.Gson
import com.google.gson.JsonElement
import com.google.gson.JsonObject
import com.google.gson.JsonParser

object JsonHelper {
    fun <T> mergeJson(old: T, new: T, type: Class<T>): T {
        val merged = mergeObjects(JsonParser.parseString(Gson().toJson(old)).asJsonObject, JsonParser.parseString(Gson().toJson(new)).asJsonObject)
        return Gson().fromJson(merged, type)
    }

    fun <T, N> mergeJson(old: T, new: N, type: Class<T>): T {
        val merged = mergeObjects(JsonParser.parseString(Gson().toJson(old)).asJsonObject, JsonParser.parseString(Gson().toJson(new)).asJsonObject)
        return Gson().fromJson(merged, type)
    }

    private fun mergeObjects(old: JsonObject, new: JsonObject): JsonObject {
        val temp = old.deepCopy()
        for (field in old.entrySet()) {
            if (new.has(field.key)) {
                val value = new.get(field.key)
                if (isFieldEmptyOrNull(value))
                    continue
                if (JsonType.getTypeFromElement(old.get(field.key)) != JsonType.getTypeFromElement(value))
                    continue
                temp.remove(field.key)
                temp.add(field.key, value)
            }
        }
        return temp
    }

    private fun isFieldEmptyOrNull(obj: JsonElement): Boolean {
        if (obj.isJsonNull)
            return true
        else if (obj.isJsonObject && (obj.asJsonObject.isEmpty || obj.asJsonObject.isJsonNull))
            return true
        else if (obj.isJsonArray && (obj.asJsonArray.isEmpty || obj.asJsonArray.isJsonNull))
            return true
        return false
    }

    enum class JsonType {
        None,
        String,
        Number,
        Boolean,
        Array,
        Object;

        companion object {
            fun getTypeFromElement(obj: JsonElement): JsonType {
                var type = None
                if (obj.isJsonPrimitive && obj.asJsonPrimitive.isString)
                    type = String
                else if (obj.isJsonPrimitive && obj.asJsonPrimitive.isNumber) // So... Int, Float.. Double..?
                    type = Number
                else if (obj.isJsonPrimitive && obj.asJsonPrimitive.isBoolean)
                    type = Boolean
                else if (obj.isJsonArray)
                    type = Array
                else if (obj.isJsonObject)
                    type = Object
                return type
            }
        }
    }
}