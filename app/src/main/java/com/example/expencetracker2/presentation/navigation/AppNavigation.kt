package com.example.expencetracker2.presentation.navigation

import androidx.compose.runtime.Composable
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.example.expencetracker2.presentation.auth.AuthViewModel
import com.example.expencetracker2.presentation.auth.SignInScreen
import com.example.expencetracker2.presentation.auth.SignUpScreen
import com.example.expencetracker2.presentation.transaction.TransactionViewModel
import com.example.expencetracker2.presentation.transaction.bottomScreen.MainScreen
import com.example.expencetracker2.presentation.transaction.screens.AddExpenseScreen
import com.example.expencetracker2.presentation.transaction.screens.AddIncomeScreen
import com.example.expencetracker2.presentation.transaction.screens.CustomizeQuickAccessScreen
import com.example.expencetracker2.presentation.transaction.screens.PremiumUserDashBoard
import com.example.expencetracker2.presentation.transaction.screens.TransferScreen


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
                    navHostController.navigate(Routes.MainScreen)
                },
                onAccountClick = {  },
                onAddAccountSave = { name, icon, balance, accountType, isPrimary , linkedBankId->
                    transactionViewModel.insertAccount(name = name, balance = balance, accountType = accountType,isPrimary =  isPrimary, icon = icon,linkedBankId = linkedBankId)
                },
                onAddIncomeClick = {
                    navHostController.navigate(Routes.AddIncomescreen)
                },
                onTransferClick = {
                    navHostController.navigate(Routes.TransferScreen)
                }
            )
        }
        composable(Routes.AddIncomescreen) {

            AddIncomeScreen(
                onBackClick = {
                    navHostController.navigate(Routes.PremiumUserdashBoard) {
                        popUpTo(Routes.AddIncomescreen) {
                            inclusive = true
                        }
                    }
                },
                viewModel = transactionViewModel,
                onSaveIncome = { transaction, selectedAccountId, selectedAccountNewBalance, linkedBankId, linkedBankNewBalance, childAccountsToUpdate ->
                    // 1️⃣ Transaction save करो
                    transactionViewModel.insertTransaction(
                        transaction.amount,
                        transaction.masterCategoryId,
                        transaction.popularCategoryId,
                        transaction.regularCategoryId,
                        transaction.note,
                        transaction.paymentMode,
                        transaction.isSynced,
                        transaction.isSpeedExpense,
                        isIncome = true,
                        isExpense = false
                    )

                    // 2️⃣ Selected Account का बैलेंस अपडेट करो
                    transactionViewModel.updateAccountBalance(
                        id = selectedAccountId,
                        balance = selectedAccountNewBalance
                    )

                    // 3️⃣ अगर Child Account (Debit Card / UPI) चुना था, तो उसके Parent Bank का बैलेंस अपडेट करो
                    if (linkedBankId != null && linkedBankNewBalance != null) {
                        transactionViewModel.updateAccountBalance(
                            id = linkedBankId,
                            balance = linkedBankNewBalance
                        )
                    }

                    // 4️⃣ अगर Main Bank चुना था, तो उससे लिंक्ड सभी Child Accounts (Debit Cards / UPI) का बैलेंस अपडेट करो
                    childAccountsToUpdate.forEach { (childId, childNewBalance) ->
                        transactionViewModel.updateAccountBalance(
                            id = childId,
                            balance = childNewBalance
                        )
                    }

                    navHostController.popBackStack()
                },
                onNavigateToAddAccount = {
                    navHostController.navigate(Routes.PremiumUserdashBoard) {
                        popUpTo(Routes.AddIncomescreen) {
                            inclusive = true
                        }
                    }
                }
            )
        }

        composable(Routes.TransferScreen) {
            TransferScreen(
                viewModel = transactionViewModel,
                onBackClick = { navHostController.popBackStack() },
                onNavigateToAddAccount = { navHostController.navigate(Routes.PremiumUserdashBoard) },
                onSaveTransfer = { transaction, accountsToUpdate ->
                    // 1. Save Transaction
                    transactionViewModel.insertTransaction(
                        transaction.amount,
                        transaction.masterCategoryId,
                        transaction.popularCategoryId,
                        transaction.regularCategoryId,
                        transaction.note,
                        transaction.paymentMode,
                        transaction.isSynced,
                        transaction.isSpeedExpense,
                        isIncome = false,
                        isExpense = false,
                        isTransfer = true
                    )

                    // 2. Bulk Update all calculated Account Balances
                    transactionViewModel.updateMultipleAccounts(accountsToUpdate)
                }
            )

        }
    })
}