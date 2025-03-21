package de.scrato.obsidianToGpt.dto

data class AuthenticationResponse(
    val accessToken: String,
    val refreshToken: String,
)