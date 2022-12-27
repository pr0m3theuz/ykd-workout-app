package com.example.myapplication.components.exercisesList

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.runtime.Composable

@Composable
fun ExercisesScreen() {
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun ExercisesLazyColumn() {
    LazyColumn() {
        stickyHeader { /* TODO Search Function */ }
        items() { /* TODO Search Function */ }
    }
}
