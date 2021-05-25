package com.example.habittracker.ui.info

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.example.habittracker.HabitTrackerApplication
import com.example.habittracker.databinding.FragmentInfoBinding
import javax.inject.Inject

class InfoFragment : Fragment() {

    private lateinit var binding: FragmentInfoBinding

    private lateinit var infoViewModel: InfoViewModel

    @Inject
    lateinit var name: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        (requireActivity().application as HabitTrackerApplication).appComponent.inject(this)

        infoViewModel = ViewModelProvider(requireActivity())
            .get(InfoViewModel::class.java)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentInfoBinding.inflate(inflater, container, false)

        binding.textInfo.text = "${infoViewModel.text.value}\n Application name: $name"

        return binding.root
    }
}