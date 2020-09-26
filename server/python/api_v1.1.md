# description of tic-tac-toe api v1.1

## /tic-tac-toe/api/v1.1/client-id/
**methods**: POST
First of all, you need to connect to the server and get two integers: **client-id** and **password**
Response code 201. Response body contains json in format
{
    "message-type": "client-id",
    "client-id": 123,
    "password": 184
}

### Extra
client-id can became blocked if the client is not active for some time
If that case the client should make a request for another client-id

## /tic-tac-toe/api/v1.1/client-id/<client-id>/start-game/
**example** /tic-tac-toe/api/v1.1/client-id/123/start-game/
**methods**: POST
Body should contain json object with field **password** and valid integer value
{
    "password": 184
}
If the client is not playing now server tries to find opponent for client
If the client is playing now server responses message to his opponent and find another opponent for the client
So you should not make several request to that URL
Response code 201. Response body contains json in format
{
    "message-type": "start-game",
    "start-game": true,
    "player-order": 1
}
or
Response code 201. Response body contains json in format
{
    "message-type": "start-game",
    "start-game": false
}
}

## /tic-tac-toe/api/v1.1/players/<client-id>/game/make-move/
**example** /tic-tac-toe/api/v1.1/client-id/123/game/make-move/
**methods**: POST
Body should contain json object in format
{
    "password": 184,
    "step": 3,
    "col": 1,
    "row": 0
}
If the request do not create any mistakes, server response will contain json in format
{
    "message-type": "accept-move",
    "move-accepted": true
}
If the client is not playing now, server response will contain error
{
    "message-type": "error"
}
If the client sends invalid move to server, server response will contain error
{
    "message-type": "accept-move",
    "move-accepted": false
}

## /tic-tac-toe/api/v1.1/players/<player_id>/game/get-changes/
**example** /tic-tac-toe/api/v1.1/client-id/123/game/get-changes/
**methods**: POST
Body should contain json object in format
{
    "password": 184
}
If the client is not playing now, server response will contain error
{
    "message-type": "error"
}
server response will contain json in format
{
    "message-type": "game-changes",
    "changed": false
}
or
{
    "message-type": "game-changes",
    "changed": true,
    "step": 3,
    "values": [0, 1, 0, 0, 2, 1, 0, 0, 0]
}