package com.artemchep.pocketmode.ui.widgets

import android.animation.Animator
import android.content.Context
import android.view.View
import android.widget.FrameLayout
import com.artemchep.pocketmode.R
import com.artemchep.pocketmode.databinding.ServiceOverlayBinding
import com.artemchep.pocketmode.ext.resumeOrNothing
import kotlinx.coroutines.*

/**
 * @author Artem Chepurnoy
 */
class OverlayWidget(context: Context) : FrameLayout(context) {

    companion object {
        private const val INIT_ALPHA = 0.0f
        private const val INIT_SCALE_X = 0.9f
        private const val INIT_SCALE_Y = 0.9f
        private const val INIT_TRANSLATE_Y = -5.0f
        private const val INIT_ROTATION_X = -12.0f
    }

    init {
        View.inflate(context, R.layout.service_overlay, this)
    }

    private val viewBinding = ServiceOverlayBinding.bind(this)

    private val enterExitAnimationDuration by lazy {
        resources.getInteger(android.R.integer.config_shortAnimTime).toLong()
    }

    private val emptyAnimatorListener = AnimationListenerAdapter()

    override fun onAttachedToWindow() {
        super.onAttachedToWindow()
        viewBinding.contentCardView.alpha = INIT_ALPHA
        viewBinding.contentCardView.scaleX = INIT_SCALE_X
        viewBinding.contentCardView.scaleY = INIT_SCALE_Y
        viewBinding.contentCardView.translationY = INIT_TRANSLATE_Y
        viewBinding.contentCardView.rotationX = INIT_ROTATION_X
        viewBinding.contentCardView.animate()
            .scaleX(1.0f)
            .scaleY(1.0f)
            .alpha(1.0f)
            .translationY(0.0f)
            .rotationX(0.0f)
            .setListener(emptyAnimatorListener)
            .duration = enterExitAnimationDuration
    }

    fun afterExitAnimation(block: () -> Unit): Job =
        GlobalScope.launch(Dispatchers.Main.immediate) {
            // Suspend the job until the animation
            // completes.
            suspendCancellableCoroutine<Unit> { continuation ->
                viewBinding.contentCardView.animate().cancel()
                viewBinding.contentCardView.animate()
                    .scaleX(INIT_SCALE_X)
                    .scaleY(INIT_SCALE_Y)
                    .alpha(INIT_ALPHA)
                    .translationY(-INIT_TRANSLATE_Y)
                    .rotationX(-INIT_ROTATION_X)
                    .setListener(object : AnimationListenerAdapter() {
                        override fun onAnimationEnd(animation: Animator?) {
                            // Execute the given block after the end of
                            // the animation.
                            launch(Dispatchers.Unconfined) {
                                block()
                            }

                            continuation.resumeOrNothing(Unit)
                        }

                        override fun onAnimationCancel(animation: Animator?) {
                            continuation.resumeOrNothing(Unit)
                        }
                    })
                    .duration = enterExitAnimationDuration
            }
        }

    open class AnimationListenerAdapter : Animator.AnimatorListener {
        override fun onAnimationRepeat(animation: Animator?) {
        }

        override fun onAnimationEnd(animation: Animator?) {
        }

        override fun onAnimationCancel(animation: Animator?) {
        }

        override fun onAnimationStart(animation: Animator?) {
        }
    }

}
