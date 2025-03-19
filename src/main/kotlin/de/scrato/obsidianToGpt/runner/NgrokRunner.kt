package de.scrato.obsidianToGpt.runner

import org.springframework.boot.ApplicationArguments
import org.springframework.boot.ApplicationRunner
import org.springframework.stereotype.Component
import java.io.BufferedReader
import java.io.InputStreamReader

@Component
class NgrokRunner : ApplicationRunner {
    override fun run(args: ApplicationArguments?) {
        println("Ngrok Runner gestartet")
        Thread {
            try {
                // Falls ngrok nicht im PATH liegt, hier den absoluten Pfad angeben.
                val process = ProcessBuilder("ngrok", "http","--url=otg.herrscote.de", "8080")
                    .redirectErrorStream(true)
                    .start()

                // Ausgabe des ngrok-Prozesses lesen (optional)
                val reader = BufferedReader(InputStreamReader(process.inputStream))
                reader.lines().forEach { println(it) }
            } catch (ex: Exception) {
                ex.printStackTrace()
            }
        }.start()
    }
}
