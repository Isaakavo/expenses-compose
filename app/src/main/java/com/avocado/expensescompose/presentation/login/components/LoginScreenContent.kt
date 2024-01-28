package com.avocado.expensescompose.presentation.login.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.avocado.expensescompose.R
import com.avocado.expensescompose.presentation.login.LoginEvent

@Composable
fun LoginScreenContent(
  username: String,
  password: String,
  userMessage: Int?,
  shouldShowPassword: Boolean,
  isQuickLogin: Boolean,
  isLoading: Boolean,
  onEvent: (event: LoginEvent, value: String) -> Unit
) {
  Surface {
    Box(modifier = Modifier.fillMaxSize()) {
      Column(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.spacedBy(8.dp, Alignment.CenterVertically),
        horizontalAlignment = Alignment.CenterHorizontally
      ) {
        Text(text = stringResource(id = R.string.login_login), modifier = Modifier)
        OutlinedTextField(
          value = username,
          onValueChange = { onEvent(LoginEvent.UpdateUsername, it) },
          placeholder = { Text(text = stringResource(id = R.string.login_user)) }
        )
        OutlinedTextField(
          value = password,
          onValueChange = { onEvent(LoginEvent.UpdatePassword, it) },
          placeholder = { Text(text = stringResource(id = R.string.login_password)) },
          keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
          enabled = !isQuickLogin,
          trailingIcon = {
            IconButton(onClick = { onEvent(LoginEvent.ToggleViewPassword, "") }) {
              if (shouldShowPassword) {
                Icon(
                  painter = painterResource(id = R.drawable.baseline_visibility_24),
                  contentDescription = stringResource(id = R.string.login_show_password)
                )
              } else {
                Icon(
                  painter = painterResource(id = R.drawable.baseline_visibility_off_24),
                  contentDescription = stringResource(id = R.string.login_hide_password)
                )
              }
            }
          },
          visualTransformation = if (shouldShowPassword) VisualTransformation.None else PasswordVisualTransformation()
        )

        Button(enabled = !isLoading || !isQuickLogin, onClick = { onEvent(LoginEvent.Login, "") }) {
          Row(horizontalArrangement = Arrangement.SpaceBetween) {
            if (isLoading) {
              CircularProgressIndicator(
                color = Color.White,
                strokeWidth = 2.dp,
                modifier = Modifier
                  .size(18.dp)
                  .padding(top = 1.dp, end = 4.dp)
              )
            }
            Text(text = stringResource(id = R.string.login_login))
          }
        }

        userMessage.takeIf { it != 0 }?.let {
          Text(text = stringResource(it))
        }

        if (isQuickLogin) {
          Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.Center) {
            TextButton(onClick = { onEvent(LoginEvent.ChangeUser, "") }) {
              Text(text = stringResource(id = R.string.login_change_user), fontSize = 14.sp)
            }
          }
        }
      }
    }
  }
}
