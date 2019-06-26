# Assignment 2
The primary goal of this project was to add upon Assignment 1. This version of the app implemented Google Authentication, Google Firebase, and in-project SQL.
Also, notifications were implemented using broadcast receivers.

## Overview
The home page of this application was its own activity (TriviaGameActivity.java). This was different from Assignment 1. Once signed in, the user's profile image is displayed,
as well as their First Name and a Sign Out button. If the user is new, they are prompted to take a picture of themselves.

<p align="center">
  <img src="Assignment2/readme-images/home.PNG" width="250" />
</p>

The application was an extension of Assignment 1. It has SIX main activities:
1. [MainActivity.java](#google-sign-in)
2. [TriviaGameActivity.java](#overview)
2. [PlayActivity.java](#play)
3. [AddWordActivity.java](#add-word)
4. [ScoreActivity.java](#score-history)
5. [TopTenActivity.java](#top-10-players)

## Google Sign In
In order to play the game, you must have a valid Google account and are forced to sign in. The user information is authenticated by Google Firebase.

<p align="center">
  <img src="Assignment2/readme-images/signIn.PNG" width="250" />
  <img src="Assignment2/readme-images/google.PNG" width="250" />
</p>

## Play
The user plays the game by answering 5 trivia vocabulary questions. As the questions are answered, the progress bar fills out appropriately. If the answer is correct, the user's score increases by 1.
This is the same as Assignment 1. However, on the home page you will see a [text to speech](#overview) switch. When enabled, the vocabulary words are read aloud to the user.
<p align="center">
  <img src="Assignment1/readme-images/play.PNG" width="250" />
</p>

## Add Word
The user has the capability to add a word to the vocab database. They can add a term and definition, and it will add right into the list of vocab words.
This functionality is the same as Assignment 1, however it displays as a pop-up Modal, instead of a full Activity screen.

<p align="center">
  <img src="Assignment2/readme-images/addWord.PNG" width="250" />
</p>

## Score History
After each game, the user's score history is stored with a timestamp and total score. The user's personal record for highest score is calculated against all other scores, and displayed.

<p align="center">
  <img src="Assignment2/readme-images/scores.PNG" width="250" />
</p>

## Top 10 Players
The top 10 performers by score are calculated and stored in order.

<p align="center">
  <img src="Assignment2/readme-images/topTen.PNG" width="250" />
</p>
