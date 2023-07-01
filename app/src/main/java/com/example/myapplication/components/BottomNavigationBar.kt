package com.example.myapplication.components

import androidx.annotation.StringRes
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Assignment
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.LibraryBooks
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import androidx.navigation.NavController
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.compose.currentBackStackEntryAsState
import com.example.myapplication.R
import com.example.myapplication.myiconpack.ExerciseFilled
import com.example.myapplication.myiconpack.MyIconPack

@Composable
fun BottomNavigationBar(navController: NavController) {
//    var selectedItem by remember { mutableIntStateOf(0) }
//    val items = listOf("Profile", "History", "Workout", "Programs", "Exercise", "Settings")

    val items = listOf(
        Screens.Profile,
        Screens.History,
        Screens.Workout,
        Screens.Programs,
        Screens.Exercise,
        Screens.Settings
    )

    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentDestination = navBackStackEntry?.destination

    NavigationBar {
        items.forEach { item ->
            NavigationBarItem(
                icon = { Icon(item.icon, contentDescription = stringResource(item.resourceId)) },
                label = { Text(stringResource(item.resourceId)) },
                selected = currentDestination?.hierarchy?.any { it.route == item.route } == true,
                onClick = {
                    navController.navigate(item.route) {
                        // Pop up to the start destination of the graph to
                        // avoid building up a large stack of destinations
                        // on the back stack as users select items
                        popUpTo(navController.graph.findStartDestination().id) {
                            saveState = true
                        }
                        // Avoid multiple copies of the same destination when
                        // reselecting the same item
                        launchSingleTop = true
                        // Restore state when reselecting a previously selected item
                        restoreState = true
                    }
                }
            )
        }
    }
}

sealed class Screens(val route: String, @StringRes val resourceId: Int, val icon: ImageVector) {
    object Profile : Screens("profile", R.string.profile, Icons.Filled.Person)
    object History : Screens("history", R.string.history, Icons.Filled.History)
    object Workout : Screens("workout", R.string.workout, Icons.Filled.Assignment)
    object Programs : Screens("programs", R.string.programs, Icons.Filled.LibraryBooks)
    object Exercise : Screens("exercises", R.string.exercises, MyIconPack.ExerciseFilled)

    object Settings : Screens("settings", R.string.settings, Icons.Filled.Settings)
}
