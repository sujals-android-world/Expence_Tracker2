package com.example.expencetracker2.presentation.transaction.bottomScreen

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.rememberDatePickerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.example.expencetracker2.presentation.navigation.Routes

@Composable
fun MainScreen(
    onAddClick: () -> Unit,
    onCategorySelected: (Int) -> Unit,
    onQuickSaveClick: (amount: Double, masterCategoryId: Long, note : String?, paymentMode: String) -> Unit,
    onCustomizeClick : () -> Unit
) {
    // प्रीमियम कलर्स को यहाँ भी डिफाइन किया ताकि पूरे बार में उपयोग हो सके
    val PremiumBg = Color(0xFFF8F9FA)
    val PremiumSurface = Color(0xFFFFFFFF)
    val PremiumTextDark = Color(0xFF1A1A1A)
    val PremiumTextGray = Color(0xFF6C757D)
    val PremiumBorder = Color(0xFFE9ECEF)
    val PremiumPrimary = Color(0xFF007AFF)

    val bottomNavController = rememberNavController()
    val screens = listOf(
        Screen.Home,
        Screen.Details,
        Screen.Analytics,
        Screen.Budget
    )

    Scaffold(
        containerColor = PremiumBg, // पूरी स्क्रीन का डिफ़ॉल्ट बैकग्राउंड ऑफ-व्हाइट किया
        bottomBar = {
            // टॉप पर बारीक बॉर्डर देने के लिए बॉक्स का उपयोग किया
            Box {
                NavigationBar(
                    modifier = Modifier.height(95.dp), // आपकी ओरिजिनल 95.dp हाइट वापस लगा दी
                    containerColor = PremiumSurface,   // बैकग्राउंड को प्रीमियम प्योर व्हाइट रखा
                    tonalElevation = 0.dp              // पुराना मटेरियल टोन हटाया ताकि साफ लुक मिले
                ) {
                    val navBackStackEntry by bottomNavController.currentBackStackEntryAsState()
                    val currentRoute = navBackStackEntry?.destination?.route
                    screens.forEach { screen ->
                        val isSelected = currentRoute == screen.route
                        NavigationBarItem(
                            selected = isSelected,
                            onClick = {
                                bottomNavController.navigate(screen.route) {
                                    popUpTo(bottomNavController.graph.findStartDestination().id) {
                                        saveState = true
                                    }
                                    launchSingleTop = true
                                    restoreState = true
                                }
                            },
                            icon = {
                                Icon(
                                    imageVector = if (isSelected) screen.selectedIcon else screen.unselectedIcon,
                                    contentDescription = null,
                                    modifier = Modifier
                                        .size(26.dp)            // आपका ओरिजिनल आइकन साइज
                                        .offset(y = 4.dp)       // आपका ओरिजinal आइकन ऑफसेट
                                )
                            },
                            label = {
                                Text(
                                    text = screen.title,
                                    fontSize = 12.sp,           // आपका ओरिजिनल फॉन्ट साइज
                                    fontWeight = if (isSelected) FontWeight.SemiBold else FontWeight.Medium,
                                    modifier = Modifier.offset(y = ((-2).dp)) // आपका ओरिजिनल टेक्स्ट ऑफसेट
                                )
                            },
                            alwaysShowLabel = true,
                            colors = NavigationBarItemDefaults.colors(
                                selectedIconColor = PremiumPrimary,    // सिलेक्ट होने पर आईओएस ब्लू
                                selectedTextColor = PremiumPrimary,    // सिलेक्ट होने पर टेक्स्ट भी ब्लू
                                unselectedIconColor = PremiumTextGray, // सामान्य स्थिति में सॉफ्ट ग्रे आइकन
                                unselectedTextColor = PremiumTextGray, // सामान्य स्थिति में सॉफ्ट ग्रे टेक्स्ट
                                indicatorColor = Color.Transparent     // सिलेक्टेड आइटम के पीछे का भारी बैकग्राउंड हटाया
                            )
                        )
                    }
                }
            }
        }
    ) { innerPadding ->
        NavHost(
            navController = bottomNavController,
            startDestination = Routes.Home,
            modifier = Modifier.padding(innerPadding)
        ) {
            composable(Routes.Home) {
                HomeScreen(
                    onAddClick = onAddClick,
                    onCategorySelected = onCategorySelected,
                    onQuickSaveClick = onQuickSaveClick,
                    onCustomizeClick = onCustomizeClick
                )
            }
            composable(Routes.Details) {
                Box(modifier = Modifier.fillMaxSize().background(PremiumBg)) {
                    DetailsScreen(
                        transactionsList = emptyList(),
                        searchQuery = "",
                        selectedCategoryText = "All",
                        selectedMethodText = "All",
                        datePickerState = rememberDatePickerState(),
                        showDatePicker = false,
                        onSearchQueryChange = { },
                        onClearSearchClick = { },
                        onDateChipClick = { },
                        onDatePickerDismiss = { },
                        onDatePickerConfirm = { },
                        onDatePickerClearFilter = { },
                        onCategorySelect = { },
                        onMethodSelect = { }
                    )
                }
            }
            composable(Routes.Analytics) {
                Box(modifier = Modifier.fillMaxSize().background(PremiumBg)) {
                    AnalyticsScreen()
                }
            }
            composable(Routes.Budget) {
                Box(modifier = Modifier.fillMaxSize().background(PremiumBg)) {
                    BudgetScreen()
                }
            }
        }
    }
}

@androidx.compose.ui.tooling.preview.Preview(showBackground = true)
@Composable
fun MainScreenPreview() {
    MainScreen(
        onAddClick = {},
        onCategorySelected = {},
        onQuickSaveClick = { _, _, _, _ -> },
        onCustomizeClick = {}
    )
}
