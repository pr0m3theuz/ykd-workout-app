package com.example.myapplication.common.infrastructure.services

import com.example.myapplication.common.domain.model.UserProfile
import org.bson.types.ObjectId

interface AuthenticationService {
  fun getCurrentUser(): UserProfile
  fun authenticatedUser(username: String, password: String): Boolean
  fun logout()
}

class AuthenticationServiceImpl() : AuthenticationService {
  private var _user: UserProfile? = null

  override fun getCurrentUser(): UserProfile {
    return _user ?: UserProfile(
      id = ObjectId(),
      name = "user",
      username = "user",
      email = "",
      password = "password",
      authToken = "",
      isLoggedIn = true
    )
  }

  override fun authenticatedUser(username: String, password: String): Boolean {
    return true
  }

  override fun logout() {
    _user = null
  }

}