package xerus.ktutil.helpers

import kotlinx.coroutines.experimental.Job
import kotlinx.coroutines.experimental.delay
import kotlinx.coroutines.experimental.launch

abstract class Refresher {
	abstract suspend fun doRefresh()
	
	protected lateinit var job: Job
	open fun refresh(startNew: Boolean = false): Job {
		if (!::job.isInitialized || !job.isActive)
			start { doRefresh() }
		else if (startNew && ::job.isInitialized) {
			val oldJob = job
			start {
				oldJob.join()
				doRefresh()
			}
		}
		return job
	}
	
	fun start(function: suspend () -> Unit) {
		job = launch { function() }
	}
	
	operator fun invoke() = refresh()
}

/** A simple [Refresher] that will execute the given function */
open class SimpleRefresher(val function: suspend () -> Unit) : Refresher() {
	override suspend fun doRefresh() = function()
}

/** A [Refresher] implementation that will only issue a refresh if a given time has elapsed since the last refresh */
abstract class TimedRefresher(private val timeDif: Int, runnable: suspend () -> Unit) : SimpleRefresher(runnable) {
	private var lastRefresh = 0L
	fun refresh(): Job {
		if (System.currentTimeMillis() > lastRefresh + timeDif)
			super.refresh(false).invokeOnCompletion { lastRefresh = System.currentTimeMillis() }
		return job
	}
}

/** A [Refresher] implementation that will wait until a given time has elapsed since the last refresh call until it actually initiates a refresh
 * @param delayMillis the milliseconds to wait */
open class DelayedRefresher(val delayMillis: Long, val function: suspend () -> Unit) : Refresher() {
	var waitUntil: Long = 0
	override fun refresh(startNew: Boolean): Job {
		waitUntil = System.currentTimeMillis() + delayMillis
		return super.refresh(startNew)
	}
	
	override suspend fun doRefresh() {
		while (waitUntil > System.currentTimeMillis())
			delay(delayMillis / 3)
		function()
	}
}