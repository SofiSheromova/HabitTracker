package com.example.habittracker.editor

import android.view.View
import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.test.platform.app.InstrumentationRegistry
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
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.withContext
import org.junit.Assert.*
import org.junit.Before
import org.junit.Rule
import org.junit.Test


class EditorViewModelTest {
    @get:Rule
    val rule = InstantTaskExecutorRule()

    private var insertInputValue: Habit? = null
    private var updateInputValue: Habit? = null
    private var deleteInputValue: Habit? = null
    private var markDoneInputValue: Pair<Habit?, Long?> = Pair(null, null)

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

        override suspend fun markDone(habit: Habit, time: Long) {
            markDoneInputValue = Pair(habit, time)
        }
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
        markDoneInputValue = Pair(null, null)

        runBlocking {
            withContext(Dispatchers.Main) {
                editorViewModel = EditorViewModel(
                    insertHabitUseCase,
                    updateHabitUseCase,
                    deleteHabitUseCase
                )
            }
        }

    }

    @Test
    fun checkCardExist() {
        assertFalse(editorViewModel.cardExists)
    }

    @Test
    fun insertHabit() {
        val expectedTitle = "new title"
        val expectedDescription = "new description"
        val expectedType = Type.BAD

        val editor = EditorFields()
        editor.title = expectedTitle
        editor.description = expectedDescription
        editor.type = expectedType

        runBlocking {
            val job = editorViewModel.updateCard(editor)
            job.join()

            assertNotNull(insertInputValue)
            assertEquals(expectedTitle, insertInputValue!!.title)
            assertEquals(expectedDescription, insertInputValue!!.description)
            assertEquals(expectedType, insertInputValue!!.type)
        }
    }

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

        runBlocking {
            val job = editorViewModel.updateCard(editor)
            job.join()

            assertNotNull(updateInputValue)
            assertEquals(expectedTitle, updateInputValue!!.title)
            assertEquals(expectedDescription, updateInputValue!!.description)
            assertEquals(expectedType, updateInputValue!!.type)
        }
    }

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
        val appContext = InstrumentationRegistry.getInstrumentation().targetContext

        runBlocking {
            val job = editorViewModel.onDelete(View(appContext))
            job.join()

            assertNotNull(deleteInputValue)
            assertEquals(originalHabit.title, deleteInputValue!!.title)
        }
    }
}