package de.scrato.obsidianToGpt.config.properties

import org.springframework.boot.context.properties.ConfigurationProperties

@ConfigurationProperties("auth0")
data class Auth0Properties(val domain: String)