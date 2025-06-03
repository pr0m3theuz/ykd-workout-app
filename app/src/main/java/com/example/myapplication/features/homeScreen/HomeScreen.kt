@file:OptIn(ExperimentalMaterial3Api::class)

package com.example.myapplication.features.homeScreen

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import com.example.myapplication.common.ui.component.BottomNavigationBar
import com.example.myapplication.common.ui.theme.MyApplicationTheme

@Composable
fun HomeScreen(navController: NavHostController = rememberNavController()) {
  Scaffold(
    topBar = {
//      TopAppBar()
    },
    bottomBar = {
      BottomNavigationBar(navController = navController)
    },
    content = { contentPadding ->
      Column(
        modifier = Modifier
          .fillMaxSize()
          .padding(contentPadding),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
      ) {
/*                Image(
                    painter = painterResource(),
                    contentDescription = "Workout",
                    modifier = Modifier
                        .size(200.dp)
                        .clip(shape = MaterialTheme.shapes.medium),
                    contentScale = ContentScale.Crop
                )*/
//        Spacer(modifier = Modifier.height(16.dp))
        Text(
          text = "Welcome to YKD!",
          style = MaterialTheme.typography.headlineMedium,
          color = MaterialTheme.colorScheme.onSurface,
          textAlign = TextAlign.Center
        )
        Spacer(modifier = Modifier.height(16.dp))
        Text(
          text = "Start your fitness journey today!",
          style = MaterialTheme.typography.bodyLarge,
          color = MaterialTheme.colorScheme.onSurface
        )
        Spacer(modifier = Modifier.height(32.dp))
        Button(
          onClick = { /* Handle button click */ },
          modifier = Modifier.fillMaxWidth(0.75f)
        ) {
          Text(text = "Get Started!")
        }
      }
    }
  )
}

@Preview
@Composable
fun HomeScreenPreview() {
  MyApplicationTheme {
    HomeScreen()
  }
}
