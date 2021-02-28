package com.sinx.todo.api.middleware

// Интерфейс для промежуточной операции над ws событиями
fun interface WebSocketMiddleware {
    operator fun invoke(nameEvent: String, json: String)
}