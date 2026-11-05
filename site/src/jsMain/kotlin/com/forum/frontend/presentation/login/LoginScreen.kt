package com.forum.frontend.presentation.login

import androidx.compose.runtime.Composable
import com.forum.frontend.ui.components.Icon
import org.jetbrains.compose.web.attributes.ButtonType
import org.jetbrains.compose.web.attributes.InputType
import org.jetbrains.compose.web.attributes.disabled
import org.jetbrains.compose.web.attributes.placeholder
import org.jetbrains.compose.web.attributes.type
import org.jetbrains.compose.web.dom.Button
import org.jetbrains.compose.web.dom.Div
import org.jetbrains.compose.web.dom.Form
import org.jetbrains.compose.web.dom.H1
import org.jetbrains.compose.web.dom.Input
import org.jetbrains.compose.web.dom.Label
import org.jetbrains.compose.web.dom.P
import org.jetbrains.compose.web.dom.Span
import org.jetbrains.compose.web.dom.Text

@Composable
fun LoginScreen(
    state: LoginUiState,
    onCarnetChange: (String) -> Unit,
    onPasswordChange: (String) -> Unit,
    onLoginClick: () -> Unit,
) {
    Div({ classes("login-screen") }) {
        Div({ classes("login-card") }) {
            Div({ classes("login-brand") }) {
                Icon("forum")
                Span { Text("Forum") }
            }
            H1({ classes("login-title") }) { Text("Bienvenido") }
            P({ classes("login-subtitle") }) { Text("Ingresa con tu carnet para continuar") }
            Form(attrs = {
                classes("login-form")
                addEventListener("submit") {
                    it.preventDefault()
                    onLoginClick()
                }
            }) {
                Label(forId = "carnet", attrs = { classes("field-label") }) { Text("Carnet") }
                Div({ classes("field") }) {
                    Icon("badge")
                    Input(InputType.Text) {
                        id("carnet")
                        classes("field-input")
                        placeholder("Ej. 2024-0001")
                        attr("autocomplete", "username")
                        value(state.carnet)
                        onInput { onCarnetChange(it.value) }
                    }
                }
                Label(forId = "password", attrs = { classes("field-label") }) { Text("Contraseña") }
                Div({ classes("field") }) {
                    Icon("lock")
                    Input(InputType.Password) {
                        id("password")
                        classes("field-input")
                        placeholder("Tu contraseña")
                        attr("autocomplete", "current-password")
                        value(state.password)
                        onInput { onPasswordChange(it.value) }
                    }
                }
                state.error?.let { message ->
                    P({ classes("form-error") }) { Text(message) }
                }
                Button(attrs = {
                    classes("btn-primary")
                    type(ButtonType.Submit)
                    if (state.isLoading) disabled()
                }) {
                    Text(if (state.isLoading) "Ingresando..." else "Iniciar sesión")
                }
            }
        }
    }
}