package com.example.plateful.model.network

import com.example.plateful.model.entities.RandomDish
import com.example.plateful.utils.Constants

import io.reactivex.rxjava3.core.Single
import retrofit2.Retrofit
import retrofit2.adapter.rxjava3.RxJava3CallAdapterFactory
import retrofit2.converter.gson.GsonConverterFactory

class RandomDishApiService {

    /**
     * Retrofit adapts a Java interface to HTTP calls by using annotations on the declared methods to
     * define how requests are made. Create instances using {@linkplain Builder the builder} and pass
     * your interface to {create} to generate an implementation.
     */
    private val api = Retrofit.Builder()
        .baseUrl(Constants.BASE_URL) // Set the API base URL.
        // Add converter factory for serialization and deserialization of objects.
        /**
         * A Converter.Factory converter which uses Gson for JSON.
         *
         * Because Gson is so flexible in the types it supports, this converter assumes that it can handle
         * all types.
         */
        .addConverterFactory(GsonConverterFactory.create())
        /**
         * **
         * Add a call adapter factory for supporting service method return types other than.
         *
         * A CallAdapter.Factory call adapter which uses RxJava 3 for creating observables.
         *
         * Adding this class to Retrofit allows you to return an Observable, Flowable, Single, Completable
         * or Maybe from service methods.
         */
        .addCallAdapterFactory(RxJava3CallAdapterFactory.create())
        .build() // Create the Retrofit instance using the configured values.
        // Create an implementation of the API endpoints defined by the service interface in our case it is RandomDishAPI.
        .create(RandomDishAPI::class.java)

    fun getRandomDish(): Single<RandomDish.Recipes> {
        return api.getRandomDish(
            Constants.API_KEY_VALUE,
            Constants.LIMIT_LICENSE_VALUE,
            Constants.TAGS_VALUE,
            Constants.NUMBER_VALUE
        )
    }
}