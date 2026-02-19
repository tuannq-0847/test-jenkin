package com.karleinstein.basemvvm.constant

import androidx.annotation.DrawableRes
import com.karleinstein.basemvvm.R

sealed class HomeDestination(
    val route: String,
    @DrawableRes val icon: Int,
    @DrawableRes val iconSelected: Int,
    val label: String
) {
    data object Home : HomeDestination("HOME", R.drawable.ic_home, R.drawable.ic_home_selected, "Home")
    data object AddTask : HomeDestination("ADD_TASK", R.drawable.ic_add, R.drawable.ic_add_selected, "Add Task")
    data object Calendar :
        HomeDestination("CALENDAR", R.drawable.ic_calendar, R.drawable.ic_calendar_selected, "Calendar")

}