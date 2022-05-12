package com.example.travelplaner.core.di

import com.example.travelplaner.core.data.AppDataRepository
import com.example.travelplaner.core.data.db.dao.AccommodationDao
import com.example.travelplaner.core.data.db.dao.CityDao
import com.example.travelplaner.core.data.db.dao.FlightDao
import com.example.travelplaner.home.GetHomeItemsUseCase
import com.example.travelplaner.login.domain.repository.LoginRepository
import com.example.travelplaner.login.domain.usecase.CredentialsLoginUseCase
import com.example.travelplaner.login.domain.usecase.CredentialsLoginUseCaseImpl
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object UseCaseModule {

    @Provides
    @Singleton
    fun provideCredentialsLoginUseCase(
        loginRepository: LoginRepository,
        coroutineDispatchers: AppCoroutineDispatchers,
    ) : CredentialsLoginUseCase = CredentialsLoginUseCaseImpl(
        loginRepository = loginRepository,
        dispatchers = coroutineDispatchers
    )

    @Provides
    @Singleton
    fun provideGetHomeItemsUseCase(
        dispatchers: AppCoroutineDispatchers,
        cityDao: CityDao,
        flightDao: FlightDao,
        accommodationDao: AccommodationDao,
        appDataRepository: AppDataRepository
    ) = GetHomeItemsUseCase(dispatchers, cityDao, flightDao, accommodationDao, appDataRepository)
}