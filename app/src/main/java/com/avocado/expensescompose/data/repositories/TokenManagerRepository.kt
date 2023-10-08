package com.avocado.expensescompose.data.repositories

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import com.avocado.expensescompose.data.TokenService
import com.avocado.expensescompose.data.model.MyResult
import kotlinx.coroutines.flow.first
import okio.IOException
import javax.inject.Inject

val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "auth")

class TokenManagerRepository @Inject constructor(private val context: Context) : TokenService {

  companion object {
    private val JWT_ACCESS_KEY = stringPreferencesKey("ACCESS_JWT")
    private val JWT_REFRESH_KEY = stringPreferencesKey("REFRESH_JWT")
  }

  override suspend fun saveAccessToken(value: String): MyResult<Boolean> =
    try {
      context.dataStore.edit { preferences ->
        preferences[JWT_ACCESS_KEY] = value
      }
      MyResult.Success(true)
    } catch (exception: IOException) {
      MyResult.Error(false, exception.message)
    }


  override suspend fun getAccessToken(): MyResult<String?> = try {
    val preferences = context.dataStore.data.first()
    MyResult.Success(preferences[JWT_ACCESS_KEY])
  } catch (e: Exception) {
    e.printStackTrace()
    MyResult.Error(null, e.message)
  }

  override suspend fun deleteAccessToken(): MyResult<Boolean> = try {
    context.dataStore.edit { preferences ->
      preferences.remove(JWT_ACCESS_KEY)
    }
    MyResult.Success(true)
  } catch (exception: IOException) {
    MyResult.Error(false, exception.message)
  }

  override suspend fun saveRefreshToken(value: String): MyResult<Boolean> =
    try {
      context.dataStore.edit { preferences ->
        preferences[JWT_REFRESH_KEY] = value
      }
      MyResult.Success(true)
    } catch (exception: IOException) {
      MyResult.Error(false, exception.message)
    }

  override suspend fun getRefreshToken(): MyResult<String?> = try {
    val preferences = context.dataStore.data.first()
    MyResult.Success(preferences[JWT_REFRESH_KEY])
  } catch (e: Exception) {
    e.printStackTrace()
    MyResult.Error(null, e.message)
  }

  override suspend fun deleteRefreshToken(): MyResult<Boolean> = try {
    context.dataStore.edit { preferences ->
      preferences.remove(JWT_REFRESH_KEY)
    }
    MyResult.Success(true)
  } catch (exception: IOException) {
    MyResult.Error(false, exception.message)
  }


}