package de.scrato.obsidianToGpt.controller

import org.springframework.hateoas.EntityModel
import org.springframework.hateoas.Link
import org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.*
import org.springframework.http.ResponseEntity
import org.springframework.security.access.prepost.PreAuthorize
import org.springframework.web.bind.annotation.*
import java.nio.file.Files
import java.nio.file.Path
import java.nio.file.Paths
import java.nio.file.StandardOpenOption

@RestController
@RequestMapping("/files")
class FileController {

    private val baseDirectory = Paths.get("C:\\Users\\willm\\privat\\rpgnotes")

    // ✅ LIST FILES
    @GetMapping("/list")
    fun listFiles(): ResponseEntity<List<EntityModel<String>>> {
        val files = Files.list(baseDirectory)
            .filter { Files.isRegularFile(it) }
            .map { file ->
                val filename = file.fileName.toString()
                EntityModel.of(
                    filename,
                    linkTo(methodOn(FileController::class.java).openFile(filename)).withRel("open")
                )
            }
            .toList()

        return ResponseEntity.ok(files)
    }

    // ✅ OPEN FILE
    @GetMapping("/open")
    fun openFile(@RequestParam("filename") filename: String): ResponseEntity<EntityModel<String>> {
        val filePath = baseDirectory.resolve(filename)
        if (!Files.exists(filePath) || !Files.isRegularFile(filePath)) {
            return ResponseEntity.notFound().build()
        }

        val content = Files.readString(filePath)

        val entity = EntityModel.of(
            content,
            linkTo(methodOn(FileController::class.java).openFile(filename)).withSelfRel(),
            linkTo(methodOn(FileController::class.java).listFiles()).withRel("list"),
            linkTo(methodOn(FileController::class.java).updateFile(filename, "")).withRel("update"),
            linkTo(methodOn(FileController::class.java).deleteFile(filename)).withRel("delete"),
            linkTo(methodOn(FileController::class.java).moveFile(filename, "newname.txt")).withRel("move")
        )

        return ResponseEntity.ok(entity)
    }

    // ✅ UPDATE FILE (nur authentifiziert)
    @PreAuthorize("isAuthenticated()")
    @PutMapping("/update")
    fun updateFile(
        @RequestParam("filename") filename: String,
        @RequestBody newContent: String
    ): ResponseEntity<EntityModel<String>> {
        val filePath = baseDirectory.resolve(filename)
        if (!Files.exists(filePath) || !Files.isRegularFile(filePath)) {
            return ResponseEntity.notFound().build()
        }

        Files.writeString(filePath, newContent, StandardOpenOption.TRUNCATE_EXISTING)

        val entity = EntityModel.of(
            "Datei erfolgreich aktualisiert.",
            linkTo(methodOn(FileController::class.java).openFile(filename)).withRel("open"),
            linkTo(methodOn(FileController::class.java).listFiles()).withRel("list")
        )

        return ResponseEntity.ok(entity)
    }

    // ✅ DELETE FILE (nur authentifiziert)
    @PreAuthorize("isAuthenticated()")
    @DeleteMapping("/delete")
    fun deleteFile(@RequestParam("filename") filename: String): ResponseEntity<EntityModel<String>> {
        val filePath = baseDirectory.resolve(filename)
        if (!Files.exists(filePath) || !Files.isRegularFile(filePath)) {
            return ResponseEntity.notFound().build()
        }

        Files.delete(filePath)

        val entity = EntityModel.of(
            "Datei erfolgreich gelöscht.",
            linkTo(methodOn(FileController::class.java).listFiles()).withRel("list")
        )

        return ResponseEntity.ok(entity)
    }

    // ✅ MOVE FILE (nur authentifiziert)
    @PreAuthorize("isAuthenticated()")
    @PostMapping("/move")
    fun moveFile(
        @RequestParam("oldFilename") oldFilename: String,
        @RequestParam("newFilename") newFilename: String
    ): ResponseEntity<EntityModel<String>> {
        val oldFilePath = baseDirectory.resolve(oldFilename)
        val newFilePath = baseDirectory.resolve(newFilename)
        if (!Files.exists(oldFilePath) || !Files.isRegularFile(oldFilePath)) {
            return ResponseEntity.notFound().build()
        }

        Files.move(oldFilePath, newFilePath)

        val entity = EntityModel.of(
            "Datei erfolgreich umbenannt.",
            linkTo(methodOn(FileController::class.java).openFile(newFilename)).withRel("open"),
            linkTo(methodOn(FileController::class.java).listFiles()).withRel("list")
        )

        return ResponseEntity.ok(entity)
    }
}
