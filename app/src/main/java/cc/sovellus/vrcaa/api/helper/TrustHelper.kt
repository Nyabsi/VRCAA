package cc.sovellus.vrcaa.api.helper

import androidx.compose.ui.graphics.Color

class TrustHelper {

    enum class Rank {
        Visitor,
        NewUser,
        User,
        Known,
        Trusted;

        companion object {
            fun toString(rank: Rank): String {
                return when(rank) {
                    Rank.Trusted -> "Trusted"
                    Rank.Known -> "Known"
                    Rank.User -> "User"
                    Rank.NewUser -> "New User"
                    Rank.Visitor -> "Visitor"
                }
            }

            fun toColor(rank: Rank): Color {
                return when(rank) {
                    Rank.Trusted -> Color(129, 67, 230)
                    Rank.Known -> Color(255, 123, 66)
                    Rank.User -> Color(43, 207, 92)
                    Rank.NewUser -> Color(23, 120, 255)
                    Rank.Visitor -> Color(204, 204, 204)
                }
            }
        }
    }

    fun getTrustRankFromTags(tags: List<String>): Rank
    {
        var rank: Rank = Rank.Visitor;

        for (tag in tags.reversed()) {

            if (tag.contains("system_trust_veteran") && (rank < Rank.Trusted))
                rank = Rank.Trusted

            if (tag.contains("system_trust_trusted") && (rank < Rank.Known))
                rank = Rank.Known

            if (tag.contains("system_trust_known") && (rank < Rank.User))
                rank = Rank.User

            if (tag.contains("system_trust_basic") && (rank < Rank.NewUser))
                rank = Rank.NewUser
        }

        return rank
    }
}