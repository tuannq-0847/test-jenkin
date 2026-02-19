package com.karleinstein.basemvvm.di

import com.karleinstein.basemvvm.viewmodel.TodoHomeViewModel
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module

val viewModelModule = module {
    viewModel { TodoHomeViewModel(get(), get()) }
}
