@file:OptIn(ExperimentalUuidApi::class)

package com.example.myapplication.features.program.ui

import android.widget.Toast
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateContentSize
import androidx.compose.animation.core.tween
import androidx.compose.animation.expandVertically
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.TextAutoSize
import androidx.compose.foundation.text.selection.SelectionContainer
import androidx.compose.material.TabRow
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.ArrowBack
import androidx.compose.material.icons.outlined.ExpandLess
import androidx.compose.material.icons.outlined.ExpandMore
import androidx.compose.material.icons.outlined.MoreVert
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.AssistChip
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Checkbox
import androidx.compose.material3.CheckboxDefaults
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExtendedFloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.ScrollableTabRow
import androidx.compose.material3.ShapeDefaults
import androidx.compose.material3.Tab
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TextField
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.BlendMode
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.LinkAnnotation
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.TextLinkStyles
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.googlefonts.Font
import androidx.compose.ui.text.googlefonts.GoogleFont
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import androidx.navigation.NavDestination
import androidx.navigation.NavGraph.Companion.findStartDestination
import coil.compose.AsyncImage
import com.example.myapplication.R
import com.example.myapplication.common.domain.model.ExerciseSets
import com.example.myapplication.common.domain.model.Mesocycle
import com.example.myapplication.common.domain.model.Microcycle
import com.example.myapplication.common.domain.model.Program
import com.example.myapplication.common.domain.model.Superset
import com.example.myapplication.common.domain.model.Workout
import com.example.myapplication.common.domain.model.formatWorkoutDate
import com.example.myapplication.common.ui.component.BottomNavigationBar
import com.example.myapplication.common.util.createAnnotatedLinkString
import com.example.myapplication.common.util.doubleFormatted
import com.example.myapplication.features.program.ProgramsViewModel
import org.bson.types.ObjectId
import org.koin.androidx.compose.koinViewModel
import kotlin.uuid.ExperimentalUuidApi

@Composable
fun ProgramDetailsScreen(
  navController: NavController,
  programsViewModel: ProgramsViewModel = koinViewModel(),
  _programID: String,
  isProgramActive: Boolean = false,
  currentDestination: NavDestination? = null,
  onNavigateBottomBar: (route: String) -> Unit,
) {

  val programID by remember { mutableStateOf(_programID) }

  val program by programsViewModel.selectedProgram.collectAsStateWithLifecycle(null)

//  val programMesocycles by programsViewModel.selectedProgramMesocycles.collectAsStateWithLifecycle()
//
//  val programMicrocycles by
//  programsViewModel.selectedProgramMicrocycles.collectAsStateWithLifecycle()
  //  val mesocycles by programsViewModel.get

  // 2. Use LaunchedEffect to trigger loading when programID changes
  LaunchedEffect(programID) {
    programsViewModel.getProgramById(ObjectId(programID))
//    programsViewModel.getMesocyclesByProgram(ObjectId(programID))
//    programsViewModel.getMicrocyclesByProgram(ObjectId(programID))
  }

  program?.let {
    ProgramDetailsLayout(
      navController = navController,
      programsViewModel = programsViewModel,
      program = it,
      mesocycles = it.mesocycles,
      microcycles = it.mesocycles.flatMap { mesocycle -> mesocycle.microcycles },
//      programMesocycles,
//      programMicrocycles,
      currentDestination = currentDestination,
      onNavigateBottomBar = onNavigateBottomBar,
    )
  }
}

// TODO Add animatedVisibility Function to expand more/less buttons
// TODO Add Floating Action Button to Start Program
@OptIn(ExperimentalMaterial3Api::class, ExperimentalFoundationApi::class)
// @Preview
@Composable
fun ProgramDetailsLayout(
  navController: NavController,
  programsViewModel: ProgramsViewModel, /*= viewModel()*/
  program: Program,
  mesocycles: List<Mesocycle>,
  microcycles: List<Microcycle>,
  currentDestination: NavDestination? = null,
  onNavigateBottomBar: (route: String) -> Unit,
) {

  val titleMedium = MaterialTheme.typography.titleLarge
  val onSurface = MaterialTheme.colorScheme.onSurface

  val scrollState = rememberScrollState()
  var selectedTabIndex by remember { mutableIntStateOf(0) }
  val titles = listOf("Overview", "Mesocycles", "Workouts")

  val programActive = programsViewModel.isProgramActive(program.uuid)
  //  Log.i("ProgramDetailsLayout", "program mesocycles: ${program.mesocycles.size}")
  //  Log.i("ProgramDetailsLayout", "programActive: $programActive")
  //  Log.i("ProgramDetailsLayout", "mesocycles: ${program.mesocycles}")
  //  Log.i("ProgramDetailsLayout", "microcycles:
  // ${Json.encodeToString(program.mesocycles[0].microcycles[0])}")

  val (startButtonPressed, setStartButtonPressed) = rememberSaveable { mutableStateOf(false) }

  Scaffold(
    modifier = Modifier.imePadding(),
    topBar = {
      TopAppBar(
        title = {
          Text(
            text = program.name ?: "New Program",
            maxLines = 2,
            overflow = TextOverflow.Visible,
            color = onSurface,
            style = titleMedium,
            textAlign = TextAlign.Center,
          )
        },
        navigationIcon = {
          IconButton(onClick = { navController.navigateUp() }) {
            Icon(
              imageVector = Icons.AutoMirrored.Outlined.ArrowBack,
              tint = MaterialTheme.colorScheme.onSurface,
              contentDescription = "Go to the previous screen",
            )
          }
        },
        actions = { ProgramDetailsOptionsDropdownMenu() },
      )
    },
    bottomBar = {
      BottomNavigationBar(
        currentDestination = currentDestination,
        onNavigate = onNavigateBottomBar,
      )
    },
    floatingActionButton = {
      if (!programActive || startButtonPressed) {
        ExtendedFloatingActionButton(
          onClick = {
            if (!startButtonPressed) {
              programsViewModel.startNewProgram(program = program)
              setStartButtonPressed(true)
            }
          }
        ) {
          Text(text = "Start Program")
        }
      }
    },
  ) { contentPadding ->
    val provider =
      GoogleFont.Provider(
        providerAuthority = "com.google.android.gms.fonts",
        providerPackage = "com.google.android.gms",
        certificates = R.array.com_google_android_gms_fonts_certs,
      )

    LazyColumn(
      Modifier
        .padding(contentPadding)
        .fillMaxWidth()
        .fillMaxHeight()
//        .verticalScroll(scrollState)
    ) {
      item {
        Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.Center) {
          /*      BoxWithConstraints(modifier = Modifier.fillMaxWidth(), contentAlignment = Alignment.Center,)
          {
            val height = constraints.maxWidth*/

          program.imageUri?.isNotBlank().let {
            Box(
              modifier = Modifier
                .fillMaxWidth()
                .fillMaxHeight(0.1f)
                .clickable(onClick = {}),
              contentAlignment = Alignment.Center,
            ) {
              AsyncImage(
                contentScale = ContentScale.Crop,
                alignment = Alignment.TopCenter,
                model = program.imageUri,
                contentDescription = null,
                colorFilter =
                  ColorFilter.tint(color = Color.LightGray, blendMode = BlendMode.Modulate),
              )
              if (!program.imageFont.isNullOrBlank()) {
                val fontName = GoogleFont(program.imageFont ?: "Montserrat")

                val fontFamily = FontFamily(Font(googleFont = fontName, fontProvider = provider))

                Text(
                  text = program.name?.uppercase() ?: "",
                  style =
                    TextStyle(
                      color = Color.White,
                      fontSize = MaterialTheme.typography.displayLarge.fontSize,
                      fontWeight = FontWeight.Bold,
                      fontFamily = fontFamily,
                    ),
                )
              }
            }
          }
          //        }

          /*        Icon(
            imageVector = Icons.Outlined.BrokenImage,
            contentDescription = "Missing Image",
            tint = MaterialTheme.colorScheme.primary,
          )*/
        }
      }

      stickyHeader {
        TabRow(
          selectedTabIndex = selectedTabIndex,
          backgroundColor = MaterialTheme.colorScheme.primaryContainer,
          contentColor = MaterialTheme.colorScheme.onPrimaryContainer,
        ) {
          titles.forEachIndexed { index, title ->
            Tab(
              selected = selectedTabIndex == index,
              onClick = { selectedTabIndex = index },
              text = { Text(text = title, maxLines = 2, overflow = TextOverflow.Ellipsis) },
            )
          }
        }
      }

      item {
        Column(
          Modifier
            .padding(12.dp)
            .fillMaxWidth()
        ) {
          when (selectedTabIndex) {
            0 ->
              OverviewTab(
                program = program,
                microcycles = microcycles,
                //              programsViewModel = programsViewModel
              )

            1 -> MesocyclesTab(activeProgram = program.isActive, mesocycles = mesocycles)
            2 ->
              WorkoutsTab(
                activeProgram = program.isActive,
                navController = navController,
                microcycles = microcycles,
              )
          }
        }
      }


    }
  }
}

@Composable
fun ProgramDetailsOptionsDropdownMenu(
  isProgramActive: Boolean = false,
  onDelete: () -> Unit = {},
  onEdit: () -> Unit = {},
  onSubstitute: () -> Unit = {}
) {
  var expanded by remember { mutableStateOf(false) }

  val onSurface = MaterialTheme.colorScheme.onSurface

  Box {
    IconButton(onClick = { expanded = true }) {
      Icon(
        Icons.Outlined.MoreVert,
        contentDescription = "Program Options and Settings",
        tint = MaterialTheme.colorScheme.onSurface,
      )
    }
    DropdownMenu(expanded = expanded, onDismissRequest = { expanded = false }) {
      DropdownMenuItem(
        text = { Text("Edit Program", color = onSurface) },
        onClick = {
          /* TODO Navigate to Create Edit Program Screen*/
          onEdit
          expanded = false
        },
      )
      if (isProgramActive) {
        DropdownMenuItem(
          text = { Text("Substitute Exercises", color = onSurface) },
          onClick = {
            /* TODO Navigate to Create Edit Program Screen*/
            onSubstitute()
            expanded = false
          },
        )

        DropdownMenuItem(
          text = { Text("Delete Program", color = onSurface) },
          onClick = {
            onDelete() /* TODO Delete Program*/
            expanded = false
          },
        )
      }
    }
  }
}

@Composable
fun OverviewTab(
  program: Program,
  microcycles: List<Microcycle>,
  //  programsViewModel: ProgramsViewModel = koinViewModel()
) {
  val programsViewModel: ProgramsViewModel = koinViewModel() // Added to access ViewModel
  val allProgramTags by programsViewModel.allProgramTags.collectAsState() // Collect all tags

  //  Log.i("OverviewTab", "microcycles: $microcycles")
  Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
    ProgramOverviewCard(program = program)
    //    CoachsCard(program = program)
    ProgramDescriptionCard(program = program)

    // Display Existing Tags
    program.let { prog ->
      if (prog.tags.isNotEmpty()) {
        Text(
          "Tags:",
          style = MaterialTheme.typography.titleMedium,
          modifier = Modifier.padding(top = 16.dp, bottom = 4.dp) // Added more top padding
        )
        androidx.compose.foundation.layout.FlowRow(
          mainAxisSpacing = 4.dp,
          crossAxisSpacing = 4.dp,
        ) {
          prog.tags.forEach { tag ->
            androidx.compose.material3.AssistChip(
              onClick = { /* TODO: Future enhancement: remove tag on click? */ },
              label = { Text(tag) }
            )
          }
        }
      }
    }

    // Manage Tags Button and Dialog
    var showTagDialog by rememberSaveable { mutableStateOf(false) }
    var newTagText by rememberSaveable { mutableStateOf("") }
    var currentSelectedTags by remember(program?.tags, showTagDialog) { mutableStateOf(program?.tags?.toSet() ?: emptySet()) }

    Button(onClick = { showTagDialog = true }, modifier = Modifier.padding(top = 8.dp)) {
      Text("Manage Tags")
    }

    if (showTagDialog) {
      AlertDialog(
        onDismissRequest = {
          newTagText = ""
          showTagDialog = false
        },
        title = { Text("Manage Tags") },
        text = {
          Column {
            TextField(
              value = newTagText,
              onValueChange = { newTagText = it },
              label = { Text("Add new tag") },
              modifier = Modifier.fillMaxWidth()
            )
            Spacer(Modifier.height(8.dp))
            Text("Select existing tags:", style = MaterialTheme.typography.labelMedium)
            LazyColumn(modifier = Modifier.heightIn(max = 200.dp)) { // Limit height
              items(allProgramTags.sorted().distinct()) { existingTag ->
                val isSelected = currentSelectedTags.contains(existingTag)
                Row(
                  Modifier
                    .fillMaxWidth()
                    .clickable {
                      currentSelectedTags = if (isSelected) {
                        currentSelectedTags - existingTag
                      } else {
                        currentSelectedTags + existingTag
                      }
                    }
                    .padding(vertical = 4.dp),
                  verticalAlignment = Alignment.CenterVertically
                ) {
                  Checkbox(checked = isSelected, onCheckedChange = null)
                  Spacer(Modifier.width(8.dp))
                  Text(existingTag)
                }
              }
            }
          }
        },
        confirmButton = {
          Button(onClick = {
            val finalTags = currentSelectedTags.toMutableSet()
            if (newTagText.isNotBlank()) {
              finalTags.add(newTagText.trim())
            }
            program?.copy(tags = finalTags.toList().sorted())?.let { updatedProgram ->
              programsViewModel.updateProgram(updatedProgram)
              // programsViewModel.getProgramById(updatedProgram.id) // Optional refresh
            }
            newTagText = ""
            showTagDialog = false
          }) { Text("Confirm") }
        },
        dismissButton = {
          Button(onClick = {
            newTagText = ""
            showTagDialog = false
          }) { Text("Cancel") }
        }
      )
    }

    WorkoutOverviewCard(microcycles = microcycles /*programsViewModel = programsViewModel*/)
    Spacer(modifier = Modifier.height(60.dp))
  }
}

@Composable
fun MesocyclesTab(activeProgram: Boolean, mesocycles: List<Mesocycle>) {
  //  TODO
  Column(modifier = Modifier.fillMaxHeight(), verticalArrangement = Arrangement.spacedBy(12.dp)) {
    MesocycleCard(activeProgram = activeProgram, mesocycles = mesocycles)
    Spacer(modifier = Modifier.height(60.dp))
  }
}

// @Preview
@Composable
fun WorkoutsTab(
  activeProgram: Boolean,
  navController: NavController,
  microcycles: List<Microcycle>,
) {
  Column(modifier = Modifier.fillMaxHeight(), verticalArrangement = Arrangement.spacedBy(12.dp)) {
    MicrocycleCard(
      activeProgram = activeProgram,
      navController = navController,
      microcycles = microcycles,
    )
    Spacer(modifier = Modifier.height(60.dp))
  }
}

@Composable
fun CoachsCard(program: Program) {
  val padding = 12.dp
  val titleLarge = MaterialTheme.typography.titleLarge
  val titleMedium = MaterialTheme.typography.titleMedium
  val bodyMedium = MaterialTheme.typography.bodyMedium

  Card() {
    SelectionContainer {
      Column(
        modifier = Modifier
          .fillMaxWidth()
          .padding(padding)
      ) {
        Row(modifier = Modifier.fillMaxWidth()) {
          Column(modifier = Modifier.padding(bottom = 8.dp)) {
            Text(text = "Coach Overview", style = titleLarge)
          }
        }
        Row(
          modifier = Modifier
            .fillMaxWidth()
            .padding(bottom = 8.dp)
        ) {
          Column() { Text(text = program.author ?: "Coach Name", style = titleMedium) }
        }
      }
    }
  }
}

@Composable
fun ProgramOverviewCard(program: Program) {
  val padding = 12.dp
  val titleMedium = MaterialTheme.typography.titleMedium
  val labelMedium = MaterialTheme.typography.bodyMedium
  val bodyMedium = MaterialTheme.typography.bodyMedium

  val textLinkStyles =
    TextLinkStyles(
      style =
        SpanStyle(
          color = MaterialTheme.colorScheme.primary,
          fontStyle = FontStyle.Italic,
          textDecoration = TextDecoration.Underline,
        )
    )

  Card() {
    Column(
      modifier = Modifier
        .fillMaxWidth()
        .padding(padding)
    ) {
      SelectionContainer {
        Row(modifier = Modifier.fillMaxWidth()) {
          Column(modifier = Modifier.padding(bottom = 8.dp)) {
            Text(
              modifier = Modifier.padding(bottom = 8.dp),
              text = "Program Overview",
              style = titleMedium,
            )
            val annotatedString =
              program.name?.length?.let {
                buildAnnotatedString {
                  append(program.name ?: "")
                  addLink(
                    url = LinkAnnotation.Url(url = program.uri ?: "", styles = textLinkStyles),
                    start = 0,
                    end = it,
                  )
                  addStringAnnotation(
                    tag = "URL",
                    annotation = program.uri ?: "",
                    start = 0,
                    end = it,
                  )
                }
              }
            if (!annotatedString.isNullOrBlank()) {
              Text(text = annotatedString, style = bodyMedium, fontWeight = FontWeight.Bold)
            }
            program.author?.let { Text(text = it, style = bodyMedium) }
          }
        }
      }

      //      Body
      Row(modifier = Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
        SelectionContainer {
          Column() {
            Row(
              modifier = Modifier.fillMaxWidth(),
              verticalAlignment = Alignment.CenterVertically,
            ) {
              Column(modifier = Modifier.weight(1f)) {
                Text(text = "Level", style = labelMedium, fontWeight = FontWeight.Bold)
              }
              Column(modifier = Modifier.weight(1f)) {
                Text(text = program.level ?: "", style = bodyMedium)
              }
            }
            Row(
              modifier = Modifier.fillMaxWidth(),
              verticalAlignment = Alignment.CenterVertically,
            ) {
              Column(modifier = Modifier.weight(1f)) {
                Text(text = "Equipment", style = labelMedium, fontWeight = FontWeight.Bold)
              }
              Column(modifier = Modifier.weight(1f)) {
                Text(text = program.programEquipment ?: "", style = bodyMedium)
              }
            }
            Row(
              modifier = Modifier.fillMaxWidth(),
              verticalAlignment = Alignment.CenterVertically,
            ) {
              Column(modifier = Modifier.weight(1f)) {
                Text(text = "Days Per Week", style = labelMedium, fontWeight = FontWeight.Bold)
              }
              Column(modifier = Modifier.weight(1f)) {
                Text(text = program.daysPerWeek ?: "", style = bodyMedium)
              }
            }
            Row(
              modifier = Modifier.fillMaxWidth(),
              verticalAlignment = Alignment.CenterVertically,
            ) {
              Column(modifier = Modifier.weight(1f)) {
                Text(text = "Program Length", style = labelMedium, fontWeight = FontWeight.Bold)
              }
              Column(modifier = Modifier.weight(1f)) {
                Text(text = program.programLength ?: "", style = bodyMedium)
              }
            }
          }
        }
      }
      SelectionContainer {
        program.shortDescription?.isNotBlank().let {
          val shortDescription = program.shortDescription ?: ""
          Row(
            modifier = Modifier
              .fillMaxWidth()
              .padding(top = 8.dp)
          ) {
            Column() {
              Text(
                text = createAnnotatedLinkString(shortDescription, textLinkStyles),
                style = bodyMedium,
              )
            }
          }
        }
      }
    }
  }
}

@Composable
fun ProgramDescriptionCard(program: Program) {
  val padding = 12.dp
  val titleMedium = MaterialTheme.typography.titleMedium
  val labelMedium = MaterialTheme.typography.labelMedium
  val bodyMedium = MaterialTheme.typography.bodyMedium

  val textLinkStyles =
    TextLinkStyles(
      style =
        SpanStyle(
          color = MaterialTheme.colorScheme.primary,
          fontStyle = FontStyle.Italic,
          textDecoration = TextDecoration.Underline,
        )
    )

  val (expanded, onClick) = remember { mutableStateOf(false) }

  //  TODO Add Expand/Hide Text Button
  Card() {
    Column(
      modifier = Modifier
        .fillMaxWidth()
        .padding(padding)
    ) {
      Row(modifier = Modifier.fillMaxWidth()) {
        Column(modifier = Modifier.padding(bottom = 8.dp)) {
          Text(text = "Program Description", style = titleMedium)
        }
      }

      Row(modifier = Modifier.fillMaxWidth()) {
        SelectionContainer {
          Column(modifier = Modifier.animateContentSize(animationSpec = tween(100))) {
            program.description?.let { description ->
              if (expanded) {
                // Text(text = description, style = bodyMedium)
                Text(
                  text = createAnnotatedLinkString(description, textLinkStyles),
                  style = bodyMedium,
                )
              } else {

                Text(
                  text = createAnnotatedLinkString(description, textLinkStyles),
                  style = bodyMedium,
                  maxLines = 20,
                  overflow = TextOverflow.Ellipsis,
                )
              }
            }
          }
        }
      }
      //      TODO Hide if not needed
      TextButton(
        onClick = { onClick(!expanded) },
        contentPadding = PaddingValues(start = 0.dp, bottom = 0.dp),
      ) {
        Text(
          text = if (expanded) "Show less" else "Show more",
          style = MaterialTheme.typography.labelMedium,
        )
      }
    }
  }
}

@Composable
fun WorkoutOverviewCard(
  microcycles: List<Microcycle>
  //  programsViewModel: ProgramsViewModel = koinViewModel()
) {
  val padding = 12.dp

  val titleMedium = MaterialTheme.typography.titleMedium
  val labelMedium = MaterialTheme.typography.labelMedium
  val bodyMedium = MaterialTheme.typography.bodyMedium

  var selectedTabIndex by remember { mutableIntStateOf(0) }
  val scrollState = rememberScrollState()
  val titles =
    if (microcycles.isNotEmpty()) {
      List(microcycles.size) { "Week ${it.plus(1)}" }
    } else {
      List(1) { "Week 1" }
    }

  if (microcycles.isNotEmpty()) {
    val workouts = microcycles[selectedTabIndex].workouts
    //    Log.i("WorkoutOverviewCard", Json.encodeToString(workouts))
    Card() {
      Column(
        modifier = Modifier
          .fillMaxWidth()
          .padding(padding)
      ) {
        Row(modifier = Modifier.fillMaxWidth()) {
          Text(text = "Workout Overview", style = titleMedium)
        }

        ScrollableTabRow(
          selectedTabIndex = selectedTabIndex,
          edgePadding = 0.dp,
          containerColor = Color.Transparent,
        ) {
          titles.forEachIndexed { index, title ->
            Tab(
              selected = selectedTabIndex == index,
              onClick = { selectedTabIndex = index },
              text = { Text(text = title, maxLines = 2, overflow = TextOverflow.Ellipsis) },
            )
          }
        }

        Row(
          Modifier
            .padding(top = 8.dp)
            .fillMaxWidth()
            .horizontalScroll(scrollState),
          horizontalArrangement = Arrangement.spacedBy(12.dp),
        ) {
          if (workouts.isNotEmpty()) {
            workouts
              .sortedBy { it.day }
              .forEachIndexed { index, workout -> WorkoutDayCard(index, workout) }
          }
        }
      }
    }
  }
}

@Composable
fun WorkoutDayCard(
  index: Int,
  workout: Workout,
  //  programsViewModel: ProgramsViewModel = koinViewModel()
) {
  val padding = 12.dp
  val spacedByPadding = 12.dp
  val titleMedium = MaterialTheme.typography.titleMedium
  val labelMedium = MaterialTheme.typography.labelMedium
  val bodyMedium = MaterialTheme.typography.bodyMedium
  val cardColors = CardDefaults.cardColors()

  val workoutItems = workout.workoutItems

  ElevatedCard() {
    Column(
      modifier = Modifier
        .width(300.dp)
        .padding(padding)
    ) {
      Row(
        modifier = Modifier
          .fillMaxWidth()
          .padding(bottom = 8.dp),
        horizontalArrangement = Arrangement.Center,
      ) {
        Text(text = "Day ${index.plus(1)}", style = labelMedium)
      }
      if (workoutItems.isNotEmpty()) {
        workoutItems
          .sortedBy { it.order }
          .forEachIndexed { _index, workoutItem ->
            when (workoutItem) {
              is ExerciseSets -> {
                ExerciseOverview(index = _index, exerciseSets = workoutItem)
                Spacer(modifier = Modifier.height(spacedByPadding))
              }

              is Superset -> {
                workoutItem.exercisesSets.forEachIndexed { index, exerciseSets ->
                  ExerciseOverview(index = index.plus(_index), exerciseSets = exerciseSets)
                  Spacer(modifier = Modifier.height(spacedByPadding))
                }
              }
            }
          }
      }
    }
  }
}

@Composable
fun ExerciseOverview(
  index: Int,
  exerciseSets: ExerciseSets,
  programsViewModel: ProgramsViewModel = koinViewModel(),
) {
  val labelMedium = MaterialTheme.typography.labelMedium
  val exercise = exerciseSets.exerciseID
  val exerciseName = programsViewModel.getExerciseNameByID(exerciseID = exercise)

  exerciseName?.let {
    Row(
      modifier = Modifier
        .fillMaxWidth()
        .padding(bottom = 4.dp)
    ) {
      Text(text = "${index.plus(1)}. $exerciseName", style = labelMedium)
    }
    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
      Text(text = "Sets", style = labelMedium, modifier = Modifier.weight(1f))
      Text(
        text = "Target Reps",
        style = labelMedium,
        modifier = Modifier.weight(1f),
        textAlign = TextAlign.Center,
      )
      Text(
        text = "Target RIR",
        style = labelMedium,
        modifier = Modifier.weight(1f),
        textAlign = TextAlign.Center,
      )
    }

    exerciseSets.sets
      .sortedBy { it.setNumber }
      .forEach { set ->
        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
          Text(
            text = set.setNumber.toString(),
            style = labelMedium,
            modifier = Modifier
              .weight(1f)
              .padding(start = 8.dp),
          )
          Text(
            text =
              when {
                set.targetMinimumRepetitions != null && set.targetMaximumRepetitions != null ->
                  if (set.targetMinimumRepetitions == set.targetMaximumRepetitions) {
                    set.targetMinimumRepetitions.toString()
                  } else {
                    "${set.targetMinimumRepetitions} - ${set.targetMaximumRepetitions}"
                  }

                set.targetMinimumRepetitions != null && set.targetMaximumRepetitions == null ->
                  "${set.targetMinimumRepetitions}"

                set.targetMinimumRepetitions == null && set.targetMaximumRepetitions != null ->
                  "${set.targetMaximumRepetitions}"

                else -> "–"
              },
            style = labelMedium,
            modifier = Modifier.weight(1f),
            textAlign = TextAlign.Center,
          )
          Text(
            text = doubleFormatted(set.targetRepetitionsInReserve),
            style = labelMedium,
            modifier = Modifier.weight(1f),
            textAlign = TextAlign.Center,
          )
        }
      }
  }
}

@Composable
fun MicrocycleCard(
  activeProgram: Boolean,
  navController: NavController,
  microcycles: List<Microcycle>,
) {
  val context = LocalContext.current
  val padding = 12.dp
  val titleMedium = MaterialTheme.typography.titleMedium
  val labelMedium = MaterialTheme.typography.labelMedium
  val bodyMedium = MaterialTheme.typography.bodyMedium
  val cardColors = CardDefaults.cardColors()

  val textLinkStyles =
    TextLinkStyles(
      style =
        SpanStyle(
          color = MaterialTheme.colorScheme.primary,
          fontStyle = FontStyle.Italic,
          textDecoration = TextDecoration.Underline,
        )
    )

  fun getExpandCollapseIcon(visible: Boolean): ImageVector {
    return if (visible) {
      Icons.Outlined.ExpandLess
    } else {
      Icons.Outlined.ExpandMore
    }
  }

  if (microcycles.isNotEmpty()) {
    microcycles.forEachIndexed { index, microcycle ->
      // TODO Figure out a way to show the most recent microcycle with completed workouts

      val (workoutVisible, setWorkoutVisible) =
        rememberSaveable { mutableStateOf(!activeProgram && index == 0) }


      Card() {
        Column(
          modifier = Modifier
            .fillMaxWidth()
            .padding(padding)
        ) {
          Row(
            modifier = Modifier
              .fillMaxWidth()
              .padding(bottom = 8.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically,
          ) {
            Text(
              text =
                if (microcycle.name == "Week ${index.plus(1)}") {
                  "Week ${index.plus(1)}"
                } else {
                  "Week ${index.plus(1)}: ${microcycle.name}"
                },
              style = titleMedium,
              maxLines = 1,
              autoSize = TextAutoSize.StepBased(maxFontSize = titleMedium.fontSize),
              //              overflow = TextOverflow.Ellipsis,
              softWrap = true,
              modifier = Modifier.weight(1f),
            )

            IconButton(onClick = { setWorkoutVisible(!workoutVisible) }) {
              Icon(
                imageVector = getExpandCollapseIcon(workoutVisible),
                contentDescription = "Show workouts",
              )
            }
          }

          AnimatedVisibility(
            visible = workoutVisible,
            enter = expandVertically(animationSpec = tween(500)),
            exit = shrinkVertically(animationSpec = tween(500)),
          ) {
            SelectionContainer {
              Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                val (expanded, onClick) = remember { mutableStateOf(false) }
                val (showShowMoreButton, unhideShowMoreButton) = remember { mutableStateOf(false) }

                if (!microcycle.description.isNullOrBlank()) {
                  Row(modifier = Modifier.fillMaxWidth()) {
                    Column(modifier = Modifier.animateContentSize(animationSpec = tween(100))) {
                      val description = microcycle.description ?: ""
                      if (expanded) {
                        Text(
                          text = createAnnotatedLinkString(description, textLinkStyles),
                          style = bodyMedium,
                        )
                      } else {
                        Text(
                          text = createAnnotatedLinkString(description, textLinkStyles),
                          style = bodyMedium,
                          maxLines = 20,
                          overflow = TextOverflow.Ellipsis,
                          onTextLayout = { textLayoutResult ->
                            if (textLayoutResult.hasVisualOverflow) {
                              unhideShowMoreButton(true)
                            }
                          },
                        )
                      }
                    }
                  }
                }

                if (showShowMoreButton) {
                  TextButton(
                    onClick = { onClick(!expanded) },
                    contentPadding = PaddingValues(start = 0.dp, bottom = 0.dp),
                  ) {
                    Text(
                      text = if (expanded) "Show less" else "Show more",
                      style = MaterialTheme.typography.labelMedium,
                    )
                  }
                }

                microcycle.workouts
                  .sortedBy { it.day }
                  .forEach { workout ->
                    Row(
                      modifier = Modifier.fillMaxWidth(),
                      //                    .padding(bottom = 8.dp),
                      //                horizontalArrangement = Arrangement.SpaceBetween,
                      verticalAlignment = Alignment.CenterVertically,
                    ) {
                      Checkbox(
                        checked = workout.workoutEnded != null,
                        onCheckedChange = {},
                        colors = CheckboxDefaults.colors(),
                      )
                      Column(modifier = Modifier.weight(1f)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                          Text(
                            text = workout.name,
                            style = titleMedium,
                            softWrap = true,
                            modifier = Modifier.weight(1f),
                            overflow = TextOverflow.Ellipsis,
                          )
                        }
                        // TODO Hide if workout not completed in Active Program
                        if (workout.workoutEnded != null) {
                          Box(
                            contentAlignment = Alignment.Center,
                            modifier =
                              Modifier
                                .padding(horizontal = 0.dp)
                                .height(22.dp)
                                .background(
                                  color = MaterialTheme.colorScheme.tertiaryContainer,
                                  shape = ShapeDefaults.ExtraSmall,
                                ),
                          ) {
                            if (workout.formatWorkoutDate().isNotEmpty()) {
                              Text(
                                text = workout.formatWorkoutDate(),
                                style = bodyMedium,
                                modifier = Modifier.padding(horizontal = 4.dp),
                              )
                            }
                          }
                        }

                        Text(text = "${workout.workoutItems.size} Exercises", style = bodyMedium)
                      }



                      if (!activeProgram) {
                        // this workout in a Active Program, so copy Workout from Workout Template
                        val workoutRoute =
                          "workoutScreen?workoutID=${workout.id.toHexString()}/copyWorkoutFromPreviousWorkout=true"
                        if (workout.workoutEnded == null) {
                          Button(
                            enabled = true, // activeProgram,
                            onClick = {
                              navController.navigate(workoutRoute) {
                                popUpTo(navController.graph.findStartDestination().id) {
                                  saveState = true
                                }
                                // Avoid multiple copies of the same destination when
                                // reselecting the same item
                                launchSingleTop = true
                                // Restore state when reselecting a previously selected item
                                restoreState = true
                              }
                            },
                          ) {
                            Text(text = "Start")
                          }
                        } else {
                          // this workout in a Active Program, so view it
                          val workoutRoute =
                            "workoutScreen?workoutID=${workout.id.toHexString()}/copyWorkoutFromPreviousWorkout=false"
                          OutlinedButton(
                            enabled = activeProgram,
                            onClick = {
                              // TODO Create EditWorkoutScreen
                              // TODO Implement Navigate to Edit Workout
                              //                        navController.navigate(workoutRoute)
                              Toast.makeText(
                                context,
                                "TODO: Implement View/Edit Workout",
                                Toast.LENGTH_SHORT,
                              )
                                .show()
                            },
                          ) {
                            Text(text = "View")
                          }
                        }
                      }
                    }
                  }
              }
            }
          }
        }
      }
    }
  }
}

@Composable
fun MesocycleCard(activeProgram: Boolean, mesocycles: List<Mesocycle>) {

  val padding = 12.dp
  val titleMedium = MaterialTheme.typography.titleMedium
  val labelMedium = MaterialTheme.typography.labelMedium
  val bodyMedium = MaterialTheme.typography.bodyMedium
  val cardColors = CardDefaults.cardColors()

  fun getExpandCollapseIcon(visible: Boolean): ImageVector {
    return if (visible) {
      Icons.Outlined.ExpandLess
    } else {
      Icons.Outlined.ExpandMore
    }
  }

  if (mesocycles.isNotEmpty()) {
    mesocycles.forEachIndexed { index, mesocycle ->
      val (workoutVisible, setWorkoutVisible) =
        rememberSaveable { mutableStateOf(!activeProgram && index == 0) }

      //      TODO Figure out a way to show the most recent microcycle with completed workouts

      val textLinkStyles =
        TextLinkStyles(
          style =
            SpanStyle(
              color = MaterialTheme.colorScheme.primary,
              fontStyle = FontStyle.Italic,
              textDecoration = TextDecoration.Underline,
            )
        )

      Card() {
        Column(
          modifier = Modifier
            .fillMaxWidth()
            .padding(padding)
        ) {
          Row(
            modifier = Modifier
              .fillMaxWidth()
              .padding(bottom = 8.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically,
          ) {
            Text(
              text =
                "Mesocycle ${index.plus(1)}${
                  if (!mesocycle.name.isNullOrBlank()) {
                    " - ${mesocycle.name}"
                  } else {
                    ""
                  }
                }",
              style = titleMedium,
              //              overflow = TextOverflow.Ellipsis,
              softWrap = true,
              modifier = Modifier.weight(1f),
            )

            IconButton(onClick = { setWorkoutVisible(!workoutVisible) }) {
              Icon(
                imageVector = getExpandCollapseIcon(workoutVisible),
                contentDescription = "Show mesocycle",
              )
            }
          }

          AnimatedVisibility(
            visible = workoutVisible,
            enter = expandVertically(animationSpec = tween(500)),
            exit = shrinkVertically(animationSpec = tween(500)),
          ) {
            SelectionContainer {
              Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                val (expanded, onClick) = remember { mutableStateOf(false) }
                val (showShowMoreButton, unhideShowMoreButton) = remember { mutableStateOf(false) }
                if (!mesocycle.description.isNullOrBlank()) {
                  Row(modifier = Modifier.fillMaxWidth()) {
                    Column(modifier = Modifier.padding(bottom = 8.dp)) {
                      Text(text = "Mesocycle Description", style = titleMedium)
                    }
                  }

                  Row(modifier = Modifier.fillMaxWidth()) {
                    Column(modifier = Modifier.animateContentSize(animationSpec = tween(100))) {
                      val description = mesocycle.description ?: ""
                      if (expanded) {
                        Text(
                          text = createAnnotatedLinkString(description, textLinkStyles),
                          style = bodyMedium,
                        )
                      } else {
                        Text(
                          text = createAnnotatedLinkString(description, textLinkStyles),
                          style = bodyMedium,
                          maxLines = 20,
                          overflow = TextOverflow.Ellipsis,
                          onTextLayout = { textLayoutResult ->
                            if (textLayoutResult.hasVisualOverflow) {
                              unhideShowMoreButton(true)
                            }
                          },
                        )
                      }
                    }
                  }
                }

                if (showShowMoreButton) {
                  TextButton(
                    onClick = { onClick(!expanded) },
                    contentPadding = PaddingValues(start = 0.dp, bottom = 0.dp),
                  ) {
                    Text(
                      text = if (expanded) "Show less" else "Show more",
                      style = MaterialTheme.typography.labelMedium,
                    )
                  }
                }

                mesocycle.microcycles.forEach { microcycle ->
                  Row(
                    modifier = Modifier.fillMaxWidth(),
                    //                    .padding(bottom = 8.dp),
                    //                horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically,
                  ) {
                    var completedWorkouts: Int = 0

                    microcycle.workouts.forEach { workout ->
                      if (workout.workoutEnded != null) {
                        completedWorkouts++
                      }
                    }

                    Checkbox(
                      checked = completedWorkouts == microcycle.workouts.size,
                      onCheckedChange = {},
                      colors = CheckboxDefaults.colors(),
                    )
                    Column(modifier = Modifier.weight(1f)) {
                      Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(
                          text = "${microcycle.name}",
                          style = titleMedium,
                          softWrap = true,
                          modifier = Modifier.weight(1f),
                          overflow = TextOverflow.Ellipsis,
                        )
                      }
                      //                  TODO Hide if workout not completed in Active Program
                      if (completedWorkouts == microcycle.workouts.size) {
                        Box(
                          contentAlignment = Alignment.Center,
                          modifier =
                            Modifier
                              .padding(horizontal = 0.dp)
                              .height(22.dp)
                              .background(
                                color = MaterialTheme.colorScheme.tertiaryContainer,
                                shape = ShapeDefaults.ExtraSmall,
                              ),
                        ) {
                          Text(
                            text = "Date Completed", // TODO Add date of completion
                            style = bodyMedium,
                            modifier = Modifier.padding(horizontal = 4.dp),
                          )
                        }
                      }

                      Text(text = "${microcycle.workouts.size} Workouts", style = bodyMedium)
                    }
                  }
                }
              }
            }
          }
        }
      }
    }
  }
}

