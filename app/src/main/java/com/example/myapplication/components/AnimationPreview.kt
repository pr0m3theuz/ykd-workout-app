@file:OptIn(ExperimentalAnimationSpecApi::class)

package com.example.myapplication.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.ExperimentalAnimationSpecApi
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.animateIntAsState
import androidx.compose.animation.core.keyframesWithSpline
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideIn
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.size
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBarItem
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.TransformOrigin
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp

@OptIn(ExperimentalAnimationSpecApi::class)
@Preview(showBackground = true)
@Composable
fun AnimationPreview() {
  NavigationBarItemPreview()
}

@Composable
fun NavigationBarItemPreview() {

  val isSelected: Int by animateIntAsState(0,
    label = "isSelected",
    animationSpec = keyframesWithSpline {
      durationMillis = 1000
      0 at 0
      1 at 250
      1 at 1000
    }
  )

  val animatedColor by animateColorAsState(
    if (isSelected == 1) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.outline,
    label = "colorAnimation",
    animationSpec = tween(500, easing = LinearEasing)
  )

  Row {
    NavigationBarItem(
      icon = { ProfileIcon(true) },
      label = {
        AnimatedVisibility(
          visible = isSelected == 1,
          enter = slideIn(
            animationSpec = spring(
              dampingRatio = Spring.DampingRatioNoBouncy,
              stiffness = Spring.StiffnessLow
            ),
//            tween(450, easing = LinearOutSlowInEasing),
            initialOffset = { IntOffset(0, 100) },
          ) + fadeIn(),
          exit = slideOutVertically() + fadeOut()
        ) {
          Text(
            text = "Profile",
            color = animatedColor,
            style = MaterialTheme.typography.labelSmall
          )
        }
      },
      selected = false, // isSelected,
      onClick = {}
    )
  }

}


@Composable
fun ProfileIcon(isSelected: Boolean) {
//  val scaleY: Int by animateIntAsState(32,
//    animationSpec = keyframesWithSpline {
//      durationMillis = 2000
//      24 at 0
//      32 at 1000
//      24 at 5000
//    }
//  )


  val animatedColor by animateColorAsState(
    if (isSelected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.outline,
    label = "colorAnimation",
    animationSpec = tween(500, easing = LinearEasing)
  )
  val scaleY: Float by animateFloatAsState(1f,
    label = "scaleY",
    animationSpec = keyframesWithSpline {
      durationMillis = 1000
      1f at 0
      1.25f at 500
      1f at 1000
    }
  )

  val translationY: Float by animateFloatAsState(0f,
    label = "translationY",
    animationSpec = keyframesWithSpline {
      durationMillis = 1000
      0f at 0
      2.5f at 500
      0f at 1000
    }
  )

  Icon(
    imageVector = Icons.Filled.Person,
    contentDescription = "person",
    modifier = Modifier
      .size(24.dp)
      .graphicsLayer {
        this.scaleX = 1f
        this.scaleY = scaleY
        this.translationY = translationY
        transformOrigin = TransformOrigin(0.5f, 1f)
      },
    tint = animatedColor
  )
}