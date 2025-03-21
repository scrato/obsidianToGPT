package de.scrato.obsidianToGpt.dto

data class AuthenticationRequest(
    val username: String,
    val password: String,
)