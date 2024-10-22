package ir.abyx.task.data.local.db.model

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import ir.abyx.task.data.local.db.DBHelper

@Entity(tableName = DBHelper.DB_TABLE)
data class UserModel(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    @ColumnInfo val username: String = "",
    @ColumnInfo val email: String = "",
    @ColumnInfo val profilePicture: String = ""
)