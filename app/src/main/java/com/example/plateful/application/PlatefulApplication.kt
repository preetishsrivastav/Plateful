package com.example.plateful.application

import android.app.Application
import com.example.plateful.model.database.PlatefulDatabase
import com.example.plateful.repository.Repository

class PlatefulApplication:Application() {

    private val database by lazy {
        PlatefulDatabase.getDatabase(this)
    }
    val repository by lazy {
        Repository(database.platefulDao())
    }


}