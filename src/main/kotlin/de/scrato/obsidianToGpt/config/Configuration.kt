package de.scrato.obsidianToGpt.config

import de.scrato.obsidianToGpt.config.properties.ApplicationProperties
import de.scrato.obsidianToGpt.config.properties.PathProperties
import org.springframework.boot.context.properties.EnableConfigurationProperties
import org.springframework.context.annotation.Configuration


@Configuration
@EnableConfigurationProperties(ApplicationProperties::class, PathProperties::class)
class Configuration {
}