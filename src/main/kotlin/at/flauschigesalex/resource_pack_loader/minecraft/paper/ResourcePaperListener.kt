package at.flauschigesalex.resource_pack_loader.minecraft.paper

import at.flauschigesalex.lib.minecraft.paper.base.internal.PaperListener
import at.flauschigesalex.resource_pack_loader.utils.scheduleAsync
import at.flauschigesalex.resource_pack_loader.utils.sendServerResourcePacks
import org.bukkit.event.EventHandler
import org.bukkit.event.EventPriority
import org.bukkit.event.player.PlayerJoinEvent

@Suppress("unused")
private class ResourcePaperListener : PaperListener() {
    
    @EventHandler (priority = EventPriority.MONITOR, ignoreCancelled = true)
    private fun onPlayerJoin(event: PlayerJoinEvent) {
        val player = event.player

        scheduleAsync {
            player.sendServerResourcePacks()
        }
    }
}