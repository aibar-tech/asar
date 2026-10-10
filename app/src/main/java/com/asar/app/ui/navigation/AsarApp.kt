package com.asar.app.ui.navigation

import androidx.activity.compose.BackHandler
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.consumeWindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Snackbar
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.saveable.rememberSaveableStateHolder
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.asar.app.data.model.Role
import com.asar.app.ui.AsarViewModel
import com.asar.app.ui.screens.ai.AiLabScreen
import com.asar.app.ui.screens.create.CreateTaskScreen
import com.asar.app.ui.screens.details.TaskDetailsScreen
import com.asar.app.ui.screens.home.HomeScreen
import com.asar.app.ui.screens.mytasks.MyTasksScreen
import com.asar.app.ui.screens.profile.ProfileScreen
import com.asar.app.ui.screens.welcome.AuthMode
import com.asar.app.ui.screens.welcome.AuthScreen
import com.asar.app.ui.screens.welcome.WelcomeScreen
import com.asar.app.ui.theme.AsarBackground
import com.asar.app.ui.theme.AsarInk
import com.asar.app.ui.theme.AsarSurface
import com.asar.app.ui.theme.AsarTheme

@Composable
fun AsarApp(vm: AsarViewModel = viewModel()) {
    AsarTheme {
        Box(Modifier.fillMaxSize().background(AsarBackground)) {
            AnimatedContent(
                targetState = vm.session != null,
                transitionSpec = { fadeIn(tween(400)) togetherWith fadeOut(tween(250)) },
                label = "root",
            ) { signedIn ->
                if (signedIn) MainShell(vm) else AuthFlow(vm)
            }
        }
    }
}

@Composable
private fun AuthFlow(vm: AsarViewModel) {
    var authMode by rememberSaveable { mutableStateOf<AuthMode?>(null) }
    BackHandler(enabled = authMode != null) { authMode = null }

    AnimatedContent(
        targetState = authMode,
        transitionSpec = {
            val forward = targetState != null
            (slideInHorizontally(tween(320)) { if (forward) it / 4 else -it / 4 } + fadeIn(tween(320))) togetherWith
                (slideOutHorizontally(tween(260)) { if (forward) -it / 4 else it / 4 } + fadeOut(tween(200)))
        },
        label = "auth",
    ) { mode ->
        if (mode == null) {
            WelcomeScreen(
                onCreateAccount = { authMode = AuthMode.REGISTER },
                onLogin = { authMode = AuthMode.LOGIN },
                onDemo = vm::enterDemo,
            )
        } else {
            AuthScreen(
                initialMode = mode,
                onBack = { authMode = null },
                onRegister = vm::register,
                onLogin = vm::login,
            )
        }
    }
}

@Composable
private fun MainShell(vm: AsarViewModel) {
    var tab by rememberSaveable { mutableStateOf(MainTab.HOME) }
    val stack = remember { mutableStateListOf<Overlay>() }
    val snackbar = remember { SnackbarHostState() }
    val tabStates = rememberSaveableStateHolder()

    val push: (Overlay) -> Unit = { stack.add(it) }
    val pop: () -> Unit = { if (stack.isNotEmpty()) stack.removeAt(stack.lastIndex) }
    val openTask: (String) -> Unit = { push(Overlay.TaskDetails(it)) }
    val applyTo: (String) -> Unit = { push(Overlay.TaskDetails(it, openApply = true)) }

    BackHandler(enabled = stack.isNotEmpty() || tab != MainTab.HOME) {
        if (stack.isNotEmpty()) pop() else tab = MainTab.HOME
    }

    val message = vm.message
    LaunchedEffect(message) {
        if (message != null) {
            snackbar.currentSnackbarData?.dismiss()
            snackbar.showSnackbar(message.text)
            vm.consumeMessage(message)
        }
    }

    val pendingForMe = vm.myRequests.filter { !it.isCompleted }.sumOf { it.pendingCount }

    Box(Modifier.fillMaxSize()) {
        Scaffold(
            containerColor = AsarBackground,
            contentWindowInsets = WindowInsets(0, 0, 0, 0),
            bottomBar = {
                AsarBottomBar(
                    selected = tab,
                    onSelect = { tab = it },
                    badges = mapOf(MainTab.MY_TASKS to if (vm.role == Role.REQUESTER) pendingForMe else 0),
                )
            },
        ) { padding ->
            AnimatedContent(
                targetState = tab,
                transitionSpec = { fadeIn(tween(220, delayMillis = 60)) togetherWith fadeOut(tween(120)) },
                modifier = Modifier.padding(padding).consumeWindowInsets(padding),
                label = "tabs",
            ) { current ->
                tabStates.SaveableStateProvider(current.name) {
                    when (current) {
                        MainTab.HOME -> HomeScreen(
                            vm = vm,
                            onOpenTask = openTask,
                            onApply = applyTo,
                            onCreate = { tab = MainTab.CREATE },
                            onOpenMyTasks = { tab = MainTab.MY_TASKS },
                        )
                        MainTab.MY_TASKS -> MyTasksScreen(
                            vm = vm,
                            onOpenTask = openTask,
                            onBrowse = { tab = MainTab.HOME },
                            onCreate = { tab = MainTab.CREATE },
                        )
                        MainTab.CREATE -> CreateTaskScreen(
                            vm = vm,
                            onPublished = { tab = MainTab.HOME },
                        )
                        MainTab.PROFILE -> ProfileScreen(
                            vm = vm,
                            onOpenAiLab = { push(Overlay.AiLab) },
                            onOpenTask = openTask,
                        )
                    }
                }
            }
        }

        // Pushed screens slide over the tab shell.
        AnimatedContent(
            targetState = stack.lastOrNull(),
            transitionSpec = {
                val forward = targetState != null && (initialState == null || stack.size > 1)
                if (forward) {
                    slideInHorizontally(tween(320)) { it } togetherWith fadeOut(tween(200))
                } else {
                    fadeIn(tween(200)) togetherWith slideOutHorizontally(tween(280)) { it }
                }
            },
            label = "overlay",
        ) { overlay ->
            if (overlay == null) {
                // Empty layer: no pointer input, so touches reach the tabs underneath.
                Box(Modifier.fillMaxSize())
            } else {
                // Surface consumes touches so nothing leaks to the screen below.
                Surface(Modifier.fillMaxSize(), color = AsarBackground) {
                    when (overlay) {
                        is Overlay.TaskDetails -> TaskDetailsScreen(
                            vm = vm,
                            taskId = overlay.taskId,
                            openApplyOnStart = overlay.openApply,
                            onBack = pop,
                        )
                        Overlay.AiLab -> AiLabScreen(vm = vm, onBack = pop)
                    }
                }
            }
        }

        SnackbarHost(
            hostState = snackbar,
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .navigationBarsPadding()
                .padding(bottom = 80.dp),
        ) { data ->
            Snackbar(
                containerColor = AsarInk,
                contentColor = AsarSurface,
                shape = RoundedCornerShape(16.dp),
                modifier = Modifier.padding(horizontal = 16.dp),
            ) {
                Text(data.visuals.message, style = MaterialTheme.typography.bodyMedium)
            }
        }
    }
}
