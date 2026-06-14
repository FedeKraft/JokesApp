package com.example.jokesapp.ui.profile

import android.app.Activity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Build
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material.icons.filled.DarkMode
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Language
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.Button
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.ListItem
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.VerticalDivider
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.jokesapp.R
import com.example.jokesapp.ui.theme.JokesAppTheme
import com.example.jokesapp.auth.AuthViewModel
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.common.api.ApiException

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProfileScreen(
    outerPadding: PaddingValues = PaddingValues(),
    isDarkTheme: Boolean,
    onDarkThemeChange: (Boolean) -> Unit,
    favoritesCount: Int = 0,
    jokesRead: Int = 0,
    coins: Int = 0,
    streak: Int = 0,
    authViewModel: AuthViewModel,
) {
    val currentUser by authViewModel.currentUser.collectAsState()

    val signInLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { result ->
        if (result.resultCode == Activity.RESULT_OK) {
            try {
                val account = GoogleSignIn.getSignedInAccountFromIntent(result.data)
                    .getResult(ApiException::class.java)
                account.idToken?.let { authViewModel.firebaseAuthWithGoogle(it) }
            } catch (_: ApiException) { }
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(title = {
                Text(stringResource(R.string.nav_profile), fontWeight = FontWeight.ExtraBold)
            })
        },
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(bottom = outerPadding.calculateBottomPadding())
                .verticalScroll(rememberScrollState())
                .padding(dimensionResource(R.dimen.padding_medium)),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {

            // ── Avatar ───────────────────────────────────────────────────────
            Surface(
                shape = CircleShape,
                color = MaterialTheme.colorScheme.primaryContainer,
                modifier = Modifier.size(dimensionResource(R.dimen.avatar_size)),
            ) {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    val initial = currentUser?.displayName?.firstOrNull()?.uppercaseChar()
                    if (initial != null) {
                        Text(
                            text = initial.toString(),
                            fontSize = 36.sp,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onPrimaryContainer,
                        )
                    } else {
                        Icon(
                            imageVector = Icons.Filled.Person,
                            contentDescription = null,
                            modifier = Modifier.size(dimensionResource(R.dimen.profile_icon_size)),
                            tint = MaterialTheme.colorScheme.onPrimaryContainer,
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Name + streak badge
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp),
            ) {
                Text(
                    text = currentUser?.displayName ?: stringResource(R.string.profile_username),
                    style = MaterialTheme.typography.headlineSmall,
                    fontWeight = FontWeight.Bold,
                )
                if (streak >= 2) {
                    Surface(
                        color = MaterialTheme.colorScheme.primaryContainer,
                        shape = RoundedCornerShape(12.dp),
                    ) {
                        Text(
                            text = "🔥 $streak",
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp),
                            style = MaterialTheme.typography.labelMedium,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onPrimaryContainer,
                        )
                    }
                }
            }

            Text(
                text = currentUser?.email ?: stringResource(R.string.profile_email),
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )

            Spacer(modifier = Modifier.height(dimensionResource(R.dimen.padding_medium)))

            // ── Google Sign-In ───────────────────────────────────────────────
            ElevatedCard(modifier = Modifier.fillMaxWidth()) {
                Column(
                    modifier = Modifier.padding(dimensionResource(R.dimen.padding_medium)),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(8.dp),
                ) {
                    if (currentUser != null) {
                        Text(
                            stringResource(R.string.profile_signed_in_as),
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                        )
                        Text(currentUser!!.email ?: "", style = MaterialTheme.typography.bodyMedium, fontWeight = FontWeight.Medium)
                        OutlinedButton(onClick = { authViewModel.signOut() }) {
                            Text(stringResource(R.string.btn_sign_out))
                        }
                    } else {
                        Text(
                            stringResource(R.string.profile_login_banner),
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            textAlign = TextAlign.Center,
                        )
                        Button(onClick = {
                            signInLauncher.launch(authViewModel.googleSignInClient.signInIntent)
                        }) {
                            Text(stringResource(R.string.btn_sign_in_google))
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(dimensionResource(R.dimen.padding_medium)))

            // ── Stats: Coins | Jokes Read | Favorites ────────────────────────
            ElevatedCard(modifier = Modifier.fillMaxWidth()) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = dimensionResource(R.dimen.padding_medium)),
                    horizontalArrangement = Arrangement.SpaceEvenly,
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    StatItem(value = "🪙 $coins", label = stringResource(R.string.stat_coins))
                    VerticalDivider(modifier = Modifier.height(40.dp))
                    StatItem(value = jokesRead.toString(), label = stringResource(R.string.stat_jokes_read))
                    VerticalDivider(modifier = Modifier.height(40.dp))
                    StatItem(value = favoritesCount.toString(), label = stringResource(R.string.stat_favorites))
                }
            }

            Spacer(modifier = Modifier.height(dimensionResource(R.dimen.padding_large)))

            // ── Settings ─────────────────────────────────────────────────────
            Text(
                stringResource(R.string.settings_title),
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.align(Alignment.Start).padding(bottom = dimensionResource(R.dimen.padding_small)),
            )

            ElevatedCard(modifier = Modifier.fillMaxWidth()) {
                Column {
                    ListItem(
                        headlineContent = { Text(stringResource(R.string.settings_dark_mode)) },
                        leadingContent = { Icon(Icons.Filled.DarkMode, contentDescription = null) },
                        trailingContent = { Switch(checked = isDarkTheme, onCheckedChange = onDarkThemeChange) },
                    )
                    HorizontalDivider()
                    ListItem(
                        headlineContent = { Text(stringResource(R.string.settings_language)) },
                        leadingContent = { Icon(Icons.Filled.Language, contentDescription = null) },
                        trailingContent = { Text(stringResource(R.string.settings_language_value), color = MaterialTheme.colorScheme.onSurfaceVariant) },
                    )
                    HorizontalDivider()
                    ListItem(
                        headlineContent = { Text(stringResource(R.string.settings_about)) },
                        leadingContent = { Icon(Icons.Filled.Info, contentDescription = null) },
                        trailingContent = { Icon(Icons.Filled.ChevronRight, contentDescription = null) },
                    )
                    HorizontalDivider()
                    ListItem(
                        headlineContent = { Text(stringResource(R.string.settings_version)) },
                        leadingContent = { Icon(Icons.Filled.Build, contentDescription = null) },
                        trailingContent = { Text(stringResource(R.string.settings_version_value), color = MaterialTheme.colorScheme.onSurfaceVariant) },
                    )
                }
            }

            Spacer(modifier = Modifier.height(dimensionResource(R.dimen.padding_medium)))
        }
    }
}

@Preview(showBackground = true, name = "Profile Screen")
@Composable
private fun ProfileScreenPreview() {
    JokesAppTheme {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            Surface(
                shape = CircleShape,
                color = MaterialTheme.colorScheme.primaryContainer,
                modifier = Modifier.size(96.dp),
            ) {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Text("F", fontSize = 36.sp, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.onPrimaryContainer)
                }
            }
            Spacer(modifier = Modifier.height(8.dp))
            Text("Federico", style = MaterialTheme.typography.headlineSmall, fontWeight = FontWeight.Bold)
            Text("federico@example.com", style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)
        }
    }
}

@Composable
private fun StatItem(value: String, label: String) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(
            text = value,
            style = MaterialTheme.typography.headlineMedium,
            fontWeight = FontWeight.ExtraBold,
            color = MaterialTheme.colorScheme.primary,
        )
        Text(
            text = label,
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
        )
    }
}
