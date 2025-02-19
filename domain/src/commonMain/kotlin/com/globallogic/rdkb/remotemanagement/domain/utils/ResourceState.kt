package com.globallogic.rdkb.remotemanagement.domain.utils

import com.globallogic.rdkb.remotemanagement.domain.utils.Resource.Failure
import com.globallogic.rdkb.remotemanagement.domain.utils.Resource.Success

sealed interface ResourceState<out Data, out Error: ResourceError> {
    data object None : ResourceState<Nothing, Nothing>
    data object Loading : ResourceState<Nothing, Nothing>
    data object Cancelled : ResourceState<Nothing, Nothing>
}

interface ResourceError

data class ThrowableResourceError(val throwable: Throwable) : ResourceError

sealed interface Resource<out Data, out Error: ResourceError> : ResourceState<Data, Error> {
    val data: Data? get() = null
    val error: Error? get() = null

    data class Success<Data>(override val data: Data): Resource<Data, Nothing>
    data class Failure<Error: ResourceError>(override val error: Error): Resource<Nothing, Error>
}

inline fun <Data, Error: ResourceError> Resource<Data, Error>.dataOrElse(default: (Error) -> Data): Data = when(this) {
    is Success -> data
    is Failure -> default(error)
}
inline fun <Data, Error: ResourceError> Resource<Data, Error>.dataOrDefault(default: Data): Data = when(this) {
    is Success -> data
    is Failure -> default
}

inline fun <Data, Error: ResourceError> Resource<Data, Error>.onSuccess(
    onSuccess: (Data) -> Unit
): Resource<Data, Error> = apply {
    data?.let(onSuccess)
}
inline fun <Data, Error: ResourceError> Resource<Data, Error>.onFailure(
    onFailure: (Error) -> Unit
): Resource<Data, Error> = apply {
    error?.let(onFailure)
}

inline fun <Data, Error: ResourceError, NewData> Resource<Data, Error>.map(
    map: (Data) -> NewData
): Resource<NewData, Error> = when(this) {
    is Success -> Success(map(data))
    is Failure -> this
}

inline fun <Data, Error: ResourceError, NewError: ResourceError> Resource<Data, Error>.mapError(
    map: (Error) -> NewError
): Resource<Data, NewError> = when(this) {
    is Success -> this
    is Failure -> Failure(map(error))
}

inline fun <Data, Error: ResourceError> Resource<Data, Error>.mapDataToError(
    mapDataToError: (Data) -> Error
): Failure<Error> = when(this) {
    is Success -> Failure(mapDataToError(data))
    is Failure -> this
}

inline fun <Data, Error: ResourceError> Resource<Data, Error>.mapErrorToData(
    mapErrorToData: (Error) -> Data
): Success<Data> = when(this) {
    is Success -> this
    is Failure -> Success(mapErrorToData(error))
}

inline fun <Data, Error: ResourceError, Result> Resource<Data, Error>.fold(
    onSuccess: (Data) -> Result,
    onFailure: (Error) -> Result,
): Result = when(this) {
    is Success -> onSuccess(data)
    is Failure -> onFailure(error)
}

inline fun <Data, Error: ResourceError, NewData, NewError: ResourceError> Resource<Data, Error>.flatMap(
    onSuccess: (Data) -> Resource<NewData, NewError>,
    onFailure: (Error) -> Resource<NewData, NewError>,
): Resource<NewData, NewError> = fold(onSuccess, onFailure)

inline fun <Data, Error: ResourceError, NewData, NewError: ResourceError> Resource<Data, Error>.flatMap(
    flatMap: (Data?, Error?) -> Resource<NewData, NewError>
): Resource<NewData, NewError> = flatMap(data, error)

inline fun <Data, Error: ResourceError, NewData> Resource<Data, Error>.flatMapData(
    flatMapData: (Data) -> Resource<NewData, Error>
): Resource<NewData, Error> = when (this) {
    is Success -> flatMapData(data)
    is Failure -> this
}

inline fun <Data, Error: ResourceError, NewError: ResourceError> Resource<Data, Error>.flatMapError(
    flatMapError: (Error) -> Resource<Data, NewError>
): Resource<Data, NewError> = when (this) {
    is Success -> this
    is Failure -> flatMapError(error)
}
