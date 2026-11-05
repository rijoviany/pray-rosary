package com.trivium.rosarium.data.model

import kotlinx.serialization.Serializable
import java.security.SecureRandom

@Serializable
data class FamilyMember(
    val id: String = "",
    val name: String = "",
    val role: String = "Family Member",
    val joinedAt: Long = System.currentTimeMillis(),
    val rosariesPrayed: Int = 0
)

@Serializable
data class FamilyGroup(
    val id: String = "",
    val name: String = "",
    val inviteCode: String = "",
    val creatorId: String = "",
    val creatorName: String = "",
    val createdAt: Long = System.currentTimeMillis(),
    val members: List<FamilyMember> = emptyList(),
    val familyIntention: String = "For peace, health, and spiritual protection in our family",
    val totalRosaries: Int = 0
)

@Serializable
data class PendingInvite(
    val inviteCode: String = "",
    val familyId: String = "",
    val familyName: String = "",
    val inviterName: String = "",
    val memberCount: Int = 1
)

object InviteCodeGenerator {
    private const val CHARACTERS = "abcdefghjkmnpqrstuvwxyz23456789" // clear alphanumeric hash (no ambiguous l,1,o,0)
    private val random = SecureRandom()

    /**
     * Generates an 8-character random hash string, e.g. "a8f9c2d1"
     */
    fun generateHash(): String {
        val bytes = ByteArray(4)
        random.nextBytes(bytes)
        return bytes.joinToString("") { "%02x".format(it) }
    }

    /**
     * Formats an invite message suitable for sharing via WhatsApp, SMS, Mail, etc.
     */
    fun buildShareMessage(familyName: String, inviteCode: String, inviterName: String): String {
        return buildString {
            appendLine("🕊️ Join the \"$familyName\" Prayer Circle on Rosarium!")
            appendLine()
            appendLine("$inviterName has invited you to pray the Holy Rosary together as a family.")
            appendLine()
            appendLine("Tap the link below to join our prayer circle:")
            appendLine("https://journs.web.app/invite?code=$inviteCode")
            appendLine()
            appendLine("If you already have Rosarium installed, open the app and use this Invite Code:")
            appendLine("🔑 Code: $inviteCode")
            appendLine()
            append("May Our Lady wrap our family in Her protective mantle.")
        }
    }
}
