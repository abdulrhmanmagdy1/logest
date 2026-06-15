# 📄 Document Management System - دليل شامل

## 🎯 نظرة عامة

نظام متكامل لإنشاء، تخزين، توقيع وإدارة المستندات داخل تطبيق إدهام.

---

## 📊 الحالة الحالية

```
✅ الموجود:
└─ DocumentsManagementScreen.kt (UI فقط)

❌ الناقص (95%):
├─ Document creation engine
├─ PDF generation
├─ Digital signature
├─ Document templates
├─ Version control
├─ Document search
├─ OCR processing
├─ Document sharing
└─ Compliance tracking
```

---

## 🏗️ Architecture

```
┌─────────────────────────────────────────┐
│      📱 Frontend (Android)              │
│  DocumentManagementScreen.kt            │
│  DocumentCreatorScreen.kt               │
│  DocumentViewerScreen.kt                │
│  DocumentSignerScreen.kt                │
└─────────────────────┬───────────────────┘
                      │
┌─────────────────────▼───────────────────┐
│      🔄 ViewModel & Domain Layer        │
│  DocumentViewModel.kt                   │
│  DocumentRepository.kt                  │
│  DocumentUseCase.kt                     │
└─────────────────────┬───────────────────┘
                      │
┌─────────────────────▼───────────────────┐
│      💾 Data Layer                      │
│  API Service                            │
│  Local Database                         │
│  File Storage                           │
└─────────────────────┬───────────────────┘
                      │
┌─────────────────────▼───────────────────┐
│      🌐 Backend (Node.js)               │
│  documentController.js                  │
│  documentService.js                     │
│  pdfService.js                          │
│  signatureService.js                    │
└─────────────────────┬───────────────────┘
                      │
┌─────────────────────▼───────────────────┐
│      💾 Database & Storage              │
│  MongoDB (metadata)                     │
│  S3/Cloud Storage (files)               │
│  Redis (cache)                          │
└─────────────────────────────────────────┘
```

---

## 📱 Android Frontend Implementation

### **1️⃣ Data Models**

```kotlin
// data/model/Document.kt
data class Document(
    val id: String,
    val title: String,
    val type: DocumentType,
    val template: DocumentTemplate?,
    val content: String,
    val metadata: DocumentMetadata,
    val status: DocumentStatus,
    val signatures: List<Signature>,
    val versions: List<DocumentVersion>,
    val createdBy: String,
    val createdAt: LocalDateTime,
    val updatedAt: LocalDateTime,
    val fileUrl: String?,
    val tags: List<String>
)

enum class DocumentType {
    INVOICE,           // الفاتورة
    DELIVERY_NOTE,     // وثيقة التسليم
    RECEIPT,          // إيصال
    CONTRACT,         // عقد
    REPORT,           // تقرير
    SHIPMENT_LABEL,   // ملصق شحنة
    CUSTOMS,          // وثائق جمركية
    INSURANCE,        // وثائق تأمين
    OTHER
}

data class DocumentTemplate(
    val id: String,
    val name: String,
    val description: String,
    val fields: List<TemplateField>,
    val headerImageUrl: String?,
    val footerText: String?,
    val companyInfo: CompanyInfo?
)

data class TemplateField(
    val id: String,
    val name: String,
    val label: String,
    val type: FieldType,  // TEXT, NUMBER, DATE, DROPDOWN, SIGNATURE
    val required: Boolean,
    val placeholder: String?,
    val options: List<String>?,
    val validation: ValidationRule?
)

enum class FieldType {
    TEXT, NUMBER, DATE, EMAIL, PHONE, DROPDOWN, TEXTAREA, SIGNATURE, IMAGE, FILE
}

data class DocumentMetadata(
    val description: String?,
    val category: String?,
    val shipmentId: String?,
    val customerId: String?,
    val driverId: String?,
    val fiscalNumber: String?,
    val legalNotes: String?
)

enum class DocumentStatus {
    DRAFT,           // مسودة
    IN_PROGRESS,     // قيد الإعداد
    PENDING_REVIEW,  // في انتظار المراجعة
    PENDING_SIGN,    // في انتظار التوقيع
    SIGNED,          // موقعة
    APPROVED,        // موافق عليها
    REJECTED,        // مرفوضة
    ARCHIVED         // مؤرشفة
}

data class Signature(
    val id: String,
    val userId: String,
    val userName: String,
    val signatureImage: ByteArray?,  // صورة التوقيع
    val signatureData: String?,       // البيانات الرقمية
    val timestamp: LocalDateTime,
    val ipAddress: String?,
    val deviceInfo: String?
)

data class DocumentVersion(
    val versionNumber: Int,
    val content: String,
    val createdBy: String,
    val createdAt: LocalDateTime,
    val changeLog: String?
)
```

### **2️⃣ API Service**

```kotlin
// data/network/DocumentApiService.kt
interface DocumentApiService {
    
    // Create Document
    @POST("documents/create")
    suspend fun createDocument(
        @Body request: CreateDocumentRequest
    ): ApiResponse<Document>
    
    // Get Document
    @GET("documents/{id}")
    suspend fun getDocument(
        @Path("id") documentId: String
    ): ApiResponse<Document>
    
    // Update Document
    @PUT("documents/{id}")
    suspend fun updateDocument(
        @Path("id") documentId: String,
        @Body request: UpdateDocumentRequest
    ): ApiResponse<Document>
    
    // Generate PDF
    @POST("documents/{id}/generate-pdf")
    suspend fun generatePDF(
        @Path("id") documentId: String
    ): ApiResponse<PdfResponse>
    
    // Sign Document
    @POST("documents/{id}/sign")
    suspend fun signDocument(
        @Path("id") documentId: String,
        @Body request: SignDocumentRequest
    ): ApiResponse<Document>
    
    // List Documents
    @GET("documents")
    suspend fun listDocuments(
        @Query("status") status: String?,
        @Query("type") type: String?,
        @Query("page") page: Int = 1,
        @Query("limit") limit: Int = 20
    ): ApiResponse<List<Document>>
    
    // Search Documents
    @GET("documents/search")
    suspend fun searchDocuments(
        @Query("query") query: String,
        @Query("filters") filters: String?
    ): ApiResponse<List<Document>>
    
    // Delete Document
    @DELETE("documents/{id}")
    suspend fun deleteDocument(
        @Path("id") documentId: String
    ): ApiResponse<Unit>
    
    // Get Templates
    @GET("documents/templates")
    suspend fun getTemplates(
        @Query("type") type: String?
    ): ApiResponse<List<DocumentTemplate>>
    
    // Upload Attachment
    @Multipart
    @POST("documents/{id}/upload-attachment")
    suspend fun uploadAttachment(
        @Path("id") documentId: String,
        @Part file: MultipartBody.Part
    ): ApiResponse<Document>
    
    // Share Document
    @POST("documents/{id}/share")
    suspend fun shareDocument(
        @Path("id") documentId: String,
        @Body request: ShareDocumentRequest
    ): ApiResponse<Unit>
    
    // Request Signature
    @POST("documents/{id}/request-signature")
    suspend fun requestSignature(
        @Path("id") documentId: String,
        @Body request: SignatureRequestData
    ): ApiResponse<Unit>
    
    // Download Document
    @GET("documents/{id}/download")
    suspend fun downloadDocument(
        @Path("id") documentId: String
    ): ResponseBody
}

data class CreateDocumentRequest(
    val title: String,
    val type: String,
    val templateId: String?,
    val content: Map<String, Any>,
    val metadata: DocumentMetadata,
    val shipmentId: String?
)

data class UpdateDocumentRequest(
    val title: String?,
    val content: Map<String, Any>?,
    val metadata: DocumentMetadata?
)

data class SignDocumentRequest(
    val signatureImage: String,  // Base64
    val signatureData: String?,
    val password: String?
)

data class ShareDocumentRequest(
    val recipientEmails: List<String>,
    val message: String?,
    val expiresIn: Long?  // milliseconds
)

data class SignatureRequestData(
    val recipientIds: List<String>,
    val message: String?,
    val deadline: LocalDateTime?
)

data class PdfResponse(
    val url: String,
    val fileName: String,
    val size: Long
)
```

### **3️⃣ Repository**

```kotlin
// data/repository/DocumentRepository.kt
class DocumentRepository(
    private val apiService: DocumentApiService,
    private val documentDao: DocumentDao,
    private val context: Context
) {
    
    suspend fun createDocument(request: CreateDocumentRequest): Result<Document> {
        return try {
            val response = apiService.createDocument(request)
            if (response.success && response.data != null) {
                documentDao.insertDocument(response.data.toEntity())
                Result.success(response.data)
            } else {
                Result.failure(Exception(response.message ?: "Unknown error"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    suspend fun getDocument(documentId: String): Result<Document> {
        return try {
            // Try local first
            val local = documentDao.getDocument(documentId)
            if (local != null) {
                return Result.success(local.toDomain())
            }
            
            // Fetch from server
            val response = apiService.getDocument(documentId)
            if (response.success && response.data != null) {
                documentDao.insertDocument(response.data.toEntity())
                Result.success(response.data)
            } else {
                Result.failure(Exception(response.message ?: "Unknown error"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    suspend fun generatePDF(documentId: String): Result<File> {
        return try {
            val response = apiService.generatePDF(documentId)
            if (response.success && response.data != null) {
                val fileUrl = response.data.url
                val file = downloadFile(fileUrl, response.data.fileName)
                Result.success(file)
            } else {
                Result.failure(Exception("Failed to generate PDF"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    suspend fun signDocument(
        documentId: String,
        signatureImage: Bitmap,
        password: String?
    ): Result<Document> {
        return try {
            val base64 = bitmapToBase64(signatureImage)
            val request = SignDocumentRequest(
                signatureImage = base64,
                password = password
            )
            
            val response = apiService.signDocument(documentId, request)
            if (response.success && response.data != null) {
                documentDao.insertDocument(response.data.toEntity())
                Result.success(response.data)
            } else {
                Result.failure(Exception("Failed to sign document"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    suspend fun listDocuments(
        status: DocumentStatus?,
        type: DocumentType?
    ): Result<List<Document>> {
        return try {
            val response = apiService.listDocuments(
                status = status?.name,
                type = type?.name
            )
            if (response.success && response.data != null) {
                documentDao.insertDocuments(response.data.map { it.toEntity() })
                Result.success(response.data)
            } else {
                Result.failure(Exception(response.message ?: "Unknown error"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    suspend fun searchDocuments(query: String): Result<List<Document>> {
        return try {
            val response = apiService.searchDocuments(query)
            if (response.success && response.data != null) {
                Result.success(response.data)
            } else {
                Result.failure(Exception("Search failed"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    suspend fun shareDocument(
        documentId: String,
        recipientEmails: List<String>,
        message: String?
    ): Result<Unit> {
        return try {
            val request = ShareDocumentRequest(
                recipientEmails = recipientEmails,
                message = message
            )
            val response = apiService.shareDocument(documentId, request)
            if (response.success) {
                Result.success(Unit)
            } else {
                Result.failure(Exception(response.message ?: "Share failed"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    suspend fun requestSignature(
        documentId: String,
        recipientIds: List<String>,
        deadline: LocalDateTime?
    ): Result<Unit> {
        return try {
            val request = SignatureRequestData(
                recipientIds = recipientIds,
                deadline = deadline
            )
            val response = apiService.requestSignature(documentId, request)
            if (response.success) {
                Result.success(Unit)
            } else {
                Result.failure(Exception("Request failed"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}
```

### **4️⃣ ViewModel**

```kotlin
// ui/documents/DocumentViewModel.kt
@HiltViewModel
class DocumentViewModel @Inject constructor(
    private val repository: DocumentRepository
) : ViewModel() {
    
    private val _documents = MutableStateFlow<UiState<List<Document>>>(UiState.Idle)
    val documents: StateFlow<UiState<List<Document>>> = _documents.asStateFlow()
    
    private val _currentDocument = MutableStateFlow<UiState<Document>>(UiState.Idle)
    val currentDocument: StateFlow<UiState<Document>> = _currentDocument.asStateFlow()
    
    private val _templates = MutableStateFlow<List<DocumentTemplate>>(emptyList())
    val templates: StateFlow<List<DocumentTemplate>> = _templates.asStateFlow()
    
    fun createDocument(request: CreateDocumentRequest) {
        viewModelScope.launch {
            _currentDocument.value = UiState.Loading
            val result = repository.createDocument(request)
            _currentDocument.value = if (result.isSuccess) {
                UiState.Success(result.getOrNull()!!)
            } else {
                UiState.Error(result.exceptionOrNull()?.message ?: "Unknown error")
            }
        }
    }
    
    fun getDocument(documentId: String) {
        viewModelScope.launch {
            _currentDocument.value = UiState.Loading
            val result = repository.getDocument(documentId)
            _currentDocument.value = if (result.isSuccess) {
                UiState.Success(result.getOrNull()!!)
            } else {
                UiState.Error(result.exceptionOrNull()?.message ?: "Unknown error")
            }
        }
    }
    
    fun generatePDF(documentId: String, onSuccess: (File) -> Unit) {
        viewModelScope.launch {
            val result = repository.generatePDF(documentId)
            if (result.isSuccess) {
                onSuccess(result.getOrNull()!!)
            }
        }
    }
    
    fun signDocument(documentId: String, signature: Bitmap, password: String?) {
        viewModelScope.launch {
            _currentDocument.value = UiState.Loading
            val result = repository.signDocument(documentId, signature, password)
            _currentDocument.value = if (result.isSuccess) {
                UiState.Success(result.getOrNull()!!)
            } else {
                UiState.Error(result.exceptionOrNull()?.message ?: "Sign failed")
            }
        }
    }
    
    fun listDocuments(status: DocumentStatus? = null, type: DocumentType? = null) {
        viewModelScope.launch {
            _documents.value = UiState.Loading
            val result = repository.listDocuments(status, type)
            _documents.value = if (result.isSuccess) {
                UiState.Success(result.getOrNull() ?: emptyList())
            } else {
                UiState.Error(result.exceptionOrNull()?.message ?: "Unknown error")
            }
        }
    }
    
    fun searchDocuments(query: String) {
        viewModelScope.launch {
            _documents.value = UiState.Loading
            val result = repository.searchDocuments(query)
            _documents.value = if (result.isSuccess) {
                UiState.Success(result.getOrNull() ?: emptyList())
            } else {
                UiState.Error(result.exceptionOrNull()?.message ?: "Search failed")
            }
        }
    }
    
    fun shareDocument(documentId: String, recipients: List<String>) {
        viewModelScope.launch {
            repository.shareDocument(documentId, recipients)
        }
    }
}
```

### **5️⃣ UI Screens**

```kotlin
// ui/documents/DocumentCreatorScreen.kt
@Composable
fun DocumentCreatorScreen(
    viewModel: DocumentViewModel = hiltViewModel(),
    onDocumentCreated: (String) -> Unit
) {
    var title by remember { mutableStateOf("") }
    var selectedType by remember { mutableStateOf<DocumentType?>(null) }
    var selectedTemplate by remember { mutableStateOf<DocumentTemplate?>(null) }
    var formData by remember { mutableStateOf<Map<String, String>>(emptyMap()) }
    
    val templates by viewModel.templates.collectAsState()
    val createState by viewModel.currentDocument.collectAsState()
    
    LaunchedEffect(Unit) {
        viewModel.loadTemplates(selectedType)
    }
    
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
            .verticalScroll(rememberScrollState())
    ) {
        // Document Title
        OutlinedTextField(
            value = title,
            onValueChange = { title = it },
            label = { Text("عنوان المستند") },
            modifier = Modifier
                .fillMaxWidth()
                .padding(8.dp)
        )
        
        // Document Type Selection
        ExposedDropdownMenuBox(
            expanded = false,
            onExpandedChange = {}
        ) {
            OutlinedTextField(
                value = selectedType?.name ?: "اختر نوع المستند",
                onValueChange = {},
                readOnly = true,
                label = { Text("نوع المستند") },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(8.dp)
            )
        }
        
        // Template Selection
        if (selectedType != null && templates.isNotEmpty()) {
            Column(modifier = Modifier.padding(8.dp)) {
                Text("قالب المستند", style = MaterialTheme.typography.labelMedium)
                templates.forEach { template ->
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(4.dp)
                            .clickable { selectedTemplate = template }
                    ) {
                        Column(modifier = Modifier.padding(12.dp)) {
                            Text(template.name, fontWeight = FontWeight.Bold)
                            Text(template.description, style = MaterialTheme.typography.bodySmall)
                        }
                    }
                }
            }
        }
        
        // Template Fields
        if (selectedTemplate != null) {
            DocumentFormFields(
                fields = selectedTemplate!!.fields,
                formData = formData,
                onFormDataChange = { formData = it }
            )
        }
        
        // Create Button
        Button(
            onClick = {
                val request = CreateDocumentRequest(
                    title = title,
                    type = selectedType?.name ?: "",
                    templateId = selectedTemplate?.id,
                    content = formData,
                    metadata = DocumentMetadata()
                )
                viewModel.createDocument(request)
            },
            modifier = Modifier
                .align(Alignment.CenterHorizontally)
                .padding(16.dp)
        ) {
            Text("إنشاء المستند")
        }
        
        // Loading/Error States
        when (createState) {
            is UiState.Loading -> CircularProgressIndicator()
            is UiState.Success -> {
                LaunchedEffect(Unit) {
                    onDocumentCreated((createState as UiState.Success).data.id)
                }
            }
            is UiState.Error -> {
                Text(
                    "خطأ: ${(createState as UiState.Error).message}",
                    color = Color.Red
                )
            }
            else -> {}
        }
    }
}

// ui/documents/DocumentSignerScreen.kt
@Composable
fun DocumentSignerScreen(
    documentId: String,
    viewModel: DocumentViewModel = hiltViewModel()
) {
    val document by viewModel.currentDocument.collectAsState()
    var showSignaturePad by remember { mutableStateOf(false) }
    var signatureImage by remember { mutableStateOf<Bitmap?>(null) }
    val signaturePadRef = remember { mutableStateOf<SignaturePadView?>(null) }
    
    LaunchedEffect(documentId) {
        viewModel.getDocument(documentId)
    }
    
    when (val state = document) {
        is UiState.Success -> {
            val doc = state.data
            
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp)
                    .verticalScroll(rememberScrollState())
            ) {
                // Document Preview
                Text(doc.title, style = MaterialTheme.typography.headlineSmall)
                
                // Document Content Preview
                Card(modifier = Modifier.fillMaxWidth()) {
                    Text(
                        doc.content,
                        modifier = Modifier.padding(12.dp),
                        style = MaterialTheme.typography.bodyMedium
                    )
                }
                
                // Existing Signatures
                if (doc.signatures.isNotEmpty()) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text("التوقيعات الموجودة:", fontWeight = FontWeight.Bold)
                        doc.signatures.forEach { sig ->
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(8.dp)
                            ) {
                                Text(sig.userName)
                                Spacer(modifier = Modifier.weight(1f))
                                Text(
                                    sig.timestamp.format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm")),
                                    style = MaterialTheme.typography.labelSmall
                                )
                            }
                        }
                    }
                }
                
                // Signature Pad
                if (showSignaturePad) {
                    SignaturePadComposable(
                        onSignatureCapture = { bitmap ->
                            signatureImage = bitmap
                            showSignaturePad = false
                        },
                        onCancel = { showSignaturePad = false }
                    )
                }
                
                // Sign Button
                if (signatureImage != null) {
                    Image(
                        bitmap = signatureImage!!.asImageBitmap(),
                        contentDescription = "التوقيع",
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(100.dp)
                    )
                    
                    Button(
                        onClick = {
                            viewModel.signDocument(documentId, signatureImage!!, null)
                        },
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text("تأكيد التوقيع")
                    }
                }
                
                // Start Signing
                if (signatureImage == null && !showSignaturePad) {
                    Button(
                        onClick = { showSignaturePad = true },
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text("توقيع المستند")
                    }
                }
            }
        }
        is UiState.Error -> {
            Text("خطأ: ${state.message}", color = Color.Red)
        }
        else -> CircularProgressIndicator()
    }
}

// ui/documents/DocumentListScreen.kt
@Composable
fun DocumentListScreen(
    viewModel: DocumentViewModel = hiltViewModel()
) {
    val documents by viewModel.documents.collectAsState()
    var searchQuery by remember { mutableStateOf("") }
    var selectedFilter by remember { mutableStateOf<DocumentType?>(null) }
    
    LaunchedEffect(Unit) {
        viewModel.listDocuments()
    }
    
    Column(modifier = Modifier.fillMaxSize()) {
        // Search Bar
        SearchBar(
            query = searchQuery,
            onQueryChange = {
                searchQuery = it
                if (it.isNotEmpty()) {
                    viewModel.searchDocuments(it)
                } else {
                    viewModel.listDocuments(type = selectedFilter)
                }
            },
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        )
        
        // Filter Chips
        LazyRow(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp)
        ) {
            items(DocumentType.values().size) { index ->
                FilterChip(
                    selected = selectedFilter == DocumentType.values()[index],
                    onClick = {
                        selectedFilter = DocumentType.values()[index]
                        viewModel.listDocuments(type = selectedFilter)
                    },
                    label = { Text(DocumentType.values()[index].name) }
                )
            }
        }
        
        // Documents List
        when (documents) {
            is UiState.Success -> {
                LazyColumn(
                    modifier = Modifier.fillMaxWidth()
                ) {
                    items((documents as UiState.Success<List<Document>>).data) { doc ->
                        DocumentCard(
                            document = doc,
                            onViewClick = { /* Navigate to view */ },
                            onShareClick = { /* Share */ },
                            onDownloadClick = { /* Download */ }
                        )
                    }
                }
            }
            is UiState.Loading -> CircularProgressIndicator()
            is UiState.Error -> Text("خطأ: ${(documents as UiState.Error).message}")
            else -> {}
        }
    }
}

@Composable
fun DocumentCard(
    document: Document,
    onViewClick: () -> Unit,
    onShareClick: () -> Unit,
    onDownloadClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp)
            .clickable { onViewClick() }
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(modifier = Modifier.fillMaxWidth()) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(document.title, fontWeight = FontWeight.Bold)
                    Text(
                        document.type.name,
                        style = MaterialTheme.typography.bodySmall
                    )
                }
                StatusBadge(status = document.status)
            }
            
            Text(
                document.metadata.description ?: "",
                style = MaterialTheme.typography.bodySmall,
                modifier = Modifier.padding(top = 8.dp)
            )
            
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 12.dp),
                horizontalArrangement = Arrangement.End
            ) {
                IconButton(onClick = onShareClick) {
                    Icon(Icons.Default.Share, "Share")
                }
                IconButton(onClick = onDownloadClick) {
                    Icon(Icons.Default.Download, "Download")
                }
            }
        }
    }
}
```

---

## 🌐 Backend Implementation

### **1️⃣ MongoDB Schemas**

```javascript
// models/Document.js
const documentSchema = new Schema({
    title: { type: String, required: true },
    type: { 
        type: String, 
        enum: ['INVOICE', 'DELIVERY_NOTE', 'RECEIPT', 'CONTRACT', 'REPORT', 'SHIPMENT_LABEL', 'CUSTOMS', 'INSURANCE'],
        required: true 
    },
    template: {
        type: Schema.Types.ObjectId,
        ref: 'DocumentTemplate'
    },
    content: { type: Map, required: true },
    metadata: {
        description: String,
        category: String,
        shipmentId: String,
        customerId: String,
        driverId: String,
        fiscalNumber: String,
        legalNotes: String
    },
    status: {
        type: String,
        enum: ['DRAFT', 'IN_PROGRESS', 'PENDING_REVIEW', 'PENDING_SIGN', 'SIGNED', 'APPROVED', 'REJECTED', 'ARCHIVED'],
        default: 'DRAFT'
    },
    signatures: [{
        userId: Schema.Types.ObjectId,
        userName: String,
        signatureImage: String,  // Base64
        signatureData: String,   // Digital signature
        timestamp: Date,
        ipAddress: String,
        deviceInfo: String
    }],
    versions: [{
        versionNumber: Number,
        content: Map,
        createdBy: Schema.Types.ObjectId,
        createdAt: Date,
        changeLog: String
    }],
    createdBy: { type: Schema.Types.ObjectId, ref: 'User', required: true },
    createdAt: { type: Date, default: Date.now },
    updatedAt: { type: Date, default: Date.now },
    fileUrl: String,
    tags: [String],
    companyId: Schema.Types.ObjectId,
    deletedAt: Date
});

documentSchema.index({ companyId: 1, createdAt: -1 });
documentSchema.index({ type: 1, status: 1 });
documentSchema.index({ 'metadata.shipmentId': 1 });
documentSchema.index({ 'metadata.customerId': 1 });

module.exports = mongoose.model('Document', documentSchema);
```

### **2️⃣ Backend Routes**

```javascript
// routes/documents.js
const express = require('express');
const router = express.Router();
const documentController = require('../controllers/documentController');
const { auth, authorize } = require('../middleware/auth');

// Create document
router.post('/create', auth, documentController.createDocument);

// Get document
router.get('/:id', auth, documentController.getDocument);

// Update document
router.put('/:id', auth, documentController.updateDocument);

// Generate PDF
router.post('/:id/generate-pdf', auth, documentController.generatePDF);

// Sign document
router.post('/:id/sign', auth, documentController.signDocument);

// List documents
router.get('/', auth, documentController.listDocuments);

// Search documents
router.get('/search', auth, documentController.searchDocuments);

// Delete document
router.delete('/:id', auth, documentController.deleteDocument);

// Get templates
router.get('/templates', auth, documentController.getTemplates);

// Upload attachment
router.post('/:id/upload-attachment', auth, documentController.uploadAttachment);

// Share document
router.post('/:id/share', auth, documentController.shareDocument);

// Request signature
router.post('/:id/request-signature', auth, documentController.requestSignature);

// Download document
router.get('/:id/download', auth, documentController.downloadDocument);

module.exports = router;
```

### **3️⃣ Document Controller**

```javascript
// controllers/documentController.js
const Document = require('../models/Document');
const DocumentTemplate = require('../models/DocumentTemplate');
const pdfService = require('../services/pdfService');
const signatureService = require('../services/signatureService');

exports.createDocument = async (req, res) => {
    try {
        const { title, type, templateId, content, metadata, shipmentId } = req.body;
        
        const document = new Document({
            title,
            type,
            template: templateId,
            content,
            metadata,
            createdBy: req.user._id,
            versions: [{
                versionNumber: 1,
                content,
                createdBy: req.user._id,
                createdAt: new Date()
            }]
        });
        
        await document.save();
        
        res.json({
            success: true,
            data: document,
            message: 'تم إنشاء المستند بنجاح'
        });
    } catch (error) {
        res.status(400).json({ 
            success: false, 
            message: error.message 
        });
    }
};

exports.generatePDF = async (req, res) => {
    try {
        const { id } = req.params;
        const document = await Document.findById(id);
        
        if (!document) {
            return res.status(404).json({ 
                success: false, 
                message: 'المستند غير موجود' 
            });
        }
        
        // Generate PDF
        const pdfBuffer = await pdfService.generatePDF(document);
        
        // Save to storage
        const fileName = `${document.title}_${Date.now()}.pdf`;
        const fileUrl = await pdfService.uploadPDF(pdfBuffer, fileName);
        
        // Update document
        document.fileUrl = fileUrl;
        await document.save();
        
        res.json({
            success: true,
            data: {
                url: fileUrl,
                fileName,
                size: pdfBuffer.length
            }
        });
    } catch (error) {
        res.status(400).json({ 
            success: false, 
            message: error.message 
        });
    }
};

exports.signDocument = async (req, res) => {
    try {
        const { id } = req.params;
        const { signatureImage, signatureData, password } = req.body;
        
        const document = await Document.findById(id);
        
        if (!document) {
            return res.status(404).json({ 
                success: false, 
                message: 'المستند غير موجود' 
            });
        }
        
        // Add signature
        document.signatures.push({
            userId: req.user._id,
            userName: req.user.name,
            signatureImage,
            signatureData,
            timestamp: new Date(),
            ipAddress: req.ip,
            deviceInfo: req.get('user-agent')
        });
        
        // Update status
        if (document.signatures.length === document.requiredSignatures) {
            document.status = 'SIGNED';
        } else {
            document.status = 'PENDING_SIGN';
        }
        
        await document.save();
        
        // Send email notification to next signer (if applicable)
        if (document.status === 'PENDING_SIGN') {
            await signatureService.notifyNextSigner(document);
        }
        
        res.json({
            success: true,
            data: document,
            message: 'تم التوقيع على المستند بنجاح'
        });
    } catch (error) {
        res.status(400).json({ 
            success: false, 
            message: error.message 
        });
    }
};

exports.shareDocument = async (req, res) => {
    try {
        const { id } = req.params;
        const { recipientEmails, message } = req.body;
        
        const document = await Document.findById(id);
        
        if (!document) {
            return res.status(404).json({ 
                success: false, 
                message: 'المستند غير موجود' 
            });
        }
        
        // Send emails
        for (const email of recipientEmails) {
            await pdfService.sendDocumentEmail(
                email,
                document,
                message
            );
        }
        
        res.json({
            success: true,
            message: `تم مشاركة المستند مع ${recipientEmails.length} شخص`
        });
    } catch (error) {
        res.status(400).json({ 
            success: false, 
            message: error.message 
        });
    }
};

exports.listDocuments = async (req, res) => {
    try {
        const { status, type, page = 1, limit = 20 } = req.query;
        
        const filter = { createdBy: req.user._id };
        if (status) filter.status = status;
        if (type) filter.type = type;
        
        const documents = await Document
            .find(filter)
            .sort({ createdAt: -1 })
            .limit(limit * 1)
            .skip((page - 1) * limit);
        
        const total = await Document.countDocuments(filter);
        
        res.json({
            success: true,
            data: documents,
            pagination: {
                currentPage: page,
                totalPages: Math.ceil(total / limit),
                total
            }
        });
    } catch (error) {
        res.status(400).json({ 
            success: false, 
            message: error.message 
        });
    }
};
```

---

## 📊 ميزات إضافية

### **🔍 Full-Text Search**

```javascript
// indexes/documentSearch.js
db.documents.createIndex({
    title: "text",
    "content": "text",
    "metadata.description": "text",
    tags: "text"
});

// في Controller
exports.searchDocuments = async (req, res) => {
    const { query } = req.query;
    
    const documents = await Document.find(
        { $text: { $search: query } },
        { score: { $meta: "textScore" } }
    ).sort({ score: { $meta: "textScore" } });
    
    res.json({ success: true, data: documents });
};
```

### **📧 Email Notifications**

```javascript
// services/emailService.js
async function sendDocumentEmailNotification(document, recipient) {
    const emailContent = `
        <h2>تم مشاركة مستند معك</h2>
        <p>المستند: ${document.title}</p>
        <p>النوع: ${document.type}</p>
        <p><a href="https://app.edham.com/documents/${document._id}">عرض المستند</a></p>
    `;
    
    await sendEmail(
        recipient.email,
        'مستند جديد',
        emailContent
    );
}
```

### **🔒 Digital Signatures (Advanced)**

```javascript
// services/signatureService.js
const crypto = require('crypto');

function createDigitalSignature(document, privateKey) {
    const dataToSign = JSON.stringify(document.content);
    const sign = crypto.createSign('RSA-SHA256');
    sign.update(dataToSign);
    return sign.sign(privateKey, 'hex');
}

function verifySignature(document, signature, publicKey) {
    const dataToVerify = JSON.stringify(document.content);
    const verify = crypto.createVerify('RSA-SHA256');
    verify.update(dataToVerify);
    return verify.verify(publicKey, signature, 'hex');
}
```

---

## 🎯 Timeline التنفيذ

```
الأسبوع 1:
├─ أ) Data Models (Android + Backend)
├─ ب) API Endpoints الأساسية
└─ ج) Database Schemas

الأسبوع 2:
├─ أ) Document Creator Screen
├─ ب) Document List Screen
└─ ج) PDF Generation

الأسبوع 3:
├─ أ) Digital Signatures
├─ ب) Document Sharing
└─ ج) Document Signing UI

الأسبوع 4:
├─ أ) Templates System
├─ ب) Full-Text Search
└─ ج) Testing & Polish
```

---

## ✅ معايير الاكتمال

- ✅ تنشيء مستندات جديدة من قوالب
- ✅ عرض وتحرير المستندات
- ✅ إنشاء PDF مع توقيع رقمي
- ✅ مشاركة المستندات عبر البريد
- ✅ طلب التوقيع من أطراف أخرى
- ✅ البحث عن المستندات
- ✅ تتبع إصدارات المستندات
- ✅ تنزيل وحفظ المستندات
