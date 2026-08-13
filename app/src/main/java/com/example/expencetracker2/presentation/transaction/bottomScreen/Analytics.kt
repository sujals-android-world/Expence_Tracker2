package com.example.expencetracker2.presentation.transaction.bottomScreen

import android.util.Log
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalLocale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.expencetracker2.domain.transaction.model.Transaction
import com.example.expencetracker2.presentation.transaction.PieChartSlice
import com.example.expencetracker2.presentation.transaction.TransactionViewModel
import kotlin.math.atan2

private val PremiumSurfaceColor = Color(0xFFF8F9FA)
private val PremiumDarkText = Color(0xFF1E1B18)
private val EmptyChartColor = Color(0xFFD1D5DB)

@Composable
fun AnalysisScreen(
    getPieChartSlices: (List<Transaction>) -> List<PieChartSlice>,
    onSliceSelect: (PieChartSlice?) -> Unit,
    transactionViewModel: TransactionViewModel
) {
    val transactionState by transactionViewModel.allTransaction.collectAsStateWithLifecycle()
    val selectedSlice by transactionViewModel.selectedSlice.collectAsStateWithLifecycle()

    val transactions = transactionState.success

    // 🔹 Slices Calculation & Sorted Descending (Highest Expense First)
    val sortedSlices = remember(transactions) {
        getPieChartSlices(transactions)
            .filter { it.sweepAngle > 0f && !it.sweepAngle.isNaN() }
            .sortedByDescending { it.amount }
    }

    Log.d("DEBUG_EXPENSE", "Total transactions = ${transactions.size}, Sorted Slices = ${sortedSlices.size}")

    // Pager for 2 Charts Only (1. Donut Chart, 2. Weekly Activity)
    val pagerState = rememberPagerState(pageCount = { 2 })

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(PremiumSurfaceColor)
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // 1. MULTI-CHART CAROUSEL CARD
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(20.dp),
            colors = CardDefaults.cardColors(containerColor = Color.White),
            elevation = CardDefaults.cardElevation(defaultElevation = 3.dp)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 16.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                HorizontalPager(
                    state = pagerState,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(230.dp)
                ) { page ->
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        when (page) {
                            0 -> DetailedPieChart(
                                slices = sortedSlices,
                                selectedSlice = selectedSlice,
                                onSliceSelect = onSliceSelect
                            )
                            1 -> DetailedDailyTrendChart(slices = sortedSlices)
                        }
                    }
                }

                Spacer(modifier = Modifier.height(8.dp))

                // 🔹 Carousel Indicators (2 Dots)
                Row(
                    horizontalArrangement = Arrangement.spacedBy(6.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    repeat(2) { index ->
                        val isSelected = pagerState.currentPage == index
                        Box(
                            modifier = Modifier
                                .size(if (isSelected) 18.dp else 6.dp, 6.dp)
                                .clip(RoundedCornerShape(3.dp))
                                .background(if (isSelected) Color(0xFF2563EB) else Color(0xFFE2E8F0))
                        )
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(20.dp))

        Text(
            text = "Expense Breakdown",
            fontSize = 17.sp,
            fontWeight = FontWeight.Bold,
            color = PremiumDarkText,
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(12.dp))

        // 2. CATEGORY LIST (CLEAN WITHOUT RANK TAG OR CIRCLE)
        if (sortedSlices.isEmpty()) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "No expenses recorded yet!",
                    color = Color.Gray,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Medium
                )
            }
        } else {
            LazyColumn(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                items(
                    items = sortedSlices,
                    key = { it.categoryName }
                ) { slice ->
                    val isSelected = slice == selectedSlice

                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable {
                                onSliceSelect(if (isSelected) null else slice)
                            },
                        shape = RoundedCornerShape(14.dp),
                        colors = CardDefaults.cardColors(
                            containerColor = if (isSelected) slice.color.copy(alpha = 0.12f) else Color.White
                        ),
                        elevation = CardDefaults.cardElevation(defaultElevation = if (isSelected) 4.dp else 1.dp)
                    ) {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(14.dp)
                        ) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                // 🔹 Only Category Name (No rank tag, no dot)
                                Text(
                                    text = slice.categoryName,
                                    fontWeight = FontWeight.SemiBold,
                                    fontSize = 15.sp,
                                    color = Color(0xFF1F2937)
                                )

                                Text(
                                    text = "₹${String.format(LocalLocale.current.platformLocale, "%.2f", slice.amount)}",
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 15.sp,
                                    color = PremiumDarkText
                                )
                            }

                            Spacer(modifier = Modifier.height(10.dp))

                            // 🔹 Percentage Dynamic Line Bar
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                LinearProgressIndicator(
                                    progress = { (slice.percentage / 100f).coerceIn(0f, 1f) },
                                    modifier = Modifier
                                        .weight(1f)
                                        .height(7.dp)
                                        .clip(RoundedCornerShape(4.dp)),
                                    color = slice.color,
                                    trackColor = Color(0xFFF1F5F9)
                                )
                                Spacer(modifier = Modifier.width(12.dp))
                                Text(
                                    text = "${String.format(LocalLocale.current.platformLocale, "%.1f", slice.percentage)}%",
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Color(0xFF6B7280)
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

// 🔹 CHART 1: DONUT CHART
@Composable
fun DetailedPieChart(
    slices: List<PieChartSlice>,
    selectedSlice: PieChartSlice?,
    onSliceSelect: (PieChartSlice?) -> Unit
) {
    val totalExpense = remember(slices) { slices.sumOf { it.amount } }

    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
        modifier = Modifier.fillMaxSize()
    ) {
        Box(
            modifier = Modifier.size(185.dp),
            contentAlignment = Alignment.Center
        ) {
            Canvas(
                modifier = Modifier
                    .fillMaxSize()
                    .pointerInput(slices) {
                        detectTapGestures { tapOffset ->
                            if (slices.isEmpty()) return@detectTapGestures
                            val center = Offset(size.width / 2f, size.height / 2f)
                            val dx = tapOffset.x - center.x
                            val dy = tapOffset.y - center.y

                            var touchAngle = Math.toDegrees(atan2(dy.toDouble(), dx.toDouble())).toFloat()
                            if (touchAngle < 0) touchAngle += 360f

                            val clickedSlice = slices.firstOrNull { slice ->
                                val start = (slice.startAngle % 360 + 360) % 360
                                val end = start + slice.sweepAngle
                                if (end > 360) {
                                    touchAngle >= start || touchAngle <= (end % 360)
                                } else {
                                    touchAngle in start..end
                                }
                            }
                            onSliceSelect(if (selectedSlice == clickedSlice) null else clickedSlice)
                        }
                    }
            ) {
                val strokePx = 28.dp.toPx()
                val padding = strokePx / 2f
                val topLeft = Offset(padding, padding)
                val arcSize = Size(size.width - strokePx, size.height - strokePx)

                if (slices.isEmpty() || totalExpense <= 0.0) {
                    drawArc(
                        color = EmptyChartColor,
                        startAngle = 0f,
                        sweepAngle = 360f,
                        useCenter = false,
                        topLeft = topLeft,
                        size = arcSize,
                        style = Stroke(width = strokePx)
                    )
                } else {
                    slices.forEach { slice ->
                        val isSelected = slice == selectedSlice
                        val currentStroke = if (isSelected) strokePx + 10f else strokePx

                        drawArc(
                            color = slice.color,
                            startAngle = slice.startAngle,
                            sweepAngle = slice.sweepAngle,
                            useCenter = false,
                            topLeft = topLeft,
                            size = arcSize,
                            style = Stroke(width = currentStroke)
                        )
                    }
                }
            }

            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                val titleText = when {
                    slices.isEmpty() || totalExpense <= 0.0 -> "No Expense"
                    selectedSlice != null -> selectedSlice.categoryName
                    else -> "Total Expense"
                }

                val amountValue = when {
                    slices.isEmpty() || totalExpense <= 0.0 -> 0.0
                    selectedSlice != null -> selectedSlice.amount
                    else -> totalExpense
                }

                Text(
                    text = titleText,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Medium,
                    color = Color.Gray,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )

                Spacer(modifier = Modifier.height(2.dp))

                Text(
                    text = "₹${String.format(LocalLocale.current.platformLocale, "%.0f", amountValue)}",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.ExtraBold,
                    color = PremiumDarkText,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )

                if (selectedSlice != null) {
                    Text(
                        text = "${String.format(LocalLocale.current.platformLocale, "%.1f", selectedSlice.percentage)}% of total",
                        fontSize = 10.sp,
                        color = selectedSlice.color,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }
    }
}

// 🔹 CHART 2: WEEKLY ACTIVITY CHART (EXPLICIT 7 DAYS INCLUDING THURSDAY)
// 🔹 CHART 2: PREMIUM SMOOTH LINE CHART (Wave Trend Line)
@Composable
fun DetailedDailyTrendChart(slices: List<PieChartSlice>) {
    val days = listOf("Mon", "Tue", "Wed", "Thu", "Fri", "Sat", "Sun")
    val mockAmounts = if (slices.isEmpty()) listOf(0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0)
    else listOf(450.0, 1200.0, 300.0, 1800.0, 950.0, 1400.0, 800.0)

    val maxAmount = (mockAmounts.maxOrNull() ?: 1.0).coerceAtLeast(1.0)

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp, vertical = 8.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "Expense Trend",
                fontSize = 14.sp,
                fontWeight = FontWeight.Bold,
                color = PremiumDarkText
            )
            if (slices.isNotEmpty()) {
                Surface(
                    shape = RoundedCornerShape(6.dp),
                    color = Color(0xFFEFF6FF)
                ) {
                    Text(
                        text = "Weekly Activity",
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF2563EB),
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(130.dp)
        ) {
            // 🔹 Wave Curve Line & Smooth Gradient Fill
            Canvas(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(100.dp)
            ) {
                val width = size.width
                val height = size.height
                val spacing = width / (days.size - 1)

                val points = mockAmounts.mapIndexed { index, amount ->
                    val x = index * spacing
                    val y = height - ((amount / maxAmount) * (height - 20f)).toFloat() - 10f
                    Offset(x, y)
                }

                val strokePath = androidx.compose.ui.graphics.Path().apply {
                    if (points.isNotEmpty()) {
                        moveTo(points.first().x, points.first().y)
                        for (i in 0 until points.size - 1) {
                            val p1 = points[i]
                            val p2 = points[i + 1]
                            val controlX1 = p1.x + (p2.x - p1.x) / 2f
                            val controlY1 = p1.y
                            val controlX2 = p1.x + (p2.x - p1.x) / 2f
                            val controlY2 = p2.y
                            cubicTo(controlX1, controlY1, controlX2, controlY2, p2.x, p2.y)
                        }
                    }
                }

                // 🔹 Curved Blue Stroke Line
                drawPath(
                    path = strokePath,
                    color = Color(0xFF2563EB),
                    style = Stroke(width = 3.dp.toPx())
                )

                // 🔹 Data Dots on Points
                points.forEach { point ->
                    drawCircle(
                        color = Color.White,
                        radius = 5.dp.toPx(),
                        center = point
                    )
                    drawCircle(
                        color = Color(0xFF2563EB),
                        radius = 3.dp.toPx(),
                        center = point
                    )
                }
            }

            // 🔹 Bottom Days Label (Mon - Sun)
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .align(Alignment.BottomCenter),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                days.forEachIndexed { index, day ->
                    Text(
                        text = day,
                        fontSize = 11.sp,
                        color = Color.Gray,
                        fontWeight = FontWeight.Medium
                    )
                }
            }
        }
    }
}