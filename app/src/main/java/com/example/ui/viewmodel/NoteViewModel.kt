package com.example.ui.viewmodel

import android.content.Context
import android.content.SharedPreferences
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.data.database.NoteDatabase
import com.example.data.model.Note
import com.example.data.repository.NoteRepository
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

class NoteViewModel(private val repository: NoteRepository, context: Context) : ViewModel() {

    private val sharedPrefs: SharedPreferences =
        context.getSharedPreferences("noteva_preferences", Context.MODE_PRIVATE)

    // Flow of active, archive, and favorite notes from Room
    val activeNotes: StateFlow<List<Note>> = repository.activeNotes
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val archivedNotes: StateFlow<List<Note>> = repository.archivedNotes
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val favoriteNotes: StateFlow<List<Note>> = repository.favoriteNotes
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    // UI filters
    private val _searchQuery = MutableStateFlow("")
    val searchQuery = _searchQuery.asStateFlow()

    private val _selectedCategory = MutableStateFlow("All")
    val selectedCategory = _selectedCategory.asStateFlow()

    private val _viewModeGrid = MutableStateFlow(sharedPrefs.getBoolean("view_mode_grid", true))
    val viewModeGrid = _viewModeGrid.asStateFlow()

    private val _darkModeEnabled = MutableStateFlow(sharedPrefs.getBoolean("dark_mode", true)) // Premium dark default
    val darkModeEnabled = _darkModeEnabled.asStateFlow()

    private val _appPasscode = MutableStateFlow(sharedPrefs.getString("app_passcode", ""))
    val appPasscode = _appPasscode.asStateFlow()

    // Loading / Synced States
    private val _syncStatus = MutableStateFlow("Synced") // "Synced", "Syncing...", "Error"
    val syncStatus = _syncStatus.asStateFlow()

    // Categories list
    val categories = listOf("All", "Work", "Personal", "Checklists", "Ideas", "Study")

    // Dynamic filtering Combining Search and Category selected
    val filteredNotes: StateFlow<List<Note>> = combine(
        activeNotes,
        _searchQuery,
        _selectedCategory
    ) { notes, query, category ->
        notes.filter { note ->
            val matchesSearch = note.title.contains(query, ignoreCase = true) ||
                    note.content.contains(query, ignoreCase = true) ||
                    note.tags.contains(query, ignoreCase = true)

            val matchesCategory = if (category == "All") {
                true
            } else if (category == "Checklists") {
                note.checklistJson != null && note.checklistJson.length > 5
            } else {
                note.category.equals(category, ignoreCase = true)
            }

            matchesSearch && matchesCategory
        }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    fun setSearchQuery(query: String) {
        _searchQuery.value = query
    }

    fun setSelectedCategory(category: String) {
        _selectedCategory.value = category
    }

    fun toggleViewMode() {
        val current = _viewModeGrid.value
        _viewModeGrid.value = !current
        sharedPrefs.edit().putBoolean("view_mode_grid", !current).apply()
    }

    fun toggleDarkMode() {
        val current = _darkModeEnabled.value
        _darkModeEnabled.value = !current
        sharedPrefs.edit().putBoolean("dark_mode", !current).apply()
    }

    fun savePasscode(passcode: String) {
        _appPasscode.value = passcode
        sharedPrefs.edit().putString("app_passcode", passcode).apply()
    }

    fun clearPasscode() {
        _appPasscode.value = ""
        sharedPrefs.edit().remove("app_passcode").apply()
    }

    // Trigger sync animation
    fun triggerSync() {
        viewModelScope.launch {
            _syncStatus.value = "Syncing..."
            kotlinx.coroutines.delay(1800)
            _syncStatus.value = "Synced"
            // Set all active and archived notes synced true in Database
            val list = activeNotes.value + archivedNotes.value
            for (note in list) {
                if (!note.isSynced) {
                    repository.updateNote(note.copy(isSynced = true))
                }
            }
        }
    }

    // Crud operations
    fun insertNote(note: Note, onComplete: (Int) -> Unit = {}) {
        viewModelScope.launch {
            _syncStatus.value = "Saving..."
            val id = repository.insertNote(note.copy(isSynced = false))
            _syncStatus.value = "Synced"
            onComplete(id.toInt())
        }
    }

    fun updateNote(note: Note) {
        viewModelScope.launch {
            _syncStatus.value = "Syncing..."
            repository.updateNote(note.copy(isSynced = false, lastModified = System.currentTimeMillis()))
            _syncStatus.value = "Synced"
        }
    }

    fun deleteNote(note: Note) {
        viewModelScope.launch {
            repository.deleteNote(note)
        }
    }

    fun deleteNoteById(id: Int) {
        viewModelScope.launch {
            repository.deleteNoteById(id)
        }
    }

    fun togglePin(note: Note) {
        updateNote(note.copy(isPinned = !note.isPinned))
    }

    fun toggleArchive(note: Note) {
        updateNote(note.copy(isArchived = !note.isArchived))
    }

    fun toggleFavorite(note: Note) {
        updateNote(note.copy(isFavorite = !note.isFavorite))
    }

    fun updateNoteCoordinates(id: Int, fromIndex: Int, toIndex: Int) {
        // Mocking custom order for drag/drop
    }
}

class NoteViewModelFactory(private val repository: NoteRepository, private val context: Context) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(NoteViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return NoteViewModel(repository, context) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
