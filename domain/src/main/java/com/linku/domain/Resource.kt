package com.linku.domain

import kotlinx.coroutines.flow.FlowCollector
import kotlinx.coroutines.flow.flow
import kotlinx.serialization.Serializable

/**
 * A data wrapper
 *
 * Data request state wrapper, usually used in a flow collector
 */
sealed class Resource<out T> {
    @Serializable
    object Loading : Resource<Nothing>()
    @Serializable
    data class Success<T>(val data: T) : Resource<T>()
    @Serializable
    data class Failure<T>(
        val message: String,
        val code: String = "9999"
    ) : Resource<T>()
}

suspend inline fun <T> FlowCollector<Resource<T>>.emitResource(data: T) =
    emit(Resource.Success(data))

suspend inline fun <T> FlowCollector<Resource<T>>.emitResource(code: String, message: String) =
    emit(Resource.Failure(code, message))

fun <T> resourceFlow(flowCollector: suspend FlowCollector<Resource<T>>.() -> Unit) = flow {
    emit(Resource.Loading)
    flowCollector.invoke(this)
}