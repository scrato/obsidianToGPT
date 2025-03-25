package de.scrato.obsidianToGpt.controller

import de.scrato.obsidianToGpt.config.properties.PathProperties
import de.scrato.obsidianToGpt.dto.FileInfo
import de.scrato.obsidianToGpt.dto.FileListInfo
import de.scrato.obsidianToGpt.services.FileActionService
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.hateoas.CollectionModel
import org.springframework.hateoas.EntityModel
import org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo
import org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn
import org.springframework.http.ResponseEntity
import org.springframework.security.access.prepost.PreAuthorize
import org.springframework.web.bind.annotation.*
import java.io.File
import java.nio.file.Files
import java.nio.file.Paths
import java.nio.file.StandardOpenOption

@RestController
@RequestMapping("/files")
class FileController @Autowired constructor(pathProperties: PathProperties,
                                            private val fileActionService: FileActionService) {


    private val baseDirectory = Paths.get(pathProperties.rootPath)

    @GetMapping("/list")
    fun listFiles(): ResponseEntity<CollectionModel<EntityModel<FileListInfo>>> {
        if (!Files.exists(baseDirectory) || !Files.isDirectory(baseDirectory)) {
            return ResponseEntity.notFound().build()
        }
        val fileInfos = getFilesRecursive(baseDirectory.toFile())
        val result: List<EntityModel<FileListInfo>> = fileInfos.map { fileInfo ->
            val entityModel = EntityModel.of(fileInfo)
            if(fileInfo.mimeType.startsWith("text") || fileInfo.mimeType == "application/markdown" || fileInfo.mimeType == "application/json")
            {
                entityModel.add(linkTo(methodOn(FileController::class.java).openFile(fileInfo.getFullPath())).withRel("open"))
            }
            entityModel.add(linkTo(methodOn(FileController::class.java).createFile("{filename}", "{content}")).withRel("create"))
            entityModel
        }
        val collectionModel = CollectionModel.of(result)
        return ResponseEntity.ok(collectionModel)
    }

    private fun getFilesRecursive(baseDir: File): List<FileListInfo> {
        val result = mutableListOf<FileListInfo>()

        fun traverse(file: File, relativePath: String) {
            if (file.name.startsWith(".")) return

            if (file.isFile) {
                val mimeType = Files.probeContentType(file.toPath()) ?: "application/octet-stream"
                result.add(FileListInfo(file.name, relativePath, mimeType))
            } else if (file.isDirectory) {
                val newRelativePath = if (relativePath.isEmpty()) file.name else "$relativePath\\${file.name}"
                file.listFiles()?.forEach { child ->
                    traverse(child, newRelativePath)
                }
            }
        }
        baseDir.listFiles()?.forEach { file -> traverse(file, "") }
        return result
    }

    @PreAuthorize("isAuthenticated()")
    @PostMapping("/create")
    fun createFile(
        @RequestParam("filename") filename: String,
        @RequestBody content: String
    ): ResponseEntity<EntityModel<FileInfo>> {
        val filePath = baseDirectory.resolve(filename)
        if (Files.exists(filePath)) {
            return ResponseEntity.status(409).body(null)
        }
        Files.writeString(filePath, content, StandardOpenOption.CREATE_NEW)
        fileActionService.createFileAction(filename, "create", content)
        val entityModel = createFileInfoForFile(filename, content)
        return ResponseEntity.ok(entityModel)
    }

    @GetMapping("/open")
    fun openFile(@RequestParam("filename") filename: String): ResponseEntity<EntityModel<FileInfo>> {
        val filePath = baseDirectory.resolve(filename)
        if (Files.exists(filePath) && Files.isRegularFile(filePath)) {
            val fileContent = Files.readString(filePath)
            val entityModel = createFileInfoForFile(filename, fileContent)

            fileActionService.createFileAction(filename, "open", fileContent)
            return ResponseEntity.ok(entityModel)
        }
        return ResponseEntity.notFound().build()
    }

    @PreAuthorize("isAuthenticated()")
    @PutMapping("/update")
    fun updateFile(
        @RequestParam("filename") filename: String,
        @RequestBody newContent: String
    ): ResponseEntity<EntityModel<FileInfo>> {
        val filePath = baseDirectory.resolve(filename)
        if (Files.exists(filePath) && Files.isRegularFile(filePath)) {
            Files.writeString(filePath, newContent, StandardOpenOption.TRUNCATE_EXISTING)
            val entityModel = createFileInfoForFile(filename, newContent)

            fileActionService.createFileAction(filename, "update", newContent)
            return ResponseEntity.ok(entityModel)
        }
        return ResponseEntity.notFound().build()
    }

    fun createFileInfoForFile(name: String, content: String): EntityModel<FileInfo>
    {
        val file = FileInfo(name, content)
        val entityModel = EntityModel.of(file)
        entityModel.add(linkTo(methodOn(FileController::class.java).updateFile(name, "{content}")).withRel("update"))
        entityModel.add(linkTo(methodOn(FileController::class.java).deleteFile(name)).withRel("delete"))
        entityModel.add(linkTo(methodOn(FileController::class.java).moveFile(name, "{newName}")).withRel("move"))
        entityModel.add(linkTo(methodOn(FileController::class.java).listFiles()).withRel("list"))
        return entityModel
    }

    @PreAuthorize("isAuthenticated()")
    @DeleteMapping("/delete")
    fun deleteFile(@RequestParam("filename") filename: String): ResponseEntity<EntityModel<FileInfo>> {
        val filePath = baseDirectory.resolve(filename)
        if (Files.exists(filePath) && Files.isRegularFile(filePath)) {
            Files.delete(filePath)
            val entityModel = createFileInfoForFile(filename, "")
            entityModel.add(linkTo(methodOn(FileController::class.java).listFiles()).withRel("list"))
            fileActionService.createFileAction(filename, "delete", "")
            return ResponseEntity.ok(entityModel)
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
            fileActionService.createFileAction(newFilename, "move", "$oldFilename -> $newFilename")
            return ResponseEntity.ok("Renamed file successfully.")
        }
        return ResponseEntity.notFound().build()
    }
}
