import jakarta.servlet.http.HttpServletRequest
import org.springframework.http.*
import org.springframework.web.bind.annotation.*
import org.springframework.web.client.RestTemplate
import java.net.URLEncoder
import java.nio.charset.StandardCharsets

@RestController
@RequestMapping("/oauth")
class AuthProxyController {

    private val auth0BaseUrl = "https://tavernlogin.eu.auth0.com"

    /**
     * GET /oauth/authorize -> 302 Redirect zu https://tavernlogin.eu.auth0.com/authorize
     */
    @GetMapping("/authorize")
    fun authorize(@RequestParam params: Map<String, String>): ResponseEntity<Void> {
        // Query-Parameter anfügen
        val queryString = params.entries.joinToString("&") {
            "${it.key}=${URLEncoder.encode(it.value, StandardCharsets.UTF_8)}"
        }
        val redirectUrl = "$auth0BaseUrl/authorize?$queryString"

        // 302-Redirect
        return ResponseEntity
            .status(HttpStatus.FOUND)
            .header(HttpHeaders.LOCATION, redirectUrl)
            .build()
    }

    /**
     * POST /oauth/device/code -> Proxy zu https://tavernlogin.eu.auth0.com/oauth/device/code
     */
    @PostMapping("/device/code")
    fun deviceCode(
        request: HttpServletRequest,
        @RequestBody body: String
    ): ResponseEntity<String> {
        val restTemplate = RestTemplate()

        // Header zusammenbauen (inkl. Content-Type)
        val headers = HttpHeaders()
        headers.contentType = MediaType.APPLICATION_FORM_URLENCODED

        // (Optional) Andere Header durchreichen, falls gewünscht:
        // request.headerNames.asIterator().forEachRemaining { name ->
        //     headers[name] = request.getHeader(name)
        // }

        val entity = HttpEntity(body, headers)
        return restTemplate.exchange(
            "$auth0BaseUrl/oauth/device/code",
            HttpMethod.POST,
            entity,
            String::class.java
        )
    }

    /**
     * POST /oauth/token -> Proxy zu https://tavernlogin.eu.auth0.com/oauth/token
     */
    @PostMapping("/token")
    fun token(
        request: HttpServletRequest,
        @RequestBody body: String
    ): ResponseEntity<String> {
        val restTemplate = RestTemplate()

        val headers = HttpHeaders()
        headers.contentType = MediaType.APPLICATION_FORM_URLENCODED

        val entity = HttpEntity(body, headers)
        return restTemplate.exchange(
            "$auth0BaseUrl/oauth/token",
            HttpMethod.POST,
            entity,
            String::class.java
        )
    }

    /**
     * GET /oauth/userinfo -> Proxy zu https://tavernlogin.eu.auth0.com/userinfo
     */
    @GetMapping("/userinfo")
    fun userInfo(request: HttpServletRequest): ResponseEntity<String> {
        val restTemplate = RestTemplate()

        // Beispiel: Authorization-Header durchreichen
        val headers = HttpHeaders()
        val authHeader = request.getHeader("Authorization")
        if (!authHeader.isNullOrBlank()) {
            headers["Authorization"] = authHeader
        }

        val entity = HttpEntity(null, headers)
        return restTemplate.exchange(
            "$auth0BaseUrl/userinfo",
            HttpMethod.GET,
            entity,
            String::class.java
        )
    }

    /**
     * GET /oauth/.well-known/openid-configuration -> Proxy zu https://tavernlogin.eu.auth0.com/.well-known/openid-configuration
     */
    @GetMapping("/.well-known/openid-configuration")
    fun openidConfiguration(): ResponseEntity<String> {
        val restTemplate = RestTemplate()
        return restTemplate.getForEntity(
            "$auth0BaseUrl/.well-known/openid-configuration",
            String::class.java
        )
    }

    /**
     * GET /oauth/.well-known/jwks.json -> Proxy zu https://tavernlogin.eu.auth0.com/.well-known/jwks.json
     */
    @GetMapping("/.well-known/jwks.json")
    fun jwks(): ResponseEntity<String> {
        val restTemplate = RestTemplate()
        return restTemplate.getForEntity(
            "$auth0BaseUrl/.well-known/jwks.json",
            String::class.java
        )
    }
}
