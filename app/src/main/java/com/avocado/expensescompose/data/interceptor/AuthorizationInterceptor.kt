package com.avocado.expensescompose.data.interceptor

import android.util.Log
import com.apollographql.apollo3.api.http.HttpRequest
import com.apollographql.apollo3.api.http.HttpResponse
import com.apollographql.apollo3.network.http.HttpInterceptor
import com.apollographql.apollo3.network.http.HttpInterceptorChain
import com.avocado.expensescompose.data.model.MyResult
import com.avocado.expensescompose.data.repositories.DataStoreRepository
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import javax.inject.Inject

class AuthorizationInterceptor @Inject constructor(
    private val dataStoreRepository: DataStoreRepository
) : HttpInterceptor {
    private val mutex = Mutex()

    private suspend fun extractJwt(): String? = mutex.withLock {
        when (val value = dataStoreRepository.getString("JWT")) {
            is MyResult.Success -> {
                return value.data
            }

            is MyResult.Error -> {
                return ""
            }
        }
    }


    private suspend fun validateJwtIsNotNullOrEmpty(
        jwt: String?, request: HttpRequest, chain: HttpInterceptorChain
    ): HttpResponse {
        if (jwt.isNullOrBlank()) {
            Log.d("AUTH", "Error jwt empty")
            return chain.proceed(request)
        }

        return chain.proceed(request.newBuilder().addHeader("X-Session-Key", jwt).build())
    }

    override suspend fun intercept(
        request: HttpRequest, chain: HttpInterceptorChain
    ): HttpResponse {

        try {
            var jwt = extractJwt()
            val response = validateJwtIsNotNullOrEmpty(jwt, request, chain)
            return if (response.statusCode == 401) {
                //TODO validate if is possible make a mutation from here to refresh the token
                jwt = extractJwt()
                validateJwtIsNotNullOrEmpty(jwt, request, chain)
            } else {
                response
            }
        } catch (e: Exception) {
            e.message?.let { Log.d("AUTH", it) }
        }

        return chain.proceed(request)
    }
}