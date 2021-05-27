package com.example.habittracker.di.module

import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.example.habittracker.ui.editor.SimpleViewModel
import dagger.BindsInstance
import dagger.Module
import dagger.Provides
import dagger.Subcomponent


@Module
class FragmentModule {
//    @Provides
//    fun provideEditorViewModel(fragment: Fragment): EditorViewModel {
//        return ViewModelProvider(fragment).get(EditorViewModel::class.java)
//    }

    @Provides
    fun provideSimpleViewModel(fragment: Fragment): SimpleViewModel {
        return ViewModelProvider(fragment).get(SimpleViewModel::class.java)
    }
}

@Subcomponent(modules = [FragmentModule::class])
interface FragmentSubComponent {
    @Subcomponent.Builder
    interface Builder {
        @BindsInstance
        fun with(fragment: Fragment): Builder
        fun build(): FragmentSubComponent
    }
}

@Subcomponent
interface ViewModelSubComponent {
    @Subcomponent.Builder
    interface Builder {
        fun build(): ViewModelSubComponent
    }

    fun inject(simpleViewModel: SimpleViewModel)
}