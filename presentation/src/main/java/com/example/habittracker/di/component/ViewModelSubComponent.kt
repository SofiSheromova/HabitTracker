package com.example.habittracker.di.component

import com.example.habittracker.ui.editor.EditorViewModel
import dagger.Subcomponent

@Subcomponent
interface ViewModelSubComponent {
    @Subcomponent.Builder
    interface Builder {
        fun build(): ViewModelSubComponent
    }

    fun inject(editorViewModel: EditorViewModel)
}