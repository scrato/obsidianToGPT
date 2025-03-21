package de.scrato.obsidianToGpt.repositories

import de.scrato.obsidianToGpt.entities.UserEntity
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository
import java.util.*

@Repository
interface UserRepository : JpaRepository<UserEntity, Long> {
    fun findByUsername(username: String): Optional<UserEntity>

    fun save(user: UserEntity): Boolean

    fun findById(id: UUID): Optional<UserEntity>

    fun deleteById(uuid: UUID): Boolean
}