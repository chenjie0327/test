package com.sunnyweather.android

import android.annotation.SuppressLint
import android.app.Application
import android.content.Context
//给SunnyWeather项目提供一种全局获取Context的方式
// 需要在AndroidManifest.xml文件的<application>标签下指定SunnyWeatherApplication
//可在项目的任何位置调用SunnyWeatherApplication.Context来获取Context对象
class SunnyWeatherApplication : Application() {

    companion object {

        const val TOKEN = "fRNApodcv6bHuGvT" // 填入你申请到的令牌值

        @SuppressLint("StaticFieldLeak")   //忽略警告
        //定义context变量
        lateinit var context: Context
    }

    //重写父类的onCreate()方法
    override fun onCreate() {
        super.onCreate()
        //调用getapplicationContext()方法得到返回值赋值给conext变量
        context = applicationContext
    }

}