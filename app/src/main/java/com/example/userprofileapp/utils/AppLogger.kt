package com.example.userprofileapp.utils

import android.content.Context
import android.util.Log
import java.io.File
import java.io.FileWriter
import java.io.IOException
import java.text.SimpleDateFormat
import java.util.*

object AppLogger {
    private lateinit var logFile: File

    fun initialize(context: Context) {
        try {
            val logDir = File(context.filesDir, "logs")
            if (!logDir.exists()) logDir.mkdirs()
            logFile = File(logDir, "app_logs.txt")
        }catch (exception : Exception){
            Log.d("",exception.message.toString())
        }
    }

    fun log(tag: String, message: String) {
        val timeStamp = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault()).format(Date())
        val logMessage = "[$timeStamp] [$tag]: $message"

        Log.d(tag, message)

        writeLogToFile(logMessage)
    }

    private fun writeLogToFile(message: String) {
        try {
            val writer = FileWriter(logFile, true)
            writer.append(message).append("\n")
            writer.flush()
            writer.close()
        } catch (e: IOException) {
            Log.e("AppLogger", "Error writing log: ${e.message}", e)
        }
    }

    fun getLogFile(): File = logFile
}
