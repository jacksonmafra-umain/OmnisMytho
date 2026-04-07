package com.umain.omnismytho

interface Platform {
    val name: String
}

expect fun getPlatform(): Platform
