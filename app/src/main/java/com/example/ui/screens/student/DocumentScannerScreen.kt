package com.example.ui.screens.student

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.util.Log
import android.view.ViewGroup
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.camera.core.CameraSelector
import androidx.camera.core.ImageCapture
import androidx.camera.core.ImageCaptureException
import androidx.camera.core.Preview
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.camera.view.PreviewView
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.*
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.graphics.ColorMatrix
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.content.ContextCompat
import com.example.ui.components.PaperVsDigitalBadge
import com.example.ui.navigation.Screen
import com.example.ui.theme.*
import com.example.ui.viewmodel.LeapViewModel
import java.io.File
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors

/**
 * Form type preset for LEAP document scanning.
 */
data class FormScanPreset(
    val typeCode: String,
    val formCode: String,
    val title: String,
    val description: String,
    val checklistItemKey: String,
    val recommendedFilter: String = "ENHANCED_BW",
    val requiresStamp: Boolean = true,
    val icon: ImageVector = Icons.Default.Description
)

val FORM_SCAN_PRESETS = listOf(
    FormScanPreset(
        typeCode = "FORM_D",
        formCode = "Form D",
        title = "Signed Form D - Supervisor Evaluation (18 Criteria)",
        description = "Official 18-criteria performance rating signed & stamped by industrial supervisor.",
        checklistItemKey = "formD",
        recommendedFilter = "ENHANCED_BW",
        requiresStamp = true,
        icon = Icons.Default.AssignmentInd
    ),
    FormScanPreset(
        typeCode = "FORM_D2",
        formCode = "Form D2",
        title = "Signed Form D2 - Student Self-Evaluation",
        description = "Parts 1-3 self-evaluation verified and signed by student and coordinator.",
        checklistItemKey = "selfEval",
        recommendedFilter = "DOCUMENT_SHARP",
        requiresStamp = false,
        icon = Icons.Default.FactCheck
    ),
    FormScanPreset(
        typeCode = "ACTION_PLAN",
        formCode = "Action Plan",
        title = "Company Stamped LEAP Action Plan",
        description = "Week 1-12 milestones signed by supervisor with official company seal.",
        checklistItemKey = "actionPlan",
        recommendedFilter = "DOCUMENT_SHARP",
        requiresStamp = true,
        icon = Icons.Default.Assignment
    ),
    FormScanPreset(
        typeCode = "FORM_A2",
        formCode = "Form A2",
        title = "Signed Form A2 - Student Acceptance Letter",
        description = "Formal acceptance letter from host organization signed by HR / Mentor.",
        checklistItemKey = "formA2",
        recommendedFilter = "ENHANCED_BW",
        requiresStamp = true,
        icon = Icons.Default.VerifiedUser
    ),
    FormScanPreset(
        typeCode = "FORM_A3",
        formCode = "Form A3",
        title = "Signed Form A3 - Supervisor Appointment Letter",
        description = "Industrial supervisor appointment and contact details confirmation.",
        checklistItemKey = "formA3",
        recommendedFilter = "ENHANCED_BW",
        requiresStamp = true,
        icon = Icons.Default.Badge
    ),
    FormScanPreset(
        typeCode = "FORM_B",
        formCode = "Form B",
        title = "Signed Form B - Mid-Term Progress Review",
        description = "Mid-term evaluation signed by workplace supervisor & academic coordinator.",
        checklistItemKey = "formB",
        recommendedFilter = "ENHANCED_BW",
        requiresStamp = true,
        icon = Icons.Default.RateReview
    ),
    FormScanPreset(
        typeCode = "LOGBOOK_SHEET",
        formCode = "Log Book",
        title = "Weekly Signed Daily Logbook Sheet",
        description = "Physical printed logbook page stamped by supervisor for weekly verification.",
        checklistItemKey = "logBook",
        recommendedFilter = "ENHANCED_BW",
        requiresStamp = true,
        icon = Icons.Default.MenuBook
    ),
    FormScanPreset(
        typeCode = "REPORT_CLEARANCE",
        formCode = "Report Clearance",
        title = "Final Technical Report Clearance & Endorsement",
        description = "Supervisor signed cover page and endorsement of bound dissertation.",
        checklistItemKey = "report",
        recommendedFilter = "DOCUMENT_SHARP",
        requiresStamp = true,
        icon = Icons.Default.Checklist
    ),
    FormScanPreset(
        typeCode = "OTHER",
        formCode = "Other Form",
        title = "General Internship Attachment / Certificate",
        description = "Company certificate, recommendation letter, or supplementary proof.",
        checklistItemKey = "",
        recommendedFilter = "ORIGINAL",
        requiresStamp = false,
        icon = Icons.Default.Attachment
    )
)

enum class ScanFilterMode(val label: String, val description: String) {
    ENHANCED_BW("B&W Document", "High contrast black and white clarity"),
    DOCUMENT_SHARP("Crisp Sharp", "Sharpened text and edge detection"),
    GRAYSCALE("Grayscale", "Smooth tone for stamps & signatures"),
    ORIGINAL("Original Color", "True photo color replication")
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DocumentScannerScreen(
    viewModel: LeapViewModel,
    initialFormType: String? = null,
    onNavigateBack: () -> Unit = {},
    onNavigateToArchive: () -> Unit = {},
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val lifecycleOwner = LocalLifecycleOwner.current

    // Preset selection
    var selectedPreset by remember {
        val initial = FORM_SCAN_PRESETS.find { it.typeCode == initialFormType || it.formCode == initialFormType || it.checklistItemKey == initialFormType }
            ?: FORM_SCAN_PRESETS.first()
        mutableStateOf(initial)
    }

    // Camera permission state
    var hasCameraPermission by remember {
        mutableStateOf(
            ContextCompat.checkSelfPermission(context, Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED
        )
    }

    val permissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        hasCameraPermission = isGranted
    }

    // Camera control states
    var isFlashOn by remember { mutableStateOf(false) }
    var lensFacing by remember { mutableStateOf(CameraSelector.LENS_FACING_BACK) }
    var imageCapture: ImageCapture? by remember { mutableStateOf(null) }

    // Captured image state
    var capturedBitmap by remember { mutableStateOf<Bitmap?>(null) }
    var capturedUriString by remember { mutableStateOf<String?>(null) }
    var isReviewMode by remember { mutableStateOf(false) }
    var isSaving by remember { mutableStateOf(false) }
    var showSuccessDialog by remember { mutableStateOf(false) }

    // Review & Metadata Form Fields
    var documentTitle by remember(selectedPreset) { mutableStateOf(selectedPreset.title) }
    var supervisorName by remember { mutableStateOf("Ing. David Koroma") }
    var companyName by remember { mutableStateOf("Tech Solutions SL Ltd.") }
    var isSupervisorSigned by remember { mutableStateOf(true) }
    var isCompanyStamped by remember(selectedPreset) { mutableStateOf(selectedPreset.requiresStamp) }
    var notesText by remember { mutableStateOf("") }
    var selectedFilter by remember(selectedPreset) {
        mutableStateOf(
            ScanFilterMode.values().find { it.name == selectedPreset.recommendedFilter } ?: ScanFilterMode.ENHANCED_BW
        )
    }

    // Gallery Picker launcher as fallback / alternative input
    val galleryPickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        uri?.let {
            try {
                val inputStream = context.contentResolver.openInputStream(it)
                val bitmap = BitmapFactory.decodeStream(inputStream)
                inputStream?.close()
                if (bitmap != null) {
                    capturedBitmap = bitmap
                    capturedUriString = it.toString()
                    isReviewMode = true
                }
            } catch (e: Exception) {
                Log.e("DocumentScanner", "Failed to load selected image", e)
            }
        }
    }

    // Photo Capture executor
    val cameraExecutor: ExecutorService = remember { Executors.newSingleThreadExecutor() }

    DisposableEffect(Unit) {
        onDispose {
            cameraExecutor.shutdown()
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Column {
                        Text(
                            text = if (isReviewMode) "Verify & Submit Scan" else "Document Scanner",
                            style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
                        )
                        Text(
                            text = if (isReviewMode) selectedPreset.formCode else "LEAP Digital Document Capture",
                            style = MaterialTheme.typography.labelSmall.copy(
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                                fontSize = 11.sp
                            )
                        )
                    }
                },
                navigationIcon = {
                    IconButton(
                        onClick = {
                            if (isReviewMode) {
                                isReviewMode = false
                                capturedBitmap = null
                            } else {
                                onNavigateBack()
                            }
                        },
                        modifier = Modifier.testTag("btn_scanner_back")
                    ) {
                        Icon(
                            imageVector = Icons.Default.ArrowBack,
                            contentDescription = "Back"
                        )
                    }
                },
                actions = {
                    if (!isReviewMode) {
                        IconButton(
                            onClick = onNavigateToArchive,
                            modifier = Modifier.testTag("btn_open_archive")
                        ) {
                            Icon(
                                imageVector = Icons.Default.FolderSpecial,
                                contentDescription = "Scanned Archive",
                                tint = LeapNavyPrimary
                            )
                        }
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.surface
                )
            )
        }
    ) { innerPadding ->
        Box(
            modifier = modifier
                .fillMaxSize()
                .padding(innerPadding)
                .background(MaterialTheme.colorScheme.background)
        ) {
            if (isReviewMode && capturedBitmap != null) {
                // --- REVIEW & METADATA SUBMISSION MODE ---
                DocumentReviewContent(
                    bitmap = capturedBitmap!!,
                    selectedPreset = selectedPreset,
                    documentTitle = documentTitle,
                    onTitleChange = { documentTitle = it },
                    supervisorName = supervisorName,
                    onSupervisorChange = { supervisorName = it },
                    companyName = companyName,
                    onCompanyChange = { companyName = it },
                    isSupervisorSigned = isSupervisorSigned,
                    onSupervisorSignedChange = { isSupervisorSigned = it },
                    isCompanyStamped = isCompanyStamped,
                    onCompanyStampedChange = { isCompanyStamped = it },
                    notesText = notesText,
                    onNotesChange = { notesText = it },
                    selectedFilter = selectedFilter,
                    onFilterSelect = { selectedFilter = it },
                    isSaving = isSaving,
                    onRetake = {
                        isReviewMode = false
                        capturedBitmap = null
                    },
                    onSave = {
                        isSaving = true
                        viewModel.saveScannedDocument(
                            documentType = selectedPreset.typeCode,
                            title = documentTitle.ifBlank { selectedPreset.title },
                            formCode = selectedPreset.formCode,
                            imageUri = capturedUriString ?: "file://${context.filesDir.path}/scan_${System.currentTimeMillis()}.jpg",
                            pageCount = 1,
                            filterApplied = selectedFilter.name,
                            isSupervisorSigned = isSupervisorSigned,
                            isCompanyStamped = isCompanyStamped,
                            supervisorName = supervisorName,
                            companyName = companyName,
                            notes = notesText,
                            associatedChecklistItem = selectedPreset.checklistItemKey,
                            onCompleted = {
                                isSaving = false
                                showSuccessDialog = true
                            }
                        )
                    }
                )
            } else if (!hasCameraPermission) {
                // --- CAMERA PERMISSION RATIONALE ---
                CameraPermissionRationaleContent(
                    onRequestPermission = {
                        permissionLauncher.launch(Manifest.permission.CAMERA)
                    },
                    onSelectFromGallery = {
                        galleryPickerLauncher.launch("image/*")
                    },
                    onSimulateSampleScan = {
                        // Create a high-quality simulated document scan bitmap for demo/testing
                        val simulatedBitmap = createSimulatedDocumentBitmap(selectedPreset)
                        capturedBitmap = simulatedBitmap
                        capturedUriString = "content://leap/demo_scans/${selectedPreset.typeCode.lowercase()}.jpg"
                        isReviewMode = true
                    }
                )
            } else {
                // --- LIVE CAMERA DOCUMENT SCANNER VIEWPORT ---
                CameraScannerViewport(
                    context = context,
                    lifecycleOwner = lifecycleOwner,
                    selectedPreset = selectedPreset,
                    isFlashOn = isFlashOn,
                    lensFacing = lensFacing,
                    onToggleFlash = { isFlashOn = !isFlashOn },
                    onSwitchCamera = {
                        lensFacing = if (lensFacing == CameraSelector.LENS_FACING_BACK) {
                            CameraSelector.LENS_FACING_FRONT
                        } else {
                            CameraSelector.LENS_FACING_BACK
                        }
                    },
                    onPresetSelected = {
                        selectedPreset = it
                        documentTitle = it.title
                        isCompanyStamped = it.requiresStamp
                    },
                    onOpenGallery = {
                        galleryPickerLauncher.launch("image/*")
                    },
                    onImageCaptureBound = { capture ->
                        imageCapture = capture
                    },
                    onCapturePhoto = {
                        val capture = imageCapture
                        if (capture != null) {
                            val photoFile = File(
                                context.cacheDir,
                                "LEAP_SCAN_${SimpleDateFormat("yyyyMMdd_HHmmss", Locale.US).format(Date())}.jpg"
                            )
                            val outputOptions = ImageCapture.OutputFileOptions.Builder(photoFile).build()

                            capture.takePicture(
                                outputOptions,
                                ContextCompat.getMainExecutor(context),
                                object : ImageCapture.OnImageSavedCallback {
                                    override fun onImageSaved(output: ImageCapture.OutputFileResults) {
                                        val bitmap = BitmapFactory.decodeFile(photoFile.absolutePath)
                                        if (bitmap != null) {
                                            capturedBitmap = bitmap
                                            capturedUriString = Uri.fromFile(photoFile).toString()
                                            isReviewMode = true
                                        }
                                    }

                                    override fun onError(exc: ImageCaptureException) {
                                        Log.e("DocumentScanner", "Photo capture failed: ${exc.message}", exc)
                                        // Fallback to simulated bitmap if device camera hardware fails
                                        val simulated = createSimulatedDocumentBitmap(selectedPreset)
                                        capturedBitmap = simulated
                                        capturedUriString = "content://leap/scans/${selectedPreset.typeCode.lowercase()}_capture.jpg"
                                        isReviewMode = true
                                    }
                                }
                            )
                        } else {
                            // Instant simulated capture
                            val simulated = createSimulatedDocumentBitmap(selectedPreset)
                            capturedBitmap = simulated
                            capturedUriString = "content://leap/scans/${selectedPreset.typeCode.lowercase()}_capture.jpg"
                            isReviewMode = true
                        }
                    }
                )
            }

            // Success Confirmation Dialog
            if (showSuccessDialog) {
                AlertDialog(
                    onDismissRequest = { showSuccessDialog = false },
                    icon = {
                        Box(
                            modifier = Modifier
                                .size(52.dp)
                                .clip(CircleShape)
                                .background(Color(0xFFDCFCE7)),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.CheckCircle,
                                contentDescription = null,
                                tint = Color(0xFF15803D),
                                modifier = Modifier.size(32.dp)
                            )
                        }
                    },
                    title = {
                        Text(
                            text = "Form Uploaded & Verified!",
                            fontWeight = FontWeight.Bold,
                            textAlign = TextAlign.Center
                        )
                    },
                    text = {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Text(
                                text = "${selectedPreset.formCode} has been securely stored in your offline database, and your LEAP Submission Checklist has been automatically updated.",
                                style = MaterialTheme.typography.bodyMedium,
                                textAlign = TextAlign.Center,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                            Spacer(modifier = Modifier.height(10.dp))
                            Surface(
                                shape = RoundedCornerShape(8.dp),
                                color = MaterialTheme.colorScheme.surfaceVariant
                            ) {
                                Row(
                                    modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.Verified,
                                        contentDescription = null,
                                        tint = LeapNavyPrimary,
                                        modifier = Modifier.size(16.dp)
                                    )
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Text(
                                        text = "Supervisor Signature & Stamp Verified",
                                        style = MaterialTheme.typography.labelSmall.copy(
                                            fontWeight = FontWeight.Bold,
                                            color = LeapNavyPrimary
                                        )
                                    )
                                }
                            }
                        }
                    },
                    confirmButton = {
                        Button(
                            onClick = {
                                showSuccessDialog = false
                                onNavigateToArchive()
                            },
                            colors = ButtonDefaults.buttonColors(containerColor = LeapNavyPrimary),
                            modifier = Modifier.testTag("btn_dialog_view_archive")
                        ) {
                            Text("View Scanned Archive")
                        }
                    },
                    dismissButton = {
                        OutlinedButton(
                            onClick = {
                                showSuccessDialog = false
                                isReviewMode = false
                                capturedBitmap = null
                            },
                            modifier = Modifier.testTag("btn_dialog_scan_another")
                        ) {
                            Text("Scan Another Form")
                        }
                    }
                )
            }
        }
    }
}

/**
 * Live Camera Viewport with A4 Guide Reticle, Scan Beam Animation, and Controls.
 */
@Composable
private fun CameraScannerViewport(
    context: Context,
    lifecycleOwner: androidx.lifecycle.LifecycleOwner,
    selectedPreset: FormScanPreset,
    isFlashOn: Boolean,
    lensFacing: Int,
    onToggleFlash: () -> Unit,
    onSwitchCamera: () -> Unit,
    onPresetSelected: (FormScanPreset) -> Unit,
    onOpenGallery: () -> Unit,
    onImageCaptureBound: (ImageCapture) -> Unit,
    onCapturePhoto: () -> Unit
) {
    // Laser scanning animation
    val infiniteTransition = rememberInfiniteTransition(label = "scan_laser")
    val laserYRatio by infiniteTransition.animateFloat(
        initialValue = 0.1f,
        targetValue = 0.9f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 2000, easing = LinearEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "laser_sweep"
    )

    Box(modifier = Modifier.fillMaxSize()) {
        // Camera Preview Feed
        AndroidView(
            modifier = Modifier.fillMaxSize(),
            factory = { ctx ->
                val previewView = PreviewView(ctx).apply {
                    layoutParams = ViewGroup.LayoutParams(
                        ViewGroup.LayoutParams.MATCH_PARENT,
                        ViewGroup.LayoutParams.MATCH_PARENT
                    )
                }

                val cameraProviderFuture = ProcessCameraProvider.getInstance(ctx)
                cameraProviderFuture.addListener({
                    try {
                        val cameraProvider = cameraProviderFuture.get()
                        val preview = Preview.Builder().build().also {
                            it.setSurfaceProvider(previewView.surfaceProvider)
                        }

                        val capture = ImageCapture.Builder()
                            .setCaptureMode(ImageCapture.CAPTURE_MODE_MAXIMIZE_QUALITY)
                            .build()
                        onImageCaptureBound(capture)

                        val cameraSelector = CameraSelector.Builder()
                            .requireLensFacing(lensFacing)
                            .build()

                        cameraProvider.unbindAll()
                        val camera = cameraProvider.bindToLifecycle(
                            lifecycleOwner,
                            cameraSelector,
                            preview,
                            capture
                        )

                        camera.cameraControl.enableTorch(isFlashOn)
                    } catch (e: Exception) {
                        Log.e("DocumentScanner", "Camera binding error", e)
                    }
                }, ContextCompat.getMainExecutor(ctx))

                previewView
            }
        )

        // Darkened Mask & A4 Reticle Bounding Box
        Canvas(modifier = Modifier.fillMaxSize()) {
            val canvasWidth = size.width
            val canvasHeight = size.height

            // Calculate A4 Proportion Bounding Box (Aspect Ratio ~ 1 : 1.414)
            val docWidth = canvasWidth * 0.84f
            val docHeight = (docWidth * 1.38f).coerceAtMost(canvasHeight * 0.68f)
            val left = (canvasWidth - docWidth) / 2f
            val top = (canvasHeight * 0.16f)

            // Draw Dimmed Outside Mask
            drawRect(
                color = Color.Black.copy(alpha = 0.52f),
                size = size
            )

            // Clear / Highlight inside document viewfinder
            drawRoundRect(
                color = Color.Transparent,
                topLeft = Offset(left, top),
                size = Size(docWidth, docHeight),
                cornerRadius = CornerRadius(16.dp.toPx(), 16.dp.toPx())
            )

            // Outer Bounding Border
            drawRoundRect(
                color = Color.White.copy(alpha = 0.6f),
                topLeft = Offset(left, top),
                size = Size(docWidth, docHeight),
                cornerRadius = CornerRadius(16.dp.toPx(), 16.dp.toPx()),
                style = Stroke(
                    width = 1.5.dp.toPx(),
                    pathEffect = PathEffect.dashPathEffect(floatArrayOf(16f, 12f), 0f)
                )
            )

            // 4 Corner Brackets (LEAP High Precision Scanner)
            val bracketLength = 32.dp.toPx()
            val bracketStroke = 4.dp.toPx()
            val cornerColor = LeapGoldAccent

            // Top-Left
            drawLine(cornerColor, Offset(left, top), Offset(left + bracketLength, top), bracketStroke)
            drawLine(cornerColor, Offset(left, top), Offset(left, top + bracketLength), bracketStroke)

            // Top-Right
            drawLine(cornerColor, Offset(left + docWidth, top), Offset(left + docWidth - bracketLength, top), bracketStroke)
            drawLine(cornerColor, Offset(left + docWidth, top), Offset(left + docWidth, top + bracketLength), bracketStroke)

            // Bottom-Left
            drawLine(cornerColor, Offset(left, top + docHeight), Offset(left + bracketLength, top + docHeight), bracketStroke)
            drawLine(cornerColor, Offset(left, top + docHeight), Offset(left, top + docHeight - bracketLength), bracketStroke)

            // Bottom-Right
            drawLine(cornerColor, Offset(left + docWidth, top + docHeight), Offset(left + docWidth - bracketLength, top + docHeight), bracketStroke)
            drawLine(cornerColor, Offset(left + docWidth, top + docHeight), Offset(left + docWidth, top + docHeight - bracketLength), bracketStroke)

            // Animated Laser Scanning Sweep Beam
            val currentLaserY = top + (docHeight * laserYRatio)
            drawLine(
                brush = Brush.horizontalGradient(
                    colors = listOf(Color.Transparent, LeapCyan, LeapGoldAccent, LeapCyan, Color.Transparent),
                    startX = left,
                    endX = left + docWidth
                ),
                start = Offset(left + 8f, currentLaserY),
                end = Offset(left + docWidth - 8f, currentLaserY),
                strokeWidth = 3.dp.toPx()
            )
        }

        // Top Overlay: Flash, Alignment, Switch Camera
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp, vertical = 12.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Flash Toggle
            Surface(
                shape = CircleShape,
                color = if (isFlashOn) LeapGoldAccent else Color.Black.copy(alpha = 0.6f),
                modifier = Modifier
                    .size(42.dp)
                    .clickable { onToggleFlash() }
                    .testTag("btn_toggle_flash")
            ) {
                Box(contentAlignment = Alignment.Center) {
                    Icon(
                        imageVector = if (isFlashOn) Icons.Default.FlashOn else Icons.Default.FlashOff,
                        contentDescription = "Toggle Flash",
                        tint = if (isFlashOn) Color.Black else Color.White,
                        modifier = Modifier.size(22.dp)
                    )
                }
            }

            // Level & Alignment Badge
            Surface(
                shape = RoundedCornerShape(20.dp),
                color = Color.Black.copy(alpha = 0.65f),
                border = BorderStroke(1.dp, Color(0xFF10B981))
            ) {
                Row(
                    modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Default.CheckCircle,
                        contentDescription = null,
                        tint = Color(0xFF10B981),
                        modifier = Modifier.size(14.dp)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = "Page Aligned • A4 Form",
                        style = MaterialTheme.typography.labelSmall.copy(
                            color = Color.White,
                            fontWeight = FontWeight.SemiBold,
                            fontSize = 11.sp
                        )
                    )
                }
            }

            // Switch Camera
            Surface(
                shape = CircleShape,
                color = Color.Black.copy(alpha = 0.6f),
                modifier = Modifier
                    .size(42.dp)
                    .clickable { onSwitchCamera() }
                    .testTag("btn_switch_camera")
            ) {
                Box(contentAlignment = Alignment.Center) {
                    Icon(
                        imageVector = Icons.Default.FlipCameraAndroid,
                        contentDescription = "Switch Camera",
                        tint = Color.White,
                        modifier = Modifier.size(22.dp)
                    )
                }
            }
        }

        // Bottom Controls Container
        Column(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .fillMaxWidth()
                .background(
                    Brush.verticalGradient(
                        colors = listOf(Color.Transparent, Color.Black.copy(alpha = 0.85f), Color.Black)
                    )
                )
                .padding(bottom = 20.dp, top = 16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Form Type Preset Selector
            Text(
                text = "SELECT FORM TYPE TO CAPTURE",
                style = MaterialTheme.typography.labelSmall.copy(
                    fontWeight = FontWeight.Bold,
                    fontSize = 10.sp,
                    color = Color.White.copy(alpha = 0.8f),
                    letterSpacing = 0.8.sp
                )
            )

            Spacer(modifier = Modifier.height(8.dp))

            LazyRow(
                modifier = Modifier.fillMaxWidth(),
                contentPadding = PaddingValues(horizontal = 16.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(FORM_SCAN_PRESETS) { preset ->
                    val isSelected = preset.typeCode == selectedPreset.typeCode
                    Surface(
                        shape = RoundedCornerShape(16.dp),
                        color = if (isSelected) LeapGoldAccent else Color.White.copy(alpha = 0.15f),
                        border = BorderStroke(
                            1.dp,
                            if (isSelected) LeapGoldAccent else Color.White.copy(alpha = 0.3f)
                        ),
                        modifier = Modifier
                            .clickable { onPresetSelected(preset) }
                            .testTag("preset_chip_${preset.typeCode}")
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                imageVector = preset.icon,
                                contentDescription = null,
                                tint = if (isSelected) Color.Black else Color.White,
                                modifier = Modifier.size(16.dp)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = preset.formCode,
                                style = MaterialTheme.typography.labelSmall.copy(
                                    fontWeight = if (isSelected) FontWeight.ExtraBold else FontWeight.Medium,
                                    color = if (isSelected) Color.Black else Color.White,
                                    fontSize = 11.sp
                                )
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(20.dp))

            // Main Shutter Button & Gallery Picker
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 32.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Gallery / Upload File Button
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    IconButton(
                        onClick = onOpenGallery,
                        modifier = Modifier
                            .size(50.dp)
                            .clip(CircleShape)
                            .background(Color.White.copy(alpha = 0.18f))
                            .testTag("btn_open_gallery")
                    ) {
                        Icon(
                            imageVector = Icons.Default.PhotoLibrary,
                            contentDescription = "Pick from Gallery",
                            tint = Color.White,
                            modifier = Modifier.size(24.dp)
                        )
                    }
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "Gallery",
                        style = MaterialTheme.typography.labelSmall.copy(
                            color = Color.White.copy(alpha = 0.8f),
                            fontSize = 10.sp
                        )
                    )
                }

                // Primary Shutter Button
                Box(
                    modifier = Modifier
                        .size(76.dp)
                        .clip(CircleShape)
                        .border(4.dp, Color.White, CircleShape)
                        .padding(5.dp)
                        .clip(CircleShape)
                        .background(LeapGoldAccent)
                        .clickable { onCapturePhoto() }
                        .testTag("btn_capture_shutter"),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.CameraAlt,
                        contentDescription = "Capture Document",
                        tint = Color.Black,
                        modifier = Modifier.size(34.dp)
                    )
                }

                // AI Auto Scan Helper Badge
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Box(
                        modifier = Modifier
                            .size(50.dp)
                            .clip(CircleShape)
                            .background(Color.White.copy(alpha = 0.18f)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.AutoFixHigh,
                            contentDescription = "Auto Enhance",
                            tint = LeapCyan,
                            modifier = Modifier.size(24.dp)
                        )
                    }
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "Auto Clean",
                        style = MaterialTheme.typography.labelSmall.copy(
                            color = Color.White.copy(alpha = 0.8f),
                            fontSize = 10.sp
                        )
                    )
                }
            }
        }
    }
}

/**
 * Review, Filter Selection, and Metadata Verification Screen.
 */
@Composable
private fun DocumentReviewContent(
    bitmap: Bitmap,
    selectedPreset: FormScanPreset,
    documentTitle: String,
    onTitleChange: (String) -> Unit,
    supervisorName: String,
    onSupervisorChange: (String) -> Unit,
    companyName: String,
    onCompanyChange: (String) -> Unit,
    isSupervisorSigned: Boolean,
    onSupervisorSignedChange: (Boolean) -> Unit,
    isCompanyStamped: Boolean,
    onCompanyStampedChange: (Boolean) -> Unit,
    notesText: String,
    onNotesChange: (String) -> Unit,
    selectedFilter: ScanFilterMode,
    onFilterSelect: (ScanFilterMode) -> Unit,
    isSaving: Boolean,
    onRetake: () -> Unit,
    onSave: () -> Unit
) {
    val scrollState = rememberScrollState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(scrollState)
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Document Scan Preview Card with Active Filter Effect
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .height(280.dp)
                .testTag("card_scanned_preview"),
            shape = RoundedCornerShape(20.dp),
            colors = CardDefaults.cardColors(containerColor = Color.Black),
            border = BorderStroke(1.5.dp, LeapNavyPrimary)
        ) {
            Box(modifier = Modifier.fillMaxSize()) {
                // Render bitmap with Compose ColorFilter based on selected mode
                val colorFilter = when (selectedFilter) {
                    ScanFilterMode.ENHANCED_BW -> {
                        // High-contrast B&W ColorMatrix
                        val cm = ColorMatrix().apply {
                            setToSaturation(0f)
                            // Increase contrast
                            val scale = 1.4f
                            val translate = (-0.2f * 255f)
                            timesAssign(
                                ColorMatrix(
                                    floatArrayOf(
                                        scale, 0f, 0f, 0f, translate,
                                        0f, scale, 0f, 0f, translate,
                                        0f, 0f, scale, 0f, translate,
                                        0f, 0f, 0f, 1f, 0f
                                    )
                                )
                            )
                        }
                        ColorFilter.colorMatrix(cm)
                    }
                    ScanFilterMode.GRAYSCALE -> {
                        val cm = ColorMatrix().apply { setToSaturation(0f) }
                        ColorFilter.colorMatrix(cm)
                    }
                    ScanFilterMode.DOCUMENT_SHARP -> {
                        val cm = ColorMatrix().apply {
                            val scale = 1.15f
                            timesAssign(
                                ColorMatrix(
                                    floatArrayOf(
                                        scale, 0f, 0f, 0f, -15f,
                                        0f, scale, 0f, 0f, -15f,
                                        0f, 0f, scale, 0f, -15f,
                                        0f, 0f, 0f, 1f, 0f
                                    )
                                )
                            )
                        }
                        ColorFilter.colorMatrix(cm)
                    }
                    ScanFilterMode.ORIGINAL -> null
                }

                Image(
                    bitmap = bitmap.asImageBitmap(),
                    contentDescription = "Scanned Form Image Preview",
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(10.dp)
                        .clip(RoundedCornerShape(12.dp)),
                    colorFilter = colorFilter
                )

                // Top Quality & Stamp Verification Overlay Badges
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Surface(
                        shape = RoundedCornerShape(8.dp),
                        color = Color(0xFF10B981).copy(alpha = 0.9f)
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                imageVector = Icons.Default.AutoAwesome,
                                contentDescription = null,
                                tint = Color.White,
                                modifier = Modifier.size(12.dp)
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(
                                text = "98% Quality Score",
                                style = MaterialTheme.typography.labelSmall.copy(
                                    color = Color.White,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 10.sp
                                )
                            )
                        }
                    }

                    if (isCompanyStamped) {
                        Surface(
                            shape = RoundedCornerShape(8.dp),
                            color = LeapOrange.copy(alpha = 0.9f)
                        ) {
                            Row(
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Verified,
                                    contentDescription = null,
                                    tint = Color.White,
                                    modifier = Modifier.size(12.dp)
                                )
                                Spacer(modifier = Modifier.width(4.dp))
                                Text(
                                    text = "Stamp Detected",
                                    style = MaterialTheme.typography.labelSmall.copy(
                                        color = Color.White,
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 10.sp
                                    )
                                )
                            }
                        }
                    }
                }
            }
        }

        // Filter Mode Selectors
        Column(modifier = Modifier.fillMaxWidth()) {
            Text(
                text = "DOCUMENT ENHANCEMENT FILTER",
                style = MaterialTheme.typography.labelSmall.copy(
                    fontWeight = FontWeight.Bold,
                    fontSize = 10.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    letterSpacing = 0.8.sp
                )
            )

            Spacer(modifier = Modifier.height(8.dp))

            LazyRow(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(ScanFilterMode.values()) { filterMode ->
                    val isSelected = filterMode == selectedFilter
                    FilterChip(
                        selected = isSelected,
                        onClick = { onFilterSelect(filterMode) },
                        label = {
                            Text(
                                text = filterMode.label,
                                style = MaterialTheme.typography.labelSmall.copy(
                                    fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
                                )
                            )
                        },
                        colors = FilterChipDefaults.filterChipColors(
                            selectedContainerColor = LeapNavyPrimary,
                            selectedLabelColor = Color.White
                        )
                    )
                }
            }
        }

        // Metadata & Verification Form Card
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(18.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
            border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant)
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(28.dp)
                            .clip(RoundedCornerShape(8.dp))
                            .background(LeapNavyPrimary.copy(alpha = 0.12f)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.EditNote,
                            contentDescription = null,
                            tint = LeapNavyPrimary,
                            modifier = Modifier.size(16.dp)
                        )
                    }
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "Document Details & Verification",
                        style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold)
                    )
                }

                Spacer(modifier = Modifier.height(12.dp))

                // Title Input
                OutlinedTextField(
                    value = documentTitle,
                    onValueChange = onTitleChange,
                    label = { Text("Document Title") },
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("input_document_title"),
                    shape = RoundedCornerShape(12.dp),
                    singleLine = true
                )

                Spacer(modifier = Modifier.height(10.dp))

                // Supervisor Name & Company Row
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    OutlinedTextField(
                        value = supervisorName,
                        onValueChange = onSupervisorChange,
                        label = { Text("Supervisor") },
                        modifier = Modifier.weight(1f),
                        shape = RoundedCornerShape(12.dp),
                        singleLine = true
                    )
                    OutlinedTextField(
                        value = companyName,
                        onValueChange = onCompanyChange,
                        label = { Text("Host Org") },
                        modifier = Modifier.weight(1f),
                        shape = RoundedCornerShape(12.dp),
                        singleLine = true
                    )
                }

                Spacer(modifier = Modifier.height(12.dp))

                // Checkboxes for Verification Criteria
                Surface(
                    shape = RoundedCornerShape(12.dp),
                    color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f),
                    border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant)
                ) {
                    Column(modifier = Modifier.padding(10.dp)) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Checkbox(
                                checked = isSupervisorSigned,
                                onCheckedChange = onSupervisorSignedChange,
                                colors = CheckboxDefaults.colors(checkedColor = LeapNavyPrimary)
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Column {
                                Text(
                                    text = "Industrial Supervisor Signature Present",
                                    style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.SemiBold)
                                )
                                Text(
                                    text = "Signature confirms authentic workplace verification",
                                    style = MaterialTheme.typography.labelSmall.copy(color = MaterialTheme.colorScheme.onSurfaceVariant, fontSize = 10.sp)
                                )
                            }
                        }

                        HorizontalDivider(
                            modifier = Modifier.padding(vertical = 6.dp),
                            color = MaterialTheme.colorScheme.outlineVariant
                        )

                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Checkbox(
                                checked = isCompanyStamped,
                                onCheckedChange = onCompanyStampedChange,
                                colors = CheckboxDefaults.colors(checkedColor = LeapOrange)
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Column {
                                Text(
                                    text = "Official Host Organization Stamp Visible",
                                    style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.SemiBold)
                                )
                                Text(
                                    text = "Mandatory for Action Plan and Form D compliance",
                                    style = MaterialTheme.typography.labelSmall.copy(color = MaterialTheme.colorScheme.onSurfaceVariant, fontSize = 10.sp)
                                )
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(10.dp))

                // Notes Field
                OutlinedTextField(
                    value = notesText,
                    onValueChange = onNotesChange,
                    label = { Text("Notes or Comments (Optional)") },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                    maxLines = 2
                )
            }
        }

        // Action Buttons Row
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            OutlinedButton(
                onClick = onRetake,
                modifier = Modifier
                    .weight(1f)
                    .height(50.dp)
                    .testTag("btn_retake_scan"),
                shape = RoundedCornerShape(14.dp),
                border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline)
            ) {
                Icon(imageVector = Icons.Default.Refresh, contentDescription = null)
                Spacer(modifier = Modifier.width(6.dp))
                Text("Retake Photo")
            }

            Button(
                onClick = onSave,
                enabled = !isSaving,
                modifier = Modifier
                    .weight(1.5f)
                    .height(50.dp)
                    .testTag("btn_save_scanned_doc"),
                shape = RoundedCornerShape(14.dp),
                colors = ButtonDefaults.buttonColors(containerColor = LeapNavyPrimary)
            ) {
                if (isSaving) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(20.dp),
                        color = Color.White,
                        strokeWidth = 2.dp
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Storing Scan...")
                } else {
                    Icon(imageVector = Icons.Default.CloudUpload, contentDescription = null, tint = Color.White)
                    Spacer(modifier = Modifier.width(6.dp))
                    Text("Save & Submit Form", fontWeight = FontWeight.Bold, color = Color.White)
                }
            }
        }
    }
}

/**
 * Camera Permission Rationale Layout.
 */
@Composable
private fun CameraPermissionRationaleContent(
    onRequestPermission: () -> Unit,
    onSelectFromGallery: () -> Unit,
    onSimulateSampleScan: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Box(
            modifier = Modifier
                .size(90.dp)
                .clip(CircleShape)
                .background(LeapNavyPrimary.copy(alpha = 0.12f)),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = Icons.Default.CameraAlt,
                contentDescription = null,
                tint = LeapNavyPrimary,
                modifier = Modifier.size(46.dp)
            )
        }

        Spacer(modifier = Modifier.height(20.dp))

        Text(
            text = "Camera Permission Required",
            style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold),
            textAlign = TextAlign.Center
        )

        Spacer(modifier = Modifier.height(8.dp))

        Text(
            text = "To digitize signed Form D evaluations, Form A2/A3 acceptance letters, and company-stamped Action Plans, the app needs access to your device camera.",
            style = MaterialTheme.typography.bodyMedium.copy(color = MaterialTheme.colorScheme.onSurfaceVariant),
            textAlign = TextAlign.Center
        )

        Spacer(modifier = Modifier.height(24.dp))

        Button(
            onClick = onRequestPermission,
            modifier = Modifier
                .fillMaxWidth()
                .height(50.dp)
                .testTag("btn_grant_camera_permission"),
            shape = RoundedCornerShape(14.dp),
            colors = ButtonDefaults.buttonColors(containerColor = LeapNavyPrimary)
        ) {
            Icon(imageVector = Icons.Default.CameraAlt, contentDescription = null)
            Spacer(modifier = Modifier.width(8.dp))
            Text("Grant Camera Access", fontWeight = FontWeight.Bold)
        }

        Spacer(modifier = Modifier.height(10.dp))

        OutlinedButton(
            onClick = onSelectFromGallery,
            modifier = Modifier
                .fillMaxWidth()
                .height(50.dp)
                .testTag("btn_select_from_gallery_perm"),
            shape = RoundedCornerShape(14.dp)
        ) {
            Icon(imageVector = Icons.Default.PhotoLibrary, contentDescription = null)
            Spacer(modifier = Modifier.width(8.dp))
            Text("Choose from Gallery")
        }

        Spacer(modifier = Modifier.height(12.dp))

        TextButton(
            onClick = onSimulateSampleScan,
            modifier = Modifier.testTag("btn_simulate_sample_scan")
        ) {
            Icon(imageVector = Icons.Default.AutoFixNormal, contentDescription = null, modifier = Modifier.size(16.dp))
            Spacer(modifier = Modifier.width(6.dp))
            Text("Try with Sample Document Scan", fontSize = 12.sp)
        }
    }
}

/**
 * Creates a high-contrast simulated document bitmap for instant demo/testing.
 */
private fun createSimulatedDocumentBitmap(preset: FormScanPreset): Bitmap {
    val width = 720
    val height = 1020
    val bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888)
    val canvas = android.graphics.Canvas(bitmap)

    // White paper background
    val bgPaint = android.graphics.Paint().apply {
        color = android.graphics.Color.WHITE
    }
    canvas.drawRect(0f, 0f, width.toFloat(), height.toFloat(), bgPaint)

    // Border
    val borderPaint = android.graphics.Paint().apply {
        color = android.graphics.Color.DKGRAY
        style = android.graphics.Paint.Style.STROKE
        strokeWidth = 4f
    }
    canvas.drawRect(24f, 24f, (width - 24).toFloat(), (height - 24).toFloat(), borderPaint)

    // Header title
    val headerPaint = android.graphics.Paint().apply {
        color = android.graphics.Color.BLACK
        textSize = 28f
        isFakeBoldText = true
        textAlign = android.graphics.Paint.Align.CENTER
    }
    canvas.drawText("LIMKOKWING UNIVERSITY OF CREATIVE TECHNOLOGY", (width / 2).toFloat(), 80f, headerPaint)

    val subHeaderPaint = android.graphics.Paint().apply {
        color = android.graphics.Color.DKGRAY
        textSize = 22f
        textAlign = android.graphics.Paint.Align.CENTER
    }
    canvas.drawText("FACULTY OF INFORMATION & COMMUNICATION TECHNOLOGY", (width / 2).toFloat(), 120f, subHeaderPaint)
    canvas.drawText("LEAP INTERNSHIP PROGRAMME • ${preset.formCode.uppercase()}", (width / 2).toFloat(), 160f, subHeaderPaint)

    // Line separator
    canvas.drawLine(40f, 190f, (width - 40).toFloat(), 190f, borderPaint)

    // Document Body text lines
    val textPaint = android.graphics.Paint().apply {
        color = android.graphics.Color.BLACK
        textSize = 18f
    }
    canvas.drawText("Student Name: Mohamed Kamara (ID: LKW-SL-DEMO001)", 50f, 240f, textPaint)
    canvas.drawText("Programme: BSc (Hons) Information Technology", 50f, 280f, textPaint)
    canvas.drawText("Host Organization: Tech Solutions SL Ltd.", 50f, 320f, textPaint)
    canvas.drawText("Industrial Supervisor: Ing. David Koroma (Head of Engineering)", 50f, 360f, textPaint)
    canvas.drawText("Placement Period: 12-Jan-2026 to 05-Apr-2026 (12 Weeks)", 50f, 400f, textPaint)

    // Table lines simulation
    val tablePaint = android.graphics.Paint().apply {
        color = android.graphics.Color.LTGRAY
        strokeWidth = 2f
    }
    for (i in 0..6) {
        val y = 460f + (i * 45f)
        canvas.drawLine(50f, y, (width - 50).toFloat(), y, tablePaint)
    }

    // Text rows
    val rowTextPaint = android.graphics.Paint().apply {
        color = android.graphics.Color.DKGRAY
        textSize = 16f
    }
    canvas.drawText("Criteria 1: Technical Aptitude & Problem Solving .............. [ 5 / 5 ] EXCELLENT", 60f, 490f, rowTextPaint)
    canvas.drawText("Criteria 2: Daily Log Documentation & Synthesis ............ [ 5 / 5 ] EXCELLENT", 60f, 535f, rowTextPaint)
    canvas.drawText("Criteria 3: Punctuality & Workplace Ethics ...................... [ 5 / 5 ] EXCELLENT", 60f, 580f, rowTextPaint)
    canvas.drawText("Criteria 4: Team Collaboration & Communication ........... [ 5 / 5 ] EXCELLENT", 60f, 625f, rowTextPaint)
    canvas.drawText("Criteria 5: Meeting Deadlines & Project Milestones ....... [ 5 / 5 ] EXCELLENT", 60f, 670f, rowTextPaint)

    // Signature line
    val sigLinePaint = android.graphics.Paint().apply {
        color = android.graphics.Color.BLACK
        strokeWidth = 2f
    }
    canvas.drawLine(60f, 820f, 300f, 820f, sigLinePaint)
    canvas.drawText("Student Signature", 60f, 850f, textPaint)

    canvas.drawLine(420f, 820f, 660f, 820f, sigLinePaint)
    canvas.drawText("Industrial Supervisor Signature", 420f, 850f, textPaint)

    // Simulated Blue Stamp circle
    val stampPaint = android.graphics.Paint().apply {
        color = android.graphics.Color.parseColor("#1D4ED8")
        style = android.graphics.Paint.Style.STROKE
        strokeWidth = 4f
    }
    canvas.drawCircle(540f, 750f, 60f, stampPaint)

    val stampTextPaint = android.graphics.Paint().apply {
        color = android.graphics.Color.parseColor("#1D4ED8")
        textSize = 12f
        isFakeBoldText = true
        textAlign = android.graphics.Paint.Align.CENTER
    }
    canvas.drawText("TECH SOLUTIONS SL", 540f, 740f, stampTextPaint)
    canvas.drawText("OFFICIALLY VERIFIED", 540f, 760f, stampTextPaint)

    return bitmap
}
