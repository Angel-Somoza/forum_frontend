package com.forum.frontend.presentation.login

import androidx.compose.runtime.Composable
import com.forum.frontend.domain.validation.LoginRules
import com.forum.frontend.ui.components.BrandLogo
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
import org.jetbrains.compose.web.dom.P
import org.jetbrains.compose.web.dom.Span
import org.jetbrains.compose.web.dom.Text

@Composable
fun LoginScreen(
    state: LoginUiState,
    onCarnetChange: (String) -> Unit,
    onPasswordChange: (String) -> Unit,
    onTogglePassword: () -> Unit,
    onLoginClick: () -> Unit,
) {
    Div({ classes("login-intro") }) {
        Div({ classes("intro-ring") })
        BrandLogo("logo-xl", "logo-split")
    }
    Div({ classes("login-screen") }) {
        Div({ classes("login-card") }) {
            Div({ classes("login-brand") }) {
                BrandLogo("logo-drop")
                Span { Text("Forum") }
            }
            H1({ classes("login-title") }) { Text("Hola de nuevo") }
            Form(attrs = {
                classes("login-form")
                attr("novalidate", "")
                addEventListener("submit") {
                    it.preventDefault()
                    onLoginClick()
                }
            }) {
                Div({ classes("field") }) {
                    Icon("badge")
                    Input(InputType.Text) {
                        id("carnet")
                        classes("field-input")
                        placeholder("Carnet")
                        attr("aria-label", "Carnet")
                        attr("autocomplete", "username")
                        attr("autocapitalize", "off")
                        attr("spellcheck", "false")
                        attr("maxlength", LoginRules.MAX_IDENTIFIER.toString())
                        value(state.carnet)
                        onInput { onCarnetChange(it.value) }
                    }
                }
                Div({ classes("field") }) {
                    Icon("lock")
                    Input(if (state.showPassword) InputType.Text else InputType.Password) {
                        id("password")
                        classes("field-input")
                        placeholder("Contraseña")
                        attr("aria-label", "Contraseña")
                        attr("autocomplete", "current-password")
                        attr("autocapitalize", "off")
                        attr("spellcheck", "false")
                        attr("maxlength", LoginRules.MAX_PASSWORD.toString())
                        value(state.password)
                        onInput { onPasswordChange(it.value) }
                    }
                    Button({
                        classes("link-button", "eye-button")
                        type(ButtonType.Button)
                        attr("aria-label", if (state.showPassword) "Ocultar" else "Mostrar")
                        onClick { onTogglePassword() }
                    }) { Icon(if (state.showPassword) "visibility_off" else "visibility") }
                }
                state.error?.let { message ->
                    P({
                        classes("form-error")
                        attr("role", "alert")
                    }) { Text(message) }
                }
                Button(attrs = {
                    classes("btn-primary")
                    type(ButtonType.Submit)
                    if (state.isLoading || state.isLocked) disabled()
                }) {
                    if (state.isLoading) {
                        Span({ classes("btn-dots") }) {
                            Span {}
                            Span {}
                            Span {}
                        }
                    } else {
                        Text("Entrar")
                    }
                }
            }
        }
    }
}
