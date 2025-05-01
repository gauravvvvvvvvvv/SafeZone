package com.example.safezone.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import androidx.fragment.app.Fragment
import com.example.safezone.R
import com.example.safezone.models.User
import com.example.safezone.utils.AuthHelper

class ProfileFragment : Fragment() {
    private lateinit var nameEditText: EditText
    private lateinit var emailEditText: EditText
    private lateinit var contactsEditText: EditText
    private lateinit var saveButton: Button
    private lateinit var logoutButton: Button

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_profile, container, false)
        nameEditText = view.findViewById(R.id.edit_name)
        emailEditText = view.findViewById(R.id.edit_email)
        contactsEditText = view.findViewById(R.id.edit_contacts)
        saveButton = view.findViewById(R.id.btn_save)
        logoutButton = view.findViewById(R.id.btn_logout)

        val user = AuthHelper.getUser(requireContext())
        user?.let {
            nameEditText.setText(it.name)
            emailEditText.setText(it.email)
            contactsEditText.setText(it.emergencyContacts.joinToString(", "))
        }

        saveButton.setOnClickListener {
            val name = nameEditText.text.toString()
            val email = emailEditText.text.toString()
            val contacts = contactsEditText.text.toString().split(",").map { c -> c.trim() }
            val password = user?.password ?: ""
            val updatedUser = User(name, email, contacts, password)
            AuthHelper.saveUser(requireContext(), updatedUser)
            showToast("Profile updated!")
        }

        logoutButton.setOnClickListener {
            AuthHelper.logout(requireContext())
            requireActivity().recreate()
        }

        return view
    }

    private fun showToast(msg: String) {
        android.widget.Toast.makeText(requireContext(), msg, android.widget.Toast.LENGTH_SHORT).show()
    }
}
