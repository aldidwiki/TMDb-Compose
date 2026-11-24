package com.aldiprahasta.tmdb.data.repository

import androidx.paging.Pager
import androidx.paging.PagingConfig
import com.aldiprahasta.tmdb.data.source.paging.ExploreFeed
import com.aldiprahasta.tmdb.data.source.paging.ExplorePage
import com.aldiprahasta.tmdb.data.source.paging.ExplorePagingSource
import com.aldiprahasta.tmdb.data.source.remote.network.RemoteService
import com.aldiprahasta.tmdb.data.source.remote.response.ExploreResponse
import com.aldiprahasta.tmdb.domain.model.ExploreFilters
import com.aldiprahasta.tmdb.domain.model.ExploreItem
import com.aldiprahasta.tmdb.domain.model.ExploreKnownFor
import com.aldiprahasta.tmdb.domain.model.ExplorePreset
import com.aldiprahasta.tmdb.domain.model.ExploreQuery
import com.aldiprahasta.tmdb.domain.model.ExploreSort
import com.aldiprahasta.tmdb.domain.model.ExploreTab
import com.aldiprahasta.tmdb.domain.repository.ExploreRepository
import com.aldiprahasta.tmdb.utils.Constant
import com.aldiprahasta.tmdb.utils.MediaType
import com.aldiprahasta.tmdb.utils.mapToExploreItem
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.coroutineScope

class ExploreRepositoryImpl(private val remoteService: RemoteService) : ExploreRepository {
    override fun explore(query: ExploreQuery) = Pager(
            config = PagingConfig(
                    pageSize = 20,
                    initialLoadSize = 20,
                    prefetchDistance = 5,
                    enablePlaceholders = false
            ),
            pagingSourceFactory = {
                val feeds = buildList {
                    if (query.tab == ExploreTab.ALL || query.tab == ExploreTab.MOVIES) {
                        titleFeed(query, MediaType.MOVIE_TYPE)?.let(::add)
                    }
                    if (query.tab == ExploreTab.ALL || query.tab == ExploreTab.TV) {
                        titleFeed(query, MediaType.TV_TYPE)?.let(::add)
                    }
                    if (query.includesPeople) add(peopleFeed(query))
                }
                val sort = itemOrder(query.order).takeIf { query.tab == ExploreTab.ALL && query.order != ExploreSort.POPULAR }
                ExplorePagingSource(feeds, sort)
            }
    ).flow

    private fun titleFeed(query: ExploreQuery, mediaType: MediaType): ExploreFeed? {
        val genreIds = query.filters.genres.filter { mediaType in it.mediaTypes }.map { it.id }
        if (query.filters.genres.isNotEmpty() && genreIds.isEmpty()) return null

        val isMovie = mediaType == MediaType.MOVIE_TYPE
        val kind = if (isMovie) Constant.MOVIE_MEDIA_TYPE else Constant.TV_MEDIA_TYPE
        val preset = query.preset
        val list = when (preset) {
            ExplorePreset.NOW_PLAYING -> "now_playing"
            ExplorePreset.UPCOMING -> "upcoming"
            ExplorePreset.MOVIE_TOP_RATED, ExplorePreset.TV_TOP_RATED -> "top_rated"
            ExplorePreset.AIRING_TODAY -> "airing_today"
            ExplorePreset.ON_THE_AIR -> "on_the_air"
            else -> null
        }
        val timezone = Calendar.getInstance().timeZone.id
        val region = MOVIE_REGION.takeIf { isMovie && preset in listOf(ExplorePreset.NOW_PLAYING, ExplorePreset.UPCOMING) }
        val airingTimezone = timezone.takeIf { preset in listOf(ExplorePreset.AIRING_TODAY, ExplorePreset.ON_THE_AIR) }
        var discoverFilters: Map<String, String>? = null

        return { page ->
            val response = if (list != null && query.sort == null && !query.filters.hasTitleFilters) {
                remoteService.getExploreList(kind, list, page, region, airingTimezone)
            } else {
                val filters = discoverFilters ?: buildMap {
                    put("sort_by", tvOrMovieOrder(query.order, isMovie))
                    genreIds.takeIf { it.isNotEmpty() }?.let { put("with_genres", it.joinToString("|")) }
                    query.filters.minimumRating?.let { put("vote_average.gte", it.toString()) }
                    query.filters.years?.let { years ->
                        if (years.from == years.to) {
                            put(if (isMovie) "primary_release_year" else "first_air_date_year", yearValue(years.from))
                        } else {
                            val parameter = if (isMovie) "primary_release_date" else "first_air_date"
                            put(parameter + ".gte", yearValue(years.from) + "-01-01")
                            put(parameter + ".lte", yearValue(years.to) + "-12-31")
                        }
                    }

                    when (preset) {
                        ExplorePreset.NOW_PLAYING, ExplorePreset.UPCOMING -> {
                            val dates = checkNotNull(remoteService.getExploreList(kind, checkNotNull(list), 1, region).dates)
                            put("release_date.gte", dates.minimum)
                            put("release_date.lte", dates.maximum)
                            put("with_release_type", "2|3")
                            region?.let { put("region", it) }
                        }
                        ExplorePreset.AIRING_TODAY, ExplorePreset.ON_THE_AIR -> {
                            val calendar = Calendar.getInstance()
                            val format = SimpleDateFormat("yyyy-MM-dd", Locale.US).apply { timeZone = calendar.timeZone }
                            put("air_date.gte", format.format(calendar.time))
                            if (preset == ExplorePreset.ON_THE_AIR) calendar.add(Calendar.DAY_OF_YEAR, 7)
                            put("air_date.lte", format.format(calendar.time))
                            put("timezone", timezone)
                        }
                        ExplorePreset.MOVIE_TOP_RATED -> {
                            put("vote_count.gte", "200")
                            put("without_genres", "99,10755")
                        }
                        ExplorePreset.TV_TOP_RATED -> put("vote_count.gte", "200")
                        else -> if (query.order == ExploreSort.HIGHEST_RATED) put("vote_count.gte", "1")
                    }
                }.also { discoverFilters = it }
                remoteService.discoverExploreContent(kind, page, filters)
            }
            val loaded = response.asPage(page, mediaType)
            if (query.tab == ExploreTab.ALL && query.order != ExploreSort.POPULAR) {
                loaded.copy(items = loaded.items.sortedWith(itemOrder(query.order)))
            } else loaded
        }
    }

    private fun peopleFeed(query: ExploreQuery): ExploreFeed {
        val sort = query.order
        val filters = query.filters
        val needsRanking = sort in listOf(ExploreSort.TITLE, ExploreSort.NAME, ExploreSort.KNOWN_FOR_MOVIES, ExploreSort.KNOWN_FOR_TV)

        return if (needsRanking) {
            {
                val first = getPeoplePage(1, trending = false)
                val pages = coroutineScope {
                    (2..minOf(first.totalPages, PEOPLE_CANDIDATE_PAGES)).map { page ->
                        async { getPeoplePage(page, trending = false) }
                    }.awaitAll()
                }
                val candidates = (listOf(first) + pages).flatMap { response ->
                    response.results.map { it.mapToExploreItem(MediaType.PERSON_TYPE) }
                }
                ExplorePage(
                        items = candidates.distinctBy { it.key }.filter { filters.matchesPerson(it) }.sortedWith(itemOrder(sort)),
                        nextPage = null
                )
            }
        } else {
            { page ->
                val response = getPeoplePage(page, trending = sort == ExploreSort.TRENDING)
                val limit = if (filters.hasPeopleFilters) PEOPLE_CANDIDATE_PAGES else MAX_PAGE
                val result = response.asPage(page, MediaType.PERSON_TYPE, limit)
                result.copy(items = result.items.filter { filters.matchesPerson(it) })
            }
        }
    }

    private suspend fun getPeoplePage(page: Int, trending: Boolean): ExploreResponse =
            if (trending) remoteService.getExploreTrendingPeople(page)
            else remoteService.getExploreList(Constant.PERSON_MEDIA_TYPE, "popular", page)

    private fun ExploreResponse.asPage(pageNumber: Int, mediaType: MediaType, maxPage: Int = MAX_PAGE): ExplorePage {
        if (page != pageNumber) return ExplorePage(emptyList(), null)
        return ExplorePage(
                items = results.map { result -> result.mapToExploreItem(mediaType) }.filter { it.title.isNotBlank() },
                nextPage = (pageNumber + 1).takeIf { pageNumber < minOf(totalPages, maxPage) && results.isNotEmpty() }
        )
    }

    private fun ExploreFilters.matchesPerson(person: ExploreItem): Boolean {
        if (departments.isNotEmpty() && departments.none { it.apiName.equals(person.department, ignoreCase = true) }) return false
        val movie = MediaType.MOVIE_TYPE in person.knownFor
        val tv = MediaType.TV_TYPE in person.knownFor
        return when (knownFor) {
            ExploreKnownFor.MOVIES -> movie
            ExploreKnownFor.TV -> tv
            ExploreKnownFor.BOTH -> movie && tv
            null -> true
        }
    }

    private fun itemOrder(sort: ExploreSort): Comparator<ExploreItem> = when (sort) {
        ExploreSort.TITLE, ExploreSort.NAME -> compareBy { item: ExploreItem -> item.title.lowercase(Locale.ROOT) }
        ExploreSort.NEWEST -> compareByDescending { it.releaseDate.orEmpty() }
        ExploreSort.HIGHEST_RATED -> compareByDescending { it.rating ?: -1.0 }
        ExploreSort.MOST_VOTED -> compareByDescending { it.voteCount }
        ExploreSort.KNOWN_FOR_MOVIES -> compareByDescending<ExploreItem> { item -> item.knownFor.count { it == MediaType.MOVIE_TYPE } }
                .thenByDescending { it.popularity }
        ExploreSort.KNOWN_FOR_TV -> compareByDescending<ExploreItem> { item -> item.knownFor.count { it == MediaType.TV_TYPE } }
                .thenByDescending { it.popularity }
        ExploreSort.POPULAR, ExploreSort.TRENDING -> compareByDescending { it.popularity }
    }

    private fun tvOrMovieOrder(sort: ExploreSort, isMovie: Boolean): String = when (sort) {
        ExploreSort.NEWEST -> if (isMovie) "primary_release_date.desc" else "first_air_date.desc"
        ExploreSort.HIGHEST_RATED -> "vote_average.desc"
        ExploreSort.TITLE -> if (isMovie) "title.asc" else "name.asc"
        ExploreSort.MOST_VOTED -> "vote_count.desc"
        else -> "popularity.desc"
    }

    private fun yearValue(year: Int): String = year.toString().padStart(4, '0')

    private companion object {
        const val MOVIE_REGION = "ID"
        const val PEOPLE_CANDIDATE_PAGES = 5
        const val MAX_PAGE = 500
    }
}
