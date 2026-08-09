package com.example.expencetracker2.presentation.auth

import androidx.compose.runtime.collectAsState
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.expencetracker2.domain.auth.repository.AuthRepo
import com.example.expencetracker2.domain.util.ResultState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AuthViewModel @Inject constructor(
    private val repository: AuthRepo
) : ViewModel() {

    private val _authState = MutableStateFlow(SignUpState())
    val authState = _authState.asStateFlow()


    fun onSignUpClick(email : String,password : String) {
        if(email.isEmpty() || password.isEmpty()) {
            _authState.value =  _authState.value.copy(
                error = "Email and Password Cannot be empty",
                loading = false
            )
            return
        }

        if(password.length < 6 ) {
            _authState.value =  _authState.value.copy(
                error = "Email and Password Cannot be empty",
                loading = false
            )
            return
        }

        _authState.value =  _authState.value.copy(
            error = null,
            loading = true
        )

        viewModelScope.launch{
            val result = repository.registerUser(email,password)

            when(result) {
                is ResultState.Loading -> {
                    _authState.value = _authState.value.copy(loading = true, error = null)
                }

                is ResultState.Success -> {
                    _authState.value = _authState.value.copy(
                        loading = false,
                        error = null,
                        success = true
                    )
                }

                is ResultState.Error -> {
                    _authState.value = _authState.value.copy(
                        loading = false,
                        error = result.exception
                    )
                }

            }

        }


    }

    private val _loginAuthState = MutableStateFlow(SignInState())
    val loginAuthState: StateFlow<SignInState> = _loginAuthState.asStateFlow()

    fun onSignInClick(email : String,password : String) {
        if(email.isEmpty() || password.isEmpty()) {
            _loginAuthState.value = _loginAuthState.value.copy(
                error = "Email and Password Cannot be empty",
                loading = false
            )
            return
        }

        _loginAuthState.value = _loginAuthState.value.copy(
            error = null,
            loading = true
        )

        viewModelScope.launch {
            val result = repository.loginUser(email,password)
            when(result) {
                is ResultState.Loading -> {
                    _loginAuthState.value = _loginAuthState.value.copy(loading = true, error = null)
                }
                is ResultState.Success -> {
                    _loginAuthState.value = _loginAuthState.value.copy(
                        loading = false,
                        error = null,
                        success = true
                    )
                }
                is ResultState.Error -> {
                    _loginAuthState.value = _loginAuthState.value.copy(
                        loading = false,
                        error = result.exception
                    )
                }
            }
        }
    }


    fun resetLoginState() {
        _loginAuthState.value = SignInState()
    }




}