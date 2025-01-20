package com.example.myapplication.data.services

import com.example.myapplication.data.model.User

interface AuthenticationService {
  fun getCurrentUser(): User
  fun authenticatedUser(username: String, password: String): Boolean
  fun logout()
}