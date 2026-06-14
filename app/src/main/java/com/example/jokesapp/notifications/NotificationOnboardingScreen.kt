package com.example.jokesapp.notifications

import android.Manifest
import android.os.Build
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.jokesapp.R
import com.example.jokesapp.ui.theme.JokesAppTheme

@Composable
fun NotificationOnboardingScreen(onFinished: () -> Unit) {
    val permissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission(),
    ) {
        onFinished()
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Text(
            text = stringResource(R.string.notification_onboarding_title),
            style = MaterialTheme.typography.headlineMedium,
        )
        Text(
            text = stringResource(R.string.notification_onboarding_description),
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier.padding(top = 8.dp),
        )

        Button(
            onClick = {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                    permissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
                } else {
                    onFinished()
                }
            },
            modifier = Modifier
                .padding(top = 24.dp)
                .fillMaxWidth(),
        ) {
            Text(stringResource(R.string.notification_onboarding_allow))
        }

        OutlinedButton(
            onClick = onFinished,
            modifier = Modifier
                .padding(top = 12.dp)
                .fillMaxWidth(),
        ) {
            Text(stringResource(R.string.notification_onboarding_not_now))
        }
    }
}

@Preview(showBackground = true, name = "Notification Onboarding")
@Composable
private fun NotificationOnboardingPreview() {
    JokesAppTheme {
        NotificationOnboardingScreen(onFinished = {})
    }
}
