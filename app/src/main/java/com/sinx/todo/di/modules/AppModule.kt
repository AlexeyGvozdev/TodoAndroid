package com.sinx.todo.di.modules

import com.sinx.todo.MainActivity
import com.sinx.todo.di.scopes.ActivityScope
import dagger.Module
import dagger.android.ContributesAndroidInjector

@Module
abstract class AppModule {

    @ActivityScope
    @ContributesAndroidInjector(modules = [WebModule::class, FragmentsModule::class, RepositoryModule::class])
    abstract fun contributeMainActivity(): MainActivity

}