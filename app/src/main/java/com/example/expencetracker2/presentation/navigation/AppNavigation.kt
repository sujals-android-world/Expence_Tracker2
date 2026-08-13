package com.example.expencetracker2.presentation.navigation

import androidx.compose.runtime.Composable
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.example.expencetracker2.presentation.auth.AuthViewModel
import com.example.expencetracker2.presentation.auth.SignInScreen
import com.example.expencetracker2.presentation.auth.SignUpScreen
import com.example.expencetracker2.presentation.transaction.screens.AddExpenseScreen
import com.example.expencetracker2.presentation.transaction.screens.CustomizeQuickAccessScreen
import com.example.expencetracker2.presentation.transaction.TransactionViewModel
import com.example.expencetracker2.presentation.transaction.bottomScreen.MainScreen
import com.example.expencetracker2.presentation.transaction.screens.PremiumUserDashBoard


@Composable
fun AppNavigation(
    navHostController: NavHostController,
    authViewModel: AuthViewModel = hiltViewModel(),
    transactionViewModel: TransactionViewModel = hiltViewModel()
) {

    NavHost(navHostController, startDestination = Routes.MainScreen, builder = {
        composable(Routes.SignUpScreen) {
            SignUpScreen(
                onSkipClick = {
                    navHostController.navigate(Routes.MainScreen) {
                        popUpTo(Routes.SignUpScreen) { inclusive = true }
                    }
                },
                onCreateAccountClick = { email , password -> authViewModel.onSignUpClick(email,password)  },
                onGoogleClick = {  },
                onFacebookClick = {  },
                onAppleClick = {  },
                onSignInClick = {
                    navHostController.navigate(Routes.SignInScreen) {
                        popUpTo(Routes.SignUpScreen) { inclusive = true }
                    }
                },
                viewModel = authViewModel,
                navHostController = navHostController
            )
        }
        composable(Routes.SignInScreen) {
            SignInScreen(
                onSkipClick = {
                    navHostController.navigate(Routes.MainScreen)
                    {
                        popUpTo(Routes.SignInScreen)
                        {
                            inclusive = true
                        }
                    }
                 },
                onSignInClick = { email, password ->
                    authViewModel.onSignInClick(email, password)
                },
                onForgotPasswordClick = {},
                onSignUpClick = { navHostController.navigate(Routes.SignUpScreen) {
                    popUpTo(Routes.SignInScreen) {
                        inclusive = true
                    }
                } },
                socialLoginsContent = {},
                authViewModel,
                navHostController
            )
        }
        composable(Routes.MainScreen) {
            MainScreen(onAddClick = {
                navHostController.navigate(Routes.AddExpenseScreen)
            }
                , onCategorySelected = {}, onQuickSaveClick = { finalAmount,catId, popularId, regularId, note, mode ->
                transactionViewModel.insertTransaction(
                    amount = finalAmount,
                    popularCategoryId = popularId,
                    regularCategoryId = regularId,
                    masterCategoryId = catId,
                    note = note,
                    paymentMode = mode,
                    isSynced = false,
                    isSpeedExpense = true
                )
            }
            , onCustomizeClick = {
                navHostController.navigate(Routes.CustomizeQuickAccessScreen)
            },
                transactionViewModel  = transactionViewModel,
                onPremiumUserDashBoardClick = {
                    navHostController.navigate(Routes.PremiumUserdashBoard)
                }
            )
        }
        composable(Routes.AddExpenseScreen) {
            AddExpenseScreen(
                onBackClick = {
                    navHostController.popBackStack()
                },
                onSaveClick = { amount, popularId,regularId, masterCategoryId, note, paymentMode, isSynced, isSpeedExpense ->
                    transactionViewModel.insertTransaction(
                        amount,
                        masterCategoryId,
                        popularId,
                        regularId,
                        note,
                        paymentMode,
                        isSynced,
                        isSpeedExpense
                    )
                    navHostController.popBackStack()
                }
            )
        }
        composable(Routes.CustomizeQuickAccessScreen) {
            CustomizeQuickAccessScreen(
                onBackClick = {
                    navHostController.navigate(Routes.MainScreen) {
                        popUpTo(Routes.CustomizeQuickAccessScreen) {
                            inclusive = true
                        }
                    }
                },
            )
        }
        composable(Routes.PremiumUserdashBoard) {
            PremiumUserDashBoard(
                transactionViewModel = transactionViewModel,
                onBackClick = {
                    navHostController.popBackStack()
                },
                onAccountClick = {  },
                onAddAccountSave = { name, icon, balance, accountType, isPrimary , linkedBankId->
                    transactionViewModel.insertAccount(name = name, balance = balance, accountType = accountType,isPrimary =  isPrimary, icon = icon,linkedBankId = linkedBankId)
                }
            )
        }
    })
}