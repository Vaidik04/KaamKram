package com.kaamkram.app.data.repository

import android.content.Context
import android.net.Uri
import com.kaamkram.app.data.local.dao.JobEventDao
import com.kaamkram.app.data.local.dao.PhotoEvidenceDao
import com.kaamkram.app.data.local.entity.JobEventEntity
import com.kaamkram.app.data.local.entity.PhotoEvidenceEntity
import com.kaamkram.app.domain.model.JobEventType
import com.kaamkram.app.domain.model.PhotoCategory
import com.kaamkram.app.domain.model.PhotoEvidence
import com.kaamkram.app.domain.model.PhotoSource
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import java.io.File
import java.io.FileOutputStream
import java.util.UUID
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class PhotoEvidenceRepository @Inject constructor(
    @ApplicationContext private val context: Context,
    private val photoEvidenceDao: PhotoEvidenceDao,
    private val jobEventDao: JobEventDao
) {
    fun getPhotosForJob(jobId: String): Flow<List<PhotoEvidence>> =
        photoEvidenceDao.getPhotosForJob(jobId).map { list ->
            list.map {
                PhotoEvidence(
                    id = it.id,
                    jobId = it.jobId,
                    filePath = it.filePath,
                    category = it.category,
                    source = it.source,
                    caption = it.caption,
                    capturedAtEpochMs = it.capturedAtEpochMs
                )
            }
        }

    suspend fun savePhotoEvidence(
        jobId: String,
        sourceUri: Uri,
        category: PhotoCategory,
        source: PhotoSource,
        caption: String? = null
    ): PhotoEvidence {
        val photoId = UUID.randomUUID().toString()
        val photosDir = File(context.filesDir, "photos").apply { mkdirs() }
        val targetFile = File(photosDir, "evidence_${jobId}_$photoId.jpg")

        context.contentResolver.openInputStream(sourceUri)?.use { input ->
            FileOutputStream(targetFile).use { output ->
                input.copyTo(output)
            }
        }

        val entity = PhotoEvidenceEntity(
            id = photoId,
            jobId = jobId,
            filePath = targetFile.absolutePath,
            category = category,
            source = source,
            caption = caption?.trim()?.ifEmpty { null }
        )
        photoEvidenceDao.insertPhoto(entity)

        jobEventDao.insertEvent(
            JobEventEntity(
                id = UUID.randomUUID().toString(),
                jobId = jobId,
                eventType = JobEventType.PHOTO_ADDED,
                summary = "Work photo added ($category) - ${source.name}"
            )
        )

        return PhotoEvidence(
            id = entity.id,
            jobId = entity.jobId,
            filePath = entity.filePath,
            category = entity.category,
            source = entity.source,
            caption = entity.caption,
            capturedAtEpochMs = entity.capturedAtEpochMs
        )
    }

    suspend fun deletePhoto(photoId: String, filePath: String) {
        photoEvidenceDao.deletePhoto(photoId)
        val file = File(filePath)
        if (file.exists()) {
            file.delete()
        }
    }
}
