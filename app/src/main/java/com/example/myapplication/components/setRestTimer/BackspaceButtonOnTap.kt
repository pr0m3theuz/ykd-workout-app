package com.example.myapplication.components.setRestTimer

// import androidx.compose.material3.tokens.ElevatedButtonTokens
// import androidx.compose.material3.tokens.FilledButtonTokens
// import androidx.compose.material3.tokens.FilledTonalButtonTokens
// import androidx.compose.material3.tokens.OutlinedButtonTokens
// import androidx.compose.material3.tokens.TextButtonTokens
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateIntAsState
import androidx.compose.animation.core.spring
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.ripple
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.LocalContentColor
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ProvideTextStyle
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.unit.dp

@OptIn(ExperimentalMaterial3Api::class, ExperimentalFoundationApi::class)
@Composable
fun BackspaceButtonOnTap(
  onClick: () -> Unit,
  onLongClick: (() -> Unit)? = null,
  modifier: Modifier = Modifier,
  enabled: Boolean = true,
  //    shape: Shape = ButtonDefaults.shape,
  colors: BackspaceButtonColors = BackspaceButtonDefaults.buttonColors(),
  elevation: BackspaceButtonElevation? = BackspaceButtonDefaults.buttonElevation(),
  border: BorderStroke? = null,
  contentPadding: PaddingValues = ButtonDefaults.ContentPadding,
  interactionSource: MutableInteractionSource = remember { MutableInteractionSource() },
  content: @Composable RowScope.() -> Unit,
) {
  val containerColor = colors.containerColor(enabled).value
  val contentColor = colors.contentColor(enabled).value
  val shadowElevation = elevation?.shadowElevation(enabled, interactionSource)?.value ?: 0.dp
  val tonalElevation = elevation?.tonalElevation(enabled, interactionSource)?.value ?: 0.dp
  val isPressed = interactionSource.collectIsPressedAsState().value
  val radius = if (isPressed) 25 else 100

  val cornerRadius =
    animateIntAsState(
      targetValue = radius,
      animationSpec =
        spring(dampingRatio = Spring.DampingRatioNoBouncy, stiffness = Spring.StiffnessHigh),
    )

  Surface(
    modifier =
      modifier.combinedClickable(
        onClick = onClick,
        onLongClick = onLongClick,
        enabled = enabled,
        role = Role.Button,
        interactionSource = interactionSource,
        indication = ripple(),
      ),
    //        enabled = enabled,
    shape = RoundedCornerShape(cornerRadius.value),
    color = containerColor,
    contentColor = contentColor,
    tonalElevation = tonalElevation,
    shadowElevation = shadowElevation,
    border = border,
  ) {
    CompositionLocalProvider(LocalContentColor provides contentColor) {
      ProvideTextStyle(value = MaterialTheme.typography.labelLarge) {
        Row(
          Modifier.defaultMinSize(
              minWidth = ButtonDefaults.MinWidth,
              minHeight = ButtonDefaults.MinHeight,
            )
            .padding(contentPadding),
          horizontalArrangement = Arrangement.Center,
          verticalAlignment = Alignment.CenterVertically,
          content = content,
        )
      }
    }
  }
}
