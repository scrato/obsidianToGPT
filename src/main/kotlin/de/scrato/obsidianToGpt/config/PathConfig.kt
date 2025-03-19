package de.scrato.obsidianToGpt.config

import org.springframework.boot.context.properties.ConfigurationProperties
import org.springframework.context.annotation.Configuration

@Configuration
@ConfigurationProperties(prefix = "paths")
class PathConfig {
    lateinit var rootPath: String
}