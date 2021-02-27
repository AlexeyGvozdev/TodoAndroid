package com.sinx.todo.di.modules

import androidx.fragment.app.Fragment
import com.sinx.todo.di.DaggerFragmentFactory
import com.sinx.todo.di.FragmentKey
import com.sinx.todo.di.scopes.ActivityScope
import com.sinx.todo.di.scopes.ApplicationScope
import com.sinx.todo.repository.TableRepository
import com.sinx.todo.ui.table.TableFragment
import com.sinx.todo.ui.table.provideTableTea
import dagger.Module
import dagger.Provides
import dagger.multibindings.IntoMap
import javax.inject.Provider

@Module
class FragmentsModule {

    @ActivityScope
    @Provides
    fun provideDaggerFragmentFactory(creators: Map<Class<out Fragment>, @JvmSuppressWildcards Provider<Fragment>>): DaggerFragmentFactory {
        return DaggerFragmentFactory(creators)
    }

    @Provides
    @IntoMap
    @FragmentKey(value = TableFragment::class)
    fun provideTableFragment(
        tableRepository: TableRepository
    ): Fragment = TableFragment(provideTableTea(tableRepository))
}