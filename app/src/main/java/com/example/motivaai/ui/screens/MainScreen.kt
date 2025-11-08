package com.example.motivaai.ui.screens

import androidx.compose.material.icons.filled.FileDownload
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.selection.selectable
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.motivaai.data.db.DiaryEntry
import com.example.motivaai.ui.MainViewModel
import com.example.motivaai.ui.theme.MotivAITheme
import kotlinx.coroutines.delay
import androidx.compose.ui.graphics.Color
import java.util.Calendar
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import android.Manifest
import android.content.pm.PackageManager
import android.os.Build
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.ui.platform.LocalContext
import androidx.core.content.ContextCompat
import com.example.motivaai.ui.DebugTestNotificationButton
import com.example.motivaai.utils.PdfHelper
import android.widget.Toast

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainScreen(viewModel: MainViewModel) {

    val userPreferences by viewModel.userPreferences.collectAsState()

    // Coleta a lista de relatos do diário do ViewModel
    val diaryEntries by viewModel.diaryEntries.collectAsState()

    // Estado para controlar o diálogo de recaída
    var showResetDialog by remember { mutableStateOf(false) }

    // Estado para controlar o diálogo de ADICIONAR RELATO
    var showAddEntryDialog by remember { mutableStateOf(false) }

    val context = LocalContext.current

    val exportToPdf = {
        PdfHelper(context).generatePdf(diaryEntries)
    }

    val storagePermissionLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { isGranted: Boolean ->
        if (isGranted) {
            exportToPdf() // Se concedida, exporta
        } else {
            Toast.makeText(context, "Permissão de armazenamento negada. Não é possível exportar.", Toast.LENGTH_LONG).show()
        }
    }

    val requestStoragePermissionOrExport = {
        // A permissão só é necessária para API 28 e abaixo
        if (Build.VERSION.SDK_INT <= Build.VERSION_CODES.P) { // P = API 28
            if (ContextCompat.checkSelfPermission(
                    context,
                    Manifest.permission.WRITE_EXTERNAL_STORAGE
                ) != PackageManager.PERMISSION_GRANTED
            ) {
                storagePermissionLauncher.launch(Manifest.permission.WRITE_EXTERNAL_STORAGE)
            } else {
                exportToPdf()
            }
        } else {
            // API 29+ usa MediaStore, não precisa de permissão runtime
            exportToPdf()
        }
    }

    val permissionLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { isGranted: Boolean ->
        if (isGranted) {
            println("Permissão de notificação CONCEDIDA")
        } else {
            println("Permissão de notificação NEGADA")
            // (no futuro pode tentar mostrar um snackbar/toast aqui informando o usuário)
        }
    }

    LaunchedEffect(key1 = true) {
        // Só pede em Android 13 (API 33) ou superior
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            val hasPermission = ContextCompat.checkSelfPermission(
                context,
                Manifest.permission.POST_NOTIFICATIONS
            ) == PackageManager.PERMISSION_GRANTED

            if (!hasPermission) {
                // Pede a permissão
                permissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
            }
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Minha Jornada") },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primary,
                    titleContentColor = MaterialTheme.colorScheme.onPrimary
                ),
                actions = {
                    IconButton(onClick = {
                        requestStoragePermissionOrExport()
                    }) {
                        Icon(
                            imageVector = Icons.Filled.FileDownload,
                            contentDescription = "Exportar Diário (PDF)",
                            tint = MaterialTheme.colorScheme.onPrimary
                        )
                    }
                }
            )
        },
        // Adiciona o Botão Flutuante (FAB) para RF05
        floatingActionButton = {
            FloatingActionButton(
                onClick = { showAddEntryDialog = true },
                containerColor = MaterialTheme.colorScheme.primary
            ) {
                // Ícone de "Adicionar" (padrão do Material)
                Icon(
                    imageVector = Icons.Filled.Add,
                    contentDescription = "Adicionar Relato Diário",
                    tint = MaterialTheme.colorScheme.onPrimary
                )
            }
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(horizontal = 16.dp), // Apenas padding horizontal
            horizontalAlignment = Alignment.CenterHorizontally
        ) {

            // 1. O Contador (com padding vertical)
            userPreferences?.let { prefs ->
                AbstinenceCounter(
                    startDateInMillis = prefs.abstinenceStartDateInMillis,
                    modifier = Modifier.padding(vertical = 24.dp) // Espaçamento
                )
            }

            // 2. O Botão de Recaída (RF03)
            Button(
                onClick = { showResetDialog = true },
                colors = ButtonDefaults.buttonColors(
                    containerColor = MaterialTheme.colorScheme.error,
                    contentColor = MaterialTheme.colorScheme.onError
                )
            ) {
                Text(text = "Marcar Recaída (Reiniciar Contador)")
            }

            Spacer(modifier = Modifier.height(16.dp)) // Um espaçador
            DebugTestNotificationButton() // Botão de teste para notificação

            Spacer(modifier = Modifier.height(24.dp)) // Espaçador

            // 3. A Lista do Diário (RF05)
            Text(
                text = "Diário de Bordo",
                style = MaterialTheme.typography.titleLarge,
                modifier = Modifier.align(Alignment.Start) // Alinha à esquerda
            )

            // Lista rolável
            LazyColumn(modifier = Modifier.fillMaxSize()) {
                items(diaryEntries) { entry ->
                    DiaryEntryItem(entry = entry) // Renderiza cada item
                }
            }
        }

        // --- Diálogos ---

        // 3. Diálogo de Confirmação de Recaída (RF03)
        if (showResetDialog) {
            AlertDialog(
                onDismissRequest = { showResetDialog = false },
                title = { Text("Reiniciar Jornada?") },
                text = { Text("Isso marcará uma recaída e reiniciará seu contador para 0. Você tem certeza?") },
                confirmButton = {
                    Button(
                        onClick = {
                            val newStartDate = System.currentTimeMillis()
                            viewModel.resetCounter(newStartDate)
                            showResetDialog = false
                        },
                        colors = ButtonDefaults.buttonColors(
                            containerColor = MaterialTheme.colorScheme.error
                        )
                    ) { Text("Sim, reiniciar") }
                },
                dismissButton = {
                    Button(onClick = { showResetDialog = false }) { Text("Cancelar") }
                }
            )
        }

        // 4. Diálogo para Adicionar Relato (RF05)
        if (showAddEntryDialog) {
            AddEntryDialog(
                onDismiss = { showAddEntryDialog = false },
                onSave = { difficulty, notes ->
                    // Cria o objeto de entrada
                    val newEntry = DiaryEntry(
                        dateInMillis = System.currentTimeMillis(),
                        difficulty = difficulty,
                        notes = notes
                    )
                    // Chama o ViewModel para salvar
                    viewModel.addDiaryEntry(newEntry)
                    showAddEntryDialog = false // Fecha o diálogo
                }
            )
        }
    }
}

/**
 * Composable para o Diálogo de Adicionar Relato (RF05)
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddEntryDialog(
    onDismiss: () -> Unit,
    onSave: (difficulty: String, notes: String) -> Unit
) {
    var notes by remember { mutableStateOf("") }
    val difficultyOptions = listOf("Fácil", "Médio", "Difícil")
    var selectedDifficulty by remember { mutableStateOf(difficultyOptions[0]) } // Padrão "Fácil"

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Relato do Dia") },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Text("Como foi o seu dia hoje?")

                // Opções de Dificuldade (Radio Buttons)
                difficultyOptions.forEach { text ->
                    Row(
                        Modifier
                            .fillMaxWidth()
                            .selectable(
                                selected = (text == selectedDifficulty),
                                onClick = { selectedDifficulty = text }
                            )
                            .padding(vertical = 4.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        RadioButton(
                            selected = (text == selectedDifficulty),
                            onClick = null // 'selectable' já cuida do clique
                        )
                        Text(text = text, modifier = Modifier.padding(start = 8.dp))
                    }
                }

                // Campo de Anotações
                OutlinedTextField(
                    value = notes,
                    onValueChange = { notes = it },
                    label = { Text("Anotações (opcional)") },
                    placeholder = { Text("Vitórias, desafios...") },
                    modifier = Modifier.fillMaxWidth()
                )
            }
        },
        confirmButton = {
            Button(
                onClick = { onSave(selectedDifficulty, notes) }
            ) {
                Text("Salvar")
            }
        },
        dismissButton = {
            Button(onClick = onDismiss) { Text("Cancelar") }
        }
    )
}

/**
 * Composable para o item da lista do diário (RF05)
 */
@Composable
fun DiaryEntryItem(entry: DiaryEntry) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            // Formata a data
            val calendar = Calendar.getInstance().apply { timeInMillis = entry.dateInMillis }
            val formattedDate = android.text.format.DateFormat.format("dd/MM/yyyy 'às' HH:mm", calendar)

            Text(
                text = formattedDate.toString(),
                style = MaterialTheme.typography.bodySmall,
                color = Color.Gray
            )
            Text(
                text = "Dificuldade: ${entry.difficulty}",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(top = 4.dp)
            )
            if (entry.notes.isNotBlank()) {
                Text(
                    text = entry.notes,
                    style = MaterialTheme.typography.bodyMedium,
                    modifier = Modifier.padding(top = 8.dp)
                )
            }
        }
    }
}

@Composable
fun AbstinenceCounter(startDateInMillis: Long, modifier: Modifier = Modifier) {
    var timeElapsed by remember { mutableStateOf(System.currentTimeMillis() - startDateInMillis) }

    LaunchedEffect(key1 = startDateInMillis) {
        while (true) {
            timeElapsed = System.currentTimeMillis() - startDateInMillis
            delay(1000L)
        }
    }

    val formattedTime = formatMillisToCounter(timeElapsed)

    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = modifier
    ) {
        Text(
            text = "TEMPO DE ABSTINÊNCIA",
            style = MaterialTheme.typography.titleMedium,
            color = MaterialTheme.colorScheme.onBackground
        )
        Text(
            text = formattedTime,
            fontSize = 40.sp,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.primary,
            modifier = Modifier.padding(16.dp)
        )
    }
}

private fun formatMillisToCounter(millis: Long): String {
    val totalSeconds = millis / 1000
    val days = totalSeconds / (24 * 3600)
    val hours = (totalSeconds % (24 * 3600)) / 3600
    val minutes = (totalSeconds % 3600) / 60
    val seconds = totalSeconds % 60

    return String.format("%02d:%02d:%02d:%02d", days, hours, minutes, seconds)
}

@Preview(showBackground = true)
@Composable
fun MainScreenPreview() {
    MotivAITheme {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            AbstinenceCounter(startDateInMillis = System.currentTimeMillis() - 100000000L)
        }
    }
}