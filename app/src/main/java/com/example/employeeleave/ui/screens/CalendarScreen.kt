package com.example.employeeleave.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.ChevronLeft
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.employeeleave.models.LeaveRequest
import java.time.LocalDate
import java.time.YearMonth
import java.time.format.DateTimeFormatter
import java.time.format.TextStyle
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CalendarScreen(
    requests: List<LeaveRequest>,
    onBackClick: () -> Unit
) {
    var currentMonth by remember { mutableStateOf(YearMonth.now()) }
    val filteredRequests = remember(requests) {
        requests.filter { it.status == "APPROVED" || it.status == "PENDING" }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Leave Calendar", fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                }
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(horizontal = 16.dp)
        ) {
            CalendarHeader(
                currentMonth = currentMonth,
                onPreviousMonth = { currentMonth = currentMonth.minusMonths(1) },
                onNextMonth = { currentMonth = currentMonth.plusMonths(1) }
            )

            CalendarGrid(
                currentMonth = currentMonth,
                requests = filteredRequests
            )

            Spacer(modifier = Modifier.height(16.dp))

            Legend()

            Spacer(modifier = Modifier.height(24.dp))

            Text(
                "Upcoming Leaves",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )

            Spacer(modifier = Modifier.height(8.dp))

            UpcomingLeavesList(requests = filteredRequests)
        }
    }
}

@Composable
fun CalendarHeader(
    currentMonth: YearMonth,
    onPreviousMonth: () -> Unit,
    onNextMonth: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        IconButton(onClick = onPreviousMonth) {
            Icon(Icons.Default.ChevronLeft, contentDescription = "Previous Month", tint = Color(0xFFD81B60))
        }
        Text(
            text = "${currentMonth.month.getDisplayName(TextStyle.FULL, Locale.getDefault())} ${currentMonth.year}",
            style = MaterialTheme.typography.titleLarge,
            fontWeight = FontWeight.Bold
        )
        IconButton(onClick = onNextMonth) {
            Icon(Icons.Default.ChevronRight, contentDescription = "Next Month", tint = Color(0xFFD81B60))
        }
    }
}

@Composable
fun CalendarGrid(
    currentMonth: YearMonth,
    requests: List<LeaveRequest>
) {
    val daysInMonth = currentMonth.lengthOfMonth()
    val firstDayOfMonth = currentMonth.atDay(1).dayOfWeek.value % 7 // 0 for Sunday, 1 for Monday...
    val daysOfWeek = listOf("Sun", "Mon", "Tue", "Wed", "Thu", "Fri", "Sat")

    Column {
        Row(modifier = Modifier.fillMaxWidth()) {
            daysOfWeek.forEach { day ->
                Text(
                    text = day,
                    modifier = Modifier.weight(1f),
                    textAlign = TextAlign.Center,
                    style = MaterialTheme.typography.bodySmall,
                    color = Color.Gray
                )
            }
        }

        Spacer(modifier = Modifier.height(8.dp))

        val totalCells = ((daysInMonth + firstDayOfMonth + 6) / 7) * 7
        for (i in 0 until totalCells step 7) {
            Row(modifier = Modifier.fillMaxWidth()) {
                for (j in 0 until 7) {
                    val dayIndex = i + j
                    val dayNum = dayIndex - firstDayOfMonth + 1
                    val isWithinMonth = dayNum in 1..daysInMonth
                    
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .aspectRatio(1f),
                        contentAlignment = Alignment.Center
                    ) {
                        if (isWithinMonth) {
                            val date = currentMonth.atDay(dayNum)
                            val leaveRequest = getLeaveForDate(date, requests)
                            
                            if (leaveRequest != null) {
                                Box(
                                    modifier = Modifier
                                        .size(36.dp)
                                        .clip(CircleShape)
                                        .background(getLeaveColor(leaveRequest.leaveType, leaveRequest.status)),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Text(
                                        text = dayNum.toString(),
                                        color = if (leaveRequest.status == "PENDING") Color.Black else Color.White,
                                        fontWeight = FontWeight.Bold
                                    )
                                }
                            } else {
                                Text(text = dayNum.toString())
                            }
                        }
                    }
                }
            }
        }
    }
}

fun getLeaveForDate(date: LocalDate, requests: List<LeaveRequest>): LeaveRequest? {
    val formatter = DateTimeFormatter.ofPattern("MMM dd, yyyy", Locale.getDefault())
    return requests.find { request ->
        try {
            val start = LocalDate.parse(request.startDate, formatter)
            val end = LocalDate.parse(request.endDate, formatter)
            !date.isBefore(start) && !date.isAfter(end)
        } catch (e: Exception) {
            false
        }
    }
}

fun getLeaveColor(type: String, status: String): Color {
    if (status == "PENDING") return Color(0xFFFFF9C4) // Light Yellow
    return when (type) {
        "Annual Leave" -> Color(0xFFBA68C8) // Purple
        "Sick Leave" -> Color(0xFFF06292) // Pink/Reddish
        "Personal Leave" -> Color(0xFFFFD54F) // Amber/Orange
        else -> Color.LightGray
    }
}

@Composable
fun Legend() {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        LegendItem(color = Color(0xFFBA68C8), label = "Annual")
        LegendItem(color = Color(0xFFF06292), label = "Sick")
        LegendItem(color = Color(0xFFFFD54F), label = "Personal")
    }
}

@Composable
fun LegendItem(color: Color, label: String) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        Box(
            modifier = Modifier
                .size(12.dp)
                .clip(CircleShape)
                .background(color)
        )
        Spacer(modifier = Modifier.width(4.dp))
        Text(text = label, style = MaterialTheme.typography.bodySmall, color = Color.Gray)
    }
}

@Composable
fun UpcomingLeavesList(requests: List<LeaveRequest>) {
    val today = LocalDate.now()
    val formatter = DateTimeFormatter.ofPattern("MMM dd, yyyy", Locale.getDefault())
    
    val upcoming = requests.filter {
        try {
            val end = LocalDate.parse(it.endDate, formatter)
            !end.isBefore(today)
        } catch (e: Exception) {
            false
        }
    }.sortedBy {
        try {
            LocalDate.parse(it.startDate, formatter)
        } catch (e: Exception) {
            LocalDate.MAX
        }
    }

    if (upcoming.isEmpty()) {
        Text("No upcoming leaves", color = Color.Gray, modifier = Modifier.padding(vertical = 16.dp))
    } else {
        LazyColumn(
            verticalArrangement = Arrangement.spacedBy(8.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            items(upcoming) { request ->
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(8.dp),
                    colors = CardDefaults.cardColors(containerColor = Color(0xFFF5F5F5))
                ) {
                    Row(
                        modifier = Modifier
                            .padding(16.dp)
                            .fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Text(request.leaveType, fontWeight = FontWeight.Bold)
                            Text(
                                "${request.startDate} - ${request.endDate}",
                                style = MaterialTheme.typography.bodySmall,
                                color = Color.Gray
                            )
                        }
                        Text(
                            text = if (request.status == "APPROVED") "Approved" else "Pending",
                            color = if (request.status == "APPROVED") Color(0xFF4CAF50) else Color(0xFFFFA000),
                            fontWeight = FontWeight.Bold,
                            style = MaterialTheme.typography.bodyMedium
                        )
                    }
                }
            }
        }
    }
}
