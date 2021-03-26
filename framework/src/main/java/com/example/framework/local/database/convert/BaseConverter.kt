package com.example.framework.local.database.convert

interface BaseConverter<I, O> {
    fun convert(): O
    fun copy(data: O): I
}