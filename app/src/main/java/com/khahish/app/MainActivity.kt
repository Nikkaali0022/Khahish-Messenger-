
package com.khahish.app

import android.Manifest
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.ContextCompat
import com.google.firebase.messaging.FirebaseMessaging

private val Green = Color(0xFF18A889)
private val Ink = Color(0xFF101A19)
private val Pale = Color(0xFFF4F8F6)
private val Muted = Color(0xFF788681)

class MainActivity : ComponentActivity() {

    private val notificationPermissionLauncher =
        registerForActivityResult(
            ActivityResultContracts.RequestPermission()
        ) { granted ->
            if (granted) {
                getFCMToken()
            }
        }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        requestNotificationPermission()

        setContent {
            KhahishApp()
        }
    }

    private fun requestNotificationPermission() {
        if (Build.VERSION.SDK_INT >= 33) {
            if (ContextCompat.checkSelfPermission(
                    this,
                    Manifest.permission.POST_NOTIFICATIONS
                ) == PackageManager.PERMISSION_GRANTED
            ) {
                getFCMToken()
            } else {
                notificationPermissionLauncher.launch(
                    Manifest.permission.POST_NOTIFICATIONS
                )
            }
        } else {
            getFCMToken()
        }
    }

    private fun getFCMToken() {
        FirebaseMessaging.getInstance().token
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    Log.d(
                        "KhahishFCM",
                        "FCM token received successfully"
                    )
                } else {
                    Log.e(
                        "KhahishFCM",
                        "FCM token failed",
                        task.exception
                    )
                }
            }
    }
}

data class Chat(
    val name: String,
    val message: String,
    val time: String,
    val unread: Int,
    val initials: String
)

private val demoChats = listOf(
    Chat("Areeba", "Theek hai, phir kal baat karte hain.", "10:42", 2, "A"),
    Chat("Family Group", "Ammi: Sab ghar pohanch gaye?", "09:18", 4, "F"),
    Chat("Hamza", "Voice message", "Yesterday", 0, "H"),
    Chat("Sana", "Photo", "Yesterday", 0, "S"),
    Chat("Study Circle", "Ali: Notes share kar diye hain", "Mon", 0, "SC")
)

@Composable
fun KhahishApp() {
    var tab by remember { mutableStateOf("Chats") }
    var selectedChat by remember { mutableStateOf<Chat?>(null) }

    MaterialTheme(
        colorScheme = lightColorScheme(
            primary = Green,
            background = Pale,
            surface = Color.White,
            onSurface = Ink
        )
    ) {
        if (selectedChat != null) {
            ConversationScreen(selectedChat!!) {
                selectedChat = null
            }
        } else {
            Scaffold(
                containerColor = Pale,
                topBar = {
                    Column(
                        Modifier
                            .background(Color.White)
                            .padding(horizontal = 20.dp, vertical = 14.dp)
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Box(
                                Modifier
                                    .size(42.dp)
                                    .background(Green, RoundedCornerShape(14.dp)),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(Icons.Default.Forum, null, tint = Color.White)
                            }

                            Spacer(Modifier.width(12.dp))

                            Column(Modifier.weight(1f)) {
                                Text(
                                    "Khahish",
                                    fontSize = 25.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Ink
                                )
                                Text(
                                    "Connect. Share. Stay Close.",
                                    fontSize = 11.sp,
                                    color = Muted
                                )
                            }

                            IconButton(onClick = { tab = "Settings" }) {
                                Icon(Icons.Default.Settings, "Settings")
                            }
                        }

                        Spacer(Modifier.height(18.dp))

                        Text(
                            if (tab == "Chats") "Messages" else tab,
                            fontSize = 23.sp,
                            fontWeight = FontWeight.Bold,
                            color = Ink
                        )
                    }
                },
                bottomBar = {
                    NavigationBar(containerColor = Color.White) {
                        listOf(
                            "Chats" to Icons.Default.Chat,
                            "Updates" to Icons.Default.DonutLarge,
                            "Calls" to Icons.Default.Call,
                            "Settings" to Icons.Default.Settings
                        ).forEach { (label, icon) ->
                            NavigationBarItem(
                                selected = tab == label,
                                onClick = { tab = label },
                                icon = { Icon(icon, label) },
                                label = { Text(label) }
                            )
                        }
                    }
                },
                floatingActionButton = {
                    if (tab == "Chats") {
                        FloatingActionButton(
                            onClick = { tab = "New chat" },
                            containerColor = Green,
                            contentColor = Color.White
                        ) {
                            Icon(Icons.Default.Edit, "New chat")
                        }
                    }
                }
            ) { padding ->
                when (tab) {
                    "Chats" -> ChatList(Modifier.padding(padding)) {
                        selectedChat = it
                    }

                    "Settings" -> SettingsPage(Modifier.padding(padding))

                    else -> PlaceholderPage(Modifier.padding(padding), tab)
                }
            }
        }
    }
}

@Composable
fun ChatList(
    modifier: Modifier = Modifier,
    onOpen: (Chat) -> Unit
) {
    Column(modifier.fillMaxSize()) {
        OutlinedTextField(
            value = "",
            onValueChange = {},
            readOnly = true,
            placeholder = { Text("Search conversations") },
            leadingIcon = { Icon(Icons.Default.Search, null) },
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            shape = RoundedCornerShape(18.dp)
        )

        LazyColumn(contentPadding = PaddingValues(bottom = 90.dp)) {
            items(demoChats) { chat ->
                Row(
                    Modifier
                        .fillMaxWidth()
                        .clickable { onOpen(chat) }
                        .padding(horizontal = 18.dp, vertical = 13.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        Modifier
                            .size(52.dp)
                            .background(Color(0xFFDCEFE9), CircleShape),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            chat.initials,
                            color = Green,
                            fontWeight = FontWeight.Bold,
                            fontSize = 18.sp
                        )
                    }

                    Spacer(Modifier.width(13.dp))

                    Column(Modifier.weight(1f)) {
                        Text(
                            chat.name,
                            fontWeight = FontWeight.SemiBold,
                            color = Ink
                        )
                        Spacer(Modifier.height(5.dp))
                        Text(
                            chat.message,
                            color = Muted,
                            fontSize = 12.sp,
                            maxLines = 1
                        )
                    }

                    Column(horizontalAlignment = Alignment.End) {
                        Text(chat.time, color = Muted, fontSize = 10.sp)

                        if (chat.unread > 0) {
                            Spacer(Modifier.height(7.dp))
                            Box(
                                Modifier
                                    .size(21.dp)
                                    .background(Green, CircleShape),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    "${chat.unread}",
                                    color = Color.White,
                                    fontSize = 10.sp
                                )
                            }
                        }
                    }
                }

                HorizontalDivider(
                    color = Color(0xFFEAF0ED),
                    modifier = Modifier.padding(start = 82.dp)
                )
            }
        }
    }
}

@Composable
fun ConversationScreen(chat: Chat, onBack: () -> Unit) {
    var text by remember { mutableStateOf("") }

    Column(Modifier.fillMaxSize().background(Pale)) {
        Row(
            Modifier
                .fillMaxWidth()
                .background(Color.White)
                .padding(8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(onClick = onBack) {
                Icon(Icons.Default.ArrowBack, "Back")
            }

            Text(
                chat.name,
                Modifier.weight(1f),
                fontWeight = FontWeight.SemiBold
            )

            IconButton(onClick = {}) {
                Icon(Icons.Default.Call, "Voice call", tint = Green)
            }

            IconButton(onClick = {}) {
                Icon(Icons.Default.Videocam, "Video call", tint = Green)
            }
        }

        Box(
            Modifier.weight(1f).fillMaxWidth(),
            contentAlignment = Alignment.Center
        ) {
            Text(
                "Khahish conversations will appear here.",
                color = Muted
            )
        }

        Row(
            Modifier
                .fillMaxWidth()
                .background(Color.White)
                .padding(8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(onClick = {}) {
                Icon(Icons.Default.AttachFile, "Attach")
            }

            OutlinedTextField(
                value = text,
                onValueChange = { text = it },
                modifier = Modifier.weight(1f),
                placeholder = { Text("Message Khahish...") },
                shape = RoundedCornerShape(24.dp),
                singleLine = true
            )

            IconButton(onClick = { text = "" }) {
                Icon(Icons.Default.Send, "Send", tint = Green)
            }
        }
    }
}

@Composable
fun PlaceholderPage(modifier: Modifier, title: String) {
    Column(
        modifier
            .fillMaxSize()
            .padding(28.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Icon(
            Icons.Default.AutoAwesome,
            null,
            tint = Green,
            modifier = Modifier.size(42.dp)
        )

        Spacer(Modifier.height(14.dp))

        Text(
            title,
            fontWeight = FontWeight.Bold,
            fontSize = 20.sp,
            color = Ink
        )

        Text(
            "This section will be connected in a later development phase.",
            color = Muted
        )
    }
}

@Composable
fun SettingsPage(modifier: Modifier) {
    Column(modifier.fillMaxSize().padding(18.dp)) {
        listOf(
            Icons.Default.Person to "Profile",
            Icons.Default.Lock to "Privacy and security",
            Icons.Default.Notifications to "Notifications",
            Icons.Default.Storage to "Storage and data",
            Icons.Default.Help to "Help and support"
        ).forEach { (icon, label) ->
            Row(
                Modifier
                    .fillMaxWidth()
                    .background(Color.White, RoundedCornerShape(16.dp))
                    .padding(16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(icon, null, tint = Green)
                Spacer(Modifier.width(14.dp))
                Text(label, color = Ink)
            }

            Spacer(Modifier.height(9.dp))
        }
    }
}
