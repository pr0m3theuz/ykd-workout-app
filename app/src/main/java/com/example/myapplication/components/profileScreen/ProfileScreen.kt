package com.example.myapplication.components.profileScreen

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.myapplication.components.BottomNavigationBar
import io.realm.kotlin.annotations.ExperimentalRealmSerializerApi
import io.realm.kotlin.mongodb.ext.profileAsBsonDocument
import org.koin.androidx.compose.koinViewModel

@OptIn(ExperimentalMaterial3Api::class, ExperimentalRealmSerializerApi::class)
@Composable
fun ProfileScreen(
  navController: NavController,
  profileViewModel: ProfileViewModel = koinViewModel(),
) {
  val user = profileViewModel.currentUser
  val userEmail = user?.profileAsBsonDocument()!!["email"]?.asString()?.value ?: "anon@anon.anon"


  Scaffold(
    topBar = {
      TopAppBar(
        title = { Text(text = "Profile") },
        actions = {
          IconButton(onClick = { /*TODO*/ }) {
            Icon(imageVector = Icons.Default.Settings, contentDescription = null)
          }
        },
      )
    },
    bottomBar = { BottomNavigationBar(navController = navController) },
    content = { paddingValues ->
      Column(modifier = Modifier
        .fillMaxSize()
        .padding(paddingValues)
        .padding(horizontal = 16.dp)) {
        Row() {
          Icon(imageVector = Icons.Default.AccountCircle, contentDescription = null)
          Text(text = user.id ?: "Anonymous User")
        }
        Text(text = userEmail)
      }
    },
  )
}
