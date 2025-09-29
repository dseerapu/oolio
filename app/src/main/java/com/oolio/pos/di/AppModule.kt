package com.oolio.pos.di

import MockPrinterServiceImpl
import android.app.Application
import android.content.Context
import androidx.room.Room
import com.oolio.pos.data.datastore.LastSyncStore
import com.oolio.pos.data.db.POSDatabase
import com.oolio.pos.data.db.UnitOfWork
import com.oolio.pos.data.db.UnitOfWorkImpl
import com.oolio.pos.data.db.datasource.SyncOfflineDataSource
import com.oolio.pos.data.device.DeviceInfoProvider
import com.oolio.pos.data.network.ApiService
import com.oolio.pos.data.network.datasource.SyncRemoteDataSource
import com.oolio.pos.data.print.PrintQueueJobManager
import com.oolio.pos.data.print.PrinterService
import com.oolio.pos.data.repository.SyncRepositoryImpl
import com.oolio.pos.domain.ResolveConflictUseCase
import com.oolio.pos.domain.SyncRepository
import com.oolio.pos.eventbus.EventBus
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
class AppModule {

    @Provides
    @Singleton
    fun provideAppDatabase(application: Application): POSDatabase {
        return Room.databaseBuilder(application.applicationContext,
            POSDatabase::class.java,
            "pos_database")
            .fallbackToDestructiveMigration(false)
            .build()
    }

    @Provides
    @Singleton
    fun providesPrinterService(): PrinterService{
        return MockPrinterServiceImpl()
    }

    @Provides
    @Singleton
    fun provideEventBus(): EventBus {
        return EventBus(scope = CoroutineScope( Dispatchers.Default))
    }

    @Provides
    @Singleton
    fun providesPrintQueueJobManager(
        db: POSDatabase,
                                     printerService: PrinterService,
                                     eventBus: EventBus): PrintQueueJobManager{
        return PrintQueueJobManager(db, printerService,eventBus)
    }

    @Provides
    @Singleton
    fun providesUnitOfWork(db: POSDatabase): UnitOfWork {
        return UnitOfWorkImpl(db)
    }

    @Provides
    @Singleton
    fun providesLastSyncStore(@ApplicationContext context: Context)= LastSyncStore(context)

    @Provides
    @Singleton
    fun providesConflictResolution() = ResolveConflictUseCase()

    @Provides
    @Singleton
    fun providesSyncOfflineDataSource(
        db: POSDatabase
    ) = SyncOfflineDataSource(db)

    @Provides
    @Singleton
    fun providesSyncOnlineDataSource(
        apiService: ApiService
    ) = SyncRemoteDataSource(apiService)

    @Provides
    @Singleton
    fun providesSyncRepository(
        syncOfflineDataSource: SyncOfflineDataSource,
        syncOnlineDataSource: SyncRemoteDataSource,
        conflictUseCase: ResolveConflictUseCase): SyncRepository {
        return SyncRepositoryImpl(syncOfflineDataSource,syncOnlineDataSource,conflictUseCase)
    }

    @Provides
    @Singleton
    fun providesDeviceInfoDatastore(
        @ApplicationContext context: Context
    ) = DeviceInfoProvider(context)

}