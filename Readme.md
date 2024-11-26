# Secure Chat Application

## Overview
The Secure Chat Application is a real-time messaging system built with Java. It utilizes socket programming for communication and employs AES and RSA encryption protocols to secure the data transmitted between clients. The application is designed to work on a local network and provides an intuitive graphical user interface for seamless interaction.

## Features
- Secure messaging with AES and RSA encryption.
- Multi-threaded server to handle multiple clients simultaneously.
- User-friendly interface for chat management.
- Real-time user selection for communication.

## Prerequisites
- Java Development Kit (JDK) installed.
- All clients and the server must be connected to the same local network.

## How to Run

1. **Clone the repository**:
   ```bash
   git clone https://github.com/aurag-fouad/Secure-chat-application.git
   ```
2. **Navigate to the project directory**:
    ```bash
   cd Secure chat app
   ```
3. **Start the server**:
    - Compile the server code:
    ```bash
    javac Server.java
    ```
    - Run the server:
    ```bash
    java Server
    ```
4. **Start a client**:
    - Compile the client code:
    ```bash
    javac Login.java
    ```
    - Run the client:
    ```bash
    java Login
    ```
5. **Login**:
    - Enter a unique username.
    - Enter the IP address of the server (the same as the local network IP).

6. **Chat**:
    - A user-friendly interface will open.
    - Select a user from the list on the right to start a chat.
    - Send messages. The other user must select your name to reply.

## Notes:
- Each client must use a unique username.
- All users must be on the same network to communicate.

## Example Usage:
1. Start the server:
    ```bash
    java Server
    ```
2. Start a client, enter a unique name and the server's IP:
    ```bash
    java Login
    ```
3. Select a user from the chat interface and send messages securely!

## Technologies Used
- Java: Programming language.
- Sockets: For real-time communication.
- Multithreading: To handle multiple clients.
- AES and RSA: Encryption for secure messaging.

## License
This project is licensed under the MIT License.
