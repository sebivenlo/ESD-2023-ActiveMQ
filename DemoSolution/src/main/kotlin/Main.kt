import androidx.compose.desktop.ui.tooling.preview.Preview
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Send
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SmallTopAppBar
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Window
import androidx.compose.ui.window.application
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import org.apache.activemq.artemis.api.core.ActiveMQException
import org.apache.activemq.artemis.api.core.QueueConfiguration
import org.apache.activemq.artemis.api.core.RoutingType
import org.apache.activemq.artemis.api.core.client.ActiveMQClient
import org.apache.activemq.artemis.api.core.client.ClientMessage
import org.apache.activemq.artemis.api.core.client.ClientProducer
import org.apache.activemq.artemis.api.core.client.ServerLocator
import org.apache.activemq.artemis.utils.UUID

data class Message(val sender: String, val text: String)

val uuid = UUID(java.util.UUID.randomUUID())
val uuid2 = UUID(java.util.UUID.randomUUID())
val uuid3 = UUID(java.util.UUID.randomUUID())

@OptIn(ExperimentalMaterial3Api::class)
@Composable
@Preview
fun App() {
    val messages = mutableStateListOf<Message>(
//        Message(uuid.toString(), "Hello, World!"),
//        Message(uuid.toString(), "Hello, World!"),
//        Message(uuid3.toString(), "Hello, World!"),
//        Message(uuid2.toString(), "Hello, World!"),
//        Message(uuid2.toString(), "Hello, World!"),
    )

    val locator: ServerLocator =
        ActiveMQClient.createServerLocator("tcp://localhost:61616")

    // In this simple example, we just use one session for both producing and receiving
    val factory = locator.createSessionFactory()
    val session = factory.createSession("artemis", "artemis", true, true, true, true, 1)

    // A producer is associated with an address ...
    val producer: ClientProducer = session.createProducer("chat")

    val lazyListState = rememberLazyListState()
    val scope = rememberCoroutineScope()

    LaunchedEffect(Unit) {

        // We need a queue attached to the address ...
        try {
            val queueConfiguration = QueueConfiguration()
            queueConfiguration.setAddress("chat")
            queueConfiguration.setRoutingType(RoutingType.MULTICAST)
            queueConfiguration.setName(uuid.toString())
            queueConfiguration.setDurable(true)
            session.createQueue(queueConfiguration)
        } catch (e: ActiveMQException) {
        }

        // And a consumer attached to the queue ...
        val consumer = session.createConsumer(uuid.toString())

        // We need to start the session before we can -receive- messages ...

        // We need to start the session before we can -receive- messages ...
        consumer.setMessageHandler { message: ClientMessage ->
            val string = message.bodyBuffer.readString()
            println(string)
            messages.add(Message(message.getStringProperty("user"), string))
            scope.launch {
                println("Scrolling to bottom")
                lazyListState.animateScrollToItem(lazyListState.layoutInfo.totalItemsCount)
            }
        }
        session.start()
    }

//    LaunchedEffect(messages.size){
//        println("Scrolling to bottom")
//        lazyListState.animateScrollToItem(lazyListState.layoutInfo.totalItemsCount)
//    }

    var newMessage by remember { mutableStateOf("") }
    MaterialTheme() {
        Scaffold(topBar = {
            SmallTopAppBar(title = { Text("ActiveMQ Demo Chat App") })
        }, bottomBar = {
            Row(modifier = Modifier.padding(8.dp), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                OutlinedTextField(newMessage, onValueChange = { newMessage = it }, modifier = Modifier.weight(1f))
                IconButton(onClick = {
                    val message = session.createMessage(true)
                    message.bodyBuffer.writeString(newMessage)
                    message.putStringProperty("user", uuid.toString())
                    producer.send(message)
                    newMessage = ""
                }) {
                    Icon(Icons.Default.Send, contentDescription = "Send")
                }
            }
        }) {
            LazyColumn(modifier = Modifier.padding(it).fillMaxWidth(), state = lazyListState) {
                items(messages) { message ->
                    val alignment = if (message.sender == uuid.toString()) {
                        Alignment.End
                    } else {
                        Alignment.Start
                    }
                    val arrangement = if (message.sender == uuid.toString()) {
                        Arrangement.End
                    } else {
                        Arrangement.Start
                    }
                    Column(horizontalAlignment = alignment) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.padding(horizontal = 16.dp).padding(top = 16.dp, bottom = 4.dp)
                        ) {
                            Icon(Icons.Default.Person, contentDescription = null, modifier = Modifier.size(16.dp))
                            Text(text = message.sender, style = MaterialTheme.typography.bodySmall)
                        }
                        Row(
                            horizontalArrangement = arrangement,
                            modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp)
                        ) {
                            Text(message.text)
                        }
                    }
                }
            }
        }

    }
}

fun main() = application {

    Window(onCloseRequest = ::exitApplication, title = "ActiveMQ Demo Chat App") {
        App()
    }
}
