package com.example.safezone.fragments

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.ScrollView
import android.widget.TextView
import androidx.fragment.app.Fragment
import com.example.safezone.R
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import java.text.SimpleDateFormat
import java.util.*

class CommunityFragment : Fragment() {

    private lateinit var messageEditText: EditText
    private lateinit var sendButton: Button
    private lateinit var chatTextView: TextView
    private lateinit var scrollView: ScrollView

    private val PREFS_NAME = "community_chat_prefs"
    private val KEY_CHAT_MESSAGES = "chat_messages"
    private val gson = Gson()
    private var chatMessages: MutableList<ChatMessage> = mutableListOf()

    data class ChatMessage(
        val sender: String,
        val content: String,
        val timestamp: Long = System.currentTimeMillis()
    )
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_community, container, false)

        messageEditText = view.findViewById(R.id.edit_message)
        sendButton = view.findViewById(R.id.btn_send)
        chatTextView = view.findViewById(R.id.text_chat)
        scrollView = view.findViewById(R.id.scroll_chat)

        loadChatMessages()
        displayAllMessages()

        sendButton.setOnClickListener {
            val message = messageEditText.text.toString().trim()
            if (message.isNotEmpty()) {
                addMessage("You", message)
                messageEditText.text.clear()
            }
        }
        return view
    }

    private fun loadChatMessages() {
        val prefs = requireContext().getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        val json = prefs.getString(KEY_CHAT_MESSAGES, null)

        if (json != null) {
            try {
                val type = object : TypeToken<MutableList<ChatMessage>>() {}.type
                chatMessages = gson.fromJson(json, type)
            } catch (e: Exception) {
                try {
                    val stringType = object : TypeToken<MutableList<String>>() {}.type
                    val oldMessages: MutableList<String> = gson.fromJson(json, stringType)

                    chatMessages = mutableListOf()
                    val dateTimeRegex = Regex("\\[(\\d{2}:\\d{2})\\] (.*?): (.*)")

                    oldMessages.forEach { messageString ->
                        val matchResult = dateTimeRegex.find(messageString)
                        if (matchResult != null) {
                            val (time, sender, content) = matchResult.destructured
                            val chatMessage = ChatMessage(sender, content.trim())
                            chatMessages.add(chatMessage)
                        }
                    }

                    saveChatMessages()
                } catch (e2: Exception) {
                    chatMessages = mutableListOf()
                    e2.printStackTrace()
                }
            }
        } else {
            chatMessages = mutableListOf()
        }
    }

    private fun saveChatMessages() {
        val prefs = requireContext().getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        prefs.edit().putString(KEY_CHAT_MESSAGES, gson.toJson(chatMessages)).apply()
    }

    private fun displayAllMessages() {
        val formattedChat = StringBuilder()
        var lastDate: String? = null

        chatMessages.forEach { message ->
            val date = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault()).format(Date(message.timestamp))

            if (date != lastDate) {
                if (lastDate != null) {
                    formattedChat.append("\n")
                }
                formattedChat.append("-------------- $date --------------\n\n")
                lastDate = date
            }

            val time = SimpleDateFormat("HH:mm", Locale.getDefault()).format(Date(message.timestamp))
            formattedChat.append("[$time] ${message.sender}: ${message.content}\n\n")
        }

        chatTextView.text = formattedChat.toString()
        scrollView.post { scrollView.fullScroll(View.FOCUS_DOWN) }
    }

    private fun addMessage(sender: String, message: String) {
        val chatMessage = ChatMessage(sender, message)
        chatMessages.add(chatMessage)
        saveChatMessages()
        displayAllMessages()
    }
}