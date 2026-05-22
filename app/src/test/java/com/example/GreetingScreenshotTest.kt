package com.example

import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onRoot
import androidx.navigation.compose.composable
import com.example.ui.screens.SplashScreen
import com.example.ui.screens.OnboardingScreen
import com.example.ui.screens.LoginScreen
import com.example.ui.theme.MyApplicationTheme
import com.github.takahirom.roborazzi.RobolectricDeviceQualifiers
import com.github.takahirom.roborazzi.captureRoboImage
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config
import org.robolectric.annotation.GraphicsMode

@RunWith(RobolectricTestRunner::class)
@GraphicsMode(GraphicsMode.Mode.NATIVE)
@Config(qualifiers = RobolectricDeviceQualifiers.Pixel8, sdk = [36])
class GreetingScreenshotTest {

  @get:Rule val composeTestRule = createComposeRule()

  @Test
  fun test_full_navigation_flow() {
    composeTestRule.setContent {
      MyApplicationTheme(darkTheme = true) {
        val navController = androidx.navigation.compose.rememberNavController()
        androidx.navigation.compose.NavHost(
            navController = navController,
            startDestination = "splash"
        ) {
            composable("splash") {
                SplashScreen(isDark = true, onNavigateNext = {
                    navController.navigate("onboarding") {
                        popUpTo("splash") { inclusive = true }
                    }
                })
            }
            composable("onboarding") {
                OnboardingScreen(isDark = true, onNavigateNext = {
                    navController.navigate("login") {
                        popUpTo("onboarding") { inclusive = true }
                    }
                })
            }
            composable("login") {
                LoginScreen(isDark = true, onNavigateNext = {})
            }
        }
      }
    }

    // Advance time to pass the 2200ms delay in SplashScreen
    composeTestRule.mainClock.advanceTimeBy(3000)
    composeTestRule.waitForIdle()

    // Now we are on onboarding, click NEXT button or skip to check login
    // Let's advance clock more to be sure everything idle
    composeTestRule.mainClock.advanceTimeBy(1000)
    composeTestRule.waitForIdle()
  }
}
