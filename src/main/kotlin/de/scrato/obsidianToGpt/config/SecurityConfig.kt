package de.scrato.obsidianToGpt.config

import de.scrato.obsidianToGpt.services.UserService
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.security.authentication.AuthenticationManager
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity
import org.springframework.security.config.annotation.web.builders.HttpSecurity
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.security.oauth2.jwt.JwtDecoder
import org.springframework.security.oauth2.jwt.NimbusJwtDecoder
import org.springframework.security.web.SecurityFilterChain

@Configuration
@EnableWebSecurity
@EnableMethodSecurity
class SecurityConfig(@Autowired private val userService: UserService) {

    @Bean
    fun passwordEncoder(): PasswordEncoder = BCryptPasswordEncoder()

    @Bean
    fun securityFilterChain(http: HttpSecurity): SecurityFilterChain {
        http
            .authorizeHttpRequests { auth ->
                auth
                    .requestMatchers("/files/list", "/files/open").permitAll()
                    .requestMatchers("/auth/authorize", "/auth/token").permitAll()
                    .requestMatchers("/error").permitAll()
                    .requestMatchers("/files/update", "/files/create", "/files/delete", "/files/move").hasAuthority("SCOPE_edit:files")
                    .anyRequest().authenticated()
            }
            .csrf { it.disable() }
            .oauth2ResourceServer { it.jwt {} }
        return http.build()
    }
}
