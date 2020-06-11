class GameException(Exception):
    pass


class InvalidStepException(GameException):
    pass


class InsufficientPlayersException(GameException):
    pass


class UnknownClientException(GameException):
    def __init__(self, client_id: int):
        self.id = client_id

