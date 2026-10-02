package com.retarget.app.di

import android.content.Context
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import javax.inject.Singleton

/**
 * App-wide bindings. Kept intentionally small until Phase 1 adds the
 * database/rotator/scheduler graph. Dispatchers are injected (never hard-coded)
 * so domain logic stays JVM-unit-testable.
 */
@Module
@InstallIn(SingletonComponent::class)
object AppModule {
    @Provides
    @Singleton
    fun provideApplicationContext(
        @ApplicationContext context: Context,
    ): Context = context

    @Provides
    fun provideIoDispatcher(): CoroutineDispatcher = Dispatchers.IO
}
