package de.scrato.obsidianToGpt.controller

import de.scrato.obsidianToGpt.config.properties.PathProperties
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.io.TempDir
import org.springframework.http.MediaType
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.get
import org.springframework.test.web.servlet.setup.MockMvcBuilders
import java.nio.file.Files
import java.nio.file.Path

class FileControllerListTest {

    /**
     * Testfall: Basisverzeichnis enthält Dateien und Unterordner.
     * Es werden zwei Dateien erwartet, da versteckte Dateien/Ordner ignoriert werden.
     * Zusätzlich wird geprüft, ob HATEOAS-Links vorhanden sind.
     */
    @Test
    fun `listFiles returns file list with HATEOAS links`(@TempDir tempDir: Path) {
        // Setup: Erstelle Dateien und Unterordner im temporären Basisverzeichnis
        // Datei direkt im Basisverzeichnis
        Files.createFile(tempDir.resolve("test1.txt"))
        // Unterordner mit einer Datei
        val subDir = Files.createDirectory(tempDir.resolve("subDir"))
        Files.createFile(subDir.resolve("test2.txt"))
        // Versteckte Datei und versteckter Ordner – sollten ignoriert werden
        Files.createFile(tempDir.resolve(".hidden.txt"))
        val hiddenDir = Files.createDirectory(tempDir.resolve(".hiddenDir"))
        Files.createFile(hiddenDir.resolve("hiddenInside.txt"))

        // Erstelle die für den Controller notwendige PathConfig
        val pathProperties = PathProperties().apply { rootPath = tempDir.toString() }
        val controller = FileController(pathProperties)
        val mockMvc: MockMvc = MockMvcBuilders.standaloneSetup(controller).build()

        // GET /files/list aufrufen und prüfen:
        // - HTTP-Status OK (200)
        // - JSON-Antwort enthält _embedded, in dem genau 2 FileListInfo-Objekte enthalten sind
        // - Jedes Element enthält den HATEOAS-Link "open"
        mockMvc.get("/files/list")
            .andExpect {
                status { isOk() }
                content { contentType(MediaType.APPLICATION_JSON) }
                // Überprüfe, dass genau 2 Dateien gelistet werden (test1.txt und test2.txt)
                jsonPath("$.content.length()") { value(2) }
                // Überprüfe, dass "test1.txt" in der Antwort enthalten ist
                jsonPath("$.content[?(@.fileName=='test1.txt')]") { exists() }
                // Überprüfe, dass "test2.txt" in der Antwort enthalten ist
                jsonPath("$.content[?(@.fileName=='test2.txt')]") { exists() }
                // Prüfe, ob das HATEOAS-Link-Attribut "open" vorhanden ist
                jsonPath("$.content[0].links[?(@.rel=='open')].href") { exists() }
            }
    }

    /**
     * Testfall: Basisverzeichnis existiert nicht.
     * Erwartetes Ergebnis: HTTP 404 Not Found.
     */
    @Test
    fun `listFiles returns 404 when base directory does not exist`() {
        // Erzeuge ein temporäres Verzeichnis und wähle einen Unterordner, der nicht existiert
        val tempDir = Files.createTempDirectory("testDir")
        val nonExistentPath = tempDir.resolve("nonexistent")
        val pathProperties = PathProperties().apply { rootPath = nonExistentPath.toString() }
        val controller = FileController(pathProperties)
        val mockMvc: MockMvc = MockMvcBuilders.standaloneSetup(controller).build()

        mockMvc.get("/files/list")
            .andExpect {
                status { isNotFound() }
            }
    }
}
