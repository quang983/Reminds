package com.example.domain.base

interface BaseUseCase<in Param,out Result> {
    suspend operator fun invoke(params : Param) : Result

    open class Param
}