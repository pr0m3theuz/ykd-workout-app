package com.example.myapplication.components.profileScreen

import androidx.lifecycle.ViewModel
import com.example.myapplication.data.network.UserRepository


class ProfileViewModel constructor(private val userRepository: UserRepository) : ViewModel() {
// TODO REWRITE THIS
//  val currentUser: User = userRepository.getCurrentUser()
val currentUser: String? = null

}
