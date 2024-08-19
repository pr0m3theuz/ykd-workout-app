package com.example.myapplication.components.profileScreen

import androidx.lifecycle.ViewModel
import com.example.myapplication.data.network.UserRepository
import io.realm.kotlin.mongodb.User

class ProfileViewModel constructor(private val userRepository: UserRepository) : ViewModel() {
  val currentUser: User = userRepository.getCurrentUser()
}
