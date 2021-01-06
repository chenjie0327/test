package com.sunnyweather.android.logic

import androidx.lifecycle.liveData
import com.sunnyweather.android.logic.dao.PlaceDao
import com.sunnyweather.android.logic.model.Place
import com.sunnyweather.android.logic.model.Weather
import com.sunnyweather.android.logic.network.SunnyWeatherNetwork
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.coroutineScope
import kotlin.coroutines.CoroutineContext
//新建Repository单例类，作为仓库层的统一封装入口
object Repository {

    fun searchPlaces(query: String) = fire(Dispatchers.IO) {
        //调用SunnyWeatherNetwork的searchPlaces()函数搜索城市数据
        val placeResponse = SunnyWeatherNetwork.searchPlaces(query)
        //如果服务器响应的状态是ok
        if (placeResponse.status == "ok") {
            val places = placeResponse.places
            Result.success(places)   //使用Kotlin内置的Result.success()方法包装获取的城市数据列表
        } else {  //否则使用Result.failure()方法来包装一个异常信息
            Result.failure(RuntimeException("response status is ${placeResponse.status}"))
        }
    }

    //refreshWeather()方法来刷新天气信息
    fun refreshWeather(lng: String, lat: String, placeName: String) = fire(Dispatchers.IO) {
        coroutineScope {
            val deferredRealtime = async {
                SunnyWeatherNetwork.getRealtimeWeather(lng, lat)
            }
            val deferredDaily = async {
                SunnyWeatherNetwork.getDailyWeather(lng, lat)
            }
            val realtimeResponse = deferredRealtime.await()
            val dailyResponse = deferredDaily.await()
            if (realtimeResponse.status == "ok" && dailyResponse.status == "ok") {
                val weather = Weather(realtimeResponse.result.realtime, dailyResponse.result.daily)
                Result.success(weather)
            } else {
                Result.failure(
                    RuntimeException(
                        "realtime response status is ${realtimeResponse.status}" +
                                "daily response status is ${dailyResponse.status}"
                    )
                )
            }
        }
    }

    fun savePlace(place: Place) = PlaceDao.savePlace(place)

    fun getSavedPlace() = PlaceDao.getSavedPlace()

    fun isPlaceSaved() = PlaceDao.isPlaceSaved()

    //获取当前地址
    fun getLocalPlace()=PlaceDao.getLocalPlace()

    //存储多个城市天气
    fun getPreferencePlaces() = PlaceDao.getPreferencePlaces()

    fun savePreferencePlaces(places: MutableList<Place>) = PlaceDao.savePreferencePlaces(places)

    fun isPreferencePlacesSaved() = PlaceDao.isSavePreferencePlaces()

    //核心fire()函数
    private fun <T> fire(context: CoroutineContext, block: suspend () -> Result<T>) =
        liveData<Result<T>>(context) {  //l可在iveData()函数中调用任意的挂起函数
            val result = try {
                block()
            } catch (e: Exception) {
                Result.failure<T>(e)
            }
            //使用emit()方法将包装的结果发射出去
            emit(result)
        }

}