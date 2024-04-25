package com.aldiprahasta.tmdb.ui.details

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.size
import androidx.compose.material3.IconButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalUriHandler
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.aldiprahasta.tmdb.R
import com.aldiprahasta.tmdb.utils.Constant

@Composable
fun ContentDetailExternal(
        instagramId: String?,
        facebookId: String?,
        twitterId: String?,
        imdbPair: Pair<Boolean, String?>,
        googleId: String?,
        modifier: Modifier = Modifier
) {
    val uriHandler = LocalUriHandler.current

    Row(
            modifier = modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(10.dp, Alignment.CenterHorizontally),
    ) {
        facebookId?.let {
            IconButton(onClick = {
                uriHandler.openUri(Constant.FACEBOOK_BASE_URL + facebookId)
            }) {
                Image(
                        painter = painterResource(id = R.drawable.facebook_logo),
                        contentDescription = "Facebook",
                        modifier = Modifier.size(25.dp)
                )
            }
        }

        instagramId?.let {
            IconButton(onClick = {
                uriHandler.openUri(Constant.INSTAGRAM_BASE_URL + instagramId)
            }) {
                Image(
                        painter = painterResource(id = R.drawable.instagram_logo),
                        contentDescription = "Instagram",
                        modifier = Modifier.size(25.dp)
                )
            }
        }

        googleId?.let {
            IconButton(onClick = {
                uriHandler.openUri(Constant.GOOGLE_SEARCH_BASE_URL + googleId)
            }) {
                Image(
                        painter = painterResource(id = R.drawable.ic_google),
                        contentDescription = "Google",
                        modifier = Modifier.size(25.dp)
                )
            }
        }

        twitterId?.let {
            IconButton(onClick = {
                uriHandler.openUri(Constant.TWITTER_BASE_URL + twitterId)
            }) {
                Image(
                        painter = painterResource(id = R.drawable.twitter_logo),
                        contentDescription = "Twitter",
                        modifier = Modifier.size(25.dp)
                )
            }
        }

        imdbPair.second?.let { imdbId ->
            IconButton(onClick = {
                uriHandler.openUri(
                        if (imdbPair.first) Constant.IMDB_BASE_URL + imdbId
                        else Constant.IMDB_PROFILE_URL + imdbId
                )
            }) {
                Image(
                        painter = painterResource(id = R.drawable.imdb_logo),
                        contentDescription = "IMDb",
                        modifier = Modifier.size(25.dp)
                )
            }
        }
    }
}

@Preview
@Composable
fun ContentDetailExternalPreview() {
    ContentDetailExternal(
            instagramId = "",
            facebookId = "",
            twitterId = "",
            imdbPair = Pair(true, ""),
            googleId = ""
    )
}