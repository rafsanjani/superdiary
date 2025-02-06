package com.foreverrafs.auth.model

import kotlinx.serialization.Serializable

@Serializable
data class UserInfo(
    val id: String,
    val avatarUrl: String,
    val name: String,
    val email: String,
    /**
     * Every user has a unique email that is generated for them once. This can
     * be used to add an entry by just sending a regular email to this address
     */
    val uniqueEmail: String = "",
)
