package de.scrato.obsidianToGpt.config

import de.scrato.obsidianToGpt.config.properties.*
import org.springframework.boot.context.properties.EnableConfigurationProperties
import org.springframework.context.annotation.Configuration


@Configuration
@EnableConfigurationProperties(ApplicationProperties::class, PathProperties::class, Auth0Properties::class)
class Configuration