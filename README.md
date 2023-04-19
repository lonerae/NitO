# Nights in the Outskirts

## Table of Contents
1. [About](#about)
2. [Multiplayer](#multiplayer)
3. [Rules](#rules)
    - [General](#general)
    - [Alliances](#alliances)
    - [Endgame](#endgame)
    - [Roles](#roles)

## ABOUT

A social-deduction game for people gathered together in the same room. _Inspired by Mafia_.

Use your detective skills and your role's special abilities to find out who are the ones spreading terror during the Nights —_or be the one who spreads the terror_. With 2 Alliances competing for the City's dark Outskirts, use brain and muscles, cunning, bluff and rhetorics to find yourself safe from the Square's noose and the knifes of your Enemies. 

May your Alliance be victorious in this strategic race —_at least until the next Game begins_.

**OS:** Android  
**Players:** rec. 6+

## MULTIPLAYER

All devices should be connected to the same WiFi and have a copy of the app installed.  

The application uses a servel-client architecture based on the Kryonet library. The device of the player who creates the game becomes the server for the game and all other devices connect to it.

> In the off-chance that the Game is not discoverable by other devices in the network, create a mobile hotspot with the host device and have all other devices connect to it.

## RULES

### GENERAL

A group of people is gathered and randomly assigned a Role. The goal of the game is to eliminate the opposing Alliance using your skills of deduction and deception, as well as your Role's Ability.

Each game consists of:

- **the First Night**: all the Player names appear on screen. Only the Assassins see their names, as well as the names of their partners, in red.
- **the Days**: the group votes who they want to eliminate from the game. In case of ties, all Players with the same number of votes get eliminated.
- **the Day Resolutions**: the names of all the eliminated by vote Players are displayed.
- **the Nights**: the alive Players may choose to use their abilities if possible.
- **the Night Resolutions**: the names of all the murdered Players are displayed.
- **the Endgame**: the winning Alliance is announced along with a list of all the Players and their respective Roles.

For your first times playing, the suggested Role set for a balanced game is:

| Role | # |
| --- | --- |
| Civilian | 2 |
| Assassin | 2 |
| Hermit | 1 |
| Witch | 1 |

### ALLIANCES

As of now the two opposing Alliances are:

1. **the Agents of Order** (_Civilian, Hermit, Witch, Necromancer, 4th Civilian_)
2. **the Agents of Chaos**: (_Assassin_)

### ENDGAME

The game is considered finished when one of the following scenarios is fulfilled:

1. **the only remaining Players belong to the same Alliance** (_that Alliance wins_).
2. **all Assassins get eliminated** (_Order wins_).
3. **a Day begins and only two Players are alive** (i.e. an Agent of Chaos and an Agent of Order). If the Agent of Order is a **Civilian**, _Order wins_. In any other case, _Chaos wins_.
4. **a Day begins and no Player is alive** (_draw_).

### ROLES

| Role | Ability Name | Ability |
| ---- | ------------ | ------- |
| **CIVILIAN** | _Folk Hero_ | If a Day begins with only an Agent of Chaos and a Civilian, Order wins.|
|**ASSASSIN** | _Back Stab_ | Once every Night, the Assassins may choose to eliminate a single Player. |
| **HERMIT** | _Revelation_ | Only once in the game, during the Night, you can see whether a Player is an Agent of Chaos or not. |
| **WITCH** | _Potion Frenzy_ | Only once in the game, during the Night, you can choose to either kill a Player or protect them from death (for that Night only). |
| **NECROMANCER** | _Call of the Void_ | Only once in the game, during the Night, you can see the Role of a dead Player. |
| **4TH CIVILIAN** | _Promotion_ | Only once in the game, during the Night, you can get the Role of a Player who died during that Night (priority is given to Chaos Roles). In case no deaths happen, your Role remains the same. |

_[...constantly updated...]_