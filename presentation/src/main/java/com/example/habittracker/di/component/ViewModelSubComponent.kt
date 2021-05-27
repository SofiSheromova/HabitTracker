package com.example.habittracker.di.component

import com.example.habittracker.ui.editor.EditorViewModel
import com.example.habittracker.ui.home.DisplayOptionsViewModel
import dagger.Subcomponent

@Subcomponent
interface ViewModelSubComponent {
    @Subcomponent.Builder
    interface Builder {
        fun build(): ViewModelSubComponent
    }

    fun inject(editorViewModel: EditorViewModel)
    fun inject(displayOptionsViewModel: DisplayOptionsViewModel)
}