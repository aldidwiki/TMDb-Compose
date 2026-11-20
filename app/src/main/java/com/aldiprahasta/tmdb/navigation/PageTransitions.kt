package com.aldiprahasta.tmdb.navigation

import androidx.compose.animation.AnimatedContentTransitionScope
import androidx.compose.animation.AnimatedContentTransitionScope.SlideDirection
import androidx.compose.animation.EnterTransition
import androidx.compose.animation.ExitTransition
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.LinearOutSlowInEasing
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.navigation.NavBackStackEntry
import androidx.navigation.NavDestination
import kotlin.math.roundToInt

private const val TabFadeDuration = 200
private const val PageEnterDuration = 300
private const val PageReturnDuration = 260
private const val PageFadeDuration = 200
private const val ForegroundSlideFraction = 0.20f
private const val BackgroundSlideFraction = 0.06f

internal fun AnimatedContentTransitionScope<NavBackStackEntry>.enterPage(): EnterTransition =
        if (isTabTransition()) {
            fadeIn(tween(TabFadeDuration, easing = LinearOutSlowInEasing))
        } else {
            slideIntoContainer(
                    towards = SlideDirection.Start,
                    animationSpec = tween(PageEnterDuration, easing = FastOutSlowInEasing),
                    initialOffset = { (it * ForegroundSlideFraction).roundToInt() }
            ) + fadeIn(tween(PageFadeDuration, easing = LinearOutSlowInEasing))
        }

internal fun AnimatedContentTransitionScope<NavBackStackEntry>.exitPage(): ExitTransition =
        if (isTabTransition()) {
            ExitTransition.KeepUntilTransitionsFinished
        } else {
            slideOutOfContainer(
                    towards = SlideDirection.Start,
                    animationSpec = tween(PageEnterDuration, easing = FastOutSlowInEasing),
                    targetOffset = { (it * BackgroundSlideFraction).roundToInt() }
            )
        }

internal fun AnimatedContentTransitionScope<NavBackStackEntry>.revealPreviousPage(): EnterTransition =
        if (isTabTransition()) {
            EnterTransition.None
        } else {
            slideIntoContainer(
                    towards = SlideDirection.End,
                    animationSpec = tween(PageReturnDuration, easing = FastOutSlowInEasing),
                    initialOffset = { (it * BackgroundSlideFraction).roundToInt() }
            )
        }

internal fun AnimatedContentTransitionScope<NavBackStackEntry>.leavePage(): ExitTransition =
        if (isTabTransition()) {
            fadeOut(tween(TabFadeDuration, easing = FastOutSlowInEasing))
        } else {
            slideOutOfContainer(
                    towards = SlideDirection.End,
                    animationSpec = tween(PageReturnDuration, easing = FastOutSlowInEasing),
                    targetOffset = { (it * ForegroundSlideFraction).roundToInt() }
            ) + fadeOut(tween(PageFadeDuration, easing = FastOutSlowInEasing))
        }

private fun AnimatedContentTransitionScope<NavBackStackEntry>.isTabTransition(): Boolean =
        initialState.destination.isTopLevel() && targetState.destination.isTopLevel()

private fun NavDestination.isTopLevel(): Boolean = navigationItem.any { it.route == route }
