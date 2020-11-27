package com.example.domain.repository

interface BaseRepository<param> {
    suspend fun invoke()
}