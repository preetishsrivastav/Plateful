package com.example.plateful.utils

object Constants {

    const val DISH_IMAGE_SOURCE_LOCAL: String = "Local"
    const val DISH_IMAGE_SOURCE_ONLINE: String = "Online"
    const val DISH_DETAILS: String = "DishDetails"

    const val END_POINT:String="recipes/random"
    const val API_KEY:String = "apiKey"
    const val LICENCE:String = "limitLicence"
    const val TAGS:String = "tags"
    const val NUMBER:String = "number"

    const val BASE_URL:String ="https://api.spoonacular.com/"
    const val API_KEY_VALUE:String = "794e82ec4d82491981ff9bc26747b01b"
    const val TAGS_VALUE:String = "vegetarian"
    const val NUMBER_VALUE:Int = 1
    const val LIMIT_LICENCE_VALUE:Boolean = true

    const val NOTIFICATION_ID = "Plateful_notification_id"
    const val NOTIFICATION_NAME = "Plateful"
    const val NOTIFICATION_CHANNEL = "Plateful_channel_01"

    const val ALL_ITEMS: String = "All"
    const val FILTER_SELECTION: String = "FilterSelection"


    fun dishTypes(): ArrayList<String> {
        val list = ArrayList<String>()
        list.add("Breakfast")
        list.add("Lunch")
        list.add("Snacks")
        list.add("Dinner")
        list.add("Salad")
        list.add("Side Dish")
        list.add("Dessert")
        list.add("Other")
        return list
    }

}