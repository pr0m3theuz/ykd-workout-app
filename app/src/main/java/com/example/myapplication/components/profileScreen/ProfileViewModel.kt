package com.example.myapplication.components.profileScreen

import androidx.lifecycle.ViewModel
import com.example.myapplication.data.model.GymCatUser
import com.example.myapplication.data.network.UserRepository
import io.realm.kotlin.mongodb.User

class ProfileViewModel constructor(private val userRepository: UserRepository) : ViewModel() {
  val user: GymCatUser = userRepository.getUser() ?: GymCatUser().apply {
  }

  val currentUser: User? = userRepository.getCurrentUser()

}
