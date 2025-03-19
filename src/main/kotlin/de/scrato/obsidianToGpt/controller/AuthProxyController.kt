import org.springframework.web.bind.annotation.*
import org.springframework.web.client.RestTemplate
import org.springframework.http.*

@RestController
@RequestMapping("/oauth")
class AuthProxyController {

    private val auth0BaseUrl = "https://tavernlogin.eu.auth0.com"

    @GetMapping("/authorize")
    fun authorize(@RequestParam params: Map<String, String>): ResponseEntity<String> {
        val uri = "$auth0BaseUrl/authorize?" + params.entries.joinToString("&") { "${it.key}=${it.value}" }
        return ResponseEntity.status(HttpStatus.FOUND).header(HttpHeaders.LOCATION, uri).build()
    }

    @PostMapping("/token")
    fun token(@RequestBody body: String): ResponseEntity<String> {
        val restTemplate = RestTemplate()
        val headers = HttpHeaders()
        headers.contentType = MediaType.APPLICATION_FORM_URLENCODED
        val request = HttpEntity(body, headers)
        return restTemplate.exchange("$auth0BaseUrl/oauth/token", HttpMethod.POST, request, String::class.java)
    }
}
