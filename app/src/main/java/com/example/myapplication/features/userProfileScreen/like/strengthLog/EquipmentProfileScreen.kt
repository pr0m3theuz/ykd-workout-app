

package com.example.myapplication.features.userProfileScreen.like.strengthLog

import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.RemoveCircle
import androidx.compose.material3.Button
import androidx.compose.material3.Checkbox
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.TileMode
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.core.graphics.toColorInt
import com.example.myapplication.common.ui.theme.IBMColorBlindPalette
import com.example.myapplication.common.ui.theme.MyApplicationTheme
import com.example.myapplication.features.userProfileScreen.ProfileViewModel
import com.example.myapplication.common.domain.model.Barbell as DomainBarbell
import com.example.myapplication.common.domain.model.Collar as DomainCollar
import com.example.myapplication.common.domain.model.Plate as DomainPlate

// Remove java.util.UUID import if no longer needed locally after domain model adoption
// import java.util.UUID

// Helper function to convert hex string to Color
fun String.toColor(): Color {
  return try {
    // Allow both #RRGGBBAA and RRGGBBAA
    val colorString = if (this.startsWith("#")) this else "#$this"
    // Handle common hex formats, default alpha to FF if not present
    when (colorString.length) {
      7 -> Color(colorString.replace("#", "#FF").toColorInt()) // #RGB -> #RRGGBB
      9 -> Color(colorString.toColorInt()) // #AARRGGBB
      else -> Color.Gray // Fallback for unexpected formats
    }
  } catch (e: IllegalArgumentException) {
    Color.Gray // Default color on parsing error
  }
}

// Helper function to convert Color to hex string (ARGB)
fun Color.toHexString(): String {
  return String.format(
    "#%02X%02X%02X%02X",
    (this.alpha * 255).toInt(),
    (this.red * 255).toInt(),
    (this.green * 255).toInt(),
    (this.blue * 255).toInt(),
  )
}

// Helper function to convert Color to hex string (ARGB)
fun Color.toRGBAString(): String {
  return String.format(
    "#%02X%02X%02X%02X",
    (this.red * 255).toInt(),
    (this.green * 255).toInt(),
    (this.blue * 255).toInt(),
    (this.alpha * 255).toInt(),
  )
}


// --- Predefined Colors for Plates (example) ---
val plateUiColors =
  listOf(
    Color.Red,
    Color.hsl(224.9f, 1f, 0.5f), // Blue
    Color.Yellow,
    Color.hsl(130f, 0.76f, 0.31f), // Green
    Color.White, // Often used for smaller plates, with black text
  )

// --- Main Screen Composable ---
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EquipmentProfileScreen(
  profileViewModel: ProfileViewModel, // Added ViewModel parameter
  bottomNavigationBar: @Composable () -> Unit,
  onNavigateBack: () -> Unit,
  onEditProfile: () -> Unit = {}, // Keep these for now, might be wired to VM later
  onDeleteProfile: () -> Unit = {}, // Keep these for now
) {
  var selectedBottomNavItem by remember {
    mutableIntStateOf(4)
  } // Assuming settings/profile is last

  val userProfileState by profileViewModel.userProfile.collectAsState()
  val platesList by profileViewModel.plates.collectAsState()
  val barbellsList by profileViewModel.barbells.collectAsState()
  val collarsList by profileViewModel.collars.collectAsState()

//  val currentProfileName = userProfileState?.name ?: "User"
  val currentUnitSystem = userProfileState.defaultWeightUnit.abbreviation

  // --- Dialog States ---
  var showAddPlateDialog by remember { mutableStateOf(false) }
  var showAddBarbellDialog by remember { mutableStateOf(false) }
  var showAddCollarDialog by remember { mutableStateOf(false) }

  Scaffold(
    topBar = {
      TopAppBar(
        title = { Text("Profile: $currentUnitSystem") }, // Use ViewModel state
        navigationIcon = {
          IconButton(onClick = onNavigateBack) { Icon(Icons.AutoMirrored.Filled.ArrowBack, "Back") }
        },
        actions = {
          IconButton(onClick = onEditProfile) { Icon(Icons.Filled.Edit, "Edit Profile") }
          IconButton(onClick = onDeleteProfile) {
            Icon(Icons.Filled.Delete, "Delete Profile", tint = MaterialTheme.colorScheme.error)
          }
        },
      )
    },
    bottomBar = {
      bottomNavigationBar()
      // Assuming BottomNavItem data class is available or defined elsewhere
    },
  ) { paddingValues ->
    LazyColumn(
      modifier = Modifier
        .fillMaxSize()
        .padding(paddingValues),
      contentPadding = PaddingValues(bottom = 16.dp),
    ) {
      // --- Your Plates Section ---
      item { SectionHeader("Your plates") }
      items(platesList) { plate -> // Use DomainPlate from ViewModel
        PlateItemRow(
          plate = plate,
          unit = currentUnitSystem,
          onRemove = { profileViewModel.removePlate(plate.id) },
        )
        HorizontalDivider()
      }
      item { AddItemRow(text = "Add plate", onClick = { showAddPlateDialog = true }) }

      // --- Your Barbells Section ---
      item { SectionHeader("Your barbells") }
      items(barbellsList) { barbell -> // Use DomainBarbell from ViewModel
        BarbellItemRow(
          barbell = barbell,
          unit = currentUnitSystem,
          onRemove = { profileViewModel.removeBarbell(barbell.id) },
        )
        HorizontalDivider()
      }
      item { AddItemRow(text = "Add barbell", onClick = { showAddBarbellDialog = true }) }

      // --- Your Collars Section ---
      item { SectionHeader("Your collars") }
      items(collarsList) { collar -> // Use DomainCollar from ViewModel
        CollarItemRow(
          collar = collar,
          unit = currentUnitSystem,
          onRemove = { profileViewModel.removeCollar(collar.id) },
        )
        HorizontalDivider()
      }
      item { AddItemRow(text = "Add collar", onClick = { showAddCollarDialog = true }) }
    }
  }

  // --- Dialogs ---
  if (showAddPlateDialog) {
    AddPlateDialog(
      unit = currentUnitSystem, // Use ViewModel state
      onDismiss = { showAddPlateDialog = false },
      onAddPlate = { weight, count, uiColor -> // uiColor is Jetpack Compose Color
        // Convert uiColor to hex string for domain model
        val colorHexString = uiColor.toHexString()
        profileViewModel.addPlate(
          DomainPlate(
            weight = weight,
            count = count,
            color = colorHexString,
            unit = currentUnitSystem,
          )
        )
        showAddPlateDialog = false
      },
    )
  }
  if (showAddBarbellDialog) {
    AddBarbellDialog(
      unit = currentUnitSystem, // Use ViewModel state
      onDismiss = { showAddBarbellDialog = false },
      onAddBarbell = { weight ->
        profileViewModel.addBarbell(DomainBarbell(weight = weight, unit = currentUnitSystem))
        showAddBarbellDialog = false
      },
    )
  }
  if (showAddCollarDialog) {
    AddCollarDialog(
      unit = currentUnitSystem, // Use ViewModel state
      onDismiss = { showAddCollarDialog = false },
      onAddCollar = { weight ->
        profileViewModel.addCollar(DomainCollar(weight = weight, unit = currentUnitSystem))
        showAddCollarDialog = false
      },
    )
  }
}

// --- UI Components for List Items & Sections ---
@Composable
private fun SectionHeader(title: String) {
  Text(
    text = title,
    style = MaterialTheme.typography.titleMedium,
    color = MaterialTheme.colorScheme.onSurface,
    modifier =
      Modifier
        .fillMaxWidth()
        .padding(start = 16.dp, end = 16.dp, bottom = 8.dp, top = 16.dp),
  )
}

@Composable
private fun PlateItemRow(
  plate: DomainPlate,
  unit: String,
  onRemove: () -> Unit,
) { // Use DomainPlate
  EquipmentItemBaseRow(
    leadingContent = {
      Box(
        modifier =
          Modifier
            .size(24.dp)
            .clip(CircleShape)
            .background(plate.color.toColor()) // Convert hex string to Color
            .border(2.dp, MaterialTheme.colorScheme.onSurface, CircleShape)
      )
    },
    text = "${plate.weight} $unit x ${plate.displayCount}",
    onRemove = onRemove,
    showRemoveIcon = true,
  )
}

@Composable
fun BarbellItemRow(
  barbell: DomainBarbell,
  unit: String,
  onRemove: () -> Unit,
) { // Use DomainBarbell
  EquipmentItemBaseRow(text = "${barbell.weight} $unit", onRemove = onRemove, showRemoveIcon = true)
}

@Composable
private fun CollarItemRow(
  collar: DomainCollar,
  unit: String,
  onRemove: () -> Unit,
) { // Use DomainCollar
  EquipmentItemBaseRow(
    text = "${collar.weight} $unit",
    onRemove = onRemove,
    showRemoveIcon = true, // Screenshot doesn't show 'x' for collars, but could be added
  )
}

@Composable
fun AddItemRow(text: String, onClick: () -> Unit) {
  Row(
    modifier =
      Modifier
        .fillMaxWidth()
        .clickable(onClick = onClick)
        //      .background(MaterialTheme.colorScheme.surfaceVariant)
        .padding(horizontal = 16.dp, vertical = 16.dp),
    verticalAlignment = Alignment.CenterVertically,
    horizontalArrangement = Arrangement.Center,
  ) {
    TextButton(onClick = onClick, modifier = Modifier.align(Alignment.CenterVertically)) {
      Icon(
        Icons.Filled.Add,
        contentDescription = text,
        //        tint = MaterialTheme.colorScheme.primary,
      )
      Text(
        text,
        //        color = MaterialTheme.colorScheme.primary,
        style = MaterialTheme.typography.titleMedium,
      )
    }
  }
}

@Composable
private fun EquipmentItemBaseRow(
  leadingContent: (@Composable () -> Unit)? = null,
  text: String,
  onRemove: () -> Unit,
  showRemoveIcon: Boolean,
) {
  Row(
    modifier =
      Modifier
        .fillMaxWidth()
        //      .background(MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.3f))
        .padding(horizontal = 16.dp, vertical = 12.dp),
    verticalAlignment = Alignment.CenterVertically,
  ) {
    leadingContent?.invoke()
    if (leadingContent != null) Spacer(modifier = Modifier.width(16.dp))
    //    else Spacer(modifier = Modifier.width(16.dp + 24.dp)) // Indent if no leading content

    Text(
      text,
      style = MaterialTheme.typography.bodyLarge,
      color = MaterialTheme.colorScheme.onSurface,
      modifier = Modifier.weight(1f),
    )

    if (showRemoveIcon) {
      IconButton(onClick = onRemove, modifier = Modifier.size(36.dp)) {
        Icon(Icons.Filled.RemoveCircle, "Remove item", tint = MaterialTheme.colorScheme.error)
      }
    }
  }
}

// --- Dialog Composables ---
@Composable
private fun AddPlateDialog(
  unit: String,
  onDismiss: () -> Unit,
  onAddPlate: (weight: Float, count: Int?, color: Color) -> Unit,
) {
  var weightStr by remember { mutableStateOf("") }
  var amountStr by remember { mutableStateOf("") }
  var isUnlimited by remember { mutableStateOf(true) }
  var selectedColorIndex by remember { mutableIntStateOf(0) } // Default to first color

  val colorStops =
    arrayOf(
      0.0f to IBMColorBlindPalette.colors[0],
      0.5f to IBMColorBlindPalette.colors[2],
      1.0f to IBMColorBlindPalette.colors[4],
    )

  val currentFontSizePx =
    with(LocalDensity.current) { MaterialTheme.typography.bodyMedium.fontSize.toPx() }
  val currentFontSizeDoublePx = currentFontSizePx * 2
  val infiniteTransitionRotation = rememberInfiniteTransition()

  val rotation by
  infiniteTransitionRotation.animateFloat(
    initialValue = 0f,
    targetValue = 360f,
    animationSpec =
      infiniteRepeatable(
        animation = tween(10000, easing = LinearEasing)
        //  repeatMode = RepeatMode.Reverse,
      ),
    label = "RotatingBorderAnimation",
  )

  DialogBase(
    title = "Add plate",
    onDismiss = onDismiss,
    onConfirm = {
      val weight = weightStr.toFloatOrNull()
      val count = if (isUnlimited) null else amountStr.toIntOrNull()
      if (weight != null && (isUnlimited || count != null)) {
        onAddPlate(weight, count, plateUiColors[selectedColorIndex]) // Pass Compose Color
      }
    },
  ) {
    OutlinedTextField(
      value = weightStr,
      onValueChange = { weightStr = it.filter { char -> char.isDigit() || char == '.' } },
      label = { Text("Weight") },
      trailingIcon = { Text(unit, style = MaterialTheme.typography.bodySmall) },
      keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
      singleLine = true,
      modifier = Modifier.fillMaxWidth(),
    )
    Spacer(modifier = Modifier.height(8.dp))
    OutlinedTextField(
      value = amountStr,
      onValueChange = { amountStr = it.filter { char -> char.isDigit() } },
      label = { Text("Total number of plates") },
      enabled = !isUnlimited,
      keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
      singleLine = true,
      modifier = Modifier.fillMaxWidth(),
    )
    Row(
      verticalAlignment = Alignment.CenterVertically,
      modifier = Modifier.clickable { isUnlimited = !isUnlimited },
    ) {
      Checkbox(checked = isUnlimited, onCheckedChange = { isUnlimited = it })
      Text("Unlimited", style = MaterialTheme.typography.bodyMedium)
    }
    Spacer(modifier = Modifier.height(16.dp))
    Text("Color:", style = MaterialTheme.typography.bodyLarge)
    Spacer(modifier = Modifier.height(8.dp))

    //  val colors =
    // IBMColorBlindPalette.colors.slice(2..4).plus(Color.White).plus(IBMColorBlindPalette.colors[2])
    //  val colors = listOf(MaterialTheme.colorScheme.secondary, Color.White,
    // MaterialTheme.colorScheme.secondary)

    Row(
      Modifier
        .fillMaxWidth()
        .height(40.dp),
      horizontalArrangement = Arrangement.spacedBy(16.dp, Alignment.CenterHorizontally),
      verticalAlignment = Alignment.CenterVertically,
    ) {
      val colorGradient = IBMColorBlindPalette.colors
      plateUiColors.forEachIndexed { index, color -> // Use plateUiColors
        val colors =
          colorGradient.plus(
            Color.White
          ) // .plus(IBMColorBlindPalette.colors.reversed()).toMutableList()
        //        repeat(1) {colors.addAll(colors)}
        val sweepBrush = Brush.sweepGradient(colors)
        val infiniteTransition = rememberInfiniteTransition()
        val offset by
        infiniteTransition.animateFloat(
          initialValue = 0f,
          targetValue = currentFontSizeDoublePx,
          animationSpec = infiniteRepeatable(tween(2000, easing = LinearEasing)),
        )

        val linearBrush =
          Brush.linearGradient(
            colors,
            start = Offset(offset, offset),
            end = Offset(offset + currentFontSizePx, offset + currentFontSizePx),
            tileMode = TileMode.Mirror,
          )

        val animatedSize by
        animateDpAsState(
          targetValue = if (selectedColorIndex == index) 40.dp else 36.dp,
          label = "plateColorSizeAnimation",
        )
        Box(
          contentAlignment = Alignment.Center,
          // TODO Animate size from 36.dp to 40.dp
          modifier = Modifier
            .size(40.dp)
            .clickable { selectedColorIndex = index }
            .clip(CircleShape),
        ) {
          if (selectedColorIndex == index) {
            Canvas(
              Modifier
                .size(animatedSize)
                .rotate(rotation)
//                .blur(
//                radiusX = 4.dp,
//                radiusY = 4.dp,
//                edgeTreatment = BlurredEdgeTreatment.Unbounded
//              ) // rotation
            ) {
              drawCircle(
                brush = linearBrush,
                radius = size.minDimension / 2f,
                center = center,
              )
            }
          } else {
            Box(
              Modifier
                .size(36.dp)
                .clip(CircleShape)
                .background(
                  Color.LightGray
                  //                  MaterialTheme.colorScheme.surfaceTint
                ) // Fallback background if not selected
            )
          }
          val alpha = if (selectedColorIndex != index) 1f else 1f
          Box(Modifier
            .size(32.dp)
            .clip(CircleShape)
            .background(color.copy(alpha = alpha)))
        }
      }
    }
  }
}

@Composable
private fun AddBarbellDialog(
  unit: String,
  onDismiss: () -> Unit,
  onAddBarbell: (weight: Float) -> Unit, // DomainBarbell will be created in EquipmentProfileScreen
) {
  var weightStr by remember { mutableStateOf("") }
  DialogBase(
    title = "Add barbell",
    onDismiss = onDismiss,
    onConfirm = { weightStr.toFloatOrNull()?.let { onAddBarbell(it) } },
  ) {
    OutlinedTextField(
      value = weightStr,
      onValueChange = { weightStr = it.filter { char -> char.isDigit() || char == '.' } },
      label = { Text("Weight") },
      trailingIcon = { Text(unit, style = MaterialTheme.typography.bodySmall) },
      keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
      singleLine = true,
      modifier = Modifier.fillMaxWidth(),
    )
  }
}

@Composable
private fun AddCollarDialog(
  unit: String,
  onDismiss: () -> Unit,
  onAddCollar: (weight: Float) -> Unit, // DomainCollar will be created in EquipmentProfileScreen
) {
  var weightStr by remember { mutableStateOf("") }
  DialogBase(
    title = "Add collar",
    onDismiss = onDismiss,
    onConfirm = { weightStr.toFloatOrNull()?.let { onAddCollar(it) } },
  ) {
    OutlinedTextField(
      value = weightStr,
      onValueChange = { weightStr = it.filter { char -> char.isDigit() || char == '.' } },
      label = { Text("Weight") },
      trailingIcon = { Text(unit, style = MaterialTheme.typography.bodySmall) },
      keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
      singleLine = true,
      modifier = Modifier.fillMaxWidth(),
    )
  }
}

@Composable
private fun DialogBase(
  title: String,
  onDismiss: () -> Unit,
  onConfirm: () -> Unit,
  content: @Composable ColumnScope.() -> Unit,
) {
  Dialog(onDismissRequest = onDismiss) {
    Surface(
      shape = RoundedCornerShape(16.dp),
      color = MaterialTheme.colorScheme.surfaceVariant,
      tonalElevation = 8.dp,
    ) {
      Column(modifier = Modifier.padding(16.dp)) {
        Text(
          title,
          style = MaterialTheme.typography.titleLarge,
          fontWeight = FontWeight.Bold,
          color = MaterialTheme.colorScheme.onSurfaceVariant,
        )
        Spacer(modifier = Modifier.height(16.dp))
        content()
        Spacer(modifier = Modifier.height(24.dp))
        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.End) {
          TextButton(onClick = onDismiss) { Text("Cancel") }
          Spacer(modifier = Modifier.width(8.dp))
          Button(onClick = onConfirm) { Text("Add") } // Or "Save" / "Confirm"
        }
      }
    }
  }
}

// --- Preview ---
// Previews would need a mock ViewModel or be adapted.
// For now, commenting out or ensuring they don't break compilation if viewModel is mandatory.
/*
@Preview(showBackground = true, uiMode = Configuration.UI_MODE_NIGHT_YES)
@Composable
private fun EquipmentProfileScreenPreviewNight() {
    MyApplicationTheme {
        // val mockViewModel = remember { ProfileViewModel(mockUserRepository) } // Requires mock repo
        // EquipmentProfileScreen(viewModel = mockViewModel, onNavigateBack = {})
        Text("Preview needs ViewModel mocking")
    }
}

@Preview(showBackground = true, uiMode = Configuration.UI_MODE_NIGHT_NO)
@Composable
private fun EquipmentProfileScreenPreviewLight() {
    MyApplicationTheme {
        // val mockViewModel = remember { ProfileViewModel(mockUserRepository) } // Requires mock repo
        // EquipmentProfileScreen(viewModel = mockViewModel, onNavigateBack = {})
        Text("Preview needs ViewModel mocking")
    }
}
*/

// @Preview
// @Composable
// fun EquipmentProfileScreenPreview() {
//  val userRepository = UserRepositoryImpl(DatabaseManager(null))
//  val viewModel = ProfileViewModel(userRepository)
//  EquipmentProfileScreen(viewModel = viewModel, onNavigateBack = {})
// }

/*
@Preview(showBackground = true)
@Composable
fun SectionHeaderPreview() {
  SectionHeader(title = "Section Title")
}

@Preview(showBackground = true)
@Composable
fun PlateItemRowPreview() {
  val plate = DomainPlate(weight = 45f, count = 2, color = "#FF0000", unit = "lb")
  PlateItemRow(plate = plate, unit = "lb", onRemove = {})
}

@Preview(showBackground = true)
@Composable
fun BarbellItemRowPreview() {
  val barbell = DomainBarbell(weight = 45f, unit = "lb")
  BarbellItemRow(barbell = barbell, unit = "lb", onRemove = {})
}

@Preview(showBackground = true)
@Composable
fun CollarItemRowPreview() {
  val collar = DomainCollar(weight = 5f, unit = "lb")
  CollarItemRow(collar = collar, unit = "lb", onRemove = {})
}

@Preview(showBackground = true)
@Composable
fun AddItemRowPreview() {
  AddItemRow(text = "Add Item", onClick = {})
}

@Preview(showBackground = true)
@Composable
fun EquipmentItemBaseRowPreview() {
  EquipmentItemBaseRow(
    leadingContent = {
      Box(
        modifier =
          Modifier.size(24.dp)
            .clip(CircleShape)
            .background(Color.Red)
            .border(2.dp, MaterialTheme.colorScheme.onSurface, CircleShape)
      )
    },
    text = "Item Text",
    onRemove = {},
    showRemoveIcon = true,
  )
}

@Preview
@Composable
fun AddBarbellDialogPreview() {
  AddBarbellDialog(unit = "lb", onDismiss = {}, onAddBarbell = {})
}

@Preview
@Composable
fun AddCollarDialogPreview() {
  AddCollarDialog(unit = "lb", onDismiss = {}, onAddCollar = {})
}

@Preview
@Composable
fun DialogBasePreview() {
  DialogBase(title = "Dialog Title", onDismiss = {}, onConfirm = {}) { Text("Dialog Content") }
}
*/

@Preview
@Composable
fun AddPlateDialogPreview() {
  MyApplicationTheme { AddPlateDialog(unit = "lb", onDismiss = {}, onAddPlate = { _, _, _ -> }) }
}
