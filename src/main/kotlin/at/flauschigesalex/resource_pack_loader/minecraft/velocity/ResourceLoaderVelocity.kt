package at.flauschigesalex.resource_pack_loader.minecraft.velocity

import at.flauschigesalex.lib.base.file.FileManager
import at.flauschigesalex.lib.minecraft.velocity.base.FlauschigeLibraryVelocity
import at.flauschigesalex.resource_pack_loader.Commands
import at.flauschigesalex.resource_pack_loader.utils.Commons
import at.flauschigesalex.resource_pack_loader.utils.Commons.slug
import at.flauschigesalex.resource_pack_loader.utils.scheduleAsync
import at.flauschigesalex.resource_pack_loader.utils.sendNewerVersionMessage
import at.flauschigesalex.rinth.version.checker.VersionChecker
import at.flauschigesalex.rinth.version.listener.PaperVersionUpdateListener
import at.flauschigesalex.rinth.version.listener.VelocityVersionUpdateListener
import at.flauschigesalex.rinth.version.onChanges
import com.google.inject.Inject
import com.velocitypowered.api.event.Subscribe
import com.velocitypowered.api.event.proxy.ProxyInitializeEvent
import com.velocitypowered.api.plugin.Plugin
import com.velocitypowered.api.proxy.ProxyServer
import org.bstats.velocity.Metrics
import org.slf4j.Logger

@Plugin(id = "resource-pack-loader")
@Suppress("unused", "UNUSED_EXPRESSION")
class ResourceLoaderVelocity @Inject constructor(
    val server: ProxyServer, 
    val logger: Logger,
    val bStats: Metrics.Factory
) {
    
    companion object {
        lateinit var instance: ResourceLoaderVelocity
            private set
    }
    
    @Subscribe
    fun onInitialize(event: ProxyInitializeEvent) {
        Commons.dataFolder = FileManager("plugins/${javaClass.simpleName}").let {
            it.createDirectory()
            return@let it.file
        }
        
        instance = this
        FlauschigeLibraryVelocity.init(this, server, javaClass.packageName)
        Commands // LOAD COMMANDS
        
        // BEGIN BSTATS
        val metrics = bStats.make(this, 30814)

        // BEGIN VERSION CHECKER
        VelocityVersionUpdateListener(server, this) { audience ->
            scheduleAsync {
                VersionChecker.check(slug).currentVersionDiff(server).onSuccess { changes ->
                    changes.onChanges {
                        audience.sendNewerVersionMessage(this)
                    }
                }.onFailure { it.printStackTrace() }
            }
        }
    }
}