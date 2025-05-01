package com.example.safezone

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import androidx.appcompat.app.AppCompatActivity
import com.example.safezone.utils.AuthHelper

class SignInActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_sign_in)

        val emailEditText = findViewById<EditText>(R.id.edit_email)
        val passwordEditText = findViewById<EditText>(R.id.edit_password)
        val signInButton = findViewById<Button>(R.id.btn_sign_in)

        signInButton.setOnClickListener {
            val email = emailEditText.text.toString()
            val password = passwordEditText.text.toString()
            val user = AuthHelper.getUser(this)
            if (user != null && user.email == email && user.password == password) {
                AuthHelper.saveUser(this, user) // Set logged in
                showToast("Sign in successful!")
                startActivity(Intent(this, MainActivity::class.java))
                finish()
            } else {
                showToast("Invalid credentials")
            }
        }
    }

    private fun showToast(msg: String) {
        android.widget.Toast.makeText(this, msg, android.widget.Toast.LENGTH_SHORT).show()
    }
}
