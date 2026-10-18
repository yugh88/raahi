package org.raahi.app

import android.Manifest
import android.graphics.Color
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.SystemBarStyle
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.lifecycle.lifecycleScope
import dagger.hilt.android.AndroidEntryPoint
import org.raahi.core.voice.DidiVoice

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    private lateinit var voice: DidiVoice
    private lateinit var voiceInput: VoiceInput
    private val micPermission = registerForActivityResult(ActivityResultContracts.RequestPermission()) { }

    override fun onCreate(savedInstanceState: Bundle?) {
        // Light theme only: dark status/navigation icons on a transparent bar.
        enableEdgeToEdge(
            statusBarStyle = SystemBarStyle.light(Color.TRANSPARENT, Color.TRANSPARENT),
            navigationBarStyle = SystemBarStyle.light(Color.TRANSPARENT, Color.TRANSPARENT),
        )
        super.onCreate(savedInstanceState)
        voice = DidiVoice(this)
        voiceInput = VoiceInput(applicationContext, lifecycleScope, debug = BuildConfig.DEBUG)
        setContent {
            RaahiApp(
                showScreenIndex = BuildConfig.DEBUG,
                voice = voice,
                voiceInput = voiceInput,
                askMicPermission = { micPermission.launch(Manifest.permission.RECORD_AUDIO) },
            )
        }
    }

    override fun onStop() {
        super.onStop()
        voice.stop()
        voiceInput.listener.stop()
    }

    override fun onDestroy() {
        voice.shutdown()
        super.onDestroy()
    }
}
