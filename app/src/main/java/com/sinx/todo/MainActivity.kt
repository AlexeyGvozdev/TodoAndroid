package com.sinx.todo

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.sinx.todo.api.ws.WebSocketConnectionBehavior
import com.sinx.todo.di.DaggerFragmentFactory
import dagger.Lazy
import dagger.android.AndroidInjection
import dagger.android.support.DaggerAppCompatActivity
import javax.inject.Inject

class MainActivity : DaggerAppCompatActivity() {

    @Inject
    lateinit var fragmentFactory: DaggerFragmentFactory
    @Inject
    lateinit var connectionBehavior: Lazy<WebSocketConnectionBehavior>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        supportFragmentManager.fragmentFactory = fragmentFactory
        setContentView(R.layout.activity_main)
    }

    override fun onStart() {
        connectionBehavior.get().connect()
        super.onStart()
    }

    override fun onStop() {
        connectionBehavior.get().disconnect()
        super.onStop()
    }
}
