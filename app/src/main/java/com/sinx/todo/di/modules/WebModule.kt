package com.sinx.todo.di.modules

import com.sinx.todo.BuildConfig
import com.sinx.todo.api.middleware.WebSocketLogger
import com.sinx.todo.api.middleware.WebSocketMiddleware
import com.sinx.todo.api.ws.SocketClient
import com.sinx.todo.api.ws.WebSocketConnectionBehavior
import com.sinx.todo.di.scopes.ActivityScope
import dagger.Module
import dagger.Provides

@Module
class WebModule {

    @ActivityScope
    @Provides
    fun provideSocketClient(webSocketMiddleware: WebSocketMiddleware): SocketClient {
        val client = SocketClient(BuildConfig.WEB_SOCKET_URL)
        if (BuildConfig.ENABLE_LOG) {
            client.addMiddleware(webSocketMiddleware)
        }
        return client
    }

    @ActivityScope
    @Provides
    fun provideConnectionBehavior(socketClient: SocketClient): WebSocketConnectionBehavior =
        WebSocketConnectionBehavior(socketClient)

    @ActivityScope
    @Provides
    fun provideLoggerWebSocket(): WebSocketMiddleware = WebSocketLogger()
}