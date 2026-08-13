package com.example.expencetracker2.presentation.transaction.bottomScreen



import android.annotation.SuppressLint
import android.view.HapticFeedbackConstants
import android.widget.Toast
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.CameraAlt
import androidx.compose.material.icons.filled.Category
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Mic
import androidx.compose.material.icons.filled.Savings
import androidx.compose.material.icons.filled.Wallet
import androidx.compose.material3.BottomSheetDefaults
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.FloatingActionButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableLongStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.expencetracker2.R
import com.example.expencetracker2.UserPreferences
import com.example.expencetracker2.data.tracsaction.local.seed.DatabaseSeedData.POPULAR_CATEGORIES
import com.example.expencetracker2.data.tracsaction.local.seed.DatabaseSeedData.REGULAR_CATEGORIES
import kotlinx.coroutines.delay


@OptIn(ExperimentalLayoutApi::class)
@Composable
fun HomeScreen(
    onAddClick: () -> Unit,
    onCategorySelected: (Int) -> Unit,
    onCustomizeClick: () -> Unit,
    onQuickSaveClick: (amount: Double, masterCategoryId: Long, popularCategoryId: Long?, regularCategoryId: Long?, note: String?, paymentMode: String) -> Unit,
    onPremiumUserDashBoardClick: () -> Unit
) {
    val PremiumBg = Color(0xFFF8F9FA) // साफ ऑफ-व्हाइट बैकग्राउंड
    val PremiumSurface = Color(0xFFFFFFFF) // बटन्स के लिए प्योर व्हाइट
    val PremiumTextDark = Color(0xFF1A1A1A) // गहरा प्रीमियम ब्लैक
    val PremiumTextGray = Color(0xFF6C757D) // सब-टेक्स्ट के लिए हल्का ग्रे
    val PremiumBorder = Color(0xFFE9ECEF) // बहुत बारीक और हल्की बॉर्डर
    val PremiumPrimary = Color(0xFF007AFF) // + बटन के लिए iOS जैसा प्रीमियम ब्लू

    var selectedCategoryId by remember { mutableLongStateOf(0L) }
    var selectedCategoryForSheet by remember { mutableStateOf<Triple<Long, String, Boolean>?>(null) }

    val context = LocalContext.current
    val view = LocalView.current
    val userPrefs = remember { UserPreferences(context) }
    val savedIds by userPrefs.selectedCategoryIds.collectAsState(initial = emptyList())

    val userSavedPopularCategories = remember(savedIds) {
        POPULAR_CATEGORIES.filter { masterItem ->
            savedIds.contains(masterItem.id)
        }.take(12)
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(PremiumBg) // पूरी स्क्रीन का बैकग्राउंड प्रीमियम ऑफ-व्हाइट किया
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 16.dp)
                .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            Spacer(modifier = Modifier.height(2.dp))

            // 1. TOP ACTION BAR
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Surface(
                    modifier = Modifier
                        .fillMaxWidth(0.72f)
                        .height(44.dp),
                    shape = RoundedCornerShape(22.dp),
                    color = PremiumSurface, // सर्च/एक्शन बार का बैकग्राउंड प्योर व्हाइट किया
                    border = BorderStroke(1.dp, PremiumBorder), // बहुत बारीक और हल्की प्रीमियम बॉर्डर दी
                    tonalElevation = 0.dp // पुराने मटेरियल टोन को हटाया ताकि साफ लुक मिले
                ) {
                    Row(
                        modifier = Modifier.fillMaxSize(),
                        horizontalArrangement = Arrangement.SpaceEvenly,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        IconButton(onClick = {
                            view.performHapticFeedback(HapticFeedbackConstants.KEYBOARD_TAP)
                        }) {
                            Icon(
                                Icons.Default.CameraAlt,
                                contentDescription = "Camera",
                                tint = PremiumTextDark, // आइकन्स का रंग गहरा प्रीमियम ब्लैक किया
                                modifier = Modifier.size(18.dp)
                            )
                        }
                        IconButton(onClick = {
                            view.performHapticFeedback(HapticFeedbackConstants.KEYBOARD_TAP)
                        }) {
                            Icon(
                                Icons.Default.Mic,
                                contentDescription = "Voice",
                                tint = PremiumTextDark, // आइकन्स का रंग गहरा प्रीमियम ब्लैक किया
                                modifier = Modifier.size(18.dp)
                            )
                        }
                        IconButton(onClick = {
                            view.performHapticFeedback(HapticFeedbackConstants.KEYBOARD_TAP)
                            onPremiumUserDashBoardClick()
                        }) {
                            Icon(
                                Icons.Default.Wallet,
                                contentDescription = "Jar",
                                tint = PremiumTextDark, // आइकन्स का रंग गहरा प्रीमियम ब्लैक किया
                                modifier = Modifier.size(18.dp)
                            )
                        }
                    }
                }

                Box(contentAlignment = Alignment.Center) {
                    Image(
                        painter = painterResource(id = R.drawable.ic_profile),
                        contentDescription = null,
                        modifier = Modifier.size(44.dp)
                    )
                }
            }



            // 2. REGULAR EXPENSES
            Column(modifier = Modifier.fillMaxWidth()) {
                Text(
                    text = "Regular Expenses",
                    fontWeight = FontWeight.Bold,
                    fontSize = 18.sp,
                    fontFamily = FontFamily.SansSerif,
                    letterSpacing = (-0.5).sp,
                    color = PremiumTextDark
                )
                Spacer(modifier = Modifier.height(10.dp))

                FlowRow(
                    modifier = Modifier.fillMaxWidth(),
                    maxItemsInEachRow = 5,
                    horizontalArrangement = Arrangement.spacedBy(6.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    REGULAR_CATEGORIES.take(20).forEach { category ->
                        val isSelected = selectedCategoryId == category.id

                        val drawableId = remember(category.iconName) {
                            context.resources.getIdentifier(
                                category.iconName,
                                "drawable",
                                context.packageName
                            )
                        }

                        // Bounce scale animation jab icon select ho
                        val scale by animateFloatAsState(
                            targetValue = if (isSelected) 1.06f else 1.0f,
                            animationSpec = spring(
                                dampingRatio = Spring.DampingRatioMediumBouncy,
                                stiffness = Spring.StiffnessLow
                            ),
                            label = "categoryScale"
                        )

                        // Dynamic Elevation: Selected hone par zyada utha hua 3D effect
                        val elevationState by animateDpAsState(
                            targetValue = if (isSelected) 8.dp else 2.dp,
                            label = "categoryElevation"
                        )

                        Column(
                            modifier = Modifier
                                .weight(1f)
                                .clip(RoundedCornerShape(12.dp))
                                .clickable {
                                    view.performHapticFeedback(HapticFeedbackConstants.KEYBOARD_TAP)
                                    selectedCategoryId = category.id
                                    onCategorySelected(category.id.toInt())
                                    selectedCategoryForSheet = Triple(category.masterCategoryId, category.name, false)
                                }
                                .padding(vertical = 2.dp),
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.Center
                        ) {
                            // Surface me 3D Rounded Box shape & Premium Shadows
                            Surface(
                                modifier = Modifier
                                    .size(44.dp)
                                    .graphicsLayer {
                                        scaleX = scale
                                        scaleY = scale
                                    },
                                shape = RoundedCornerShape(12.dp), // Premium Rounded Square (Not Round / Not Hard Square)
                                color = if (isSelected) {
                                    PremiumPrimary
                                } else {
                                    PremiumSurface
                                },
                                border = BorderStroke(
                                    width = if (isSelected) 0.dp else 1.dp,
                                    color = PremiumBorder.copy(alpha = 0.6f)
                                ),
                                shadowElevation = elevationState, // Card/Box ko screen se upar uthane ke liye 3D shadow
                                tonalElevation = if (isSelected) 4.dp else 0.dp
                            ) {
                                Box(contentAlignment = Alignment.Center) {
                                    val iconTint = if (isSelected) {
                                        Color.White
                                    } else {
                                        PremiumTextDark
                                    }

                                    if (drawableId != 0) {
                                        Icon(
                                            painter = painterResource(id = drawableId),
                                            contentDescription = category.name,
                                            tint = iconTint,
                                            modifier = Modifier.size(22.dp)
                                        )
                                    } else {
                                        Icon(
                                            imageVector = Icons.Default.Category,
                                            contentDescription = category.name,
                                            tint = iconTint,
                                            modifier = Modifier.size(22.dp)
                                        )
                                    }
                                }
                            }

                            Spacer(modifier = Modifier.height(6.dp))

                            Text(
                                text = category.name.substringBefore(" "),
                                fontSize = 11.sp,
                                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                                color = if (isSelected) {
                                    PremiumPrimary
                                } else {
                                    PremiumTextGray
                                },
                                textAlign = TextAlign.Center,
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis
                            )
                        }
                    }

                    val regRemainder = POPULAR_CATEGORIES.take(20).size % 5
                    if (regRemainder != 0) {
                        repeat(5 - regRemainder) {
                            Spacer(modifier = Modifier.weight(1f))
                        }
                    }
                }
            }


            // 3. POPULAR CUSTOM (Har Item Individual Elevated Card With Subtle Elevation)
            if (userSavedPopularCategories.isNotEmpty()) {
                Column(modifier = Modifier.fillMaxWidth()) {
                    Text(
                        text = "Popular Custom",
                        fontWeight = FontWeight.Bold,
                        fontSize = 18.sp,
                        fontFamily = FontFamily.SansSerif,
                        letterSpacing = (-0.5).sp,
                        color = PremiumTextDark
                    )
                    Spacer(modifier = Modifier.height(10.dp))

                    FlowRow(
                        modifier = Modifier.fillMaxWidth(),
                        maxItemsInEachRow = 3,
                        horizontalArrangement = Arrangement.spacedBy(10.dp),
                        verticalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        userSavedPopularCategories.forEach { category ->
                            val isSelected = selectedCategoryId == category.id
                            val firstInt = category.id.toString().first().digitToInt().toLong()

                            // Icon fetch karne ke liye (User selected or default)
                            val drawableId = remember(category.iconName) {
                                context.resources.getIdentifier(
                                    category.iconName,
                                    "drawable",
                                    context.packageName
                                )
                            }

                            val cardScale by animateFloatAsState(
                                targetValue = if (isSelected) 1.03f else 1.0f,
                                animationSpec = spring(
                                    dampingRatio = Spring.DampingRatioMediumBouncy,
                                    stiffness = Spring.StiffnessLow
                                ),
                                label = "cardScale"
                            )

                            Card(
                                modifier = Modifier
                                    .weight(1f)
                                    .height(40.dp)
                                    .graphicsLayer {
                                        scaleX = cardScale
                                        scaleY = cardScale
                                    }
                                    .clip(RoundedCornerShape(12.dp))
                                    .clickable {
                                        view.performHapticFeedback(HapticFeedbackConstants.KEYBOARD_TAP)
                                        selectedCategoryId = category.id
                                        onCategorySelected(category.id.toInt())
                                        selectedCategoryForSheet = Triple(firstInt, category.name, true)
                                    },
                                shape = RoundedCornerShape(12.dp),
                                colors = CardDefaults.cardColors(
                                    containerColor = if (isSelected) {
                                        PremiumPrimary
                                    } else {
                                        PremiumSurface
                                    }
                                ),
                                border = if (!isSelected) BorderStroke(1.dp, PremiumBorder) else null
                            ) {
                                Row(
                                    modifier = Modifier
                                        .fillMaxSize()
                                        .padding(horizontal = 3.dp),
                                    horizontalArrangement = Arrangement.Center,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    val iconTint = if (isSelected) Color.White else Color.Unspecified

                                    // Chota Icon
                                    if (drawableId != 0) {
                                        Icon(
                                            painter = painterResource(id = drawableId),
                                            contentDescription = category.name,
                                            tint = iconTint,
                                            modifier = Modifier.size(16.dp)
                                        )
                                    } else {
                                        Icon(
                                            imageVector = Icons.Default.Category,
                                            contentDescription = category.name,
                                            tint = iconTint,
                                            modifier = Modifier.size(16.dp)
                                        )
                                    }

                                    Spacer(modifier = Modifier.width(6.dp))

                                    // Category Name
                                    Text(
                                        text = category.name.substringBefore(" "),
                                        fontSize = 11.sp,
                                        fontWeight = if (isSelected) FontWeight.Bold else FontWeight.SemiBold,
                                        color = if (isSelected) {
                                            Color.White
                                        } else {
                                            PremiumTextDark
                                        },
                                        maxLines = 1,
                                        overflow = TextOverflow.Ellipsis
                                    )
                                }
                            }
                        }

                        val popRemainder = userSavedPopularCategories.size % 3
                        if (popRemainder != 0) {
                            repeat(3 - popRemainder) {
                                Spacer(modifier = Modifier.weight(1f))
                            }
                        }
                    }
                }
            }
        }

        FloatingActionButton(
            onClick = {
                view.performHapticFeedback(HapticFeedbackConstants.KEYBOARD_TAP)
                onAddClick()
            },
            containerColor = Color(0xFF007AFF), // आपके द्वारा चुना गया आईओएस जैसा प्रीमियम ब्लू
            contentColor = Color.White,       // ब्लू बैकग्राउंड पर प्योर व्हाइट प्लस (+) आइकन
            shape = CircleShape,
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .padding(horizontal = 16.dp, vertical = 6.dp), // बॉटम बार से बेहतर दूरी के लिए वर्टिकल पैडिंग 4dp से 16dp की
            elevation = FloatingActionButtonDefaults.elevation(
                defaultElevation = 4.dp,       // पुराने भारी 20dp को हटाकर साफ और स्मूथ 4dp एलिवेशन दिया
                pressedElevation = 2.dp
            )
        ) {
            Icon(
                imageVector = Icons.Default.Add,
                contentDescription = "Add Expense",
                modifier = Modifier.size(24.dp)
            )
        }

        // 5. QUICK ADD BOTTOM SHEET
        selectedCategoryForSheet?.let { (id, name, isPopular) ->
            QuickAddExpenseBottomSheet(
                masterCategoryId = id,
                categoryName = name,
                popularCategoryId = if (isPopular) selectedCategoryId else null,
                regularCategoryId = if (!isPopular) selectedCategoryId else null,
                onDismissRequest = {
                    selectedCategoryForSheet = null
                    selectedCategoryId = 0L
                },
                onSaveClick = { amount, catId, popId, regId, note, mode ->
                    onQuickSaveClick(amount, catId, popId, regId, note, mode)
                    selectedCategoryForSheet = null
                    selectedCategoryId = 0L
                    Toast.makeText(context, "₹$amount Saved Successfully", Toast.LENGTH_SHORT).show()
                }
            )
        }
    }
}
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun QuickAddExpenseBottomSheet(
    masterCategoryId: Long,
    categoryName: String,
    popularCategoryId: Long? = null,
    regularCategoryId: Long? = null,
    onDismissRequest: () -> Unit,
    onSaveClick: (amount: Double, masterCategoryId: Long, popularCategoryId: Long?, regularCategoryId: Long?, note: String?, paymentMode: String) -> Unit
) {
    // 1. Local States
    var amount by remember { mutableStateOf("") }
    var paymentMode by remember { mutableStateOf("UPI") }
    var isAmountFocused by remember { mutableStateOf(false) }
    var note by remember { mutableStateOf("") }

    // Focus & Keyboard Managers
    val focusRequester = remember { FocusRequester() }
    val focusManager = LocalFocusManager.current

    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)

    // Common Save Action
    val submitExpense = {
        focusManager.clearFocus()
        val finalAmount = amount.toDoubleOrNull() ?: 0.0
        if (finalAmount > 0.0) {
            onSaveClick(finalAmount, masterCategoryId, popularCategoryId, regularCategoryId, note.ifBlank { null }, paymentMode)
        }
    }

    // Auto Keyboard Focus on Sheet Open
    LaunchedEffect(Unit) {
        delay(200)
        focusRequester.requestFocus()
    }

    ModalBottomSheet(
        onDismissRequest = onDismissRequest,
        sheetState = sheetState,
        containerColor = MaterialTheme.colorScheme.surface,
        dragHandle = { BottomSheetDefaults.DragHandle() }
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .navigationBarsPadding()
                .padding(horizontal = 24.dp, vertical = 16.dp)
                .clickable(
                    interactionSource = remember { MutableInteractionSource() },
                    indication = null
                ) {
                    focusManager.clearFocus()
                },
            verticalArrangement = Arrangement.spacedBy(24.dp)
        ) {
            // HEADER SECTION
            Column(modifier = Modifier.fillMaxWidth()) {
                Text(
                    text = "Adding Expense For",
                    fontSize = 12.sp,
                    color = Color.Gray,
                    fontWeight = FontWeight.Medium
                )
                Text(
                    text = categoryName,
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.primary
                )
            }

            // 💰 HERO AMOUNT INPUT (Keyboard Done button saves directly)
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier.fillMaxWidth()
            ) {
                BasicTextField(
                    value = amount,
                    onValueChange = { input ->
                        if (input.all { char -> char.isDigit() || char == '.' }) {
                            amount = input
                        }
                    },
                    modifier = Modifier
                        .focusRequester(focusRequester)
                        .onFocusChanged { focusState ->
                            isAmountFocused = focusState.isFocused
                        },
                    textStyle = TextStyle(
                        fontSize = 44.sp,
                        fontWeight = FontWeight.Black,
                        color = MaterialTheme.colorScheme.onSurface,
                        textAlign = TextAlign.Center
                    ),
                    // 🔥 Decimal Keyboard + Done Action
                    keyboardOptions = KeyboardOptions(
                        keyboardType = KeyboardType.Decimal,
                        imeAction = ImeAction.Done
                    ),
                    // 🔥 Keyboard par Right/Done tap karne par direct save
                    keyboardActions = KeyboardActions(
                        onDone = { submitExpense() }
                    ),
                    cursorBrush = SolidColor(MaterialTheme.colorScheme.primary),
                    decorationBox = { innerTextField ->
                        Row(
                            horizontalArrangement = Arrangement.Center,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text("₹ ", fontSize = 44.sp, fontWeight = FontWeight.Black, color = MaterialTheme.colorScheme.primary)

                            if (!isAmountFocused && amount.isEmpty()) {
                                Text("0.00", fontSize = 44.sp, fontWeight = FontWeight.Black, color = Color.LightGray)
                            } else {
                                innerTextField()
                            }
                        }
                    }
                )
            }

            // 💳 PAYMENT MODE SECTION
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Text(
                    text = "Select Payment Mode",
                    fontWeight = FontWeight.SemiBold,
                    fontSize = 14.sp,
                    color = Color.Gray
                )

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    val paymentOptions = listOf("UPI", "Cash", "Card")

                    paymentOptions.forEach { option ->
                        val isSelected = paymentMode == option

                        Card(
                            modifier = Modifier
                                .weight(1f)
                                .height(48.dp)
                                .clickable {
                                    focusManager.clearFocus()
                                    paymentMode = option
                                },
                            shape = RoundedCornerShape(12.dp),
                            colors = CardDefaults.cardColors(
                                containerColor = if (isSelected) {
                                    MaterialTheme.colorScheme.primaryContainer
                                } else {
                                    MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.4f)
                                }
                            ),
                            border = if (isSelected) {
                                BorderStroke(2.dp, MaterialTheme.colorScheme.primary)
                            } else {
                                null
                            }
                        ) {
                            Box(
                                modifier = Modifier.fillMaxSize(),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = option,
                                    fontSize = 14.sp,
                                    fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                                    color = if (isSelected) {
                                        MaterialTheme.colorScheme.onPrimaryContainer
                                    } else {
                                        MaterialTheme.colorScheme.onSurface
                                    }
                                )
                            }
                        }
                    }
                }
            }

            // 📝 NOTE INPUT (Keyboard Done button saves directly)
            OutlinedTextField(
                value = note,
                onValueChange = { note = it },
                label = { Text("Add a note (Optional)") },
                leadingIcon = { Icon(Icons.Default.Edit, contentDescription = null) },
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp),
                singleLine = true,
                // 🔥 Note me bhi Done action
                keyboardOptions = KeyboardOptions(
                    keyboardType = KeyboardType.Text,
                    imeAction = ImeAction.Done
                ),
                keyboardActions = KeyboardActions(
                    onDone = { submitExpense() }
                )
            )

            // 💾 SAVE BUTTON
            Button(
                onClick = { submitExpense() },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(54.dp),
                shape = RoundedCornerShape(14.dp),
                colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary)
            ) {
                Text(
                    text = "Confirm & Save",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.White
                )
            }
        }
    }
}

