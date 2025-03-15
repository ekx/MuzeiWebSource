package com.google.android.apps.muzei.websource

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.google.android.apps.muzei.websource.ui.theme.MuzeiWebSourceTheme
import kotlinx.coroutines.launch

class WebSourceSettingsActivity : ComponentActivity() {

    @OptIn(ExperimentalMaterial3Api::class)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            val dataStore = WebSourceDataStore(this)
            var showBottomSheet by remember { mutableStateOf(true) } // Initially show the bottom sheet
            val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
            val scope = rememberCoroutineScope()

            LaunchedEffect(Unit) {
                sheetState.show()
            }

            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Color.Transparent)
            ) {
                if (showBottomSheet) {
                    MuzeiWebSourceTheme {
                        ModalBottomSheet(
                            onDismissRequest = {
                                showBottomSheet = false
                                finish()
                            },
                            sheetState = sheetState
                        ) {
                            UrlScreen(
                                dataStore = dataStore,
                                onCloseClicked = {
                                    scope.launch {
                                        sheetState.hide()
                                    }.invokeOnCompletion {
                                        if (!sheetState.isVisible) {
                                            showBottomSheet = false
                                            finish()
                                        }
                                    }
                                }
                            )
                        }
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun UrlScreen(dataStore: WebSourceDataStore, onCloseClicked: () -> Unit) {
    val viewModel: WebSourceSettingsViewModel = viewModel(factory = WebSourceSettingsViewModelFactory(dataStore))
    var urlInput by remember { mutableStateOf("") } // Use remember and mutableStateOf in Composable
    val storedUrl by viewModel.storedUrl.collectAsStateWithLifecycle()

    LaunchedEffect(storedUrl) {
        // When the storedUrl changes, update urlInput
        if (!storedUrl.isNullOrEmpty()){
            urlInput = storedUrl.orEmpty()
        }
    }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        TextField(
            value = urlInput,
            onValueChange = {
                urlInput = it
                viewModel.onUrlChanged(it)
            },
            label = { Text("Enter URL") },
            modifier = Modifier.fillMaxWidth(),
            keyboardOptions = KeyboardOptions(imeAction = ImeAction.Done),
            keyboardActions = KeyboardActions(
                onDone = { viewModel.saveUrl(onCloseClicked) }
            )
        )
        Spacer(modifier = Modifier.height(16.dp))
        Button(onClick = { viewModel.saveUrl(onCloseClicked) }) {
            Text("Save")
        }
    }
}