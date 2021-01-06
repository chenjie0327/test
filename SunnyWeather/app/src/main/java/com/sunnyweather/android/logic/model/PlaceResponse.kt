package com.sunnyweather.android.logic.model

import com.google.gson.annotations.SerializedName

class PlaceResponse(val status: String, val places: List<Place>)

//使用@SerializedName注解的方式，来让JSON字段和Kotlin字段之间建立映射关系
class Place(val name: String, val location: Location, @SerializedName("formatted_address") val address: String)

class Location(val lng: String, val lat: String)