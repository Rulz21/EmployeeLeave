package com.example.employeeleave.ui

import androidx.compose.runtime.*
import androidx.compose.ui.platform.LocalContext
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.employeeleave.data.DatabaseManager
import com.example.employeeleave.models.*
import com.example.employeeleave.ui.screens.*
import com.example.employeeleave.utils.HashUtils
import kotlinx.coroutines.launch

@Composable
fun LeaveAppNavigation() {
    val navController = rememberNavController()

    val context = LocalContext.current
    val dbManager = remember { DatabaseManager(context) }
    val coroutineScope = rememberCoroutineScope()

    var currentUser by remember { mutableStateOf<User?>(null) }
    var userRequests by remember { mutableStateOf<List<LeaveRequest>>(emptyList()) }
    var allRequests by remember { mutableStateOf<List<LeaveRequest>>(emptyList()) }
    var allUsers by remember { mutableStateOf<List<User>>(emptyList()) }

    NavHost(navController = navController, startDestination = "login") {

        composable("login") {
            LoginScreen(
                onLoginClick = { enteredEmail, enteredPassword, onError ->
                    coroutineScope.launch {
                        val user = dbManager.getUserProfile(enteredEmail)
                        val hashedEnteredPassword = HashUtils.hashPassword(enteredPassword)
                        if (user != null && user.password == hashedEnteredPassword) {
                            currentUser = user
                            if (user.role == "ADMIN") {
                                navController.navigate("admin_home") {
                                    popUpTo("login") { inclusive = true }
                                }
                            } else {
                                navController.navigate("employee_home/${user.email}") {
                                    popUpTo("login") { inclusive = true }
                                }
                            }
                        } else {
                            // Simple way to handle "admin" login for testing if no DB entries yet
                            if (enteredEmail == "admin@company.com" && enteredPassword == "admin") {
                                dbManager.createUserProfile("Admin", "admin@company.com", "admin", "ADMIN")
                                currentUser = dbManager.getUserProfile("admin@company.com")
                                navController.navigate("admin_home") {
                                    popUpTo("login") { inclusive = true }
                                }
                            } else {
                                onError("Invalid email or password")
                            }
                        }
                    }
                },
                onSignUpClick = {
                    navController.navigate("sign_up")
                }
            )
        }

        composable("sign_up") {
            SignUpScreen(
                onNavigateBack = {
                    navController.popBackStack()
                },
                onRegisterSuccess = { enteredName, enteredEmail, enteredPassword, enteredRole, onError ->
                    coroutineScope.launch {
                        val existingUser = dbManager.getUserProfile(enteredEmail)
                        if (existingUser != null) {
                            onError("User with this email already exists")
                        } else {
                            dbManager.createUserProfile(
                                name = enteredName,
                                email = enteredEmail,
                                password = enteredPassword,
                                role = enteredRole
                            )
                            navController.popBackStack()
                        }
                    }
                }
            )
        }

        composable("employee_home/{email}") { backStackEntry ->
            val email = backStackEntry.arguments?.getString("email") ?: ""

            LaunchedEffect(email) {
                currentUser = dbManager.getUserProfile(email)
                userRequests = dbManager.db.leaveRequestDao().getRequestsByUser(email)
            }

            currentUser?.let { user ->
                HomeScreen(
                    userName = user.name,
                    annualBalance = user.annualBalance,
                    sickBalance = user.sickBalance,
                    personalBalance = user.personalBalance,
                    recentRequests = userRequests.take(3),
                    onApplyLeaveClick = { navController.navigate("apply_leave") },
                    onMyRequestsClick = { navController.navigate("my_requests") },
                    onCalendarClick = { navController.navigate("calendar") },
                    onProfileClick = { navController.navigate("profile") },
                    onSignOutClick = {
                        currentUser = null
                        navController.navigate("login") {
                            popUpTo(0) { inclusive = true }
                        }
                    }
                )
            }
        }

        composable("my_requests") {
            MyRequestsScreen(
                requests = userRequests,
                onBackClick = { navController.popBackStack() }
            )
        }

        composable("calendar") {
            CalendarScreen(
                requests = userRequests,
                onBackClick = { navController.popBackStack() }
            )
        }

        composable("profile") {
            ProfileScreen(
                user = currentUser,
                onSignOutClick = {
                    currentUser = null
                    navController.navigate("login") {
                        popUpTo(0) { inclusive = true }
                    }
                },
                onBackClick = { navController.popBackStack() }
            )
        }

        composable("apply_leave") {
            ApplyLeaveScreen(
                user = currentUser,
                onSubmit = { type, start, end, reason ->
                    coroutineScope.launch {
                        currentUser?.let { user ->
                            val request = LeaveRequest(
                                userEmail = user.email,
                                leaveType = type,
                                startDate = start,
                                endDate = end,
                                reason = reason,
                                status = "PENDING"
                            )
                            dbManager.submitLeaveRequest(request)
                            navController.popBackStack()
                        }
                    }
                },
                onCalendarClick = { navController.navigate("calendar") },
                onProfileClick = { navController.navigate("profile") },
                onCancel = { navController.popBackStack() }
            )
        }

        composable("admin_home") {
            LaunchedEffect(Unit) {
                allRequests = dbManager.getAllRequests()
                allUsers = dbManager.db.userDao().getAllUsers()
            }

            AdminScreen(
                allRequests = allRequests,
                users = allUsers,
                onApprove = { requestId ->
                    coroutineScope.launch {
                        dbManager.updateRequestStatus(requestId, "APPROVED")
                        allRequests = dbManager.getAllRequests()
                        allUsers = dbManager.db.userDao().getAllUsers()
                    }
                },
                onReject = { requestId ->
                    coroutineScope.launch {
                        dbManager.updateRequestStatus(requestId, "REJECTED")
                        allRequests = dbManager.getAllRequests()
                        allUsers = dbManager.db.userDao().getAllUsers()
                    }
                },
                onEditBalance = { email, annual, sick, personal ->
                    coroutineScope.launch {
                        dbManager.updateUserBalances(email, annual, sick, personal)
                        allUsers = dbManager.db.userDao().getAllUsers()
                    }
                },
                onProfileClick = { navController.navigate("profile") },
                onSignOutClick = {
                    currentUser = null
                    navController.navigate("login") {
                        popUpTo(0) { inclusive = true }
                    }
                }
            )
        }
    }
}