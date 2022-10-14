package com.livingtechusa.gotjokes.di

import com.livingtechusa.reflexion.data.localService.ILocalService
import com.livingtechusa.reflexion.data.localService.LocalServiceImpl
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ActivityComponent

@Module
@InstallIn(ActivityComponent::class)
abstract class LocalServiceModule {
    @Binds
    abstract fun providesLocalService(impl: LocalServiceImpl) : ILocalService
}
