package com.sinx.todo.di.modules

import com.sinx.todo.api.ws.SocketClient
import com.sinx.todo.di.scopes.ActivityScope
import com.sinx.todo.repository.TableRepository
import dagger.Module
import dagger.Provides

@Module
class RepositoryModule {

    @ActivityScope
    @Provides
    fun provideTableRepository(socketClient: SocketClient): TableRepository =
        TableRepository(socketClient)

}