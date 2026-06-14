package com.example.jokesapp.auth

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import com.example.jokesapp.R
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.auth.auth
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

class AuthViewModel(application: Application) : AndroidViewModel(application) {

    private val auth = Firebase.auth

    val googleSignInClient: GoogleSignInClient = GoogleSignIn.getClient(
        application,
        GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(application.getString(R.string.default_web_client_id))
            .requestEmail()
            .build()
    )

    private val _currentUser = MutableStateFlow<FirebaseUser?>(auth.currentUser)
    val currentUser: StateFlow<FirebaseUser?> = _currentUser.asStateFlow()

    fun firebaseAuthWithGoogle(idToken: String) {
        val credential = GoogleAuthProvider.getCredential(idToken, null)
        auth.signInWithCredential(credential)
            .addOnSuccessListener { result -> _currentUser.value = result.user }
    }

    fun signOut() {
        auth.signOut()
        googleSignInClient.signOut()
        _currentUser.value = null
    }
}
