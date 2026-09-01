package com.example.ui.screens.student

import androidx.compose.animation.*
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.automirrored.filled.MenuBook
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.local.entities.InternshipApplicationEntity
import com.example.ui.components.LeapTopAppBar
import com.example.ui.components.PaperVsDigitalBadge
import com.example.ui.components.StatusChip
import com.example.ui.theme.*
import com.example.ui.viewmodel.LeapViewModel
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class, ExperimentalLayoutApi::class)
@Composable
fun InternshipApplicationScreen(
    viewModel: LeapViewModel,
    onBack: () -> Unit
) {
    val currentStudent by viewModel.currentStudent.collectAsState()
    val currentApp by viewModel.currentApplication.collectAsState()
    val coroutineScope = rememberCoroutineScope()
    val snackbarHostState = remember { SnackbarHostState() }

    // Step state (0: Student Info, 1: Industry Sector, 2: CV & Skills, 3: Review & Submit)
    var currentStep by remember { mutableStateOf(0) }

    // Form states - Step 1: Student Details
    var fullName by remember { mutableStateOf(currentStudent?.name ?: "Mohamed Kamara") }
    var studentIdNumber by remember { mutableStateOf(currentStudent?.studentIdCode ?: "LKW-SL-DEMO001") }
    var email by remember { mutableStateOf(currentStudent?.email ?: "student@leap.demo") }
    var phone by remember { mutableStateOf(currentStudent?.phone ?: "+232 78 450123") }
    var faculty by remember { mutableStateOf("Faculty of Information & Communication Technology") }
    var programme by remember { mutableStateOf(currentStudent?.programme ?: "BSc (Hons) in Information Technology") }
    var currentYearSemester by remember { mutableStateOf("Year 3, Semester 2") }
    var cgpa by remember { mutableStateOf("3.82") }
    var address by remember { mutableStateOf("22 Sanders Street, Freetown") }
    var emergencyContactName by remember { mutableStateOf("Fatmata Kamara") }
    var emergencyContactPhone by remember { mutableStateOf("+232 76 112233") }
    var emergencyContactRelation by remember { mutableStateOf("Parent / Guardian") }

    // Form states - Step 2: Preferred Industry Sector & Placement
    var primarySector by remember { mutableStateOf("Software & Cloud Engineering") }
    var secondarySector by remember { mutableStateOf("Telecommunications & Digital Services") }
    var preferredRoleDepartment by remember { mutableStateOf("Mobile & Web Software Development") }
    var preferredLocation by remember { mutableStateOf("Freetown Central") }
    var preferredWorkMode by remember { mutableStateOf("On-site") }
    var internshipDurationWeeks by remember { mutableStateOf(12) }
    var preferredStartDate by remember { mutableStateOf("15-Mar-2026") }
    var hasOwnLaptop by remember { mutableStateOf(true) }
    var willingnessToRelocate by remember { mutableStateOf(false) }

    // Form states - Step 3: CV Information & Credentials
    var professionalSummary by remember {
        mutableStateOf(
            "Dedicated penultimate-year IT student with hands-on experience in Kotlin Android development, Room local database architecture, and REST API integration. Passionate about building robust software solutions for African enterprise ecosystems."
        )
    }
    var selectedTechSkills by remember {
        mutableStateOf(
            listOf("Kotlin", "Jetpack Compose", "Java", "Python", "SQL / Room DB", "Git", "REST APIs", "Linux")
        )
    }
    var customSkillInput by remember { mutableStateOf("") }
    var selectedSoftSkills by remember {
        mutableStateOf(
            listOf("Problem Solving", "Team Leadership", "Agile Collaboration", "Technical Writing", "Fast Learner")
        )
    }
    var educationHistory by remember {
        mutableStateOf("Limkokwing University of Creative Technology (BSc Hons IT, 2023 - Present)\nSierra Leone Grammar School (WASSCE, 2017 - 2023)")
    }
    var projectExperience by remember {
        mutableStateOf("1. LEAP Offline-First Android Sync Manager (Kotlin, Room, Jetpack Compose)\n2. Campus Student Portal Web Backend (Node.js, PostgreSQL)\n3. Local Point-of-Sale Mobile Application")
    }
    var certifications by remember {
        mutableStateOf("Google IT Support Professional Certificate, Cisco Networking Basics (CCNA 1)")
    }
    var cvFileName by remember { mutableStateOf("Mohamed_Kamara_LEAP_CV_2026.pdf") }
    var cvFileSize by remember { mutableStateOf("1.8 MB") }
    var cvLastModified by remember { mutableStateOf("Today, 10:15 AM") }
    var portfolioUrl by remember { mutableStateOf("https://github.com/mohamedkamara-dev") }
    var linkedInUrl by remember { mutableStateOf("https://linkedin.com/in/mohamed-kamara-sl") }
    var refereeName by remember { mutableStateOf("Dr. Fatmata Sesay") }
    var refereeTitle by remember { mutableStateOf("Head of Computing, Limkokwing University SL") }
    var refereeContact by remember { mutableStateOf("f.sesay@limkokwing.edu.sl") }

    // Form states - Step 4: Declarations & Modal
    var isDeclarationAccepted by remember { mutableStateOf(true) }
    var showSuccessDialog by remember { mutableStateOf(false) }
    var submittedRefNumber by remember { mutableStateOf("") }
    var showCvPreviewDialog by remember { mutableStateOf(false) }

    // Pre-populate if application exists
    LaunchedEffect(currentApp) {
        currentApp?.let { app ->
            fullName = app.fullName
            studentIdNumber = app.studentIdNumber
            email = app.email
            phone = app.phone
            faculty = app.faculty
            programme = app.programme
            currentYearSemester = app.currentYearSemester
            cgpa = app.cgpa
            address = app.address
            emergencyContactName = app.emergencyContactName
            emergencyContactPhone = app.emergencyContactPhone
            emergencyContactRelation = app.emergencyContactRelation
            primarySector = app.primarySector
            secondarySector = app.secondarySector
            preferredRoleDepartment = app.preferredRoleDepartment
            preferredLocation = app.preferredLocation
            preferredWorkMode = app.preferredWorkMode
            internshipDurationWeeks = app.internshipDurationWeeks
            preferredStartDate = app.preferredStartDate
            hasOwnLaptop = app.hasOwnLaptop
            willingnessToRelocate = app.willingnessToRelocate
            professionalSummary = app.professionalSummary
            selectedTechSkills = app.technicalSkills.split(",").map { it.trim() }.filter { it.isNotEmpty() }
            selectedSoftSkills = app.softSkills.split(",").map { it.trim() }.filter { it.isNotEmpty() }
            educationHistory = app.educationHistory
            projectExperience = app.projectExperience
            certifications = app.certifications
            cvFileName = app.cvFileName
            cvFileSize = app.cvFileSize
            portfolioUrl = app.portfolioOrGithubUrl
            linkedInUrl = app.linkedInUrl
            refereeName = app.academicRefereeName
            refereeTitle = app.academicRefereeTitle
            refereeContact = app.academicRefereeContact
            isDeclarationAccepted = app.isDeclarationAccepted
        }
    }

    val stepTitles = listOf(
        "Student Details",
        "Industry Sector",
        "CV & Credentials",
        "Review & Submit"
    )

    val stepIcons = listOf(
        Icons.Default.Person,
        Icons.Default.Business,
        Icons.Default.Description,
        Icons.Default.CheckCircle
    )

    Scaffold(
        topBar = {
            LeapTopAppBar(
                title = "Internship Application",
                subtitle = "Multi-Step Industry Placement Form",
                showBackButton = true,
                onBackClick = onBack
            )
        },
        snackbarHost = { SnackbarHost(snackbarHostState) }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .background(MaterialTheme.colorScheme.background)
        ) {
            // Header Banner with Status
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 8.dp),
                shape = RoundedCornerShape(12.dp),
                colors = CardDefaults.cardColors(containerColor = LeapNavyPrimary)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(14.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = "LEAP Placement Portal",
                            color = Color.White,
                            fontWeight = FontWeight.Bold,
                            fontSize = 16.sp
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = if (currentApp != null && currentApp?.submissionStatus != "Draft") {
                                "Ref: ${currentApp?.applicationRefNumber} • Status: ${currentApp?.submissionStatus}"
                            } else {
                                "Complete all 4 steps to submit for industrial placement matching."
                            },
                            color = Color.White.copy(alpha = 0.85f),
                            fontSize = 12.sp
                        )
                    }
                    if (currentApp != null) {
                        StatusChip(
                            status = currentApp?.submissionStatus ?: "Submitted"
                        )
                    }
                }
            }

            PaperVsDigitalBadge(
                paperProcess = "Manual paper CV submissions, lost physical forms, and weeks of coordinator routing.",
                digitalProcess = "Instant structured digital profile with multi-sector matching, CV storage, and real-time coordinator alerts.",
                modifier = Modifier.padding(horizontal = 16.dp, vertical = 4.dp)
            )

            // Step Progress Indicator
            StepProgressHeader(
                steps = stepTitles,
                icons = stepIcons,
                currentStep = currentStep,
                onStepClick = { stepIndex ->
                    currentStep = stepIndex
                }
            )

            // Step Content (Scrollable)
            Box(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth()
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .verticalScroll(rememberScrollState())
                        .padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    AnimatedContent(
                        targetState = currentStep,
                        label = "step_transition"
                    ) { targetStep ->
                        when (targetStep) {
                            0 -> Step1StudentDetailsContent(
                                fullName = fullName,
                                onFullNameChange = { fullName = it },
                                studentIdNumber = studentIdNumber,
                                onStudentIdChange = { studentIdNumber = it },
                                email = email,
                                onEmailChange = { email = it },
                                phone = phone,
                                onPhoneChange = { phone = it },
                                faculty = faculty,
                                onFacultyChange = { faculty = it },
                                programme = programme,
                                onProgrammeChange = { programme = it },
                                currentYearSemester = currentYearSemester,
                                onYearSemesterChange = { currentYearSemester = it },
                                cgpa = cgpa,
                                onCgpaChange = { cgpa = it },
                                address = address,
                                onAddressChange = { address = it },
                                emergencyContactName = emergencyContactName,
                                onEmergencyNameChange = { emergencyContactName = it },
                                emergencyContactPhone = emergencyContactPhone,
                                onEmergencyPhoneChange = { emergencyContactPhone = it },
                                emergencyContactRelation = emergencyContactRelation,
                                onEmergencyRelationChange = { emergencyContactRelation = it }
                            )

                            1 -> Step2IndustrySectorContent(
                                primarySector = primarySector,
                                onPrimarySectorChange = { primarySector = it },
                                secondarySector = secondarySector,
                                onSecondarySectorChange = { secondarySector = it },
                                preferredRole = preferredRoleDepartment,
                                onPreferredRoleChange = { preferredRoleDepartment = it },
                                preferredLocation = preferredLocation,
                                onPreferredLocationChange = { preferredLocation = it },
                                preferredWorkMode = preferredWorkMode,
                                onPreferredWorkModeChange = { preferredWorkMode = it },
                                preferredStartDate = preferredStartDate,
                                onPreferredStartDateChange = { preferredStartDate = it },
                                hasOwnLaptop = hasOwnLaptop,
                                onHasOwnLaptopChange = { hasOwnLaptop = it },
                                willingnessToRelocate = willingnessToRelocate,
                                onWillingnessToRelocateChange = { willingnessToRelocate = it }
                            )

                            2 -> Step3CvInformationContent(
                                professionalSummary = professionalSummary,
                                onProfessionalSummaryChange = { professionalSummary = it },
                                selectedTechSkills = selectedTechSkills,
                                onAddTechSkill = { skill ->
                                    if (skill.isNotBlank() && !selectedTechSkills.contains(skill.trim())) {
                                        selectedTechSkills = selectedTechSkills + skill.trim()
                                    }
                                },
                                onRemoveTechSkill = { skill ->
                                    selectedTechSkills = selectedTechSkills.filter { it != skill }
                                },
                                customSkillInput = customSkillInput,
                                onCustomSkillInputChange = { customSkillInput = it },
                                selectedSoftSkills = selectedSoftSkills,
                                onAddSoftSkill = { skill ->
                                    if (skill.isNotBlank() && !selectedSoftSkills.contains(skill.trim())) {
                                        selectedSoftSkills = selectedSoftSkills + skill.trim()
                                    }
                                },
                                onRemoveSoftSkill = { skill ->
                                    selectedSoftSkills = selectedSoftSkills.filter { it != skill }
                                },
                                educationHistory = educationHistory,
                                onEducationHistoryChange = { educationHistory = it },
                                projectExperience = projectExperience,
                                onProjectExperienceChange = { projectExperience = it },
                                certifications = certifications,
                                onCertificationsChange = { certifications = it },
                                cvFileName = cvFileName,
                                cvFileSize = cvFileSize,
                                cvLastModified = cvLastModified,
                                onUploadCv = {
                                    cvFileName = "${fullName.replace(" ", "_")}_Resume_Updated.pdf"
                                    cvFileSize = "2.1 MB"
                                    cvLastModified = "Just now"
                                    coroutineScope.launch {
                                        snackbarHostState.showSnackbar("New CV Document '$cvFileName' uploaded & verified.")
                                    }
                                },
                                onPreviewCv = { showCvPreviewDialog = true },
                                portfolioUrl = portfolioUrl,
                                onPortfolioUrlChange = { portfolioUrl = it },
                                linkedInUrl = linkedInUrl,
                                onLinkedInUrlChange = { linkedInUrl = it },
                                refereeName = refereeName,
                                onRefereeNameChange = { refereeName = it },
                                refereeTitle = refereeTitle,
                                onRefereeTitleChange = { refereeTitle = it },
                                refereeContact = refereeContact,
                                onRefereeContactChange = { refereeContact = it }
                            )

                            3 -> Step4ReviewAndSubmitContent(
                                fullName = fullName,
                                studentIdNumber = studentIdNumber,
                                email = email,
                                phone = phone,
                                programme = programme,
                                cgpa = cgpa,
                                primarySector = primarySector,
                                secondarySector = secondarySector,
                                preferredRole = preferredRoleDepartment,
                                preferredLocation = preferredLocation,
                                preferredWorkMode = preferredWorkMode,
                                cvFileName = cvFileName,
                                cvFileSize = cvFileSize,
                                selectedTechSkills = selectedTechSkills,
                                isDeclarationAccepted = isDeclarationAccepted,
                                onDeclarationChange = { isDeclarationAccepted = it },
                                onPreviewCv = { showCvPreviewDialog = true }
                            )
                        }
                    }
                }
            }

            // Bottom Navigation Controls
            Surface(
                tonalElevation = 8.dp,
                shadowElevation = 8.dp,
                color = MaterialTheme.colorScheme.surface
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // Back / Previous Button
                    if (currentStep > 0) {
                        OutlinedButton(
                            onClick = { currentStep -= 1 },
                            modifier = Modifier
                                .weight(1f)
                                .testTag("btn_step_previous"),
                            shape = RoundedCornerShape(8.dp)
                        ) {
                            Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = null, modifier = Modifier.size(18.dp))
                            Spacer(modifier = Modifier.width(6.dp))
                            Text("Previous")
                        }
                        Spacer(modifier = Modifier.width(12.dp))
                    } else {
                        // Draft button on Step 1
                        OutlinedButton(
                            onClick = {
                                val draft = buildApplicationEntity(
                                    studentId = currentStudent?.id ?: 1L,
                                    currentApp = currentApp,
                                    fullName = fullName,
                                    studentIdNumber = studentIdNumber,
                                    email = email,
                                    phone = phone,
                                    faculty = faculty,
                                    programme = programme,
                                    currentYearSemester = currentYearSemester,
                                    cgpa = cgpa,
                                    address = address,
                                    emergencyContactName = emergencyContactName,
                                    emergencyContactPhone = emergencyContactPhone,
                                    emergencyContactRelation = emergencyContactRelation,
                                    primarySector = primarySector,
                                    secondarySector = secondarySector,
                                    preferredRole = preferredRoleDepartment,
                                    preferredLocation = preferredLocation,
                                    preferredWorkMode = preferredWorkMode,
                                    durationWeeks = internshipDurationWeeks,
                                    preferredStartDate = preferredStartDate,
                                    hasOwnLaptop = hasOwnLaptop,
                                    willingnessToRelocate = willingnessToRelocate,
                                    professionalSummary = professionalSummary,
                                    techSkills = selectedTechSkills.joinToString(", "),
                                    softSkills = selectedSoftSkills.joinToString(", "),
                                    educationHistory = educationHistory,
                                    projectExperience = projectExperience,
                                    certifications = certifications,
                                    cvFileName = cvFileName,
                                    cvFileSize = cvFileSize,
                                    cvLastModified = cvLastModified,
                                    portfolioUrl = portfolioUrl,
                                    linkedInUrl = linkedInUrl,
                                    refereeName = refereeName,
                                    refereeTitle = refereeTitle,
                                    refereeContact = refereeContact,
                                    isDeclarationAccepted = isDeclarationAccepted,
                                    isDraft = true
                                )
                                viewModel.saveApplicationDraft(draft) {
                                    coroutineScope.launch {
                                        snackbarHostState.showSnackbar("Application draft saved successfully.")
                                    }
                                }
                            },
                            modifier = Modifier
                                .weight(1f)
                                .testTag("btn_save_draft"),
                            shape = RoundedCornerShape(8.dp)
                        ) {
                            Icon(Icons.Default.Save, contentDescription = null, modifier = Modifier.size(18.dp))
                            Spacer(modifier = Modifier.width(6.dp))
                            Text("Save Draft")
                        }
                        Spacer(modifier = Modifier.width(12.dp))
                    }

                    // Next / Submit Button
                    if (currentStep < 3) {
                        Button(
                            onClick = {
                                if (validateCurrentStep(currentStep, fullName, studentIdNumber, email, phone, cgpa, primarySector, selectedTechSkills)) {
                                    currentStep += 1
                                } else {
                                    coroutineScope.launch {
                                        snackbarHostState.showSnackbar("Please fill in the required fields before continuing.")
                                    }
                                }
                            },
                            modifier = Modifier
                                .weight(1f)
                                .testTag("btn_step_next"),
                            shape = RoundedCornerShape(8.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = LeapNavyPrimary)
                        ) {
                            Text("Next")
                            Spacer(modifier = Modifier.width(6.dp))
                            Icon(Icons.AutoMirrored.Filled.ArrowForward, contentDescription = null, modifier = Modifier.size(18.dp))
                        }
                    } else {
                        Button(
                            onClick = {
                                if (!isDeclarationAccepted) {
                                    coroutineScope.launch {
                                        snackbarHostState.showSnackbar("Please accept the student declaration before submitting.")
                                    }
                                    return@Button
                                }
                                val application = buildApplicationEntity(
                                    studentId = currentStudent?.id ?: 1L,
                                    currentApp = currentApp,
                                    fullName = fullName,
                                    studentIdNumber = studentIdNumber,
                                    email = email,
                                    phone = phone,
                                    faculty = faculty,
                                    programme = programme,
                                    currentYearSemester = currentYearSemester,
                                    cgpa = cgpa,
                                    address = address,
                                    emergencyContactName = emergencyContactName,
                                    emergencyContactPhone = emergencyContactPhone,
                                    emergencyContactRelation = emergencyContactRelation,
                                    primarySector = primarySector,
                                    secondarySector = secondarySector,
                                    preferredRole = preferredRoleDepartment,
                                    preferredLocation = preferredLocation,
                                    preferredWorkMode = preferredWorkMode,
                                    durationWeeks = internshipDurationWeeks,
                                    preferredStartDate = preferredStartDate,
                                    hasOwnLaptop = hasOwnLaptop,
                                    willingnessToRelocate = willingnessToRelocate,
                                    professionalSummary = professionalSummary,
                                    techSkills = selectedTechSkills.joinToString(", "),
                                    softSkills = selectedSoftSkills.joinToString(", "),
                                    educationHistory = educationHistory,
                                    projectExperience = projectExperience,
                                    certifications = certifications,
                                    cvFileName = cvFileName,
                                    cvFileSize = cvFileSize,
                                    cvLastModified = cvLastModified,
                                    portfolioUrl = portfolioUrl,
                                    linkedInUrl = linkedInUrl,
                                    refereeName = refereeName,
                                    refereeTitle = refereeTitle,
                                    refereeContact = refereeContact,
                                    isDeclarationAccepted = isDeclarationAccepted,
                                    isDraft = false
                                )
                                viewModel.submitInternshipApplication(application) {
                                    submittedRefNumber = application.applicationRefNumber
                                    showSuccessDialog = true
                                }
                            },
                            modifier = Modifier
                                .weight(1f)
                                .testTag("btn_submit_application"),
                            shape = RoundedCornerShape(8.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = LeapGoldAccent)
                        ) {
                            Icon(Icons.Default.Send, contentDescription = null, tint = LeapNavyPrimary, modifier = Modifier.size(18.dp))
                            Spacer(modifier = Modifier.width(6.dp))
                            Text("Submit Application", color = LeapNavyPrimary, fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }
        }
    }

    // Success Confirmation Dialog
    if (showSuccessDialog) {
        AlertDialog(
            onDismissRequest = { showSuccessDialog = false },
            icon = {
                Icon(
                    Icons.Default.CheckCircle,
                    contentDescription = null,
                    tint = StatusActive,
                    modifier = Modifier.size(52.dp)
                )
            },
            title = {
                Text(
                    text = "Application Submitted Successfully!",
                    textAlign = TextAlign.Center,
                    fontWeight = FontWeight.Bold,
                    fontSize = 18.sp
                )
            },
            text = {
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    verticalArrangement = Arrangement.spacedBy(8.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = "Your internship application has been officially logged in the Limkokwing LEAP portal.",
                        fontSize = 13.sp,
                        textAlign = TextAlign.Center
                    )
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        colors = CardDefaults.cardColors(containerColor = LeapNavyPrimary.copy(alpha = 0.06f)),
                        shape = RoundedCornerShape(8.dp)
                    ) {
                        Column(modifier = Modifier.padding(12.dp)) {
                            Text(
                                text = "Application Reference: $submittedRefNumber",
                                fontWeight = FontWeight.Bold,
                                fontSize = 13.sp,
                                color = LeapNavyPrimary
                            )
                            Text(
                                text = "Preferred Sector: $primarySector",
                                fontSize = 12.sp,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                            Text(
                                text = "Attached CV: $cvFileName ($cvFileSize)",
                                fontSize = 12.sp,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                            Text(
                                text = "Review Status: Pending Academic Coordinator Review",
                                fontSize = 12.sp,
                                fontWeight = FontWeight.SemiBold,
                                color = LeapBlue
                            )
                        }
                    }
                    Text(
                        text = "You will receive automated in-app notifications when host companies review your application and offer interview slots.",
                        fontSize = 12.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        textAlign = TextAlign.Center
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        showSuccessDialog = false
                        onBack()
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = LeapNavyPrimary)
                ) {
                    Text("Return to Dashboard")
                }
            }
        )
    }

    // CV Preview Dialog
    if (showCvPreviewDialog) {
        AlertDialog(
            onDismissRequest = { showCvPreviewDialog = false },
            title = {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.PictureAsPdf, contentDescription = null, tint = LeapGoldAccent)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(cvFileName, fontSize = 16.sp, fontWeight = FontWeight.Bold)
                }
            },
            text = {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .verticalScroll(rememberScrollState()),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(8.dp),
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
                    ) {
                        Column(modifier = Modifier.padding(12.dp)) {
                            Text(text = fullName, fontWeight = FontWeight.Bold, fontSize = 16.sp, color = LeapNavyPrimary)
                            Text(text = "$programme • $studentIdNumber", fontSize = 12.sp, fontWeight = FontWeight.Medium)
                            Text(text = "$email • $phone • $address", fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                        }
                    }

                    Text("Professional Summary", fontWeight = FontWeight.Bold, fontSize = 13.sp, color = LeapNavyPrimary)
                    Text(professionalSummary, fontSize = 12.sp, lineHeight = 16.sp)

                    HorizontalDivider()

                    Text("Technical & Core Competencies", fontWeight = FontWeight.Bold, fontSize = 13.sp, color = LeapNavyPrimary)
                    Text(selectedTechSkills.joinToString(" • "), fontSize = 12.sp, color = LeapBlue)

                    HorizontalDivider()

                    Text("Education & Academic Background", fontWeight = FontWeight.Bold, fontSize = 13.sp, color = LeapNavyPrimary)
                    Text(educationHistory, fontSize = 12.sp, lineHeight = 16.sp)

                    HorizontalDivider()

                    Text("Selected Projects & Practical Work", fontWeight = FontWeight.Bold, fontSize = 13.sp, color = LeapNavyPrimary)
                    Text(projectExperience, fontSize = 12.sp, lineHeight = 16.sp)

                    HorizontalDivider()

                    Text("Certifications & Referees", fontWeight = FontWeight.Bold, fontSize = 13.sp, color = LeapNavyPrimary)
                    Text("Certifications: $certifications", fontSize = 11.sp)
                    Text("Referee: $refereeName ($refereeTitle) - $refereeContact", fontSize = 11.sp)
                }
            },
            confirmButton = {
                Button(
                    onClick = { showCvPreviewDialog = false },
                    colors = ButtonDefaults.buttonColors(containerColor = LeapNavyPrimary)
                ) {
                    Text("Close Preview")
                }
            }
        )
    }
}

// ----------------------------------------------------
// Step Progress Header
// ----------------------------------------------------
@Composable
fun StepProgressHeader(
    steps: List<String>,
    icons: List<ImageVector>,
    currentStep: Int,
    onStepClick: (Int) -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 4.dp),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
    ) {
        Column(modifier = Modifier.padding(12.dp)) {
            LinearProgressIndicator(
                progress = { (currentStep + 1) / steps.size.toFloat() },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(6.dp)
                    .clip(RoundedCornerShape(3.dp)),
                color = LeapGoldAccent,
                trackColor = MaterialTheme.colorScheme.surfaceVariant
            )

            Spacer(modifier = Modifier.height(10.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                steps.forEachIndexed { index, title ->
                    val isActive = index == currentStep
                    val isCompleted = index < currentStep

                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        modifier = Modifier
                            .weight(1f)
                            .clickable { onStepClick(index) }
                            .padding(vertical = 4.dp)
                    ) {
                        Box(
                            modifier = Modifier
                                .size(32.dp)
                                .clip(CircleShape)
                                .background(
                                    when {
                                        isActive -> LeapNavyPrimary
                                        isCompleted -> StatusActive
                                        else -> MaterialTheme.colorScheme.surfaceVariant
                                    }
                                ),
                            contentAlignment = Alignment.Center
                        ) {
                            if (isCompleted) {
                                Icon(
                                    Icons.Default.Check,
                                    contentDescription = "Completed",
                                    tint = Color.White,
                                    modifier = Modifier.size(18.dp)
                                )
                            } else {
                                Text(
                                    text = "${index + 1}",
                                    color = if (isActive) Color.White else MaterialTheme.colorScheme.onSurfaceVariant,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 13.sp
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(4.dp))

                        Text(
                            text = title,
                            fontSize = 10.sp,
                            fontWeight = if (isActive) FontWeight.Bold else FontWeight.Normal,
                            color = if (isActive) LeapNavyPrimary else MaterialTheme.colorScheme.onSurfaceVariant,
                            maxLines = 1,
                            textAlign = TextAlign.Center
                        )
                    }
                }
            }
        }
    }
}

// ----------------------------------------------------
// Step 1: Student Details Content
// ----------------------------------------------------
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun Step1StudentDetailsContent(
    fullName: String,
    onFullNameChange: (String) -> Unit,
    studentIdNumber: String,
    onStudentIdChange: (String) -> Unit,
    email: String,
    onEmailChange: (String) -> Unit,
    phone: String,
    onPhoneChange: (String) -> Unit,
    faculty: String,
    onFacultyChange: (String) -> Unit,
    programme: String,
    onProgrammeChange: (String) -> Unit,
    currentYearSemester: String,
    onYearSemesterChange: (String) -> Unit,
    cgpa: String,
    onCgpaChange: (String) -> Unit,
    address: String,
    onAddressChange: (String) -> Unit,
    emergencyContactName: String,
    onEmergencyNameChange: (String) -> Unit,
    emergencyContactPhone: String,
    onEmergencyPhoneChange: (String) -> Unit,
    emergencyContactRelation: String,
    onEmergencyRelationChange: (String) -> Unit
) {
    val faculties = listOf(
        "Faculty of Information & Communication Technology",
        "Faculty of Business & Globalisation",
        "Faculty of Architecture & Built Environment",
        "Faculty of Design Innovation",
        "Faculty of Communication, Media & Broadcasting"
    )

    val programmes = listOf(
        "BSc (Hons) in Information Technology",
        "BSc (Hons) in Software Engineering",
        "BSc (Hons) in Multimedia & Digital Media",
        "BSc (Hons) in Telecommunications & Networks",
        "BSc (Hons) in Information Systems",
        "BSc (Hons) in Cybersecurity & Digital Forensics",
        "BSc (Hons) in Cloud Computing & Big Data"
    )

    var facultyExpanded by remember { mutableStateOf(false) }
    var programmeExpanded by remember { mutableStateOf(false) }

    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(Icons.Default.School, contentDescription = null, tint = LeapGoldAccent)
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "Step 1: Student & Academic Profile",
                    fontWeight = FontWeight.Bold,
                    fontSize = 16.sp,
                    color = LeapNavyPrimary
                )
            }
            Text(
                text = "Please verify your personal contact info and academic registration standing at Limkokwing University.",
                fontSize = 12.sp,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            HorizontalDivider(modifier = Modifier.padding(vertical = 4.dp))

            // Full Name & Student ID
            OutlinedTextField(
                value = fullName,
                onValueChange = onFullNameChange,
                label = { Text("Full Legal Name *") },
                leadingIcon = { Icon(Icons.Default.Person, contentDescription = null) },
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("input_full_name"),
                singleLine = true
            )

            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                OutlinedTextField(
                    value = studentIdNumber,
                    onValueChange = onStudentIdChange,
                    label = { Text("Student ID Code *") },
                    leadingIcon = { Icon(Icons.Default.Badge, contentDescription = null) },
                    modifier = Modifier
                        .weight(1f)
                        .testTag("input_student_id"),
                    singleLine = true
                )

                OutlinedTextField(
                    value = cgpa,
                    onValueChange = onCgpaChange,
                    label = { Text("CGPA (Out of 4.0) *") },
                    leadingIcon = { Icon(Icons.Default.Grade, contentDescription = null) },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                    modifier = Modifier
                        .weight(1f)
                        .testTag("input_cgpa"),
                    singleLine = true
                )
            }

            // Email & Phone
            OutlinedTextField(
                value = email,
                onValueChange = onEmailChange,
                label = { Text("University / Contact Email *") },
                leadingIcon = { Icon(Icons.Default.Email, contentDescription = null) },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("input_email"),
                singleLine = true
            )

            OutlinedTextField(
                value = phone,
                onValueChange = onPhoneChange,
                label = { Text("Phone Number (+232) *") },
                leadingIcon = { Icon(Icons.Default.Phone, contentDescription = null) },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone),
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("input_phone"),
                singleLine = true
            )

            OutlinedTextField(
                value = address,
                onValueChange = onAddressChange,
                label = { Text("Residential Address (City/District) *") },
                leadingIcon = { Icon(Icons.Default.Home, contentDescription = null) },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true
            )

            // Faculty Dropdown
            ExposedDropdownMenuBox(
                expanded = facultyExpanded,
                onExpandedChange = { facultyExpanded = !facultyExpanded }
            ) {
                OutlinedTextField(
                    value = faculty,
                    onValueChange = {},
                    readOnly = true,
                    label = { Text("Faculty / Directorate *") },
                    trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = facultyExpanded) },
                    modifier = Modifier
                        .fillMaxWidth()
                        .menuAnchor()
                )
                ExposedDropdownMenu(
                    expanded = facultyExpanded,
                    onDismissRequest = { facultyExpanded = false }
                ) {
                    faculties.forEach { item ->
                        DropdownMenuItem(
                            text = { Text(item, fontSize = 13.sp) },
                            onClick = {
                                onFacultyChange(item)
                                facultyExpanded = false
                            }
                        )
                    }
                }
            }

            // Degree Programme Dropdown
            ExposedDropdownMenuBox(
                expanded = programmeExpanded,
                onExpandedChange = { programmeExpanded = !programmeExpanded }
            ) {
                OutlinedTextField(
                    value = programme,
                    onValueChange = {},
                    readOnly = true,
                    label = { Text("Degree Programme *") },
                    trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = programmeExpanded) },
                    modifier = Modifier
                        .fillMaxWidth()
                        .menuAnchor()
                )
                ExposedDropdownMenu(
                    expanded = programmeExpanded,
                    onDismissRequest = { programmeExpanded = false }
                ) {
                    programmes.forEach { item ->
                        DropdownMenuItem(
                            text = { Text(item, fontSize = 13.sp) },
                            onClick = {
                                onProgrammeChange(item)
                                programmeExpanded = false
                            }
                        )
                    }
                }
            }

            OutlinedTextField(
                value = currentYearSemester,
                onValueChange = onYearSemesterChange,
                label = { Text("Current Academic Level *") },
                leadingIcon = { Icon(Icons.AutoMirrored.Filled.MenuBook, contentDescription = null) },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true
            )

            HorizontalDivider(modifier = Modifier.padding(vertical = 4.dp))

            // Emergency Contact Section
            Text(
                text = "Emergency Contact Person",
                fontWeight = FontWeight.SemiBold,
                fontSize = 14.sp,
                color = LeapNavyPrimary
            )

            OutlinedTextField(
                value = emergencyContactName,
                onValueChange = onEmergencyNameChange,
                label = { Text("Next of Kin / Contact Name") },
                leadingIcon = { Icon(Icons.Default.Contacts, contentDescription = null) },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true
            )

            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                OutlinedTextField(
                    value = emergencyContactPhone,
                    onValueChange = onEmergencyPhoneChange,
                    label = { Text("Contact Phone") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone),
                    modifier = Modifier.weight(1f),
                    singleLine = true
                )
                OutlinedTextField(
                    value = emergencyContactRelation,
                    onValueChange = onEmergencyRelationChange,
                    label = { Text("Relationship") },
                    modifier = Modifier.weight(1f),
                    singleLine = true
                )
            }
        }
    }
}

// ----------------------------------------------------
// Step 2: Industry Sector Content
// ----------------------------------------------------
@OptIn(ExperimentalLayoutApi::class, ExperimentalMaterial3Api::class)
@Composable
fun Step2IndustrySectorContent(
    primarySector: String,
    onPrimarySectorChange: (String) -> Unit,
    secondarySector: String,
    onSecondarySectorChange: (String) -> Unit,
    preferredRole: String,
    onPreferredRoleChange: (String) -> Unit,
    preferredLocation: String,
    onPreferredLocationChange: (String) -> Unit,
    preferredWorkMode: String,
    onPreferredWorkModeChange: (String) -> Unit,
    preferredStartDate: String,
    onPreferredStartDateChange: (String) -> Unit,
    hasOwnLaptop: Boolean,
    onHasOwnLaptopChange: (Boolean) -> Unit,
    willingnessToRelocate: Boolean,
    onWillingnessToRelocateChange: (Boolean) -> Unit
) {
    val industrySectors = listOf(
        Triple("Software & Cloud Engineering", Icons.Default.Code, "Enterprise software, mobile apps, SaaS & cloud platforms"),
        Triple("Telecommunications & Digital Services", Icons.Default.CellTower, "Orange, Africell, NOC fiber networks & mobile money VAS"),
        Triple("Banking & Financial Technology", Icons.Default.AccountBalance, "Digital banking channels, core payment gateways & fintech"),
        Triple("Cybersecurity & Network Defense", Icons.Default.Security, "Information security audits, penetration testing & data safety"),
        Triple("Data Analytics & Artificial Intelligence", Icons.Default.Analytics, "Data pipelines, predictive models, BI dashboards & AI tools"),
        Triple("Creative Multimedia & UX Design", Icons.Default.Palette, "UI/UX prototypes, brand design, video editing & graphics"),
        Triple("Public Sector & E-Governance", Icons.Default.Domain, "NRA, NCRA, Ministry of Tech digital government systems")
    )

    val locations = listOf(
        "Freetown Central",
        "Wilberforce / West End",
        "Lumley & Goderich",
        "Waterloo / Western Rural",
        "Bo City",
        "Kenema",
        "Makeni",
        "Hybrid / Remote Placement"
    )

    val workModes = listOf("On-site", "Hybrid", "Remote")

    var locationExpanded by remember { mutableStateOf(false) }

    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(Icons.Default.BusinessCenter, contentDescription = null, tint = LeapGoldAccent)
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "Step 2: Preferred Industry Sector & Placement",
                    fontWeight = FontWeight.Bold,
                    fontSize = 16.sp,
                    color = LeapNavyPrimary
                )
            }
            Text(
                text = "Select your target industrial sectors and work logistics so LEAP matching algorithms can pair you with verified host organizations.",
                fontSize = 12.sp,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            HorizontalDivider(modifier = Modifier.padding(vertical = 4.dp))

            Text(
                text = "Primary Industry Sector *",
                fontWeight = FontWeight.SemiBold,
                fontSize = 14.sp,
                color = LeapNavyPrimary
            )

            // Sector Cards
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                industrySectors.forEach { (sectorTitle, icon, desc) ->
                    val isSelected = primarySector == sectorTitle
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { onPrimarySectorChange(sectorTitle) }
                            .border(
                                width = if (isSelected) 2.dp else 1.dp,
                                color = if (isSelected) LeapGoldAccent else MaterialTheme.colorScheme.outlineVariant,
                                shape = RoundedCornerShape(10.dp)
                            ),
                        shape = RoundedCornerShape(10.dp),
                        colors = CardDefaults.cardColors(
                            containerColor = if (isSelected) LeapGoldAccent.copy(alpha = 0.12f) else MaterialTheme.colorScheme.surface
                        )
                    ) {
                        Row(
                            modifier = Modifier.padding(12.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(36.dp)
                                    .clip(CircleShape)
                                    .background(if (isSelected) LeapNavyPrimary else MaterialTheme.colorScheme.surfaceVariant),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    icon,
                                    contentDescription = null,
                                    tint = if (isSelected) LeapGoldAccent else MaterialTheme.colorScheme.onSurfaceVariant,
                                    modifier = Modifier.size(20.dp)
                                )
                            }
                            Spacer(modifier = Modifier.width(12.dp))
                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    text = sectorTitle,
                                    fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                                    fontSize = 13.sp,
                                    color = if (isSelected) LeapNavyPrimary else MaterialTheme.colorScheme.onSurface
                                )
                                Text(
                                    text = desc,
                                    fontSize = 11.sp,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                                    lineHeight = 14.sp
                                )
                            }
                            RadioButton(
                                selected = isSelected,
                                onClick = { onPrimarySectorChange(sectorTitle) },
                                colors = RadioButtonDefaults.colors(selectedColor = LeapNavyPrimary)
                            )
                        }
                    }
                }
            }

            HorizontalDivider(modifier = Modifier.padding(vertical = 4.dp))

            // Preferred Role / Specialization
            OutlinedTextField(
                value = preferredRole,
                onValueChange = onPreferredRoleChange,
                label = { Text("Target Department / Role Specialization *") },
                placeholder = { Text("e.g. Mobile Developer, NOC Engineer, Core Banking Support") },
                leadingIcon = { Icon(Icons.Default.Work, contentDescription = null) },
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("input_preferred_role"),
                singleLine = true
            )

            // Preferred Location Dropdown
            ExposedDropdownMenuBox(
                expanded = locationExpanded,
                onExpandedChange = { locationExpanded = !locationExpanded }
            ) {
                OutlinedTextField(
                    value = preferredLocation,
                    onValueChange = {},
                    readOnly = true,
                    label = { Text("Preferred Placement Location *") },
                    leadingIcon = { Icon(Icons.Default.LocationOn, contentDescription = null) },
                    trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = locationExpanded) },
                    modifier = Modifier
                        .fillMaxWidth()
                        .menuAnchor()
                )
                ExposedDropdownMenu(
                    expanded = locationExpanded,
                    onDismissRequest = { locationExpanded = false }
                ) {
                    locations.forEach { loc ->
                        DropdownMenuItem(
                            text = { Text(loc, fontSize = 13.sp) },
                            onClick = {
                                onPreferredLocationChange(loc)
                                locationExpanded = false
                            }
                        )
                    }
                }
            }

            // Work Mode Selection (Chips)
            Text(
                text = "Work Arrangement Preference",
                fontWeight = FontWeight.SemiBold,
                fontSize = 13.sp,
                color = LeapNavyPrimary
            )
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                workModes.forEach { mode ->
                    val isSelected = preferredWorkMode == mode
                    FilterChip(
                        selected = isSelected,
                        onClick = { onPreferredWorkModeChange(mode) },
                        label = { Text(mode, fontSize = 12.sp) },
                        leadingIcon = if (isSelected) {
                            { Icon(Icons.Default.Check, contentDescription = null, modifier = Modifier.size(16.dp)) }
                        } else null,
                        colors = FilterChipDefaults.filterChipColors(
                            selectedContainerColor = LeapNavyPrimary,
                            selectedLabelColor = Color.White
                        )
                    )
                }
            }

            // Target Start Date
            OutlinedTextField(
                value = preferredStartDate,
                onValueChange = onPreferredStartDateChange,
                label = { Text("Earliest Available Commencement Date") },
                leadingIcon = { Icon(Icons.Default.CalendarToday, contentDescription = null) },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true
            )

            HorizontalDivider(modifier = Modifier.padding(vertical = 4.dp))

            // Logistics Switches
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(text = "Personal Working Laptop Available", fontSize = 13.sp, fontWeight = FontWeight.Medium)
                    Text(text = "Required for software and remote placements", fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                }
                Switch(
                    checked = hasOwnLaptop,
                    onCheckedChange = onHasOwnLaptopChange,
                    colors = SwitchDefaults.colors(checkedThumbColor = LeapGoldAccent, checkedTrackColor = LeapNavyPrimary)
                )
            }

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(text = "Willing to Relocate / Travel Outside Freetown", fontSize = 13.sp, fontWeight = FontWeight.Medium)
                    Text(text = "For provincial branch sites (Bo, Kenema, Makeni)", fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                }
                Switch(
                    checked = willingnessToRelocate,
                    onCheckedChange = onWillingnessToRelocateChange,
                    colors = SwitchDefaults.colors(checkedThumbColor = LeapGoldAccent, checkedTrackColor = LeapNavyPrimary)
                )
            }
        }
    }
}

// ----------------------------------------------------
// Step 3: CV Information Content
// ----------------------------------------------------
@OptIn(ExperimentalLayoutApi::class)
@Composable
fun Step3CvInformationContent(
    professionalSummary: String,
    onProfessionalSummaryChange: (String) -> Unit,
    selectedTechSkills: List<String>,
    onAddTechSkill: (String) -> Unit,
    onRemoveTechSkill: (String) -> Unit,
    customSkillInput: String,
    onCustomSkillInputChange: (String) -> Unit,
    selectedSoftSkills: List<String>,
    onAddSoftSkill: (String) -> Unit,
    onRemoveSoftSkill: (String) -> Unit,
    educationHistory: String,
    onEducationHistoryChange: (String) -> Unit,
    projectExperience: String,
    onProjectExperienceChange: (String) -> Unit,
    certifications: String,
    onCertificationsChange: (String) -> Unit,
    cvFileName: String,
    cvFileSize: String,
    cvLastModified: String,
    onUploadCv: () -> Unit,
    onPreviewCv: () -> Unit,
    portfolioUrl: String,
    onPortfolioUrlChange: (String) -> Unit,
    linkedInUrl: String,
    onLinkedInUrlChange: (String) -> Unit,
    refereeName: String,
    onRefereeNameChange: (String) -> Unit,
    refereeTitle: String,
    onRefereeTitleChange: (String) -> Unit,
    refereeContact: String,
    onRefereeContactChange: (String) -> Unit
) {
    val commonTechSkills = listOf(
        "Kotlin", "Jetpack Compose", "Java", "Python", "SQL / Room DB", "Git / GitHub",
        "REST APIs", "Docker", "Linux / Bash", "React.js", "Node.js", "CCNA Networking",
        "Figma UI/UX", "Cyber Defense", "Spring Boot", "HTML / CSS", "TypeScript"
    )

    val commonSoftSkills = listOf(
        "Problem Solving", "Team Leadership", "Agile Collaboration", "Technical Writing",
        "Fast Learner", "Critical Thinking", "Communication", "Time Management"
    )

    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(Icons.Default.ContactPage, contentDescription = null, tint = LeapGoldAccent)
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "Step 3: CV Information & Credentials",
                    fontWeight = FontWeight.Bold,
                    fontSize = 16.sp,
                    color = LeapNavyPrimary
                )
            }
            Text(
                text = "Structure your digital curriculum vitae profile and attach your latest PDF resume for employer review.",
                fontSize = 12.sp,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            // CV Document Attachment Box
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(10.dp),
                colors = CardDefaults.cardColors(containerColor = LeapNavyPrimary.copy(alpha = 0.05f)),
                border = androidx.compose.foundation.BorderStroke(1.dp, LeapNavyPrimary.copy(alpha = 0.2f))
            ) {
                Row(
                    modifier = Modifier.padding(12.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier
                            .size(44.dp)
                            .clip(RoundedCornerShape(8.dp))
                            .background(StatusAlert.copy(alpha = 0.12f)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            Icons.Default.PictureAsPdf,
                            contentDescription = "PDF",
                            tint = StatusAlert,
                            modifier = Modifier.size(26.dp)
                        )
                    }
                    Spacer(modifier = Modifier.width(12.dp))
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = cvFileName,
                            fontWeight = FontWeight.Bold,
                            fontSize = 13.sp,
                            color = LeapNavyPrimary
                        )
                        Text(
                            text = "Size: $cvFileSize • Updated: $cvLastModified",
                            fontSize = 11.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                    Row {
                        IconButton(onClick = onPreviewCv) {
                            Icon(Icons.Default.Visibility, contentDescription = "Preview", tint = LeapNavyPrimary)
                        }
                        IconButton(onClick = onUploadCv) {
                            Icon(Icons.Default.UploadFile, contentDescription = "Upload New", tint = LeapGoldAccent)
                        }
                    }
                }
            }

            HorizontalDivider()

            // Professional Summary
            Text(
                text = "Professional Executive Summary *",
                fontWeight = FontWeight.SemiBold,
                fontSize = 13.sp,
                color = LeapNavyPrimary
            )
            OutlinedTextField(
                value = professionalSummary,
                onValueChange = onProfessionalSummaryChange,
                placeholder = { Text("Briefly describe your career objectives, technical focus, and value proposition...") },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(110.dp)
                    .testTag("input_summary"),
                maxLines = 4
            )

            HorizontalDivider()

            // Technical Skills Builder
            Text(
                text = "Technical Competencies & Tools *",
                fontWeight = FontWeight.SemiBold,
                fontSize = 13.sp,
                color = LeapNavyPrimary
            )

            // Quick add popular chips
            Text(
                text = "Tap to add / remove common technical skills:",
                fontSize = 11.sp,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            FlowRow(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(6.dp),
                verticalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                commonTechSkills.forEach { skill ->
                    val isSelected = selectedTechSkills.contains(skill)
                    FilterChip(
                        selected = isSelected,
                        onClick = {
                            if (isSelected) onRemoveTechSkill(skill) else onAddTechSkill(skill)
                        },
                        label = { Text(skill, fontSize = 11.sp) },
                        leadingIcon = if (isSelected) {
                            { Icon(Icons.Default.Check, contentDescription = null, modifier = Modifier.size(14.dp)) }
                        } else null,
                        colors = FilterChipDefaults.filterChipColors(
                            selectedContainerColor = LeapNavyPrimary,
                            selectedLabelColor = Color.White
                        )
                    )
                }
            }

            // Custom Skill Input
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                OutlinedTextField(
                    value = customSkillInput,
                    onValueChange = onCustomSkillInputChange,
                    label = { Text("Add Custom Skill") },
                    placeholder = { Text("e.g. Flutter, Kubernetes") },
                    modifier = Modifier.weight(1f),
                    singleLine = true
                )
                Button(
                    onClick = {
                        if (customSkillInput.isNotBlank()) {
                            onAddTechSkill(customSkillInput)
                            onCustomSkillInputChange("")
                        }
                    },
                    shape = RoundedCornerShape(8.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = LeapNavyPrimary)
                ) {
                    Text("Add")
                }
            }

            HorizontalDivider()

            // Soft Skills Builder
            Text(
                text = "Soft Skills & Workplace Attributes",
                fontWeight = FontWeight.SemiBold,
                fontSize = 13.sp,
                color = LeapNavyPrimary
            )
            FlowRow(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(6.dp),
                verticalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                commonSoftSkills.forEach { skill ->
                    val isSelected = selectedSoftSkills.contains(skill)
                    FilterChip(
                        selected = isSelected,
                        onClick = {
                            if (isSelected) onRemoveSoftSkill(skill) else onAddSoftSkill(skill)
                        },
                        label = { Text(skill, fontSize = 11.sp) },
                        colors = FilterChipDefaults.filterChipColors(
                            selectedContainerColor = LeapGoldAccent,
                            selectedLabelColor = LeapNavyPrimary
                        )
                    )
                }
            }

            HorizontalDivider()

            // Education & Prior Experience
            OutlinedTextField(
                value = educationHistory,
                onValueChange = onEducationHistoryChange,
                label = { Text("Education History & Institutions") },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(90.dp)
            )

            OutlinedTextField(
                value = projectExperience,
                onValueChange = onProjectExperienceChange,
                label = { Text("Key Projects & Portfolio Practical Work") },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(100.dp)
            )

            OutlinedTextField(
                value = certifications,
                onValueChange = onCertificationsChange,
                label = { Text("Certifications & Professional Badges") },
                modifier = Modifier.fillMaxWidth()
            )

            HorizontalDivider()

            // Online Links & Portfolio
            Text(
                text = "Online Portfolio & Social Links",
                fontWeight = FontWeight.SemiBold,
                fontSize = 13.sp,
                color = LeapNavyPrimary
            )
            OutlinedTextField(
                value = portfolioUrl,
                onValueChange = onPortfolioUrlChange,
                label = { Text("GitHub / Portfolio URL") },
                leadingIcon = { Icon(Icons.Default.Language, contentDescription = null) },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true
            )

            OutlinedTextField(
                value = linkedInUrl,
                onValueChange = onLinkedInUrlChange,
                label = { Text("LinkedIn Profile URL") },
                leadingIcon = { Icon(Icons.Default.Link, contentDescription = null) },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true
            )

            HorizontalDivider()

            // Referee Information
            Text(
                text = "Academic / Character Referee",
                fontWeight = FontWeight.SemiBold,
                fontSize = 13.sp,
                color = LeapNavyPrimary
            )
            OutlinedTextField(
                value = refereeName,
                onValueChange = onRefereeNameChange,
                label = { Text("Referee Name & Title") },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true
            )
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                OutlinedTextField(
                    value = refereeTitle,
                    onValueChange = onRefereeTitleChange,
                    label = { Text("Department / Institution") },
                    modifier = Modifier.weight(1f),
                    singleLine = true
                )
                OutlinedTextField(
                    value = refereeContact,
                    onValueChange = onRefereeContactChange,
                    label = { Text("Email / Contact") },
                    modifier = Modifier.weight(1f),
                    singleLine = true
                )
            }
        }
    }
}

// ----------------------------------------------------
// Step 4: Review & Submit Content
// ----------------------------------------------------
@OptIn(ExperimentalLayoutApi::class)
@Composable
fun Step4ReviewAndSubmitContent(
    fullName: String,
    studentIdNumber: String,
    email: String,
    phone: String,
    programme: String,
    cgpa: String,
    primarySector: String,
    secondarySector: String,
    preferredRole: String,
    preferredLocation: String,
    preferredWorkMode: String,
    cvFileName: String,
    cvFileSize: String,
    selectedTechSkills: List<String>,
    isDeclarationAccepted: Boolean,
    onDeclarationChange: (Boolean) -> Unit,
    onPreviewCv: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(Icons.Default.VerifiedUser, contentDescription = null, tint = StatusActive)
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "Step 4: Application Summary & Verification",
                    fontWeight = FontWeight.Bold,
                    fontSize = 16.sp,
                    color = LeapNavyPrimary
                )
            }
            Text(
                text = "Review all captured application and CV details prior to final digital submission to the LEAP Industrial Directorate.",
                fontSize = 12.sp,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            // AI Match Readiness Banner
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(10.dp),
                colors = CardDefaults.cardColors(containerColor = LeapGoldAccent.copy(alpha = 0.15f))
            ) {
                Row(
                    modifier = Modifier.padding(12.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(Icons.Default.AutoAwesome, contentDescription = null, tint = LeapNavyPrimary, modifier = Modifier.size(24.dp))
                    Spacer(modifier = Modifier.width(10.dp))
                    Column {
                        Text(
                            text = "AI Match Readiness: 98% Profile Match",
                            fontWeight = FontWeight.Bold,
                            fontSize = 13.sp,
                            color = LeapNavyPrimary
                        )
                        Text(
                            text = "Profile perfectly aligned with host openings at Tech Solutions SL, Orange SL, and SLCB Fintech.",
                            fontSize = 11.sp,
                            color = LeapNavyPrimary.copy(alpha = 0.85f)
                        )
                    }
                }
            }

            HorizontalDivider()

            // 1. Personal & Academic Summary
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(8.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f))
            ) {
                Column(modifier = Modifier.padding(12.dp), verticalArrangement = Arrangement.spacedBy(4.dp)) {
                    Text(text = "1. Student Profile", fontWeight = FontWeight.Bold, fontSize = 13.sp, color = LeapNavyPrimary)
                    Text(text = "• Name: $fullName", fontSize = 12.sp)
                    Text(text = "• Student Code: $studentIdNumber", fontSize = 12.sp)
                    Text(text = "• Degree: $programme (CGPA: $cgpa)", fontSize = 12.sp)
                    Text(text = "• Contact: $email | $phone", fontSize = 12.sp)
                }
            }

            // 2. Placement Preferences Summary
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(8.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f))
            ) {
                Column(modifier = Modifier.padding(12.dp), verticalArrangement = Arrangement.spacedBy(4.dp)) {
                    Text(text = "2. Sector & Placement Preferences", fontWeight = FontWeight.Bold, fontSize = 13.sp, color = LeapNavyPrimary)
                    Text(text = "• Primary Sector: $primarySector", fontSize = 12.sp, fontWeight = FontWeight.SemiBold, color = LeapNavyPrimary)
                    Text(text = "• Target Department: $preferredRole", fontSize = 12.sp)
                    Text(text = "• Location & Mode: $preferredLocation ($preferredWorkMode)", fontSize = 12.sp)
                }
            }

            // 3. CV & Skill Summary
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(8.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f))
            ) {
                Column(modifier = Modifier.padding(12.dp), verticalArrangement = Arrangement.spacedBy(6.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(text = "3. CV & Technical Profile", fontWeight = FontWeight.Bold, fontSize = 13.sp, color = LeapNavyPrimary)
                        TextButton(onClick = onPreviewCv, contentPadding = PaddingValues(0.dp)) {
                            Text("Preview CV", fontSize = 11.sp, color = LeapBlue)
                        }
                    }
                    Text(text = "• Attached PDF: $cvFileName ($cvFileSize)", fontSize = 12.sp)

                    Text(text = "• Key Skills (${selectedTechSkills.size}):", fontSize = 12.sp, fontWeight = FontWeight.Medium)
                    FlowRow(
                        horizontalArrangement = Arrangement.spacedBy(4.dp),
                        verticalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        selectedTechSkills.take(8).forEach { skill ->
                            AssistChip(
                                onClick = {},
                                label = { Text(skill, fontSize = 10.sp) },
                                modifier = Modifier.height(26.dp)
                            )
                        }
                        if (selectedTechSkills.size > 8) {
                            AssistChip(
                                onClick = {},
                                label = { Text("+${selectedTechSkills.size - 8} more", fontSize = 10.sp) },
                                modifier = Modifier.height(26.dp)
                            )
                        }
                    }
                }
            }

            HorizontalDivider()

            // Declaration Checkbox
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { onDeclarationChange(!isDeclarationAccepted) }
                    .padding(vertical = 4.dp),
                verticalAlignment = Alignment.Top
            ) {
                Checkbox(
                    checked = isDeclarationAccepted,
                    onCheckedChange = onDeclarationChange,
                    colors = CheckboxDefaults.colors(checkedColor = LeapNavyPrimary),
                    modifier = Modifier.testTag("checkbox_declaration")
                )
                Spacer(modifier = Modifier.width(8.dp))
                Column {
                    Text(
                        text = "Official LEAP Student Declaration *",
                        fontWeight = FontWeight.Bold,
                        fontSize = 13.sp,
                        color = LeapNavyPrimary
                    )
                    Text(
                        text = "I hereby declare that all academic credentials, contact info, preferred industry sectors, and attached CV documents are truthful and accurate. I commit to abiding strictly by the Limkokwing LEAP Code of Conduct during internship placement.",
                        fontSize = 11.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        lineHeight = 15.sp
                    )
                }
            }
        }
    }
}

// ----------------------------------------------------
// Helper Validation & Factory Functions
// ----------------------------------------------------
private fun validateCurrentStep(
    step: Int,
    fullName: String,
    studentId: String,
    email: String,
    phone: String,
    cgpa: String,
    sector: String,
    techSkills: List<String>
): Boolean {
    return when (step) {
        0 -> fullName.isNotBlank() && studentId.isNotBlank() && email.isNotBlank() && phone.isNotBlank() && cgpa.isNotBlank()
        1 -> sector.isNotBlank()
        2 -> techSkills.isNotEmpty()
        else -> true
    }
}

private fun buildApplicationEntity(
    studentId: Long,
    currentApp: InternshipApplicationEntity?,
    fullName: String,
    studentIdNumber: String,
    email: String,
    phone: String,
    faculty: String,
    programme: String,
    currentYearSemester: String,
    cgpa: String,
    address: String,
    emergencyContactName: String,
    emergencyContactPhone: String,
    emergencyContactRelation: String,
    primarySector: String,
    secondarySector: String,
    preferredRole: String,
    preferredLocation: String,
    preferredWorkMode: String,
    durationWeeks: Int,
    preferredStartDate: String,
    hasOwnLaptop: Boolean,
    willingnessToRelocate: Boolean,
    professionalSummary: String,
    techSkills: String,
    softSkills: String,
    educationHistory: String,
    projectExperience: String,
    certifications: String,
    cvFileName: String,
    cvFileSize: String,
    cvLastModified: String,
    portfolioUrl: String,
    linkedInUrl: String,
    refereeName: String,
    refereeTitle: String,
    refereeContact: String,
    isDeclarationAccepted: Boolean,
    isDraft: Boolean
): InternshipApplicationEntity {
    val existingId = currentApp?.id ?: 0L
    val existingRef = currentApp?.applicationRefNumber ?: "APP-LEAP-2026-${(1000..9999).random()}"

    return InternshipApplicationEntity(
        id = existingId,
        studentId = studentId,
        applicationRefNumber = existingRef,
        fullName = fullName,
        studentIdNumber = studentIdNumber,
        email = email,
        phone = phone,
        faculty = faculty,
        programme = programme,
        currentYearSemester = currentYearSemester,
        cgpa = cgpa,
        address = address,
        emergencyContactName = emergencyContactName,
        emergencyContactPhone = emergencyContactPhone,
        emergencyContactRelation = emergencyContactRelation,
        primarySector = primarySector,
        secondarySector = secondarySector,
        preferredRoleDepartment = preferredRole,
        preferredLocation = preferredLocation,
        preferredWorkMode = preferredWorkMode,
        internshipDurationWeeks = durationWeeks,
        preferredStartDate = preferredStartDate,
        hasOwnLaptop = hasOwnLaptop,
        willingnessToRelocate = willingnessToRelocate,
        professionalSummary = professionalSummary,
        technicalSkills = techSkills,
        softSkills = softSkills,
        educationHistory = educationHistory,
        projectExperience = projectExperience,
        certifications = certifications,
        cvFileName = cvFileName,
        cvFileSize = cvFileSize,
        cvLastModified = cvLastModified,
        portfolioOrGithubUrl = portfolioUrl,
        linkedInUrl = linkedInUrl,
        academicRefereeName = refereeName,
        academicRefereeTitle = refereeTitle,
        academicRefereeContact = refereeContact,
        isDeclarationAccepted = isDeclarationAccepted,
        submissionStatus = if (isDraft) "Draft" else "Submitted",
        submittedDate = if (isDraft) "" else java.text.SimpleDateFormat("dd-MMM-yyyy", java.util.Locale.getDefault()).format(java.util.Date()),
        timestamp = System.currentTimeMillis()
    )
}
