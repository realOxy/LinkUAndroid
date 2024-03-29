package com.linku.domain.entity

import androidx.compose.runtime.Stable
import androidx.room.Entity
import androidx.room.PrimaryKey
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Entity
@Stable
data class User(
    @PrimaryKey
    val id: Int,
    val name: String,
    val email: String,
    val verified: Boolean,
    val realName: String?,
    val avatar: String?
)

@Serializable
@Stable
data class UserDTO(
    @SerialName("id")
    val id: Int,
    @SerialName("nickname")
    val name: String = "",
    @SerialName("email")
    val email: String = "",
    @SerialName("password")
    val password: String = "",
    @SerialName("salt")
    val salt: String = "",
    @SerialName("role")
    val role: String = "",
    @SerialName("is_verified")
    val verified: Boolean = false,
    @SerialName("real_name")
    val realName: String? = null,
    @SerialName("avatar")
    val avatar: String? = null
) {
    fun toUser() = User(
        id = id,
        name = name,
        email = email,
        verified = verified,
        realName = realName,
        avatar = avatar
    )
}