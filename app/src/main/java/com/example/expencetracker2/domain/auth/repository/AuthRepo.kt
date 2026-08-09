package com.example.expencetracker2.domain.auth.repository

import com.example.expencetracker2.domain.transaction.model.MyUserModel
import com.example.expencetracker2.domain.util.ResultState

interface AuthRepo {
    suspend fun registerUser(email: String, password: String) : ResultState<MyUserModel>

    suspend fun loginUser(email: String, password: String) : ResultState<Unit>


}