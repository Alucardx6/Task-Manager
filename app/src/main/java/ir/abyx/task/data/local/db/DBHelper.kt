package ir.abyx.task.data.local.db

import androidx.room.Room
import androidx.room.RoomDatabase
import ir.abyx.task.data.local.db.dao.UserDao
import ir.abyx.task.data.remote.ext.MyApplication

abstract class DBHelper : RoomDatabase() {
    abstract fun userDao(): UserDao

    companion object {
        private const val DB_NAME = "user.db"

        const val DB_VERSION = 1
        const val DB_TABLE = "user"

        private val INSTANCE: DBHelper by lazy {
            Room.databaseBuilder(
                MyApplication.appContext!!,
                DBHelper::class.java,
                DB_NAME
            )
                .fallbackToDestructiveMigration()
                .build()
        }
    }
}