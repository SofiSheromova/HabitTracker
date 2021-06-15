package com.example.habittracker.editor

import androidx.test.espresso.Espresso
import androidx.test.espresso.matcher.ViewMatchers.*
import androidx.test.ext.junit.rules.ActivityScenarioRule
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.filters.LargeTest
import com.example.habittracker.MainActivity
import com.example.habittracker.R
import com.example.habittracker.screen.EditorScreen
import com.example.habittracker.screen.HomeScreen
import com.example.habittracker.ui.editor.EditorFields
import io.github.kakaocup.kakao.common.utilities.getResourceString
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith


@LargeTest
@RunWith(AndroidJUnit4::class)
class EditorFragmentTest {
    @Rule
    @JvmField
    val rule = ActivityScenarioRule(MainActivity::class.java)

    private val screen = EditorScreen()
    private val homeScreen = HomeScreen()

    @Before
    fun setUp() {
        homeScreen {
            cardCreateButton.click()
        }
    }

    @Test
    fun checkEmptyFieldsAndDisabledButtonsOnStartup() {
        screen {
            editTitle.hasEmptyText()
            editDescription.hasEmptyText()
            goodTypeButton.matches { isChecked() }
            badTypeButton.matches { isNotChecked() }
            editRepetitionsNumber.hasText("1")
            editDaysNumber.hasText("1")
            deleteButton.isDisabled()
            submitButton.isDisabled()
        }
    }

    @Test
    fun successfulSendingWithCorrectData() {
        screen {
            editTitle.click()
            editTitle.replaceText("title")
            Espresso.closeSoftKeyboard()

            editDescription.click()
            editDescription.replaceText("description")
            Espresso.closeSoftKeyboard()

            editRepetitionsNumber.click()
            editRepetitionsNumber.replaceText("2")
            Espresso.closeSoftKeyboard()

            editDaysNumber.click()
            editDaysNumber.replaceText("3")
            Espresso.closeSoftKeyboard()

            submitButton.isEnabled()
        }
    }

    @Test
    fun submitButtonDisabledWithEmpty() {
        screen {
            editTitle.click()
            editTitle.replaceText("")
            Espresso.closeSoftKeyboard()

            editDescription.click()
            editDescription.replaceText("description")
            Espresso.closeSoftKeyboard()

            submitButton.isDisabled()
        }
    }

    @Test
    fun errorShownAndSubmitButtonDisabledWithTooLongTitle() {
        screen {
            editTitle.click()
            editTitle.replaceText("a".repeat(EditorFields.TITLE_MAX_LENGTH + 1))
            Espresso.closeSoftKeyboard()

            editDescription.click()
            editDescription.replaceText("description")
            Espresso.closeSoftKeyboard()

            editTitle.matches { hasErrorText(getResourceString(R.string.error_too_long)) }
            submitButton.isDisabled()
        }
    }

    @Test
    fun submitButtonDisabledWitEmptyDescription() {
        screen {
            editTitle.click()
            editTitle.replaceText("title")
            Espresso.closeSoftKeyboard()

            editDescription.click()
            editDescription.replaceText("")
            Espresso.closeSoftKeyboard()

            editRepetitionsNumber.click()
            Espresso.closeSoftKeyboard()

            submitButton.isDisabled()
        }
    }

    @Test
    fun errorShownAndSubmitButtonDisabledWithTooLongDescription() {
        screen {
            editTitle.click()
            editTitle.replaceText("title")
            Espresso.closeSoftKeyboard()

            editDescription.click()
            editDescription.replaceText("a".repeat(EditorFields.DESCRIPTION_MAX_LENGTH + 1))
            Espresso.closeSoftKeyboard()

            editRepetitionsNumber.click()
            Espresso.closeSoftKeyboard()

            editDescription.matches { hasErrorText(getResourceString(R.string.error_too_long)) }
            submitButton.isDisabled()
        }
    }

    @Test
    fun errorShownAndSubmitButtonDisabledWithIncorrectDaysNumber() {
        screen {
            editTitle.click()
            editTitle.replaceText("title")
            Espresso.closeSoftKeyboard()

            editDescription.click()
            editDescription.replaceText("description")
            Espresso.closeSoftKeyboard()

            editDaysNumber.click()
            editDaysNumber.replaceText("")
            Espresso.closeSoftKeyboard()

            editRepetitionsNumber.click()

            editDaysNumber.matches { hasErrorText(getResourceString(R.string.invalid_value)) }
            submitButton.isDisabled()
        }
    }

    @Test
    fun submitButtonDisabledWithIncorrectRepetitionsNumber() {
        screen {
            editTitle.click()
            editTitle.replaceText("title")
            Espresso.closeSoftKeyboard()

            editDescription.click()
            editDescription.replaceText("description")
            Espresso.closeSoftKeyboard()

            editRepetitionsNumber.click()
            editRepetitionsNumber.replaceText("")
            Espresso.closeSoftKeyboard()

            submitButton.isDisabled()
        }
    }

    @Test
    fun checkNumberOfPriorities() {
        assert(screen.prioritySpinner.itemTypes.size == 3)
    }
}