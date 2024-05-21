package com.skymonkey.auth.data.di

import com.skymonkey.auth.data.AuthRepositoryImpl
import com.skymonkey.auth.data.EmailPatternValidator
import com.skymonkey.auth.domain.AuthRepository
import com.skymonkey.auth.domain.PatternValidator
import com.skymonkey.auth.domain.UserDataValidator
import org.koin.core.module.dsl.singleOf
import org.koin.dsl.bind
import org.koin.dsl.module

/**
 * Defines Koin dependencies for auth feature.
 * Koin is more flexible when it comes to dynamic feature modules.
 * More simple than dagger2
 */

val authDataModule = module {
    single<PatternValidator> {
        EmailPatternValidator
    }
    singleOf(::UserDataValidator)
    // provides AuthRepositoryImpl, for instances of AuthRepository interface
    singleOf(::AuthRepositoryImpl).bind<AuthRepository>()
}