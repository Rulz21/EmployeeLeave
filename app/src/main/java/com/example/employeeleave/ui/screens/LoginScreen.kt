package com.example.employeeleave.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.launch

@Composable
fun LoginScreen(onLoginClick: (String, String, (String) -> Unit) -> Unit, onSignUpClick: () -> Unit) {
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }

    val snackbarHostState = remember { SnackbarHostState() }
    val scope = rememberCoroutineScope()

    val purpleTheme = Color(0xFFD188BE)
    val lightGrayBg = Color(0xFFF9FAFB)

    Scaffold(
        snackbarHost = { SnackbarHost(hostState = snackbarHostState) },
        containerColor = Color.White
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(horizontal = 24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Top
        ) {
            Spacer(modifier = Modifier.height(60.dp))

            // Logo
            Box(
                modifier = Modifier
                    .size(80.dp)
                    .background(purpleTheme, CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    "LM",
                    color = Color.White,
                    fontSize = 32.sp,
                    fontWeight = FontWeight.Bold
                )
            }

            Spacer(modifier = Modifier.height(24.dp))

            Text(
                text = "Leave Manager",
                style = MaterialTheme.typography.displaySmall.copy(
                    fontWeight = FontWeight.Bold,
                    fontSize = 36.sp
                )
            )
            Text(
                text = "Sign in to manage your leaves",
                style = MaterialTheme.typography.bodyLarge.copy(color = Color.Gray)
            )

            Spacer(modifier = Modifier.height(48.dp))

            Column(modifier = Modifier.fillMaxWidth()) {
                Text("Email Address", fontWeight = FontWeight.Medium, modifier = Modifier.padding(bottom = 8.dp))
                TextField(
                    value = email,
                    onValueChange = { email = it },
                    placeholder = { Text("name@company.com", color = Color.LightGray) },
                    modifier = Modifier.fillMaxWidth(),
                    colors = TextFieldDefaults.colors(
                        focusedContainerColor = lightGrayBg,
                        unfocusedContainerColor = lightGrayBg,
                        disabledContainerColor = lightGrayBg,
                        focusedIndicatorColor = Color.Transparent,
                        unfocusedIndicatorColor = Color.Transparent,
                    ),
                    shape = RoundedCornerShape(8.dp)
                )

                Spacer(modifier = Modifier.height(16.dp))

                Text("Password", fontWeight = FontWeight.Medium, modifier = Modifier.padding(bottom = 8.dp))
                TextField(
                    value = password,
                    onValueChange = { password = it },
                    placeholder = { Text("........", color = Color.LightGray) },
                    visualTransformation = PasswordVisualTransformation(),
                    modifier = Modifier.fillMaxWidth(),
                    colors = TextFieldDefaults.colors(
                        focusedContainerColor = lightGrayBg,
                        unfocusedContainerColor = lightGrayBg,
                        disabledContainerColor = lightGrayBg,
                        focusedIndicatorColor = Color.Transparent,
                        unfocusedIndicatorColor = Color.Transparent,
                    ),
                    shape = RoundedCornerShape(8.dp)
                )
            }

            TextButton(
                onClick = { /* Handle Forgot Password */ },
                modifier = Modifier.align(Alignment.End)
            ) {
                Text("Forgot Password?", color = purpleTheme)
            }

            Spacer(modifier = Modifier.height(32.dp))

            Button(
                onClick = {
                    if (email.isBlank() || password.isBlank()) {
                        scope.launch {
                            snackbarHostState.showSnackbar("Please enter both email and password")
                        }
                    } else {
                        onLoginClick(email, password) { errorMessage ->
                            scope.launch {
                                snackbarHostState.showSnackbar(errorMessage)
                            }
                        }
                    }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp),
                colors = ButtonDefaults.buttonColors(containerColor = purpleTheme),
                shape = RoundedCornerShape(8.dp)
            ) {
                Text("Sign In", fontSize = 18.sp, fontWeight = FontWeight.Medium)
            }

            Spacer(modifier = Modifier.weight(1f))

            Row(
                modifier = Modifier.padding(bottom = 24.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text("Don't have an account? ", color = Color.Gray)
                TextButton(onClick = onSignUpClick, contentPadding = PaddingValues(0.dp)) {
                    Text("Sign Up", color = purpleTheme)
                }
            }
        }
    }
}
