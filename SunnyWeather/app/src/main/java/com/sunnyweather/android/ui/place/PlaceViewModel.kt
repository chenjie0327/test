package com.sunnyweather.android.ui.place

import androidx.lifecycle.*
import com.sunnyweather.android.logic.Repository
import com.sunnyweather.android.logic.model.Place

class PlaceViewModel : ViewModel() {

    private val searchLiveData = MutableLiveData<String>()

    val placeList = ArrayList<Place>()

    //使用Transformations的switchMap()方法来观察searchLiveData对象
    val placeLiveData = Transformations.switchMap(searchLiveData) { query ->
        Repository.searchPlaces(query)
    }

    //没有直接调用searchPlaces()函数，将传入的搜索参数赋值给searchLiveData对象
    fun searchPlaces(query: String) {
        searchLiveData.value = query
    }

    fun savePlace(place: Place) = Repository.savePlace(place)

    fun getSavedPlace() = Repository.getSavedPlace()

    fun isPlaceSaved() = Repository.isPlaceSaved()

    fun getLocalPlace()= Repository.getLocalPlace()

    fun savePreferencePlaces(places: MutableList<Place>) = Repository.savePreferencePlaces(places)

    fun getPreferencePlaces() = Repository.getPreferencePlaces()

    fun isPreferencePlacesSaved() = Repository.isPreferencePlacesSaved()
}