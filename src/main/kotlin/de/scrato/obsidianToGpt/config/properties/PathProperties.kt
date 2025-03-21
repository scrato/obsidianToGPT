package de.scrato.obsidianToGpt.config.properties

import org.springframework.boot.context.properties.ConfigurationProperties

@ConfigurationProperties("paths")
data class PathProperties(
    val rootPath: String)