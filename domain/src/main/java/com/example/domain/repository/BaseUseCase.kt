package com.example.domain.repository

interface BaseUseCase<in Param,out Result> {
    suspend operator fun invoke(params : Param) : Result

    open class Param
}