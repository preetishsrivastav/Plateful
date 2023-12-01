package com.example.plateful.viewmodel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.plateful.model.entities.Recipes
import com.example.plateful.model.network.RandomDishApi
import com.example.plateful.model.network.RetrofitInstance
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers
import io.reactivex.rxjava3.core.Scheduler
import io.reactivex.rxjava3.disposables.CompositeDisposable
import io.reactivex.rxjava3.observers.DisposableSingleObserver
import io.reactivex.rxjava3.schedulers.Schedulers

class RandomDishViewModel : ViewModel() {

    private val retrofitInstance = RetrofitInstance
//    Disposables Help us to control the lifecycle of observables
    private val compositeDisposable = CompositeDisposable()

    val loadRandomDish = MutableLiveData<Boolean>()
    val randomDishResponse = MutableLiveData<Recipes.Recipe>()
    val randomDishLoadingError = MutableLiveData<Boolean>()

    fun getRandomDishFromAPI() {
        loadRandomDish.value = true
        compositeDisposable.add(
            retrofitInstance.getRandomDish()
                .subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeWith(object : DisposableSingleObserver<Recipes.Recipe>() {
                    override fun onSuccess(value: Recipes.Recipe) {
                        loadRandomDish.value = false
                        randomDishResponse.value = value
                        randomDishLoadingError.value = false
                    }

                    override fun onError(e: Throwable) {
                        loadRandomDish.value = false
                        randomDishLoadingError.value = true
                        e.printStackTrace()
                    }
                })
        )


    }


}