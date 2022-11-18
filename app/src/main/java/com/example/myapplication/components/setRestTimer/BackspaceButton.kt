package com.example.myapplication.components.setRestTimer

// import androidx.compose.material3.tokens.ElevatedButtonTokens
// import androidx.compose.material3.tokens.FilledButtonTokens
// import androidx.compose.material3.tokens.FilledTonalButtonTokens
// import androidx.compose.material3.tokens.OutlinedButtonTokens
// import androidx.compose.material3.tokens.TextButtonTokens
import androidx.compose.animation.core.*
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.interaction.*
import androidx.compose.foundation.layout.*
import androidx.compose.material.ripple.rememberRipple
import androidx.compose.material3.*
import androidx.compose.material3.ButtonDefaults.TextButtonContentPadding
import androidx.compose.material3.tokens.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

@OptIn(ExperimentalMaterial3Api::class, ExperimentalFoundationApi::class)
@Composable
fun BackspaceButton(
    onClick: () -> Unit,
    onLongClick: (() -> Unit)? = null,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    shape: Shape = ButtonDefaults.shape,
    colors: BackspaceButtonColors = BackspaceButtonDefaults.buttonColors(),
    elevation: BackspaceButtonElevation? = BackspaceButtonDefaults.buttonElevation(),
    border: BorderStroke? = null,
    contentPadding: PaddingValues = ButtonDefaults.ContentPadding,
    interactionSource: MutableInteractionSource = remember { MutableInteractionSource() },
    content: @Composable RowScope.() -> Unit
) {
    val containerColor = colors.containerColor(enabled).value
    val contentColor = colors.contentColor(enabled).value
    val shadowElevation = elevation?.shadowElevation(enabled, interactionSource)?.value ?: 0.dp
    val tonalElevation = elevation?.tonalElevation(enabled, interactionSource)?.value ?: 0.dp
    Surface(
        modifier = modifier.combinedClickable(
            onClick = onClick,
            onLongClick = onLongClick,
            enabled = enabled,
            role = Role.Button,
            interactionSource = interactionSource,
            indication = rememberRipple()
        ),
//        enabled = enabled,
        shape = shape,
        color = containerColor,
        contentColor = contentColor,
        tonalElevation = tonalElevation,
        shadowElevation = shadowElevation,
        border = border
    ) {
        CompositionLocalProvider(LocalContentColor provides contentColor) {
            ProvideTextStyle(value = MaterialTheme.typography.labelLarge) {
                Row(
                    Modifier
                        .defaultMinSize(
                            minWidth = ButtonDefaults.MinWidth,
                            minHeight = ButtonDefaults.MinHeight
                        )
                        .padding(contentPadding),
                    horizontalArrangement = Arrangement.Center,
                    verticalAlignment = Alignment.CenterVertically,
                    content = content
                )
            }
        }
    }
}

@Immutable
class BackspaceButtonColors internal constructor(
    private val containerColor: Color,
    private val contentColor: Color,
    private val disabledContainerColor: Color,
    private val disabledContentColor: Color
) {
    /**
     * Represents the container color for this button, depending on [enabled].
     *
     * @param enabled whether the button is enabled
     */
    @Composable
    internal fun containerColor(enabled: Boolean): State<Color> {
        return rememberUpdatedState(if (enabled) containerColor else disabledContainerColor)
    }

    /**
     * Represents the content color for this button, depending on [enabled].
     *
     * @param enabled whether the button is enabled
     */
    @Composable
    internal fun contentColor(enabled: Boolean): State<Color> {
        return rememberUpdatedState(if (enabled) contentColor else disabledContentColor)
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other == null || other !is BackspaceButtonColors) return false

        if (containerColor != other.containerColor) return false
        if (contentColor != other.contentColor) return false
        if (disabledContainerColor != other.disabledContainerColor) return false
        if (disabledContentColor != other.disabledContentColor) return false

        return true
    }

    override fun hashCode(): Int {
        var result = containerColor.hashCode()
        result = 31 * result + contentColor.hashCode()
        result = 31 * result + disabledContainerColor.hashCode()
        result = 31 * result + disabledContentColor.hashCode()
        return result
    }
}

object BackspaceButtonDefaults {

    private val ButtonHorizontalPadding = 24.dp
    private val ButtonVerticalPadding = 8.dp

    /**
     * The default content padding used by [Button], [ElevatedButton], [FilledTonalButton], and
     * [OutlinedButton] buttons.
     *
     * - See [TextButtonContentPadding] for content padding used by [TextButton].
     * - See [ButtonWithIconContentPadding] for content padding used by [Button] that contains [Icon].
     */
    val ContentPadding =
        PaddingValues(
            start = ButtonHorizontalPadding,
            top = ButtonVerticalPadding,
            end = ButtonHorizontalPadding,
            bottom = ButtonVerticalPadding
        )

    private val ButtonWithIconHorizontalStartPadding = 16.dp

    /** The default content padding used by [Button] that contains an [Icon]. */
    val ButtonWithIconContentPadding =
        PaddingValues(
            start = ButtonWithIconHorizontalStartPadding,
            top = ButtonVerticalPadding,
            end = ButtonHorizontalPadding,
            bottom = ButtonVerticalPadding
        )

    private val TextButtonHorizontalPadding = 12.dp

    /** The default content padding used by [TextButton] */
    val TextButtonContentPadding =
        PaddingValues(
            start = TextButtonHorizontalPadding,
            top = ContentPadding.calculateTopPadding(),
            end = TextButtonHorizontalPadding,
            bottom = ContentPadding.calculateBottomPadding()
        )

    /**
     * The default min width applied for all buttons. Note that you can override it by applying
     * Modifier.widthIn directly on the button composable.
     */
    val MinWidth = 58.dp

    /**
     * The default min height applied for all buttons. Note that you can override it by applying
     * Modifier.heightIn directly on the button composable.
     */
    val MinHeight = 40.dp

    /** The default size of the icon when used inside any button. */
//    val IconSize = FilledButtonTokens.IconSize

    /**
     * The default size of the spacing between an icon and a text when they used inside any button.
     */
    val IconSpacing = 8.dp

    /** Default shape for a button. */
//    val shape: Shape @Composable get() = FilledButtonTokens.ContainerShape.toShape()

    /** Default shape for an elevated button. */
//    val elevatedShape: Shape @Composable get() = ElevatedButtonTokens.ContainerShape.toShape()

    /** Default shape for a filled tonal button. */
//    val filledTonalShape: Shape @Composable get() = FilledTonalButtonTokens.ContainerShape.toShape()

    /** Default shape for an outlined button. */
//    val outlinedShape: Shape @Composable get() = OutlinedButtonTokens.ContainerShape.toShape()

    /** Default shape for a text button. */
//    val textShape: Shape @Composable get() = TextButtonTokens.ContainerShape.toShape()

    /**
     * Creates a [ButtonColors] that represents the default container and content colors used in a
     * [Button].
     *
     * @param containerColor the container color of this [Button] when enabled.
     * @param contentColor the content color of this [Button] when enabled.
     * @param disabledContainerColor the container color of this [Button] when not enabled.
     * @param disabledContentColor the content color of this [Button] when not enabled.
     */
    @Composable
    fun buttonColors(
        containerColor: Color = MaterialTheme.colorScheme.primary,
        contentColor: Color = MaterialTheme.colorScheme.onPrimary,
        disabledContainerColor: Color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.12f),
        disabledContentColor: Color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.38f)
    ): BackspaceButtonColors = BackspaceButtonColors(
        containerColor = containerColor,
        contentColor = contentColor,
        disabledContainerColor = disabledContainerColor,
        disabledContentColor = disabledContentColor
    )

    /**
     * Creates a [ButtonColors] that represents the default container and content colors used in an
     * [ElevatedButton].
     *
     * @param containerColor the container color of this [ElevatedButton] when enabled
     * @param contentColor the content color of this [ElevatedButton] when enabled
     * @param disabledContainerColor the container color of this [ElevatedButton] when not enabled
     * @param disabledContentColor the content color of this [ElevatedButton] when not enabled
     */
/*    @Composable
    fun elevatedButtonColors(
       containerColor: Color = ElevatedButtonTokens.ContainerColor.toColor(),
       contentColor: Color = ElevatedButtonTokens.LabelTextColor.toColor(),
       disabledContainerColor: Color = ElevatedButtonTokens.DisabledContainerColor
           .toColor()
           .copy(alpha = ElevatedButtonTokens.DisabledContainerOpacity),
       disabledContentColor: Color = ElevatedButtonTokens.DisabledLabelTextColor
           .toColor()
           .copy(alpha = ElevatedButtonTokens.DisabledLabelTextOpacity)
   ): BackspaceButtonColors = BackspaceButtonColors(
       containerColor = containerColor,
       contentColor = contentColor,
       disabledContainerColor = disabledContainerColor,
       disabledContentColor = disabledContentColor
   )

   */
    /**
     * Creates a [ButtonColors] that represents the default container and content colors used in an
     * [FilledTonalButton].
     *
     * @param containerColor the container color of this [FilledTonalButton] when enabled
     * @param contentColor the content color of this [FilledTonalButton] when enabled
     * @param disabledContainerColor the container color of this [FilledTonalButton] when not enabled
     * @param disabledContentColor the content color of this [FilledTonalButton] when not enabled
     *//*
   @Composable
   fun filledTonalButtonColors(
       containerColor: Color = FilledTonalButtonTokens.ContainerColor.toColor(),
       contentColor: Color = FilledTonalButtonTokens.LabelTextColor.toColor(),
       disabledContainerColor: Color = FilledTonalButtonTokens.DisabledContainerColor
           .toColor()
           .copy(alpha = FilledTonalButtonTokens.DisabledContainerOpacity),
       disabledContentColor: Color = FilledTonalButtonTokens.DisabledLabelTextColor
           .toColor()
           .copy(alpha = FilledTonalButtonTokens.DisabledLabelTextOpacity)
   ): BackspaceButtonColors = BackspaceButtonColors(
       containerColor = containerColor,
       contentColor = contentColor,
       disabledContainerColor = disabledContainerColor,
       disabledContentColor = disabledContentColor
   )

   */
    /**
     * Creates a [ButtonColors] that represents the default container and content colors used in an
     * [OutlinedButton].
     *
     * @param containerColor the container color of this [OutlinedButton] when enabled
     * @param contentColor the content color of this [OutlinedButton] when enabled
     * @param disabledContainerColor the container color of this [OutlinedButton] when not enabled
     * @param disabledContentColor the content color of this [OutlinedButton] when not enabled
     *//*
   @Composable
   fun outlinedButtonColors(
       containerColor: Color = Color.Transparent,
       contentColor: Color = OutlinedButtonTokens.LabelTextColor.toColor(),
       disabledContainerColor: Color = Color.Transparent,
       disabledContentColor: Color = OutlinedButtonTokens.DisabledLabelTextColor
           .toColor()
           .copy(alpha = OutlinedButtonTokens.DisabledLabelTextOpacity)
   ): BackspaceButtonColors = BackspaceButtonColors(
       containerColor = containerColor,
       contentColor = contentColor,
       disabledContainerColor = disabledContainerColor,
       disabledContentColor = disabledContentColor
   )

   */
    /**
     * Creates a [ButtonColors] that represents the default container and content colors used in a
     * [TextButton].
     *
     * @param containerColor the container color of this [TextButton] when enabled
     * @param contentColor the content color of this [TextButton] when enabled
     * @param disabledContainerColor the container color of this [TextButton] when not enabled
     * @param disabledContentColor the content color of this [TextButton] when not enabled
     *//*
   @Composable
   fun textButtonColors(
       containerColor: Color = Color.Transparent,
       contentColor: Color = TextButtonTokens.LabelTextColor.toColor(),
       disabledContainerColor: Color = Color.Transparent,
       disabledContentColor: Color = TextButtonTokens.DisabledLabelTextColor
           .toColor()
           .copy(alpha = TextButtonTokens.DisabledLabelTextOpacity)
   ): ButtonColors = ButtonColors(
       containerColor = containerColor,
       contentColor = contentColor,
       disabledContainerColor = disabledContainerColor,
       disabledContentColor = disabledContentColor
   )
*/
    /**
     * Creates a [ButtonElevation] that will animate between the provided values according to the
     * Material specification for a [Button].
     *
     * @param defaultElevation the elevation used when the [Button] is enabled, and has no other
     * [Interaction]s.
     * @param pressedElevation the elevation used when this [Button] is enabled and pressed.
     * @param focusedElevation the elevation used when the [Button] is enabled and focused.
     * @param hoveredElevation the elevation used when the [Button] is enabled and hovered.
     * @param disabledElevation the elevation used when the [Button] is not enabled.
     */
    @Composable
    fun buttonElevation(
        defaultElevation: Dp = 0.0.dp,
        pressedElevation: Dp = 0.0.dp,
        focusedElevation: Dp = 0.0.dp,
        hoveredElevation: Dp = 1.0.dp,
        disabledElevation: Dp = 0.0.dp
    ): BackspaceButtonElevation = BackspaceButtonElevation(
        defaultElevation = defaultElevation,
        pressedElevation = pressedElevation,
        focusedElevation = focusedElevation,
        hoveredElevation = hoveredElevation,
        disabledElevation = disabledElevation
    )

    /**
     * Creates a [ButtonElevation] that will animate between the provided values according to the
     * Material specification for a [ElevatedButton].
     *
     * @param defaultElevation the elevation used when the [ElevatedButton] is enabled, and has no
     * other [Interaction]s.
     * @param pressedElevation the elevation used when this [ElevatedButton] is enabled and pressed.
     * @param focusedElevation the elevation used when the [ElevatedButton] is enabled and focused.
     * @param hoveredElevation the elevation used when the [ElevatedButton] is enabled and hovered.
     * @param disabledElevation the elevation used when the [ElevatedButton] is not enabled.
     */
/*   @Composable
   fun elevatedButtonElevation(
       defaultElevation: Dp = ElevatedButtonTokens.ContainerElevation,
       pressedElevation: Dp = ElevatedButtonTokens.PressedContainerElevation,
       focusedElevation: Dp = ElevatedButtonTokens.FocusContainerElevation,
       hoveredElevation: Dp = ElevatedButtonTokens.HoverContainerElevation,
       disabledElevation: Dp = ElevatedButtonTokens.DisabledContainerElevation
   ): BackspaceButtonElevation = BackspaceButtonElevation(
       defaultElevation = defaultElevation,
       pressedElevation = pressedElevation,
       focusedElevation = focusedElevation,
       hoveredElevation = hoveredElevation,
       disabledElevation = disabledElevation
   )

   */
    /**
     * Creates a [ButtonElevation] that will animate between the provided values according to the
     * Material specification for a [FilledTonalButton].
     *
     * @param defaultElevation the elevation used when the [FilledTonalButton] is enabled, and has no
     * other [Interaction]s.
     * @param pressedElevation the elevation used when this [FilledTonalButton] is enabled and
     * pressed.
     * @param focusedElevation the elevation used when the [FilledTonalButton] is enabled and focused.
     * @param hoveredElevation the elevation used when the [FilledTonalButton] is enabled and hovered.
     * @param disabledElevation the elevation used when the [FilledTonalButton] is not enabled.
     *//*
   @Composable
   fun filledTonalButtonElevation(
       defaultElevation: Dp = FilledTonalButtonTokens.ContainerElevation,
       pressedElevation: Dp = FilledTonalButtonTokens.PressedContainerElevation,
       focusedElevation: Dp = FilledTonalButtonTokens.FocusContainerElevation,
       hoveredElevation: Dp = FilledTonalButtonTokens.HoverContainerElevation,
       disabledElevation: Dp = 0.dp
   ): BackspaceButtonElevation = BackspaceButtonElevation(
       defaultElevation = defaultElevation,
       pressedElevation = pressedElevation,
       focusedElevation = focusedElevation,
       hoveredElevation = hoveredElevation,
       disabledElevation = disabledElevation
   )

   */
    /** The default [BorderStroke] used by [OutlinedButton]. *//*
   val outlinedButtonBorder: BorderStroke
       @Composable
       get() = BorderStroke(
           width = OutlinedButtonTokens.OutlineWidth,
           color = OutlinedButtonTokens.OutlineColor.toColor()
       )*/
}

class BackspaceButtonElevation internal constructor(
    private val defaultElevation: Dp,
    private val pressedElevation: Dp,
    private val focusedElevation: Dp,
    private val hoveredElevation: Dp,
    private val disabledElevation: Dp
) {
    /**
     * Represents the tonal elevation used in a button, depending on its [enabled] state and
     * [interactionSource]. This should typically be the same value as the [shadowElevation].
     *
     * Tonal elevation is used to apply a color shift to the surface to give the it higher emphasis.
     * When surface's color is [ColorScheme.surface], a higher elevation will result in a darker
     * color in light theme and lighter color in dark theme.
     *
     * See [shadowElevation] which controls the elevation of the shadow drawn around the button.
     *
     * @param enabled whether the button is enabled
     * @param interactionSource the [InteractionSource] for this button
     */
    @Composable
    internal fun tonalElevation(enabled: Boolean, interactionSource: InteractionSource): State<Dp> {
        return animateElevation(enabled = enabled, interactionSource = interactionSource)
    }

    /**
     * Represents the shadow elevation used in a button, depending on its [enabled] state and
     * [interactionSource]. This should typically be the same value as the [tonalElevation].
     *
     * Shadow elevation is used to apply a shadow around the button to give it higher emphasis.
     *
     * See [tonalElevation] which controls the elevation with a color shift to the surface.
     *
     * @param enabled whether the button is enabled
     * @param interactionSource the [InteractionSource] for this button
     */
    @Composable
    internal fun shadowElevation(
        enabled: Boolean,
        interactionSource: InteractionSource
    ): State<Dp> {
        return animateElevation(enabled = enabled, interactionSource = interactionSource)
    }

    @Composable
    private fun animateElevation(
        enabled: Boolean,
        interactionSource: InteractionSource
    ): State<Dp> {
        val interactions = remember { mutableStateListOf<Interaction>() }
        LaunchedEffect(interactionSource) {
            interactionSource.interactions.collect { interaction ->
                when (interaction) {
                    is HoverInteraction.Enter -> {
                        interactions.add(interaction)
                    }
                    is HoverInteraction.Exit -> {
                        interactions.remove(interaction.enter)
                    }
                    is FocusInteraction.Focus -> {
                        interactions.add(interaction)
                    }
                    is FocusInteraction.Unfocus -> {
                        interactions.remove(interaction.focus)
                    }
                    is PressInteraction.Press -> {
                        interactions.add(interaction)
                    }
                    is PressInteraction.Release -> {
                        interactions.remove(interaction.press)
                    }
                    is PressInteraction.Cancel -> {
                        interactions.remove(interaction.press)
                    }
                }
            }
        }

        val interaction = interactions.lastOrNull()

        val target =
            if (!enabled) {
                disabledElevation
            } else {
                when (interaction) {
                    is PressInteraction.Press -> pressedElevation
                    is HoverInteraction.Enter -> hoveredElevation
                    is FocusInteraction.Focus -> focusedElevation
                    else -> defaultElevation
                }
            }

        val animatable = remember { Animatable(target, Dp.VectorConverter) }

        if (!enabled) {
            // No transition when moving to a disabled state
            LaunchedEffect(target) { animatable.snapTo(target) }
        } else {
            LaunchedEffect(target) {
                val lastInteraction = when (animatable.targetValue) {
                    pressedElevation -> PressInteraction.Press(Offset.Zero)
                    hoveredElevation -> HoverInteraction.Enter()
                    focusedElevation -> FocusInteraction.Focus()
                    else -> null
                }

                animatable.AnimateElevation(
                    from = lastInteraction,
                    to = interaction,
                    target = target
                )
            }
        }

        return animatable.asState()
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other == null || other !is BackspaceButtonElevation) return false

        if (defaultElevation != other.defaultElevation) return false
        if (pressedElevation != other.pressedElevation) return false
        if (focusedElevation != other.focusedElevation) return false
        if (hoveredElevation != other.hoveredElevation) return false
        if (disabledElevation != other.disabledElevation) return false

        return true
    }

    override fun hashCode(): Int {
        var result = defaultElevation.hashCode()
        result = 31 * result + pressedElevation.hashCode()
        result = 31 * result + focusedElevation.hashCode()
        result = 31 * result + hoveredElevation.hashCode()
        result = 31 * result + disabledElevation.hashCode()
        return result
    }
}

private suspend fun Animatable<Dp, *>.AnimateElevation(
    from: Interaction?,
    to: Interaction?,
    target: Dp
) {
    val spec = when {
        // Moving to a new state
        to != null -> ElevationDefaults.incomingAnimationSpecForInteraction(to)
        // Moving to default, from a previous state
        from != null -> ElevationDefaults.outgoingAnimationSpecForInteraction(from)
        // Loading the initial state, or moving back to the baseline state from a disabled /
        // unknown state, so just snap to the final value.
        else -> null
    }
    if (spec != null) animateTo(target, spec) else snapTo(target)

    /**
     * Contains default [AnimationSpec]s used for animating elevation between different [Interaction]s.
     *
     * Typically you should use [animateElevation] instead, which uses these [AnimationSpec]s
     * internally. [animateElevation] in turn is used by the defaults for cards and buttons.
     *
     * @see animateElevation
     */
}

/**
 * Represents the container and content colors used in a button in different states.
 *
 * - See [ButtonDefaults.buttonColors] for the default colors used in a [Button].
 * - See [ButtonDefaults.elevatedButtonColors] for the default colors used in a [ElevatedButton].
 * - See [ButtonDefaults.textButtonColors] for the default colors used in a [TextButton].
 */

private object ElevationDefaults {
    /**
     * Returns the [AnimationSpec]s used when animating elevation to [interaction], either from a
     * previous [Interaction], or from the default state. If [interaction] is unknown, then
     * returns `null`.
     *
     * @param interaction the [Interaction] that is being animated to
     */
    fun incomingAnimationSpecForInteraction(interaction: Interaction): AnimationSpec<Dp>? {
        return when (interaction) {
            is PressInteraction.Press -> DefaultIncomingSpec
            is DragInteraction.Start -> DefaultIncomingSpec
            is HoverInteraction.Enter -> DefaultIncomingSpec
            is FocusInteraction.Focus -> DefaultIncomingSpec
            else -> null
        }
    }

    /**
     * Returns the [AnimationSpec]s used when animating elevation away from [interaction], to the
     * default state. If [interaction] is unknown, then returns `null`.
     *
     * @param interaction the [Interaction] that is being animated away from
     */
    fun outgoingAnimationSpecForInteraction(interaction: Interaction): AnimationSpec<Dp>? {
        return when (interaction) {
            is PressInteraction.Press -> DefaultOutgoingSpec
            is DragInteraction.Start -> DefaultOutgoingSpec
            is HoverInteraction.Enter -> HoveredOutgoingSpec
            is FocusInteraction.Focus -> DefaultOutgoingSpec
            else -> null
        }
    }
}

private val OutgoingSpecEasing: Easing = CubicBezierEasing(0.40f, 0.00f, 0.60f, 1.00f)

private val DefaultIncomingSpec = TweenSpec<Dp>(
    durationMillis = 120,
    easing = FastOutSlowInEasing
)

private val DefaultOutgoingSpec = TweenSpec<Dp>(
    durationMillis = 150,
    easing = OutgoingSpecEasing
)

private val HoveredOutgoingSpec = TweenSpec<Dp>(
    durationMillis = 120,
    easing = OutgoingSpecEasing
)
