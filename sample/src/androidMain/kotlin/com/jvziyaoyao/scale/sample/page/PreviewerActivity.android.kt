package com.jvziyaoyao.scale.sample.page

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.BackHandler
import androidx.activity.compose.setContent
import androidx.compose.runtime.Composable
import com.jvziyaoyao.scale.zoomable.previewer.PreviewerState
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch

class PreviewerActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            PreviewerBody()
        }
    }
}

@Composable
actual fun handlePlatformBack(
    scope: CoroutineScope,
    previewerState: PreviewerState
) {
    BackHandler {
        scope.launch {
            previewerState.close()
        }
    }
}