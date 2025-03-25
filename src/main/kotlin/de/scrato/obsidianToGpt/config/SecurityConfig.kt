package de.scrato.obsidianToGpt.config

import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity
import org.springframework.security.config.annotation.web.builders.HttpSecurity
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity
import org.springframework.security.web.SecurityFilterChain

@Configuration
@EnableWebSecurity
@EnableMethodSecurity
class SecurityConfig {
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
