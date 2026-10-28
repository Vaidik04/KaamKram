package com.kaamkram.app.data.local.db

import androidx.room.TypeConverter
import com.kaamkram.app.domain.model.EstimateStatus
import com.kaamkram.app.domain.model.InvoiceStatus
import com.kaamkram.app.domain.model.JobEventType
import com.kaamkram.app.domain.model.JobStatus
import com.kaamkram.app.domain.model.PaymentMode
import com.kaamkram.app.domain.model.PaymentType
import com.kaamkram.app.domain.model.PhotoCategory
import com.kaamkram.app.domain.model.PhotoSource
import java.math.BigDecimal

class Converters {

    @TypeConverter
    fun fromBigDecimal(value: BigDecimal?): String? = value?.toPlainString()

    @TypeConverter
    fun toBigDecimal(value: String?): BigDecimal? = value?.let { BigDecimal(it) }

    @TypeConverter
    fun fromJobStatus(status: JobStatus?): String? = status?.name

    @TypeConverter
    fun toJobStatus(value: String?): JobStatus? = value?.let { JobStatus.valueOf(it) }

    @TypeConverter
    fun fromEstimateStatus(status: EstimateStatus?): String? = status?.name

    @TypeConverter
    fun toEstimateStatus(value: String?): EstimateStatus? = value?.let { EstimateStatus.valueOf(it) }

    @TypeConverter
    fun fromInvoiceStatus(status: InvoiceStatus?): String? = status?.name

    @TypeConverter
    fun toInvoiceStatus(value: String?): InvoiceStatus? = value?.let { InvoiceStatus.valueOf(it) }

    @TypeConverter
    fun fromPaymentMode(mode: PaymentMode?): String? = mode?.name

    @TypeConverter
    fun toPaymentMode(value: String?): PaymentMode? = value?.let { PaymentMode.valueOf(it) }

    @TypeConverter
    fun fromPaymentType(type: PaymentType?): String? = type?.name

    @TypeConverter
    fun toPaymentType(value: String?): PaymentType? = value?.let { PaymentType.valueOf(it) }

    @TypeConverter
    fun fromJobEventType(type: JobEventType?): String? = type?.name

    @TypeConverter
    fun toJobEventType(value: String?): JobEventType? = value?.let { JobEventType.valueOf(it) }

    @TypeConverter
    fun fromPhotoCategory(category: PhotoCategory?): String? = category?.name

    @TypeConverter
    fun toPhotoCategory(value: String?): PhotoCategory? = value?.let { PhotoCategory.valueOf(it) }

    @TypeConverter
    fun fromPhotoSource(source: PhotoSource?): String? = source?.name

    @TypeConverter
    fun toPhotoSource(value: String?): PhotoSource? = value?.let { PhotoSource.valueOf(it) }
}
