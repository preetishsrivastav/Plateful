package com.example.plateful.model.database

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.example.plateful.model.entities.PlatefulModel
import kotlinx.coroutines.flow.Flow

@Dao
interface PlatefulDao {

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertDishDetails(platefulDish: PlatefulModel)

    @Query("SELECT * FROM plateful_dishes ORDER BY id")
    fun getAllDishes(): Flow<List<PlatefulModel>>

    @Update
    suspend fun updateDishDetails(platefulDish: PlatefulModel)

    @Query("SELECT * FROM plateful_dishes WHERE favoriteDish = 1")
     fun getFavouriteDishes(): Flow<List<PlatefulModel>>


     @Delete
     suspend fun deleteDish(platefulModel: PlatefulModel)

     @Query("SELECT * FROM plateful_dishes WHERE type = :filterType ")
     fun getFilteredList(filterType:String):Flow<List<PlatefulModel>>

}