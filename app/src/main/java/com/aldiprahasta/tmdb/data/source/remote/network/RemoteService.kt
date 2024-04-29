package com.aldiprahasta.tmdb.data.source.remote.network

import com.aldiprahasta.tmdb.data.source.remote.response.movie.MovieDetailResponse
import com.aldiprahasta.tmdb.data.source.remote.response.movie.MovieResponse
import com.aldiprahasta.tmdb.data.source.remote.response.person.PersonResponse
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Path

interface RemoteService {
    @GET("movie/popular")
    suspend fun getPopularMovies(): Response<MovieResponse>

    @GET("movie/{movie_id}?append_to_response=release_dates,credits,external_ids,videos")
    suspend fun getMovieDetail(@Path("movie_id") movieId: Int): Response<MovieDetailResponse>

    @GET("person/{person_id}?append_to_response=external_ids,combined_credits")
    suspend fun getPersonDetail(@Path("person_id") personId: Int): Response<PersonResponse>
}