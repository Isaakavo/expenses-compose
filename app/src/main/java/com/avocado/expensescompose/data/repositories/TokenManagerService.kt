package com.avocado.expensescompose.data.repositories

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import com.avocado.expensescompose.data.TokenManagerServiceI
import com.avocado.expensescompose.data.model.MyResult
import com.avocado.expensescompose.data.model.isSuccess
import javax.inject.Inject
import kotlinx.coroutines.flow.first
import okio.IOException
import timber.log.Timber

val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "auth")

class TokenManagerService @Inject constructor(
  private val context: Context
) : TokenManagerServiceI {

  companion object {
    private val JWT_ACCESS_KEY = stringPreferencesKey("ACCESS_JWT")
    private val JWT_REFRESH_KEY = stringPreferencesKey("REFRESH_JWT")
    private val USER_NAME_KEY = stringPreferencesKey("USERNAME_KEY")
  }

  suspend fun saveUsername(username: String): MyResult<Unit> =
    try {
      context.dataStore.edit { preferences ->
        preferences[USER_NAME_KEY] = username
      }
      Timber.d("Username saved correctly")
      MyResult.Success(Unit)
    } catch (exception: IOException) {
      MyResult.Error(data = Unit, exception = exception)
    }

  suspend fun getUsername(): MyResult<String?> =
    try {
      context.dataStore.data.first()[USER_NAME_KEY]
        ?.let { userName ->
          MyResult.Success(userName)
        } ?: MyResult.Error()
    } catch (exception: Exception) {
      MyResult.Error(exception = exception)
    }

  suspend fun deleteUsername(): MyResult<Unit> =
    try {
      context.dataStore.edit { preferences ->
        preferences.remove(USER_NAME_KEY)
      }
      MyResult.Success(Unit)
    } catch (exception: IOException) {
      MyResult.Error(Unit, exception = exception)
    } catch (exception: Exception) {
      MyResult.Error(data = Unit, exception = exception)
    }

  suspend fun validateTokens(): Boolean =
    getAccessToken().isSuccess() && getRefreshToken().isSuccess()

  override suspend fun saveAccessToken(value: String): MyResult<Unit> =
    try {
      context.dataStore.edit { preferences ->
        preferences[JWT_ACCESS_KEY] = value
      }
      Timber.d("Access Token saved $value")
      MyResult.Success(Unit)
    } catch (exception: IOException) {
      MyResult.Error(data = Unit, exception = exception)
    }

  override suspend fun getAccessToken(): MyResult<String?> = try {
    context.dataStore.data.first()[JWT_ACCESS_KEY]
      ?.let { accessToken ->
        MyResult.Success(accessToken)
      } ?: MyResult.Error(null)
  } catch (exception: Exception) {
    MyResult.Error(exception = exception)
  }

  override suspend fun deleteAccessToken(): MyResult<Unit> =
    try {
      context.dataStore.edit { preferences ->
        preferences.remove(JWT_ACCESS_KEY)
      }
      MyResult.Success(Unit)
    } catch (exception: IOException) {
      MyResult.Error(data = Unit, exception = exception)
    }

  override suspend fun saveRefreshToken(value: String): MyResult<Unit> =
    try {
      context.dataStore.edit { preferences ->
        preferences[JWT_REFRESH_KEY] = value
      }
      Timber.d("Refresh Token saved $value")
      MyResult.Success(data = Unit)
    } catch (exception: IOException) {
      MyResult.Error(data = Unit, exception = exception)
    }

  override suspend fun getRefreshToken(): MyResult<String?> =
    try {
      val preferences = context.dataStore.data.first()
      MyResult.Success(data = preferences[JWT_REFRESH_KEY])
    } catch (exception: Exception) {
      MyResult.Error(exception = exception)
    }

  override suspend fun deleteRefreshToken(): MyResult<Unit> =
    try {
      context.dataStore.edit { preferences ->
        preferences.remove(JWT_REFRESH_KEY)
      }
      MyResult.Success(data = Unit)
    } catch (exception: IOException) {
      MyResult.Error(data = Unit, exception = exception)
    }
}
