package com.example.shield.model

enum class AppLanguage(val code: String, val displayName: String, val nativeName: String) {
    EN("en", "English", "English"),
    HI("hi", "Hindi", "हिंदी");

    val isHindi: Boolean get() = this == HI
}
