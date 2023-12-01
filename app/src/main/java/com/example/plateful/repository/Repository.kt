package com.example.plateful.repository

import androidx.annotation.WorkerThread
import com.example.plateful.model.database.PlatefulDao
import com.example.plateful.model.entities.PlatefulModel
import kotlinx.coroutines.flow.Flow

class Repository(private val platefulDao: PlatefulDao) {

    @WorkerThread
    suspend fun insertDishData(platefulModel: PlatefulModel) {
        platefulDao.insertDishDetails(platefulModel)
    }

    @WorkerThread
    suspend fun updateDish(platefulModel: PlatefulModel){
        platefulDao.updateDishDetails(platefulModel)
    }

    @WorkerThread
    suspend fun deleteDish(platefulModel: PlatefulModel){
        platefulDao.deleteDish(platefulModel)
    }

    val allDishesList: Flow<List<PlatefulModel>> = platefulDao.getAllDishes()
    val favouriteDishesList:Flow<List<PlatefulModel>> = platefulDao.getFavouriteDishes()

    fun getFilteredList(filterType:String):Flow<List<PlatefulModel>> =
        platefulDao.getFilteredList(filterType)
}