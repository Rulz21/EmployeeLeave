package com.example.employeeleave.models

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "users")
data class User(
    @PrimaryKey
    val email: String,
    val name: String,
    val password: String = "",
    val role: String = "EMPLOYEE",
    val annualBalance: Int = 14,
    val sickBalance: Int = 7,
    val personalBalance: Int = 3
)

@Entity(tableName = "leave_requests")
data class LeaveRequest(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val userEmail: String,
    val leaveType: String,
    val startDate: String,
    val endDate: String,
    val status: String = "PENDING",
    val reason: String
)