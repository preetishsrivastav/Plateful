package com.example.plateful.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.plateful.repository.Repository
import java.lang.IllegalArgumentException

class PlatefulViewModelFactory(private val repository: Repository):ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if(modelClass.isAssignableFrom(PlatefulViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return PlatefulViewModel(repository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel Class")
    }

}