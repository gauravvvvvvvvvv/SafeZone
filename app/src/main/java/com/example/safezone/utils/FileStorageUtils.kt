package com.example.safezone.utils

import android.content.Context
import android.graphics.Bitmap
import android.net.Uri
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import java.text.SimpleDateFormat
import java.util.*

/**
 * Utility class to handle file storage operations for SafeZone app
 */
object FileStorageUtils {
    
    private const val DIRECTORY_REPORTS = "reports"
    private const val DIRECTORY_IMAGES = "images"
    
    /**
     * Save a text report to internal storage
     */
    fun saveReport(context: Context, reportTitle: String, reportContent: String): Boolean {
        try {
            // Create reports directory if it doesn't exist
            val reportsDir = File(context.filesDir, DIRECTORY_REPORTS)
            if (!reportsDir.exists()) {
                reportsDir.mkdirs()
            }
            
            // Create file with timestamp and title
            val timestamp = SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(Date())
            val fileName = "${timestamp}_${reportTitle.replace(" ", "_")}.txt"
            val reportFile = File(reportsDir, fileName)
            
            // Write the report content to the file
            FileOutputStream(reportFile).use { outputStream ->
                outputStream.write(reportContent.toByteArray())
            }
            
            return true
        } catch (e: IOException) {
            e.printStackTrace()
            return false
        }
    }
    
    /**
     * Save an image to internal storage
     */
    fun saveImage(context: Context, bitmap: Bitmap): Uri? {
        try {
            // Create images directory if it doesn't exist
            val imagesDir = File(context.filesDir, DIRECTORY_IMAGES)
            if (!imagesDir.exists()) {
                imagesDir.mkdirs()
            }
            
            // Create image file with timestamp
            val timestamp = SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(Date())
            val imageFile = File(imagesDir, "IMG_${timestamp}.jpg")
            
            // Save the bitmap to the file
            FileOutputStream(imageFile).use { outputStream ->
                bitmap.compress(Bitmap.CompressFormat.JPEG, 90, outputStream)
            }
            
            // Return the file URI
            return Uri.fromFile(imageFile)
        } catch (e: IOException) {
            e.printStackTrace()
            return null
        }
    }
    
    /**
     * Get all saved reports
     */
    fun getAllReports(context: Context): List<File> {
        val reportsDir = File(context.filesDir, DIRECTORY_REPORTS)
        if (!reportsDir.exists()) {
            return emptyList()
        }
        
        return reportsDir.listFiles()?.toList() ?: emptyList()
    }
    
    /**
     * Read a report file content
     */
    fun readReport(file: File): String {
        return file.readText()
    }
    
    /**
     * Delete a file
     */
    fun deleteFile(file: File): Boolean {
        return file.delete()
    }
    
    /**
     * Get all saved images
     */
    fun getAllImages(context: Context): List<File> {
        val imagesDir = File(context.filesDir, DIRECTORY_IMAGES)
        if (!imagesDir.exists()) {
            return emptyList()
        }
        
        return imagesDir.listFiles()?.filter { 
            it.name.endsWith(".jpg", ignoreCase = true) 
        } ?: emptyList()
    }
}
