@file:OptIn(ExperimentalUuidApi::class, ExperimentalMaterial3ExpressiveApi::class)

package com.example.myapplication.features.program.ui

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.tween
import androidx.compose.animation.expandVertically
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.Assignment
import androidx.compose.material.icons.filled.FilterList
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.outlined.Add
import androidx.compose.material.icons.outlined.CreateNewFolder
import androidx.compose.material.icons.outlined.ExpandLess
import androidx.compose.material.icons.outlined.ExpandMore
import androidx.compose.material.icons.outlined.FileUpload
import androidx.compose.material.icons.outlined.MoreVert
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.Checkbox
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Chip
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SearchBar
import androidx.compose.material3.SearchBarDefaults
import androidx.compose.material3.Snackbar
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import androidx.navigation.NavDestination
import androidx.navigation.NavGraph.Companion.findStartDestination
import com.example.myapplication.common.domain.model.Program
import com.example.myapplication.common.domain.model.Workout // Added import for Workout
import com.example.myapplication.common.ui.component.BottomNavigationBar
import com.example.myapplication.common.ui.component.Screens
import com.example.myapplication.features.program.ProgramsViewModel
import org.koin.androidx.compose.koinViewModel
import kotlin.uuid.ExperimentalUuidApi

// TODO Add a placeholder card if there is no active program
// TODO Add FAB
// TODO Add a card to add new program
// TODO Add a placeholder card to when there are no programs, mesocycles or workouts
// TODO Add a card/button to add new mesocycle/block
// TODO Add animatedVisibility Function to expand more/less buttons
// TODO Create screens to show all programs and all workouts respectively
// TODO Fix MoreVert Icon Alignment on all composables

@OptIn(ExperimentalMaterial3Api::class, ExperimentalFoundationApi::class)
// @Preview
@Composable
fun ProgramsScreen(
  navController: NavController,
  programsViewModel: ProgramsViewModel = koinViewModel(),
  currentDestination: NavDestination? = null,
  onNavigateBottomBar: (route: String) -> Unit,
) {
  val padding8 = 8.dp
  val titleMedium = MaterialTheme.typography.titleMedium
  val scrollState = rememberScrollState()

  val (programVisible, setProgramVisible) = rememberSaveable { mutableStateOf(false) }
  val (mesocycleVisible, setMesocycleVisible) = rememberSaveable { mutableStateOf(false) }
  val (workoutVisible, setWorkoutVisible) = rememberSaveable { mutableStateOf(false) }

  //  TODO Get unique list of programs
  val programs by programsViewModel.programs.collectAsState()
  val activePrograms by
  programsViewModel.activePrograms.collectAsState() // TODO Get List of Active Programs

  fun getExpandCollapseIcon(visible: Boolean): ImageVector {
    return if (visible) {
      Icons.Outlined.ExpandLess
    } else {
      Icons.Outlined.ExpandMore
    }
  }

  val toastMessage = programsViewModel.toastMessage.collectAsStateWithLifecycle("")

  if (toastMessage.value.isNotEmpty()) {
    Snackbar {
      Text(text = toastMessage.value)
    }
  }

  Scaffold(
    modifier = Modifier.imePadding(),
    topBar = {
      TopAppBar(
        title = { Text(text = "Programs", style = MaterialTheme.typography.titleLarge) },
        navigationIcon = {
          /* IconButton(
            onClick = { navController.navigateUp() },
          ) {
            Icon(
              imageVector = Icons.AutoMirrored.Outlined.ArrowBack,
              tint = MaterialTheme.colorScheme.onSurface,
              contentDescription = "Go to the previous screen"
            )
          }*/
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
  ) { contentPadding ->
    LazyColumn(
      contentPadding = contentPadding,
      modifier = Modifier
        .fillMaxWidth()
        .fillMaxHeight()
        .padding(horizontal = 12.dp),
      //      verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
      stickyHeader { WorkoutSearchBar() }

      item {
        Row(modifier = Modifier.padding(top = 16.dp)) {
          if (activePrograms.isEmpty()) { // TODO Change back to activeProgram == null
            Text("Start a Program", style = titleMedium)
            // TODO No Active Programs Card
          } else {
            Text("Continue Program", style = titleMedium)
          }
        }
      }

      item {
        // TODO Implement Horizontal Pager/LazyRow
        // https://developer.android.com/jetpack/compose/layouts/pager
        val pagerState = rememberPagerState(pageCount = { activePrograms.size })
        HorizontalPager(
          state = pagerState,
          pageSpacing = 8.dp,
          contentPadding = PaddingValues(0.dp),
        ) { page ->
          activePrograms[page].let { program ->
            Column(
              modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 0.dp),
              verticalArrangement = Arrangement.spacedBy(padding8)
            ) {
              if (activePrograms.isEmpty()) {
                // TODO Change back to activeProgram == null
                // Text("Start a Program", style = titleMedium)
                // TODO No Active Programs Card
              } else {
                // Text("Continue Program", style = titleMedium)
                val nextWorkout = programsViewModel.getNextWorkoutByProgramID(program.id)
                // Log.i("PROGRAMS", "nextWorkout: $nextWorkout")

                // TODO resume current workout
                val currentlyActiveWorkout = programsViewModel.getCurrentlyActiveWorkouts()
                  .collectAsState(initial = emptyList())
                if (currentlyActiveWorkout.value.isNotEmpty()) {
                  currentlyActiveWorkout.value.forEach {
                    ActiveWorkoutCard(
                      navController,
                      it,
                      programsViewModel = programsViewModel,
                    )
                  }
                }
                if (nextWorkout != null) {
                  CurrentProgramCard(
                    navController,
                    program,
                    nextWorkout,
                    programsViewModel = programsViewModel,
                  )
                } else {
                  Text(
                    text =
                      "This program has no workouts or all workouts have been completed. You can start a new program."
                  )
                }
              }
            }
          }
        }
        Row(
          Modifier
            .height(20.dp)
            .fillMaxWidth(),
          //                    .align(Alignment.BottomCenter),
          //          verticalAlignment = Alignment.CenterVertically,
          horizontalArrangement = Arrangement.Center,
        ) {
          repeat(pagerState.pageCount) { iteration ->
            val primary = MaterialTheme.colorScheme.primary.copy(alpha = 1f)
            val color =
              if (pagerState.currentPage == iteration) primary else primary.copy(alpha = 0.5f)
            Box(
              modifier = Modifier
                .padding(2.dp)
                .clip(CircleShape)
                .background(color)
                .size(8.dp)
            )
          }
        }
      }

      item {
        Text("Quick Start", style = titleMedium)
        Button(
          onClick = {
            // TODO Navigate to Workout Screen
            navController.navigate(Screens.Workout.baseRoute) {
              popUpTo(navController.graph.findStartDestination().id) { saveState = true }
              // Avoid multiple copies of the same destination when
              // reselecting the same item
              launchSingleTop = true
              // Restore state when reselecting a previously selected item
              restoreState = true
            }
          }
        ) {
          Icon(imageVector = Icons.Outlined.Add, contentDescription = "Localized Description")
          Text(text = "Start An Empty Workout")
        }
      }

      item {
        Row(
          modifier = Modifier.fillMaxWidth(),
          horizontalArrangement = Arrangement.SpaceBetween,
          verticalAlignment = Alignment.CenterVertically,
        ) {
          Text(text = "Programs", style = titleMedium)

          Box {
            Row(verticalAlignment = Alignment.CenterVertically) { // Ensure icons are vertically centered
              // Program Filter Button
              var showProgramFilterDialog by rememberSaveable { mutableStateOf(false) }
              IconButton(onClick = { showProgramFilterDialog = true }) {
                Icon(
                  imageVector = Icons.Default.FilterList,
                  contentDescription = "Filter Programs"
                )
              }
              if (showProgramFilterDialog) {
                val allProgramTags by programsViewModel.allProgramTags.collectAsState()
                val currentProgramFilters by programsViewModel.selectedProgramFilterTags.collectAsState()
                var selectedTagsForFilter by remember(currentProgramFilters) { mutableStateOf(currentProgramFilters) }

                AlertDialog(
                  onDismissRequest = { showProgramFilterDialog = false },
                  title = { Text("Filter Programs by Tags") },
                  text = {
                    LazyColumn(modifier = Modifier.heightIn(max = 300.dp)) {
                      items(allProgramTags.sorted().distinct()) { tag ->
                        val isSelected = selectedTagsForFilter.contains(tag)
                        Row(
                          Modifier
                            .fillMaxWidth()
                            .clickable {
                              selectedTagsForFilter = if (isSelected) {
                                selectedTagsForFilter - tag
                              } else {
                                selectedTagsForFilter + tag
                              }
                            }
                            .padding(vertical = 4.dp),
                          verticalAlignment = Alignment.CenterVertically
                        ) {
                          Checkbox(checked = isSelected, onCheckedChange = null)
                          Text(tag, modifier = Modifier.padding(start = 8.dp))
                        }
                      }
                    }
                  },
                  confirmButton = {
                    Button(onClick = {
                      programsViewModel.updateSelectedProgramFilterTags(selectedTagsForFilter)
                      showProgramFilterDialog = false
                    }) { Text("Apply") }
                  },
                  dismissButton = {
                    Button(onClick = { showProgramFilterDialog = false }) { Text("Cancel") }
                  }
                )
              }

              IconButton(onClick = { /*TODO Import/Export*/ }) {
                Icon(
                  imageVector = Icons.Outlined.FileUpload,
                  contentDescription = "Import/Export New Program",
                )
              }
              IconButton(onClick = { /*TODO Create New Program*/ }) {
                Icon(
                  imageVector = Icons.Outlined.CreateNewFolder,
                  contentDescription = "Add New Program",
                )
              }
            }
          }
        }
      }

      item {
        Button(onClick = { /*TODO*/ }) {
          Icon(
            imageVector = Icons.AutoMirrored.Outlined.Assignment,
            contentDescription = "Add New Program",
          )
          Text(text = "New Program", Modifier.padding(start = 8.dp))
        }
      }

      item {
        Row(
          modifier = Modifier.fillMaxWidth(),
          horizontalArrangement = Arrangement.SpaceBetween,
          verticalAlignment = Alignment.CenterVertically,
        ) {
          IconButton(onClick = { setProgramVisible(!programVisible) }) {
            Icon(
              imageVector = getExpandCollapseIcon(programVisible),
              contentDescription = "Show more programs",
            )
          }
          TextButton(
            onClick = { setProgramVisible(!programVisible) },
            modifier = Modifier.weight(1f),
            enabled = true,
            contentPadding = PaddingValues(0.dp),
            //            style = MaterialTheme.typography.bodySmall
          ) {
            Text("Programs (${programs.size})", style = MaterialTheme.typography.bodyLarge)
          }
          // TODO: Consider moving this MoreVert to a more common place if it's for overall screen options
          IconButton(onClick = { /*TODO More Program Options*/ }) {
            Icon(
              imageVector = Icons.Outlined.MoreVert,
              contentDescription = "Localized Description",
            )
          }
        }
      }

      if (programs.isNotEmpty()) {
        items(programs, key = { it.lazyListItemID }) { programTemplate -> // Ensure this uses the filtered programs
          val program = activePrograms.find { it.uuid == programTemplate.uuid } ?: programTemplate

          AnimatedVisibility(
            visible = programVisible,
            enter = expandVertically(animationSpec = tween(500)),
            exit = shrinkVertically(animationSpec = tween(500)),
          ) {
            ProgramCard(
              program = program,
              onNavigate = { navController.navigate("programDetails/${program.id.toHexString()}/isProgramActive=${program.isActive}") },
              programDetailsOptionsDropdownMenu = {
                ProgramDetailsOptionsDropdownMenu(
                  isProgramActive = program.isActive,
                  onDelete = { programsViewModel.deleteActiveProgram(program) },
                )
              },
            )
          }
        }
      } else {
        item {
          AnimatedVisibility(
            visible = programVisible,
            enter = expandVertically(animationSpec = tween(500)),
            exit = shrinkVertically(animationSpec = tween(500)),
          ) {
            Text("No Programs")
          }
        }
      }
      // Display Active Program Filters
      item {
        val currentProgramFilters by programsViewModel.selectedProgramFilterTags.collectAsState()
        if (currentProgramFilters.isNotEmpty()) {
          Row(
            modifier = Modifier.padding(vertical = 4.dp, horizontal = 8.dp).fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(4.dp)
          ) {
            Text("Active Filters:", style = MaterialTheme.typography.labelSmall)
            androidx.compose.foundation.layout.FlowRow(
                mainAxisSpacing = 4.dp,
                crossAxisSpacing = 4.dp
            ) {
                currentProgramFilters.forEach { tag ->
                    androidx.compose.material3.Chip(
                        onClick = { /* Maybe remove single tag? */ },
                        label = { Text(tag, style = MaterialTheme.typography.labelSmall) },
                        // colors = ChipDefaults.secondaryChipColors() // Example
                    )
                }
            }
            TextButton(onClick = { programsViewModel.updateSelectedProgramFilterTags(emptySet()) }) {
              Text("Clear", style = MaterialTheme.typography.labelSmall)
            }
          }
        }
      }

      item {
        Row(
          modifier = Modifier.fillMaxWidth(),
          horizontalArrangement = Arrangement.SpaceBetween,
          verticalAlignment = Alignment.CenterVertically,
        ) {
          IconButton(onClick = { setMesocycleVisible(!mesocycleVisible) }) {
            Icon(
              imageVector = getExpandCollapseIcon(mesocycleVisible),
              contentDescription = "Show more mesocycles",
            )
          }
          Text(text = "Mesocycle (###)")
          IconButton(onClick = { /*TODO*/ }) {
            Icon(
              imageVector = Icons.Outlined.MoreVert,
              contentDescription = "Localized Description",
            )
          }
        }
      }

      item {
        Row(
          modifier = Modifier.fillMaxWidth(),
          horizontalArrangement = Arrangement.SpaceBetween,
          verticalAlignment = Alignment.CenterVertically,
        ) {
          IconButton(onClick = { setWorkoutVisible(!workoutVisible) }) {
            Icon(
              imageVector = getExpandCollapseIcon(workoutVisible),
              contentDescription = "Show more workouts",
            )
          }
          val workoutTemplates by programsViewModel.workoutTemplates.collectAsState()
          Text(text = "Workouts (${workoutTemplates.size})", modifier = Modifier.weight(1f)) // Allow text to take space before filter icon

          // Workout Filter Button
          var showWorkoutFilterDialog by rememberSaveable { mutableStateOf(false) }
          IconButton(onClick = { showWorkoutFilterDialog = true }) {
            Icon(
              imageVector = Icons.Default.FilterList,
              contentDescription = "Filter Workouts"
            )
          }
          if (showWorkoutFilterDialog) {
            val allWorkoutTags by programsViewModel.allWorkoutTags.collectAsState()
            val currentWorkoutFilters by programsViewModel.selectedWorkoutFilterTags.collectAsState()
            var selectedTagsForFilter by remember(currentWorkoutFilters) { mutableStateOf(currentWorkoutFilters) }

            AlertDialog(
              onDismissRequest = { showWorkoutFilterDialog = false },
              title = { Text("Filter Workouts by Tags") },
              text = {
                LazyColumn(modifier = Modifier.heightIn(max = 300.dp)) {
                  items(allWorkoutTags.sorted().distinct()) { tag ->
                    val isSelected = selectedTagsForFilter.contains(tag)
                    Row(
                      Modifier
                        .fillMaxWidth()
                        .clickable {
                          selectedTagsForFilter = if (isSelected) {
                            selectedTagsForFilter - tag
                          } else {
                            selectedTagsForFilter + tag
                          }
                        }
                        .padding(vertical = 4.dp),
                      verticalAlignment = Alignment.CenterVertically
                    ) {
                      Checkbox(checked = isSelected, onCheckedChange = null)
                      Text(tag, modifier = Modifier.padding(start = 8.dp))
                    }
                  }
                }
              },
              confirmButton = {
                Button(onClick = {
                  programsViewModel.updateSelectedWorkoutFilterTags(selectedTagsForFilter)
                  showWorkoutFilterDialog = false
                }) { Text("Apply") }
              },
              dismissButton = {
                Button(onClick = { showWorkoutFilterDialog = false }) { Text("Cancel") }
              }
            )
          }

          IconButton(onClick = { /*TODO More Workout Options*/ }) {
            Icon(
              imageVector = Icons.Outlined.MoreVert,
              contentDescription = "Localized Description",
            )
          }
        }
      }

      // Display Active Workout Filters
      item {
        val currentWorkoutFilters by programsViewModel.selectedWorkoutFilterTags.collectAsState()
        if (currentWorkoutFilters.isNotEmpty()) {
          Row(
            modifier = Modifier.padding(vertical = 4.dp, horizontal = 8.dp).fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(4.dp)
          ) {
            Text("Active Filters:", style = MaterialTheme.typography.labelSmall)
            androidx.compose.foundation.layout.FlowRow(
                mainAxisSpacing = 4.dp,
                crossAxisSpacing = 4.dp
            ) {
                currentWorkoutFilters.forEach { tag ->
                    Chip(
                        onClick = { /* Maybe remove single tag? */ },
                        label = { Text(tag, style = MaterialTheme.typography.labelSmall) },
                    )
                }
            }
            TextButton(onClick = { programsViewModel.updateSelectedWorkoutFilterTags(emptySet()) }) {
              Text("Clear", style = MaterialTheme.typography.labelSmall)
            }
          }
        }
      }

      val workoutTemplates by programsViewModel.workoutTemplates.collectAsState()
      items(workoutTemplates, key = { it.lazyListItemID }) { workoutTemplate ->
        AnimatedVisibility(
          visible = workoutVisible,
          enter = expandVertically(animationSpec = tween(500)),
          exit = shrinkVertically(animationSpec = tween(500)),
        ) {
          WorkoutCard(workout = workoutTemplate, onClick = {
            navController.navigate(Screens.Workout.baseRoute + "/${workoutTemplate.id.toHexString()}")
          })
        }
      }
    }
  }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun WorkoutSearchBar() {
  var text by rememberSaveable { mutableStateOf("") }
  var active by rememberSaveable { mutableStateOf(false) }

  SearchBar(
    inputField = {
      SearchBarDefaults.InputField(
        query = text,
        onQueryChange = {
          text = it
          // TODO filterWorkouts("%$it%")
          // TODO filterPrograms("%$it%")
        },
        onSearch = { active = false },
        expanded = active,
        onExpandedChange = { active = false },
        enabled = true,
        placeholder = {
          Text("Search Programs", color = MaterialTheme.colorScheme.onSecondaryContainer)
        },
        leadingIcon = {
          Icon(
            Icons.Default.Search,
            contentDescription = null,
            tint = MaterialTheme.colorScheme.onSecondaryContainer,
          )
        },
        trailingIcon = null,
        interactionSource = null,
      )
    },
    expanded = active,
    onExpandedChange = { active = false },
    modifier = Modifier.fillMaxWidth(),
    windowInsets = WindowInsets(0.dp),
  ) {}
}

// @Preview
@Composable
fun WorkoutCard(workout: com.example.myapplication.common.domain.model.Workout, onClick: () -> Unit) {
    val padding = 8.dp
    val titleMedium = MaterialTheme.typography.titleMedium
    Card(
        modifier = Modifier
            .padding(vertical = padding)
            .fillMaxWidth() // Ensure it takes full width
            .clickable(onClick = onClick)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp), // Consistent padding
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.Top,
        ) {
            Column(modifier = Modifier.weight(1f)) { // Allow text to take space
                Text(text = workout.name, style = titleMedium)
                Text(text = workout.workoutAuthor ?: "Unknown Author", style = MaterialTheme.typography.titleSmall)
                // Display a few tags if available
                if (workout.tags.isNotEmpty()) {
                    Text(
                        text = "Tags: ${workout.tags.take(3).joinToString(", ")}${if (workout.tags.size > 3) "..." else ""}",
                        style = MaterialTheme.typography.bodySmall,
                        maxLines = 1
                    )
                }
                // Text(text = "Last performed: Date", style = MaterialTheme.typography.bodySmall) // Placeholder
            }
            // IconButton for options - can be added later if needed
            // IconButton(onClick = { /* TODO: Options for workout template */ }) {
            //    Icon(imageVector = Icons.Outlined.MoreVert, contentDescription = "Options")
            // }
        }
    }
  }
}

@Composable
fun ProgramCard(
  program: Program,
  onNavigate: () -> Unit,
  programDetailsOptionsDropdownMenu: @Composable () -> Unit = {}
) {
  val padding = 16.dp
  val titleMedium = MaterialTheme.typography.titleMedium

  Card(
    modifier =
      Modifier
        .padding(vertical = 8.dp)
        .clickable {
          // Navigate to Program Details Screen
          onNavigate()
        }
  ) {
//    Row(
//      modifier = Modifier
//        .fillMaxWidth()
//        .padding(padding),
//      horizontalArrangement = Arrangement.Center,
//      verticalAlignment = Alignment.CenterVertically,
//    ) {
//      Icon(
//        imageVector = Icons.Outlined.BrokenImage,
//        contentDescription = "Missing Image",
//        tint = MaterialTheme.colorScheme.primary,
//      )
//    }
    Row(
      modifier = Modifier
        .fillMaxWidth()
        .padding(start = 16.dp, top = 16.dp, bottom = 16.dp, end = 0.dp),
      horizontalArrangement = Arrangement.SpaceBetween,
      verticalAlignment = Alignment.Top,
    ) {
      Column(
        modifier = Modifier.weight(1f),
      ) {
        Text(text = program.name ?: "New Program", style = titleMedium)
        Text(text = program.author ?: "", style = MaterialTheme.typography.bodyMedium)
      }
      if (program.isActive) {
        Box(
          modifier = Modifier
            .clip(CircleShape)
            .background(MaterialTheme.colorScheme.tertiaryContainer)
//            .padding(4.dp)
        ) {
          Text(
            text = "Active", style = MaterialTheme.typography.titleSmallEmphasized,
            modifier = Modifier.padding(vertical = 4.dp, horizontal = 16.dp),
            color = MaterialTheme.colorScheme.onTertiaryContainer
          )
        }
      }
      programDetailsOptionsDropdownMenu()
    }
  }
}
