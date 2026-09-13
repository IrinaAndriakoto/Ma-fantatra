package com.ma_fantatra.ui.procedures

import com.ma_fantatra.domain.model.Procedure
import com.ma_fantatra.domain.model.ProcedureCategory
import com.ma_fantatra.testing.FakeProcedureRepository
import com.ma_fantatra.testing.MainDispatcherRule
import com.ma_fantatra.testing.sampleProcedures
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Rule
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class ProcedureListViewModelTest {

    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    private suspend fun filteredIds(viewModel: ProcedureListViewModel): List<Long> =
        viewModel.filteredProcedures.first().map(Procedure::id)

    @Test
    fun `init shows all procedures`() = runTest {
        val viewModel = ProcedureListViewModel(FakeProcedureRepository(sampleProcedures()))

        assertEquals(listOf(1L, 2L, 3L), filteredIds(viewModel))
    }

    @Test
    fun `query filters by title ignoring case`() = runTest {
        val viewModel = ProcedureListViewModel(FakeProcedureRepository(sampleProcedures()))

        viewModel.onQueryChanged("carte")
        assertEquals(listOf(1L), filteredIds(viewModel))
    }

    @Test
    fun `query filters by description`() = runTest {
        val viewModel = ProcedureListViewModel(FakeProcedureRepository(sampleProcedures()))

        viewModel.onQueryChanged("domicile")
        assertEquals(listOf(2L), filteredIds(viewModel))
    }

    @Test
    fun `category filter narrows results`() = runTest {
        val viewModel = ProcedureListViewModel(FakeProcedureRepository(sampleProcedures()))

        viewModel.onCategorySelected(ProcedureCategory.ETAT_CIVIL)
        assertEquals(listOf(3L), filteredIds(viewModel))
    }

    @Test
    fun `category and query combined`() = runTest {
        val viewModel = ProcedureListViewModel(FakeProcedureRepository(sampleProcedures()))

        viewModel.onCategorySelected(ProcedureCategory.ETAT_CIVIL)
        viewModel.onQueryChanged("acte")
        assertEquals(listOf(3L), filteredIds(viewModel))

        viewModel.onQueryChanged("inexistant")
        assertTrue(filteredIds(viewModel).isEmpty())
    }

    @Test
    fun `no match returns empty list`() = runTest {
        val viewModel = ProcedureListViewModel(FakeProcedureRepository(sampleProcedures()))

        viewModel.onQueryChanged("zzzz")
        assertTrue(filteredIds(viewModel).isEmpty())
    }
}