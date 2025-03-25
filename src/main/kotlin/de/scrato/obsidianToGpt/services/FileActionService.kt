package de.scrato.obsidianToGpt.services

import de.scrato.obsidianToGpt.entities.FileActionEntity
import de.scrato.obsidianToGpt.repositories.FileActionRepository
import org.springframework.stereotype.Service
import java.time.LocalDateTime

@Service
class FileActionService(private val fileActionRepository: FileActionRepository) {
    fun createFileAction(fileName: String, action: String, content: String): FileActionEntity {
        val fileAction = FileActionEntity(
            fileName = fileName,
            action = action,
            content = content,
            timestamp = LocalDateTime.now()
        )
        return fileActionRepository.save(fileAction)
    }
}