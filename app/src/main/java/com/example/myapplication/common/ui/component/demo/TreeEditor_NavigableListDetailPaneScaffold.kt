package com.example.myapplication.common.ui.component.demo
/*

import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowRight
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.Description
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateMapOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.myapplication.common.ui.theme.MyApplicationTheme

// ===============================================================================================
// 1. Data Models (Based on User-Provided Classes)
// ===============================================================================================

private data class Program(
  val id: String = "program-1",
  var name: String = "Strength & Hypertrophy",
  var author: String = "John Doe",
  var shortDescription: String = "A 12-week program for building muscle and strength.",
  var description: String =
    "This comprehensive program is designed to maximize gains in both muscle size and strength through a combination of compound lifts and accessory work.",
  var programLength: String = "12 weeks",
  var uri: String = "https://example.com",
  var level: String = "Intermediate",
  var programEquipment: String = "Full Gym",
  var daysPerWeek: String = "4",
  var mesocycles: List<Mesocycle> = listOf(),
)

private data class Mesocycle(
  val id: String,
  var name: String,
  var description: String,
  var order: Int,
  var microcycles: List<Microcycle> = listOf(),
)

private data class Microcycle(
  val id: String,
  var name: String,
  var description: String,
  var order: Int,
  var workouts: List<Workout>? = null,
  var advancedWorkouts: List<AdvancedWorkout>? = null,
)

private data class Workout(
  val id: String,
  var name: String,
  var day: Int,
  var notes: String,
  var exercisesSets: List<com.example.myapplication.common.ui.component.ExerciseSets> = listOf(),
)

private data class AdvancedWorkout(
  val id: String,
  var name: String,
  var day: Int,
  var notes: String,
  var exercisesSets: List<com.example.myapplication.common.ui.component.ExerciseSets> = listOf(),
  var advancedExercisesSets: List<AdvancedExerciseSets> = listOf(),
)

private data class ExerciseSets(
  val id: String,
  var name: String,
  var notes: String,
  var sets: Int,
  var repetitions: Int? = null,
  var setType: String = "Normal",
  var targetWeightPounds: Float? = null,
  var targetWeightKilograms: Float? = null,
  var weightIncrementPounds: Float? = null,
  var weightIncrementKilograms: Float? = null,
  var targetRepetitionsInReserve: Int? = null,
  var targetRatePerceivedEffort: Int? = null,
  var percentage1RM: Float? = null,
  var progressionSchemeID: String? = null,
  var minRepetitions: Int? = null,
  var maxRepetitions: Int? = null,
)

private data class AdvancedExerciseSets(
  val id: String,
  var name: String,
  var description: String,
  var notes: List<String>,
  var sets: Int? = null,
  var setType: Int = 1,
  var repetitions: List<Int>? = null,
  var targetRepetitionsInReserve: List<Int>? = null,
  var targetRatePerceivedEffort: List<Int>? = null,
  var weightIncrementPounds: Float? = null,
  var weightIncrementKilograms: Float? = null,
  var targetWeightPounds: List<Float>? = null,
  var targetWeightKilograms: List<Float>? = null,
  var percentage1RM: List<Float>? = null,
  var progressionSchemeID: String? = null,
  var minRepetitions: Int? = null,
  var maxRepetitions: Int? = null,
  var setScheme: List<com.example.myapplication.common.ui.component.SetScheme> = listOf(),
)

private data class SetScheme(
  var sets: Int,
  var setType: Int = 1,
  var repetitions: List<Int>,
  var targetWeightPounds: List<Float>,
  var targetWeightKilograms: List<Float>,
  var weightIncrementPounds: Float? = null,
  var weightIncrementKilograms: Float? = null,
  var targetRepetitionsInReserve: List<Int>,
  var targetRatePerceivedEffort: List<Int>,
  var percentage1RM: List<Float>,
  var progressionSchemeID: String? = null,
  var minRepetitions: Int? = null,
  var maxRepetitions: Int? = null,
)

// A generic sealed class to hold any type of node data for our tree
private sealed class TreeNodeData {
  data class ProgramNode(val data: com.example.myapplication.common.ui.component.Program) : com.example.myapplication.common.ui.component.TreeNodeData()

  data class MesocycleNode(val data: Mesocycle) : com.example.myapplication.common.ui.component.TreeNodeData()

  data class MicrocycleNode(val data: Microcycle) : com.example.myapplication.common.ui.component.TreeNodeData()

  data class WorkoutNode(val data: Workout) : com.example.myapplication.common.ui.component.TreeNodeData()

  data class AdvancedWorkoutNode(val data: AdvancedWorkout) : com.example.myapplication.common.ui.component.TreeNodeData()

  data class ExerciseSetsNode(val data: com.example.myapplication.common.ui.component.ExerciseSets) : com.example.myapplication.common.ui.component.TreeNodeData()

  data class AdvancedExerciseSetsNode(val data: AdvancedExerciseSets) : com.example.myapplication.common.ui.component.TreeNodeData()
}

// ===============================================================================================
// 2. Main Composable & State Management
// ===============================================================================================

// TODO make tree items selectable
@Composable
private fun TreeEditorApp(program: com.example.myapplication.common.ui.component.Program) {
  // State to hold the currently selected node for editing
  var selectedNode by remember { mutableStateOf<com.example.myapplication.common.ui.component.TreeNodeData?>(null) }
  // State to hold the IDs of the expanded nodes
  val expandedNodes = remember { mutableStateMapOf<String, Boolean>() }

  // A function to get a unique ID for a node, crucial for tracking state
  fun getNodeId(node: com.example.myapplication.common.ui.component.TreeNodeData): String {
    return when (node) {
      is TreeNodeData.ProgramNode -> node.data.id
      is TreeNodeData.MesocycleNode -> node.data.id
      is TreeNodeData.MicrocycleNode -> node.data.id
      is TreeNodeData.WorkoutNode -> node.data.id
      is TreeNodeData.AdvancedWorkoutNode -> node.data.id
      is TreeNodeData.ExerciseSetsNode -> node.data.id
      is TreeNodeData.AdvancedExerciseSetsNode -> node.data.id
    }
  }

  // A recursive function to build the tree list for our LazyColumn
  fun buildTreeList(node: com.example.myapplication.common.ui.component.TreeNodeData, level: Int): List<Pair<com.example.myapplication.common.ui.component.TreeNodeData, Int>> {
    val list = mutableListOf(node to level)
    if (expandedNodes[getNodeId(node)] == true) {
      val children =
        when (node) {
          is TreeNodeData.ProgramNode -> node.data.mesocycles.map { _root_ide_package_.com.example.myapplication.common.ui.component.TreeNodeData.MesocycleNode(it) }
          is TreeNodeData.MesocycleNode ->
            node.data.microcycles.map { _root_ide_package_.com.example.myapplication.common.ui.component.TreeNodeData.MicrocycleNode(it) }
          is TreeNodeData.MicrocycleNode -> {
            val workouts = node.data.workouts?.map { _root_ide_package_.com.example.myapplication.common.ui.component.TreeNodeData.WorkoutNode(it) } ?: emptyList()
            val advancedWorkouts =
              node.data.advancedWorkouts?.map { _root_ide_package_.com.example.myapplication.common.ui.component.TreeNodeData.AdvancedWorkoutNode(it) }
                ?: emptyList()
            workouts + advancedWorkouts
          }
          is TreeNodeData.WorkoutNode ->
            node.data.exercisesSets.map { _root_ide_package_.com.example.myapplication.common.ui.component.TreeNodeData.ExerciseSetsNode(it) }
          is TreeNodeData.AdvancedWorkoutNode -> {
            val exerciseSets = node.data.exercisesSets.map { _root_ide_package_.com.example.myapplication.common.ui.component.TreeNodeData.ExerciseSetsNode(it) }
            val advancedExerciseSets =
              node.data.advancedExercisesSets.map { _root_ide_package_.com.example.myapplication.common.ui.component.TreeNodeData.AdvancedExerciseSetsNode(it) }
            exerciseSets + advancedExerciseSets
          }
          is TreeNodeData.ExerciseSetsNode -> emptyList() // Leaf node
          is TreeNodeData.AdvancedExerciseSetsNode -> emptyList() // Leaf node
        }
      children.forEach { child -> list.addAll(buildTreeList(child, level + 1)) }
    }
    return list
  }

  // Convert the top-level Program to a tree list for the UI
  val treeList = buildTreeList(_root_ide_package_.com.example.myapplication.common.ui.component.TreeNodeData.ProgramNode(program), 0)
  //  Log.d("TreeEditorApp", "Tree List: ${treeList.forEach { getNodeId(it.first) }}")

  // Main layout: a responsive Row that changes to a Column on smaller screens
  Row(modifier = Modifier.fillMaxSize()) {
    // Tree View Pane
    Box(modifier = Modifier.weight(1f).fillMaxHeight().padding(16.dp)) {
      TreePane(
        treeList = treeList,
        expandedNodes = expandedNodes,
        onNodeClicked = { node, isExpanded ->
          Log.d("TreePane", "Clicked Node: ${getNodeId(node)}, expanded: $isExpanded")
          when (isExpanded) {
            true -> {
              expandedNodes[getNodeId(node)] = true
              Log.d("TreeEditorApp", "Collapsed Nodes: ${getNodeId(node)}")
            }
            false -> {
              expandedNodes[getNodeId(node)] = false
              Log.d("TreeEditorApp", "Expanded Nodes: ${getNodeId(node)}")
            }
          }
        },
        onNodeSelected = { selectedNode = it },
        selectedNode = selectedNode,
      )
    }

    // Editing Pane
    selectedNode?.let {
      Card(
        modifier = Modifier.weight(1f).fillMaxHeight().padding(16.dp)
        //          .background(Color.LightGray)
      ) {
        EditingPane(
          node = it,
          onSave = { updatedNode ->
            // In a real app, this is where you'd update your database or repository.
            // Here, we'll just update the local state.
            when (updatedNode) {
              is TreeNodeData.ProgramNode -> program.name = updatedNode.data.name
              is TreeNodeData.MesocycleNode ->
                program.mesocycles.find { it.id == updatedNode.data.id }?.name =
                  updatedNode.data.name
              // ... and so on for all types
              else -> {}
            }
            selectedNode = null // Hide editing pane after saving
          },
          onCancel = {
            selectedNode = null // Hide editing pane on cancel
          },
        )
      }
    }
  }
}

// ===============================================================================================
// 3. Tree View Pane Composable
// ===============================================================================================
@Composable
private fun TreePane(
  treeList: List<Pair<com.example.myapplication.common.ui.component.TreeNodeData, Int>>,
  expandedNodes: Map<String, Boolean>,
  onNodeClicked: (com.example.myapplication.common.ui.component.TreeNodeData, Boolean) -> Unit,
  onNodeSelected: (com.example.myapplication.common.ui.component.TreeNodeData) -> Unit,
  selectedNode: com.example.myapplication.common.ui.component.TreeNodeData?,
) {
  LazyColumn(modifier = Modifier.fillMaxSize()) {
    items(treeList) { (node, level) ->
      val nodeId =
        when (node) {
          is TreeNodeData.ProgramNode -> node.data.id
          is TreeNodeData.MesocycleNode -> node.data.id
          is TreeNodeData.MicrocycleNode -> node.data.id
          is TreeNodeData.WorkoutNode -> node.data.id
          is TreeNodeData.AdvancedWorkoutNode -> node.data.id
          is TreeNodeData.ExerciseSetsNode -> node.data.id
          is TreeNodeData.AdvancedExerciseSetsNode -> node.data.id
        }

      val isExpanded = expandedNodes[nodeId] ?: false
      //      var isExpanded by remember { mutableStateOf(false) }
      val hasChildren =
        when (node) {
          is TreeNodeData.ProgramNode -> node.data.mesocycles.isNotEmpty()
          is TreeNodeData.MesocycleNode -> node.data.microcycles.isNotEmpty()
          is TreeNodeData.MicrocycleNode ->
            (node.data.workouts?.isNotEmpty() ?: false) ||
              (node.data.advancedWorkouts?.isNotEmpty() ?: false)
          is TreeNodeData.WorkoutNode -> node.data.exercisesSets.isNotEmpty()
          is TreeNodeData.AdvancedWorkoutNode ->
            (node.data.exercisesSets.isNotEmpty()) || (node.data.advancedExercisesSets.isNotEmpty())
          is TreeNodeData.ExerciseSetsNode,
          is TreeNodeData.AdvancedExerciseSetsNode -> false
        }
      val backgroundColor =
        if (selectedNode == node) MaterialTheme.colorScheme.primaryContainer else Color.Transparent

      Row(
        modifier =
          Modifier.fillMaxWidth()
            .background(backgroundColor)
            .clickable {
              onNodeSelected(node)
              //              if (hasChildren) {
              //                onNodeClicked(node, !isExpanded)
              //                Log.d("TreePane", "Clicked Node: $nodeId, expanded: $isExpanded")
              //              }
            }
            .padding(start = 16.dp * level),
        verticalAlignment = Alignment.CenterVertically,
      ) {
        if (hasChildren) {
          //          Log.d("TreePane", "Has Children: $nodeId, true")
          Icon(
            imageVector =
              if (isExpanded) Icons.Default.ArrowDropDown else Icons.AutoMirrored.Filled.ArrowRight,
            contentDescription = if (isExpanded) "Collapse" else "Expand",
            modifier = Modifier.clickable { onNodeClicked(node, !isExpanded) },
          )
        } else {
          Spacer(modifier = Modifier.width(24.dp))
        }

        // Example icon, could be a folder for a parent node or a file for a leaf node
        Icon(
          imageVector = Icons.Default.Description,
          contentDescription = "Node Icon",
          modifier = Modifier.size(24.dp),
        )

        Spacer(modifier = Modifier.width(8.dp))

        val displayName =
          when (node) {
            is TreeNodeData.ProgramNode -> node.data.name
            is TreeNodeData.MesocycleNode -> node.data.name
            is TreeNodeData.MicrocycleNode -> node.data.name
            is TreeNodeData.WorkoutNode -> node.data.name
            is TreeNodeData.AdvancedWorkoutNode -> node.data.name
            is TreeNodeData.ExerciseSetsNode -> node.data.name
            is TreeNodeData.AdvancedExerciseSetsNode -> node.data.name
          }

        Text(text = displayName, modifier = Modifier.padding(vertical = 8.dp))
      }
    }
  }
}

// ===============================================================================================
// 4. Editing Pane Composable
// ===============================================================================================
@Composable
private fun EditingPane(node: com.example.myapplication.common.ui.component.TreeNodeData, onSave: (com.example.myapplication.common.ui.component.TreeNodeData) -> Unit, onCancel: () -> Unit) {
  var isEditing by remember { mutableStateOf(false) }

  // In-place editable state for a simple String property (e.g., name)
  var editedName by remember {
    mutableStateOf(
      when (node) {
        is TreeNodeData.ProgramNode -> node.data.name
        is TreeNodeData.MesocycleNode -> node.data.name
        is TreeNodeData.MicrocycleNode -> node.data.name
        is TreeNodeData.WorkoutNode -> node.data.name
        is TreeNodeData.AdvancedWorkoutNode -> node.data.name
        is TreeNodeData.ExerciseSetsNode -> node.data.name
        is TreeNodeData.AdvancedExerciseSetsNode -> node.data.name
      }
    )
  }

  Column(modifier = Modifier.padding(16.dp)) {
    // Title and Edit button
    Row(
      modifier = Modifier.fillMaxWidth(),
      horizontalArrangement = Arrangement.SpaceBetween,
      verticalAlignment = Alignment.CenterVertically,
    ) {
      Text(text = "Editing Pane", style = MaterialTheme.typography.headlineSmall)
      IconButton(onClick = { isEditing = true }) {
        Icon(imageVector = Icons.Default.Edit, contentDescription = "Edit")
      }
    }

    Spacer(modifier = Modifier.height(16.dp))

    // Dynamic content based on the node type
    when (node) {
      is TreeNodeData.ProgramNode ->
        ProgramEditor(
          program = node.data,
          isEditing = isEditing,
          onSave = { updatedProgram -> onSave(_root_ide_package_.com.example.myapplication.common.ui.component.TreeNodeData.ProgramNode(updatedProgram)) },
          onCancel = { isEditing = false },
        )
      is TreeNodeData.MesocycleNode ->
        MesocycleEditor(
          mesocycle = node.data,
          isEditing = isEditing,
          onSave = { updatedMesocycle -> onSave(_root_ide_package_.com.example.myapplication.common.ui.component.TreeNodeData.MesocycleNode(updatedMesocycle)) },
          onCancel = { isEditing = false },
        )
      is TreeNodeData.MicrocycleNode ->
        MicrocycleEditor(
          microcycle = node.data,
          isEditing = isEditing,
          onSave = { updatedMicrocycle -> onSave(_root_ide_package_.com.example.myapplication.common.ui.component.TreeNodeData.MicrocycleNode(updatedMicrocycle)) },
          onCancel = { isEditing = false },
        )
      is TreeNodeData.WorkoutNode ->
        WorkoutEditor(
          workout = node.data,
          isEditing = isEditing,
          onSave = { updatedWorkout -> onSave(_root_ide_package_.com.example.myapplication.common.ui.component.TreeNodeData.WorkoutNode(updatedWorkout)) },
          onCancel = { isEditing = false },
        )
      is TreeNodeData.AdvancedWorkoutNode ->
        AdvancedWorkoutEditor(
          advancedWorkout = node.data,
          isEditing = isEditing,
          onSave = { updatedAdvancedWorkout ->
            onSave(_root_ide_package_.com.example.myapplication.common.ui.component.TreeNodeData.AdvancedWorkoutNode(updatedAdvancedWorkout))
          },
          onCancel = { isEditing = false },
        )
      is TreeNodeData.ExerciseSetsNode ->
        ExerciseSetsEditor(
          exerciseSets = node.data,
          isEditing = isEditing,
          onSave = { updatedExerciseSets ->
            onSave(_root_ide_package_.com.example.myapplication.common.ui.component.TreeNodeData.ExerciseSetsNode(updatedExerciseSets))
          },
          onCancel = { isEditing = false },
        )
      is TreeNodeData.AdvancedExerciseSetsNode ->
        AdvancedExerciseSetsEditor(
          advancedExerciseSets = node.data,
          isEditing = isEditing,
          onSave = { updatedAdvancedExerciseSets ->
            onSave(_root_ide_package_.com.example.myapplication.common.ui.component.TreeNodeData.AdvancedExerciseSetsNode(updatedAdvancedExerciseSets))
          },
          onCancel = { isEditing = false },
        )
    }
  }
}

// -----------------------------------------------------------------------------------------------
//  Individual Editors for each data class
// -----------------------------------------------------------------------------------------------

@Composable
private fun ProgramEditor(
  program: com.example.myapplication.common.ui.component.Program,
  isEditing: Boolean,
  onSave: (com.example.myapplication.common.ui.component.Program) -> Unit,
  onCancel: () -> Unit,
) {
  var editedProgram by remember { mutableStateOf(program.copy()) }
  var editedName by remember { mutableStateOf(program.name) }

  Column {
    if (isEditing) {
      TextField(value = editedProgram.name, onValueChange = { editedProgram = editedProgram.copy(name = it) }, label = { Text("Name") }, modifier = Modifier.fillMaxWidth())
      TextField(value = editedProgram.author, onValueChange = { editedProgram = editedProgram.copy(author = it) }, label = { Text("Author") }, modifier = Modifier.fillMaxWidth())
      TextField(value = editedProgram.shortDescription, onValueChange = { editedProgram = editedProgram.copy(shortDescription = it) }, label = { Text("Short Description") }, modifier = Modifier.fillMaxWidth())
      TextField(value = editedProgram.description, onValueChange = { editedProgram = editedProgram.copy(description = it) }, label = { Text("Description") }, modifier = Modifier.fillMaxWidth())
      TextField(value = editedProgram.programLength, onValueChange = { editedProgram = editedProgram.copy(programLength = it) }, label = { Text("Program Length") }, modifier = Modifier.fillMaxWidth())
      TextField(value = editedProgram.uri, onValueChange = { editedProgram = editedProgram.copy(uri = it) }, label = { Text("URI") }, modifier = Modifier.fillMaxWidth())
      TextField(value = editedProgram.level, onValueChange = { editedProgram = editedProgram.copy(level = it) }, label = { Text("Level") }, modifier = Modifier.fillMaxWidth())
      TextField(value = editedProgram.programEquipment, onValueChange = { editedProgram = editedProgram.copy(programEquipment = it) }, label = { Text("Program Equipment") }, modifier = Modifier.fillMaxWidth())
      TextField(value = editedProgram.daysPerWeek, onValueChange = { editedProgram = editedProgram.copy(daysPerWeek = it) }, label = { Text("Days Per Week") }, modifier = Modifier.fillMaxWidth())

      Row(horizontalArrangement = Arrangement.End, modifier = Modifier.fillMaxWidth().padding(top = 16.dp)) {
        Button(onClick = onCancel) { Text("Cancel") }
        Spacer(modifier = Modifier.width(8.dp))
        Button(onClick = { onSave(editedProgram) }) { Text("Save") }
      }

    } else {
      Text("Name: ${program.name}")
      Text("Author: ${program.author}")
      Text("Short Description: ${program.shortDescription}")
      Text("Description: ${program.description}")
      Text("Program Length: ${program.programLength}")
      Text("URI: ${program.uri}")
      Text("Level: ${program.level}")
      Text("Program Equipment: ${program.programEquipment}")
      Text("Days Per Week: ${program.daysPerWeek}")
    }
  }
}

@Composable
private fun MesocycleEditor(
  mesocycle: Mesocycle,
  isEditing: Boolean,
  onSave: (Mesocycle) -> Unit,
  onCancel: () -> Unit,
) {
  var editedMesocycle by remember { mutableStateOf(mesocycle.copy()) }
  var editedName by remember { mutableStateOf(mesocycle.name) }

  Column {
    if (isEditing) {
      TextField(
        value = editedName,
        onValueChange = { editedName = it },
        label = { Text("Mesocycle Name") },
        modifier = Modifier.fillMaxWidth(),
      )
      TextField(
        value = editedMesocycle.description,
        onValueChange = { editedMesocycle = editedMesocycle.copy(description = it) },
        label = { Text("Description") },
        modifier = Modifier.fillMaxWidth(),
      )
      // TODO Enable reordering
//      TextField(
//        value = editedMesocycle.order.toString(),
//        onValueChange = { editedMesocycle = editedMesocycle.copy(order = it.toIntOrNull() ?: 0) },
//        label = { Text("Order") },
//        modifier = Modifier.fillMaxWidth(),
//      )
      // TODO Enable adding/removing/reordering microcycles
      Spacer(modifier = Modifier.height(8.dp))
      Row(horizontalArrangement = Arrangement.End, modifier = Modifier.fillMaxWidth()) {
        Button(onClick = { onCancel() }) { Text("Cancel") }
        Spacer(modifier = Modifier.width(8.dp))
        Button(onClick = { onSave(mesocycle.copy(name = editedName)) }) { Text("Save") }
      }
    } else {
      Text("Name: ${mesocycle.name}")
      Text("Description: ${mesocycle.description}")
    }
  }
}

@Composable
private fun MicrocycleEditor(
  microcycle: Microcycle,
  isEditing: Boolean,
  onSave: (Microcycle) -> Unit,
  onCancel: () -> Unit,
) {
  var editedName by remember { mutableStateOf(microcycle.name) }

  Column {
    if (isEditing) {
      TextField(
        value = editedName,
        onValueChange = { editedName = it },
        label = { Text("Microcycle Name") },
        modifier = Modifier.fillMaxWidth(),
      )
      TextField(
        value = microcycle.description,
        onValueChange = { editedName = it },
        label = { Text("Description") },
        modifier = Modifier.fillMaxWidth(),
      )
      // TODO Enable adding/removing/reordering workouts
      Spacer(modifier = Modifier.height(8.dp))
      Row(horizontalArrangement = Arrangement.End, modifier = Modifier.fillMaxWidth()) {
        Button(onClick = { onCancel() }) { Text("Cancel") }
        Spacer(modifier = Modifier.width(8.dp))
        Button(onClick = { onSave(microcycle.copy(name = editedName)) }) { Text("Save") }
      }
    } else {
      Text("Name: ${microcycle.name}")
      Text("Description: ${microcycle.description}")
    }
  }
}

@Composable
private fun WorkoutEditor(
  workout: Workout,
  isEditing: Boolean,
  onSave: (Workout) -> Unit,
  onCancel: () -> Unit,
) {
  var editedName by remember { mutableStateOf(workout.name) }

  Column {
    if (isEditing) {
      TextField(
        value = editedName,
        onValueChange = { editedName = it },
        label = { Text("Workout Name") },
        modifier = Modifier.fillMaxWidth(),
      )
      Spacer(modifier = Modifier.height(8.dp))
      Row(horizontalArrangement = Arrangement.End, modifier = Modifier.fillMaxWidth()) {
        Button(onClick = { onCancel() }) { Text("Cancel") }
        Spacer(modifier = Modifier.width(8.dp))
        Button(onClick = { onSave(workout.copy(name = editedName)) }) { Text("Save") }
      }
    } else {
      Text("Name: ${workout.name}")
      Text("Day: ${workout.day}")
      Text("Notes: ${workout.notes}")
    }
  }
}

@Composable
private fun AdvancedWorkoutEditor(
  advancedWorkout: AdvancedWorkout,
  isEditing: Boolean,
  onSave: (AdvancedWorkout) -> Unit,
  onCancel: () -> Unit,
) {
  var editedName by remember { mutableStateOf(advancedWorkout.name) }

  Column {
    if (isEditing) {
      TextField(
        value = editedName,
        onValueChange = { editedName = it },
        label = { Text("Advanced Workout Name") },
        modifier = Modifier.fillMaxWidth(),
      )
      Spacer(modifier = Modifier.height(8.dp))
      Row(horizontalArrangement = Arrangement.End, modifier = Modifier.fillMaxWidth()) {
        Button(onClick = { onCancel() }) { Text("Cancel") }
        Spacer(modifier = Modifier.width(8.dp))
        Button(onClick = { onSave(advancedWorkout.copy(name = editedName)) }) { Text("Save") }
      }
    } else {
      Text("Name: ${advancedWorkout.name}")
      Text("Day: ${advancedWorkout.day}")
      Text("Notes: ${advancedWorkout.notes}")
    }
  }
}

@Composable
private fun ExerciseSetsEditor(
  exerciseSets: com.example.myapplication.common.ui.component.ExerciseSets,
  isEditing: Boolean,
  onSave: (com.example.myapplication.common.ui.component.ExerciseSets) -> Unit,
  onCancel: () -> Unit,
) {
  var editedExerciseSets by remember { mutableStateOf(exerciseSets.copy()) }
  var editedName by remember { mutableStateOf(exerciseSets.name) }

  Column {
    if (isEditing) {
      TextField(
        value = editedName,
        onValueChange = { editedName = it },
        label = { Text("ExerciseSets Name") },
        modifier = Modifier.fillMaxWidth(),
      )
      // TODO Enable adding/removing/reordering sets
      Spacer(modifier = Modifier.height(8.dp))
      Row(horizontalArrangement = Arrangement.End, modifier = Modifier.fillMaxWidth()) {
        Button(onClick = { onCancel() }) { Text("Cancel") }
        Spacer(modifier = Modifier.width(8.dp))
        Button(onClick = { onSave(exerciseSets.copy(name = editedName)) }) { Text("Save") }
      }
    } else {
      Text("Name: ${exerciseSets.name}")
      Text("Sets: ${exerciseSets.sets}")
      Text("Reps: ${exerciseSets.repetitions}")
    }
  }
}

@Composable
private fun AdvancedExerciseSetsEditor(
  advancedExerciseSets: AdvancedExerciseSets,
  isEditing: Boolean,
  onSave: (AdvancedExerciseSets) -> Unit,
  onCancel: () -> Unit,
) {
  var editedName by remember { mutableStateOf(advancedExerciseSets.name) }

  Column {
    if (isEditing) {
      TextField(
        value = editedName,
        onValueChange = { editedName = it },
        label = { Text("Advanced ExerciseSets Name") },
        modifier = Modifier.fillMaxWidth(),
      )
      // TODO Enable adding/removing/reordering sets
      Spacer(modifier = Modifier.height(8.dp))
      Row(horizontalArrangement = Arrangement.End, modifier = Modifier.fillMaxWidth()) {
        Button(onClick = { onCancel() }) { Text("Cancel") }
        Spacer(modifier = Modifier.width(8.dp))
        Button(onClick = { onSave(advancedExerciseSets.copy(name = editedName)) }) { Text("Save") }
      }
    } else {
      Text("Name: ${advancedExerciseSets.name}")
      Text("Description: ${advancedExerciseSets.description}")
    }
  }
}

// -----------------------------------------------------------------------------------------------
//  Preview Composable
// -----------------------------------------------------------------------------------------------
@Preview(showBackground = true)
@Composable
fun PreviewTreeEditorApp() {
  // Create some sample data for the preview
  val sampleProgram =
    _root_ide_package_.com.example.myapplication.common.ui.component.Program(
      mesocycles =
        listOf(
          Mesocycle(
            id = "meso-1",
            name = "Mesocycle 1: Base",
            description = "Focus on base strength",
            order = 1,
            microcycles =
              listOf(
                Microcycle(
                  id = "micro-1-1",
                  name = "Microcycle 1.1",
                  description = "Week 1",
                  order = 1,
                  workouts =
                    listOf(
                      Workout(
                        id = "workout-1-1-1",
                        name = "Workout A",
                        day = 1,
                        notes = "Heavy compound lifts",
                        exercisesSets =
                          listOf(
                            _root_ide_package_.com.example.myapplication.common.ui.component.ExerciseSets(
                              id = "ex-1",
                              name = "Squats",
                              notes = "5x5",
                              sets = 5,
                              repetitions = 5,
                            )
                          ),
                      )
                    ),
                ),
                Microcycle(
                  id = "micro-1-2",
                  name = "Microcycle 1.2",
                  description = "Week 2",
                  order = 2,
                  advancedWorkouts =
                    listOf(
                      AdvancedWorkout(
                        id = "adv-workout-1-2-1",
                        name = "Workout B",
                        day = 2,
                        notes = "Accessory work",
                        advancedExercisesSets =
                          listOf(
                            AdvancedExerciseSets(
                              id = "adv-ex-1",
                              name = "Bicep Curls",
                              description = "Isolation work",
                              notes = listOf("Superset with triceps extensions"),
                              sets = 3,
                              repetitions = listOf(10, 10, 10),
                              setScheme =
                                listOf(
                                  _root_ide_package_.com.example.myapplication.common.ui.component.SetScheme(
                                    sets = 3,
                                    repetitions = listOf(10, 10, 10),
                                    targetWeightPounds = listOf(20f, 25f, 30f),
                                    targetWeightKilograms = listOf(9.1f, 11.3f, 13.6f),
                                    targetRepetitionsInReserve = listOf(2, 2, 1),
                                    targetRatePerceivedEffort = listOf(7, 8, 9),
                                    percentage1RM = listOf(60f, 65f, 70f),
                                  )
                                ),
                            )
                          ),
                      )
                    ),
                ),
              ),
          ),
          Mesocycle(
            id = "meso-2",
            name = "Mesocycle 2: Peak",
            description = "Focus on 1RM attempts",
            order = 2,
          ),
        )
    )

  MyApplicationTheme {
    Scaffold { paddingValues ->
      Column(modifier = Modifier.padding(paddingValues)) { TreeEditorApp(program = sampleProgram) }
    }
  }
}
*/
