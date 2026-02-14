package com.karleinstein.basemvvm

import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.runtime.Composable
import androidx.fragment.app.FragmentActivity
import androidx.navigation.compose.rememberNavController
import com.karleinstein.basemvvm.navigation.TodoNavGraph


class MainActivity : FragmentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            TodoApp()
        }
    }

    @Composable
    fun TodoApp() {
        val navController = rememberNavController()
        TodoNavGraph(navController = navController)
    }
}
