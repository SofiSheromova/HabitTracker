package com.example.habittracker.editor

import android.view.View
import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import com.example.domain.model.Habit
import com.example.domain.model.Periodicity
import com.example.domain.model.Priority
import com.example.domain.model.Type
import com.example.domain.repository.HabitRepository
import com.example.domain.usecase.DeleteHabitUseCase
import com.example.domain.usecase.InsertHabitUseCase
import com.example.domain.usecase.UpdateHabitUseCase
import com.example.habittracker.ui.editor.EditorFields
import com.example.habittracker.ui.editor.EditorViewModel
import com.nhaarman.mockitokotlin2.whenever
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.runBlockingTest
import org.junit.Assert.*
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.mockito.Mockito.mock


class EditorViewModelTest {
    @get:Rule
    val rule = InstantTaskExecutorRule()

    private var insertInputValue: Habit? = null
    private var updateInputValue: Habit? = null
    private var deleteInputValue: Habit? = null

    private val habitRepository = object : HabitRepository {
        override val allHabits: Flow<List<Habit>>
            get() = flowOf(listOf())

        override suspend fun start() {}

        override suspend fun refresh(): Boolean = true

        override suspend fun insert(habit: Habit) {
            insertInputValue = habit
        }

        override suspend fun update(habit: Habit) {
            updateInputValue = habit
        }

        override suspend fun delete(habit: Habit) {
            deleteInputValue = habit
        }

        override suspend fun markDone(habit: Habit, time: Long) {}
    }

    private val insertHabitUseCase: InsertHabitUseCase = InsertHabitUseCase(habitRepository)
    private val updateHabitUseCase: UpdateHabitUseCase = UpdateHabitUseCase(habitRepository)
    private val deleteHabitUseCase: DeleteHabitUseCase = DeleteHabitUseCase(habitRepository)
    private lateinit var editorViewModel: EditorViewModel

    @Before
    fun setUp() {
        insertInputValue = null
        updateInputValue = null
        deleteInputValue = null

        editorViewModel = EditorViewModel(
            insertHabitUseCase,
            updateHabitUseCase,
            deleteHabitUseCase
        )
    }

    @Test
    fun test() {
        assertTrue(true)
    }

    @Test
    fun checkCardExist() {
        assertFalse(editorViewModel.cardExists)
    }

    @ExperimentalCoroutinesApi
    @Test
    fun insertHabit() {
        val expectedTitle = "new title"
        val expectedDescription = "new description"
        val expectedType = Type.BAD

        val editor = EditorFields()
        editor.title = expectedTitle
        editor.description = expectedDescription
        editor.type = expectedType

        runBlockingTest {
            val job = editorViewModel.updateCard(editor)
            job.join()

            assertNotNull(insertInputValue)
            assertEquals(expectedTitle, insertInputValue!!.title)
            assertEquals(expectedDescription, insertInputValue!!.description)
            assertEquals(expectedType, insertInputValue!!.type)
        }
    }

    @ExperimentalCoroutinesApi
    @Test
    fun updateHabit() {
        val originalHabit = Habit(
            "title",
            "description",
            Periodicity(1, 2),
            Type.GOOD,
            Priority.HIGH
        )
        editorViewModel.setCard(originalHabit)

        val expectedTitle = "new title"
        val expectedDescription = "new description"
        val expectedType = Type.BAD

        val editor = EditorFields()
        editor.title = expectedTitle
        editor.description = expectedDescription
        editor.type = expectedType

        runBlockingTest {
            val job = editorViewModel.updateCard(editor)
            job.join()

            assertNotNull(updateInputValue)
            assertEquals(expectedTitle, updateInputValue!!.title)
            assertEquals(expectedDescription, updateInputValue!!.description)
            assertEquals(expectedType, updateInputValue!!.type)
        }
    }

    @ExperimentalCoroutinesApi
    @Test
    fun deleteHabit() {
        val originalHabit = Habit(
            "title",
            "description",
            Periodicity(1, 2),
            Type.GOOD,
            Priority.HIGH
        )
        editorViewModel.setCard(originalHabit)
        val view = mock(View::class.java)
        whenever(view.id).thenReturn(1)

        runBlockingTest {
            val job = editorViewModel.onDelete(view)
            job.join()

            assertNotNull(deleteInputValue)
            assertEquals(originalHabit.title, deleteInputValue!!.title)
        }
    }
}