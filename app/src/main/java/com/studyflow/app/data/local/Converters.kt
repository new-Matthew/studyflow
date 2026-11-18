package com.studyflow.app.data.local

import androidx.room.TypeConverter
import com.studyflow.app.data.model.Difficulty
import com.studyflow.app.data.model.ErrorReason
import com.studyflow.app.data.model.LearningStatus
import com.studyflow.app.data.model.MaterialType
import com.studyflow.app.data.model.TipType

class Converters {
    @TypeConverter
    fun fromLearningStatus(value: LearningStatus?): String? = value?.name

    @TypeConverter
    fun toLearningStatus(value: String?): LearningStatus? =
        value?.let { LearningStatus.valueOf(it) }

    @TypeConverter
    fun fromTipType(value: TipType?): String? = value?.name

    @TypeConverter
    fun toTipType(value: String?): TipType? = value?.let { TipType.valueOf(it) }

    @TypeConverter
    fun fromMaterialType(value: MaterialType?): String? = value?.name

    @TypeConverter
    fun toMaterialType(value: String?): MaterialType? = value?.let { MaterialType.valueOf(it) }

    @TypeConverter
    fun fromDifficulty(value: Difficulty?): String? = value?.name

    @TypeConverter
    fun toDifficulty(value: String?): Difficulty? = value?.let { Difficulty.valueOf(it) }

    @TypeConverter
    fun fromErrorReason(value: ErrorReason?): String? = value?.name

    @TypeConverter
    fun toErrorReason(value: String?): ErrorReason? = value?.let { ErrorReason.valueOf(it) }
}
