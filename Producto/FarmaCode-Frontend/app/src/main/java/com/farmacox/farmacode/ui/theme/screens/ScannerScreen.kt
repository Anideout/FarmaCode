package com.farmacox.farmacode.ui.theme.screens

import android.Manifest
import android.util.Log
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.camera.core.CameraSelector
import androidx.camera.core.ImageCapture
import androidx.camera.core.ImageCaptureException
import androidx.camera.core.Preview
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.camera.view.PreviewView
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CameraAlt
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.content.ContextCompat
import androidx.lifecycle.viewmodel.compose.viewModel
import com.farmacox.farmacode.FarmaCodeApp
import com.farmacox.farmacode.ui.theme.components.MedicationDetailDialog
import com.farmacox.farmacode.ui.theme.theme.PrimaryGreen
import com.farmacox.farmacode.viewmodel.ScannerViewModel
import com.google.mlkit.vision.common.InputImage
import com.google.mlkit.vision.text.TextRecognition
import com.google.mlkit.vision.text.latin.TextRecognizerOptions

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ScannerScreen(
    fontSize: Float,
    language: String
) {
    val context = LocalContext.current
    val app = context.applicationContext as FarmaCodeApp
    val viewModel: ScannerViewModel = viewModel(
        factory = ScannerViewModel.Factory(app.repository)
    )

    val uiState by viewModel.uiState.collectAsState()
    val isEnglish = language == "English"

    var hasCameraPermission by remember { mutableStateOf(false) }
    var imageCapture by remember { mutableStateOf<ImageCapture?>(null) }

    val recognizer = remember { TextRecognition.getClient(TextRecognizerOptions.DEFAULT_OPTIONS) }

    val permissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission(),
        onResult = { granted -> hasCameraPermission = granted }
    )

    LaunchedEffect(Unit) {
        permissionLauncher.launch(Manifest.permission.CAMERA)
    }

    Box(modifier = Modifier.fillMaxSize()) {

        if (hasCameraPermission) {

            PhotoCameraPreview(
                onImageCaptureReady = { capture -> imageCapture = capture }
            )

            // Encabezado flotante
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .align(Alignment.TopCenter)
                    .statusBarsPadding()
                    .padding(16.dp)
                    .background(
                        brush = Brush.verticalGradient(
                            colors = listOf(PrimaryGreen, PrimaryGreen.copy(alpha = 0.85f))
                        ),
                        shape = RoundedCornerShape(16.dp)
                    )
                    .padding(horizontal = 20.dp, vertical = 14.dp),
                contentAlignment = Alignment.Center
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(
                        text = if (isEnglish) "Identify Medication" else "Identificar Medicamento",
                        fontSize = (fontSize + 3).sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                    Spacer(Modifier.height(4.dp))
                    Text(
                        text = if (isEnglish)
                            "Point at the packaging and press the button"
                        else
                            "Apunta al envase y presiona el botón",
                        fontSize = (fontSize - 1).sp,
                        color = Color.White.copy(alpha = 0.9f),
                        textAlign = TextAlign.Center
                    )
                }
            }

            // Overlay mientras se procesa
            if (uiState.isLoading) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(Color.Black.copy(alpha = 0.55f)),
                    contentAlignment = Alignment.Center
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        CircularProgressIndicator(color = PrimaryGreen, strokeWidth = 4.dp)
                        Spacer(Modifier.height(16.dp))
                        Text(
                            text = if (isEnglish) "Reading text..." else "Leyendo texto...",
                            color = Color.White,
                            fontSize = fontSize.sp,
                            fontWeight = FontWeight.Medium
                        )
                    }
                }
            }

            // Mensaje de error
            if (uiState.errorMessage != null && !uiState.isLoading) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .align(Alignment.BottomCenter)
                        .padding(bottom = 130.dp, start = 24.dp, end = 24.dp)
                        .background(Color.Black.copy(alpha = 0.7f), RoundedCornerShape(12.dp))
                        .padding(12.dp)
                ) {
                    Text(
                        text = uiState.errorMessage!!,
                        color = Color(0xFFFF6B6B),
                        fontSize = fontSize.sp,
                        textAlign = TextAlign.Center,
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            }

            // Botón de captura
            if (!uiState.isLoading) {
                FloatingActionButton(
                    onClick = {
                        val executor = ContextCompat.getMainExecutor(context)
                        imageCapture?.takePicture(
                            executor,
                            object : ImageCapture.OnImageCapturedCallback() {
                                override fun onCaptureSuccess(image: androidx.camera.core.ImageProxy) {
                                    val bitmap = image.toBitmap()
                                    val rotation = image.imageInfo.rotationDegrees
                                    image.close()

                                    val inputImage = InputImage.fromBitmap(bitmap, rotation)
                                    recognizer.process(inputImage)
                                        .addOnSuccessListener { result ->
                                            val texto = result.text.trim()
                                            if (texto.isBlank()) {
                                                viewModel.setError(
                                                    if (isEnglish)
                                                        "No text detected in the image"
                                                    else
                                                        "No se detectó texto en la imagen"
                                                )
                                            } else {
                                                viewModel.buscarPorTextoOcr(texto)
                                            }
                                        }
                                        .addOnFailureListener { e ->
                                            viewModel.setError(
                                                if (isEnglish)
                                                    "Error reading text: ${e.message}"
                                                else
                                                    "Error al leer texto: ${e.message}"
                                            )
                                            Log.e("ScannerScreen", "ML Kit error", e)
                                        }
                                }

                                override fun onError(exception: ImageCaptureException) {
                                    viewModel.setError(
                                        if (isEnglish) "Error capturing photo"
                                        else "Error al capturar la foto"
                                    )
                                }
                            }
                        )
                    },
                    modifier = Modifier
                        .align(Alignment.BottomCenter)
                        .padding(bottom = 40.dp)
                        .size(80.dp),
                    containerColor = PrimaryGreen,
                    shape = CircleShape,
                    elevation = FloatingActionButtonDefaults.elevation(8.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.CameraAlt,
                        contentDescription = if (isEnglish) "Capture" else "Capturar",
                        tint = Color.White,
                        modifier = Modifier.size(36.dp)
                    )
                }
            }

        } else {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(MaterialTheme.colorScheme.background),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = if (isEnglish) "Camera permission required" else "Se requiere permiso de cámara",
                    textAlign = TextAlign.Center,
                    modifier = Modifier.padding(32.dp)
                )
            }
        }

        // Bottom sheet con resultados
        if (uiState.showResult && uiState.foundMedication != null) {
            ModalBottomSheet(
                onDismissRequest = { viewModel.dismissResult() }
            ) {
                MedicationDetailDialog(
                    medication = uiState.foundMedication!!,
                    alternatives = uiState.alternatives,
                    onDismiss = { viewModel.dismissResult() },
                    onAlternativeClick = { },
                    fontSize = fontSize,
                    language = language
                )
            }
        }
    }
}

@Composable
fun PhotoCameraPreview(
    onImageCaptureReady: (ImageCapture) -> Unit
) {
    val context = LocalContext.current
    val lifecycleOwner = LocalLifecycleOwner.current
    val cameraProviderFuture = remember { ProcessCameraProvider.getInstance(context) }
    val imageCaptureUseCase = remember {
        ImageCapture.Builder()
            .setCaptureMode(ImageCapture.CAPTURE_MODE_MINIMIZE_LATENCY)
            .build()
    }

    LaunchedEffect(imageCaptureUseCase) {
        onImageCaptureReady(imageCaptureUseCase)
    }

    AndroidView(
        factory = { ctx ->
            val previewView = PreviewView(ctx)
            cameraProviderFuture.addListener({
                val cameraProvider = cameraProviderFuture.get()
                val preview = Preview.Builder().build().also {
                    it.setSurfaceProvider(previewView.surfaceProvider)
                }
                try {
                    cameraProvider.unbindAll()
                    cameraProvider.bindToLifecycle(
                        lifecycleOwner,
                        CameraSelector.DEFAULT_BACK_CAMERA,
                        preview,
                        imageCaptureUseCase
                    )
                } catch (e: Exception) {
                    Log.e("PhotoCameraPreview", "Binding failed", e)
                }
            }, ContextCompat.getMainExecutor(ctx))
            previewView
        },
        modifier = Modifier.fillMaxSize()
    )
}
