package de.scrato.obsidianToGpt.config.properties

import org.springframework.boot.context.properties.ConfigurationProperties


@ConfigurationProperties("app")
data class ApplicationProperties (
    val useNgrok: Boolean
)