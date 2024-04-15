package com.example.jetpackscrollable.state

data class CommonState<out T>(val status: Status, val message: String? = null, val data: T? = null) {
    companion object {
        fun <T> success(data: T?): CommonState<T> {
            return CommonState(Status.SUCCESS, data = data)
        }
        fun <T> failed(msg: String): CommonState<T> {
            return CommonState(Status.FAILED, message = msg)
        }
        fun <T> loading(): CommonState<T> {
            return CommonState(Status.LOADING)
        }
    }
}

enum class Status {
    SUCCESS,
    FAILED,
    LOADING
}
