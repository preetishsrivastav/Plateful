package com.example.plateful.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import com.example.plateful.model.database.PlatefulDao
import com.example.plateful.model.entities.PlatefulModel
import com.example.plateful.repository.Repository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.launch

class PlatefulViewModel(private val repository: Repository) : ViewModel() {

    fun insertDishDetails(platefulModel: PlatefulModel) = viewModelScope.launch {
        repository.insertDishData(platefulModel)
    }

    fun updateDishDetails(platefulModel: PlatefulModel) = viewModelScope.launch {
        repository.updateDish(platefulModel)
    }

    fun deleteDish(platefulModel: PlatefulModel) = viewModelScope.launch {
        repository.deleteDish(platefulModel)
    }


    val allDishesList: LiveData<List<PlatefulModel>> = repository.allDishesList.asLiveData()
    val favouriteDishList: LiveData<List<PlatefulModel>> =
        repository.favouriteDishesList.asLiveData()

    fun filteredList(filterType: String): LiveData<List<PlatefulModel>> =
        repository.getFilteredList(filterType).asLiveData()


}