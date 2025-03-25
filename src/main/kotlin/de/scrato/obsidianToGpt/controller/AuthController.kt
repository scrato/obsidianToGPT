package de.scrato.obsidianToGpt.controller

import org.springframework.beans.factory.annotation.Value
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.http.HttpStatus
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.util.UriComponentsBuilder
import java.net.URI

@RequestMapping("/auth")
class AuthController {
    @Value("\${auth0.domain}")
    lateinit var auth0Domain: String

    @Value("\${auth0.clientId}")
    lateinit var clientId: String

    @GetMapping("/authorize")
    fun authorize(@RequestParam("callback") callback: String): ResponseEntity<Void> {
        val url = UriComponentsBuilder.fromUriString("https://$auth0Domain/authorize")
            .queryParam("response_type", "code")
            .queryParam("client_id", clientId)
            .queryParam("redirect_uri", callback)
            .build()
            .toUriString()

        return ResponseEntity.status(HttpStatus.FOUND)
            .location(URI(url))
            .build()
    }
}