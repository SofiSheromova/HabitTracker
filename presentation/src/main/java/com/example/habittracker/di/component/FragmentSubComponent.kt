package com.example.habittracker.di.component

import androidx.fragment.app.Fragment
import dagger.BindsInstance
import dagger.Subcomponent

@Subcomponent
interface FragmentSubComponent {
    @Subcomponent.Builder
    interface Builder {
        @BindsInstance
        fun with(fragment: Fragment): Builder
        fun build(): FragmentSubComponent
    }
}