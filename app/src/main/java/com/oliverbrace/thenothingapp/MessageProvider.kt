package com.oliverbrace.thenothingapp.domain

class MessageProvider {

    private val messages = listOf(
        "Nothing happened.",
        "Still nothing.",
        "The Nothing App thanks you.",
        "Try again… still nothing."
    )

    fun randomMessage(): String = messages.random()
}