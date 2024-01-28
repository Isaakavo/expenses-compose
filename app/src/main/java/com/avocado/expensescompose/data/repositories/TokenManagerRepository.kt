package com.avocado.expensescompose.data.repositories

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import com.avocado.expensescompose.R
import com.avocado.expensescompose.data.TokenService
import com.avocado.expensescompose.data.model.MyResult
import kotlinx.coroutines.flow.first
import okio.IOException
import timber.log.Timber
import javax.inject.Inject

val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "auth")

class TokenManagerRepository @Inject constructor(private val context: Context) : TokenService {

  companion object {
    private val JWT_ACCESS_KEY = stringPreferencesKey("ACCESS_JWT")
    private val JWT_REFRESH_KEY = stringPreferencesKey("REFRESH_JWT")
    private val USER_NAME_KEY = stringPreferencesKey("USERNAME_KEY")
  }

  suspend fun saveUsername(username: String): MyResult<Boolean> {
    return try {
      context.dataStore.edit { preferences ->
        preferences[USER_NAME_KEY] = username
      }
      Timber.d("Username saved correctly")
      MyResult.Success(true)
    } catch (exception: IOException) {
      Timber.e("Token user save error ${exception.printStackTrace()}")
      MyResult.Error(false, R.string.token_user_save_error, exception = exception)
    }
  }

  suspend fun getUsername(): MyResult<String?> {
    return try {
      val preferences = context.dataStore.data.first()
      val accessToken = preferences[USER_NAME_KEY]
      if (accessToken != null) {
        MyResult.Success(preferences[USER_NAME_KEY])
      } else {
        MyResult.Error(null, R.string.token_user_not_found)
      }
    } catch (e: Exception) {
      Timber.e("Token get user error ${e.printStackTrace()}")
      MyResult.Error(null, R.string.token_user_error)
    }
  }

  suspend fun deleteUsername(): MyResult<Boolean> {
    return try {
      context.dataStore.edit { preferences ->
        preferences.remove(USER_NAME_KEY)
      }
      MyResult.Success(true)
    } catch (exception: IOException) {
      Timber.e("Error deleting access token ${exception.printStackTrace()}")
      MyResult.Error(false, R.string.token_user_error)
    }
  }

  override suspend fun saveAccessToken(value: String): MyResult<Boolean> =
    try {
      context.dataStore.edit { preferences ->
        preferences[JWT_ACCESS_KEY] = value
      }
      Timber.d("Access Token saved $value")
      MyResult.Success(true)
    } catch (exception: IOException) {
      Timber.e("Error saving access token ${exception.printStackTrace()}")
      MyResult.Error(false, R.string.token_user_save_error)
    }


  override suspend fun getAccessToken(): MyResult<String?> = try {
    val preferences = context.dataStore.data.first()
    val accessToken = preferences[JWT_REFRESH_KEY]
    if (accessToken != null) {
      MyResult.Success(preferences[JWT_ACCESS_KEY])
    } else {
      MyResult.Error(null, R.string.token_user_error)
    }
  } catch (e: Exception) {
    Timber.e("Error get access token ${e.printStackTrace()}")
    MyResult.Error(null, R.string.token_user_error)
  }

  override suspend fun deleteAccessToken(): MyResult<Boolean> = try {
    context.dataStore.edit { preferences ->
      preferences.remove(JWT_ACCESS_KEY)
    }
    MyResult.Success(true)
  } catch (exception: IOException) {
    Timber.e("Error deleting access token ${exception.printStackTrace()}")
    MyResult.Error(false, R.string.token_user_error)
  }

  override suspend fun saveRefreshToken(value: String): MyResult<Boolean> =
    try {
      context.dataStore.edit { preferences ->
        preferences[JWT_REFRESH_KEY] = value
      }

      Timber.d("Refresh Token saved $value")
      MyResult.Success(true)
    } catch (exception: IOException) {
      Timber.e("Error saving refresh token ${exception.printStackTrace()}")
      MyResult.Error(false, R.string.token_user_error)
    }

  override suspend fun getRefreshToken(): MyResult<String?> = try {
    val preferences = context.dataStore.data.first()
    MyResult.Success(preferences[JWT_REFRESH_KEY])
  } catch (e: Exception) {
    Timber.e("Error getting refresh token ${e.printStackTrace()}")
    MyResult.Error(null, R.string.token_user_error)
  }

  override suspend fun deleteRefreshToken(): MyResult<Boolean> = try {
    context.dataStore.edit { preferences ->
      preferences.remove(JWT_REFRESH_KEY)
    }
    MyResult.Success(true)
  } catch (exception: IOException) {
    Timber.e("Error deleting refresh token ${exception.printStackTrace()}")
    MyResult.Error(false, R.string.token_user_error)
  }


}