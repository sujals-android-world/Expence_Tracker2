package com.example.expencetracker2.presentation.auth


data class SignUpState(
    val success : Boolean = false,
    val error : String? = null,
    var loading : Boolean = false
)

data class SignInState(
    val success : Boolean = false,
    val error : String? = null,
    var loading : Boolean = false
)