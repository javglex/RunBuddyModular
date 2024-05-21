package com.skymonkey.auth.presentation.di

import org.koin.androidx.viewmodel.dsl.viewModelOf
import com.skymonkey.auth.presentation.register.RegisterViewModel
import org.koin.dsl.module

val authViewModelModule = module {
    viewModelOf(::RegisterViewModel)
}