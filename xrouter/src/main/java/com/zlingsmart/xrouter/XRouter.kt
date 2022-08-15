package com.zlingsmart.xrouter

import android.annotation.SuppressLint
import android.app.Application
import android.content.Context
import android.content.Intent
import android.content.Intent.FLAG_ACTIVITY_NEW_TASK
import android.util.Log
import androidx.core.app.ActivityCompat

@SuppressLint("StaticFieldLeak")
object XRouter {
    val hashMap = HashMap<String,Class<Any>>()

    var mContext:Context? = null

    fun init(context: Application){
        mContext = context
        val claName = "com.example.xrouter.XRouterPathCollector"
        val clazz = Class.forName(claName)
        val ob = clazz.newInstance()
        val collectMethod = clazz.getMethod("loadInfo", hashMap.javaClass)
        collectMethod.invoke(ob, hashMap)
    }

    public fun navigation(route:String) {
        if (!hashMap.containsKey(route)) return;
        val intent = Intent(mContext, hashMap.get(route))
        Log.d("xia","jump")
        Log.d("xia",intent.toString())
        intent.addFlags(FLAG_ACTIVITY_NEW_TASK)
        ActivityCompat.startActivity(mContext!!, intent,null )
    }

}

