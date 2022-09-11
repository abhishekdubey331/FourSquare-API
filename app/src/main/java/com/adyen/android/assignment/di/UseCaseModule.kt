package com.adyen.android.assignment.di

import android.content.Context
import com.adyen.android.assignment.domain.repository.contract.VenueRepository
import com.adyen.android.assignment.domain.usecase.contract.FetchLocationUseCase
import com.adyen.android.assignment.domain.usecase.contract.GetVenuesUseCase
import com.adyen.android.assignment.domain.usecase.impl.FetchLocationUseCaseImpl
import com.adyen.android.assignment.domain.usecase.impl.GetVenuesUseCaseImpl
import com.adyen.android.assignment.utils.StringUtils
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ViewModelComponent
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.CoroutineDispatcher

@InstallIn(ViewModelComponent::class)
@Module
object UseCaseModule {

    @Provides
    fun getVenuesUseCase(
        VenueRepository: VenueRepository,
        stringUtils: StringUtils,
        @IoDispatcher coroutineDispatcher: CoroutineDispatcher
    ): GetVenuesUseCase = GetVenuesUseCaseImpl(
        VenueRepository, stringUtils, coroutineDispatcher
    )

    @Provides
    fun getFetchLocationUseCase(
        @ApplicationContext context: Context,
        @IoDispatcher coroutineDispatcher: CoroutineDispatcher
    ): FetchLocationUseCase = FetchLocationUseCaseImpl(
        context,
        coroutineDispatcher
    )
}
