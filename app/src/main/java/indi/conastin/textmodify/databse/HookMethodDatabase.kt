package indi.conastin.textmodify.databse

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase


@Database(entities = [HookMethodEntity::class], version = 1, exportSchema = false)
abstract class HookMethodDatabase : RoomDatabase() {

    abstract fun HookMethodDao(): HookMethodDao

    companion object {
        @Volatile
        private var INSTANCE: HookMethodDatabase? = null

        fun getDatabase(context: Context): HookMethodDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    HookMethodDatabase::class.java,
                    "database.db"
                ).fallbackToDestructiveMigration().build()
                INSTANCE = instance
                instance
            }
        }

    }
}