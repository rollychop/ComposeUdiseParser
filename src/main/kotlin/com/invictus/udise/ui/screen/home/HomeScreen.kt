package com.invictus.udise.ui.screen.home

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Html
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.unit.dp
import com.invictus.udise.LocalWindow
import com.invictus.udise.data.UDISEHelper
import com.invictus.udise.model.SchoolDetailModel
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.launch
import java.awt.datatransfer.DataFlavor
import java.awt.dnd.DnDConstants
import java.awt.dnd.DropTarget
import java.awt.dnd.DropTargetDropEvent
import java.io.*

@OptIn(ExperimentalMaterialApi::class)
@Composable
fun HomeScreen() {

    val name = remember { mutableStateOf(TextFieldValue("")) }
    val files = remember { mutableStateOf(listOf<File>()) }
    val error = remember { mutableStateOf("") }
    val parseError = remember { mutableStateOf("") }
    val parseMessage = remember { mutableStateOf("") }
    val scope = rememberCoroutineScope()
    val progress = remember { mutableFloatStateOf(0f) }
    val a = remember { mutableStateOf("") }
    val job = remember { mutableStateOf<Job?>(null) }
    val gbt = remember { mutableStateOf("Generate CSV") }

    val window = LocalWindow.current

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Udise Parser") },
            )
        },
        bottomBar = {
            if (files.value.isNotEmpty()) {
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    verticalArrangement = Arrangement.Center,
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    AnimatedVisibility(visible = parseError.value.isNotEmpty()) {
                        Text(
                            text = parseError.value,
                            modifier = Modifier.padding(16.dp),
                            color = MaterialTheme.colors.error,
                            fontWeight = FontWeight.Bold
                        )
                    }
                    AnimatedVisibility(visible = parseMessage.value.isNotEmpty()) {
                        Text(
                            text = parseMessage.value,
                            modifier = Modifier.padding(16.dp),
                            color = MaterialTheme.colors.primary,
                            fontWeight = FontWeight.Bold
                        )
                    }
                    Row(
                        horizontalArrangement = Arrangement.Center,
                        verticalAlignment = Alignment.CenterVertically,
                    ) {
                        if (progress.value > 0) {
                            Text(a.value)
                            LinearProgressIndicator(progress = progress.value)
                        } else if (progress.value == -1f) {
                            Text(a.value)
                            LinearProgressIndicator()
                        }
                    }
                    Row {
                        if (progress.value == 0f) {
                            Button(
                                onClick = {
                                    val inDir = File(name.value.text)
                                    val htmlFilesDir = File(inDir, "/cached").apply { mkdirs() }
                                    val outPutFile = File(inDir, "output.csv")

                                    progress.value = -1f
                                    parseError.value = ""
                                    parseMessage.value = ""
                                    job.value = scope.launch(Dispatchers.IO) {
                                        try {
                                            val total = files.value.size
                                            if (files.value.isEmpty()) {
                                                progress.value = 0f
                                                return@launch
                                            }

                                            files.value.mapIndexed { index, file ->
                                                a.value = "${index + 1}/${total}"
                                                UDISEHelper.downloadFiles(
                                                    srcHtmlFile = file,
                                                    destinationDir = htmlFilesDir.absolutePath
                                                ).flowOn(Dispatchers.IO).collect { value ->
                                                    progress.value = value
                                                }
                                            }

                                            val rows = (htmlFilesDir.listFiles()?.toList() ?: emptyList())
                                                .mapNotNull { file ->
                                                    runCatching {
                                                        UDISEHelper.parse(file).toCsvRow()
                                                    }.getOrNull()
                                                }

                                            BufferedWriter(FileWriter(outPutFile))
                                                .use { writer ->
                                                    writer.append(SchoolDetailModel.csvHeader())
                                                    writer.appendLine()
                                                    rows.forEachIndexed { index, row ->
                                                        writer.append(row)
                                                        if (index != files.value.lastIndex) {
                                                            writer.appendLine()
                                                        }
                                                    }
                                                }

                                            parseMessage.value = "File generated ${outPutFile.absolutePath}"
                                        } catch (ex: Exception) {
                                            if (ex is CancellationException) {
                                                throw ex
                                            }
                                            parseError.value = ex.message ?: "Failed to parse files"
                                        }
                                        progress.value = 0f
                                    }
                                }
                            ) {
                                Text(gbt.value)
                            }
                        } else {
                            Button(onClick = {
                                job.value?.cancel()
                                job.value = null
                                progress.value = 0f
                            }) {
                                Text("Cancel")
                            }
                        }
                    }
                }
            }
        }
    ) { innerPAdding ->
        Column(
            modifier = Modifier
                .padding(innerPAdding)
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 16.dp)
                .fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Row(
                modifier = Modifier.padding(top = 16.dp).height(IntrinsicSize.Max),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp),
            ) {
                TextField(
                    value = name.value,
                    onValueChange = {
                        name.value = it
                    },
                    label = { Text("UDISE Html Folder path") },
                    modifier = Modifier.weight(1f),
                )

                Button(
                    modifier = Modifier.fillMaxHeight(),
                    onClick = {
                        resolvePath(error, files, name)
                    }
                ) {
                    Text(text = "Resolve")
                }

            }
            AnimatedVisibility(error.value.isNotBlank()) {
                Text(
                    text = error.value,
                    modifier = Modifier.padding(16.dp),
                    color = MaterialTheme.colors.error,
                    fontWeight = FontWeight.Bold
                )
            }
            files.value.forEach {
                ListItem(
                    text = {
                        Text(text = it.name)
                    },
                    modifier = Modifier
                        .clip(MaterialTheme.shapes.medium)
                        .background(MaterialTheme.colors.primary.copy(alpha = 0.1f)),
                    icon = {
                        Icon(
                            imageVector = Icons.Default.Html,
                            contentDescription = "html"
                        )
                    }
                )
            }

        }

        val target = remember {
            object : DropTarget() {
                @Synchronized
                override fun drop(evt: DropTargetDropEvent) {
                    try {
                        evt.acceptDrop(DnDConstants.ACTION_REFERENCE)
                        val droppedFiles = evt
                            .transferable.getTransferData(
                                DataFlavor.javaFileListFlavor
                            ) as List<*>
                        droppedFiles.first()?.let {
                            name.value = TextFieldValue((it as File).absolutePath)
                            resolvePath(error, files, name)
                        }
                    } catch (ex: Exception) {
                        ex.printStackTrace()
                    }
                }
            }
        }
        window.contentPane.dropTarget = target
    }
}

private fun resolvePath(
    error: MutableState<String>,
    files: MutableState<List<File>>,
    name: MutableState<TextFieldValue>
) {
    error.value = ""
    files.value = emptyList()
    try {
        val file = File(name.value.text)
        if (!file.isDirectory) {
            throw IllegalArgumentException("Path must be a directory")
        }

        if (file.exists() && file.isDirectory) {
            files.value =
                file.listFiles(FileFilter { it.name.endsWith(".html") })
                    ?.toList()?.takeIf { it.isNotEmpty() }
                    ?: throw FileNotFoundException("No html file found in this folder")
        } else {
            throw FileNotFoundException()
        }
    } catch (ex: Exception) {
        error.value = ex.message ?: "Folder not found"
    }
}























