package com.example.common.base.model

interface BaseConverter<I, O> {
    fun convert(): O
    fun copy(data: O): I
}