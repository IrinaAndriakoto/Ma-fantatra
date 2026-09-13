package com.ma_fantatra.domain.model

data class ChecklistProgress(
    val checkedCount: Int,
    val totalCount: Int,
) {
    val fraction: Float
        get() = if (totalCount == 0) 0f else checkedCount.toFloat() / totalCount

    val isComplete: Boolean
        get() = totalCount > 0 && checkedCount == totalCount
}

fun computeChecklistProgress(
    requirements: List<DocumentRequirement>,
    checkedDocumentIds: Set<Long>,
): ChecklistProgress = ChecklistProgress(
    checkedCount = requirements.count { it.id in checkedDocumentIds },
    totalCount = requirements.size,
)