package com.example.employeeleave.data

import android.content.Context
import com.example.employeeleave.models.LeaveRequest
import com.example.employeeleave.models.User
import com.example.employeeleave.utils.HashUtils

class DatabaseManager(context: Context) {

    val db = AppDatabase.getDatabase(context)
    private val userDao = db.userDao()
    private val requestDao = db.leaveRequestDao()

    suspend fun createUserProfile(name: String, email: String, password: String, role: String = "EMPLOYEE") {
        val hashedPassword = HashUtils.hashPassword(password)
        val newUser = if (role == "ADMIN") {
            User(
                email = email,
                name = name,
                password = hashedPassword,
                role = role,
                annualBalance = 0,
                sickBalance = 0,
                personalBalance = 0
            )
        } else {
            User(
                email = email,
                name = name,
                password = hashedPassword,
                role = role
            )
        }
        userDao.insertUser(newUser)
    }

    suspend fun getUserProfile(email: String): User? {
        return userDao.getUserByEmail(email)
    }

    suspend fun submitLeaveRequest(request: LeaveRequest) {
        requestDao.insertRequest(request)
    }

    suspend fun getRequestsByStatus(status: String): List<LeaveRequest> {
        return when (status) {
            "PENDING" -> requestDao.getPendingRequests()
            "APPROVED" -> requestDao.getApprovedRequests()
            "REJECTED" -> requestDao.getRejectedRequests()
            else -> requestDao.getAllRequests()
        }
    }

    suspend fun getAllRequests(): List<LeaveRequest> {
        return requestDao.getAllRequests()
    }

    suspend fun updateUserBalances(email: String, annual: Int, sick: Int, personal: Int) {
        val user = userDao.getUserByEmail(email)
        if (user != null) {
            userDao.insertUser(user.copy(
                annualBalance = annual,
                sickBalance = sick,
                personalBalance = personal
            ))
        }
    }

    suspend fun updateRequestStatus(requestId: Int, newStatus: String) {
        if (newStatus == "APPROVED") {
            val request = requestDao.getRequestById(requestId)
            if (request != null && request.status != "APPROVED") {
                val user = userDao.getUserByEmail(request.userEmail)
                if (user != null) {
                    val duration = calculateDuration(request.startDate, request.endDate)
                    val updatedUser = when (request.leaveType) {
                        "Annual Leave" -> user.copy(annualBalance = user.annualBalance - duration)
                        "Sick Leave" -> user.copy(sickBalance = user.sickBalance - duration)
                        "Personal Leave" -> user.copy(personalBalance = user.personalBalance - duration)
                        else -> user
                    }
                    userDao.insertUser(updatedUser)
                }
            }
        }
        requestDao.updateRequestStatus(requestId, newStatus)
    }

    private fun calculateDuration(startDate: String, endDate: String): Int {
        return try {
            val formatter = java.time.format.DateTimeFormatter.ofPattern("MMM dd, yyyy", java.util.Locale.getDefault())
            val start = java.time.LocalDate.parse(startDate, formatter)
            val end = java.time.LocalDate.parse(endDate, formatter)
            (java.time.temporal.ChronoUnit.DAYS.between(start, end) + 1).toInt()
        } catch (e: Exception) {
            0
        }
    }
}