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
fun SignUpScreen(
    onNavigateBack: () -> Unit,
    onRegisterSuccess: (String, String, String, String, (String) -> Unit) -> Unit
) {
    var name by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var selectedRole by remember { mutableStateOf("EMPLOYEE") }

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
            Spacer(modifier = Modifier.height(40.dp))

            // Logo
            Box(
                modifier = Modifier
                    .size(70.dp)
                    .background(purpleTheme, CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    "LM",
                    color = Color.White,
                    fontSize = 28.sp,
                    fontWeight = FontWeight.Bold
                )
            }

            Spacer(modifier = Modifier.height(24.dp))

            Text(
                text = "Create Account",
                style = MaterialTheme.typography.displaySmall.copy(
                    fontWeight = FontWeight.Bold,
                    fontSize = 32.sp
                )
            )
            Text(
                text = "Sign up to join the Leave Manager",
                style = MaterialTheme.typography.bodyLarge.copy(color = Color.Gray)
            )

            Spacer(modifier = Modifier.height(32.dp))

            Column(modifier = Modifier.fillMaxWidth()) {
                Text("Full Name", fontWeight = FontWeight.Medium, modifier = Modifier.padding(bottom = 8.dp))
                TextField(
                    value = name,
                    onValueChange = { name = it },
                    placeholder = { Text("Your Name", color = Color.LightGray) },
                    modifier = Modifier.fillMaxWidth(),
                    colors = TextFieldDefaults.colors(
                        focusedContainerColor = lightGrayBg,
                        unfocusedContainerColor = lightGrayBg,
                        focusedIndicatorColor = Color.Transparent,
                        unfocusedIndicatorColor = Color.Transparent,
                    ),
                    shape = RoundedCornerShape(8.dp)
                )

                Spacer(modifier = Modifier.height(16.dp))

                Text("Email Address", fontWeight = FontWeight.Medium, modifier = Modifier.padding(bottom = 8.dp))
                TextField(
                    value = email,
                    onValueChange = { email = it },
                    placeholder = { Text("name@company.com", color = Color.LightGray) },
                    modifier = Modifier.fillMaxWidth(),
                    colors = TextFieldDefaults.colors(
                        focusedContainerColor = lightGrayBg,
                        unfocusedContainerColor = lightGrayBg,
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
                        focusedIndicatorColor = Color.Transparent,
                        unfocusedIndicatorColor = Color.Transparent,
                    ),
                    shape = RoundedCornerShape(8.dp)
                )

                Spacer(modifier = Modifier.height(16.dp))

                Text("Select Role", fontWeight = FontWeight.Medium, modifier = Modifier.padding(bottom = 8.dp))
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    RadioButton(
                        selected = selectedRole == "EMPLOYEE",
                        onClick = { selectedRole = "EMPLOYEE" },
                        colors = RadioButtonDefaults.colors(selectedColor = purpleTheme)
                    )
                    Text("Employee")
                    Spacer(modifier = Modifier.width(16.dp))
                    RadioButton(
                        selected = selectedRole == "ADMIN",
                        onClick = { selectedRole = "ADMIN" },
                        colors = RadioButtonDefaults.colors(selectedColor = purpleTheme)
                    )
                    Text("Admin")
                }
            }

            Spacer(modifier = Modifier.height(32.dp))

            Button(
                onClick = {
                    if (name.isBlank() || email.isBlank() || password.isBlank()) {
                        scope.launch {
                            snackbarHostState.showSnackbar("Please fill all fields")
                        }
                    } else if (!android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
                        scope.launch {
                            snackbarHostState.showSnackbar("Please enter a valid email")
                        }
                    } else if (password.length < 6) {
                        scope.launch {
                            snackbarHostState.showSnackbar("Password must be at least 6 characters")
                        }
                    } else {
                        onRegisterSuccess(name, email, password, selectedRole) { error ->
                            scope.launch {
                                snackbarHostState.showSnackbar(error)
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
                Text("Register", fontSize = 18.sp, fontWeight = FontWeight.Medium)
            }

            Spacer(modifier = Modifier.weight(1f))

            Row(
                modifier = Modifier.padding(bottom = 24.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text("Already have an account? ", color = Color.Gray)
                TextButton(onClick = onNavigateBack, contentPadding = PaddingValues(0.dp)) {
                    Text("Sign In", color = purpleTheme)
                }
            }
        }
    }
}

