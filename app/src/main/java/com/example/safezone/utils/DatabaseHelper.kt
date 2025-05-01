package com.example.safezone.utils

import android.content.ContentValues
import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import com.example.safezone.models.Alert
import com.example.safezone.models.IncidentReport

/**
 * SQLite database helper for SafeZone app
 * Handles database creation and data access operations
 */
class DatabaseHelper(context: Context) : SQLiteOpenHelper(
    context, DATABASE_NAME, null, DATABASE_VERSION
) {
    companion object {
        private const val DATABASE_VERSION = 1
        private const val DATABASE_NAME = "SafeZone.db"
        
        // Alerts table
        private const val TABLE_ALERTS = "alerts"
        private const val COLUMN_ALERT_ID = "id"
        private const val COLUMN_ALERT_TITLE = "title"
        private const val COLUMN_ALERT_DESCRIPTION = "description"
        private const val COLUMN_ALERT_LOCATION = "location"
        private const val COLUMN_ALERT_SEVERITY = "severity"
        private const val COLUMN_ALERT_TIMESTAMP = "timestamp"
        
        // Reports table
        private const val TABLE_REPORTS = "reports"
        private const val COLUMN_REPORT_ID = "id"
        private const val COLUMN_REPORT_TITLE = "title"
        private const val COLUMN_REPORT_DESCRIPTION = "description"
        private const val COLUMN_REPORT_LOCATION = "location"
        private const val COLUMN_REPORT_TIMESTAMP = "timestamp"
        private const val COLUMN_REPORT_SAFETY_RATING = "safety_rating"
        private const val COLUMN_REPORT_IMAGE_URI = "image_uri"
    }
    
    override fun onCreate(db: SQLiteDatabase) {
        // Create alerts table
        val createAlertsTable = """
            CREATE TABLE $TABLE_ALERTS (
                $COLUMN_ALERT_ID INTEGER PRIMARY KEY,
                $COLUMN_ALERT_TITLE TEXT,
                $COLUMN_ALERT_DESCRIPTION TEXT,
                $COLUMN_ALERT_LOCATION TEXT,
                $COLUMN_ALERT_SEVERITY TEXT,
                $COLUMN_ALERT_TIMESTAMP INTEGER
            )
        """.trimIndent()
        
        // Create reports table
        val createReportsTable = """
            CREATE TABLE $TABLE_REPORTS (
                $COLUMN_REPORT_ID TEXT PRIMARY KEY,
                $COLUMN_REPORT_TITLE TEXT,
                $COLUMN_REPORT_DESCRIPTION TEXT,
                $COLUMN_REPORT_LOCATION TEXT,
                $COLUMN_REPORT_TIMESTAMP INTEGER,
                $COLUMN_REPORT_SAFETY_RATING INTEGER,
                $COLUMN_REPORT_IMAGE_URI TEXT
            )
        """.trimIndent()
        
        db.execSQL(createAlertsTable)
        db.execSQL(createReportsTable)
    }
    
    override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
        // In case of database upgrade, drop the old tables and recreate
        db.execSQL("DROP TABLE IF EXISTS $TABLE_ALERTS")
        db.execSQL("DROP TABLE IF EXISTS $TABLE_REPORTS")
        onCreate(db)
    }
    
    /**
     * Insert a new alert into the database
     */
    fun insertAlert(alert: Alert): Long {
        val db = this.writableDatabase
        val values = ContentValues().apply {
            put(COLUMN_ALERT_ID, alert.id)
            put(COLUMN_ALERT_TITLE, alert.title)
            put(COLUMN_ALERT_DESCRIPTION, alert.description)
            put(COLUMN_ALERT_LOCATION, alert.location)
            put(COLUMN_ALERT_SEVERITY, alert.severity)
            put(COLUMN_ALERT_TIMESTAMP, alert.timestamp)
        }
        
        val id = db.insert(TABLE_ALERTS, null, values)
        db.close()
        return id
    }
    
    /**
     * Get all alerts from the database, ordered by timestamp (newest first)
     */
    fun getAllAlerts(): List<Alert> {
        val alertList = mutableListOf<Alert>()
        val selectQuery = "SELECT * FROM $TABLE_ALERTS ORDER BY $COLUMN_ALERT_TIMESTAMP DESC"
        
        val db = this.readableDatabase
        val cursor = db.rawQuery(selectQuery, null)
        
        if (cursor.moveToFirst()) {
            do {
                val id = cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_ALERT_ID))
                val title = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_ALERT_TITLE))
                val description = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_ALERT_DESCRIPTION))
                val location = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_ALERT_LOCATION))
                val severity = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_ALERT_SEVERITY))
                val timestamp = cursor.getLong(cursor.getColumnIndexOrThrow(COLUMN_ALERT_TIMESTAMP))
                
                val alert = Alert(id, title, description, location, severity, timestamp)
                alertList.add(alert)
            } while (cursor.moveToNext())
        }
        
        cursor.close()
        db.close()
        return alertList
    }
    
    /**
     * Insert a new incident report into the database
     */
    fun insertReport(report: IncidentReport): Long {
        val db = this.writableDatabase
        val values = ContentValues().apply {
            put(COLUMN_REPORT_ID, report.id)
            put(COLUMN_REPORT_TITLE, report.title)
            put(COLUMN_REPORT_DESCRIPTION, report.description)
            put(COLUMN_REPORT_LOCATION, report.location)
            put(COLUMN_REPORT_TIMESTAMP, report.timestamp)
            put(COLUMN_REPORT_SAFETY_RATING, report.safetyRating)
            put(COLUMN_REPORT_IMAGE_URI, report.imageUri)
        }
        
        val id = db.insert(TABLE_REPORTS, null, values)
        db.close()
        return id
    }
    
    /**
     * Get all incident reports from the database, ordered by timestamp (newest first)
     */
    fun getAllReports(): List<IncidentReport> {
        val reportList = mutableListOf<IncidentReport>()
        val selectQuery = "SELECT * FROM $TABLE_REPORTS ORDER BY $COLUMN_REPORT_TIMESTAMP DESC"
        
        val db = this.readableDatabase
        val cursor = db.rawQuery(selectQuery, null)
        
        if (cursor.moveToFirst()) {
            do {
                val id = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_REPORT_ID))
                val title = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_REPORT_TITLE))
                val description = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_REPORT_DESCRIPTION))
                val location = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_REPORT_LOCATION))
                val timestamp = cursor.getLong(cursor.getColumnIndexOrThrow(COLUMN_REPORT_TIMESTAMP))
                val safetyRating = cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_REPORT_SAFETY_RATING))
                val imageUri = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_REPORT_IMAGE_URI))
                
                val report = IncidentReport(id, title, description, location, timestamp, safetyRating, imageUri)
                reportList.add(report)
            } while (cursor.moveToNext())
        }
        
        cursor.close()
        db.close()
        return reportList
    }
}
