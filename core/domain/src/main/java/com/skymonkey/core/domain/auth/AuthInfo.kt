package com.skymonkey.core.domain.auth

data class AuthInfo(
    val accessToken: String,
    val refreshToken: String,
    val userId: String,
)
