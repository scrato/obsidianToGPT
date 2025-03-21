package de.scrato.obsidianToGpt.runner

import org.springframework.boot.ApplicationArguments
import org.springframework.boot.ApplicationRunner
import org.springframework.stereotype.Component

@Component
class NgrokRunner : ApplicationRunner {
    override fun run(args: ApplicationArguments?) {
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
