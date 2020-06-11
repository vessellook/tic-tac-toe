from exceptions import InvalidStepException, InsufficientPlayersException, UnknownClientException
from enum import Enum
from time import time
from typing import Optional, List, Dict
from dataclasses import dataclass
from random import randrange


class State(Enum):
    DRAW = 0
    WIN1 = 1
    WIN2 = 2
    ELSE = -1


@dataclass
class Player:
    order: int = 0
    is_playing: bool = False
    game: Optional['Game'] = None

    def set_turn(self) -> None:
        pass


class BotPlayer(Player):
    def __init__(self, order: int):
        super(BotPlayer, self).__init__(order, True)


@dataclass
class Client:
    id: int
    password: int
    lastUpdate: float
    player: Optional[Player] = None

    def update(self) -> None:
        self.lastUpdate = time()

    def is_playing(self) -> bool:
        return self.player is not None and self.player.is_playing


class Game:
    player1: Player
    player2: Player
    dim: int
    values: List[int]
    step: int
    state: State = State.ELSE

    def __init__(self, player1: Player, player2: Player, dim: int = 3):
        self.player1 = player1
        player1.order = 1
        player1.is_playing = True
        player1.game = self
        self.player2 = player2
        player2.order = 2
        player2.game = self
        player2.is_playing = True
        self.dim = dim
        self.values = [0] * (dim * dim)
        self.step = 0

    def order(self) -> int:
        return self.step % 2 + 1
    #
    # def get_verification(self, player: Player, order: int) -> bool:
    #     if order == 1:
    #         return player == self.player1
    #     elif order == 2:
    #         return player == self.player2
    #     else:
    #         return False

    def disconnect_player(self, player: Player) -> None:
        if player == self.player1:
            self.player1 = BotPlayer(1)
        elif player == self.player2:
            self.player2 = BotPlayer(2)
        else:
            return
        player.is_playing = False
        player.order = 0
        player.game = None

    def get_wins(self, player_num: int) -> dict:
        cols = [0] * self.dim
        rows = [0] * self.dim
        diags = [0, 0]
        for num, value in enumerate(self.values):
            if value == player_num:
                cols[num % self.dim] += 1
                rows[num // self.dim] += 1
                if num // self.dim == num % self.dim:
                    diags[0] += 1
                if num // self.dim + num % self.dim == self.dim - 1:
                    diags[1] += 1
        return {'cols': cols, 'rows': rows, 'diags': diags}

    def _is_win(self, wins: dict) -> bool:
        for value in wins['cols']:
            if value == self.dim:
                return True
        for value in wins['rows']:
            if value == self.dim:
                return True
        for value in wins['diags']:
            if value == self.dim:
                return True
        return False

    def make_move(self, player: Player, col: int, row: int):
        index = col * self.dim + row
        if self.values[index] != 0:
            order = self.step % 2 + 1
            self.step += 1
            self.values[index] = order
            if self._is_win(self.get_wins(order)):
                self.state = State.WIN1 if order == 1 else State.WIN2
            else:
                if self.step == self.dim ** 2:  # if it is the final step
                    self.state = State.DRAW
            if order == 2:
                self.player1.set_turn()
            else:
                self.player2.set_turn()
        raise InvalidStepException


class ClientManager:
    clients: Dict[int, Client] = dict()
    waiting_clients: Dict[int, Client] = dict()

    def add_new_client(self) -> int:
        client = Client(len(self.clients), randrange(100), time())
        self.clients[client.id] = client
        self.waiting_clients[client.id] = client
        return client.id

    def remove_client(self, player_id: int) -> None:
        self.clients.pop(player_id)
        self.waiting_clients.pop(player_id)

    def is_valid_client(self, client_id: int, password: int) -> bool:
        client = self.clients.get(client_id)
        return client and client.password == password

    def is_client_playing(self, client_id: int) -> bool:
        return self.clients[client_id].is_playing()

    def start_game(self, client_id) -> id:
        if client_id in self.waiting_clients:
            if len(self.waiting_clients) < 2:
                raise InsufficientPlayersException
            first_client = self.waiting_clients.pop(client_id)
            _, second_client = self.waiting_clients.popitem()
            first_client.player = Player()
            second_client.player = Player()
            Game(first_client.player, second_client.player)
        elif client_id not in self.clients:
            raise UnknownClientException(client_id)
        else:
            if not self.waiting_clients:
                raise InsufficientPlayersException
            first_client = self.clients[client_id]
            first_client.player.game.disconnect_player(first_client.player)
            _, second_client = self.waiting_clients.popitem()
            second_client.player = Player()
            Game(first_client.player, second_client.player)
        return second_client.id

    def make_move(self, client_id: int, col: int, row: int) -> None:
        game = self.clients[client_id].player.game
        game.change_state(col, row)

    def get_game_state(self, client_id: int) -> State:
        return self.clients[client_id].player.game.state

    def get_game_step(self, client_id: int) -> int:
        return self.clients[client_id].player.game.step

    def get_player(self, client_id: int) -> Player:
        return self.clients[client_id].player

# class Game:
#     class State(Enum):
#         DRAW = 0
#         WIN1 = 1
#         WIN2 = 2
#         COMMON = 3
#
#     def __init__(self, player1: Player, player2: Player, dim: int = 3):
#         player1.game = self
#         player1.order = 1
#         player2.game = self
#         player2.order = 2
#         self.player_order = 1
#         self.dim = dim
#         self.dim2 = dim * dim
#         self.values = [0] * self.dim2
#         self.state = Game.State.COMMON
#         self.step = 0
#
#

#
#
# class GameManager:
