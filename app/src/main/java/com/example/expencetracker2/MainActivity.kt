package com.example.expencetracker2

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.navigation.compose.rememberNavController
import com.example.expencetracker2.presentation.auth.AuthViewModel
import com.example.expencetracker2.presentation.navigation.AppNavigation
import com.example.expencetracker2.presentation.transaction.TransactionViewModel
import com.example.expencetracker2.presentation.ui.theme.ExpenceTracker2Theme
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    private val authViewModel: AuthViewModel by viewModels()
    private val TransactionViewModel: TransactionViewModel by viewModels()


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            val navHostController = rememberNavController()
            ExpenceTracker2Theme {
                AppNavigation(
                    authViewModel = authViewModel,
                    navHostController = navHostController,
                    transactionViewModel = TransactionViewModel
                )
            }
        }
    }
}