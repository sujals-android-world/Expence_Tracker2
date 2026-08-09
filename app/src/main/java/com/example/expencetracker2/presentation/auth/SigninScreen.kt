package com.example.expencetracker2.presentation.auth

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavHostController
import com.example.expencetracker2.presentation.navigation.Routes


@Composable
fun SignInScreen(
    onSkipClick: () -> Unit,
    onSignInClick: (String, String) -> Unit,
    onForgotPasswordClick: () -> Unit,
    onSignUpClick: () -> Unit,
    socialLoginsContent: () -> Unit,
    viewModel: AuthViewModel,
    navHostController: NavHostController
) {
    var email by remember { mutableStateOf("sujal@gmail.com") }
    var password by remember { mutableStateOf("111111") }
    var passwordVisible by remember { mutableStateOf(false) }
    val loginState by viewModel.loginAuthState.collectAsStateWithLifecycle()
    val context = LocalContext.current

    LaunchedEffect(loginState) {
        if (loginState.error != null) {
            Toast.makeText(context, loginState.error, Toast.LENGTH_SHORT).show()
        } else if(loginState.success) {
            navHostController.navigate(Routes.MainScreen) {
                popUpTo(Routes.SignInScreen) {
                    inclusive  = true
                }
                viewModel.resetLoginState()
            }
        }
    }




    // बैकग्राउंड के लिए एक प्रीमियम ग्रेडिएंट कलर (Deep Navy Blue to Indigo)
    val backgroundGradient = Brush.verticalGradient(
        colors = listOf(Color(0xFF1A1B2F), Color(0xFF161623))
    )

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(backgroundGradient)
            .statusBarsPadding() // स्टेटस बार के नीचे से स्क्रीन शुरू होगी
    ) {
        // 1. SKIP FOR NOW (Top Right Corner)

        // स्क्रॉलिंग सपोर्ट ताकि छोटे फोन में भी UI न कटे
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 24.dp)
                .verticalScroll(rememberScrollState())
                .then(
                    if (loginState.loading) {
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
            // 2. PREMIUM BANNER TEXT
            Text(
                text = "Welcome Back",
                color = Color.White,
                fontSize = 32.sp,
                fontWeight = FontWeight.Bold,
                textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = "Sign in to your account to continue",
                color = Color.LightGray.copy(alpha = 0.8f),
                fontSize = 15.sp,
                textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.height(32.dp))

            // 3. CARD FOR INPUT FIELDS
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(24.dp),
                colors = CardDefaults.cardColors(
                    containerColor = Color(0xFF26263B) // मॉडर्न डार्क कार्ड लुक
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
                        leadingIcon = { Icon(Icons.Default.Email, contentDescription = "Email", tint = Color(0xFF6C63FF)) },
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
                        leadingIcon = { Icon(Icons.Default.Lock, contentDescription = "Password", tint = Color(0xFF6C63FF)) },
                        trailingIcon = {
                            val image = if (passwordVisible) Icons.Default.Visibility else Icons.Default.VisibilityOff
                            IconButton(onClick = { passwordVisible = !passwordVisible }) {
                                Icon(image, contentDescription = "Toggle Password", tint = Color.Gray)
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

                    Spacer(modifier = Modifier.height(12.dp))

                    // 4. FORGOT PASSWORD (कार्ड के अंदर नीचे Right Side में)
                    Text(
                        text = "Forgot Password?",
                        color = Color(0xFF6C63FF),
                        fontSize = 14.sp,
                        fontWeight = FontWeight.SemiBold,
                        modifier = Modifier
                            .align(Alignment.End)
                            .clickable { onForgotPasswordClick() }
                    )

                    Spacer(modifier = Modifier.height(24.dp))

                    // SIGN IN BUTTON (एकदम मॉडर्न ग्रेडिएंट या नियॉन पर्पल लुक)
                    Button(
                        onClick = { onSignInClick(email, password) },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(50.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = Color(0xFF6C63FF)
                        ),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Text(
                            text = "Sign In",
                            color = Color.White,
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }

            // 5. SOCIAL LOGINS (पिछला सोशल बटन वाला कोड यहाँ ऑटोमैटिक रेंडर होगा)
            socialLoginsContent()

            Spacer(modifier = Modifier.height(16.dp))

            // 6. NO ACCOUNT? CREATE ONE (सबसे नीचे)
            Row(
                modifier = Modifier
                    .padding(bottom = 32.dp)
                    .clickable { onSignUpClick() },
                horizontalArrangement = Arrangement.Center
            ) {
                Text(
                    text = "Don't have an account? ",
                    color = Color.LightGray.copy(alpha = 0.7f),
                    fontSize = 14.sp
                )
                Text(
                    text = "Create One",
                    color = Color(0xFF6C63FF),
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold
                )
            }
        }
        if (loginState.loading) {
            CircularProgressIndicator(
                Modifier.align(Alignment.Center),
                color = Color(0xFF6C63FF)
            )
        }
    }
}


