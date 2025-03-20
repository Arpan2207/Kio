package com.kioskable.api.controller

import com.kioskable.api.dto.request.ContentRequest
import com.kioskable.api.dto.response.ContentResponse
import com.kioskable.api.dto.response.MessageResponse
import com.kioskable.api.dto.response.PagedContentResponse
import com.kioskable.api.service.ContentService
import org.springframework.data.domain.PageRequest
import org.springframework.data.domain.Sort
import org.springframework.http.HttpStatus
import org.springframework.http.MediaType
import org.springframework.http.ResponseEntity
import org.springframework.security.core.annotation.AuthenticationPrincipal
import org.springframework.security.core.userdetails.UserDetails
import org.springframework.web.bind.annotation.*
import org.springframework.web.multipart.MultipartFile
import javax.validation.Valid

@RestController
@RequestMapping("/api/content")
class ContentController(
    private val contentService: ContentService
) {
    
    @GetMapping
    fun getAllContent(
        @RequestParam(defaultValue = "0") page: Int,
        @RequestParam(defaultValue = "10") size: Int,
        @RequestParam(defaultValue = "order") sortBy: String,
        @RequestParam(required = false) businessId: String?
    ): ResponseEntity<PagedContentResponse> {
        // TODO: Get the current user's business ID if not provided explicitly
        val businessIdToUse = businessId ?: "default-business"
        
        val pageable = PageRequest.of(page, size, Sort.by(sortBy))
        val contentPage = contentService.getContentByBusinessId(businessIdToUse, pageable)
        
        val response = PagedContentResponse(
            content = contentPage.content.map { ContentResponse.fromEntity(it) },
            totalElements = contentPage.totalElements,
            totalPages = contentPage.totalPages,
            currentPage = page,
            size = size,
            first = contentPage.isFirst,
            last = contentPage.isLast
        )
        
        return ResponseEntity.ok(response)
    }
    
    @GetMapping("/active")
    fun getActiveContent(
        @RequestParam(defaultValue = "0") page: Int,
        @RequestParam(defaultValue = "10") size: Int,
        @RequestParam(defaultValue = "order") sortBy: String,
        @RequestParam(required = false) businessId: String?
    ): ResponseEntity<PagedContentResponse> {
        // TODO: Get the current user's business ID if not provided explicitly
        val businessIdToUse = businessId ?: "default-business"
        
        val pageable = PageRequest.of(page, size, Sort.by(sortBy))
        val contentPage = contentService.getActiveContentByBusinessId(businessIdToUse, pageable)
        
        val response = PagedContentResponse(
            content = contentPage.content.map { ContentResponse.fromEntity(it) },
            totalElements = contentPage.totalElements,
            totalPages = contentPage.totalPages,
            currentPage = page,
            size = size,
            first = contentPage.isFirst,
            last = contentPage.isLast
        )
        
        return ResponseEntity.ok(response)
    }
    
    @GetMapping("/scheduled")
    fun getScheduledContent(
        @RequestParam(defaultValue = "0") page: Int,
        @RequestParam(defaultValue = "10") size: Int,
        @RequestParam(defaultValue = "order") sortBy: String,
        @RequestParam(required = false) businessId: String?
    ): ResponseEntity<PagedContentResponse> {
        // TODO: Get the current user's business ID if not provided explicitly
        val businessIdToUse = businessId ?: "default-business"
        
        val pageable = PageRequest.of(page, size, Sort.by(sortBy))
        val contentPage = contentService.getScheduledContent(businessIdToUse, pageable)
        
        val response = PagedContentResponse(
            content = contentPage.content.map { ContentResponse.fromEntity(it) },
            totalElements = contentPage.totalElements,
            totalPages = contentPage.totalPages,
            currentPage = page,
            size = size,
            first = contentPage.isFirst,
            last = contentPage.isLast
        )
        
        return ResponseEntity.ok(response)
    }
    
    @GetMapping("/{id}")
    fun getContentById(@PathVariable id: String): ResponseEntity<ContentResponse> {
        val content = contentService.getContentById(id)
        return ResponseEntity.ok(ContentResponse.fromEntity(content))
    }
    
    @PostMapping
    fun createContent(
        @Valid @RequestBody contentRequest: ContentRequest,
        @RequestParam businessId: String,
        @AuthenticationPrincipal userDetails: UserDetails
    ): ResponseEntity<ContentResponse> {
        val content = contentRequest.toEntity(businessId, userDetails.username)
        val savedContent = contentService.createContent(content)
        return ResponseEntity
            .status(HttpStatus.CREATED)
            .body(ContentResponse.fromEntity(savedContent))
    }
    
    @PutMapping("/{id}")
    fun updateContent(
        @PathVariable id: String,
        @Valid @RequestBody contentRequest: ContentRequest,
        @RequestParam businessId: String,
        @AuthenticationPrincipal userDetails: UserDetails
    ): ResponseEntity<ContentResponse> {
        val content = contentRequest.toEntity(businessId, userDetails.username)
        val updatedContent = contentService.updateContent(id, content)
        return ResponseEntity.ok(ContentResponse.fromEntity(updatedContent))
    }
    
    @DeleteMapping("/{id}")
    fun deleteContent(@PathVariable id: String): ResponseEntity<MessageResponse> {
        contentService.deleteContent(id)
        return ResponseEntity.ok(MessageResponse("Content deleted successfully"))
    }
    
    @PostMapping("/upload")
    fun uploadFile(
        @RequestParam("file") file: MultipartFile,
        @RequestParam businessId: String
    ): ResponseEntity<Map<String, String>> {
        if (file.isEmpty) {
            return ResponseEntity
                .badRequest()
                .body(mapOf("error" to "Please select a file to upload"))
        }
        
        val fileName = file.originalFilename ?: "unnamed-file"
        val contentType = file.contentType ?: MediaType.APPLICATION_OCTET_STREAM_VALUE
        
        val fileUrl = contentService.uploadContentFile(
            businessId,
            file.bytes,
            fileName,
            contentType
        )
        
        return ResponseEntity
            .status(HttpStatus.CREATED)
            .body(mapOf("url" to fileUrl))
    }
    
    @PutMapping("/{id}/active")
    fun setContentActiveStatus(
        @PathVariable id: String,
        @RequestParam active: Boolean
    ): ResponseEntity<ContentResponse> {
        val updatedContent = contentService.setContentActiveStatus(id, active)
        return ResponseEntity.ok(ContentResponse.fromEntity(updatedContent))
    }
    
    @PutMapping("/reorder")
    fun reorderContent(
        @RequestBody contentIds: List<String>
    ): ResponseEntity<List<ContentResponse>> {
        val updatedContent = contentService.reorderContent(contentIds)
        return ResponseEntity.ok(updatedContent.map { ContentResponse.fromEntity(it) })
    }
} 