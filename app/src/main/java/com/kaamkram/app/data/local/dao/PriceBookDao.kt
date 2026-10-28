package com.kaamkram.app.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.kaamkram.app.data.local.entity.PriceBookItemEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface PriceBookDao {

    @Query("SELECT * FROM price_book_items ORDER BY sortOrder ASC, name ASC")
    fun getAllItems(): Flow<List<PriceBookItemEntity>>

    @Query("SELECT * FROM price_book_items WHERE tradeCategory = :category ORDER BY sortOrder ASC, name ASC")
    fun getItemsForCategory(category: String): Flow<List<PriceBookItemEntity>>

    @Query("SELECT * FROM price_book_items WHERE name LIKE '%' || :query || '%' ORDER BY name ASC")
    fun searchItems(query: String): Flow<List<PriceBookItemEntity>>

    @Query("SELECT COUNT(*) FROM price_book_items")
    suspend fun getItemCount(): Int

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertItems(items: List<PriceBookItemEntity>)

    @Insert(onConflict = OnConflictStrategy.ABORT)
    suspend fun insertItem(item: PriceBookItemEntity)

    @Update
    suspend fun updateItem(item: PriceBookItemEntity)

    @Query("DELETE FROM price_book_items WHERE id = :id")
    suspend fun deleteItem(id: String)
}
