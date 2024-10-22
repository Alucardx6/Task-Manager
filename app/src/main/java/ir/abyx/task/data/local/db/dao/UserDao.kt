package ir.abyx.task.data.local.db.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import ir.abyx.task.data.local.db.DBHelper
import ir.abyx.task.data.remote.model.UserModel

@Dao
interface UserDao {

    @Insert
    fun saveUser(userModel: UserModel): Long

    @get:Query("SELECT * FROM ${DBHelper.DB_TABLE}")
    val getUser: UserModel

    @Update
    fun updateUser(user: UserModel): Int

    @Delete
    fun deleteUser(user: UserModel): Int
}