package com.example

import android.app.Application
import com.example.data.AppDatabase
import com.example.data.Repository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.SupervisorJob

class SkillskapesApplication : Application() {
    val applicationScope = CoroutineScope(SupervisorJob())
    val database by lazy { AppDatabase.getDatabase(this, applicationScope) }
    val repository by lazy { 
        Repository(
            database.employeeDao(),
            database.attendanceDao(),
            database.leaveRequestDao(),
            database.meetingDao(),
            database.chatMessageDao(),
            database.appNotificationDao()
        ) 
    }
}
