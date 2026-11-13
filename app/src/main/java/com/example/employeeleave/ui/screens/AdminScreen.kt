package com.example.employeeleave.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ExitToApp
import androidx.compose.material.icons.automirrored.filled.ListAlt
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.example.employeeleave.models.LeaveRequest
import com.example.employeeleave.models.User
import java.time.LocalDate
import java.time.YearMonth
import java.time.format.DateTimeFormatter
import java.time.format.TextStyle
import java.util.*

@Composable
fun AdminScreen(
    allRequests: List<LeaveRequest>,
    users: List<User>,
    onApprove: (Int) -> Unit,
    onReject: (Int) -> Unit,
    onEditBalance: (String, Int, Int, Int) -> Unit,
    onProfileClick: () -> Unit,
    onSignOutClick: () -> Unit
) {
    var selectedMainTab by remember { mutableStateOf("Requests") }
    val purpleTheme = Color(0xFFD188BE)

    Scaffold(
        bottomBar = {
            NavigationBar(containerColor = Color.White) {
                val tabs = listOf("Requests", "Balances", "Calendar", "Profile")
                tabs.forEach { tab ->
                    NavigationBarItem(
                        icon = {
                            Icon(
                                when (tab) {
                                    "Requests" -> Icons.AutoMirrored.Filled.ListAlt
                                    "Balances" -> Icons.Default.AccountBalanceWallet
                                    "Calendar" -> Icons.Default.CalendarMonth
                                    "Profile" -> Icons.Default.Person
                                    else -> Icons.Default.Settings
                                },
                                contentDescription = tab
                            )
                        },
                        label = { Text(tab) },
                        selected = selectedMainTab == tab,
                        onClick = { 
                            if (tab == "Profile") onProfileClick()
                            else selectedMainTab = tab 
                        },
                        colors = NavigationBarItemDefaults.colors(
                            selectedIconColor = purpleTheme,
                            selectedTextColor = purpleTheme,
                            unselectedIconColor = Color.Gray,
                            unselectedTextColor = Color.Gray,
                            indicatorColor = Color.Transparent
                        )
                    )
                }
            }
        }
    ) { padding ->
        Column(modifier = Modifier.padding(padding)) {
            AdminHeader(selectedMainTab, onSignOutClick)

            when (selectedMainTab) {
                "Requests" -> AdminRequestsTab(allRequests, onApprove, onReject)
                "Balances" -> AdminBalancesTab(users, onEditBalance)
                "Calendar" -> AdminCalendarTab(allRequests, users)
                else -> Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) { Text("Dashboard") }
            }
        }
    }
}

@Composable
fun AdminHeader(title: String, onSignOutClick: () -> Unit) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .background(Color(0xFF1A1A1A))
            .padding(top = 16.dp, bottom = 24.dp, start = 24.dp, end = 24.dp)
    ) {
        Column {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text("Admin Panel", color = Color.Gray, fontSize = 14.sp)
                IconButton(onClick = onSignOutClick) {
                    Icon(Icons.AutoMirrored.Filled.ExitToApp, contentDescription = "Sign Out", tint = Color.White)
                }
            }
            Text(
                text = "Leave $title",
                color = Color.White,
                fontSize = 32.sp,
                fontWeight = FontWeight.Bold
            )
        }
    }
}

@Composable
fun AdminRequestsTab(
    requests: List<LeaveRequest>,
    onApprove: (Int) -> Unit,
    onReject: (Int) -> Unit
) {
    var selectedStatus by remember { mutableStateOf("PENDING") }
    val statuses = listOf("PENDING", "APPROVED", "REJECTED")
    val purpleTheme = Color(0xFFD188BE)

    Column(modifier = Modifier.padding(16.dp)) {
        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            statuses.forEach { status ->
                val isSelected = selectedStatus == status
                Surface(
                    modifier = Modifier
                        .clip(RoundedCornerShape(20.dp))
                        .clickable { selectedStatus = status },
                    color = if (isSelected) purpleTheme else Color(0xFFF0F0F0),
                    contentColor = if (isSelected) Color.White else Color.Gray
                ) {
                    Text(
                        text = status.lowercase().replaceFirstChar { it.uppercase() },
                        modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp),
                        fontWeight = FontWeight.Medium
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        val filtered = requests.filter { it.status == selectedStatus }

        LazyColumn(verticalArrangement = Arrangement.spacedBy(12.dp)) {
            items(filtered) { request ->
                AdminRequestCard(request, onApprove, onReject)
            }
        }
    }
}

@Composable
fun AdminRequestCard(
    request: LeaveRequest,
    onApprove: (Int) -> Unit,
    onReject: (Int) -> Unit
) {
    val locale = LocalConfiguration.current.locales[0]
    val formatter = DateTimeFormatter.ofPattern("MMM dd, yyyy", locale)
    val duration = try {
        val start = LocalDate.parse(request.startDate, formatter)
        val end = LocalDate.parse(request.endDate, formatter)
        java.time.temporal.ChronoUnit.DAYS.between(start, end) + 1
    } catch (e: Exception) { 0L }

    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = Color(0xFFF9F9F9))
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                Text(request.userEmail.substringBefore("@"), fontSize = 18.sp, fontWeight = FontWeight.SemiBold)
                Surface(
                    color = Color(0xFFFDEAF4),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Text(
                        request.leaveType,
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                        fontSize = 12.sp,
                        color = Color(0xFFD188BE)
                    )
                }
            }
            Text("${request.startDate} - ${request.endDate}  ·  $duration days", color = Color.Gray, fontSize = 14.sp)
            Text("Reason: ${request.reason}", color = Color.DarkGray, fontSize = 14.sp, modifier = Modifier.padding(top = 4.dp))

            if (request.status == "PENDING") {
                Spacer(modifier = Modifier.height(16.dp))
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                    Button(
                        onClick = { onApprove(request.id) },
                        modifier = Modifier.weight(1f),
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF2E7D32)),
                        shape = RoundedCornerShape(8.dp)
                    ) {
                        Text("✓ Approve")
                    }
                    OutlinedButton(
                        onClick = { onReject(request.id) },
                        modifier = Modifier.weight(1f),
                        colors = ButtonDefaults.outlinedButtonColors(contentColor = Color(0xFFC62828)),
                        border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFFC62828)),
                        shape = RoundedCornerShape(8.dp)
                    ) {
                        Text("Reject")
                    }
                }
            }
        }
    }
}

@Composable
fun AdminBalancesTab(
    users: List<User>,
    onEditBalance: (String, Int, Int, Int) -> Unit
) {
    var searchQuery by remember { mutableStateOf("") }
    val filteredUsers = users.filter { it.role == "EMPLOYEE" && (it.name.contains(searchQuery, ignoreCase = true) || it.email.contains(searchQuery, ignoreCase = true)) }
    var userToEdit by remember { mutableStateOf<User?>(null) }

    Column(modifier = Modifier.padding(16.dp)) {
        TextField(
            value = searchQuery,
            onValueChange = { searchQuery = it },
            placeholder = { Text("Search employee...") },
            modifier = Modifier.fillMaxWidth(),
            leadingIcon = { Icon(Icons.Default.Search, contentDescription = null) },
            colors = TextFieldDefaults.colors(
                focusedContainerColor = Color(0xFFF0F0F0),
                unfocusedContainerColor = Color(0xFFF0F0F0),
                focusedIndicatorColor = Color.Transparent,
                unfocusedIndicatorColor = Color.Transparent
            ),
            shape = RoundedCornerShape(8.dp)
        )

        Spacer(modifier = Modifier.height(16.dp))

        LazyColumn(verticalArrangement = Arrangement.spacedBy(12.dp)) {
            items(filteredUsers) { user ->
                AdminBalanceCard(user, onEditClick = { userToEdit = user })
            }
        }
    }

    if (userToEdit != null) {
        EditBalanceDialog(
            user = userToEdit!!,
            onDismiss = { userToEdit = null },
            onSave = { email, a, s, p ->
                onEditBalance(email, a, s, p)
                userToEdit = null
            }
        )
    }
}

@Composable
fun AdminBalanceCard(user: User, onEditClick: () -> Unit) {
    val purpleTheme = Color(0xFFD188BE)
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = Color(0xFFF9F9F9))
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                Text(user.name, fontSize = 18.sp, fontWeight = FontWeight.SemiBold)
                Surface(
                    color = Color(0xFFFDEAF4),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Text(
                        "Employee", 
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                        fontSize = 12.sp,
                        color = Color(0xFFD188BE)
                    )
                }
            }
            
            Spacer(modifier = Modifier.height(12.dp))
            
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                AdminBalanceBox("Annual", user.annualBalance, modifier = Modifier.weight(1f))
                AdminBalanceBox("Sick", user.sickBalance, modifier = Modifier.weight(1f))
                AdminBalanceBox("Personal", user.personalBalance, modifier = Modifier.weight(1f))
            }

            Spacer(modifier = Modifier.height(12.dp))

            Button(
                onClick = onEditClick,
                modifier = Modifier.fillMaxWidth(),
                colors = ButtonDefaults.buttonColors(containerColor = purpleTheme),
                shape = RoundedCornerShape(8.dp)
            ) {
                Text("Edit Balance")
            }
        }
    }
}

@Composable
fun AdminBalanceBox(label: String, count: Int, modifier: Modifier = Modifier) {
    Column(
        modifier = modifier
            .border(1.dp, Color(0xFFEEEEEE), RoundedCornerShape(8.dp))
            .background(Color.White, RoundedCornerShape(8.dp))
            .padding(8.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(count.toString(), fontSize = 20.sp, fontWeight = FontWeight.Bold)
        Text(label, fontSize = 12.sp, color = Color.Gray)
    }
}

@Composable
fun EditBalanceDialog(
    user: User,
    onDismiss: () -> Unit,
    onSave: (String, Int, Int, Int) -> Unit
) {
    var annual by remember { mutableStateOf(user.annualBalance.toString()) }
    var sick by remember { mutableStateOf(user.sickBalance.toString()) }
    var personal by remember { mutableStateOf(user.personalBalance.toString()) }

    Dialog(onDismissRequest = onDismiss) {
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(16.dp)
        ) {
            Column(modifier = Modifier.padding(24.dp)) {
                Text("Edit Balances", style = MaterialTheme.typography.headlineSmall, fontWeight = FontWeight.Bold)
                Text(user.name, color = Color.Gray)
                
                Spacer(modifier = Modifier.height(16.dp))

                OutlinedTextField(value = annual, onValueChange = { annual = it }, label = { Text("Annual Leave") })
                OutlinedTextField(value = sick, onValueChange = { sick = it }, label = { Text("Sick Leave") })
                OutlinedTextField(value = personal, onValueChange = { personal = it }, label = { Text("Personal Leave") })

                Spacer(modifier = Modifier.height(24.dp))

                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.End) {
                    TextButton(onClick = onDismiss) { Text("Cancel") }
                    Button(onClick = { 
                        onSave(user.email, annual.toIntOrNull() ?: 0, sick.toIntOrNull() ?: 0, personal.toIntOrNull() ?: 0)
                    }) { Text("Save") }
                }
            }
        }
    }
}

@Composable
fun AdminCalendarTab(requests: List<LeaveRequest>, users: List<User>) {
    var currentMonth by remember { mutableStateOf(YearMonth.now()) }
    val approvedRequests = requests.filter { it.status == "APPROVED" }

    Column(modifier = Modifier.padding(16.dp)) {
        AdminCalendarHeader(
            currentMonth = currentMonth,
            onPreviousMonth = { currentMonth = currentMonth.minusMonths(1) },
            onNextMonth = { currentMonth = currentMonth.plusMonths(1) }
        )

        AdminCalendarGrid(currentMonth, approvedRequests, users)

        Spacer(modifier = Modifier.height(24.dp))

        Text("Team Members", style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold)
        
        Spacer(modifier = Modifier.height(12.dp))

        LazyColumn(verticalArrangement = Arrangement.spacedBy(12.dp)) {
            val employees = users.filter { it.role == "EMPLOYEE" }
            items(employees) { employee ->
                val empRequests = approvedRequests.filter { it.userEmail == employee.email }
                if (empRequests.isNotEmpty()) {
                    AdminTeamMemberCard(employee, empRequests)
                }
            }
        }
    }
}

@Composable
fun AdminCalendarHeader(currentMonth: YearMonth, onPreviousMonth: () -> Unit, onNextMonth: () -> Unit) {
    val locale = LocalConfiguration.current.locales[0]
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        IconButton(onClick = onPreviousMonth) { Icon(Icons.Default.ChevronLeft, contentDescription = null, tint = Color(0xFFD188BE)) }
        Text(
            "${currentMonth.month.getDisplayName(TextStyle.FULL, locale)} ${currentMonth.year}",
            fontWeight = FontWeight.Bold, fontSize = 20.sp
        )
        IconButton(onClick = onNextMonth) { Icon(Icons.Default.ChevronRight, contentDescription = null, tint = Color(0xFFD188BE)) }
    }
}

@Composable
fun AdminCalendarGrid(currentMonth: YearMonth, requests: List<LeaveRequest>, users: List<User>) {
    val daysInMonth = currentMonth.lengthOfMonth()
    val firstDayOfMonth = currentMonth.atDay(1).dayOfWeek.value // 1=Mon, ..., 7=Sun
    val daysOfWeek = listOf("Mon", "Tue", "Wed", "Thu", "Fri", "Sat", "Sun")

    Column {
        Row(modifier = Modifier.fillMaxWidth()) {
            daysOfWeek.forEach { Text(it, modifier = Modifier.weight(1f), textAlign = TextAlign.Center, fontSize = 12.sp, color = Color.Gray) }
        }
        Spacer(modifier = Modifier.height(8.dp))

        val startOffset = firstDayOfMonth - 1
        val totalCells = ((daysInMonth + startOffset + 6) / 7) * 7
        for (i in 0 until totalCells step 7) {
            Row(modifier = Modifier.fillMaxWidth()) {
                for (j in 0 until 7) {
                    val dayIndex = i + j
                    val dayNum = dayIndex - startOffset + 1
                    val isWithinMonth = dayNum in 1..daysInMonth
                    
                    Column(
                        modifier = Modifier.weight(1f).height(60.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        if (isWithinMonth) {
                            Text(dayNum.toString(), fontSize = 14.sp)
                            val date = currentMonth.atDay(dayNum)
                            val leavesOnDate = getAdminLeavesForDate(date, requests)
                            
                            Row(horizontalArrangement = Arrangement.spacedBy(2.dp)) {
                                leavesOnDate.take(2).forEach { req ->
                                    val user = users.find { it.email == req.userEmail }
                                    val initials = user?.name?.split(" ")?.mapNotNull { it.firstOrNull() }?.joinToString("")?.take(2)?.uppercase() 
                                        ?: req.userEmail.take(2).uppercase()
                                    
                                    Box(
                                        modifier = Modifier
                                            .size(18.dp)
                                            .clip(RoundedCornerShape(4.dp))
                                            .background(getAdminHighlightColor(req.leaveType).copy(alpha = 0.2f)),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Text(initials, fontSize = 8.sp, fontWeight = FontWeight.Bold, color = getAdminHighlightColor(req.leaveType))
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

fun getAdminLeavesForDate(date: LocalDate, requests: List<LeaveRequest>): List<LeaveRequest> {
    val formatter = DateTimeFormatter.ofPattern("MMM dd, yyyy", Locale.getDefault())
    return requests.filter {
        try {
            val start = LocalDate.parse(it.startDate, formatter)
            val end = LocalDate.parse(it.endDate, formatter)
            !date.isBefore(start) && !date.isAfter(end)
        } catch (e: Exception) { false }
    }
}

fun getAdminHighlightColor(type: String): Color {
    return when (type) {
        "Annual Leave" -> Color(0xFFBA68C8)
        "Sick Leave" -> Color(0xFFF06292)
        "Personal Leave" -> Color(0xFFFFB300)
        else -> Color.Gray
    }
}

@Composable
fun AdminTeamMemberCard(employee: User, empRequests: List<LeaveRequest>) {
    val initials = employee.name.split(" ").mapNotNull { it.firstOrNull() }.joinToString("").take(2).uppercase()
    val totalDays = empRequests.sumOf { req ->
        try {
            val formatter = DateTimeFormatter.ofPattern("MMM dd, yyyy", Locale.getDefault())
            val start = LocalDate.parse(req.startDate, formatter)
            val end = LocalDate.parse(req.endDate, formatter)
            java.time.temporal.ChronoUnit.DAYS.between(start, end) + 1
        } catch (e: Exception) { 0L }
    }

    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = Color(0xFFF9F9F9))
    ) {
        Row(modifier = Modifier.padding(16.dp), verticalAlignment = Alignment.CenterVertically) {
            Box(
                modifier = Modifier.size(40.dp).clip(CircleShape).background(Color(0xFFBA68C8)),
                contentAlignment = Alignment.Center
            ) {
                Text(initials, color = Color.White, fontWeight = FontWeight.Bold)
            }
            Spacer(modifier = Modifier.width(12.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(employee.name, fontWeight = FontWeight.Bold)
                Text("${empRequests.size} requests · $totalDays days", color = Color.Gray, fontSize = 12.sp)
            }
            Text(empRequests.firstOrNull()?.leaveType ?: "", color = Color(0xFFBA68C8), fontSize = 12.sp)
        }
    }
}
