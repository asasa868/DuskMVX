package com.lzq.dawn.di

import android.content.Context
import android.webkit.WebView
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import java.lang.ref.WeakReference


@Module
@InstallIn(SingletonComponent::class)
class DuskModule {
    private var mWebViewRef: WeakReference<WebView>? = null



    @Provides
    fun provideWebView(@ApplicationContext context: Context): WebView {
        return mWebViewRef?.get() ?: WebView(context).apply {
            mWebViewRef = WeakReference(this)
        }
    }

}