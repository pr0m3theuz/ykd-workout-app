package com.example.myapplication.data.services

import com.example.myapplication.data.model.User

interface AuthenticationService {
  fun getCurrentUser(): User
  fun authenticatedUser(username: String, password: String): Boolean
  fun logout()
}

class AuthenticationServiceImpl() : AuthenticationService {
  private var _user: User? = null

  override fun getCurrentUser(): User {
    return _user ?: User(name = "DEMO", username = "DEMO", email = "demo@localhost", password = "")
  }

  override fun authenticatedUser(username: String, password: String): Boolean {
    return true
  }

  override fun logout() {
    _user = null
  }

}