import org.springframework.security.core.annotation.AuthenticationPrincipal
import org.springframework.security.oauth2.jwt.Jwt
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/user")
class UserController {

    @GetMapping("/me")
    fun getUserInfo(@AuthenticationPrincipal jwt: Jwt): Map<String, Any> {
        return mapOf(
            "sub" to jwt.subject,  // User ID von Auth0
            "email" to jwt.getClaim<String>("email"),
            "name" to jwt.getClaim<String>("name")
        )
    }
}
