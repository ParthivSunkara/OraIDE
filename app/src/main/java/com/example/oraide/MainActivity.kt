package com.example.oraide

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.ui.Modifier
import com.example.oraide.data.SafFileRepository
import com.example.oraide.data.SettingsManager
import com.example.oraide.theme.OraIDETheme
import com.example.oraide.ui.MainScreen
import com.example.oraide.ui.MainViewModel
import com.example.oraide.ui.editor.EditorViewModel
import com.example.oraide.ui.explorer.FileExplorerViewModel

class MainActivity : ComponentActivity() {

    private lateinit var settingsManager: SettingsManager
    private lateinit var repository: SafFileRepository
    private lateinit var explorerViewModel: FileExplorerViewModel

    private val openDocumentTreeLauncher = registerForActivityResult(ActivityResultContracts.OpenDocumentTree()) { uri ->
        if (uri != null) {
            contentResolver.takePersistableUriPermission(
                uri,
                Intent.FLAG_GRANT_READ_URI_PERMISSION or Intent.FLAG_GRANT_WRITE_URI_PERMISSION
            )
            settingsManager.addProjectUri(uri.toString())
            repository.setWorkspaceRoot(uri.toString())
            explorerViewModel.loadProject()
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        
        enableEdgeToEdge()

        settingsManager = SettingsManager(this)
        repository = SafFileRepository(this)
        val mainViewModel = MainViewModel()
        explorerViewModel = FileExplorerViewModel(repository)
        val editorViewModel = EditorViewModel(repository, settingsManager)

        val searchViewModel = com.example.oraide.ui.search.SearchViewModel(repository)

        // Initialize from Settings
        val savedUri = settingsManager.activeProjectUri.value
        if (savedUri != null) {
            try {
                repository.setWorkspaceRoot(savedUri)
                explorerViewModel.loadProject()
            } catch (e: Exception) {
                // Ignore if URI is no longer valid
            }
        }

        setContent {
            OraIDETheme(settingsManager = settingsManager) {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    MainScreen(
                        mainViewModel = mainViewModel,
                        explorerViewModel = explorerViewModel,
                        editorViewModel = editorViewModel,
                        searchViewModel = searchViewModel,
                        settingsManager = settingsManager,
                        onOpenProject = { openDocumentTreeLauncher.launch(null) }
                    )
                }
            }
        }
    }
}
