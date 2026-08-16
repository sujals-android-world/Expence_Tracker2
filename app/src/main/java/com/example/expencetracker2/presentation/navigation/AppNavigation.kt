package com.example.expencetracker2.presentation.navigation

import androidx.compose.runtime.Composable
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.example.expencetracker2.presentation.auth.AuthViewModel
import com.example.expencetracker2.presentation.auth.SignInScreen
import com.example.expencetracker2.presentation.auth.SignUpScreen
import com.example.expencetracker2.presentation.premiumUserDashboard.PremiumUserDashboardViewmodel
import com.example.expencetracker2.presentation.premiumUserDashboard.screens.AddAccountScreen
import com.example.expencetracker2.presentation.premiumUserDashboard.screens.AddIncomeScreen
import com.example.expencetracker2.presentation.premiumUserDashboard.screens.PremiumUserDashBoard
import com.example.expencetracker2.presentation.premiumUserDashboard.screens.TransferScreen
import com.example.expencetracker2.presentation.transaction.TransactionViewModel
import com.example.expencetracker2.presentation.transaction.bottomScreen.MainScreen
import com.example.expencetracker2.presentation.transaction.screens.AddExpenseScreen
import com.example.expencetracker2.presentation.transaction.screens.CustomizeQuickAccessScreen


@Composable
fun AppNavigation(
    navHostController: NavHostController,
    authViewModel: AuthViewModel = hiltViewModel(),
    transactionViewModel: TransactionViewModel = hiltViewModel(),
    premiumUserDashboardViewmodel: PremiumUserDashboardViewmodel = hiltViewModel()
) {

    NavHost(navHostController, startDestination = Routes.mainScreen, builder = {
        composable(Routes.signUpScreen) {
            SignUpScreen(
                onSkipClick = {
                    navHostController.navigate(Routes.mainScreen) {
                        popUpTo(Routes.signUpScreen) { inclusive = true }
                    }
                },
                onCreateAccountClick = { email , password -> authViewModel.onSignUpClick(email,password)  },
                onGoogleClick = {  },
                onFacebookClick = {  },
                onAppleClick = {  },
                onSignInClick = {
                    navHostController.navigate(Routes.signInScreen) {
                        popUpTo(Routes.signUpScreen) { inclusive = true }
                    }
                },
                viewModel = authViewModel,
                navHostController = navHostController
            )
        }
        composable(Routes.signInScreen) {
            SignInScreen(
                onSkipClick = {
                    navHostController.navigate(Routes.mainScreen)
                    {
                        popUpTo(Routes.signInScreen)
                        {
                            inclusive = true
                        }
                    }
                 },
                onSignInClick = { email, password ->
                    authViewModel.onSignInClick(email, password)
                },
                onForgotPasswordClick = {},
                onSignUpClick = { navHostController.navigate(Routes.signUpScreen) {
                    popUpTo(Routes.signInScreen) {
                        inclusive = true
                    }
                } },
                socialLoginsContent = {},
                authViewModel,
                navHostController
            )
        }
        composable(Routes.mainScreen) {
            MainScreen(onAddClick = {
                navHostController.navigate(Routes.addExpenseScreen)
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
                navHostController.navigate(Routes.customizeQuickAccessScreen)
            },
                transactionViewModel  = transactionViewModel,
                onPremiumUserDashBoardClick = {
                    navHostController.navigate(Routes.premiumUserDashBoard)
                }
            )
        }
        composable(Routes.addExpenseScreen) {
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
        composable(Routes.customizeQuickAccessScreen) {
            CustomizeQuickAccessScreen(
                onBackClick = {
                    navHostController.navigate(Routes.mainScreen) {
                        popUpTo(Routes.customizeQuickAccessScreen) {
                            inclusive = true
                        }
                    }
                },
            )
        }
        composable(Routes.premiumUserDashBoard) {
            PremiumUserDashBoard(
                transactionViewModel = transactionViewModel,
                onAddAccountClick = {
                    navHostController.navigate(Routes.addAccountScreen)
                },
                onAccountClick = {},
                onAddIncomeClick = {
                    navHostController.navigate(Routes.addIncomeScreen)
                },
                onTransferClick = {
                    navHostController.navigate(Routes.transferScreen)
                },
                premiumUserDashboardViewmodel = premiumUserDashboardViewmodel
            )
        }
        composable(Routes.addIncomeScreen) {

            AddIncomeScreen(
                onBackClick = {
                    navHostController.navigate(Routes.premiumUserDashBoard) {
                        popUpTo(Routes.addIncomeScreen) {
                            inclusive = true
                        }
                    }
                },
                premiumUserDashboardViewmodel = premiumUserDashboardViewmodel,
                onSaveIncome = { transaction, selectedAccountId, selectedAccountNewBalance, linkedBankId, linkedBankNewBalance, childAccountsToUpdate,  ->
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
                    premiumUserDashboardViewmodel.updateAccountBalance(
                        id = selectedAccountId,
                        balance = selectedAccountNewBalance
                    )

                    // 3️⃣ अगर Child Account (Debit Card / UPI) चुना था, तो उसके Parent Bank का बैलेंस अपडेट करो
                    if (linkedBankId != null && linkedBankNewBalance != null) {
                        premiumUserDashboardViewmodel.updateAccountBalance(
                            id = linkedBankId,
                            balance = linkedBankNewBalance
                        )
                    }

                    // 4️⃣ अगर Main Bank चुना था, तो उससे लिंक्ड सभी Child Accounts (Debit Cards / UPI) का बैलेंस अपडेट करो
                    childAccountsToUpdate.forEach { (childId, childNewBalance) ->
                        premiumUserDashboardViewmodel.updateAccountBalance(
                            id = childId,
                            balance = childNewBalance
                        )
                    }

                    navHostController.popBackStack()
                },
                onNavigateToAddAccount = {
                    navHostController.navigate(Routes.premiumUserDashBoard) {
                        popUpTo(Routes.addIncomeScreen) {
                            inclusive = true
                        }
                    }
                }
            )
        }

        composable(Routes.transferScreen) {
            TransferScreen(
                premiumUserDashboardViewmodel = premiumUserDashboardViewmodel,
                onBackClick = { navHostController.popBackStack() },
                onNavigateToAddAccount = { navHostController.navigate(Routes.premiumUserDashBoard) },
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
                    premiumUserDashboardViewmodel.updateMultipleAccounts(accountsToUpdate)
                }
            )

        }

        composable(Routes.addAccountScreen){


                AddAccountScreen(
                    premiumUserDashboardViewmodel = premiumUserDashboardViewmodel,
                    onBackClick = {
                        navHostController.navigate(Routes.premiumUserDashBoard)
                    },
                    onSaveAccount = { accountName,icon, balance, accountType, isprimary,linkedBankId,creditLimit,statementDate,dueDate ->
                        premiumUserDashboardViewmodel.insertAccount(
                            name = accountName,
                            icon = icon,
                            balance = balance,
                            accountType = accountType,
                            isPrimary = isprimary,
                            linkedBankId = linkedBankId,
                            creditLimit = creditLimit,
                            statementDate = statementDate,
                            dueDate = dueDate
                        )
                        navHostController.navigate(Routes.premiumUserDashBoard) {
                            popUpTo(Routes.addAccountScreen) { inclusive = true }
                        }
                    }
                )
        }
    })
}