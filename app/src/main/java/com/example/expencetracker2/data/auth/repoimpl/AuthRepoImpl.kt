package com.example.expencetracker2.data.auth.repoimpl

import com.example.expencetracker2.domain.auth.repository.AuthRepo
import com.example.expencetracker2.domain.transaction.model.MyUserModel
import com.example.expencetracker2.domain.util.ResultState
import io.github.jan.supabase.SupabaseClient
import io.github.jan.supabase.auth.auth
import io.github.jan.supabase.auth.providers.builtin.Email
import jakarta.inject.Inject

class AuthRepoImpl @Inject constructor(
    private val supabase: SupabaseClient
) : AuthRepo {
    override suspend fun registerUser(email: String, password: String): ResultState<MyUserModel> {
        return try {
            supabase.auth.signUpWith(provider = Email) {
                this.email = email.trim()
                this.password = password.trim()
            }
            val user = supabase.auth.currentUserOrNull()
            val myUser = MyUserModel(id = user?.id ?: "", email = user?.email ?: "")
            ResultState.Success(myUser)
        }  catch (e : Exception) {
            ResultState.Error(e.localizedMessage ?: "An unknown error occurred")
        }
    }

    override suspend fun loginUser(email: String, password: String): ResultState<Unit> {
        return try {
            supabase.auth.signInWith(provider = Email){
                this.email = email.trim()
                this.password = password.trim()
            }

            ResultState.Success(Unit)
        } catch (e: Exception) {
            ResultState.Error(e.localizedMessage ?: "An unknown error occurred")
        }
    }

}