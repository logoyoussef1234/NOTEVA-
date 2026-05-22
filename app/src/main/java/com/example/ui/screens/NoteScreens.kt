package com.example.ui.screens

import android.widget.Toast
import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.Subject
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import kotlinx.coroutines.flow.map
import com.example.data.model.ChecklistItem
import com.example.data.model.Note
import com.example.ui.components.GlassmorphicCard
import com.example.ui.components.PremiumBackground
import com.example.ui.viewmodel.NoteViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

// -----------------------------------------------------
// 1. HOME SCREEN COMPOSE
// -----------------------------------------------------
@OptIn(androidx.compose.foundation.ExperimentalFoundationApi::class)
@Composable
fun HomeScreen(
    viewModel: NoteViewModel,
    onNavigateToEditor: (Int) -> Unit,
    onNavigateToSettings: () -> Unit,
    onNavigateToProfile: () -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val notes by viewModel.filteredNotes.collectAsState()
    val searchQuery by viewModel.searchQuery.collectAsState()
    val selectedCategory by viewModel.selectedCategory.collectAsState()
    val isGridView by viewModel.viewModeGrid.collectAsState()
    val isDark by viewModel.darkModeEnabled.collectAsState()
    val syncStatus by viewModel.syncStatus.collectAsState()

    var showLockDialogForNote by remember { mutableStateOf<Note?>(null) }
    var enteredPIN by remember { mutableStateOf("") }

    PremiumBackground(isDark = isDark) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .statusBarsPadding()
                .navigationBarsPadding()
        ) {
            // Header Row: App name, Sync status, Avatar
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp, vertical = 12.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .size(36.dp)
                            .clip(RoundedCornerShape(10.dp))
                            .background(Color(0xFFD0BCFF))
                            .clickable { viewModel.triggerSync() },
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.EditNote,
                            contentDescription = null,
                            tint = Color(0xFF381E72),
                            modifier = Modifier.size(24.dp)
                        )
                    }
                    Column {
                        Text(
                            text = "NOTEVA",
                            fontSize = 18.sp,
                            fontWeight = FontWeight.ExtraBold,
                            color = if (isDark) Color.White else Color(0xFF0F121F),
                            letterSpacing = 2.sp
                        )
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(4.dp)
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(6.dp)
                                    .clip(CircleShape)
                                    .background(if (syncStatus == "Synced") Color(0xFF00E676) else Color(0xFFFFB300))
                            )
                            Text(
                                text = syncStatus,
                                fontSize = 10.sp,
                                color = Color.Gray,
                                fontWeight = FontWeight.Medium
                            )
                        }
                    }
                }

                Row(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    IconButton(onClick = { viewModel.toggleViewMode() }) {
                        Icon(
                            imageVector = if (isGridView) Icons.Default.ViewStream else Icons.Default.GridView,
                            contentDescription = "Toggle Grid/List",
                            tint = if (isDark) Color.LightGray else Color.DarkGray
                        )
                    }

                    IconButton(onClick = onNavigateToSettings) {
                        Icon(
                            imageVector = Icons.Default.Settings,
                            contentDescription = "Settings",
                            tint = if (isDark) Color.LightGray else Color.DarkGray
                        )
                    }

                    Box(
                        modifier = Modifier
                            .size(36.dp)
                            .clip(CircleShape)
                            .border(1.dp, Color(0xFFD0BCFF), CircleShape)
                            .clickable { onNavigateToProfile() }
                    ) {
                        Icon(
                            imageVector = Icons.Default.Person,
                            contentDescription = "Profile",
                            tint = Color(0xFFD0BCFF),
                            modifier = Modifier
                                .size(24.dp)
                                .align(Alignment.Center)
                        )
                    }
                }
            }

            // Central search area
            OutlinedTextField(
                value = searchQuery,
                onValueChange = { viewModel.setSearchQuery(it) },
                placeholder = { Text("Search tags, contents, details...", color = Color.Gray) },
                leadingIcon = { Icon(Icons.Default.Search, contentDescription = null, tint = Color.Gray) },
                trailingIcon = {
                    if (searchQuery.isNotEmpty()) {
                        IconButton(onClick = { viewModel.setSearchQuery("") }) {
                            Icon(Icons.Default.Close, contentDescription = "Clear search", tint = Color.Gray)
                        }
                    }
                },
                singleLine = true,
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = Color(0xFFD0BCFF).copy(alpha = 0.8f),
                    unfocusedBorderColor = if (isDark) Color(0xFF3E3C45) else Color.DarkGray.copy(alpha = 0.15f),
                    focusedContainerColor = if (isDark) Color(0xFF1C1B1F).copy(alpha = 0.4f) else Color.White,
                    unfocusedContainerColor = if (isDark) Color(0xFF1C1B1F).copy(alpha = 0.4f) else Color.White
                ),
                shape = RoundedCornerShape(16.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp, vertical = 6.dp)
            )

            // Category scrolling list options
            LazyRow(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                contentPadding = PaddingValues(horizontal = 20.dp, vertical = 12.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                items(viewModel.categories) { category ->
                    val isSelected = selectedCategory == category
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(12.dp))
                            .background(
                                if (isSelected) {
                                    if (isDark) Color(0xFFD0BCFF) else Color(0xFF6750A4)
                                } else {
                                    if (isDark) Color(0xFF1C1B1F) else Color(0xFFF1F5F9)
                                }
                            )
                            .then(
                                if (!isSelected && isDark) {
                                    Modifier.border(1.dp, Color(0xFF3E3C45), RoundedCornerShape(12.dp))
                                } else if (!isSelected) {
                                    Modifier.border(1.dp, Color(0xFFE2E8F0), RoundedCornerShape(12.dp))
                                } else Modifier
                            )
                            .clickable { viewModel.setSelectedCategory(category) }
                            .padding(horizontal = 16.dp, vertical = 10.dp)
                    ) {
                        Text(
                            text = category,
                            fontSize = 12.sp,
                            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                            color = if (isSelected) {
                                if (isDark) Color(0xFF381E72) else Color.White
                            } else {
                                if (isDark) Color(0xFFE6E1E5) else Color(0xFF475569)
                            }
                        )
                    }
                }
            }

            // Quick stats notification banner when notes list is empty
            if (notes.isEmpty()) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f),
                    contentAlignment = Alignment.Center
                ) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.spacedBy(12.dp),
                        modifier = Modifier.padding(32.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Outlined.ContentPasteOff,
                            contentDescription = null,
                            tint = Color.Gray,
                            modifier = Modifier.size(64.dp)
                        )
                        Text(
                            text = "Your digital mind is empty",
                            fontSize = 16.sp,
                            color = if (isDark) Color.White else Color.Black,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = "Tap the glowing expander button below to compose notes, tasks checklists, voice entries and photos.",
                            color = Color.Gray,
                            fontSize = 13.sp,
                            textAlign = TextAlign.Center,
                            lineHeight = 20.sp
                        )
                    }
                }
            } else {
                // List vs Grid viewport display
                if (isGridView) {
                    LazyVerticalGrid(
                        columns = GridCells.Fixed(2),
                        contentPadding = PaddingValues(start = 16.dp, end = 16.dp, bottom = 80.dp),
                        horizontalArrangement = Arrangement.spacedBy(10.dp),
                        verticalArrangement = Arrangement.spacedBy(10.dp),
                        modifier = Modifier.weight(1f)
                    ) {
                        items(notes, key = { it.id }) { note ->
                            NoteCardItem(
                                note = note,
                                isDark = isDark,
                                onClick = {
                                    if (note.isLocked) {
                                        showLockDialogForNote = note
                                        enteredPIN = ""
                                    } else {
                                        onNavigateToEditor(note.id)
                                    }
                                },
                                onLongClick = {
                                    // Custom Drag-and-drop sort indicator / Quick Pin action
                                    viewModel.togglePin(note)
                                    Toast.makeText(context, if (note.isPinned) "Unpinned note" else "Pinned note", Toast.LENGTH_SHORT).show()
                                }
                            )
                        }
                    }
                } else {
                    LazyColumn(
                        contentPadding = PaddingValues(start = 16.dp, end = 16.dp, bottom = 80.dp),
                        verticalArrangement = Arrangement.spacedBy(10.dp),
                        modifier = Modifier.weight(1f)
                    ) {
                        items(notes, key = { it.id }) { note ->
                            NoteCardItem(
                                note = note,
                                isDark = isDark,
                                onClick = {
                                    if (note.isLocked) {
                                        showLockDialogForNote = note
                                        enteredPIN = ""
                                    } else {
                                        onNavigateToEditor(note.id)
                                    }
                                },
                                onLongClick = {
                                    viewModel.togglePin(note)
                                    Toast.makeText(context, if (note.isPinned) "Unpinned" else "Pinned", Toast.LENGTH_SHORT).show()
                                }
                            )
                        }
                    }
                }
            }
        }

        // Animated Glassmorphic Expandable Add Note FAB
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(bottom = 24.dp, end = 24.dp),
            contentAlignment = Alignment.BottomEnd
        ) {
            Box(
                modifier = Modifier
                    .size(56.dp)
                    .clip(RoundedCornerShape(16.dp))
                    .background(Color(0xFFD0BCFF))
                    .clickable { onNavigateToEditor(0) } // id 0 means new note
                    .border(
                        1.dp,
                        Color(0xFF3E3C45).copy(alpha = 0.5f),
                        RoundedCornerShape(16.dp)
                    ),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.Add,
                    contentDescription = "New Note",
                    tint = Color(0xFF381E72),
                    modifier = Modifier.size(32.dp)
                )
            }
        }

        // Swipe passcode lock dial dialog
        if (showLockDialogForNote != null) {
            val noteToUnlock = showLockDialogForNote!!
            AlertDialog(
                onDismissRequest = { showLockDialogForNote = null },
                containerColor = Color(0xFF1C1B1F),
                title = {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        Icon(Icons.Default.Lock, contentDescription = null, tint = Color(0xFFD0BCFF))
                        Text("Note Encrypted", color = Color.White, fontSize = 20.sp, fontWeight = FontWeight.Bold)
                    }
                },
                text = {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text(
                            text = "This specific workspace note requires a custom passcode to verify authorization.",
                            color = Color.Gray,
                            fontSize = 13.sp,
                            textAlign = TextAlign.Center
                        )
                        Spacer(modifier = Modifier.height(16.dp))

                        // Quick 4 dots PIN visualization
                        Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                            (1..4).forEach { idx ->
                                val active = enteredPIN.length >= idx
                                Box(
                                    modifier = Modifier
                                        .size(16.dp)
                                        .clip(CircleShape)
                                        .background(if (active) Color(0xFFD0BCFF) else Color.Gray.copy(alpha = 0.3f))
                                        .border(1.dp, if (active) Color(0xFFD0BCFF) else Color.Transparent, CircleShape)
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(24.dp))

                        // Custom dialpad
                        val numbers = listOf("1", "2", "3", "4", "5", "6", "7", "8", "9", "CLR", "0", "DEL")
                        Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                            for (row in 0..3) {
                                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                                    for (col in 0..2) {
                                        val valStr = numbers[row * 3 + col]
                                        Box(
                                            modifier = Modifier
                                                .size(54.dp)
                                                .clip(CircleShape)
                                                .background(Color(0xFF2B2930))
                                                .clickable {
                                                    if (valStr == "CLR") {
                                                        enteredPIN = ""
                                                    } else if (valStr == "DEL") {
                                                        if (enteredPIN.isNotEmpty()) {
                                                            enteredPIN = enteredPIN.dropLast(1)
                                                        }
                                                    } else {
                                                        if (enteredPIN.length < 4) {
                                                            enteredPIN += valStr
                                                            if (enteredPIN.length == 4) {
                                                                val expected = noteToUnlock.lockPasscode ?: "1111"
                                                                if (enteredPIN == expected) {
                                                                    showLockDialogForNote = null
                                                                    onNavigateToEditor(noteToUnlock.id)
                                                                } else {
                                                                    Toast.makeText(context, "Incorrect PIN", Toast.LENGTH_SHORT).show()
                                                                    enteredPIN = ""
                                                                }
                                                            }
                                                        }
                                                    }
                                                },
                                            contentAlignment = Alignment.Center
                                        ) {
                                            Text(
                                                valStr,
                                                color = if (valStr == "CLR" || valStr == "DEL") Color.Gray else Color.White,
                                                fontSize = 16.sp,
                                                fontWeight = FontWeight.Bold
                                            )
                                        }
                                    }
                                }
                            }
                        }
                    }
                },
                confirmButton = {
                    TextButton(onClick = { showLockDialogForNote = null }) {
                        Text("CANCEL", color = Color.Gray)
                    }
                }
            )
        }
    }
}

// -----------------------------------------------------
// 2. NOTE INDIVIDUAL CARD ITEM
// -----------------------------------------------------
@OptIn(androidx.compose.foundation.ExperimentalFoundationApi::class)
@Composable
fun NoteCardItem(
    note: Note,
    isDark: Boolean,
    onClick: () -> Unit,
    onLongClick: () -> Unit
) {
    val items = ChecklistItem.fromJsonArray(note.checklistJson)
    val checkedCount = items.count { it.isChecked }
    val totalCount = items.size

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .combinedClickable(
                onClick = onClick,
                onLongClick = onLongClick
            ),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = if (isDark) Color(0xFF1C1B1F).copy(alpha = 0.85f) else Color.White,
            contentColor = if (isDark) Color(0xFFE6E1E5) else Color(0xFF1E293B)
        ),
        border = BorderStroke(
            1.dp,
            if (note.isPinned) {
                Color(0xFFD0BCFF).copy(alpha = 0.6f)
            } else {
                if (isDark) Color(0xFF3E3C45) else Color.DarkGray.copy(alpha = 0.08f)
            }
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
    ) {
        Column(
            modifier = Modifier.padding(14.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            // Header: Category, isPinned, isFavorite badges
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(6.dp))
                        .background(
                            if (isDark) Color(0xFFD0BCFF).copy(alpha = 0.12f) else Color(0xFF6750A4).copy(alpha = 0.15f)
                        )
                        .padding(horizontal = 8.dp, vertical = 3.dp)
                ) {
                    Text(
                        text = note.category.uppercase(),
                        fontSize = 9.sp,
                        fontWeight = FontWeight.Bold,
                        color = if (isDark) Color(0xFFD0BCFF) else Color(0xFF6750A4)
                    )
                }

                Row(
                    horizontalArrangement = Arrangement.spacedBy(6.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    if (note.isPinned) {
                        Icon(
                            imageVector = Icons.Default.PushPin,
                            contentDescription = "Pinned Note",
                            tint = Color(0xFFD0BCFF),
                            modifier = Modifier.size(14.dp)
                        )
                    }
                    if (note.isFavorite) {
                        Icon(
                            imageVector = Icons.Default.Favorite,
                            contentDescription = "Favorite Note",
                            tint = Color.Red,
                            modifier = Modifier.size(14.dp)
                        )
                    }
                    if (note.isLocked) {
                        Icon(
                            imageVector = Icons.Default.Lock,
                            contentDescription = "Locked Note",
                            tint = Color.Gray,
                            modifier = Modifier.size(14.dp)
                        )
                    }
                }
            }

            // Title
            Text(
                text = if (note.isLocked) "Encrypted Note" else note.title,
                fontSize = 15.sp,
                fontWeight = FontWeight.ExtraBold,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )

            // Content Preview or image attachment preview
            if (!note.isLocked) {
                if (note.imagePath != null && note.imagePath.isNotBlank()) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(100.dp)
                            .clip(RoundedCornerShape(10.dp))
                            .background(Color.DarkGray.copy(alpha = 0.3f))
                    ) {
                        AsyncImage(
                            model = note.imagePath,
                            contentDescription = "Attachment preview",
                            modifier = Modifier.fillMaxSize(),
                            contentScale = androidx.compose.ui.layout.ContentScale.Crop
                        )
                    }
                }

                if (note.content.isNotBlank()) {
                    Text(
                        text = note.content,
                        fontSize = 12.sp,
                        color = Color.Gray,
                        maxLines = 4,
                        overflow = TextOverflow.Ellipsis,
                        lineHeight = 18.sp
                    )
                }

                // Checklist count progress
                if (totalCount > 0) {
                    Row(
                        modifier = Modifier.fillMaxWidth().padding(top = 4.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.CheckBox,
                            contentDescription = null,
                            tint = Color(0xFFD0BCFF),
                            modifier = Modifier.size(14.dp)
                        )
                        Text(
                            text = "Tasks: $checkedCount/$totalCount",
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.Gray
                        )
                    }
                }

                // Voice support wave preview
                if (note.voicePath != null) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(top = 4.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Mic,
                            contentDescription = null,
                            tint = Color(0xFFD0BCFF),
                            modifier = Modifier.size(14.dp)
                        )
                        // Mock tiny static waveform
                        Row(horizontalArrangement = Arrangement.spacedBy(2.dp)) {
                            (1..6).forEach { size ->
                                Box(
                                    modifier = Modifier
                                        .size(2.dp, (4 + (size % 3) * 3).dp)
                                        .clip(CircleShape)
                                        .background(Color(0xFFD0BCFF))
                                )
                            }
                        }
                    }
                }
            } else {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    modifier = Modifier.padding(vertical = 8.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Fingerprint,
                        contentDescription = null,
                        tint = Color.Gray,
                        modifier = Modifier.size(24.dp)
                    )
                    Text(
                        text = "Unlock with PIN code",
                        fontSize = 11.sp,
                        color = Color.Gray
                    )
                }
            }

            // Footer: timestamp and tags pill
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = android.text.format.DateFormat.format("MMM dd, yyyy", note.lastModified).toString(),
                    fontSize = 9.sp,
                    color = Color.Gray
                )

                if (note.tags.isNotBlank()) {
                    Text(
                        text = "#" + note.tags.split(",").firstOrNull(),
                        fontSize = 9.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = Color.Gray
                    )
                }
            }
        }
    }
}

// -----------------------------------------------------
// 3. NOTE EDITOR SCREEN
// -----------------------------------------------------
@OptIn(ExperimentalLayoutApi::class)
@Composable
fun NoteEditorScreen(
    viewModel: NoteViewModel,
    noteId: Int,
    onNavigateBack: () -> Unit
) {
    val coroutineScope = rememberCoroutineScope()
    val isDark by viewModel.darkModeEnabled.collectAsState()
    val syncStatus by viewModel.syncStatus.collectAsState()

    var noteTitle by remember { mutableStateOf("") }
    var noteContent by remember { mutableStateOf("") }
    var selectedCategory by remember { mutableStateOf("Work") }
    var tagsInput by remember { mutableStateOf("") }

    // Pinning / Archiving states
    var isPinned by remember { mutableStateOf(false) }
    var isArchived by remember { mutableStateOf(false) }
    var isFavorite by remember { mutableStateOf(false) }

    // Media properties
    var imageAttachment by remember { mutableStateOf<String?>(null) }
    var voiceAttachment by remember { mutableStateOf<String?>(null) }
    var checklistItems by remember { mutableStateOf<List<ChecklistItem>>(emptyList()) }

    // Locking
    var isLocked by remember { mutableStateOf(false) }
    var lockPIN by remember { mutableStateOf("") }

    // Voice record states mock
    var isRecording by remember { mutableStateOf(false) }
    var isVoicePlaying by remember { mutableStateOf(false) }

    // Auto save tracker
    var isAutoSaving by remember { mutableStateOf(false) }

    // Load original note data
    LaunchedEffect(noteId) {
        if (noteId > 0) {
            val original = viewModel.activeNotes.value.find { it.id == noteId }
                ?: viewModel.archivedNotes.value.find { it.id == noteId }
            if (original != null) {
                noteTitle = original.title
                noteContent = original.content
                selectedCategory = original.category
                tagsInput = original.tags
                isPinned = original.isPinned
                isArchived = original.isArchived
                isFavorite = original.isFavorite
                imageAttachment = original.imagePath
                voiceAttachment = original.voicePath
                checklistItems = ChecklistItem.fromJsonArray(original.checklistJson)
                isLocked = original.isLocked
                lockPIN = original.lockPasscode ?: ""
            }
        }
    }

    // Auto save system triggered on edits
    LaunchedEffect(noteTitle, noteContent, selectedCategory, tagsInput, isPinned, isArchived, isFavorite, imageAttachment, voiceAttachment, checklistItems, isLocked, lockPIN) {
        if (noteTitle.isNotBlank() || noteContent.isNotBlank() || checklistItems.isNotEmpty()) {
            isAutoSaving = true
            delay(1500) // Debounce autosaver

            val currentNote = Note(
                id = if (noteId > 0) noteId else 0,
                title = noteTitle,
                content = noteContent,
                isPinned = isPinned,
                isArchived = isArchived,
                isFavorite = isFavorite,
                category = selectedCategory,
                tags = tagsInput,
                imagePath = imageAttachment,
                voicePath = voiceAttachment,
                checklistJson = if (checklistItems.isNotEmpty()) ChecklistItem.toJsonArray(checklistItems) else null,
                isLocked = isLocked,
                lockPasscode = if (isLocked) (if (lockPIN.isBlank()) "1111" else lockPIN) else null
            )

            if (noteId > 0) {
                viewModel.updateNote(currentNote)
            } else {
                // To keep user inside edited screen with actual live id
                // It can create initial insert and let succeeding saves trigger updates
            }
            isAutoSaving = false
        }
    }

    // Helper functions for formatting rich text inputs
    fun applyMarkdownFormat(symbol: String) {
        // Simple mock formatting append
        noteContent += " $symbol"
    }

    PremiumBackground(isDark = isDark) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .statusBarsPadding()
                .navigationBarsPadding()
        ) {
            // Editor Options top bar Options Menu
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 14.dp, vertical = 8.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    IconButton(onClick = {
                        // Ensure final block save
                        val currentNote = Note(
                            id = if (noteId > 0) noteId else 0,
                            title = noteTitle.ifBlank { "Untitled Note" },
                            content = noteContent,
                            isPinned = isPinned,
                            isArchived = isArchived,
                            isFavorite = isFavorite,
                            category = selectedCategory,
                            tags = tagsInput,
                            imagePath = imageAttachment,
                            voicePath = voiceAttachment,
                            checklistJson = if (checklistItems.isNotEmpty()) ChecklistItem.toJsonArray(checklistItems) else null,
                            isLocked = isLocked,
                            lockPasscode = if (isLocked) (if (lockPIN.isBlank()) "1111" else lockPIN) else null
                        )
                        viewModel.insertNote(currentNote)
                        onNavigateBack()
                    }) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back", tint = if (isDark) Color.White else Color.Black)
                    }

                    Text(
                        text = if (isAutoSaving) "Saving..." else "Draft Synced",
                        fontSize = 11.sp,
                        color = Color.Gray
                    )
                }

                Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                    // Lock Toggle
                    IconButton(onClick = {
                        isLocked = !isLocked
                        if (isLocked && lockPIN.isBlank()) {
                            lockPIN = "1111" // Default PIN
                        }
                    }) {
                        Icon(
                            imageVector = if (isLocked) Icons.Default.Lock else Icons.Default.LockOpen,
                            contentDescription = "Encrypted Lock",
                            tint = if (isLocked) Color(0xFF00F0FF) else Color.Gray
                        )
                    }

                    // Pin note
                    IconButton(onClick = { isPinned = !isPinned }) {
                        Icon(
                            imageVector = if (isPinned) Icons.Default.PushPin else Icons.Outlined.PushPin,
                            contentDescription = "Pin Note",
                            tint = if (isPinned) Color(0xFF00F0FF) else Color.Gray
                        )
                    }

                    // Archive Note
                    IconButton(onClick = { isArchived = !isArchived }) {
                        Icon(
                            imageVector = if (isArchived) Icons.Default.Archive else Icons.Outlined.Archive,
                            contentDescription = "Archive Note",
                            tint = if (isArchived) Color(0xFFE040FB) else Color.Gray
                        )
                    }

                    // Favorite Note
                    IconButton(onClick = { isFavorite = !isFavorite }) {
                        Icon(
                            imageVector = if (isFavorite) Icons.Default.Favorite else Icons.Outlined.Favorite,
                            contentDescription = "Favorite Note",
                            tint = if (isFavorite) Color.Red else Color.Gray
                        )
                    }

                    // Delete button in edit
                    if (noteId > 0) {
                        IconButton(onClick = {
                            viewModel.deleteNoteById(noteId)
                            onNavigateBack()
                        }) {
                            Icon(Icons.Default.DeleteSweep, contentDescription = "Delete note", tint = Color.Red.copy(alpha = 0.8f))
                        }
                    }
                }
            }

            // Note Composer
            LazyColumn(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                // Title
                item {
                    OutlinedTextField(
                        value = noteTitle,
                        onValueChange = { noteTitle = it },
                        placeholder = { Text("Workspace Title", fontSize = 24.sp, fontWeight = FontWeight.Bold, color = Color.Gray) },
                        textStyle = MaterialTheme.typography.headlineSmall.copy(fontWeight = FontWeight.Bold),
                        singleLine = true,
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = Color.Transparent,
                            unfocusedBorderColor = Color.Transparent
                        ),
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 4.dp)
                    )
                }

                // Category and tags row selection
                item {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        // Category Pills options
                        viewModel.categories.filter { it != "All" && it != "Checklists" }.forEach { cat ->
                            val active = selectedCategory == cat
                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(8.dp))
                                    .background(if (active) Color(0xFF00F0FF).copy(alpha = 0.2f) else Color.Transparent)
                                    .border(1.dp, if (active) Color(0xFF00F0FF) else Color.Gray.copy(alpha = 0.3f), RoundedCornerShape(8.dp))
                                    .clickable { selectedCategory = cat }
                                    .padding(horizontal = 10.dp, vertical = 6.dp)
                            ) {
                                Text(cat, fontSize = 11.sp, color = if (active) Color(0xFF00F0FF) else Color.Gray)
                            }
                        }
                    }
                }

                // Custom TAG Pills input
                item {
                    OutlinedTextField(
                        value = tagsInput,
                        onValueChange = { tagsInput = it },
                        placeholder = { Text("Tags separated by commas (e.g. Work, Ideas)", fontSize = 11.sp, color = Color.Gray) },
                        textStyle = MaterialTheme.typography.bodyMedium.copy(fontSize = 12.sp),
                        singleLine = true,
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = Color(0xFF00F0FF).copy(alpha = 0.3f),
                            unfocusedBorderColor = Color.Transparent
                        ),
                        shape = RoundedCornerShape(10.dp),
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(44.dp)
                    )
                }

                // Image attachment representation
                if (imageAttachment != null) {
                    item {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(180.dp)
                                .clip(RoundedCornerShape(16.dp))
                                .border(1.dp, Color.White.copy(alpha = 0.1f), RoundedCornerShape(16.dp))
                        ) {
                            AsyncImage(
                                model = imageAttachment,
                                contentDescription = "Uploaded workspace graphic",
                                modifier = Modifier.fillMaxSize(),
                                contentScale = androidx.compose.ui.layout.ContentScale.Crop
                            )
                            IconButton(
                                onClick = { imageAttachment = null },
                                modifier = Modifier
                                    .align(Alignment.TopEnd)
                                    .padding(8.dp)
                                    .background(Color.Black.copy(alpha = 0.6f), CircleShape)
                            ) {
                                Icon(Icons.Default.Delete, contentDescription = "Clear attachment", tint = Color.Red)
                            }
                        }
                    }
                }

                // Voice Recording Representation Waveform Player
                if (voiceAttachment != null) {
                    item {
                        Card(
                            colors = CardDefaults.cardColors(containerColor = Color(0xFF1C1B1F)),
                            border = BorderStroke(1.dp, Color(0xFFD0BCFF).copy(alpha = 0.3f)),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Row(
                                modifier = Modifier.padding(12.dp),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                                ) {
                                    IconButton(
                                        onClick = { isVoicePlaying = !isVoicePlaying },
                                        colors = IconButtonDefaults.iconButtonColors(containerColor = Color(0xFFD0BCFF).copy(alpha = 0.2f))
                                    ) {
                                        Icon(
                                            imageVector = if (isVoicePlaying) Icons.Filled.Pause else Icons.Filled.PlayArrow,
                                            contentDescription = null,
                                            tint = Color(0xFFD0BCFF)
                                        )
                                    }

                                    Column {
                                        Text("Voice Entry Memo", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = Color.White)
                                        Text(if (isVoicePlaying) "Playing back voice note..." else "0:12 Audio Note", fontSize = 11.sp, color = Color.Gray)
                                    }
                                }

                                // Interactive Animated playing waveform indicators
                                Row(
                                    horizontalArrangement = Arrangement.spacedBy(2.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    (1..12).forEach { s ->
                                        val heightVal = if (isVoicePlaying) {
                                            rememberInfiniteTransition().animateFloat(
                                                initialValue = 4f,
                                                targetValue = 24f,
                                                animationSpec = infiniteRepeatable(
                                                    animation = tween(300 + s * 50, easing = LinearOutSlowInEasing),
                                                    repeatMode = RepeatMode.Reverse
                                                ),
                                                label = "bar"
                                            ).value
                                        } else {
                                            (4 + (s % 3) * 4).toFloat()
                                        }

                                        Box(
                                            modifier = Modifier
                                                .size(2.dp, heightVal.dp)
                                                .background(Color(0xFFD0BCFF))
                                        )
                                    }
                                }

                                IconButton(onClick = {
                                    voiceAttachment = null
                                    isVoicePlaying = false
                                }) {
                                    Icon(Icons.Default.Delete, contentDescription = "Remove voice path", tint = Color.Gray)
                                }
                            }
                        }
                    }
                }

                // Checklist Module renderer
                if (checklistItems.isNotEmpty()) {
                    item {
                        Text(
                            text = "CHECKLIST / TO-DO TASKS",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.ExtraBold,
                            color = Color(0xFFD0BCFF),
                            letterSpacing = 1.sp,
                            modifier = Modifier.padding(vertical = 4.dp)
                        )
                    }

                    items(checklistItems) { chkItem ->
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 2.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Checkbox(
                                checked = chkItem.isChecked,
                                onCheckedChange = { checkedVal ->
                                    val updated = checklistItems.map {
                                        if (it.id == chkItem.id) it.copy(isChecked = checkedVal) else it
                                    }
                                    checklistItems = updated
                                }
                            )

                            OutlinedTextField(
                                value = chkItem.text,
                                onValueChange = { newVal ->
                                    val updated = checklistItems.map {
                                        if (it.id == chkItem.id) it.copy(text = newVal) else it
                                    }
                                    checklistItems = updated
                                },
                                textStyle = LocalTextStyle.current.copy(
                                    textDecoration = if (chkItem.isChecked) TextDecoration.LineThrough else TextDecoration.None,
                                    color = if (chkItem.isChecked) Color.Gray else (if (isDark) Color.White else Color.Black)
                                ),
                                colors = OutlinedTextFieldDefaults.colors(
                                    focusedBorderColor = Color.Transparent,
                                    unfocusedBorderColor = Color.Transparent
                                ),
                                modifier = Modifier.weight(1f)
                            )

                            IconButton(onClick = {
                                checklistItems = checklistItems.filter { it.id != chkItem.id }
                            }) {
                                Icon(Icons.Default.RemoveCircleOutline, contentDescription = "Delete task", tint = Color.Gray)
                            }
                        }
                    }

                    item {
                        TextButton(
                            onClick = {
                                checklistItems = checklistItems + ChecklistItem(
                                    id = System.currentTimeMillis().toString(),
                                    text = "New task item",
                                    isChecked = false
                                )
                            }
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                                Icon(Icons.Default.AddBox, contentDescription = null, tint = Color(0xFFD0BCFF))
                                Text("ADD TASKS CHECKLIST ITEM", color = Color(0xFFD0BCFF), fontSize = 11.sp, fontWeight = FontWeight.Bold)
                            }
                        }
                    }
                }

                // Body text content input field descriptor
                item {
                    OutlinedTextField(
                        value = noteContent,
                        onValueChange = { noteContent = it },
                        placeholder = { Text("Compile your custom insights here... Support rich layouts", color = Color.Gray) },
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = Color.Transparent,
                            unfocusedBorderColor = Color.Transparent
                        ),
                        modifier = Modifier
                            .fillMaxWidth()
                            .heightIn(min = 200.dp)
                    )
                }
            }

            // Bottom rich markdown support and format action tools
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(Color(0xFF1C1B1F))
                    .border(1.dp, Color.White.copy(alpha = 0.05f))
                    .padding(horizontal = 12.dp, vertical = 8.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth().windowInsetsPadding(WindowInsets.navigationBars),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                        IconButton(onClick = { applyMarkdownFormat("**") }) {
                            Icon(Icons.Default.FormatBold, contentDescription = "Bold", tint = Color.White)
                        }
                        IconButton(onClick = { applyMarkdownFormat("*") }) {
                            Icon(Icons.Default.FormatItalic, contentDescription = "Italic", tint = Color.White)
                        }
                        IconButton(onClick = { applyMarkdownFormat("\n# ") }) {
                            Icon(Icons.Default.Title, contentDescription = "Title text", tint = Color.White)
                        }
                        IconButton(onClick = { applyMarkdownFormat("\n- ") }) {
                            Icon(Icons.Default.FormatListBulleted, contentDescription = "Bullet list", tint = Color.White)
                        }

                        // Checklist toggle button insertion
                        IconButton(onClick = {
                            if (checklistItems.isEmpty()) {
                                checklistItems = listOf(
                                    ChecklistItem(id = "1", text = "Perform client analysis", isChecked = false),
                                    ChecklistItem(id = "2", text = "Sync database notes metadata", isChecked = true)
                                )
                            } else {
                                checklistItems = emptyList() // Clear checklist json mode
                            }
                        }) {
                            Icon(
                                imageVector = Icons.Default.FormatListNumbered,
                                contentDescription = "Checklists trigger",
                                tint = if (checklistItems.isNotEmpty()) Color(0xFFD0BCFF) else Color.White
                            )
                        }
                    }

                    // Multimedia Upload components and microphone support action
                    Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                        // Image mockup uploader trigger
                        IconButton(onClick = {
                            // Inject modern digital workspace graphic automatically
                            imageAttachment = "https://images.unsplash.com/photo-1517842645767-c639042777db?auto=format&fit=crop&q=80&w=400"
                        }) {
                            Icon(
                                imageVector = Icons.Default.Image,
                                contentDescription = "Image attachment mockup upload",
                                tint = if (imageAttachment != null) Color(0xFFD0BCFF) else Color.White
                            )
                        }

                        // Microphone Voice Recording simulation
                        Box {
                            IconButton(onClick = {
                                if (isRecording) {
                                    isRecording = false
                                    // Complete mock recording entry
                                    voiceAttachment = "mock_voice_memo_1.mp3"
                                } else {
                                    isRecording = true
                                }
                            }) {
                                Icon(
                                    imageVector = if (isRecording) Icons.Filled.StopCircle else Icons.Default.Mic,
                                    contentDescription = "Voice recorder Support",
                                    tint = if (isRecording) Color.Red else (if (voiceAttachment != null) Color(0xFFD0BCFF) else Color.White)
                                )
                            }
                        }
                    }
                }
            }
        }

        // Voice recorder active waveform dialog
        if (isRecording) {
            AlertDialog(
                onDismissRequest = { isRecording = false },
                containerColor = Color(0xFF1C1B1F),
                title = {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Icon(Icons.Default.Mic, contentDescription = null, tint = Color.Red)
                        Text("Voice Recording", color = Color.White, fontSize = 20.sp, fontWeight = FontWeight.Bold)
                    }
                },
                text = {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text("NOTEVA listening. Keep speaking to transcribe audio into text context summaries.", color = Color.Gray, fontSize = 13.sp, textAlign = TextAlign.Center)
                        Spacer(modifier = Modifier.height(24.dp))

                        // Live bouncing waveform visual animation circles using infinitely glowing parameters in compose
                        Row(
                            horizontalArrangement = Arrangement.spacedBy(4.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            (1..12).forEach { s ->
                                val heightVal = rememberInfiniteTransition().animateFloat(
                                    initialValue = 8f,
                                    targetValue = 48f,
                                    animationSpec = infiniteRepeatable(
                                        animation = tween(250 + s * 40, easing = LinearOutSlowInEasing),
                                        repeatMode = RepeatMode.Reverse
                                    ),
                                    label = "bouncing_bars"
                                ).value

                                Box(
                                    modifier = Modifier
                                        .size(3.dp, heightVal.dp)
                                        .background(Color.Red.copy(alpha = 0.8f))
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(16.dp))
                        Text("Transcribing Voice input...", fontSize = 11.sp, color = Color.Gray)
                    }
                },
                confirmButton = {
                    Button(
                        colors = ButtonDefaults.buttonColors(containerColor = Color.Red, contentColor = Color.White),
                        onClick = {
                            isRecording = false
                            voiceAttachment = "mock_voice_memo_path"
                            noteContent += "\n[Transcription: Modern digital notebook with premium Android 16 glassmorphic workspace features.]"
                        }
                    ) {
                        Text("COMPLETE RECORDING", fontWeight = FontWeight.Bold, fontSize = 11.sp)
                    }
                }
            )
        }
    }
}

// -----------------------------------------------------
// 4. SETTINGS SCREEN
// -----------------------------------------------------
@Composable
fun SettingsScreen(
    viewModel: NoteViewModel,
    onNavigateBack: () -> Unit,
    modifier: Modifier = Modifier
) {
    val isDark by viewModel.darkModeEnabled.collectAsState()
    val appPasscode by viewModel.appPasscode.collectAsState()
    val syncStatus by viewModel.syncStatus.collectAsState()

    val totalActiveNotes by viewModel.activeNotes.map { it.size }.collectAsState(0)
    val totalArchivedNotes by viewModel.archivedNotes.map { it.size }.collectAsState(0)

    var currentPasscodeInput by remember { mutableStateOf("") }
    val context = LocalContext.current

    PremiumBackground(isDark = isDark) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .statusBarsPadding()
                .navigationBarsPadding()
        ) {
            // Options Top bar
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 14.dp, vertical = 12.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(onClick = onNavigateBack) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                        contentDescription = "Back",
                        tint = if (isDark) Color.White else Color.Black
                    )
                }
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "SETTINGS WORKSPACE",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.ExtraBold,
                    color = if (isDark) Color.White else Color.Black,
                    letterSpacing = 1.sp
                )
            }

            // Outer List Container
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 20.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                // Section 1: Dashboard Analytics summary
                item {
                    Text("WORKSPACE ANALYTICS", fontSize = 11.sp, color = Color(0xFFD0BCFF), fontWeight = FontWeight.ExtraBold, letterSpacing = 2.sp)
                    Spacer(modifier = Modifier.height(8.dp))
                    Card(
                        colors = CardDefaults.cardColors(containerColor = Color(0xFF1C1B1F)),
                        border = BorderStroke(1.dp, Color.White.copy(alpha = 0.05f)),
                        shape = RoundedCornerShape(16.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Text("Total Active Documents", color = Color.Gray, fontSize = 13.sp)
                                Text(totalActiveNotes.toString(), color = Color.White, fontWeight = FontWeight.Bold, fontSize = 14.sp)
                            }
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Text("Archived Items", color = Color.Gray, fontSize = 13.sp)
                                Text(totalArchivedNotes.toString(), color = Color.White, fontWeight = FontWeight.Bold, fontSize = 14.sp)
                            }
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Text("Sync Integrity", color = Color.Gray, fontSize = 13.sp)
                                Text("100% Secure", color = Color(0xFFD0BCFF), fontWeight = FontWeight.Bold, fontSize = 14.sp)
                            }
                        }
                    }
                }

                // Section 2: Visual Aesthetic customization modes
                item {
                    Text("AESTHETIC STYLE CONFIG", fontSize = 11.sp, color = Color(0xFFD0BCFF), fontWeight = FontWeight.ExtraBold, letterSpacing = 2.sp)
                    Spacer(modifier = Modifier.height(8.dp))
                    Card(
                        colors = CardDefaults.cardColors(containerColor = Color(0xFF1C1B1F)),
                        border = BorderStroke(1.dp, Color.White.copy(alpha = 0.05f)),
                        shape = RoundedCornerShape(16.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Column {
                                    Text("Premium Cyber Dark Mode", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 14.sp)
                                    Text("Saves battery with deep cosmic neon highlights", color = Color.Gray, fontSize = 11.sp)
                                }
                                Switch(
                                    checked = isDark,
                                    onCheckedChange = { viewModel.toggleDarkMode() },
                                    colors = SwitchDefaults.colors(checkedThumbColor = Color(0xFFD0BCFF))
                                )
                            }
                        }
                    }
                }

                // Section 3: Note PIN Encryption protection
                item {
                    Text("BIOMETRIC & PIN ENCRYPTION", fontSize = 11.sp, color = Color(0xFFD0BCFF), fontWeight = FontWeight.ExtraBold, letterSpacing = 2.sp)
                    Spacer(modifier = Modifier.height(8.dp))
                    Card(
                        colors = CardDefaults.cardColors(containerColor = Color(0xFF1C1B1F)),
                        border = BorderStroke(1.dp, Color.White.copy(alpha = 0.05f)),
                        shape = RoundedCornerShape(16.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Column {
                                    Text("Workspace Lock Status", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 14.sp)
                                    Text(
                                        text = if (appPasscode.isNullOrBlank()) "Unsecured. Access keys decrypted." else "Highly Secured via custom PIN",
                                        color = if (appPasscode.isNullOrBlank()) Color.Red else Color(0xFFD0BCFF),
                                        fontSize = 11.sp
                                    )
                                }
                                if (!appPasscode.isNullOrBlank()) {
                                    IconButton(onClick = { viewModel.clearPasscode() }) {
                                        Icon(Icons.Default.Delete, contentDescription = "Clear PIN code", tint = Color.Red)
                                    }
                                }
                            }

                            // Dynamic Passcode config Input field PIN setup
                            OutlinedTextField(
                                value = currentPasscodeInput,
                                onValueChange = { inputVal ->
                                    if (inputVal.length <= 4) {
                                        currentPasscodeInput = inputVal
                                    }
                                },
                                placeholder = { Text("Configure new 4-digit numeric PIN", color = Color.Gray) },
                                singleLine = true,
                                keyboardOptions = androidx.compose.foundation.text.KeyboardOptions(
                                    keyboardType = androidx.compose.ui.text.input.KeyboardType.Number
                                ),
                                trailingIcon = {
                                    if (currentPasscodeInput.length == 4) {
                                        IconButton(onClick = {
                                            viewModel.savePasscode(currentPasscodeInput)
                                            Toast.makeText(context, "Encryption Lock updated successfully!", Toast.LENGTH_SHORT).show()
                                            currentPasscodeInput = ""
                                        }) {
                                            Icon(Icons.Default.Check, contentDescription = "Save PIN", tint = Color(0xFFD0BCFF))
                                        }
                                    }
                                },
                                colors = OutlinedTextFieldDefaults.colors(
                                    focusedBorderColor = Color(0xFFD0BCFF),
                                    unfocusedBorderColor = Color.White.copy(alpha = 0.15f)
                                ),
                                shape = RoundedCornerShape(12.dp),
                                modifier = Modifier.fillMaxWidth()
                            )
                        }
                    }
                }

                // Section 4: Sync control manual override actions
                item {
                    Text("SYNC ACTIONS AND INTEGRITY", fontSize = 11.sp, color = Color(0xFFD0BCFF), fontWeight = FontWeight.ExtraBold, letterSpacing = 2.sp)
                    Spacer(modifier = Modifier.height(8.dp))
                    Card(
                        colors = CardDefaults.cardColors(containerColor = Color(0xFF1C1B1F)),
                        border = BorderStroke(1.dp, Color.White.copy(alpha = 0.05f)),
                        shape = RoundedCornerShape(16.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(16.dp)) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Column {
                                    Text("Force Node Database Sync", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 14.sp)
                                    Text("Sync offline state nodes right now with fallback", color = Color.Gray, fontSize = 11.sp)
                                }
                                Button(
                                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFD0BCFF), contentColor = Color(0xFF381E72)),
                                    onClick = {
                                        viewModel.triggerSync()
                                        Toast.makeText(context, "Full offline synchronisation verified successfully!", Toast.LENGTH_SHORT).show()
                                    }
                                ) {
                                    Text("SYNC NOW", fontSize = 10.sp, fontWeight = FontWeight.Bold)
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

// -----------------------------------------------------
// 5. PROFILE SCREEN
// -----------------------------------------------------
@Composable
fun ProfileScreen(
    viewModel: NoteViewModel,
    onNavigateBack: () -> Unit,
    modifier: Modifier = Modifier
) {
    val isDark by viewModel.darkModeEnabled.collectAsState()
    val totalActiveNotes by viewModel.activeNotes.map { it.size }.collectAsState(0)

    PremiumBackground(isDark = isDark) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .statusBarsPadding()
                .navigationBarsPadding(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Options Top row
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 14.dp, vertical = 12.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(onClick = onNavigateBack) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                        contentDescription = "Back",
                        tint = if (isDark) Color.White else Color.Black
                    )
                }
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "PROFILE DETAILS",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.ExtraBold,
                    color = if (isDark) Color.White else Color.Black,
                    letterSpacing = 1.sp
                )
            }

            Spacer(modifier = Modifier.height(32.dp))

            // User Identity circle profile layout
            Box(
                modifier = Modifier
                    .size(120.dp)
                    .clip(CircleShape)
                    .background(Brush.sweepGradient(listOf(Color(0xFFD0BCFF), Color(0xFF381E72), Color(0xFFD0BCFF))))
                    .padding(3.dp)
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .clip(CircleShape)
                        .background(Color(0xFF1C1B1F)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.Person,
                        contentDescription = "Hologram Profile User",
                        tint = Color(0xFFD0BCFF),
                        modifier = Modifier.size(64.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            // User Personal Details Card Information
            Text(
                text = "YOUSSEF LOGO",
                fontSize = 28.sp,
                fontWeight = FontWeight.ExtraBold,
                color = if (isDark) Color.White else Color.Black,
                letterSpacing = 2.sp
            )

            Text(
                text = "logoyoussef173@gmail.com",
                fontSize = 14.sp,
                color = Color.Gray
            )

            Spacer(modifier = Modifier.height(8.dp))

            Box(
                modifier = Modifier
                    .clip(RoundedCornerShape(10.dp))
                    .background(Color(0xFFD0BCFF).copy(alpha = 0.12f))
                    .border(1.dp, Color(0xFFD0BCFF).copy(alpha = 0.4f), RoundedCornerShape(10.dp))
                    .padding(horizontal = 12.dp, vertical = 6.dp)
            ) {
                Text(
                    text = "ENTERPRISE WORKSPACE ARCHITECT",
                    fontSize = 10.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFFD0BCFF)
                )
            }

            Spacer(modifier = Modifier.height(48.dp))

            // Analytics highlight cards
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 40.dp),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(text = totalActiveNotes.toString(), fontSize = 32.sp, fontWeight = FontWeight.Bold, color = Color(0xFFD0BCFF))
                    Text(text = "Active Notes", fontSize = 12.sp, color = Color.Gray)
                }

                Box(
                    modifier = Modifier
                        .width(1.dp)
                        .height(48.dp)
                        .background(Color.Gray.copy(alpha = 0.3f))
                )

                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(text = "100%", fontSize = 32.sp, fontWeight = FontWeight.Bold, color = Color(0xFFD0BCFF))
                    Text(text = "Offline Sync", fontSize = 12.sp, color = Color.Gray)
                }
            }

            Spacer(modifier = Modifier.weight(1f))

            // Noteva Premium footer card tag
            Text(
                text = "NOTEVA PRODUCTIVITY PLATFORM V1.0_BETA",
                fontSize = 9.sp,
                color = Color.Gray.copy(alpha = 0.5f),
                letterSpacing = 1.sp,
                modifier = Modifier.padding(bottom = 24.dp)
            )
        }
    }
}
