package com.ma_fantatra.data.worker

import android.content.Context
import androidx.hilt.work.HiltWorker
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.ma_fantatra.domain.repository.CommuneRepository
import com.ma_fantatra.domain.repository.FokontanyRepository
import com.ma_fantatra.domain.repository.ProcedureRepository
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject

@HiltWorker
class SyncWorker @AssistedInject constructor(
    @Assisted context: Context,
    @Assisted params: WorkerParameters,
    private val procedureRepository: ProcedureRepository,
    private val fokontanyRepository: FokontanyRepository,
    private val communeRepository: CommuneRepository,
) : CoroutineWorker(context, params) {

    override suspend fun doWork(): Result {
        return try {
            procedureRepository.refresh()
            fokontanyRepository.refresh()
            communeRepository.refresh()
            Result.success()
        } catch (_: Exception) {
            Result.retry()
        }
    }
}