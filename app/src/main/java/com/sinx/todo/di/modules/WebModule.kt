package com.sinx.todo.di.modules

import android.util.Log
import com.sinx.todo.BuildConfig
import com.sinx.todo.api.ws.WebSocketConnectionBehavior
import com.sinx.todo.api.ws.SocketClient
import com.sinx.todo.di.scopes.ActivityScope
import dagger.Module
import dagger.Provides

@Module
class WebModule {

    @ActivityScope
    @Provides
    fun provideSocketClient(): SocketClient {
        return if (BuildConfig.ENABLE_LOG) {
            val logMiddleWare: (String, String) -> Unit = { name, eventObject ->
                Log.d("websocket", "$name: $eventObject")
            }
            SocketClient(BuildConfig.WEB_SOCKET_URL, logMiddleWare)
        } else {
            SocketClient(BuildConfig.WEB_SOCKET_URL)
        }
    }

    @ActivityScope
    @Provides
    fun provideConnectionBehavior(socketClient: SocketClient) : WebSocketConnectionBehavior = WebSocketConnectionBehavior(socketClient)
}