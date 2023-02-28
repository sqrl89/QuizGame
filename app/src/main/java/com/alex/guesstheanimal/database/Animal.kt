package com.alex.guesstheanimal.database

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import java.io.Serializable

@Entity(tableName = "animals")
data class Animal(
    @PrimaryKey(autoGenerate = true)
    var id: Int = 0,
    @ColumnInfo(name = "category")
    var category: String,
    @ColumnInfo(name = "name_ru")
    var nameRu: String,
    @ColumnInfo(name = "name_en")
    var nameEn: String,
    @ColumnInfo(name = "image_uri")
    var imageUri: String? = null,
    @ColumnInfo(name = "sound_uri_ru")
    var soundUriRu: String? = null,
    @ColumnInfo(name = "sound_uri_en")
    var soundUriEN: String? = null
) : Serializable