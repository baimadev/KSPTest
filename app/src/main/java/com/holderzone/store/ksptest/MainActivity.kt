package com.holderzone.store.ksptest

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import com.holderzone.store.annotation.Route
import com.zlingsmart.xrouter.XRouter

@Route(route = "Main")
class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        findViewById<Button>(R.id.bt_jump).setOnClickListener {
            XRouter.navigation("Second")

        }
    }
}