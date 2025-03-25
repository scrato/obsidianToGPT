package de.scrato.obsidianToGpt.entities

import java.time.LocalDateTime
import jakarta.persistence.*

@Entity
class FileActionEntity(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long = 0,

    @Column(name = "file_name", nullable = false)
    val fileName: String,

    @Column(name = "action", nullable = false)
    val action: String,

    @Column(name = "content", nullable = false)
    val content: String,

    @Column(name = "timestamp", nullable = false)
    val timestamp: LocalDateTime = LocalDateTime.now()
)