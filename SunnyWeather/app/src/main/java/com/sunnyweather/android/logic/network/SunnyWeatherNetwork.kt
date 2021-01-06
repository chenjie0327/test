package com.sunnyweather.android.logic.network

import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException
import kotlin.coroutines.suspendCoroutine
//定义一个统一的网络数据源访问入口，对所有网络请求的API进行封装
object SunnyWeatherNetwork {

    private val weatherService = ServiceCreator.create(WeatherService::class.java)

    suspend fun getDailyWeather(lng: String, lat: String) = weatherService.getDailyWeather(lng, lat).await()

    suspend fun getRealtimeWeather(lng: String, lat: String) = weatherService.getRealtimeWeather(lng, lat).await()

    //创建一个placeService接口的动态代理对象
    private val placeService = ServiceCreator.create(PlaceService::class.java)

    //调用placeService接口中定义的searchPlaces()方法，以发起搜索城市数据请求
    //将searchPlaces()函数声明成挂起函数，searchPlaces()函数在得到await()函数的返回值后会将该数据再返回到上一层
    suspend fun searchPlaces(query: String) = placeService.searchPlaces(query).await()

    //定义了一个await()函数
    private suspend fun <T> Call<T>.await(): T {
        return suspendCoroutine { continuation ->
            enqueue(object : Callback<T> {
                override fun onResponse(call: Call<T>, response: Response<T>) {
                    val body = response.body()
                    if (body != null) continuation.resume(body)
                    else continuation.resumeWithException(RuntimeException("response body is null"))
                }

                override fun onFailure(call: Call<T>, t: Throwable) {
                    continuation.resumeWithException(t)
                }
            })
        }
    }

}