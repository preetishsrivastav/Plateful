package com.example.plateful.model.network

import com.example.plateful.model.entities.Recipes
import com.example.plateful.utils.Constants
import io.reactivex.rxjava3.core.Single
import retrofit2.Retrofit
import retrofit2.adapter.rxjava3.RxJava3CallAdapterFactory
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.create

object RetrofitInstance {

    private val api = Retrofit.Builder()
        .baseUrl(Constants.BASE_URL)
        .addConverterFactory(GsonConverterFactory.create())
        .addCallAdapterFactory(RxJava3CallAdapterFactory.create())
        .build()
        .create(RandomDishApi::class.java)

    fun getRandomDish():Single<Recipes.Recipe>{
        return api.getRandomDish(
            Constants.API_KEY_VALUE,
            Constants.LIMIT_LICENCE_VALUE,
            Constants.TAGS_VALUE,
            Constants.NUMBER_VALUE
        )
    }
}