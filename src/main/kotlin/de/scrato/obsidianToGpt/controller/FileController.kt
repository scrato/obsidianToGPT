package de.scrato.obsidianToGpt.controller

import org.springframework.web.bind.annotation.*
import org.springframework.http.ResponseEntity
import org.springframework.security.access.prepost.PreAuthorize
import java.nio.file.Files
import java.nio.file.Paths
import java.nio.file.StandardOpenOption

@RestController
@RequestMapping("/files")
class FileController {

    // Basisverzeichnis für Dateioperationen
    private val baseDirectory = Paths.get("C:\\Users\\willm\\privat\\rpgnotes")

    // list: Listet alle Dateien auf
    @GetMapping("/list")
    fun listFiles(): ResponseEntity<List<String>> {
        val files = Files.list(baseDirectory)
            .filter { Files.isRegularFile(it) }
            .map { it.fileName.toString() }
            .toList()
        return ResponseEntity.ok(files)
    }

    // open: Gibt den Inhalt einer Datei zurück
    @GetMapping("/open")
    fun openFile(@RequestParam("filename") filename: String): ResponseEntity<String> {
        val filePath = baseDirectory.resolve(filename)
        if (Files.exists(filePath) && Files.isRegularFile(filePath)) {
            val content = Files.readString(filePath)
            return ResponseEntity.ok(content)
        }
        return ResponseEntity.notFound().build()
    }

    // update: Aktualisiert den Inhalt einer Datei (nur für authentifizierte Benutzer)
    @PreAuthorize("isAuthenticated()")
    @PutMapping("/update")
    fun updateFile(
        @RequestParam("filename") filename: String,
        @RequestBody newContent: String
    ): ResponseEntity<String> {
        val filePath = baseDirectory.resolve(filename)
        if (Files.exists(filePath) && Files.isRegularFile(filePath)) {
            Files.writeString(filePath, newContent, StandardOpenOption.TRUNCATE_EXISTING)
            return ResponseEntity.ok("Datei erfolgreich aktualisiert.")
        }
        return ResponseEntity.notFound().build()
    }

    // delete: Löscht eine Datei (nur für authentifizierte Benutzer)
    @PreAuthorize("isAuthenticated()")
    @DeleteMapping("/delete")
    fun deleteFile(@RequestParam("filename") filename: String): ResponseEntity<String> {
        val filePath = baseDirectory.resolve(filename)
        if (Files.exists(filePath) && Files.isRegularFile(filePath)) {
            Files.delete(filePath)
            return ResponseEntity.ok("Datei erfolgreich gelöscht.")
        }
        return ResponseEntity.notFound().build()
    }

    // move: Ändert den Dateinamen (nur für authentifizierte Benutzer)
    @PreAuthorize("isAuthenticated()")
    @PostMapping("/move")
    fun moveFile(
        @RequestParam("oldFilename") oldFilename: String,
        @RequestParam("newFilename") newFilename: String
    ): ResponseEntity<String> {
        val oldFilePath = baseDirectory.resolve(oldFilename)
        val newFilePath = baseDirectory.resolve(newFilename)
        if (Files.exists(oldFilePath) && Files.isRegularFile(oldFilePath)) {
            Files.move(oldFilePath, newFilePath)
            return ResponseEntity.ok("Datei erfolgreich umbenannt.")
        }
        return ResponseEntity.notFound().build()
    }
}
