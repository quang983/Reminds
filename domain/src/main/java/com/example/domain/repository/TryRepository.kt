package com.example.domain.repository

import com.example.domain.model.MainResponse

interface TryRepository {
   suspend fun getTryApi() : MainResponse?
}