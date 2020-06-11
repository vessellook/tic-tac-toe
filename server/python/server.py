from flask import Flask, jsonify, abort, request
from game_manager import ClientManager, State
from exceptions import InvalidStepException, InsufficientPlayersException

app = Flask(__name__)
manager = ClientManager()

@app.route('/')
def hello_world():
    return 'hello, world'


@app.errorhandler(400)
def invalid_data():
    return jsonify({'message-type': 'error', 'error': 'invalid data'}), 400


@app.errorhandler(404)
def not_found():
    return jsonify({'message-type': 'error', 'error': 'player with id not found'}), 404


@app.errorhandler(405)
def method_not_allowed():
    return jsonify({'message-type': 'error', 'error': 'method not allowed'}), 405


@app.route('/tic-tac-toe/api/v1.1/client-id/', methods=['GET', 'POST'])
def add_new_client():
    player_id = manager.add_new_client()
    return jsonify({'message-type': 'client-id', 'client-id': player_id}), 201


@app.route('/tic-tac-toe/api/v1.1/client-id/<client_id>/start-game/', methods=['GET', 'POST'])
def start_new_game(client_id):
    json = request.get_json(force=True)
    password = json.get('password')
    if not client_id.isnumeric():
        abort(400)
    client_id = int(client_id)
    if not manager.is_valid_client(client_id, password):
        abort(404)
    order = manager.get_player(client_id).order
    if order == 0:
        try:
            manager.start_game(client_id)
            order = manager.get_player(client_id).order
        except InsufficientPlayersException:
            return jsonify({'message-type': 'start-game', 'start-game': False})
    return jsonify({'message-type': 'start-game', 'start-game': True, "player-order": order}), 201


@app.route('/tic-tac-toe/api/v1.1/players/<client_id>/game/make-move/', methods=['POST'])
def make_move(client_id):
    json = request.get_json(force=True)
    password = json.get('password')
    step = json.get('step')
    col = json.get('col')
    row = json.get('row')
    if not client_id.isnumeric() or not step.isnumeric() or not col.isnumeric() or not row.isnumeric():
        abort(400)
    if not manager.is_valid_client(client_id, password):
        abort(404)
    client_id = int(client_id)
    col = int(col)
    row = int(row)
    try:
        manager.make_move(client_id, col, row)
    except InvalidStepException:
        return jsonify({'message-type': 'accept-step', 'accepted': False})
    return jsonify({'message-type': 'accept-step', 'accepted': True})


@app.route('/tic-tac-toe/api/v1.1/players/<player_id>/game/get-changes/', methods=['GET'])
def get_changes(player_id, step):
    json = request.get_json(force=True)
    password = json.get('password')
    if not player_id.isnumeric() or not step.isnumeric():
        abort(400)
    player_id = int(player_id)
    step = int(step)
    if not manager.is_valid_client(player_id, password) or not manager.is_client_playing(player_id):
        abort(404)

    player = manager.get_player(player_id)
    current_step = player.game.step
    if current_step == step:
        return jsonify({'message-type': 'game-changes', 'changed': False})
    state = player.game.state
    if state == State.DRAW:
        state = 'draw'
    elif state == State.WIN1:
        state = 'victory' if player.order == 1 else 'failure'
    elif state == State.WIN2:
        state = 'victory' if player.order == 2 else 'failure'
    else:
        state = 'common'
    return jsonify({'message-type': 'game-changes',
                    'changed': True,
                    'state': state,
                    'step': current_step,
                    'values': player.game.values})


if __name__ == '__main__':
    app.run(host="0.0.0.0", debug=True)

