package at.flauschigesalex.resource_pack_loader.minecraft.velocity

import at.flauschigesalex.lib.base.file.FileManager
import at.flauschigesalex.lib.minecraft.velocity.base.FlauschigeLibraryVelocity
import at.flauschigesalex.resource_pack_loader.Commands
import at.flauschigesalex.resource_pack_loader.utils.bStatsPluginId
import com.google.inject.Inject
import com.velocitypowered.api.event.Subscribe
import com.velocitypowered.api.event.proxy.ProxyInitializeEvent
import com.velocitypowered.api.plugin.Plugin
import com.velocitypowered.api.proxy.ProxyServer
import org.bstats.charts.SimplePie
import org.bstats.velocity.Metrics
import org.slf4j.Logger
import at.flauschigesalex.resource_pack_loader.utils.dataFolder as internalDataFolder

@Plugin(id = "resource-pack-loader")
@Suppress("unused", "UNUSED_EXPRESSION")
class ResourceLoaderVelocity @Inject constructor(val server: ProxyServer,
                                                 val logger: Logger,
                                                 val bStats: Metrics.Factory
) {
    
    companion object {
        lateinit var instance: ResourceLoaderVelocity
            private set
    }
    
    @Subscribe
    fun onInitialize(event: ProxyInitializeEvent) {
        internalDataFolder = FileManager("plugins/${javaClass.simpleName}").let { 
            it.createDirectory()
            return@let it.file
        }
        
        instance = this
        FlauschigeLibraryVelocity.init(this, server, javaClass.packageName)
        Commands // LOAD COMMANDS
        
        val metrics = bStats.make(this, bStatsPluginId)

        metrics.addCustomChart(SimplePie("server_brand") { server.version.name })
        metrics.addCustomChart(SimplePie("server_version") { server.version.version })
    }
}