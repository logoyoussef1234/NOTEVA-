package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.data.database.NoteDatabase
import com.example.data.repository.NoteRepository
import com.example.ui.screens.*
import com.example.ui.theme.MyApplicationTheme
import com.example.ui.viewmodel.NoteViewModel
import com.example.ui.viewmodel.NoteViewModelFactory

class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        // Core Room persistence initialization
        val database = NoteDatabase.getDatabase(applicationContext)
        val repository = NoteRepository(database.noteDao)
        val viewModel: NoteViewModel by viewModels { NoteViewModelFactory(repository, applicationContext) }

        setContent {
            val isDark by viewModel.darkModeEnabled.collectAsState()

            MyApplicationTheme(darkTheme = isDark) {
                val navController = rememberNavController()

                NavHost(
                    navController = navController,
                    startDestination = "splash",
                    modifier = Modifier.fillMaxSize()
                ) {
                    // 1. Splash Screen
                    composable("splash") {
                        SplashScreen(
                            isDark = isDark,
                            onNavigateNext = {
                                navController.navigate("onboarding") {
                                    popUpTo("splash") { inclusive = true }
                                }
                            }
                        )
                    }

                    // 2. Onboarding Screen
                    composable("onboarding") {
                        OnboardingScreen(
                            isDark = isDark,
                            onNavigateNext = {
                                navController.navigate("login") {
                                    popUpTo("onboarding") { inclusive = true }
                                }
                            }
                        )
                    }

                    // 3. Login / Signup Screen
                    composable("login") {
                        LoginScreen(
                            isDark = isDark,
                            onNavigateNext = {
                                navController.navigate("home") {
                                    popUpTo("login") { inclusive = true }
                                }
                            }
                        )
                    }

                    // 4. Home Note Grid Screen Workspace
                    composable("home") {
                        HomeScreen(
                            viewModel = viewModel,
                            onNavigateToEditor = { noteId ->
                                navController.navigate("editor/$noteId")
                            },
                            onNavigateToSettings = {
                                navController.navigate("settings")
                            },
                            onNavigateToProfile = {
                                navController.navigate("profile")
                            }
                        )
                    }

                    // 5. Note Editor Composer Screen
                    composable(
                        route = "editor/{noteId}",
                        arguments = listOf(navArgument("noteId") { type = NavType.IntType })
                    ) { backStackEntry ->
                        val noteId = backStackEntry.arguments?.getInt("noteId") ?: 0
                        NoteEditorScreen(
                            viewModel = viewModel,
                            noteId = noteId,
                            onNavigateBack = {
                                navController.popBackStack()
                            }
                        )
                    }

                    // 6. Settings Screen Workspace
                    composable("settings") {
                        SettingsScreen(
                            viewModel = viewModel,
                            onNavigateBack = {
                                navController.popBackStack()
                            }
                        )
                    }

                    // 7. Profile Screen workspace
                    composable("profile") {
                        ProfileScreen(
                            viewModel = viewModel,
                            onNavigateBack = {
                                navController.popBackStack()
                            }
                        )
                    }
                }
            }
        }
    }
}
