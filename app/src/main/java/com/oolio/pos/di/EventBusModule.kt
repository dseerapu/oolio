package com.oolio.pos.di

import com.oolio.pos.eventbus.EventBus
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
class EventBusModule {

    @Provides
    @Singleton
    fun provideEventBus(): EventBus {
        return EventBus(scope = CoroutineScope( Dispatchers.Default))
    }
}