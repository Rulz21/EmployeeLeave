package com.example.employeeleave.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import java.time.Instant
import java.time.LocalDate
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.time.temporal.ChronoUnit

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ApplyLeaveScreen(
    user: com.example.employeeleave.models.User?,
    onSubmit: (String, String, String, String) -> Unit,
    onCalendarClick: () -> Unit,
    onProfileClick: () -> Unit,
    onCancel: () -> Unit
) {
    var leaveType by remember { mutableStateOf("Annual Leave") }
    val leaveTypes = listOf("Annual Leave", "Sick Leave", "Personal Leave")
    var expanded by remember { mutableStateOf(false) }

    val dateFormatter = DateTimeFormatter.ofPattern("MMM dd, yyyy")
    var startDate by remember { mutableStateOf(LocalDate.now()) }
    var endDate by remember { mutableStateOf(LocalDate.now().plusDays(2)) }
    
    var showStartDatePicker by remember { mutableStateOf(false) }
    var showEndDatePicker by remember { mutableStateOf(false) }

    val startDatePickerState = rememberDatePickerState(
        initialSelectedDateMillis = startDate.atStartOfDay(ZoneId.systemDefault()).toInstant().toEpochMilli()
    )
    val endDatePickerState = rememberDatePickerState(
        initialSelectedDateMillis = endDate.atStartOfDay(ZoneId.systemDefault()).toInstant().toEpochMilli()
    )

    val duration = ChronoUnit.DAYS.between(startDate, endDate) + 1

    var reason by remember { mutableStateOf("") }
    var notifyManager by remember { mutableStateOf(true) }

    val purpleTheme = Color(0xFFD188BE)
    val lightPurpleBg = Color(0xFFF3E8FF)
    val lightGrayBg = Color(0xFFF9FAFB)

    if (showStartDatePicker) {
        DatePickerDialog(
            onDismissRequest = { showStartDatePicker = false },
            confirmButton = {
                TextButton(onClick = {
                    startDatePickerState.selectedDateMillis?.let {
                        startDate = Instant.ofEpochMilli(it).atZone(ZoneId.systemDefault()).toLocalDate()
                        if (endDate.isBefore(startDate)) {
                            endDate = startDate
                        }
                    }
                    showStartDatePicker = false
                }) { Text("OK") }
            },
            dismissButton = {
                TextButton(onClick = { showStartDatePicker = false }) { Text("Cancel") }
            }
        ) {
            DatePicker(state = startDatePickerState)
        }
    }

    if (showEndDatePicker) {
        DatePickerDialog(
            onDismissRequest = { showEndDatePicker = false },
            confirmButton = {
                TextButton(onClick = {
                    endDatePickerState.selectedDateMillis?.let {
                        val selectedDate = Instant.ofEpochMilli(it).atZone(ZoneId.systemDefault()).toLocalDate()
                        if (selectedDate.isBefore(startDate)) {
                            // Optionally show error
                        } else {
                            endDate = selectedDate
                        }
                    }
                    showEndDatePicker = false
                }) { Text("OK") }
            },
            dismissButton = {
                TextButton(onClick = { showEndDatePicker = false }) { Text("Cancel") }
            }
        ) {
            DatePicker(state = endDatePickerState)
        }
    }

    Scaffold(
        bottomBar = {
            NavigationBar(containerColor = Color.White) {
                NavigationBarItem(
                    icon = { Icon(Icons.Default.Home, contentDescription = null) },
                    label = { Text("Home") },
                    selected = false,
                    onClick = onCancel
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
                    selected = true,
                    onClick = {}
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
            Spacer(modifier = Modifier.height(32.dp))
            Text(
                "Apply Leave",
                style = MaterialTheme.typography.displaySmall.copy(
                    fontWeight = FontWeight.Bold,
                    fontSize = 36.sp
                ),
                modifier = Modifier.align(Alignment.CenterHorizontally)
            )
            Spacer(modifier = Modifier.height(32.dp))

            // Leave Type
            Text("Leave Type", fontWeight = FontWeight.Medium, modifier = Modifier.padding(bottom = 8.dp))
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp)
                    .background(lightGrayBg, RoundedCornerShape(8.dp))
                    .clickable { expanded = true }
                    .padding(horizontal = 16.dp),
                contentAlignment = Alignment.CenterStart
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(leaveType)
                    Icon(Icons.Default.ArrowDropDown, contentDescription = null)
                }
                DropdownMenu(
                    expanded = expanded,
                    onDismissRequest = { expanded = false }
                ) {
                    leaveTypes.forEach { type ->
                        DropdownMenuItem(
                            text = { Text(type) },
                            onClick = {
                                leaveType = type
                                expanded = false
                            }
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Dates
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                Column(modifier = Modifier.weight(1f)) {
                    Text("Start Date", fontWeight = FontWeight.Medium, modifier = Modifier.padding(bottom = 8.dp))
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(56.dp)
                            .background(lightGrayBg, RoundedCornerShape(8.dp))
                            .clickable { showStartDatePicker = true }
                            .padding(horizontal = 16.dp),
                        contentAlignment = Alignment.CenterStart
                    ) {
                        Text(startDate.format(dateFormatter))
                    }
                }
                Column(modifier = Modifier.weight(1f)) {
                    Text("End Date", fontWeight = FontWeight.Medium, modifier = Modifier.padding(bottom = 8.dp))
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(56.dp)
                            .background(lightGrayBg, RoundedCornerShape(8.dp))
                            .clickable { showEndDatePicker = true }
                            .padding(horizontal = 16.dp),
                        contentAlignment = Alignment.CenterStart
                    ) {
                        Text(endDate.format(dateFormatter))
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Duration box
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(40.dp)
                    .background(lightPurpleBg, RoundedCornerShape(4.dp)),
                contentAlignment = Alignment.Center
            ) {
                val durationText = if (duration == 1L) "1 day" else "$duration days"
                Text("Duration: $durationText", color = purpleTheme, fontWeight = FontWeight.Medium)
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Reason
            Text("Reason", fontWeight = FontWeight.Medium, modifier = Modifier.padding(bottom = 8.dp))
            TextField(
                value = reason,
                onValueChange = { reason = it },
                placeholder = { Text("Family vacation trip...", color = Color.LightGray) },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(120.dp),
                colors = TextFieldDefaults.colors(
                    focusedContainerColor = lightGrayBg,
                    unfocusedContainerColor = lightGrayBg,
                    focusedIndicatorColor = Color.Transparent,
                    unfocusedIndicatorColor = Color.Transparent,
                ),
                shape = RoundedCornerShape(8.dp)
            )

            Spacer(modifier = Modifier.height(32.dp))

            // Notify Manager Switch
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(48.dp)
                    .background(lightGrayBg, RoundedCornerShape(4.dp))
                    .padding(horizontal = 16.dp),
                contentAlignment = Alignment.CenterStart
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text("Notify Manager")
                    Switch(
                        checked = notifyManager,
                        onCheckedChange = { notifyManager = it },
                        colors = SwitchDefaults.colors(checkedThumbColor = Color.White, checkedTrackColor = purpleTheme)
                    )
                }
            }

            Spacer(modifier = Modifier.height(32.dp))

            // Submit Button
            val balance = when (leaveType) {
                "Annual Leave" -> user?.annualBalance ?: 0
                "Sick Leave" -> user?.sickBalance ?: 0
                "Personal Leave" -> user?.personalBalance ?: 0
                else -> 0
            }
            val isBalanceSufficient = duration <= balance

            if (!isBalanceSufficient) {
                Text(
                    "Insufficient balance. Available: $balance days",
                    color = Color.Red,
                    fontSize = 12.sp,
                    modifier = Modifier.padding(bottom = 8.dp)
                )
            }

            Button(
                onClick = { 
                    onSubmit(
                        leaveType, 
                        startDate.format(dateFormatter), 
                        endDate.format(dateFormatter), 
                        reason
                    ) 
                },
                enabled = isBalanceSufficient && reason.isNotBlank(),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp),
                colors = ButtonDefaults.buttonColors(containerColor = purpleTheme),
                shape = RoundedCornerShape(8.dp)
            ) {
                Text("Submit Request", fontSize = 18.sp, fontWeight = FontWeight.Medium)
            }

            Spacer(modifier = Modifier.height(12.dp))

            TextButton(
                onClick = onCancel,
                modifier = Modifier.align(Alignment.CenterHorizontally)
            ) {
                Text("Cancel", color = Color.Gray)
            }
        }
    }
}
