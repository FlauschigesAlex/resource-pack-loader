package at.flauschigesalex.resource_pack_loader.minecraft.velocity

import at.flauschigesalex.lib.base.file.FileManager
import at.flauschigesalex.lib.minecraft.velocity.base.FlauschigeLibraryVelocity
import at.flauschigesalex.resource_pack_loader.Commands
import at.flauschigesalex.resource_pack_loader.utils.scheduleAsync
import at.flauschigesalex.resource_pack_loader.version.VersionChecker
import at.flauschigesalex.resource_pack_loader.version.sendNewerVersionMessage
import com.google.inject.Inject
import com.velocitypowered.api.event.Subscribe
import com.velocitypowered.api.event.proxy.ProxyInitializeEvent
import com.velocitypowered.api.plugin.Plugin
import com.velocitypowered.api.proxy.ProxyServer
import org.bstats.charts.SimplePie
import org.bstats.velocity.Metrics
import org.slf4j.Logger
import kotlin.jvm.optionals.getOrNull
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
        
        // BEGIN BSTATS
        val metrics = bStats.make(this, 30814)

        metrics.addCustomChart(SimplePie("server_brand") { server.version.name })
        metrics.addCustomChart(SimplePie("server_version") { server.version.version })

        // BEGIN VERSION CHECKER
        scheduleAsync {
            val pluginVersion = this.server.pluginManager.getPlugin(VersionChecker.slug).getOrNull()?.description?.version?.getOrNull() ?: return@scheduleAsync
            VersionChecker.checkVersion(null, pluginVersion).onSuccess { changes ->
                changes?.let { changes ->
                    server.consoleCommandSource.sendNewerVersionMessage(changes)
                }
            }.onFailure { it.printStackTrace() }
        }
    }
}