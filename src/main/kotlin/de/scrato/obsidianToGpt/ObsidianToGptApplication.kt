package de.scrato.obsidianToGpt

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication

@SpringBootApplication(scanBasePackages= ["de.scrato.obsidianToGpt"])
class ObsidianToGptApplication

fun main(args: Array<String>) {
    runApplication<ObsidianToGptApplication>(*args)
}
