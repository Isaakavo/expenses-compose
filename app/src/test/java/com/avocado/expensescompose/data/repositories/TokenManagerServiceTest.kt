package com.avocado.expensescompose.data.repositories

import android.content.Context
import androidx.datastore.preferences.core.MutablePreferences
import androidx.datastore.preferences.core.PreferenceDataStoreFactory
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import com.avocado.expensescompose.data.model.MyResult
import io.mockk.coEvery
import io.mockk.every
import io.mockk.mockk
import io.mockk.mockkStatic
import io.mockk.unmockkAll
import java.io.File
import kotlin.test.assertIs
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.TestScope
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

@OptIn(ExperimentalCoroutinesApi::class)
class TokenManagerServiceTest {

  private val context: Context = mockk(relaxed = true)

  val testScope = TestScope(UnconfinedTestDispatcher())
  private var dataStore = PreferenceDataStoreFactory.create(
    scope = testScope,
    produceFile = { File.createTempFile("test", ".preferences_pb") }
  )
  private val testDispatcher = StandardTestDispatcher()

  private lateinit var service: TokenManagerService

  @BeforeEach
  fun setup() {
    Dispatchers.setMain(testDispatcher)
    // Mock the extension property
    mockkStatic("com.avocado.expensescompose.data.repositories.TokenManagerServiceKt")
    every { context.dataStore } returns dataStore

    service = TokenManagerService(context)
  }

  @AfterEach
  fun tearDown() {
    Dispatchers.resetMain()
    unmockkAll()
  }

  @Test
  fun `saveUsername should store username and return success`() = runTest {
    val username = "isaak"

    coEvery { dataStore.edit(mockk(relaxed = true)) } coAnswers {
      // simulate editing prefs
      firstArg<suspend (MutablePreferences) -> Preferences>().invoke(mockk(relaxed = true))
    }

    val result = service.saveUsername(username)

    assertIs<MyResult.Success<Unit>>(result)
  }

  @Test
  fun `getUsername should return success with username`() = runTest {
    dataStore.edit {
      it[stringPreferencesKey("USERNAME_KEY")] = "isaak"
    }

    val result = service.getUsername()

    assertIs<MyResult.Success<String>>(result)
    assertEquals("isaak", (result as MyResult.Success).data)
  }

  @Test
  fun `getUsername should return error when username missing`() = runTest {
    val result = service.getUsername()

    assertIs<MyResult.Error<String?>>(result)
    assertEquals(null, result.data)
  }

  @Test
  fun `deleteUsername should return success when preferences updated`() = runTest {
    coEvery { dataStore.edit(mockk()) } coAnswers {
      firstArg<suspend (MutablePreferences) -> Preferences>().invoke(mockk(relaxed = true))
    }

    val result = service.deleteUsername()

    assertIs<MyResult.Success<Unit>>(result)
  }

//  @Test
//  fun `deleteUsername should return error on exception`() = runTest {
//    val brokenDataStore = object : DataStore<Preferences> by dataStore {
//      override suspend fun updateData(transform: suspend (Preferences) -> Preferences): Preferences {
//        throw IOException("Simulated IO error")
//      }
//    }
//
//    dataStore = brokenDataStore
//
//    val result = service.deleteUsername()
//
//    assertIs<MyResult.Error<Unit>>(result)
//    assert(result.exception is IOException)
//  }
}
