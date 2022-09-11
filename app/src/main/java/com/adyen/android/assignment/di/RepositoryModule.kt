package com.adyen.android.assignment.di

import com.adyen.android.assignment.api.VenueRecommendationsQueryBuilder
import com.adyen.android.assignment.api.VenuesService
import com.adyen.android.assignment.domain.repository.contract.VenueRepository
import com.adyen.android.assignment.domain.repository.impl.VenuesRepositoryImpl
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ViewModelComponent
import dagger.hilt.android.scopes.ViewModelScoped

@Module
@InstallIn(ViewModelComponent::class)
object RepositoryModule {

    @Provides
    @ViewModelScoped
    fun provideVenueRepository(
        VenuesService: VenuesService
    ): VenueRepository = VenuesRepositoryImpl(VenuesService)

    @Provides
    @ViewModelScoped
    fun provideVenueRecommendationsQueryBuilder(): VenueRecommendationsQueryBuilder {
        return VenueRecommendationsQueryBuilder()
    }
}
