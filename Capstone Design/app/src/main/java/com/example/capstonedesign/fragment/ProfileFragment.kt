package com.example.capstonedesign.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.RadioGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.example.capstonedesign.R

class ProfileFragment : Fragment() {

    private lateinit var nicknameEditText: EditText
    private lateinit var ageEditText: EditText
    private lateinit var genderRadioGroup: RadioGroup
    private lateinit var travelPreferenceEditText: EditText
    private lateinit var saveButton: Button

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_profile, container, false)

        nicknameEditText = view.findViewById(R.id.et_nickname)
        ageEditText = view.findViewById(R.id.et_age)
        genderRadioGroup = view.findViewById(R.id.radio_group_gender)
        travelPreferenceEditText = view.findViewById(R.id.et_travel_preference)
        saveButton = view.findViewById(R.id.btn_save)

        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        saveButton.setOnClickListener {
            saveProfile()
        }
    }

    private fun saveProfile() {
        val nickname = nicknameEditText.text.toString()
        val age = ageEditText.text.toString()
        val selectedGenderId = genderRadioGroup.checkedRadioButtonId
        val gender = when (selectedGenderId) {
            R.id.radio_male -> "Male"
            R.id.radio_female -> "Female"
            else -> "Other"
        }
        val travelPreference = travelPreferenceEditText.text.toString()

        // 프로필 저장 로직 (예: SharedPreferences 또는 데이터베이스 사용)
        Toast.makeText(requireContext(), "Profile saved", Toast.LENGTH_SHORT).show()
    }
}
