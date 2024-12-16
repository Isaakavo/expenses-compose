package com.avocado.expenses.shared

interface Platform {
    val name: String
}

expect fun getPlatform(): Platform