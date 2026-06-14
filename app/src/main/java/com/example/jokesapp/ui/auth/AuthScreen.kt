package com.example.jokesapp.ui.auth

import android.content.Context
import android.content.ContextWrapper
import androidx.biometric.BiometricManager
import androidx.biometric.BiometricManager.Authenticators.BIOMETRIC_STRONG
import androidx.biometric.BiometricManager.Authenticators.BIOMETRIC_WEAK
import androidx.biometric.BiometricManager.Authenticators.DEVICE_CREDENTIAL
import androidx.biometric.BiometricPrompt
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat
import androidx.fragment.app.FragmentActivity
import com.example.jokesapp.R
import com.example.jokesapp.ui.theme.JokesAppTheme

@Composable
fun AuthScreen(onAuthenticated: () -> Unit) {
    val context = LocalContext.current
    val activity = remember(context) { context.findFragmentActivity() }
    val canUseBiometric = remember(context) { canAuthenticate(context) }
    var errorMessage by remember { mutableStateOf<String?>(null) }

    // If device has no biometric/PIN, skip auth automatically
    if (!canUseBiometric) {
        LaunchedEffect(Unit) { onAuthenticated() }
        return
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Text(
            text = stringResource(R.string.auth_title),
            style = MaterialTheme.typography.headlineMedium,
        )
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            text = stringResource(R.string.auth_subtitle),
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
        )

        if (activity != null) {
            Button(
                onClick = {
                    errorMessage = null
                    authenticate(activity, onAuthenticated) { error -> errorMessage = error }
                },
                modifier = Modifier
                    .padding(top = 24.dp)
                    .fillMaxWidth(),
            ) {
                Text(stringResource(R.string.auth_continue))
            }
        }

        errorMessage?.let {
            Text(
                text = it,
                modifier = Modifier.padding(top = 12.dp),
                color = MaterialTheme.colorScheme.error,
            )
        }
    }
}

private fun canAuthenticate(context: Context): Boolean =
    BiometricManager.from(context).canAuthenticate(allowedAuthenticators()) ==
        BiometricManager.BIOMETRIC_SUCCESS

private fun authenticate(
    activity: FragmentActivity,
    onAuthenticated: () -> Unit,
    onError: (String) -> Unit,
) {
    val promptInfo = BiometricPrompt.PromptInfo.Builder()
        .setTitle(activity.getString(R.string.auth_title))
        .setSubtitle(activity.getString(R.string.auth_subtitle))
        .setAllowedAuthenticators(allowedAuthenticators())
        .build()

    BiometricPrompt(
        activity,
        ContextCompat.getMainExecutor(activity),
        object : BiometricPrompt.AuthenticationCallback() {
            override fun onAuthenticationSucceeded(result: BiometricPrompt.AuthenticationResult) {
                onAuthenticated()
            }
            override fun onAuthenticationError(errorCode: Int, errString: CharSequence) {
                onError(errString.toString())
            }
            override fun onAuthenticationFailed() {
                onError(activity.getString(R.string.auth_failed))
            }
        },
    ).authenticate(promptInfo)
}

private fun allowedAuthenticators() = BIOMETRIC_STRONG or BIOMETRIC_WEAK or DEVICE_CREDENTIAL

private tailrec fun Context.findFragmentActivity(): FragmentActivity? = when (this) {
    is FragmentActivity -> this
    is ContextWrapper -> baseContext.findFragmentActivity()
    else -> null
}

@Preview(showBackground = true, name = "Auth Screen")
@Composable
private fun AuthScreenPreview() {
    JokesAppTheme {
        Column(
            modifier = Modifier.fillMaxSize().padding(24.dp),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            Text(
                text = stringResource(R.string.auth_title),
                style = MaterialTheme.typography.headlineMedium,
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = stringResource(R.string.auth_subtitle),
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
            Button(
                onClick = {},
                modifier = Modifier.padding(top = 24.dp).fillMaxWidth(),
            ) {
                Text(stringResource(R.string.auth_continue))
            }
        }
    }
}
