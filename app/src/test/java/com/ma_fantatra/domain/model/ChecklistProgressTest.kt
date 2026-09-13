package com.ma_fantatra.domain.model

import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class ChecklistProgressTest {

    private fun requirement(id: Long) = DocumentRequirement(
        id = id,
        procedureId = 1L,
        title = "Pièce $id",
        isMandatory = true,
        note = null,
    )

    @Test
    fun `empty checklist has zero progress and is not complete`() {
        val progress = computeChecklistProgress(requirements = emptyList(), checkedDocumentIds = emptySet())

        assertEquals(0, progress.checkedCount)
        assertEquals(0, progress.totalCount)
        assertEquals(0f, progress.fraction)
        assertFalse(progress.isComplete)
    }

    @Test
    fun `partial checklist computes count and fraction`() {
        val requirements = listOf(requirement(1L), requirement(2L), requirement(3L), requirement(4L), requirement(5L))
        val checked = setOf(1L, 2L, 3L)

        val progress = computeChecklistProgress(requirements, checked)

        assertEquals(3, progress.checkedCount)
        assertEquals(5, progress.totalCount)
        assertEquals(0.6f, progress.fraction)
        assertFalse(progress.isComplete)
    }

    @Test
    fun `all checked is complete`() {
        val requirements = listOf(requirement(1L), requirement(2L))
        val checked = setOf(1L, 2L)

        val progress = computeChecklistProgress(requirements, checked)

        assertEquals(2, progress.checkedCount)
        assertEquals(2, progress.totalCount)
        assertEquals(1f, progress.fraction)
        assertTrue(progress.isComplete)
    }

    @Test
    fun `checked ids outside the checklist are ignored`() {
        val requirements = listOf(requirement(1L))
        val checked = setOf(1L, 999L)

        val progress = computeChecklistProgress(requirements, checked)

        assertEquals(1, progress.checkedCount)
        assertEquals(1, progress.totalCount)
    }
}