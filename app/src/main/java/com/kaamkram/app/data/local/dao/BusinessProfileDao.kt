package com.kaamkram.app.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.kaamkram.app.data.local.entity.BusinessProfileEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface BusinessProfileDao {

    @Query("SELECT * FROM business_profile WHERE id = 'default_profile' LIMIT 1")
    fun getProfile(): Flow<BusinessProfileEntity?>

    @Query("SELECT * FROM business_profile WHERE id = 'default_profile' LIMIT 1")
    suspend fun getProfileDirect(): BusinessProfileEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertOrUpdateProfile(profile: BusinessProfileEntity)

    @Query("UPDATE business_profile SET nextJobNumber = nextJobNumber + 1 WHERE id = 'default_profile'")
    suspend fun incrementJobNumber()

    @Query("UPDATE business_profile SET nextInvoiceNumber = nextInvoiceNumber + 1 WHERE id = 'default_profile'")
    suspend fun incrementInvoiceNumber()
}
