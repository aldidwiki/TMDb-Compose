package com.aldiprahasta.tmdb.data.source.remote.network

import com.aldiprahasta.tmdb.data.source.remote.response.CreditResponse
import com.aldiprahasta.tmdb.data.source.remote.response.movie.MovieDetailResponse
import com.aldiprahasta.tmdb.data.source.remote.response.movie.MovieResponse
import com.aldiprahasta.tmdb.data.source.remote.response.person.PersonResponse
import com.aldiprahasta.tmdb.data.source.remote.response.tv.TvDetailResponse
import com.aldiprahasta.tmdb.data.source.remote.response.tv.TvResponse
import com.aldiprahasta.tmdb.data.source.remote.response.tv.TvSeasonResponse
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

interface RemoteService {
    @GET("movie/popular")
    suspend fun getPopularMovies(@Query("page") page: Int): Response<MovieResponse>

    @GET("movie/{movie_id}?append_to_response=release_dates,credits,external_ids,videos")
    suspend fun getMovieDetail(@Path("movie_id") movieId: Int): Response<MovieDetailResponse>

    @GET("person/{person_id}?append_to_response=external_ids,combined_credits")
    suspend fun getPersonDetail(@Path("person_id") personId: Int): Response<PersonResponse>

    @GET("movie/{movie_id}/credits")
    suspend fun getMovieCredits(@Path("movie_id") movieId: Int): Response<CreditResponse>

    @GET("person/{person_id}/combined_credits")
    suspend fun getPersonCredits(@Path("person_id") personId: Int): Response<CreditResponse>

    @GET("tv/popular")
    suspend fun getPopularTv(): Response<TvResponse>

    @GET("tv/{tv_id}?append_to_response=videos,external_ids,content_ratings,aggregate_credits")
    suspend fun getTvDetail(@Path("tv_id") tvId: Int): Response<TvDetailResponse>

    @GET("tv/{tv_id}/aggregate_credits")
    suspend fun getTvCredits(@Path("tv_id") tvId: Int): Response<CreditResponse>

    @GET("tv/{tv_id}/season/{season_number}")
    suspend fun getTvSeasonDetail(
            @Path("tv_id") tvId: Int,
            @Path("season_number") seasonNumber: Int
    ): Response<TvSeasonResponse>
}