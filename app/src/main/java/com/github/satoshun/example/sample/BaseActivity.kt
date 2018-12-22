package com.github.satoshun.example.sample

import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleObserver
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.OnLifecycleEvent
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlin.coroutines.CoroutineContext

abstract class BaseActivity : AppCompatActivity(),
  CoroutineScope {

  private val job = Job()
  override val coroutineContext: CoroutineContext get() = job + Dispatchers.Main

  override fun onDestroy() {
    super.onDestroy()
    job.cancel()
  }
}

fun LifecycleOwner.addJob(job: Job) {
  val state = lifecycle.currentState
  when (state) {
    Lifecycle.State.DESTROYED -> TODO()
    Lifecycle.State.INITIALIZED -> lifecycle.addObserver(AnyObserver(job, Lifecycle.Event.ON_DESTROY))
    Lifecycle.State.CREATED -> lifecycle.addObserver(AnyObserver(job, Lifecycle.Event.ON_DESTROY))
    Lifecycle.State.STARTED -> lifecycle.addObserver(AnyObserver(job, Lifecycle.Event.ON_STOP))
    Lifecycle.State.RESUMED -> lifecycle.addObserver(AnyObserver(job, Lifecycle.Event.ON_PAUSE))
  }
}

private class AnyObserver(
  private val job: Job,
  private val event: Lifecycle.Event
) : LifecycleObserver {
  @OnLifecycleEvent(Lifecycle.Event.ON_ANY)
  fun onEvent(owner: LifecycleOwner, event: Lifecycle.Event) {
    if (event == this.event) {
      owner.lifecycle.removeObserver(this)
      job.cancel()
    }
  }
}
