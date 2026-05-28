package at.flauschigesalex.resource_pack_loader.utils

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch


private val asyncExecutor = SupervisorJob() + Dispatchers.IO
private val scope = CoroutineScope(asyncExecutor)
internal fun scheduleAsync(block: suspend (CoroutineScope) -> Unit) {
    scope.launch { block(this) }
}