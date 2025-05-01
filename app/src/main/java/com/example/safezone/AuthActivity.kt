package com.example.safezone

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.textfield.TextInputEditText
import java.io.File
import org.xmlpull.v1.XmlSerializer
import android.util.Xml
import org.xmlpull.v1.XmlPullParserFactory

class AuthActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val prefs = getSharedPreferences("user_prefs", Context.MODE_PRIVATE)
        if (prefs.getBoolean("logged_in", false)) {
            startActivity(Intent(this, MainActivity::class.java))
            finish()
            return
        }
        setContentView(R.layout.activity_auth)

        val emailEditText = findViewById<TextInputEditText>(R.id.edit_email)
        val passwordEditText = findViewById<TextInputEditText>(R.id.edit_password)
        val signInButton = findViewById<Button>(R.id.btn_sign_in)
        val signUpButton = findViewById<Button>(R.id.btn_sign_up)

        signInButton.setOnClickListener {
            val email = emailEditText.text.toString()
            val password = passwordEditText.text.toString()
            if (checkCredentials(this, email, password)) {
                getSharedPreferences("user_prefs", Context.MODE_PRIVATE).edit()
                    .putBoolean("logged_in", true).apply()
                startActivity(Intent(this, MainActivity::class.java))
                finish()
            } else {
                showToast("Invalid credentials")
            }
        }

        signUpButton.setOnClickListener {
            val email = emailEditText.text.toString()
            val password = passwordEditText.text.toString()
            if (email.isBlank() || password.isBlank()) {
                showToast("Please enter email and password")
                return@setOnClickListener
            }
            saveCredentialsToXml(this, email, password)
            getSharedPreferences("user_prefs", Context.MODE_PRIVATE).edit()
                .putBoolean("logged_in", true).apply()
            startActivity(Intent(this, MainActivity::class.java))
            finish()
        }
    }

    private fun showToast(msg: String) {
        android.widget.Toast.makeText(this, msg, android.widget.Toast.LENGTH_SHORT).show()
    }

    private fun saveCredentialsToXml(context: Context, email: String, password: String) {
        val file = File(context.filesDir, "user_credentials.xml")
        val serializer: XmlSerializer = Xml.newSerializer()
        file.outputStream().use { fos ->
            serializer.setOutput(fos, "UTF-8")
            serializer.startDocument(null, true)
            serializer.startTag(null, "user")
            serializer.startTag(null, "email")
            serializer.text(email)
            serializer.endTag(null, "email")
            serializer.startTag(null, "password")
            serializer.text(password)
            serializer.endTag(null, "password")
            serializer.endTag(null, "user")
            serializer.endDocument()
            serializer.flush()
        }
    }

    private fun checkCredentials(context: Context, email: String, password: String): Boolean {
        val file = File(context.filesDir, "user_credentials.xml")
        if (!file.exists()) return false
        val factory = XmlPullParserFactory.newInstance()
        val parser = factory.newPullParser()
        file.inputStream().use { fis ->
            parser.setInput(fis, null)
            var eventType = parser.eventType
            var storedEmail = ""
            var storedPassword = ""
            while (eventType != org.xmlpull.v1.XmlPullParser.END_DOCUMENT) {
                if (eventType == org.xmlpull.v1.XmlPullParser.START_TAG) {
                    when (parser.name) {
                        "email" -> storedEmail = parser.nextText()
                        "password" -> storedPassword = parser.nextText()
                    }
                }
                eventType = parser.next()
            }
            return storedEmail == email && storedPassword == password
        }
    }
}
