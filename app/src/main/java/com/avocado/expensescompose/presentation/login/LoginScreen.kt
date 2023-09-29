package com.avocado.expensescompose.presentation.login

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import com.avocado.expensescompose.R
import com.avocado.expensescompose.presentation.RoutesConstants


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LoginScreen(
    navController: NavHostController, viewModel: LoginViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()

    Box(modifier = Modifier.fillMaxSize()) {
        Column(
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.spacedBy(8.dp, Alignment.CenterVertically),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(text = "Iniciar Sesión", modifier = Modifier)
            OutlinedTextField(
                value = uiState.username,
                onValueChange = viewModel::updateUsername,
                placeholder = { Text(text = "User name") },
            )
            OutlinedTextField(
                value = uiState.password,
                onValueChange = viewModel::updatePassword,
                placeholder = { Text(text = "Password") },
                trailingIcon = {
                    IconButton(onClick = { viewModel.onToggleViewPassword() }) {
                        if (uiState.shouldShowPassword) Icon(
                            painter = painterResource(id = R.drawable.baseline_visibility_24),
                            contentDescription = "Mostrar contraseña"
                        )
                        else Icon(
                            painter = painterResource(id = R.drawable.baseline_visibility_off_24),
                            contentDescription = "Ocultar contraseña"
                        )
                    }
                },
                visualTransformation = if (uiState.shouldShowPassword) VisualTransformation.None else PasswordVisualTransformation()
            )

            Button(onClick = { viewModel.login() }) {
                Row(horizontalArrangement = Arrangement.SpaceBetween) {
                    if (uiState.isLoading) {
                        CircularProgressIndicator(
                            color = Color.White,
                            strokeWidth = 2.dp,
                            modifier = Modifier
                                .size(18.dp)
                                .padding(end = 4.dp)
                        )
                    }
                    Text(text = "Iniciar Sesión")
                }

            }

            if (uiState.userMessage?.isNotBlank() == true) {
                Text(text = uiState.userMessage!!)
            }

            if (uiState.isSuccess) {
                navController.navigate(RoutesConstants.INCOME_OVERVIEW)
            }
        }
    }
}
