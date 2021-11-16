package com.holderzone.store.ksptest

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.TextView
import com.holderzone.store.annotation.MyClass
import com.holderzone.store.annotation.findView
import com.holderzone.store.processor.ButterKnife

@MyClass
class MainActivity : AppCompatActivity() {

    @findView(R.id.tv_hello)
    var textView: TextView? = null


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        ButterKnife.bindView(this)
        textView?.setText("bindview")
    }
}