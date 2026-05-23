package com.example.data

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "employees")
data class Employee(
    @PrimaryKey val id: String, // e.g. "sam", "vaishu", "loki", "aathi", "naomi", "sharmi", "jojo", "priyanka"
    val name: String,
    val role: String, // "Developer" or "Designer" or "Boss"
    val email: String,
    val isActive: Boolean = true,
    val password: String = "password123", // secure password field
    val avatarUri: String? = null // custom avatar identifier (preset avatars or local)
)

@Entity(tableName = "attendance")
data class AttendanceRecord(
    @PrimaryKey val date: String, // format "dd/MM/yyyy"
    val dayOfWeek: String, // "Monday", "Tuesday", etc.
    val p1: String?, // Chair 1 (Developer)
    val p2: String?, // Chair 2 (Developer)
    val p3: String?, // Chair 3 (Designer)
    val absent: String?, // comma separated list of absentees, e.g. "vaishu"
    val note: String? = null // optional note, e.g. "sick leave"
)

@Entity(tableName = "leave_requests")
data class LeaveRequest(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val employeeId: String,
    val employeeName: String,
    val date: String, // "dd/MM/yyyy"
    val reason: String,
    val status: String, // "PENDING", "APPROVED", "REJECTED"
    val timestamp: Long = System.currentTimeMillis()
)

@Entity(tableName = "meetings")
data class Meeting(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val title: String,
    val date: String, // "dd/MM/yyyy"
    val time: String, // e.g. "10:15 AM", "06:00 PM"
    val gmeetLink: String,
    val createdBy: String, // "Boss" / Boss name
    val notes: String? = null
)

@Entity(tableName = "chat_messages")
data class ChatMessage(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val senderId: String,
    val senderName: String,
    val senderRole: String,
    val receiverId: String?, // null if it is a general Group Chat message, otherwise target employeeId
    val messageText: String,
    val timestamp: Long = System.currentTimeMillis()
)

@Entity(tableName = "app_notifications")
data class AppNotification(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val employeeId: String, // "all" for general broadcasts, or specific employee id
    val title: String,
    val message: String,
    val timestamp: Long = System.currentTimeMillis(),
    val isRead: Boolean = false
)
