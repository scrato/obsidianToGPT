package de.scrato.obsidianToGpt.dto

data class FileListInfo(val fileName: String, val filePath: String) {
    fun getFullPath(): String {
        return if (filePath.isEmpty()) fileName else "$filePath\\$fileName"
    }
}
