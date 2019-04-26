<h1>Ticket to Dave's Heart</h1>

<a href="https://www.codacy.com/app/Lucas-Kohorst/ticketToRide?utm_source=github.com&amp;utm_medium=referral&amp;utm_content=tickettodavesheart/ticketToRide&amp;utm_campaign=Badge_Grade"><img src="https://api.codacy.com/project/badge/Grade/2ac0aae70b3a43779d96e53e8b02921c" /></a>

<br><br>
<h1>Project Description</h1>
<p>Ticket to Dave’s Heart is a computerized version of the board game Ticket to Ride. The objective of the game is to build a railroad network on the USA map. The application supports multiple games, which can be created and joined in the lobby and multiplayer-play
    over a network. Additional features include an in-game chat.</p>

<h1>Contributers</h1>
<a href="https://github.com/bugryn-josh">Joshua Bugryn</a><br>
<a href="https://github.com/danielsause">Daniel Sause</a><br>
<a href="https://github.com/michaelvasile">Michael Vasile</a><br>
<a href="https://github.com/lucas-kohorst">Lucas Kohorst</a>

<br><br>
<h1>Version History</h1>
<strong>0.01</strong> Intial Release<br>
<strong>0.02</strong> Added the RMI Chat Server<br>
<strong>0.03</strong> Created GameDocumentation.md and edited to include draft image of game board<br>
<strong>0.04</strong> Added support for multiple game servers concurently, the user can either select active games or create a game<br>
<strong>0.05</strong> Limited each game to 4 players<br>
<strong>0.06</strong> Functional GameBoard GUI added<br>
<strong>0.07</strong> Started to add start and end of turn sequences<br>
<strong>0.07</strong> Calculated the longest path for the end of game<br>
<strong>0.07</strong> A gameboard in a single JFrame<br>

<br><br>
<h1>Todo</h1>
<p>☐ Add ticket and destination cards</p>
<p>☐ Calculate points for each player</p>
<p>☐ Add game logs</p>


<br><br>
<h1>Screenshots</h1>
<h3>Game Board</h3>
<img src="https://imgur.com/FnWNDkf.png" alt="Game Board" width="500px">
<h3>Game Lobby</h3>
<img src="https://imgur.com/BTrDF6N.png" alt="Game Lobby" width="500px">

<br><br>
<h1>Networking</h1>
<p>Remote Method Invocation (RMI) is the networking protocol that is used in this application. Below are the different servers and clients that are created.</p>
<ul>
    <li>A main <strong>server</strong> is intially started, this will serve as the place that all of the active games will be stored</li>
    <li>When a user creates a new game in the lobby the main server will be notifyed and then proceed to create a new <strong>game server</strong> this will be the place that all clients in a given game will connect to</li>
    <li>Clients will enter through the <strong>lobby</strong> which after choosing to either create or join a game will pass off to the <strong>game client</strong> which will connect tpo the appropriate game</li>
</ul>

<br><br>
<h1>Unresolved Issues</h1>
<p>☐ Limiting to four players per game</p>