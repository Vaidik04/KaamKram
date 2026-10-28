package com.kaamkram.app.data.local.db

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.kaamkram.app.data.local.dao.BusinessProfileDao
import com.kaamkram.app.data.local.dao.CustomerDao
import com.kaamkram.app.data.local.dao.EstimateDao
import com.kaamkram.app.data.local.dao.InvoiceDao
import com.kaamkram.app.data.local.dao.JobDao
import com.kaamkram.app.data.local.dao.JobEventDao
import com.kaamkram.app.data.local.dao.PaymentDao
import com.kaamkram.app.data.local.dao.PhotoEvidenceDao
import com.kaamkram.app.data.local.dao.PriceBookDao
import com.kaamkram.app.data.local.dao.WarrantyDao
import com.kaamkram.app.data.local.entity.BusinessProfileEntity
import com.kaamkram.app.data.local.entity.CustomerEntity
import com.kaamkram.app.data.local.entity.EstimateEntity
import com.kaamkram.app.data.local.entity.EstimateLineItemEntity
import com.kaamkram.app.data.local.entity.EstimateVersionEntity
import com.kaamkram.app.data.local.entity.InvoiceEntity
import com.kaamkram.app.data.local.entity.InvoiceLineItemEntity
import com.kaamkram.app.data.local.entity.JobEntity
import com.kaamkram.app.data.local.entity.JobEventEntity
import com.kaamkram.app.data.local.entity.PaymentEntity
import com.kaamkram.app.data.local.entity.PhotoEvidenceEntity
import com.kaamkram.app.data.local.entity.PriceBookItemEntity
import com.kaamkram.app.data.local.entity.SiteEntity
import com.kaamkram.app.data.local.entity.WarrantyEntity

@Database(
    entities = [
        CustomerEntity::class,
        SiteEntity::class,
        JobEntity::class,
        EstimateEntity::class,
        EstimateVersionEntity::class,
        EstimateLineItemEntity::class,
        InvoiceEntity::class,
        InvoiceLineItemEntity::class,
        PaymentEntity::class,
        BusinessProfileEntity::class,
        PriceBookItemEntity::class,
        JobEventEntity::class,
        PhotoEvidenceEntity::class,
        WarrantyEntity::class
    ],
    version = 1,
    exportSchema = true
)
@TypeConverters(Converters::class)
abstract class KaamKramDatabase : RoomDatabase() {
    abstract fun customerDao(): CustomerDao
    abstract fun jobDao(): JobDao
    abstract fun estimateDao(): EstimateDao
    abstract fun invoiceDao(): InvoiceDao
    abstract fun paymentDao(): PaymentDao
    abstract fun businessProfileDao(): BusinessProfileDao
    abstract fun priceBookDao(): PriceBookDao
    abstract fun jobEventDao(): JobEventDao
    abstract fun photoEvidenceDao(): PhotoEvidenceDao
    abstract fun warrantyDao(): WarrantyDao

    companion object {
        const val DATABASE_NAME = "kaamkram.db"
    }
}
