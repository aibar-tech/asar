package com.asar.app.ui.screens.welcome

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Lock
import androidx.compose.material.icons.rounded.Mail
import androidx.compose.material.icons.rounded.Person
import androidx.compose.material.icons.rounded.Shield
import androidx.compose.material.icons.rounded.Visibility
import androidx.compose.material.icons.rounded.VisibilityOff
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import com.asar.app.data.DemoAuth
import com.asar.app.ui.components.AsarTextField
import com.asar.app.ui.components.AsarTopBar
import com.asar.app.ui.components.PrimaryCta
import com.asar.app.ui.components.QuietButton
import com.asar.app.ui.components.Segment
import com.asar.app.ui.components.SegmentedControl
import com.asar.app.ui.theme.AsarAmber
import com.asar.app.ui.theme.AsarBackground
import com.asar.app.ui.theme.AsarDanger
import com.asar.app.ui.theme.AsarDangerSoft
import com.asar.app.ui.theme.AsarInk
import com.asar.app.ui.theme.AsarInkMuted
import com.asar.app.ui.theme.AsarSandSoft
import com.asar.app.ui.theme.Spacing

enum class AuthMode { REGISTER, LOGIN }

/**
 * Demo-only sign up / sign in. Callbacks return an error message or null on success.
 */
@Composable
fun AuthScreen(
    initialMode: AuthMode,
    onBack: () -> Unit,
    onRegister: (name: String, email: String, password: String) -> String?,
    onLogin: (email: String, password: String) -> String?,
) {
    var mode by rememberSaveable { mutableStateOf(initialMode) }
    var name by rememberSaveable { mutableStateOf("") }
    var email by rememberSaveable { mutableStateOf("") }
    var password by rememberSaveable { mutableStateOf("") }
    var showPassword by rememberSaveable { mutableStateOf(false) }
    var error by rememberSaveable { mutableStateOf<String?>(null) }

    val submit = {
        error = if (mode == AuthMode.REGISTER) onRegister(name, email, password) else onLogin(email, password)
    }

    Column(
        Modifier
            .fillMaxSize()
            .background(AsarBackground)
            .statusBarsPadding()
            .navigationBarsPadding()
            .imePadding()
    ) {
        AsarTopBar(title = "", onBack = onBack)
        Column(
            Modifier
                .weight(1f)
                .verticalScroll(rememberScrollState())
                .padding(horizontal = Spacing.xl)
        ) {
            AnimatedContent(
                targetState = mode,
                transitionSpec = { fadeIn() togetherWith fadeOut() },
                label = "authTitle",
            ) { m ->
                Column {
                    Text(
                        if (m == AuthMode.REGISTER) "Join your neighbours" else "Welcome back",
                        style = MaterialTheme.typography.headlineLarge,
                    )
                    Spacer(Modifier.height(Spacing.xs))
                    Text(
                        if (m == AuthMode.REGISTER) "Create a profile to request help or volunteer nearby."
                        else "Sign in to continue helping your community.",
                        style = MaterialTheme.typography.bodyLarge,
                        color = AsarInkMuted,
                    )
                }
            }
            Spacer(Modifier.height(Spacing.xl))
            SegmentedControl(
                options = listOf(Segment("Sign up"), Segment("Sign in")),
                selectedIndex = mode.ordinal,
                onSelect = { mode = AuthMode.entries[it]; error = null },
            )
            Spacer(Modifier.height(Spacing.xl))

            AnimatedVisibility(
                visible = mode == AuthMode.REGISTER,
                enter = fadeIn() + expandVertically(),
                exit = fadeOut() + shrinkVertically(),
            ) {
                Column {
                    AsarTextField(
                        value = name,
                        onValueChange = { name = it; error = null },
                        label = "Full name",
                        placeholder = "e.g. Aigerim Bekova",
                        leadingIcon = { Icon(Icons.Rounded.Person, null) },
                        keyboardOptions = KeyboardOptions(imeAction = ImeAction.Next),
                    )
                    Spacer(Modifier.height(Spacing.md))
                }
            }
            AsarTextField(
                value = email,
                onValueChange = { email = it; error = null },
                label = "Email",
                placeholder = "you@example.kz",
                leadingIcon = { Icon(Icons.Rounded.Mail, null) },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email, imeAction = ImeAction.Next),
            )
            Spacer(Modifier.height(Spacing.md))
            AsarTextField(
                value = password,
                onValueChange = { password = it; error = null },
                label = "Password",
                placeholder = "At least 6 characters",
                leadingIcon = { Icon(Icons.Rounded.Lock, null) },
                trailingIcon = {
                    IconButton(onClick = { showPassword = !showPassword }) {
                        Icon(
                            if (showPassword) Icons.Rounded.VisibilityOff else Icons.Rounded.Visibility,
                            contentDescription = if (showPassword) "Hide password" else "Show password",
                            tint = AsarInkMuted,
                        )
                    }
                },
                visualTransformation = if (showPassword) VisualTransformation.None else PasswordVisualTransformation(),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password, imeAction = ImeAction.Done),
            )

            AnimatedVisibility(visible = error != null) {
                Text(
                    error.orEmpty(),
                    style = MaterialTheme.typography.bodyMedium,
                    color = AsarDanger,
                    modifier = Modifier
                        .padding(top = Spacing.md)
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(12.dp))
                        .background(AsarDangerSoft)
                        .padding(Spacing.sm),
                )
            }

            Spacer(Modifier.height(Spacing.lg))
            DemoNotice(onFill = {
                mode = AuthMode.LOGIN
                email = DemoAuth.DEMO_EMAIL
                password = DemoAuth.DEMO_PASSWORD
                error = null
            })
            Spacer(Modifier.height(Spacing.lg))
        }

        Column(Modifier.padding(horizontal = Spacing.xl, vertical = Spacing.md)) {
            PrimaryCta(
                text = if (mode == AuthMode.REGISTER) "Create account" else "Sign in",
                onClick = submit,
            )
            QuietButton(
                text = if (mode == AuthMode.REGISTER) "Already have an account? Sign in" else "New to ASAR? Create an account",
                onClick = {
                    mode = if (mode == AuthMode.REGISTER) AuthMode.LOGIN else AuthMode.REGISTER
                    error = null
                },
                modifier = Modifier.align(Alignment.CenterHorizontally),
                color = AsarInk,
            )
        }
    }
}

@Composable
private fun DemoNotice(onFill: () -> Unit) {
    Column(
        Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(18.dp))
            .background(AsarSandSoft)
            .padding(Spacing.md)
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(Icons.Rounded.Shield, null, tint = AsarAmber, modifier = Modifier.size(18.dp))
            Spacer(Modifier.width(Spacing.xs))
            Text("Demo authentication", style = MaterialTheme.typography.titleSmall, color = AsarAmber)
        }
        Spacer(Modifier.height(Spacing.xxs))
        Text(
            "This prototype has no server. Accounts exist only in memory on this device and are " +
                "not secure — please don't use a real password.",
            style = MaterialTheme.typography.bodySmall,
            color = AsarInk,
        )
        Spacer(Modifier.height(Spacing.xs))
        Text(
            "Use demo credentials  →  ${DemoAuth.DEMO_EMAIL}",
            style = MaterialTheme.typography.labelLarge,
            color = AsarAmber,
            modifier = Modifier
                .clip(RoundedCornerShape(8.dp))
                .clickable(onClick = onFill)
                .padding(vertical = 4.dp),
        )
    }
}
