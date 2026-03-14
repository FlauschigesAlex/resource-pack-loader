package at.flauschigesalex.resource_pack_loader.minecraft.velocity

import at.flauschigesalex.lib.minecraft.velocity.base.internal.VelocityListener
import at.flauschigesalex.resource_pack_loader.Configuration
import at.flauschigesalex.resource_pack_loader.utils.sendServerResourcePacks
import com.velocitypowered.api.event.EventTask
import com.velocitypowered.api.event.Subscribe
import com.velocitypowered.api.event.player.PlayerResourcePackStatusEvent
import com.velocitypowered.api.event.player.ServerPostConnectEvent
import kotlinx.coroutines.runBlocking
import net.kyori.adventure.text.minimessage.MiniMessage

@Suppress("unused")
private class ResourceVelocityListener : VelocityListener() {

    @Subscribe
    fun onInitialLogin(event: ServerPostConnectEvent): EventTask = EventTask.async {
        val player = event.player
        runBlocking {
            player.sendServerResourcePacks()
        }
    }

    @Subscribe
    fun onResourcePackStatus(event: PlayerResourcePackStatusEvent) {
        val player = event.player
        if (event.status != PlayerResourcePackStatusEvent.Status.DECLINED) return
        
        val serverMessage = Configuration.richPrompt
        var kickMessage = """
            <white><lang:'multiplayer.texturePrompt.line1'>
            <yellow><b><lang:'multiplayer.texturePrompt.line2'></b>
        """.trimIndent()
        
        if (serverMessage != null) 
            kickMessage += "<white><lang:'multiplayer.texturePrompt.serverPrompt':'':'$serverMessage'>"
        
        player.disconnect(MiniMessage.miniMessage().deserialize(kickMessage))
    }
}