package com.example.ui.screens

import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.components.PremiumBackground
import kotlinx.coroutines.delay

@Composable
fun SplashScreen(
    isDark: Boolean,
    onNavigateNext: () -> Unit
) {
    var startAnimate by remember { mutableStateOf(false) }
    val scale by animateFloatAsState(
        targetValue = if (startAnimate) 1.2f else 0.8f,
        animationSpec = spring(dampingRatio = Spring.DampingRatioMediumBouncy, stiffness = Spring.StiffnessLow),
        label = "LogoScale"
    )
    val opacity by animateFloatAsState(
        targetValue = if (startAnimate) 1f else 0f,
        animationSpec = tween(1200),
        label = "LogoOpacity"
    )

    LaunchedEffect(Unit) {
        startAnimate = true
        delay(2200) // Beautiful cinematic entrance
        onNavigateNext()
    }

    PremiumBackground(isDark = isDark) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(24.dp),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Elegant brand halo circle
            Box(
                modifier = Modifier
                    .size(160.dp)
                    .clip(CircleShape)
                    .background(
                        Brush.radialGradient(
                            colors = listOf(
                                Color(0xFFD0BCFF).copy(alpha = 0.2f),
                                Color.Transparent
                            )
                        )
                    )
                    .border(
                        1.dp,
                        Brush.sweepGradient(
                            colors = listOf(Color(0xFFD0BCFF), Color(0xFF381E72), Color(0xFFD0BCFF))
                        ),
                        CircleShape
                    ),
                contentAlignment = Alignment.Center
            ) {
                // Sleek artistic geometric N symbol
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier.rotate(15f)
                ) {
                    Icon(
                        imageVector = Icons.Default.EditNote,
                        contentDescription = "NOTEVA Tech Badge",
                        tint = Color(0xFFD0BCFF),
                        modifier = Modifier.size(64.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(32.dp))

            Text(
                text = "NOTEVA",
                fontSize = 42.sp,
                fontWeight = FontWeight.ExtraBold,
                color = if (isDark) Color.White else Color(0xFF0F121F),
                letterSpacing = 8.sp,
                textAlign = TextAlign.Center,
                modifier = Modifier.animateContentSize()
            )

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = "PERSONAL VAULT",
                fontSize = 11.sp,
                fontWeight = FontWeight.Bold,
                color = Color(0xFFD0BCFF),
                letterSpacing = 4.sp,
                textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.height(64.dp))

            CircularProgressIndicator(
                color = Color(0xFFD0BCFF),
                strokeWidth = 2.dp,
                modifier = Modifier.size(24.dp)
            )
        }
    }
}

data class OnboardingPageData(
    val title: String,
    val description: String,
    val icon: ImageVector,
    val accentColor: Color
)

@Composable
fun OnboardingScreen(
    isDark: Boolean,
    onNavigateNext: () -> Unit
) {
    val pages = listOf(
        OnboardingPageData(
            title = "Capture At Speed",
            description = "Noteva stores checklists, image highlights, and instant thoughts offline. Auto-saved so you never lose a concept.",
            icon = Icons.Default.ElectricBolt,
            accentColor = Color(0xFFD0BCFF)
        ),
        OnboardingPageData(
            title = "Neural Grid Sorting",
            description = "Organize with drag-and-drop mechanics. Group by premium tags or pin archives using fluid custom gestures.",
            icon = Icons.Default.AutoAwesomeMosaic,
            accentColor = Color(0xFFCCC2DC)
        ),
        OnboardingPageData(
            title = "Biometric Passcode",
            description = "Secure highly individual notes directly through custom finger locks and private PIN structures.",
            icon = Icons.Default.Fingerprint,
            accentColor = Color(0xFFD0BCFF)
        )
    )

    var currentPage by remember { mutableStateOf(0) }
    val page = pages[currentPage]

    PremiumBackground(isDark = isDark) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .navigationBarsPadding()
                .statusBarsPadding()
                .padding(24.dp),
            verticalArrangement = Arrangement.SpaceBetween,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Elegant top skip button
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.End
            ) {
                Text(
                    text = "SKIP",
                    color = Color.Gray,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold,
                    letterSpacing = 2.sp,
                    modifier = Modifier
                        .clickable { onNavigateNext() }
                        .padding(12.dp)
                )
            }

            // Visual Center Illustration
            Box(
                modifier = Modifier
                    .size(220.dp)
                    .clip(RoundedCornerShape(32.dp))
                    .background(Color(0x1B1F2E).copy(alpha = 0.5f))
                    .border(
                        1.dp,
                        Brush.linearGradient(
                            listOf(Color.White.copy(alpha = 0.15f), page.accentColor.copy(alpha = 0.3f))
                        ),
                        RoundedCornerShape(32.dp)
                    ),
                contentAlignment = Alignment.Center
            ) {
                // Interactive animation logo scale
                Icon(
                    imageVector = page.icon,
                    contentDescription = null,
                    tint = page.accentColor,
                    modifier = Modifier.size(96.dp)
                )
            }

            // Description texts
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier.padding(horizontal = 16.dp)
            ) {
                Text(
                    text = page.title,
                    fontSize = 32.sp,
                    fontWeight = FontWeight.ExtraBold,
                    color = if (isDark) Color.White else Color(0xFF13141C),
                    textAlign = TextAlign.Center
                )

                Spacer(modifier = Modifier.height(16.dp))

                Text(
                    text = page.description,
                    fontSize = 15.sp,
                    fontWeight = FontWeight.Light,
                    color = Color.Gray,
                    textAlign = TextAlign.Center,
                    lineHeight = 22.sp
                )
            }

            // Bottom elements: Slide indicator dots & Action button
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 16.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Indicator dots
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    pages.forEachIndexed { index, _ ->
                        Box(
                            modifier = Modifier
                                .size(if (index == currentPage) 24.dp else 8.dp, 8.dp)
                                .clip(CircleShape)
                                .background(if (index == currentPage) page.accentColor else Color.Gray.copy(alpha = 0.3f))
                        )
                    }
                }

                // Call to action button
                Button(
                    onClick = {
                        if (currentPage < pages.lastIndex) {
                            currentPage++
                        } else {
                            onNavigateNext()
                        }
                    },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = page.accentColor,
                        contentColor = Color.Black
                    ),
                    shape = RoundedCornerShape(16.dp),
                    contentPadding = PaddingValues(horizontal = 24.dp, vertical = 14.dp)
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Text(
                            text = if (currentPage == pages.lastIndex) "GET STARTED" else "NEXT",
                            fontWeight = FontWeight.Bold,
                            fontSize = 13.sp,
                            letterSpacing = 1.sp
                        )
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowForward,
                            contentDescription = "Arrow Forward",
                            modifier = Modifier.size(16.dp)
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun LoginScreen(
    isDark: Boolean,
    onNavigateNext: () -> Unit
) {
    var email by remember { mutableStateOf("logoyoussef173@gmail.com") }
    var password by remember { mutableStateOf("••••••••") }
    var isSignUp by remember { mutableStateOf(false) }

    PremiumBackground(isDark = isDark) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .navigationBarsPadding()
                .statusBarsPadding()
                .padding(24.dp),
            verticalArrangement = Arrangement.SpaceBetween,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Header Group
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier.padding(top = 24.dp)
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.EditNote,
                        contentDescription = "Logo",
                        tint = Color(0xFFD0BCFF),
                        modifier = Modifier.size(36.dp)
                    )
                    Text(
                        text = "NOTEVA",
                        fontSize = 24.sp,
                        fontWeight = FontWeight.ExtraBold,
                        color = if (isDark) Color.White else Color(0xFF0F121F),
                        letterSpacing = 4.sp
                    )
                }

                Spacer(modifier = Modifier.height(12.dp))

                Text(
                    text = if (isSignUp) "Create your productivity workspace." else "Welcome back. Work smarter.",
                    fontSize = 14.sp,
                    color = Color.Gray,
                    textAlign = TextAlign.Center
                )
            }

            // Form inputs Group
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 8.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                OutlinedTextField(
                    value = email,
                    onValueChange = { email = it },
                    label = { Text("Workspace Email") },
                    leadingIcon = { Icon(Icons.Default.Email, contentDescription = null, tint = Color(0xFFD0BCFF)) },
                    singleLine = true,
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = Color(0xFFD0BCFF),
                        unfocusedBorderColor = Color.Gray.copy(alpha = 0.4f),
                        focusedLabelColor = Color(0xFFD0BCFF)
                    ),
                    shape = RoundedCornerShape(16.dp),
                    modifier = Modifier.fillMaxWidth()
                )

                OutlinedTextField(
                    value = password,
                    onValueChange = { password = it },
                    label = { Text("Password PIN") },
                    leadingIcon = { Icon(Icons.Default.Lock, contentDescription = null, tint = Color(0xFFD0BCFF)) },
                    singleLine = true,
                    visualTransformation = PasswordVisualTransformation(),
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = Color(0xFFD0BCFF),
                        unfocusedBorderColor = Color.Gray.copy(alpha = 0.4f),
                        focusedLabelColor = Color(0xFFD0BCFF)
                    ),
                    shape = RoundedCornerShape(16.dp),
                    modifier = Modifier.fillMaxWidth()
                )

                if (!isSignUp) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.End
                    ) {
                        Text(
                            text = "Forgot passcode?",
                            color = Color(0xFFD0BCFF),
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Medium
                        )
                    }
                }

                Spacer(modifier = Modifier.height(8.dp))

                Button(
                    onClick = { onNavigateNext() },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = if (isDark) Color(0xFFD0BCFF) else Color(0xFF0F121F),
                        contentColor = if (isDark) Color(0xFF381E72) else Color.White
                    ),
                    shape = RoundedCornerShape(16.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(54.dp)
                ) {
                    Text(
                        text = if (isSignUp) "CREATE ACCOUNT" else "CONTINUE TO WORKSPACE",
                        fontWeight = FontWeight.Bold,
                        letterSpacing = 1.sp
                    )
                }

                // Premium Guest Bypass
                OutlinedButton(
                    onClick = { onNavigateNext() },
                    shape = RoundedCornerShape(16.dp),
                    border = BorderStroke(1.dp, Color(0xFFD0BCFF).copy(alpha = 0.4f)),
                    colors = ButtonDefaults.outlinedButtonColors(contentColor = Color(0xFFD0BCFF)),
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(54.dp)
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Icon(Icons.Default.Launch, contentDescription = null, modifier = Modifier.size(18.dp))
                        Text(
                            text = "CONTINUE AS GUEST",
                            fontWeight = FontWeight.Bold,
                            letterSpacing = 1.sp
                        )
                    }
                }
            }

            // Bottom Switcher
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 12.dp),
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = if (isSignUp) "Already have an account?" else "New to Noteva?",
                    color = Color.Gray,
                    fontSize = 14.sp
                )
                Spacer(modifier = Modifier.width(6.dp))
                Text(
                    text = if (isSignUp) "Sign In" else "Sign Up",
                    color = Color(0xFFD0BCFF),
                    fontWeight = FontWeight.Bold,
                    fontSize = 14.sp,
                    modifier = Modifier
                        .clickable { isSignUp = !isSignUp }
                        .padding(4.dp)
                )
            }
        }
    }
}
