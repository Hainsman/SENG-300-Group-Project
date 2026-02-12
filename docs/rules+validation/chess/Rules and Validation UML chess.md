# Rules and Validation UML for chess game #

## About this Diagram ##

Very rough UML, heavily based on the examples of UMLs given

## PlantUML Editor Code ##

```uml
@startuml
title Rule/Validation (Chess)

hide empty members
skinparam classAttributeIconSize 0
skinparam linetype ortho

' Very much based off of examples given


note "Note:\nProbably would have a chess piece class\nand subclasses for each type of piece" as N_RULE

package "Chess" {
  class ChessGame  {
    - board: Board
    + startGame()
    + handleAction(action)
    + validateAction(action): boolean
    + getGameState()
    + isGameOver(): boolean
  }

  class Board
  class ChessPiece
  class HumanPlayer
  class Action
  class Position

  ChessGame o-- Board
  Board o-- ChessPiece
  HumanPlayer --> Action
  Action --> Position
}

N_RULE .. ChessPiece

@enduml
```
