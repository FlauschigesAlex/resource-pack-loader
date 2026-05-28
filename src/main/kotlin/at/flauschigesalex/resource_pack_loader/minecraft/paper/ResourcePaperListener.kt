package at.flauschigesalex.resource_pack_loader.minecraft.paper

import at.flauschigesalex.lib.minecraft.paper.base.internal.PaperListener
import at.flauschigesalex.resource_pack_loader.Configuration
import at.flauschigesalex.resource_pack_loader.utils.scheduleAsync
import at.flauschigesalex.resource_pack_loader.utils.sendServerResourcePacks
import at.flauschigesalex.resource_pack_loader.utils.sendTranslated
import net.kyori.adventure.key.Key
import net.kyori.adventure.sound.Sound
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
    
    @EventHandler (priority = EventPriority.HIGH)
    private fun onDefaultCheck(event: PlayerJoinEvent) {
        val player = event.player
        if (player.hasPermission("rpl.default_check").not() && player.isOp.not()) return
        
        if (Configuration.packs.isNotEmpty() && Configuration.packs.all { it.isDefault }.not())
            return

        player.playSound(Sound.sound(Key.key("entity.item.break"), Sound.Source.MASTER, 1f, .1f))
        player.sendTranslated("rpl.default_check") {
            "<red>$it"
        }
    }
}