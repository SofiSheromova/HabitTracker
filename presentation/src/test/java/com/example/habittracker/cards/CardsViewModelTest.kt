package com.example.habittracker.cards

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import com.example.domain.model.Habit
import com.example.domain.model.Type
import com.example.domain.repository.HabitRepository
import com.example.domain.usecase.*
import com.example.habittracker.model.DisplayOptions
import com.example.habittracker.model.HabitFulfillmentReportFormatter
import com.example.habittracker.ui.cards.CardsViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.withContext
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mock
import org.mockito.junit.MockitoJUnitRunner

@RunWith(MockitoJUnitRunner::class)
class CardsViewModelTest {
    @get:Rule
    val rule = InstantTaskExecutorRule()

    private var markHabitInputValue: Pair<Habit?, Long?> = Pair(null, null)
    private val habits: MutableList<Habit> = mutableListOf()

    private val habitRepository = object : HabitRepository {
        override val allHabits: Flow<List<Habit>>
            get() = flowOf(habits)

        override suspend fun start() {}

        override suspend fun refresh(): Boolean = true

        override suspend fun insert(habit: Habit) {}

        override suspend fun update(habit: Habit) {}

        override suspend fun delete(habit: Habit) {}

        override suspend fun markDone(habit: Habit, time: Long) {
            markHabitInputValue = Pair(habit, time)
        }
    }

    private lateinit var cardsViewModel: CardsViewModel

    private val displayOptions: DisplayOptions = DisplayOptions({ it.type == Type.GOOD })
    private val getAllHabitsUseCase: GetAllHabitsUseCase = GetAllHabitsUseCase(habitRepository)
    private val refreshHabitsUseCase: RefreshHabitsUseCase = RefreshHabitsUseCase(habitRepository)
    private val markHabitDoneUseCase: MarkHabitDoneUseCase = MarkHabitDoneUseCase(habitRepository)
    private val latestDoneDatesHabitUseCase: LatestDoneDatesHabitUseCase =
        LatestDoneDatesHabitUseCase()
    private val reportUseCase: HabitFulfillmentReportUseCase =
        HabitFulfillmentReportUseCase(latestDoneDatesHabitUseCase)

    @Mock
    lateinit var reportFormatter: HabitFulfillmentReportFormatter

    @Before
    fun setUp() {
        runBlocking {
            markHabitInputValue = Pair(null, null)

            withContext(Dispatchers.Main) {
                cardsViewModel = CardsViewModel(
                    displayOptions,
                    getAllHabitsUseCase,
                    refreshHabitsUseCase,
                    markHabitDoneUseCase,
                    reportUseCase,
                    reportFormatter
                )
                cardsViewModel.habitsLiveData.observeForever { }
            }
        }
    }

    @Test
    fun checkMarkDone() {
        val habit = Habit()

        runBlocking {
            val job = cardsViewModel.markDone(habit)
            job.join()

            assertEquals(1, habit.doneDates.size)
            assertEquals(markHabitInputValue.first, habit)
        }
    }
}