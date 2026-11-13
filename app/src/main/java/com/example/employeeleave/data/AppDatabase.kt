package com.example.employeeleave.data

import android.content.Context
import androidx.room.*
import com.example.employeeleave.models.LeaveRequest
import com.example.employeeleave.models.User

@Dao
interface UserDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertUser(user: User)

    @Query("SELECT * FROM users WHERE email = :email LIMIT 1")
    suspend fun getUserByEmail(email: String): User?

    @Query("SELECT * FROM users")
    suspend fun getAllUsers(): List<User>
}

@Dao
interface LeaveRequestDao {
    @Insert
    suspend fun insertRequest(request: LeaveRequest)

    @Query("SELECT * FROM leave_requests WHERE userEmail = :email")
    suspend fun getRequestsByUser(email: String): List<LeaveRequest>

    @Query("SELECT * FROM leave_requests")
    suspend fun getAllRequests(): List<LeaveRequest>

    @Query("SELECT * FROM leave_requests WHERE status = 'PENDING'")
    suspend fun getPendingRequests(): List<LeaveRequest>

    @Query("SELECT * FROM leave_requests WHERE status = 'APPROVED'")
    suspend fun getApprovedRequests(): List<LeaveRequest>

    @Query("SELECT * FROM leave_requests WHERE status = 'REJECTED'")
    suspend fun getRejectedRequests(): List<LeaveRequest>

    @Query("SELECT * FROM leave_requests WHERE id = :requestId LIMIT 1")
    suspend fun getRequestById(requestId: Int): LeaveRequest?

    @Query("UPDATE leave_requests SET status = :newStatus WHERE id = :requestId")
    suspend fun updateRequestStatus(requestId: Int, newStatus: String)
}

@Database(entities = [User::class, LeaveRequest::class], version = 2, exportSchema = false)
abstract class AppDatabase : RoomDatabase() {
    abstract fun userDao(): UserDao
    abstract fun leaveRequestDao(): LeaveRequestDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getDatabase(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "leave_management_database"
                )
                .fallbackToDestructiveMigration()
                .build()
                INSTANCE = instance
                instance
            }
        }
    }
}