package com.example.plateful.model.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.example.plateful.model.entities.PlatefulModel

@Database(entities = [PlatefulModel::class], version = 1)
abstract class PlatefulDatabase : RoomDatabase() {

    abstract fun platefulDao(): PlatefulDao


    companion object {
        // Singleton prevents multiple instances of database opening at the
        // same time.
        @Volatile
        private var INSTANCE: com.example.plateful.model.database.PlatefulDatabase? = null

        fun getDatabase(context: Context): PlatefulDatabase {
            // if the INSTANCE is not null, then return it,
            // if it is, then create the database
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    PlatefulDatabase::class.java,
                    "plateful_database"
                ).fallbackToDestructiveMigration()
                    .build()
                INSTANCE = instance
                // return instance
                instance
            }
        }
    }


}