package com.example.plateful.model.network

import com.example.plateful.model.entities.Recipes
import com.example.plateful.utils.Constants
import io.reactivex.rxjava3.core.Single
import retrofit2.http.GET
import retrofit2.http.Query

interface RandomDishApi {

@GET(Constants.END_POINT)
   fun getRandomDish(
    @Query(Constants.API_KEY) apiKey:String,
    @Query(Constants.LICENCE) limitLicence:Boolean,
    @Query(Constants.TAGS) tags:String,
    @Query(Constants.NUMBER) number:Int
   ):Single<Recipes.Recipe>



}