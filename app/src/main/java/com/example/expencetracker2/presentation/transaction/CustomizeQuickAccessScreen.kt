package com.example.expencetracker2.presentation.transaction

import android.annotation.SuppressLint
import android.util.Log.e
import android.widget.Toast
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.GridItemSpan
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Category
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.expencetracker2.UserPreferences
import com.example.expencetracker2.data.tracsaction.local.entity.SubCategoryEntity
import com.example.expencetracker2.data.tracsaction.local.seed.DatabaseSeedData.MASTER_CATEGORIES
import com.example.expencetracker2.data.tracsaction.local.seed.DatabaseSeedData.POPULAR_CATEGORIES
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch

// ==========================================
// MAIN SCREEN COMPOSE
// ==========================================
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CustomizeQuickAccessScreen(
    onBackClick: () -> Unit = {},
) {
    var searchQuery by remember { mutableStateOf("") }
    var selectedCategoryFilter by remember { mutableStateOf<Long?>(null) }
    val selectedItemIds = remember { mutableStateListOf<Long>() }
    val maxSelectionLimit = 12
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    val userPrefs = remember { UserPreferences(context) }

    // Saved preferences se pehle se selected items load karna
    LaunchedEffect(Unit) {
        val savedIds = userPrefs.selectedCategoryIds.first()
        selectedItemIds.clear()
        selectedItemIds.addAll(savedIds)
    }

    // Filter Logic
    val filteredSubCategories = remember(searchQuery, selectedCategoryFilter) {
        POPULAR_CATEGORIES.filter { sub ->
            val matchesCategory = selectedCategoryFilter == null || sub.masterCategoryId == selectedCategoryFilter
            val matchesSearch = sub.name.contains(searchQuery, ignoreCase = true)
            matchesCategory && matchesSearch
        }
    }

    // Grouping
    val groupedCategories = remember(filteredSubCategories) {
        MASTER_CATEGORIES.mapNotNull { master ->
            val subs = filteredSubCategories.filter { it.masterCategoryId == master.id }
            if (subs.isNotEmpty()) master to subs else null
        }
    }

    Scaffold(
        contentWindowInsets = WindowInsets.navigationBars,
        topBar = {
            TopAppBar(
                title = {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Text(
                            text = "Quick Access",
                            fontWeight = FontWeight.Bold,
                            fontSize = 18.sp
                        )
                        Surface(
                            shape = RoundedCornerShape(12.dp),
                            color = if (selectedItemIds.size == maxSelectionLimit)
                                MaterialTheme.colorScheme.errorContainer
                            else MaterialTheme.colorScheme.primaryContainer
                        ) {
                            Text(
                                text = "${selectedItemIds.size}/$maxSelectionLimit",
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold,
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                                color = if (selectedItemIds.size == maxSelectionLimit)
                                    MaterialTheme.colorScheme.onErrorContainer
                                else MaterialTheme.colorScheme.onPrimaryContainer
                            )
                        }
                    }
                },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Back"
                        )
                    }
                },
                actions = {
                    // 🔥 0 item selected hone par bhi Save button hamesha clickable rahega
                    TextButton(
                        onClick = {
                            scope.launch {
                                try {
                                    // 0 selection hone par empty list pass hogi
                                    userPrefs.saveCategoryIds(selectedItemIds.toList())
                                    Toast.makeText(context, "Quick access updated", Toast.LENGTH_SHORT).show()
                                    onBackClick()
                                } catch (e: Exception) {
                                    e("CustomizeQuickAccess", "Failed to save preferences", e)
                                }
                            }
                        }
                    ) {
                        Text(text = "Save", fontWeight = FontWeight.Bold, fontSize = 16.sp)
                    }
                }
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            // 1. Search Bar
            OutlinedTextField(
                value = searchQuery,
                onValueChange = { searchQuery = it },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 6.dp),
                placeholder = { Text("Search Swiggy, Petrol, Netflix...") },
                leadingIcon = { Icon(Icons.Default.Search, contentDescription = "Search") },
                trailingIcon = {
                    if (searchQuery.isNotEmpty()) {
                        IconButton(onClick = { searchQuery = "" }) {
                            Icon(Icons.Default.Close, contentDescription = "Clear")
                        }
                    }
                },
                singleLine = true,
                shape = RoundedCornerShape(14.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = MaterialTheme.colorScheme.primary,
                    unfocusedBorderColor = MaterialTheme.colorScheme.outline.copy(alpha = 0.3f)
                )
            )

            // 2. Horizontal Filter Category Chips
            LazyRow(
                contentPadding = PaddingValues(horizontal = 16.dp, vertical = 6.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                item {
                    FilterChip(
                        selected = selectedCategoryFilter == null,
                        onClick = { selectedCategoryFilter = null },
                        label = { Text("All", fontWeight = FontWeight.SemiBold) },
                        shape = RoundedCornerShape(20.dp)
                    )
                }

                items(MASTER_CATEGORIES) { master ->
                    val isSelected = selectedCategoryFilter == master.id
                    FilterChip(
                        selected = isSelected,
                        onClick = {
                            selectedCategoryFilter = if (isSelected) null else master.id
                        },
                        label = { Text(master.name.substringBefore(" ")) },
                        shape = RoundedCornerShape(20.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            // 3. Main 3-Column Grid Layout
            LazyVerticalGrid(
                columns = GridCells.Fixed(3),
                modifier = Modifier.fillMaxSize(),
                contentPadding = PaddingValues(start = 12.dp, end = 12.dp, top = 8.dp, bottom = 100.dp),
                horizontalArrangement = Arrangement.spacedBy(10.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                if (groupedCategories.isEmpty()) {
                    item(span = { GridItemSpan(this.maxLineSpan) }) {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(32.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = "No items found matching '$searchQuery'",
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                } else {
                    groupedCategories.forEach { (masterCategory, subCategories) ->
                        // Header Span
                        item(span = { GridItemSpan(this.maxLineSpan) }) {
                            Text(
                                text = "${masterCategory.iconName} ${masterCategory.name}",
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.primary,
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(top = 12.dp, bottom = 4.dp, start = 4.dp)
                            )
                        }

                        // Grid Items
                        items(subCategories, key = { it.id }) { subItem ->
                            val isSelected = selectedItemIds.contains(subItem.id)

                            GridSubCategoryCard(
                                item = subItem,
                                isSelected = isSelected,
                                onSelectToggle = {
                                    if (isSelected) {
                                        selectedItemIds.remove(subItem.id)
                                    } else {
                                        if (selectedItemIds.size < maxSelectionLimit) {
                                            selectedItemIds.add(subItem.id)
                                        }
                                    }
                                }
                            )
                        }
                    }
                }
            }
        }
    }
}

// ==========================================
// 3-COLUMN GRID ITEM CARD
// ==========================================
@SuppressLint("DiscouragedApi", "LocalContextResourcesRead")
@Composable
fun GridSubCategoryCard(
    item: SubCategoryEntity,
    isSelected: Boolean,
    onSelectToggle: () -> Unit
) {
    val context = LocalContext.current

    val drawableId = remember(item.iconName) {
        context.resources.getIdentifier(
            item.iconName,
            "drawable",
            context.packageName
        )
    }

    Surface(
        onClick = onSelectToggle,
        shape = RoundedCornerShape(16.dp),
        color = if (isSelected)
            MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.5f)
        else MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.3f),
        border = if (isSelected)
            BorderStroke(1.5.dp, MaterialTheme.colorScheme.primary)
        else null,
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(
            modifier = Modifier.padding(10.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Box(contentAlignment = Alignment.TopEnd) {
                val itemColor = parseHexColor(item.colorHex)
                Box(
                    modifier = Modifier
                        .size(46.dp)
                        .clip(CircleShape)
                        .background(itemColor.copy(alpha = 0.2f)),
                    contentAlignment = Alignment.Center
                ) {
                    if (drawableId != 0) {
                        Icon(
                            painter = painterResource(id = drawableId),
                            contentDescription = item.name,
                            tint = Color.Unspecified,
                            modifier = Modifier.size(26.dp)
                        )
                    } else {
                        Icon(
                            imageVector = Icons.Default.Category,
                            contentDescription = item.name,
                            tint = parseHexColor(item.colorHex),
                            modifier = Modifier.size(24.dp)
                        )
                    }
                }

                if (isSelected) {
                    Box(
                        modifier = Modifier
                            .size(18.dp)
                            .clip(CircleShape)
                            .background(MaterialTheme.colorScheme.primary),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.Check,
                            contentDescription = "Selected",
                            tint = Color.White,
                            modifier = Modifier.size(12.dp)
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = item.name,
                fontSize = 12.sp,
                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                textAlign = TextAlign.Center,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis,
                color = MaterialTheme.colorScheme.onSurface,
                lineHeight = 15.sp
            )
        }
    }
}

fun parseHexColor(hexColor: String): Color {
    val cleanHex = hexColor.replace("#", "")
    val colorInt = cleanHex.toLongOrNull(16) ?: 0xFF808080
    return if (cleanHex.length == 6) {
        Color(colorInt or 0xFF000000)
    } else {
        Color(colorInt)
    }
}