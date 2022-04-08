package com.capstone.nfc.di

import android.app.Application
import android.content.Context
import android.content.Intent
import com.capstone.nfc.Constants.AUTH_INTENT
import com.capstone.nfc.Constants.MAIN_INTENT
import com.capstone.nfc.Constants.SPLASH_INTENT
import com.capstone.nfc.main.MainActivity
import com.capstone.nfc.splash.SplashActivity
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.*

@Module
@InstallIn(SingletonComponent::class)
class AppModule {
    @Provides
    fun provideContext(application: Application): Context {
        return application.applicationContext
    }

    @Provides
    @Named(SPLASH_INTENT)
    fun provideSplashIntent(context: Context): Intent {
        return Intent(context, SplashActivity::class.java)
    }

    @Provides
    @Named(MAIN_INTENT)
    fun provideMainIntent(context: Context): Intent {
        return Intent(context, MainActivity::class.java)
    }
}