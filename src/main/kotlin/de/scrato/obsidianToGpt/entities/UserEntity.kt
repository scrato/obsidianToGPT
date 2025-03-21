package de.scrato.obsidianToGpt.entities

import jakarta.persistence.*
import java.util.*

@Entity
@Table(name = "users")
class UserEntity(
    @Id @GeneratedValue(strategy = GenerationType.UUID)
    val id: UUID,
    val username: String,
    val password: String,
    @Enumerated(EnumType.STRING) val role: UserRole) {
}

enum class UserRole {
    USER, ADMIN
}