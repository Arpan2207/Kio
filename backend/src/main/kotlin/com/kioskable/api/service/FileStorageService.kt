package com.kioskable.api.service

import com.kioskable.api.exception.FileStorageException
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Service
import java.io.IOException
import java.nio.file.Files
import java.nio.file.Path
import java.nio.file.Paths
import java.nio.file.StandardCopyOption
import java.util.UUID

@Service
class FileStorageService {
    
    @Value("\${file.upload-dir:uploads}")
    private lateinit var uploadDir: String
    
    @Value("\${app.base-url:http://localhost:8080}")
    private lateinit var baseUrl: String
    
    /**
     * Initialize storage directories
     */
    fun init() {
        try {
            val rootDir = Paths.get(uploadDir)
            Files.createDirectories(rootDir)
        } catch (ex: IOException) {
            throw FileStorageException("Could not initialize storage", ex)
        }
    }
    
    /**
     * Store a file in the specified directory
     */
    fun storeFile(fileBytes: ByteArray, originalFilename: String, directory: String, contentType: String): String {
        try {
            // Create directory if it doesn't exist
            val targetDir = Paths.get(uploadDir, directory)
            Files.createDirectories(targetDir)
            
            // Generate a unique filename to prevent conflicts
            val filename = generateUniqueFilename(originalFilename)
            val targetPath = targetDir.resolve(filename)
            
            // Copy the file to the target location
            Files.copy(fileBytes.inputStream(), targetPath, StandardCopyOption.REPLACE_EXISTING)
            
            // Return the URL to access the file
            return "$baseUrl/$uploadDir/$directory/$filename"
        } catch (ex: IOException) {
            throw FileStorageException("Failed to store file $originalFilename", ex)
        }
    }
    
    /**
     * Delete a file
     */
    fun deleteFile(fileUrl: String): Boolean {
        try {
            // Extract the path from the URL
            val filePath = extractPathFromUrl(fileUrl)
            if (filePath != null) {
                val path = Paths.get(filePath)
                return Files.deleteIfExists(path)
            }
            return false
        } catch (ex: IOException) {
            throw FileStorageException("Failed to delete file $fileUrl", ex)
        }
    }
    
    /**
     * Generate a unique filename to prevent conflicts
     */
    private fun generateUniqueFilename(originalFilename: String): String {
        val fileExtension = getFileExtension(originalFilename)
        return "${UUID.randomUUID()}$fileExtension"
    }
    
    /**
     * Extract the file extension from a filename
     */
    private fun getFileExtension(filename: String): String {
        return if (filename.contains(".")) {
            filename.substring(filename.lastIndexOf('.'))
        } else {
            ""
        }
    }
    
    /**
     * Extract the file path from a URL
     */
    private fun extractPathFromUrl(fileUrl: String): String? {
        // Check if the URL starts with the base URL
        if (fileUrl.startsWith(baseUrl)) {
            // Remove the base URL and return the path
            return fileUrl.substring(baseUrl.length + 1)
        }
        return null
    }
} 