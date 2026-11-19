package com.aldiprahasta.tmdb.di

import com.aldiprahasta.tmdb.domain.usecase.GetExploreContent
import com.aldiprahasta.tmdb.domain.usecase.GetMovieCredits
import com.aldiprahasta.tmdb.domain.usecase.GetMovieDetail
import com.aldiprahasta.tmdb.domain.usecase.GetMovieGenreList
import com.aldiprahasta.tmdb.domain.usecase.GetMovieImages
import com.aldiprahasta.tmdb.domain.usecase.GetMovieTrailers
import com.aldiprahasta.tmdb.domain.usecase.GetMoviesByGenre
import com.aldiprahasta.tmdb.domain.usecase.GetNowPlayingMovies
import com.aldiprahasta.tmdb.domain.usecase.GetPersonCredits
import com.aldiprahasta.tmdb.domain.usecase.GetPersonDetail
import com.aldiprahasta.tmdb.domain.usecase.GetPersonImages
import com.aldiprahasta.tmdb.domain.usecase.GetPopularMovieList
import com.aldiprahasta.tmdb.domain.usecase.GetPopularTv
import com.aldiprahasta.tmdb.domain.usecase.GetSearchResult
import com.aldiprahasta.tmdb.domain.usecase.GetSimilarContent
import com.aldiprahasta.tmdb.domain.usecase.GetTrendingMovies
import com.aldiprahasta.tmdb.domain.usecase.GetTvCredits
import com.aldiprahasta.tmdb.domain.usecase.GetTvDetail
import com.aldiprahasta.tmdb.domain.usecase.GetTvGenreList
import com.aldiprahasta.tmdb.domain.usecase.GetTvImages
import com.aldiprahasta.tmdb.domain.usecase.GetTvSeasonDetail
import com.aldiprahasta.tmdb.domain.usecase.favorite.DeleteFavorite
import com.aldiprahasta.tmdb.domain.usecase.favorite.GetAllFavoriteList
import com.aldiprahasta.tmdb.domain.usecase.favorite.GetFavoriteStatus
import com.aldiprahasta.tmdb.domain.usecase.favorite.InsertFavorite
import com.aldiprahasta.tmdb.domain.usecase.wrapper.CreditWrapper
import com.aldiprahasta.tmdb.domain.usecase.wrapper.DetailWrapper
import com.aldiprahasta.tmdb.domain.usecase.wrapper.FavoriteWrapper
import com.aldiprahasta.tmdb.domain.usecase.wrapper.PersonDetailWrapper
import com.aldiprahasta.tmdb.domain.usecase.wrapper.TvWrapper
import org.koin.core.module.dsl.factoryOf
import org.koin.dsl.module

private val interactionModule = module {
    factoryOf(::GetExploreContent)
    factoryOf(::GetPopularMovieList)
    factoryOf(::GetTrendingMovies)
    factoryOf(::GetNowPlayingMovies)
    factoryOf(::GetMoviesByGenre)
    factoryOf(::GetMovieTrailers)
    factoryOf(::GetMovieDetail)
    factoryOf(::GetPersonDetail)
    factoryOf(::GetMovieCredits)
    factoryOf(::GetPersonCredits)
    factoryOf(::GetPopularTv)
    factoryOf(::GetTvDetail)
    factoryOf(::GetTvCredits)
    factoryOf(::GetTvSeasonDetail)
    factoryOf(::GetSearchResult)
    factoryOf(::GetSimilarContent)
    factoryOf(::GetMovieGenreList)
    factoryOf(::GetTvGenreList)
    factoryOf(::GetTvImages)
    factoryOf(::InsertFavorite)
    factoryOf(::DeleteFavorite)
    factoryOf(::GetAllFavoriteList)
    factoryOf(::GetFavoriteStatus)
    factoryOf(::GetMovieImages)
    factoryOf(::GetPersonImages)
}

private val interactionWrapperModule = module {
    factoryOf(::CreditWrapper)
    factoryOf(::DetailWrapper)
    factoryOf(::TvWrapper)
    factoryOf(::PersonDetailWrapper)
    factoryOf(::FavoriteWrapper)
}

val useCaseModule = module {
    includes(interactionModule, interactionWrapperModule)
}
