package de.scrato.obsidianToGpt.dto

data class FileListInfo(val fileName: String, val filePath: String, val mimeType: String) {
    fun getFullPath(): String {
        return if (filePath.isEmpty()) fileName else "$filePath\\$fileName"
    }
}
