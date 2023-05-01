package com.example.plateful.viewmodel

import androidx.lifecycle.*
import com.example.plateful.model.database.FavDishRepository
import com.example.plateful.model.entities.FavDish

import kotlinx.coroutines.launch

/**
 * The ViewModel's role is to provide data to the UI and survive configuration changes.
 * A ViewModel acts as a communication center between the Repository and the UI.
 * You can also use a ViewModel to share data between fragments.
 * The ViewModel is part of the lifecycle library.
 *
 * @param repository - The repository class is
 */
class FavDishViewModel(private val repository: FavDishRepository) : ViewModel() {

    /**
     * Launching a new coroutine to insert the data in a non-blocking way.
     */
    fun insert(dish: FavDish) = viewModelScope.launch {
        // Call the repository function and pass the details.
        repository.insertFavDishData(dish)
    }

    /** Using LiveData and caching what allDishes returns has several benefits:
     * We can put an observer on the data (instead of polling for changes) and only
     * update the UI when the data actually changes.
     * Repository is completely separated from the UI through the ViewModel.
     */
    val allDishesList: LiveData<List<FavDish>> = repository.allDishesList.asLiveData()

    /**
     * Launching a new coroutine to update the data in a non-blocking way
     */
    fun update(dish: FavDish) = viewModelScope.launch {
        repository.updateFavDishData(dish)
    }

    // Get the list of favorite dishes that we can populate in the UI.
    /** Using LiveData and caching what favoriteDishes returns has several benefits:
     * We can put an observer on the data (instead of polling for changes) and only
     * update the UI when the data actually changes.
     * Repository is completely separated from the UI through the ViewModel.
     */
    val favoriteDishes: LiveData<List<FavDish>> = repository.favoriteDishes.asLiveData()

    /**
     * Launching a new coroutine to delete the data in a non-blocking way.
     */
    fun delete(dish: FavDish) = viewModelScope.launch {
        // Call the repository function and pass the details.
        repository.deleteFavDishData(dish)
    }

    /**
     * A function to get the filtered list of dishes based on the dish type selection.
     *
     * @param value - dish type selection
     */
    fun getFilteredList(value: String): LiveData<List<FavDish>> =
        repository.filteredListDishes(value).asLiveData()
}

/**
 * To create the ViewModel we implement a ViewModelProvider.Factory that gets as a parameter the dependencies
 * needed to create FavDishViewModel: the FavDishRepository.
 * By using viewModels and ViewModelProvider.Factory then the framework will take care of the lifecycle of the ViewModel.
 * It will survive configuration changes and even if the Activity is recreated,
 * you'll always get the right instance of the FavDishViewModel class.
 */
class FavDishViewModelFactory(private val repository: FavDishRepository) :
    ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(FavDishViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return FavDishViewModel(repository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}