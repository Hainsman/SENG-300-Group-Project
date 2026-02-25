@startuml
title 03 Game

hide empty members
skinparam classAttributeIconSize 0
skinparam linetype ortho

abstract Game <<abstract>> {
+gameState: GameState
-validator: RuleValidator
+createInitialState(players): GameState
+handleAction(PlayerAction: action)
+isGameOver(GameState: state)
}

abstract RuleValidator <<abstract>> {
+validateMove(GameState: state, PlayerAction: move): boolean
+getValidMoves(GameState: state)
}

class PlayerAction {
-player: Player
-move
}

abstract GameState {
-board: Array2D
-playerTurn: Player
-state: State
+applyAction(PlayerAction: action): boolean
}

enum Player {
PLAYER_1
PLAYER_2
EMPTY
}

enum State {
IN_PROGRESS
TIE
P1_WIN
P2_WIN
}

Game --> RuleValidator
Game --> GameState
Game o-- PlayerAction
GameState *-- State
GameState *-- Player


@enduml