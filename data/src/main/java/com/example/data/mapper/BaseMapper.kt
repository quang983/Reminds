package com.example.data.mapper

interface BaseMapper<M, E> {
    fun toModel(entity: E): M
    fun toEntity(model: M): E
}