package com.example.habittracker.editor

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import com.example.domain.model.Habit
import com.example.domain.model.Periodicity
import com.example.domain.model.Priority
import com.example.domain.model.Type
import com.example.domain.repository.HabitRepository
import com.example.domain.usecase.DeleteHabitUseCase
import com.example.domain.usecase.InsertHabitUseCase
import com.example.domain.usecase.UpdateHabitUseCase
import com.example.habittracker.CoroutineScopeRule
import com.example.habittracker.CoroutineTest
import com.example.habittracker.ui.editor.EditorViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf
import org.junit.Assert
import org.junit.Before
import org.junit.Rule
import org.junit.Test

@ExperimentalCoroutinesApi
class ExperimentalTest : CoroutineTest {
    override val coroutineRule: CoroutineScopeRule = CoroutineScopeRule()

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
    fun checkCardExist() {
        Assert.assertFalse(editorViewModel.cardExists)
    }

    @Test
    fun insertHabit() = test {
        val expectedTitle = "new title"
        val expectedDescription = "new description"
        val expectedType = Type.BAD

        editorViewModel.editor.title = expectedTitle
        editorViewModel.editor.description = expectedDescription
        editorViewModel.editor.type = expectedType

        editorViewModel.onSave()

        Assert.assertNotNull(insertInputValue)
        Assert.assertEquals(expectedTitle, insertInputValue!!.title)
        Assert.assertEquals(expectedDescription, insertInputValue!!.description)
        Assert.assertEquals(expectedType, insertInputValue!!.type)

    }

    @Test
    fun updateHabit() = test {
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

        editorViewModel.editor.title = expectedTitle
        editorViewModel.editor.description = expectedDescription
        editorViewModel.editor.type = expectedType

        editorViewModel.onSave()

        Assert.assertNotNull(updateInputValue)
        Assert.assertEquals(expectedTitle, updateInputValue!!.title)
        Assert.assertEquals(expectedDescription, updateInputValue!!.description)
        Assert.assertEquals(expectedType, updateInputValue!!.type)

    }

    @Test
    fun deleteHabit() = test {
        val originalHabit = Habit(
            "title",
            "description",
            Periodicity(1, 2),
            Type.GOOD,
            Priority.HIGH
        )
        editorViewModel.setCard(originalHabit)

        editorViewModel.onDelete()

        Assert.assertNotNull(deleteInputValue)
        Assert.assertEquals(originalHabit.title, deleteInputValue!!.title)
    }
}