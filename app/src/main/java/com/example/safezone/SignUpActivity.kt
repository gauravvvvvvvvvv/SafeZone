package com.example.safezone

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import androidx.appcompat.app.AppCompatActivity
import com.example.safezone.models.User
import com.example.safezone.utils.AuthHelper

class SignUpActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_sign_up)

        val nameEditText = findViewById<EditText>(R.id.edit_name)
        val emailEditText = findViewById<EditText>(R.id.edit_email)
        val passwordEditText = findViewById<EditText>(R.id.edit_password)
        val contactsEditText = findViewById<EditText>(R.id.edit_contacts)
        val signUpButton = findViewById<Button>(R.id.btn_sign_up)

        signUpButton.setOnClickListener {
            val name = nameEditText.text.toString()
            val email = emailEditText.text.toString()
            val password = passwordEditText.text.toString()
            val contacts = contactsEditText.text.toString().split(",").map { it.trim() }

            if (name.isBlank() || email.isBlank() || password.isBlank()) {
                showToast("Please fill in all fields")
                return@setOnClickListener
            }

            val user = User(name, email, contacts, password)
            AuthHelper.saveUser(this, user)
            showToast("Sign up successful!")
            startActivity(Intent(this, MainActivity::class.java))
            finish()
        }
    }

    private fun showToast(msg: String) {
        android.widget.Toast.makeText(this, msg, android.widget.Toast.LENGTH_SHORT).show()
    }
}
