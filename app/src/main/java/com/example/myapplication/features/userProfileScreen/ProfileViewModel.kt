package com.example.myapplication.features.userProfileScreen

import androidx.lifecycle.ViewModel
import com.example.myapplication.common.infrastructure.persistence.UserRepository


class ProfileViewModel constructor(private val userRepository: UserRepository) : ViewModel() {
// TODO REWRITE THIS
//  val currentUser: User = userRepository.getCurrentUser()
val currentUser: String? = null

}
