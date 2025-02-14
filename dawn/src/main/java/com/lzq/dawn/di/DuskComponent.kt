package com.lzq.dawn.di

import dagger.Component
import dagger.hilt.DefineComponent
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Singleton
@Component(modules = [DuskModule::class])
@DefineComponent(parent = SingletonComponent::class)
interface DuskComponent {
    @DefineComponent.Builder
    interface DuskComponentBuilder {
        fun build(): DuskComponent
    }
}
