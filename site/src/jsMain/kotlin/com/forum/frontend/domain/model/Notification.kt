package com.forum.frontend.domain.model

enum class NotificationType(val icon: String) {
    REACTION("favorite"),
    COMMENT("chat_bubble"),
    REPLY("reply"),
    MENTION("alternate_email"),
    SHARE("repeat"),
    BADGE("workspace_premium"),
    OTHER("notifications");

    companion object {
        fun fromApi(value: String): NotificationType {
            val name = value.lowercase()
            return when {
                "reply" in name || "respuesta" in name -> REPLY
                "comment" in name || "comentario" in name -> COMMENT
                "reaction" in name || "like" in name || "reaccion" in name -> REACTION
                "mention" in name || "mencion" in name -> MENTION
                "share" in name || "compart" in name -> SHARE
                "badge" in name || "insignia" in name -> BADGE
                else -> OTHER
            }
        }
    }
}

data class Notification(
    val id: Int,
    val type: NotificationType,
    val isRead: Boolean,
    val createdAt: Double,
    val actorId: Int?,
    val actorFirstName: String,
    val actorLastName: String,
    val actorPictureUrl: String?,
    val postId: Int?,
    val commentId: Int?,
    val badgeName: String?,
    val badgeIconUrl: String?,
    val postPreview: String?,
) {
    val actorName: String get() = "$actorFirstName $actorLastName".trim()
}
