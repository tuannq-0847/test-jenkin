package com.karleinstein.basemvvm.model

import java.io.Serializable

data class Todo(
    val id: Long,
    val title: String,
    val isDone: Boolean = false,
) : Serializable
