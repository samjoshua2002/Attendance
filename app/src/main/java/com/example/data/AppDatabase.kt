package com.example.data

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.sqlite.db.SupportSQLiteDatabase
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

@Database(
    entities = [
        Employee::class,
        AttendanceRecord::class,
        LeaveRequest::class,
        Meeting::class,
        ChatMessage::class,
        AppNotification::class
    ],
    version = 5,
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {

    abstract fun employeeDao(): EmployeeDao
    abstract fun attendanceDao(): AttendanceDao
    abstract fun leaveRequestDao(): LeaveRequestDao
    abstract fun meetingDao(): MeetingDao
    abstract fun chatMessageDao(): ChatMessageDao
    abstract fun appNotificationDao(): AppNotificationDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getDatabase(context: Context, scope: CoroutineScope): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "skillskapes_database"
                )
                .addCallback(AppDatabaseCallback(scope))
                .fallbackToDestructiveMigration()
                .build()
                INSTANCE = instance
                instance
            }
        }
    }

    private class AppDatabaseCallback(
        private val scope: CoroutineScope
    ) : RoomDatabase.Callback() {
        override fun onOpen(db: SupportSQLiteDatabase) {
            super.onOpen(db)
            INSTANCE?.let { database ->
                scope.launch(Dispatchers.IO) {
                    val employeeDao = database.employeeDao()
                    // Check if we already have data
                    if (employeeDao.getEmployeeCount() == 0) {
                        // Populate employees
                        SeedData.INITIAL_EMPLOYEES.forEach { emp ->
                            employeeDao.insertEmployee(emp)
                        }

                        // Populate initial meetings
                        val meetingDao = database.meetingDao()
                        SeedData.INITIAL_MEETINGS.forEach { meeting ->
                            meetingDao.insertMeeting(meeting)
                        }

                        // Populate attendance history
                        val attendanceDao = database.attendanceDao()
                        val historicalRecords = SeedData.parseSeedAttendance()
                        attendanceDao.insertAllAttendance(historicalRecords)
                    }
                }
            }
        }
    }
}
