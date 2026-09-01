package com.example.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.sqlite.db.SupportSQLiteDatabase
import com.example.data.local.dao.LeapDao
import com.example.data.local.entities.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

@Database(
    entities = [
        UserEntity::class,
        StudentEntity::class,
        OrganizationEntity::class,
        SupervisorEntity::class,
        InternshipEntity::class,
        ActionPlanEntity::class,
        ActionPlanTaskEntity::class,
        DailyLogEntity::class,
        WeeklyReportEntity::class,
        SupervisorFeedbackEntity::class,
        AssessmentEntity::class,
        AssessmentItemEntity::class,
        SelfEvaluationEntity::class,
        SubmissionChecklistEntity::class,
        InternshipReportEntity::class,
        NotificationEntity::class,
        ScannedDocumentEntity::class,
        InternshipApplicationEntity::class
    ],

    version = 1,
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {

    abstract fun leapDao(): LeapDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getInstance(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "leap_internship_database.db"
                )
                .fallbackToDestructiveMigration()
                .addCallback(object : Callback() {
                    override fun onCreate(db: SupportSQLiteDatabase) {
                        super.onCreate(db)
                        CoroutineScope(Dispatchers.IO).launch {
                            InitialDataGenerator.populateInitialData(getInstance(context).leapDao())
                        }
                    }
                })
                .build()
                INSTANCE = instance
                instance
            }
        }

        fun getDatabase(context: Context): AppDatabase = getInstance(context)
    }
}
