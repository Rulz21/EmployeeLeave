package com.example.employeeleave.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ExitToApp
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.employeeleave.models.LeaveRequest

@Composable
fun HomeScreen(
    userName: String,
    annualBalance: Int,
    sickBalance: Int,
    personalBalance: Int,
    recentRequests: List<LeaveRequest>,
    onApplyLeaveClick: () -> Unit,
    onMyRequestsClick: () -> Unit,
    onCalendarClick: () -> Unit,
    onProfileClick: () -> Unit,
    onSignOutClick: () -> Unit
) {
    val purpleTheme = Color(0xFFD188BE)
    Scaffold(
        bottomBar = {
            NavigationBar(containerColor = Color.White) {
                NavigationBarItem(
                    icon = { Icon(Icons.Default.Home, contentDescription = null) },
                    label = { Text("Home") },
                    selected = true,
                    onClick = {}
                )
                NavigationBarItem(
                    icon = { Icon(Icons.Default.CalendarMonth, contentDescription = null) },
                    label = { Text("Calendar") },
                    selected = false,
                    onClick = onCalendarClick
                )
                NavigationBarItem(
                    icon = { Icon(Icons.Default.AddCircle, contentDescription = null, tint = purpleTheme) },
                    label = { Text("Apply", color = purpleTheme) },
                    selected = false,
                    onClick = onApplyLeaveClick
                )
                NavigationBarItem(
                    icon = { Icon(Icons.Default.Person, contentDescription = null) },
                    label = { Text("Profile") },
                    selected = false,
                    onClick = onProfileClick
                )
            }
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(horizontal = 24.dp)
        ) {
            Spacer(modifier = Modifier.height(24.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.Top
            ) {
                Column {
                    Text(
                        "Good Morning,",
                        style = MaterialTheme.typography.titleLarge.copy(
                            color = Color.Gray,
                            fontWeight = FontWeight.Normal
                        )
                    )
                    Text(
                        userName,
                        style = MaterialTheme.typography.displaySmall.copy(
                            fontWeight = FontWeight.Bold,
                            fontSize = 40.sp
                        )
                    )
                }
                IconButton(onClick = onSignOutClick) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ExitToApp,
                        contentDescription = "Sign Out"
                    )
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            // Leave Balance Card
            Card(
                colors = CardDefaults.cardColors(containerColor = Color(0xFF1A1A1A)),
                shape = RoundedCornerShape(16.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(20.dp)) {
                    Text(
                        "Leave Balance",
                        color = Color.White,
                        style = MaterialTheme.typography.titleMedium
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceAround
                    ) {
                        CircularBalanceIndicator("Annual", annualBalance, Color(0xFFC084FC))
                        CircularBalanceIndicator("Sick", sickBalance, Color(0xFFF87171))
                        CircularBalanceIndicator("Personal", personalBalance, Color(0xFFFEF08A))
                    }
                }
            }

            Spacer(modifier = Modifier.height(32.dp))

            Text("Quick Actions", style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.SemiBold)
            Spacer(modifier = Modifier.height(16.dp))

            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                ActionButton("Apply Leave", Color(0xFFD188BE), Modifier.weight(1f), onClick = onApplyLeaveClick)
                ActionButton("My Requests", Color(0xFFF87171).copy(alpha = 0.7f), Modifier.weight(1f), onClick = onMyRequestsClick)
            }
            Spacer(modifier = Modifier.height(12.dp))
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                ActionButton("Calendar", Color(0xFFFEF08A), Modifier.fillMaxWidth(), onClick = onCalendarClick)
            }

            Spacer(modifier = Modifier.height(32.dp))

            Text("Recent Requests", style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.SemiBold)
            Spacer(modifier = Modifier.height(16.dp))

            LazyColumn(
                verticalArrangement = Arrangement.spacedBy(12.dp),
                contentPadding = PaddingValues(bottom = 16.dp)
            ) {
                items(recentRequests) { request ->
                    RequestCard(request)
                }
            }
        }
    }
}

@Composable
fun CircularBalanceIndicator(label: String, count: Int, color: Color) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Box(
            modifier = Modifier
                .size(60.dp)
                .background(color, CircleShape),
            contentAlignment = Alignment.Center
        ) {
            Text(
                count.toString(),
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold,
                color = Color.Black
            )
        }
        Spacer(modifier = Modifier.height(8.dp))
        Text(label, color = Color.Gray, style = MaterialTheme.typography.bodyMedium)
    }
}

@Composable
fun ActionButton(
    text: String,
    containerColor: Color,
    modifier: Modifier = Modifier,
    textColor: Color = Color.White,
    onClick: () -> Unit = {}
) {
    Button(
        onClick = onClick,
        colors = ButtonDefaults.buttonColors(containerColor = containerColor),
        shape = RoundedCornerShape(8.dp),
        modifier = modifier.height(56.dp),
        contentPadding = PaddingValues(0.dp)
    ) {
        Text(text, color = textColor, fontWeight = FontWeight.Medium)
    }
}

@Composable
fun RequestCard(request: LeaveRequest) {
    Card(
        colors = CardDefaults.cardColors(containerColor = Color(0xFFF9FAFB)),
        shape = RoundedCornerShape(8.dp),
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier
                .padding(16.dp)
                .fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column {
                Text(request.leaveType, fontWeight = FontWeight.SemiBold, fontSize = 16.sp)
                Text(
                    "${request.startDate} - ${request.endDate}",
                    style = MaterialTheme.typography.bodySmall,
                    color = Color.Gray
                )
            }
            Text(
                text = request.status.lowercase().replaceFirstChar { it.uppercase() },
                fontWeight = FontWeight.Medium,
                color = when (request.status) {
                    "APPROVED" -> Color(0xFF22C55E)
                    "PENDING" -> Color(0xFFEAB308)
                    else -> Color(0xFFEF4444)
                }
            )
        }
    }
}