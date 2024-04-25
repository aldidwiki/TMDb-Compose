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

@Composable
fun ContentDetailExternal(
        instagramUrl: String,
        facebookUrl: String,
        twitterUrl: String,
        imdbUrl: String,
        googleUrl: String,
        modifier: Modifier = Modifier
) {
    val uriHandler = LocalUriHandler.current

    Row(
            modifier = modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(16.dp, Alignment.CenterHorizontally),
    ) {
        IconButton(onClick = {
            uriHandler.openUri(facebookUrl)
        }) {
            Image(
                    painter = painterResource(id = R.drawable.facebook_logo),
                    contentDescription = "Facebook",
                    modifier = Modifier.size(25.dp)
            )
        }

        IconButton(onClick = {
            uriHandler.openUri(instagramUrl)
        }) {
            Image(
                    painter = painterResource(id = R.drawable.instagram_logo),
                    contentDescription = "Instagram",
                    modifier = Modifier.size(25.dp)
            )
        }

        IconButton(onClick = {
            uriHandler.openUri(googleUrl)
        }) {
            Image(
                    painter = painterResource(id = R.drawable.ic_google),
                    contentDescription = "Google",
                    modifier = Modifier.size(25.dp)
            )
        }

        IconButton(onClick = {
            uriHandler.openUri(twitterUrl)
        }) {
            Image(
                    painter = painterResource(id = R.drawable.twitter_logo),
                    contentDescription = "Twitter",
                    modifier = Modifier.size(25.dp)
            )
        }

        IconButton(onClick = {
            uriHandler.openUri(imdbUrl)
        }) {
            Image(
                    painter = painterResource(id = R.drawable.imdb_logo),
                    contentDescription = "IMDb",
                    modifier = Modifier.size(25.dp)
            )
        }
    }
}

@Preview
@Composable
fun ContentDetailExternalPreview() {
    ContentDetailExternal(
            instagramUrl = "",
            facebookUrl = "",
            twitterUrl = "",
            imdbUrl = "",
            googleUrl = ""
    )
}