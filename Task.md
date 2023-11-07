# Task "ActiveMQ Chat App"

You will be using ActiveMQ to create a chat application. The application will have a server and multiple clients. The server will be responsible for receiving messages from clients and sending them to clients. The client will be responsible for sending messages to the server and receiving messages from the server.

You should work in groups of 2–3 people.

## Server

Use the provided docker compose file to start the server. The server should be listening on port 61616. 

```bash
docker-compose up -d
```

## Client

The client should be able to send messages to the server and receive messages from the server. It should be possible to select one specific other client to send messages to as well as sending messages to all clients.

You are free to decide if you want to create a console application or a GUI application. For creating a GUI application, you can use the JavaFX library or use Compose Desktop with Kotlin.

## Bonus

- Create a GUI application with a framework of your choice.
- Add online notifications when a client connects (or disconnects) to the server.
