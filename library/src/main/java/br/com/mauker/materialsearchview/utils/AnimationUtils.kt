package br.com.mauker.materialsearchview.utils

import android.animation.AnimatorListenerAdapter
import android.annotation.TargetApi
import android.view.View
import android.view.ViewAnimationUtils
import androidx.core.view.ViewCompat
import androidx.core.view.ViewPropertyAnimatorListener

/**
 * Created by Mauker on 18/04/2016. (dd/MM/yyyy).
 *
 * Utility class used to easily animate Views. Most used for revealing or hiding Views.
 */
object AnimationUtils {

    @JvmStatic
    val ANIMATION_DURATION_SHORTEST = 150

    @JvmStatic
    val ANIMATION_DURATION_SHORT = 250

    @JvmStatic
    val ANIMATION_DURATION_MEDIUM = 400

    @JvmStatic
    val ANIMATION_DURATION_LONG = 800

    @TargetApi(21)
    @JvmStatic
    @JvmOverloads
    fun circleRevealView(view: View, duration: Int = ANIMATION_DURATION_SHORT) {
        // get the center for the clipping circle
        val cx = view.width
        val cy = view.height / 2

        // get the final radius for the clipping circle
        val finalRadius = Math.hypot(cx.toDouble(), cy.toDouble()).toFloat()

        // create the animator for this view (the start radius is zero)
        val anim = ViewAnimationUtils.createCircularReveal(view, cx, cy, 0f, finalRadius)

        anim.duration = if (duration > 0) duration.toLong() else ANIMATION_DURATION_SHORT.toLong()

        // make the view visible and start the animation
        view.visibility = View.VISIBLE
        anim.start()
    }

    @TargetApi(21)
    @JvmStatic
    fun circleHideView(view: View, listenerAdapter: AnimatorListenerAdapter) {
        circleHideView(view, ANIMATION_DURATION_SHORT, listenerAdapter)
    }

    @TargetApi(21)
    @JvmStatic
    fun circleHideView(view: View, duration: Int, listenerAdapter: AnimatorListenerAdapter) {
        // get the center for the clipping circle
        val cx = view.width
        val cy = view.height / 2

        // get the initial radius for the clipping circle
        val initialRadius = Math.hypot(cx.toDouble(), cy.toDouble()).toFloat()

        // create the animation (the final radius is zero)
        val anim = ViewAnimationUtils.createCircularReveal(view, cx, cy, initialRadius, 0f)

        // make the view invisible when the animation is done
        anim.addListener(listenerAdapter)

        anim.duration = if (duration > 0) duration.toLong() else ANIMATION_DURATION_SHORT.toLong()

        // start the animation
        anim.start()
    }

    /**
     * Reveal the provided View with a fade-in animation.
     *
     * @param view The View that's being animated.
     * @param duration How long should the animation take, in millis.
     */
    @JvmStatic
    @JvmOverloads
    fun fadeInView(view: View, duration: Int = ANIMATION_DURATION_SHORTEST) {
        view.visibility = View.VISIBLE
        view.alpha = 0f

        // Setting the listener to null, so it won't keep getting called.
        ViewCompat.animate(view).alpha(1f).setDuration(duration.toLong()).setListener(null)
    }

    /**
     * Hide the provided View with a fade-out animation.
     *
     * @param view The View that's being animated.
     * @param duration How long should the animation take, in millis.
     */
    @JvmStatic
    @JvmOverloads
    fun fadeOutView(view: View, duration: Int = ANIMATION_DURATION_SHORTEST) {
        ViewCompat.animate(view).alpha(0f).setDuration(duration.toLong()).setListener(object : ViewPropertyAnimatorListener {
            override fun onAnimationStart(view: View) {
                view.isDrawingCacheEnabled = true
            }

            override fun onAnimationEnd(view: View) {
                view.visibility = View.GONE
                view.alpha = 1f
                view.isDrawingCacheEnabled = false
            }

            override fun onAnimationCancel(view: View) {}
        })
    }

    /**
     * Cross animate two views, showing one, hiding the other.
     *
     * @param showView The View that's going to be visible after the animation.
     * @param hideView The View that's going to disappear after the animation.
     */
    @JvmStatic
    @JvmOverloads
    fun crossFadeViews(showView: View, hideView: View, duration: Int = ANIMATION_DURATION_SHORT) {
        fadeInView(showView, duration)
        fadeOutView(hideView, duration)
    }

    // TODO - Cross fade with circle reveal.
}

