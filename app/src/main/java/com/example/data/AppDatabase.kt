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
    version = 1,
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
        override fun onCreate(db: SupportSQLiteDatabase) {
            super.onCreate(db)
            INSTANCE?.let { database ->
                scope.launch(Dispatchers.IO) {
                    // Populate employees
                    val employeeDao = database.employeeDao()
                    SeedData.INITIAL_EMPLOYEES.forEach { emp ->
                        employeeDao.insertEmployee(emp)
                    }

                    // Populate initial meetings
                    val meetingDao = database.meetingDao()
                    SeedData.INITIAL_MEETINGS.forEach { meeting ->
                        meetingDao.insertMeeting(meeting)
                    }

                    // Populate attendance history (over 100 entries)
                    val attendanceDao = database.attendanceDao()
                    val historicalRecords = SeedData.parseSeedAttendance()
                    attendanceDao.insertAllAttendance(historicalRecords)
                }
            }
        }
    }
}
