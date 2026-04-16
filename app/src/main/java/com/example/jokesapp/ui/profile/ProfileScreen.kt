package com.example.jokesapp.ui.profile

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
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Build
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material.icons.filled.DarkMode
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Language
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.ListItem
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.VerticalDivider
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.foundation.Image
import com.example.jokesapp.R
import com.example.jokesapp.ui.theme.JokesAppTheme

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProfileScreen(
    outerPadding: PaddingValues = PaddingValues(),
    isDarkTheme: Boolean,
    onDarkThemeChange: (Boolean) -> Unit
) {
    Scaffold(
        topBar = {
            TopAppBar(title = { Text(stringResource(R.string.nav_profile)) })
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(bottom = outerPadding.calculateBottomPadding())
                .verticalScroll(rememberScrollState())
                .padding(dimensionResource(R.dimen.padding_medium)),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(modifier = Modifier.height(dimensionResource(R.dimen.padding_small)))

            Surface(
                shape = CircleShape,
                color = MaterialTheme.colorScheme.primaryContainer,
                modifier = Modifier.size(dimensionResource(R.dimen.avatar_size))
            ) {
                Image(
                    painter = painterResource(id = R.drawable.ic_launcher_foreground),
                    contentDescription = stringResource(R.string.cd_profile_avatar),
                    contentScale = ContentScale.Fit,
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(dimensionResource(R.dimen.padding_small))
                )
            }

            Spacer(modifier = Modifier.height(dimensionResource(R.dimen.padding_medium)))

            Text(stringResource(R.string.profile_username), style = MaterialTheme.typography.headlineSmall)
            Text(
                stringResource(R.string.profile_email),
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            Spacer(modifier = Modifier.height(dimensionResource(R.dimen.padding_large)))

            ElevatedCard(modifier = Modifier.fillMaxWidth()) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = dimensionResource(R.dimen.padding_medium)),
                    horizontalArrangement = Arrangement.SpaceEvenly
                ) {
                    StatItem(value = "24", label = stringResource(R.string.stat_jokes_read))
                    VerticalDivider(modifier = Modifier.height(dimensionResource(R.dimen.avatar_size) / 2))
                    StatItem(value = "8", label = stringResource(R.string.stat_favorites))
                    VerticalDivider(modifier = Modifier.height(dimensionResource(R.dimen.avatar_size) / 2))
                    StatItem(value = "3", label = stringResource(R.string.stat_categories))
                }
            }

            Spacer(modifier = Modifier.height(dimensionResource(R.dimen.padding_large)))

            Text(
                stringResource(R.string.settings_title),
                style = MaterialTheme.typography.titleMedium,
                modifier = Modifier
                    .align(Alignment.Start)
                    .padding(bottom = dimensionResource(R.dimen.padding_small))
            )

            ElevatedCard(modifier = Modifier.fillMaxWidth()) {
                Column {
                    ListItem(
                        headlineContent = { Text(stringResource(R.string.settings_dark_mode)) },
                        leadingContent = {
                            Icon(Icons.Filled.DarkMode, contentDescription = null)
                        },
                        trailingContent = {
                            Switch(
                                checked = isDarkTheme,
                                onCheckedChange = onDarkThemeChange
                            )
                        }
                    )
                    HorizontalDivider()
                    ListItem(
                        headlineContent = { Text(stringResource(R.string.settings_language)) },
                        leadingContent = {
                            Icon(Icons.Filled.Language, contentDescription = null)
                        },
                        trailingContent = {
                            Text(
                                stringResource(R.string.settings_language_value),
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    )
                    HorizontalDivider()
                    ListItem(
                        headlineContent = { Text(stringResource(R.string.settings_about)) },
                        leadingContent = {
                            Icon(Icons.Filled.Info, contentDescription = null)
                        },
                        trailingContent = {
                            Icon(Icons.Filled.ChevronRight, contentDescription = null)
                        }
                    )
                    HorizontalDivider()
                    ListItem(
                        headlineContent = { Text(stringResource(R.string.settings_version)) },
                        leadingContent = {
                            Icon(Icons.Filled.Build, contentDescription = null)
                        },
                        trailingContent = {
                            Text(
                                stringResource(R.string.settings_version_value),
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    )
                }
            }

            Spacer(modifier = Modifier.height(dimensionResource(R.dimen.padding_medium)))
        }
    }
}

@Composable
private fun StatItem(value: String, label: String) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(value, style = MaterialTheme.typography.headlineSmall)
        Text(
            label,
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}

@Preview(showBackground = true, name = "ProfileScreen - Light")
@Composable
private fun ProfileScreenLightPreview() {
    JokesAppTheme(darkTheme = false) {
        ProfileScreen(isDarkTheme = false, onDarkThemeChange = {})
    }
}

@Preview(showBackground = true, name = "ProfileScreen - Dark")
@Composable
private fun ProfileScreenDarkPreview() {
    JokesAppTheme(darkTheme = true) {
        ProfileScreen(isDarkTheme = true, onDarkThemeChange = {})
    }
}
