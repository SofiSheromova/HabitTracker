package com.example.habittracker.editor

import androidx.navigation.findNavController
import androidx.test.core.app.ActivityScenario
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.example.habittracker.MainActivity
import com.example.habittracker.R
import com.example.habittracker.screen.EditorScreen
import com.example.habittracker.ui.editor.EditorFields
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith


@RunWith(AndroidJUnit4::class)
class EditorFragmentTest {
    private val screen = EditorScreen()

    @Before
    fun setUp() {
        ActivityScenario.launch(MainActivity::class.java)
            .onActivity {
                val navController = it.findNavController(R.id.nav_host_fragment)
                // TODO а можно как-то установить фрагменту данные?
                //val navHostFragment = it
                //    .supportFragmentManager
                //    .findFragmentById(R.id.nav_host_fragment) as NavHostFragment
                //
                //val homeFragment = navHostFragment
                //    .childFragmentManager
                //    .fragments[0]

                navController.navigate(R.id.action_nav_home_to_nav_editor)
            }
    }

    @Test
    fun deleteButtonDisabled() {
        screen {
            deleteButton.isDisabled()
        }
    }

    @Test
    fun submitButtonDisabledWithEmptyTitle() {
        screen {
            editTitle.replaceText("")
            submitButton.isDisabled()
        }
    }

    @Test
    fun submitButtonDisabledWithEmptyDescription() {
        screen {
            editDescription.replaceText("")
            submitButton.isDisabled()
        }
    }

    @Test
    fun submitButtonEnabledWithCorrectData() {
        screen {
            editTitle.replaceText("title")
            editDescription.replaceText("description")
            editRepetitionsNumber.replaceText("2")
            editDaysNumber.replaceText("3")
            submitButton.isEnabled()
        }
    }

    @Test
    fun submitButtonDisabledWithTooLongTitle() {
        screen {
            editTitle.replaceText("a".repeat(EditorFields.TITLE_MAX_LENGTH + 1))
            editDescription.replaceText("description")

            // TODO нельзя проверить ошибку
            //editTitle.matches { error("") }
            //editTitle.hasHint(getResourceString(R.string.error_too_long))

            submitButton.isDisabled()
        }
    }

    @Test
    fun submitButtonDisabledWithTooLongDescription() {
        screen {
            editDescription.replaceText("a".repeat(EditorFields.DESCRIPTION_MAX_LENGTH + 1))

            submitButton.isDisabled()
        }
    }

    @Test
    fun submitButtonDisabledWithIncorrectDaysNumber() {
        screen {
            editDaysNumber.replaceText("")
            editTitle.replaceText("title")
            editDescription.replaceText("description")

            submitButton.isDisabled()
        }
    }

    @Test
    fun submitButtonDisabledWithIncorrectRepetitionsNumber() {
        screen {
            editRepetitionsNumber.replaceText("")
            editTitle.replaceText("title")
            editDescription.replaceText("description")

            submitButton.isDisabled()
        }
    }

    @Test
    fun checkNumberOfPriorities() {
        screen {
            assert(prioritySpinner.itemTypes.size == 3)
        }
    }

    @Test
    fun typeButtonsIsClicable() {
        screen {
            goodTypeButton.isEnabled()
            goodTypeButton.isClickable()
            badTypeButton.isEnabled()
            badTypeButton.isClickable()
        }
    }
}