package indi.conastin.textmodify.databse

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "app_method")
data class HookMethodEntity(
    @PrimaryKey
    val id: Int,

    @ColumnInfo(name = "packageName")
    val packageName: String,

    @ColumnInfo(name = "originText")
    val originText: String,

    @ColumnInfo(name = "newText")
    val newText: String
)