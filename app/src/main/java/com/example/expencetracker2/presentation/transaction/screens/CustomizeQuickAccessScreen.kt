package com.example.expencetracker2.presentation.transaction.screens

import android.annotation.SuppressLint
import android.util.Log.e
import android.widget.Toast
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
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
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
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
                    TextButton(
                        onClick = {
                            scope.launch {
                                try {
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
                        // Header Span with Master Category Icon
                        item(span = { GridItemSpan(this.maxLineSpan) }) {
                            val masterIconName = remember(masterCategory.iconName) {
                                masterCategory.iconName.substringBeforeLast(".").trim().lowercase()
                            }

                            val masterDrawableId = remember(masterIconName) {
                                if (masterIconName.isNotBlank()) {
                                    context.resources.getIdentifier(
                                        masterIconName,
                                        "drawable",
                                        context.packageName
                                    )
                                } else 0
                            }

                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(8.dp),
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(top = 12.dp, bottom = 4.dp, start = 4.dp)
                            ) {
                                // Master Category Icon
                                if (masterDrawableId != 0) {
                                    Image(
                                        painter = painterResource(id = masterDrawableId),
                                        contentDescription = masterCategory.name,
                                        contentScale = ContentScale.Fit,
                                        modifier = Modifier.size(20.dp)
                                    )
                                } else {
                                    Icon(
                                        imageVector = Icons.Default.Category,
                                        contentDescription = masterCategory.name,
                                        tint = MaterialTheme.colorScheme.primary,
                                        modifier = Modifier.size(20.dp)
                                    )
                                }

                                // Master Category Title
                                Text(
                                    text = masterCategory.name,
                                    fontSize = 14.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = MaterialTheme.colorScheme.primary
                                )
                            }
                        }

                        // Grid Items (as is)
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
// CLEAN CARD (TOP-END CROSS, NO ICON BG, 2-LINE TEXT WRAP)
// ==========================================
@SuppressLint("DiscouragedApi", "LocalContextResourcesRead")
@Composable
fun GridSubCategoryCard(
    item: SubCategoryEntity,
    isSelected: Boolean,
    onSelectToggle: () -> Unit
) {
    val context = LocalContext.current

    val cleanIconName = remember(item.iconName) {
        item.iconName.substringBeforeLast(".").trim().lowercase()
    }

    val drawableId = remember(cleanIconName) {
        if (cleanIconName.isNotBlank()) {
            context.resources.getIdentifier(
                cleanIconName,
                "drawable",
                context.packageName
            )
        } else 0
    }

    // Outer Box taaki Top-End chookdi badge overflow/float kar sake
    Box(
        modifier = Modifier.fillMaxWidth(),
        contentAlignment = Alignment.TopEnd
    ) {
        ElevatedCard(
            onClick = onSelectToggle,
            shape = RoundedCornerShape(12.dp),
            colors = CardDefaults.elevatedCardColors(
                containerColor = MaterialTheme.colorScheme.surface
            ),
            elevation = CardDefaults.elevatedCardElevation(
                defaultElevation = 2.dp
            ),
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 4.dp, end = 3.dp) // Space for top-end badge
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 6.dp, vertical = 8.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                // 1. Direct Icon (No Circle Background behind icon)
                if (drawableId != 0) {
                    Image(
                        painter = painterResource(id = drawableId),
                        contentDescription = item.name,
                        contentScale = ContentScale.Fit,
                        modifier = Modifier.size(15.dp)
                    )
                } else {
                    Icon(
                        imageVector = Icons.Default.Category,
                        contentDescription = item.name,
                        tint = parseHexColor(item.colorHex),
                        modifier = Modifier.size(15.dp)
                    )
                }

                // 2. Full Name with 2-Line Wrap
                Text(
                    text = item.name,
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Medium,
                    maxLines = 2,
                    lineHeight = 13.sp,
                    overflow = TextOverflow.Ellipsis,
                    color = MaterialTheme.colorScheme.onSurface,
                    modifier = Modifier.weight(1f)
                )
            }
        }

        // 3. Top-End Chookdi (Cross) Badge on Selection
        if (isSelected) {
            Box(
                modifier = Modifier
                    .size(16.dp)
                    .clip(CircleShape)
                    .background(Color(0xFF616161)), // Dark neutral cross badge
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.Close,
                    contentDescription = "Remove",
                    tint = Color.White,
                    modifier = Modifier.size(10.dp)
                )
            }
        }
    }
}

fun parseHexColor(hexColor: String): Color {
    return try {
        val cleanHex = hexColor.replace("#", "")
        val colorInt = cleanHex.toLong(16)
        if (cleanHex.length == 6) Color(colorInt or 0xFF000000) else Color(colorInt)
    } catch (e: Exception) {
        Color.Gray
    }
}