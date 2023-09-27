package com.avocado.expensescompose.data.repositories

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import com.avocado.expensescompose.data.AuthDataStore
import kotlinx.coroutines.flow.first
import javax.inject.Inject

val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "auth")

class DataStoreRepository @Inject constructor(private val context: Context) : AuthDataStore {
    override suspend fun putString(key: String, value: String) {
        val preferencesKey = stringPreferencesKey(key)
        context.dataStore.edit { preferences ->
            preferences[preferencesKey] = value
        }
    }

    override suspend fun getString(key: String): String? = try {
        val preferencesKey = stringPreferencesKey(key)
        val preferences = context.dataStore.data.first()
        preferences[preferencesKey]
    } catch (e: Exception) {
        e.printStackTrace()
        null
    }


}