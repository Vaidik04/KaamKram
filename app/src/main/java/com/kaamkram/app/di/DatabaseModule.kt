package com.kaamkram.app.di

import android.content.Context
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.sqlite.db.SupportSQLiteDatabase
import com.kaamkram.app.data.local.SeedData
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
import com.kaamkram.app.data.local.db.KaamKramDatabase
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import javax.inject.Provider
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {

    @Provides
    @Singleton
    fun provideDatabase(
        @ApplicationContext context: Context,
        priceBookDaoProvider: Provider<PriceBookDao>
    ): KaamKramDatabase {
        return Room.databaseBuilder(
            context,
            KaamKramDatabase::class.java,
            KaamKramDatabase.DATABASE_NAME
        ).addCallback(object : RoomDatabase.Callback() {
            override fun onCreate(db: SupportSQLiteDatabase) {
                super.onCreate(db)
                CoroutineScope(Dispatchers.IO).launch {
                    priceBookDaoProvider.get().insertItems(SeedData.getAllInitialSeedItems())
                }
            }
        }).build()
    }

    @Provides
    fun provideCustomerDao(db: KaamKramDatabase): CustomerDao = db.customerDao()

    @Provides
    fun provideJobDao(db: KaamKramDatabase): JobDao = db.jobDao()

    @Provides
    fun provideEstimateDao(db: KaamKramDatabase): EstimateDao = db.estimateDao()

    @Provides
    fun provideInvoiceDao(db: KaamKramDatabase): InvoiceDao = db.invoiceDao()

    @Provides
    fun providePaymentDao(db: KaamKramDatabase): PaymentDao = db.paymentDao()

    @Provides
    fun provideBusinessProfileDao(db: KaamKramDatabase): BusinessProfileDao = db.businessProfileDao()

    @Provides
    fun providePriceBookDao(db: KaamKramDatabase): PriceBookDao = db.priceBookDao()

    @Provides
    fun provideJobEventDao(db: KaamKramDatabase): JobEventDao = db.jobEventDao()

    @Provides
    fun providePhotoEvidenceDao(db: KaamKramDatabase): PhotoEvidenceDao = db.photoEvidenceDao()

    @Provides
    fun provideWarrantyDao(db: KaamKramDatabase): WarrantyDao = db.warrantyDao()
}
