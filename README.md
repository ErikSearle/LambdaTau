# LambdaTau
End-to-end encrypted messaging app with cli

Messages are sent with the following protocol:
First digit is the message type:
    0: message to the server
    1: message
    2: slash command, handled by the client internal
 Second digit is the ID of sender
String Command, if not command then ""
String arguments for the command, no command ""
String message
 Ex: 0 0 /msg jim hello
 0 is the message id
 0 is the ID
 /msg is the command
 jim is the argument
 hello is the message
