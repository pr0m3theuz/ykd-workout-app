package com.example.myapplication.common.infrastructure.services

import com.example.myapplication.common.domain.model.User

interface AuthenticationService {
  fun getCurrentUser(): User
  fun authenticatedUser(username: String, password: String): Boolean
  fun logout()
}

class AuthenticationServiceImpl() : AuthenticationService {
  private var _user: User? = null

  override fun getCurrentUser(): User {
    return _user ?: User(name = "user", username = "user", email = "", password = "password")
  }

  override fun authenticatedUser(username: String, password: String): Boolean {
    return true
  }

  override fun logout() {
    _user = null
  }

}