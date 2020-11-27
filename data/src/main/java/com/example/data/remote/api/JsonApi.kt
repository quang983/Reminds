package com.example.data.remote.api

import com.example.domain.model.MainResponse
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Query

interface JsonApi {
    @GET("/api/v1/search/")
    suspend fun getImageList(
        @Query("q") queryParam: String,
        @Query("sorting") sorting: String,
        @Query("page") page: Int
    ): Response<MainResponse>
}