# Blackjack Architecture

## Purpose
The purpose of this document is to outline the structure of the game logic of Blackjack and how it will be implemented with the abstract classes outlined in abstraction.md.

## Rules of Blackjack (Paraphrased from bicyclecards.com)
### Cards
* Generally, the game uses a standard 52 card deck of cards, however it is also common to use multiple decks so that there are duplicates. This deck is shuffled and a point is decided for when the deck will be reshuffled, some number of cards before the deck is empty.
* Face cards count as 10, Aces count as either 1 or 11 depending on the player's hand.

### Deal
* At the beginning of the round, before any cards are dealt, each player places a bet, generally between 2 and 500.
* Each player and the dealer are dealt two cards. Some variations have the player's cards face up so everyone can see them, but for two players and 52 cards, they should likely be hidden from eachother. The dealer always has one face up card every one can see, and one face down card.
* If a player is dealt a 10 card and an Ace, naturally adding to 21, it's called a natural. If a player is dealt a natural they get 1.5x their bet. If the dealer gets a natural, they immediately win, and if a player and the dealer both get a natural, they tie and the player just gets their bet back.

### Play
* Each player takes a turn deciding whether to HIT (ask for another card) or STAND (stay with the cards they were dealt). If a player hits they are dealt another card, and can continue to hit until deciding to stand (if the total of their cards is less than or equal to 21), or they "bust" (if they go over 21) in which case they lose their bet.
* When a player first has an ace it is counted as an 11, but if they HIT and an 11 would put them over 21, it is instead counted as 1, and they can continue hitting if they like.

### Dealer
* Once all players have played, the dealer reveals their face down card
* The dealer then plays based on their total. If their total is 17 or more, they must STAND. If it is 16 or under they must HIT until the total is 17 or more when they will STAND or have "bust"ed. If the dealer has an ace and an 11 would put the total between 17 and 21, it must be counted as an 11 and they must STAND.
* The dealer's turn is entirely automatic.

### Splitting Pairs
* If a player is dealt two cards of the same number, they can choose to SPLIT them and treat them as two separate hands. Their original bet then goes on one of the hands, and they must put an additional equal amount on the other hand.
* The player plays one hand to completion, then plays the other hand.
* If it is a pair of aces, the player is dealt a card for each hand and can not HIT from there.
* If a ten card is dealt to one of them, it is treated as a normal hand and not as a natural.

### Doubling Down
* If a player's original two cards total 9, 10, or 11, a player can DOUBLE DOWN. They double their bet and are dealt a single card that is revealed at the end of the game.
* If they have two 5s they have the option of splitting, doubling down, or playing normally.
* The dealer does not have the option to split or double down.

### Insurance
* If the dealer's face-up card is an ace, the players may choose to place a side bet of up to half their original bet. If the face down card is a 10 card, (the dealer has a natural), the players are paid double their side bet.

### Settlement
* If a player busts, they lose and the dealer takes their bet, even if the dealer also busts
* If the dealer busts, they lose and everyone who didn't bust is paid the amount of their bet.
* If the dealer does not bust, they pay out to anyone with a higher total (not over 21), and collect the bets of anyone lower.
* If there is a tie, nothing is exchanged.
* The cards are then discarded and another round is played. The deck is only restocked and reshuffled when it has reached the reshuffle point determined earlier.

## Architecture
*