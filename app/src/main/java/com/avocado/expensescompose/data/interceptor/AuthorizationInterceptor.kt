package com.avocado.expensescompose.data.interceptor

import android.util.Log
import com.apollographql.apollo3.api.http.HttpRequest
import com.apollographql.apollo3.api.http.HttpResponse
import com.apollographql.apollo3.network.http.HttpInterceptor
import com.apollographql.apollo3.network.http.HttpInterceptorChain
import com.avocado.expensescompose.data.repositories.DataStoreRepository
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import javax.inject.Inject

class AuthorizationInterceptor @Inject constructor(
    private val dataStoreRepository: DataStoreRepository
) : HttpInterceptor {
    private val mutex = Mutex()
    override suspend fun intercept(
        request: HttpRequest, chain: HttpInterceptorChain
    ): HttpResponse {

        try {
            var jwt = mutex.withLock { dataStoreRepository.getString("JWT") }
            if (jwt.isNullOrBlank()) {
                Log.d("AUTH", "Error jwt empty")
                return chain.proceed(request)
            }
            val response =
                chain.proceed(request.newBuilder().addHeader("X-Session-Key", jwt).build())
            return if (response.statusCode == 401) {
                //TODO add logic to refetch the token or close the session
                jwt = mutex.withLock {
                    dataStoreRepository.getString("JWT")
                }
                if (jwt.isNullOrBlank()) {
                    Log.d("AUTH", "Error jwt empty")
                    return chain.proceed(request)
                }
                chain.proceed(request.newBuilder().addHeader("X-Session-Key", jwt).build())
            } else {
                response
            }
        } catch (e: Exception) {
            e.message?.let { Log.d("AUTH", it) }
        }

        return chain.proceed(request)
    }
}