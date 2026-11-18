package com.studyflow.app.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.studyflow.app.data.local.dao.AnswerRecordDao
import com.studyflow.app.data.local.dao.ContentDao
import com.studyflow.app.data.local.dao.DisciplineDao
import com.studyflow.app.data.local.dao.MaterialDao
import com.studyflow.app.data.local.dao.NoteDao
import com.studyflow.app.data.local.dao.QuestionDao
import com.studyflow.app.data.local.dao.SubjectDao
import com.studyflow.app.data.local.dao.TipDao
import com.studyflow.app.data.local.entity.AnswerRecordEntity
import com.studyflow.app.data.local.entity.ContentEntity
import com.studyflow.app.data.local.entity.DisciplineEntity
import com.studyflow.app.data.local.entity.MaterialEntity
import com.studyflow.app.data.local.entity.NoteEntity
import com.studyflow.app.data.local.entity.QuestionEntity
import com.studyflow.app.data.local.entity.SubjectEntity
import com.studyflow.app.data.local.entity.TipEntity

@Database(
    entities = [
        DisciplineEntity::class,
        SubjectEntity::class,
        ContentEntity::class,
        TipEntity::class,
        MaterialEntity::class,
        QuestionEntity::class,
        AnswerRecordEntity::class,
        NoteEntity::class
    ],
    version = 1,
    exportSchema = true
)
@TypeConverters(Converters::class)
abstract class StudyFlowDatabase : RoomDatabase() {

    abstract fun disciplineDao(): DisciplineDao
    abstract fun subjectDao(): SubjectDao
    abstract fun contentDao(): ContentDao
    abstract fun tipDao(): TipDao
    abstract fun materialDao(): MaterialDao
    abstract fun questionDao(): QuestionDao
    abstract fun answerRecordDao(): AnswerRecordDao
    abstract fun noteDao(): NoteDao

    companion object {
        @Volatile
        private var INSTANCE: StudyFlowDatabase? = null

        fun getInstance(context: Context): StudyFlowDatabase =
            INSTANCE ?: synchronized(this) {
                INSTANCE ?: Room.databaseBuilder(
                    context.applicationContext,
                    StudyFlowDatabase::class.java,
                    "studyflow.db"
                ).build().also { INSTANCE = it }
            }
    }
}
