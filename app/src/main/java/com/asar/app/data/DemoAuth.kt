package com.asar.app.data

/**
 * DEMO AUTHENTICATION ONLY.
 *
 * Accounts live in memory for the lifetime of the app process. Passwords are compared
 * as plain strings and are never persisted or sent anywhere. This exists purely so the
 * hackathon prototype can show a realistic sign-up flow — it is not real security.
 */
class DemoAuth {

    data class Account(val name: String, val email: String, val password: String)

    private val accounts = mutableMapOf(
        DEMO_EMAIL to Account("Aigerim Bekova", DEMO_EMAIL, DEMO_PASSWORD)
    )

    fun register(name: String, email: String, password: String): Result<Account> {
        val key = email.trim().lowercase()
        return when {
            name.isBlank() -> Result.failure(IllegalArgumentException("Please enter your name"))
            !EMAIL_REGEX.matches(key) -> Result.failure(IllegalArgumentException("Please enter a valid email"))
            password.length < 6 -> Result.failure(IllegalArgumentException("Password must be at least 6 characters"))
            accounts.containsKey(key) -> Result.failure(IllegalArgumentException("An account with this email already exists"))
            else -> {
                val account = Account(name.trim(), key, password)
                accounts[key] = account
                Result.success(account)
            }
        }
    }

    fun login(email: String, password: String): Result<Account> {
        val account = accounts[email.trim().lowercase()]
        return if (account != null && account.password == password) {
            Result.success(account)
        } else {
            Result.failure(IllegalArgumentException("Email or password is incorrect"))
        }
    }

    fun demoAccount(): Account = accounts.getValue(DEMO_EMAIL)

    companion object {
        const val DEMO_EMAIL = "demo@asar.kz"
        const val DEMO_PASSWORD = "asar2026"
        private val EMAIL_REGEX = Regex("^[^@\\s]+@[^@\\s]+\\.[^@\\s]+$")
    }
}
