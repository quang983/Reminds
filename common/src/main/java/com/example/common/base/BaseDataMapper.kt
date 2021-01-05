package com.example.common.base

interface BaseDataMapper<M, E> {
    fun toData(entity: E): M
    fun toDomain(model: M): E
}