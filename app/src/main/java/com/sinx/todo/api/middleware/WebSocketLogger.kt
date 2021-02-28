package com.sinx.todo.api.middleware

import android.util.Log

class WebSocketLogger : WebSocketMiddleware {
    override fun invoke(nameEvent: String, json: String) {
        Log.d("websocket", "$nameEvent: $json")
    }
}