package com.karleinstein.basemvvm.di

import androidx.room.Room
import com.karleinstein.basemvvm.database.TodoDatabase
import com.karleinstein.basemvvm.usecase.GetTodoListUseCase
import com.karleinstein.basemvvm.usecase.GetTodoListUseCaseImpl
import org.koin.android.ext.koin.androidContext
import org.koin.dsl.module

val appModule = module {
    single {
        Room.databaseBuilder(
            androidContext(),
            TodoDatabase::class.java,
            "todo-database"
        ).build()
    }

    single { get<TodoDatabase>().todoDao() }

    factory<GetTodoListUseCase> { GetTodoListUseCaseImpl(get()) }
}
