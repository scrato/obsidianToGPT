package de.scrato.obsidianToGpt.controller

import de.scrato.obsidianToGpt.config.PathConfig
import de.scrato.obsidianToGpt.dto.FileListInfo
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.web.bind.annotation.*
import org.springframework.http.ResponseEntity
import org.springframework.security.access.prepost.PreAuthorize
import java.io.File
import java.nio.file.Files
import org.springframework.hateoas.EntityModel
import org.springframework.hateoas.CollectionModel
import org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo
import org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController
import java.nio.file.Paths
import java.nio.file.StandardOpenOption

@RestController
@RequestMapping("/files")
class FileController @Autowired constructor(private val pathConfig: PathConfig)
{


    private val baseDirectory = Paths.get(pathConfig.rootPath)

    @GetMapping("/list")
    fun listFiles(): ResponseEntity<CollectionModel<EntityModel<FileListInfo>>> {
        val files: List<EntityModel<FileListInfo>> = getFilesRecursive(baseDirectory.toFile()).map { fileInfo ->
            val entityModel = EntityModel.of(fileInfo)
            val openLink = linkTo(methodOn(FileController::class.java).openFile(fileInfo.getFullPath())).withRel("open")
            entityModel.add(openLink)
            entityModel
        }
        val collectionModel = CollectionModel.of(files)
        return ResponseEntity.ok(collectionModel)
    }

    private fun getFilesRecursive(baseDir: File): List<FileListInfo> {
        val result = mutableListOf<FileListInfo>()

        fun traverse(file: File, relativePath: String) {
            if (file.name.startsWith(".")) return

            if (file.isFile) {
                result.add(FileListInfo(file.name, relativePath))
            } else if (file.isDirectory) {
                val newRelativePath = if (relativePath.isEmpty()) file.name else "$relativePath\\${file.name}"
                file.listFiles()?.forEach { child ->
                    traverse(child, newRelativePath)
                }
            }
        }
        baseDir.listFiles()?.forEach{ file -> traverse(file, "")}
        return result
    }

    @GetMapping("/open")
    fun openFile(@RequestParam("filename") filename: String): ResponseEntity<String> {
        val filePath = baseDirectory.resolve(filename)
        if (Files.exists(filePath) && Files.isRegularFile(filePath)) {
            val content = Files.readString(filePath)
            return ResponseEntity.ok(content)
        }
        return ResponseEntity.notFound().build()
    }

    @PreAuthorize("isAuthenticated()")
    @PutMapping("/update")
    fun updateFile(
        @RequestParam("filename") filename: String,
        @RequestBody newContent: String
    ): ResponseEntity<String> {
        val filePath = baseDirectory.resolve(filename)
        if (Files.exists(filePath) && Files.isRegularFile(filePath)) {
            Files.writeString(filePath, newContent, StandardOpenOption.TRUNCATE_EXISTING)
            return ResponseEntity.ok("Updated file successfully.")
        }
        return ResponseEntity.notFound().build()
    }

    @PreAuthorize("isAuthenticated()")
    @DeleteMapping("/delete")
    fun deleteFile(@RequestParam("filename") filename: String): ResponseEntity<String> {
        val filePath = baseDirectory.resolve(filename)
        if (Files.exists(filePath) && Files.isRegularFile(filePath)) {
            Files.delete(filePath)
            return ResponseEntity.ok("Deleted file successfully.")
        }
        return ResponseEntity.notFound().build()
    }

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
            return ResponseEntity.ok("Renamed file successfully.")
        }
        return ResponseEntity.notFound().build()
    }
}
