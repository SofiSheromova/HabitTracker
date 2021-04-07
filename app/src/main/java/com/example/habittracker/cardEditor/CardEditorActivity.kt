package com.example.habittracker.cardEditor

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.habittracker.R
import com.example.habittracker.databinding.ActivityCardEditorBinding


class CardEditorActivity : AppCompatActivity(), CardEditorFragment.OnFragmentSendDataListener {
    companion object {
        const val CARD_ID = "CARD_ID"
    }

    private lateinit var binding: ActivityCardEditorBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityCardEditorBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)

        if (savedInstanceState == null)
            addCardEditorFragment()
    }

    private fun addCardEditorFragment() {
        val extras = intent.extras

        val cardId: String? = extras?.getString(CARD_ID)

        supportFragmentManager.beginTransaction()
            .add(
                R.id.card_editor_fragment,
                CardEditorFragment.newInstance(cardId)
            )
            .commit()
    }

    override fun onFormSubmit() {
        setResult(RESULT_OK)
        finish()
    }
}