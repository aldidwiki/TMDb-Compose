package com.aldiprahasta.tmdb.utils

import android.content.Context
import androidx.browser.customtabs.CustomTabsIntent
import androidx.core.net.toUri

fun Context.openBrowser(url: String) {
    CustomTabsIntent.Builder().apply {
        setShareState(CustomTabsIntent.SHARE_STATE_OFF)
    }.build().launchUrl(this, url.toUri())
}
