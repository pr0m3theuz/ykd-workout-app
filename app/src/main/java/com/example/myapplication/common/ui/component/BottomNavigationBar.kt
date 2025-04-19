package com.example.myapplication.common.ui.component

import androidx.annotation.StringRes
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.ExperimentalAnimationSpecApi
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.VisibilityThreshold
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.size
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Assignment
import androidx.compose.material.icons.automirrored.filled.LibraryBooks
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.compose.currentBackStackEntryAsState
import com.example.myapplication.R
import com.example.myapplication.common.ui.icon.ExerciseFilled
import com.example.myapplication.common.ui.icon.MyIconPack

@OptIn(ExperimentalAnimationSpecApi::class)
@Composable
fun BottomNavigationBar(navController: NavController) {
  //    var selectedItem by remember { mutableIntStateOf(0) }
  //    val items = listOf("Profile", "History", "Workout", "Programs", "Exercise", "Settings")

  val items =
    listOf(
      Screens.Profile,
      Screens.History,
      Screens.Workout,
      Screens.Programs,
      Screens.Exercise,
//      Screens.Settings
      Screens.Replicator
    )

  val navBackStackEntry by navController.currentBackStackEntryAsState()
  val currentDestination = navBackStackEntry?.destination
//  Log.i("BottomNavigationBar", "currentDestination: $currentDestination")

  NavigationBar(windowInsets = WindowInsets.navigationBars) {
    items.forEach { item ->
//      Log.i("BottomNavigationBar", "item.route: ${item.route}")
      val isSelected =
        currentDestination?.hierarchy?.any { it.route?.contains(item.baseRoute) ?: false } ==
                true

      val animatedColor by animateColorAsState(
        if (isSelected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.outline,
        label = "colorAnimation",
        animationSpec = tween(500, easing = LinearEasing)
      )

      NavigationBarItem(
        icon = {
          when {
            item.baseRoute == "Profile" -> ProfileIcon(isSelected)
            else ->
              Icon(
                item.icon,
                contentDescription = stringResource(item.resourceId),
                modifier = Modifier
                  .size(24.dp),
                tint = animatedColor
              )
          }
        },
        label = {
          AnimatedVisibility(
            visible = isSelected,
            enter = slideInVertically(
              animationSpec = spring(
                dampingRatio = Spring.DampingRatioNoBouncy,
                stiffness = Spring.StiffnessLow,
                visibilityThreshold = IntOffset.VisibilityThreshold
              ),
              initialOffsetY = { 100 }
            ) + fadeIn(),
            exit = slideOutVertically() + fadeOut()
          ) {
            Text(
              stringResource(item.resourceId),
              color = animatedColor,
              style = MaterialTheme.typography.labelSmall
            )
          }
        },
        selected = false, // isSelected,
        onClick = {
          if (
            currentDestination?.hierarchy?.any { it.route?.contains(item.baseRoute) == true } !=
            true
          ) {
            navController.navigate(item.route) {
              // Pop up to the start destination of the graph to
              // avoid building up a large stack of destinations
              // on the back stack as users select items
              popUpTo(navController.graph.findStartDestination().id) { saveState = true }
              // Avoid multiple copies of the same destination when
              // reselecting the same item
              launchSingleTop = true
              // Restore state when reselecting a previously selected item
              restoreState = true
            }
          }
        }
      )
    }
  }
}

sealed class Screens(
  val route: String,
  val baseRoute: String,
  @StringRes val resourceId: Int,
  val icon: ImageVector
) {
  object Profile : Screens("profile", "profile", R.string.profile, Icons.Filled.Person)

  object History : Screens("history", "history", R.string.history, Icons.Filled.History)

  object Workout :
    Screens(
      "workout",
      "workout",
      R.string.workout,
      Icons.AutoMirrored.Filled.Assignment
    )

  object Programs :
    Screens("programs", "programs", R.string.programs, Icons.AutoMirrored.Filled.LibraryBooks)

  object Exercise :
    Screens("exercises", "exercises", R.string.exercises, MyIconPack.ExerciseFilled)

  object Settings : Screens("settings", "settings", R.string.settings, Icons.Filled.Settings)

  object Replicator :
    Screens("replicator", "replicator", R.string.replicator, Icons.Filled.Settings)
}
