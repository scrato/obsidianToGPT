package de.scrato.obsidianToGpt.services

import de.scrato.obsidianToGpt.entities.*
import de.scrato.obsidianToGpt.repositories.UserRepository
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.security.core.authority.*
import org.springframework.security.core.context.*
import org.springframework.security.core.userdetails.*
import org.springframework.stereotype.Service
import java.util.*

@Service
class UserService(@Autowired val userRepository: UserRepository) : UserDetailsService {

    override fun loadUserByUsername(username: String): UserDetails {
        val user = userRepository.findByUsername(username).orElseThrow {
            throw UsernameNotFoundException(username)
        }
        return User(user.username, user.password, listOf(SimpleGrantedAuthority(user.role.name)))
    }

    fun loadCurrentUserAsEntity(): Optional<UserEntity> {
        val authentication = SecurityContextHolder.getContext().authentication
        return userRepository.findByUsername(authentication.name)
    }

    fun getRoleOfCurrentUser(): Optional<UserRole> {
        val authentication = SecurityContextHolder.getContext().authentication
        val role = authentication.authorities.firstOrNull()?.let { UserRole.valueOf(it.authority) }
        return Optional.ofNullable(role)
    }

    fun createUser(user: UserEntity): UserEntity? {
        val found = userRepository.findByUsername(user.username)
        return if (found.isEmpty) {
            userRepository.save(user)
            user
        } else null
    }

    fun findById(uuid: UUID): UserEntity? =
        userRepository.findById(uuid).orElse(null)

    fun findAll(): List<UserEntity> =
        userRepository.findAll()
            .toList()

    fun deleteById(uuid: UUID): Boolean =
        userRepository.deleteById(uuid)
}