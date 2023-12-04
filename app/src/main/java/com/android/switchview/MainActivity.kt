package com.android.switchview

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import com.jy.switchview.SwitchView

class MainActivity : AppCompatActivity() {

    lateinit var sv:SwitchView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        sv = findViewById(R.id.sv)

        sv.setICheckChanged(object :SwitchView.ICheckChangedListener{
            override fun onCheckChanged(isChecked: Boolean) {
                Log.d("TAG", "onCheckChanged: isChecked:$isChecked")
            }
        })
    }
}