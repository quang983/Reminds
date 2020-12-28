package com.example.common.base

interface BaseDataMapper<M, E> {
    fun toModel(entity: E): M
    fun toEntity(model: M): E
}