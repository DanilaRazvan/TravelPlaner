package com.example.travelplaner.core.di

import android.content.Context
import com.example.travelplaner.core.data.AppDataRepository
import com.example.travelplaner.core.data.AppDataRepositoryImpl
import com.example.travelplaner.core.data.db.dao.AccommodationDao
import com.example.travelplaner.core.data.db.dao.CityDao
import com.example.travelplaner.core.data.db.dao.FlightDao
import com.example.travelplaner.home.GetHomeItemsUseCase
import com.example.travelplaner.login.domain.repository.DemoLoginRepository
import com.example.travelplaner.login.domain.repository.LoginRepository
import com.example.travelplaner.login.domain.usecase.CredentialsLoginUseCase
import com.example.travelplaner.login.domain.usecase.CredentialsLoginUseCaseImpl
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object RepositoryModule {

    @Provides
    @Singleton
    fun provideLoginRepository() : LoginRepository = DemoLoginRepository()

    @Provides
    @Singleton
    fun provideAppDataRepository(
        dispatchers: AppCoroutineDispatchers,
        @ApplicationContext context: Context
    ) : AppDataRepository = AppDataRepositoryImpl(dispatchers = dispatchers, context = context)
}