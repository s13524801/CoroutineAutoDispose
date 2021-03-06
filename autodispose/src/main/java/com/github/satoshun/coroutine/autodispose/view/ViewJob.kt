package com.github.satoshun.coroutine.autodispose.view

import android.view.View
import kotlinx.coroutines.CompletionHandler
import kotlinx.coroutines.Job

/**
 * [Job] is automatically disposed and follows the attach/detach lifecycle of [View].
 */
fun View.autoDispose(job: Job) {
  val listener = ViewListener(this, job)
  this.addOnAttachStateChangeListener(listener)
}

private class ViewListener(
  private val view: View,
  private val job: Job
) : View.OnAttachStateChangeListener,
  CompletionHandler {
  override fun onViewDetachedFromWindow(v: View) {
    view.removeOnAttachStateChangeListener(this)
    job.cancel()
  }

  override fun onViewAttachedToWindow(v: View) {
    // do nothing
  }

  override fun invoke(cause: Throwable?) {
    view.removeOnAttachStateChangeListener(this)
    job.cancel()
  }
}
