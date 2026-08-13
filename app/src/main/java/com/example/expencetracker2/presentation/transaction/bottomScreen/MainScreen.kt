package com.example.expencetracker2.presentation.transaction.bottomScreen

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.rememberDatePickerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.expencetracker2.presentation.navigation.Routes
import com.example.expencetracker2.presentation.transaction.TransactionViewModel
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainScreen(
    onAddClick: () -> Unit,
    onCategorySelected: (Int) -> Unit,
    onQuickSaveClick: (amount: Double, masterCategoryId: Long, popularCategoryId: Long?, regularCategoryId: Long?, note: String?, paymentMode: String) -> Unit,
    onCustomizeClick: () -> Unit,
    transactionViewModel: TransactionViewModel,
    onPremiumUserDashBoardClick: () -> Unit
) {
    // प्रीमियम कलर्स
    val PremiumBg = Color(0xFFF8F9FA)
    val PremiumSurface = Color(0xFFFFFFFF)
    val PremiumTextGray = Color(0xFF6C757D)
    val PremiumPrimary = Color(0xFF007AFF)

    val screens = listOf(
        Screen.Home,
        Screen.Details,
        Screen.Analytics,
        Screen.Budget
    )

    // 1. Swiping handle करने के लिए Pager State और Coroutine Scope
    val pagerState = rememberPagerState(pageCount = { screens.size })
    val coroutineScope = rememberCoroutineScope()

    // 2. Details Screen States
    var searchQuery by rememberSaveable { mutableStateOf("") }
    var selectedCategoryText by rememberSaveable { mutableStateOf("All") }
    var selectedMethodText by rememberSaveable { mutableStateOf("All") }
    val datePickerState = rememberDatePickerState()
    var showDatePicker by rememberSaveable { mutableStateOf(false) }

    // 3. Analytics Screen States


    Scaffold(
        containerColor = PremiumBg,
        bottomBar = {
            Box {
                NavigationBar(
                    modifier = Modifier.height(95.dp),
                    containerColor = PremiumSurface,
                    tonalElevation = 0.dp
                ) {
                    screens.forEachIndexed { index, screen ->
                        val isSelected = pagerState.currentPage == index
                        NavigationBarItem(
                            selected = isSelected,
                            onClick = {
                                // Bottom Item पर क्लिक करने पर स्मूथ स्वाइप
                                coroutineScope.launch {
                                    pagerState.animateScrollToPage(index)
                                }
                            },
                            icon = {
                                Icon(
                                    imageVector = if (isSelected) screen.selectedIcon else screen.unselectedIcon,
                                    contentDescription = null,
                                    modifier = Modifier
                                        .size(26.dp)
                                        .offset(y = 4.dp)
                                )
                            },
                            label = {
                                Text(
                                    text = screen.title,
                                    fontSize = 12.sp,
                                    fontWeight = if (isSelected) FontWeight.SemiBold else FontWeight.Medium,
                                    modifier = Modifier.offset(y = (-2).dp)
                                )
                            },
                            alwaysShowLabel = true,
                            colors = NavigationBarItemDefaults.colors(
                                selectedIconColor = PremiumPrimary,
                                selectedTextColor = PremiumPrimary,
                                unselectedIconColor = PremiumTextGray,
                                unselectedTextColor = PremiumTextGray,
                                indicatorColor = Color.Transparent
                            )
                        )
                    }
                }
            }
        }
    ) { innerPadding ->
        // 4. HorizontalPager से स्क्रीन अगल-बगल स्वाइप होंगी
        HorizontalPager(
            state = pagerState,
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) { page ->
            when (page) {
                0 -> {
                    HomeScreen(
                        onAddClick = onAddClick,
                        onCategorySelected = onCategorySelected,
                        onQuickSaveClick = onQuickSaveClick,
                        onCustomizeClick = onCustomizeClick,
                        onPremiumUserDashBoardClick = onPremiumUserDashBoardClick
                    )
                }
                1 -> {
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .background(PremiumBg)
                    ) {
                        DetailsScreen(
                            searchQuery = searchQuery,
                            selectedCategoryText = selectedCategoryText,
                            selectedMethodText = selectedMethodText,
                            datePickerState = datePickerState,
                            showDatePicker = showDatePicker,
                            transactionViewModel = transactionViewModel,
                            onSearchQueryChange = { newQuery -> searchQuery = newQuery },
                            onClearSearchClick = { searchQuery = "" },
                            onDateChipClick = { showDatePicker = true },
                            onDatePickerDismiss = { showDatePicker = false },
                            onDatePickerConfirm = { millis ->
                                datePickerState.selectedDateMillis = millis
                                showDatePicker = false
                            },
                            onDatePickerClearFilter = {
                                datePickerState.selectedDateMillis = null
                                showDatePicker = false
                            },
                            onCategorySelect = { category -> selectedCategoryText = category },
                            onMethodSelect = { method -> selectedMethodText = method }
                        )
                    }
                }
                2 -> {
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .background(PremiumBg)
                    ) {
                        AnalysisScreen(
                            getPieChartSlices = transactionViewModel::getPieChartSlices,
                            onSliceSelect = { slice -> transactionViewModel.selectSlice(slice) },
                            transactionViewModel = transactionViewModel
                        )
                    }
                }
                3 -> {
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .background(PremiumBg)
                    ) {
                        BudgetScreen()
                    }
                }
            }
        }
    }
}