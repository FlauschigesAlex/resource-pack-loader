package at.flauschigesalex.resource_pack_loader.utils

import at.flauschigesalex.resource_pack_loader.Configuration
import at.flauschigesalex.resource_pack_loader.data.ResourcePackData
import net.kyori.adventure.audience.Audience
import net.kyori.adventure.resource.ResourcePackRequest
import net.kyori.adventure.text.Component
import com.velocitypowered.api.proxy.Player as ProxyPlayer
import org.bukkit.entity.Player as PaperPlayer

internal suspend fun Audience.sendServerResourcePacks(): Boolean {
    val packs = Configuration.packs
    if (packs.isEmpty()) return true

    return this.sendResourcePacks(packs, Configuration.isReplace, Configuration.isRequired, Configuration.prompt)
}
suspend fun Audience.sendResourcePacks(data: Set<ResourcePackData>, replace: Boolean, required: Boolean, prompt: Component? = null): Boolean {
    if (data.isEmpty()) return false
    
    val packs = data.filterNot { it.isDefault }.mapNotNull { it.toResourcePackInfo() }
    if (packs.isEmpty()) return false
    
    val request = ResourcePackRequest.resourcePackRequest().packs(packs)
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