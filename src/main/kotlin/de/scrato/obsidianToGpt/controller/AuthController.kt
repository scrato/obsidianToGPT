package de.scrato.obsidianToGpt.controller

import de.scrato.obsidianToGpt.config.properties.Auth0Properties
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*
import org.springframework.web.util.UriComponentsBuilder
import java.net.URI

@RestController
@RequestMapping("/auth")
class AuthController(@Autowired val auth0Properties: Auth0Properties) {

    @GetMapping("/authorize")
    fun authorize(@RequestParam("client_id") clientId: String,
                  @RequestParam("redirect_uri") callback: String,
                  @RequestParam("response_type") responseType: String,
                  @RequestParam("scope", required = false) scope: String?,
                  @RequestParam("state", required = false) state: String?): ResponseEntity<Void> {
        val uriBuilder = UriComponentsBuilder.fromUriString("https://${auth0Properties.domain}/authorize")
            .queryParam("response_type", responseType)
            .queryParam("client_id", clientId)
            .queryParam("redirect_uri", callback)

        if (scope != null) {
            uriBuilder.queryParam("scope", scope)
        }

        if (state != null) {
            uriBuilder.queryParam("state", state)
        }

        val url = uriBuilder
            .build()
            .toUriString()

        return ResponseEntity.status(HttpStatus.PERMANENT_REDIRECT)
            .location(URI(url))
            .build()
    }

    @PostMapping("/token")
    fun token(@RequestParam("client_id") clientId: String,
                  @RequestParam("client_secret") clientSecret: String,
                  @RequestParam("code") code: String,
                  @RequestParam("grant_type") grantType: String?,
                  @RequestParam("redirect_uri") redirectUri: String?): ResponseEntity<Void> {
        val uriBuilder = UriComponentsBuilder.fromUriString("https://${auth0Properties.domain}/oauth/token")
            .queryParam("client_id", clientId)
            .queryParam("client_secret", clientSecret)
            .queryParam("code", code)
            .queryParam("grant_type", grantType)
            .queryParam("redirect_uri", redirectUri)


        val url = uriBuilder
            .build()
            .toUriString()

        return ResponseEntity.status(HttpStatus.PERMANENT_REDIRECT)
            .location(URI(url))
            .build()
    }
}