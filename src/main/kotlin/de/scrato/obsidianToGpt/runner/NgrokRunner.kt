package de.scrato.obsidianToGpt.runner

import de.scrato.obsidianToGpt.config.properties.ApplicationProperties
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.ApplicationArguments
import org.springframework.boot.ApplicationRunner
import org.springframework.stereotype.Component

@Component
class NgrokRunner(@Autowired private val config: ApplicationProperties) : ApplicationRunner {
    override fun run(args: ApplicationArguments?) {
        if(config.useNgrok) {
            startNgrok()
        }

    }
    private fun startNgrok() {
        println("Ngrok Runner gestartet")
        Thread {
            try {
                val process = ProcessBuilder("powershell.exe", "-NoExit", "-Command", "Start-Process ngrok -ArgumentList 'http --url=otg.herrscote.de 8080'")
                    .redirectErrorStream(true)
                    .start()

                process.waitFor()
            } catch (ex: Exception) {
                ex.printStackTrace()
            }
        }.start()
    }
}
