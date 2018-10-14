# PopularMovies
This project was done as part of the Google Nanodegree scholarship. The app presents users with movies from TMDb Api. Users can sort movies by popularity as well as ratings
# TMDb Api
The movies database api is used to fetch movies. An api key is required before fetching movies. The api key can be obtained by creating an account at TMDb Api and then
requesting for a key.
# Implementation
The project is done in accordance with the official Android app design. Data is obtained through a data repository class.
Viewmodel class holds data and logic while UI's main job is to display data and to interact with the user
# Images
Images are loaded using Picasso library
# Network
Retrofit is used to make network calls to the api
# Paging Library
This library is used to load data in the background for a much better user experience. Data pagination is achieved using this library
# Room Persistence Library
It is used to save user chosen favourite movies which are then displayed without a network call
# Sorting Movies
A settings fragment is implemented to provide the two settings required: sorting movies by movie ratings, sorting by movie popularity and sorting by user favourite list
# Final Features
Now it is possible to choose your favourite movies which are then stored locally using Room persistency library. It is possible to view movie revies and trailers. The video links are sharable 
# Future Release
This is the final version of the popular movies project.
