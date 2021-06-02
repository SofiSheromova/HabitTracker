package com.example.habittracker.screen

import android.widget.ListView
import androidx.test.espresso.DataInteraction
import com.agoda.kakao.edit.KEditText
import com.agoda.kakao.list.KAdapterItem
import com.agoda.kakao.screen.Screen
import com.agoda.kakao.spinner.KSpinner
import com.agoda.kakao.text.KButton
import com.agoda.kakao.text.KTextView
import com.example.habittracker.R

class EditorScreen : Screen<EditorScreen>() {
    val editTitle = KEditText { withId(R.id.title_edit) }
    val editDescription = KEditText { withId(R.id.description_edit) }
    val goodTypeButton = KButton { withId(R.id.radio_good) }
    val badTypeButton = KButton { withId(R.id.radio_bad) }
    val prioritySpinner = KSpinner(
        builder = { isInstanceOf(ListView::class.java) },
        itemTypeBuilder = { itemType(::PriorityItem) },
    )
    val editRepetitionsNumber = KEditText { withId(R.id.repetitions_number_edit) }
    val editDaysNumber = KEditText { withId(R.id.days_number_edit) }
    val submitButton = KButton { withId(R.id.submit_button) }
    val deleteButton = KButton { withId(R.id.delete_button) }
}

class PriorityItem(interaction: DataInteraction) : KAdapterItem<PriorityItem>(interaction) {
    val text = KTextView(interaction) { withId(R.id.priority_spinner) }
}