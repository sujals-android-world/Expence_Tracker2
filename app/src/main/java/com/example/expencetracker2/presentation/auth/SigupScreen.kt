package com.example.expencetracker2.presentation.auth

import android.widget.Toast
import androidx.compose.foundation.Image
import com.example.expencetracker2.R
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.blur
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavHostController
import com.example.expencetracker2.presentation.navigation.Routes



@Composable
fun SignUpScreen(
    onSkipClick: () -> Unit,
    onCreateAccountClick: (String, String) -> Unit,
    onGoogleClick: () -> Unit,
    onFacebookClick: () -> Unit,
    onAppleClick: () -> Unit,
    onSignInClick: () -> Unit,
    viewModel: AuthViewModel = hiltViewModel(),
    navHostController: NavHostController
) {
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var passwordVisible by remember { mutableStateOf(false) }
    val authState by viewModel.authState.collectAsStateWithLifecycle()
    val context = LocalContext.current

    LaunchedEffect(authState) {
        if (authState.error != null) {
            Toast.makeText(context, authState.error, Toast.LENGTH_SHORT).show()
        } else if(authState.success) {
            navHostController.navigate(Routes.mainScreen) {
                popUpTo(Routes.signInScreen) {
                    inclusive  = true
                }
            }
        }
    }

    // प्रीमियम डार्क ग्रेडिएंट बैकग्राउंड
    val backgroundGradient = Brush.verticalGradient(
        colors = listOf(Color(0xFF1A1B2F), Color(0xFF161623))
    )

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(backgroundGradient)
            .statusBarsPadding()

    ) {

        // स्क्रॉलिंग सपोर्ट ताकि UI कभी न कटे
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 24.dp)
                .verticalScroll(rememberScrollState())
                .then(
                    if (authState.loading) {
                        Modifier
                            .blur(6.dp)
                            .alpha(0.5f)
                    } else {
                        Modifier
                    }
                ),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(modifier = Modifier.height(10.dp))

            // 1. SKIP FOR NOW (सब्से ऊपर Right Side में)
            TextButton(
                onClick = onSkipClick,
                modifier = Modifier
                    .align(Alignment.End)
                    .padding(8.dp)
            ) {
                Text(
                    text = "Skip for now",
                    color = Color.White.copy(alpha = 0.6f),
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Medium
                )
            }

            // 2. BANNER TEXT
            Text(
                text = "Create Account",
                color = Color.White,
                fontSize = 32.sp,
                fontWeight = FontWeight.Bold,
                textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = "Please fill all details to get started",
                color = Color.LightGray.copy(alpha = 0.8f),
                fontSize = 15.sp,
                textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.height(28.dp))

            // 3. MAIN CARD (सभी इनपुट, बटन और सोशल लॉगिन्स इसके अंदर हैं)
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(24.dp),
                colors = CardDefaults.cardColors(
                    containerColor = Color(0xFF26263B)
                ),
                elevation = CardDefaults.cardElevation(defaultElevation = 8.dp)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(24.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    // EMAIL TEXT FIELD
                    OutlinedTextField(
                        value = email,
                        onValueChange = { email = it },
                        label = { Text("Email Address", color = Color.Gray) },
                        leadingIcon = {
                            Icon(
                                Icons.Default.Email,
                                contentDescription = "Email",
                                tint = Color(0xFF6C63FF)
                            )
                        },
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedTextColor = Color.White,
                            unfocusedTextColor = Color.White,
                            focusedBorderColor = Color(0xFF6C63FF),
                            unfocusedBorderColor = Color.Gray.copy(alpha = 0.5f),
                            cursorColor = Color(0xFF6C63FF)
                        ),
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(12.dp)
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    // PASSWORD TEXT FIELD
                    OutlinedTextField(
                        value = password,
                        onValueChange = { password = it },
                        label = { Text("Password", color = Color.Gray) },
                        leadingIcon = {
                            Icon(
                                Icons.Default.Lock,
                                contentDescription = "Password",
                                tint = Color(0xFF6C63FF)
                            )
                        },
                        trailingIcon = {
                            val image =
                                if (passwordVisible) Icons.Default.Visibility else Icons.Default.VisibilityOff
                            IconButton(onClick = { passwordVisible = !passwordVisible }) {
                                Icon(
                                    image,
                                    contentDescription = "Toggle Password",
                                    tint = Color.Gray
                                )
                            }
                        },
                        visualTransformation = if (passwordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedTextColor = Color.White,
                            unfocusedTextColor = Color.White,
                            focusedBorderColor = Color(0xFF6C63FF),
                            unfocusedBorderColor = Color.Gray.copy(alpha = 0.5f),
                            cursorColor = Color(0xFF6C63FF)
                        ),
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(12.dp)
                    )

                    Spacer(modifier = Modifier.height(28.dp))

                    // CREATE BUTTON
                    Button(
                        onClick = { onCreateAccountClick(email, password) },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(50.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = Color(0xFF6C63FF)
                        ),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Text(
                            text = "Create",
                            color = Color.White,
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }

                    Spacer(modifier = Modifier.height(24.dp))

                    // OR SIGN UP WITH DIVIDER
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        HorizontalDivider(
                            modifier = Modifier.weight(1f),
                            color = Color.Gray.copy(alpha = 0.4f)
                        )
                        Text(
                            text = " Or sign up with ",
                            color = Color.Gray,
                            fontSize = 13.sp,
                            modifier = Modifier.padding(horizontal = 8.dp)
                        )
                        HorizontalDivider(
                            modifier = Modifier.weight(1f),
                            color = Color.Gray.copy(alpha = 0.4f)
                        )
                    }

                    Spacer(modifier = Modifier.height(20.dp))

                    // 3 GOL SOCIAL BUTTONS (सीधे आपके drawable नाम के साथ)
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(16.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        // Google Button
                        IconButton(
                            onClick = onGoogleClick,
                            modifier = Modifier
                                .size(52.dp)
                                .background(Color(0xFFF5F5F5), shape = CircleShape)
                                .padding(12.dp)
                        ) {
                            Image(
                                painter = painterResource(id = R.drawable.google),
                                contentDescription = "Google SignUp",
                                modifier = Modifier.fillMaxSize()
                            )
                        }

                        // Facebook Button
                        IconButton(
                            onClick = onFacebookClick,
                            modifier = Modifier
                                .size(52.dp)
                                .background(Color(0xFFF5F5F5), shape = CircleShape)
                                .padding(12.dp)
                        ) {
                            Image(
                                painter = painterResource(id = R.drawable.facebook),
                                contentDescription = "Facebook SignUp",
                                modifier = Modifier.fillMaxSize()
                            )
                        }
                        IconButton(
                            onClick = onAppleClick, modifier = Modifier
                                .size(52.dp)
                                .background(
                                    Color(0xFFF5F5F5),
                                    shape = CircleShape
                                )
                                .padding(12.dp)
                        ) {
                            Image(
                                painter = painterResource(id = R.drawable.apple),
                                contentDescription = "Apple SignUp",
                                modifier = Modifier.fillMaxSize()
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(28.dp))

            // 4. ALREADY HAVE AN ACCOUNT? (Outside the card)
            Row(
                modifier = Modifier
                    .padding(bottom = 32.dp)
                    .clickable { onSignInClick() },
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Already have an account? ",
                    color = Color.LightGray.copy(alpha = 0.7f),
                    fontSize = 14.sp
                )
                Text(
                    text = "Sign In",
                    color = Color(0xFF6C63FF),
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold
                )
            }
        }
        if (authState.loading) {
            CircularProgressIndicator(
                Modifier.align(Alignment.Center),
                color = Color(0xFF6C63FF)
            )
        }
    }
}

