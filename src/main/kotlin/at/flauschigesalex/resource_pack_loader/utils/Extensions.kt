package at.flauschigesalex.resource_pack_loader.utils

import at.flauschigesalex.resource_pack_loader.Configuration
import at.flauschigesalex.resource_pack_loader.data.ResourcePackData
import com.velocitypowered.api.proxy.Player as ProxyPlayer
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch
import net.kyori.adventure.audience.Audience
import net.kyori.adventure.resource.ResourcePackRequest
import net.kyori.adventure.text.Component
import org.bukkit.entity.Player as PaperPlayer
import java.io.File

private val asyncExecutor = SupervisorJob() + Dispatchers.IO
private val scope = CoroutineScope(asyncExecutor)
internal fun scheduleAsync(block: suspend (CoroutineScope) -> Unit) {
    scope.launch { block(this) }
}

internal lateinit var dataFolder: File

internal suspend fun Audience.sendServerResourcePacks(): Boolean {
    val packs = Configuration.packs
    if (packs.isEmpty()) return true

    return this.sendResourcePacks(packs, Configuration.isReplace, Configuration.isRequired, Configuration.prompt)
}
suspend fun Audience.sendResourcePacks(data: Set<ResourcePackData>, replace: Boolean, required: Boolean, prompt: Component? = null): Boolean {
    if (data.isEmpty()) return false
    
    val mapped = data.mapNotNull { it.toResourcePackInfo() }
    if (mapped.isEmpty()) return false
    
    val request = ResourcePackRequest.resourcePackRequest().packs(mapped)
        .replace(replace)
        .required(required)
        .prompt(prompt)
    
    runCatching {
        this.sendResourcePacks(request)
    }.onFailure { return false }
    
    return true
}

typealias ProxyPlayer = ProxyPlayer
typealias PaperPlayer = PaperPlayer