package de.scrato.obsidianToGpt.repositories

import de.scrato.obsidianToGpt.entities.FileActionEntity
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

@Repository
interface FileActionRepository : JpaRepository<FileActionEntity, Long> {
}