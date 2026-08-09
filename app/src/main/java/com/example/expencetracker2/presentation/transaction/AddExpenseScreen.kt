package com.example.expencetracker2.presentation.transaction

// Compose Runtime & State

// UI Layout & Modifiers

// Material Design 3 Components

// Graphics, Text Styling & Icons

// Text Inputs & Keyboard Operations
import android.widget.Toast
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
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableLongStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.graphics.toColorInt
import com.example.expencetracker2.data.tracsaction.local.seed.DatabaseSeedData.MASTER_CATEGORIES

@OptIn(ExperimentalMaterial3Api::class, ExperimentalLayoutApi::class)
    @Composable
    fun AddExpenseScreen(
        onSaveClick: (amount: Double, subCategoryId: Long?, mainCategoryId : Long, note: String?, paymentMode: String, isSynced: Boolean, isSpeedExpense: Boolean) -> Unit,
        onBackClick: () -> Unit
    ) {
        // States for inputs
        var amount by remember { mutableStateOf("") }
        var note by remember { mutableStateOf("") }
        var paymentMode by remember { mutableStateOf("UPI") }
        var isSpeedExpense by remember { mutableStateOf(false) }
        var isAmountFocused by remember { mutableStateOf(false) }
        var selectedCategoryId by remember { mutableLongStateOf(1L) }
        var selectedSubCategoryId by remember { mutableStateOf<Long?>(null) }

        // Constant values as per your requirement
        val isSynced = false

        Scaffold(
            topBar = {
                TopAppBar(
                    title = { Text("Add Expense", fontWeight = FontWeight.Bold) },
                    navigationIcon = {
                        IconButton(onClick = onBackClick) {
                            Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                        }
                    },
                    colors = TopAppBarDefaults.topAppBarColors(containerColor = MaterialTheme.colorScheme.surface)
                )
            }
        ) { paddingValues ->
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
                    .padding(16.dp)
                    .verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.spacedBy(20.dp)
            ) {

                // 💰 AMOUNT INPUT (Big & Bold Premium Look)
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("Enter Amount", style = MaterialTheme.typography.bodyMedium, color = Color.Gray)
                    Spacer(modifier = Modifier.height(8.dp))
                    BasicTextField(
                        value = amount,
                        onValueChange = { input ->
                            if (input.all { char -> char.isDigit() || char == '.' }) {
                                amount = input
                            }
                        },
                        // 🔥 यहाँ पर हम फोकस चेंज को कैप्चर कर रहे हैं
                        modifier = Modifier.onFocusChanged { focusState ->
                            isAmountFocused = focusState.isFocused
                        },
                        textStyle = TextStyle(
                            fontSize = 42.sp,
                            fontWeight = FontWeight.Black,
                            color = MaterialTheme.colorScheme.primary,
                            textAlign = TextAlign.Center
                        ),
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                        cursorBrush = SolidColor(MaterialTheme.colorScheme.primary),
                        decorationBox = { innerTextField ->
                            Row(
                                horizontalArrangement = Arrangement.Center,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text("₹ ", fontSize = 42.sp, fontWeight = FontWeight.Black, color = MaterialTheme.colorScheme.primary)

                                // 🔥 असली फिक्स: अगर फ़ील्ड पर फोकस है, तो '0.00' तुरंत छिप जाएगा, चाहे यूजर ने कुछ टाइप किया हो या नहीं!
                                if (!isAmountFocused && amount.isEmpty()) {
                                    Text("0.00", fontSize = 42.sp, fontWeight = FontWeight.Black, color = Color.LightGray)
                                } else {
                                    innerTextField() // कर्सर और यूजर का इनपुट यहाँ दिखेगा
                                }
                            }
                        }
                    )
                }

                HorizontalDivider(color = Color.LightGray.copy(alpha = 0.5f))

                // 📂 SUB-CATEGORY PICKER (Chips Layout)
                Column {
                    Text(
                        text = "Select Category",
                        fontWeight = FontWeight.SemiBold,
                        style = MaterialTheme.typography.bodyMedium,
                        color = Color.Gray
                    )
                    Spacer(modifier = Modifier.height(8.dp))

                    // ContextualFlowRow use Kia hai jo 3 columns ka automatic calculation Karena
                    FlowRow(
                        modifier = Modifier.fillMaxWidth(),
                        // maxItemsInEachRow = 3 करने से ये एक लाइन में सिर्फ 3 ही खांचे बनाएगा
                        maxItemsInEachRow = 3,
                        horizontalArrangement = Arrangement.spacedBy(6.dp),
                        verticalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        MASTER_CATEGORIES.forEach { category ->
                            val isSelected = selectedCategoryId == category.id

                            val categoryColor = remember(category.colorHex) {
                                Color(category.colorHex.toColorInt())
                            }

                            FilterChip(
                                selected = isSelected,
                                onClick = {
                                    selectedCategoryId = category.id
                                },
                                // 🔥 यहाँ Modifier.weight(1f) लगाने से 3 ही खांचे स्क्रीन की चौड़ाई में बराबर फिट हो जाएंगे
                                modifier = Modifier
                                    .weight(1f)
                                    .height(40.dp),
                                label = {
                                    Box(
                                        modifier = Modifier.fillMaxWidth(),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Text(
                                            text = category.name.substringBefore("&"),
                                            fontSize = 11.sp,
                                            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                                            textAlign = TextAlign.Center,
                                            maxLines = 1
                                        )
                                    }
                                },
                                colors = FilterChipDefaults.filterChipColors(
                                    selectedContainerColor = categoryColor,
                                    selectedLabelColor = Color.White,
                                    containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.3f),
                                    labelColor = MaterialTheme.colorScheme.onSurface
                                ),
                                border = FilterChipDefaults.filterChipBorder(
                                    enabled = true,
                                    selected = isSelected,
                                    selectedBorderColor = categoryColor,
                                    borderColor = Color.LightGray.copy(alpha = 0.2f)
                                ),
                                shape = RoundedCornerShape(10.dp)
                            )
                        }
                    }
                }

                // 💳 PAYMENT MODE (UPI vs CASH Toggle)
                Column {
                    Text("Payment Mode", fontWeight = FontWeight.SemiBold, style = MaterialTheme.typography.bodyLarge)
                    Spacer(modifier = Modifier.height(8.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        listOf("UPI", "Cash", "Card").forEach { mode ->
                            val isSelected = paymentMode == mode
                            Button(
                                onClick = { paymentMode = mode },
                                modifier = Modifier.weight(1f),
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = if (isSelected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.surfaceVariant,
                                    contentColor = if (isSelected) Color.White else Color.Black
                                ),
                                shape = RoundedCornerShape(12.dp)
                            ) {
                                Text(mode)
                            }
                        }
                    }
                }

                // 📝 NOTE INPUT
                OutlinedTextField(
                    value = note,
                    onValueChange = { note = it },
                    label = { Text("Add a note (Optional)") },
                    leadingIcon = { Icon(Icons.Default.Edit, contentDescription = null) },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp)
                )

                val context = LocalContext.current

                // 💾 SAVE BUTTON
                Button(
                    onClick = {
                        val finalAmount = amount.toDoubleOrNull() ?: 0.0
                        onSaveClick( finalAmount, selectedSubCategoryId,selectedCategoryId , note.ifBlank { null }, paymentMode, isSynced, isSpeedExpense)
                        Toast.makeText(context, "$amount Inserted", Toast.LENGTH_SHORT).show()
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(56.dp),
                    shape = RoundedCornerShape(16.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary)
                ) {
                    Text("Save Expense", fontSize = 18.sp, fontWeight = FontWeight.Bold, color = Color.White)
                }
            }
        }
    }