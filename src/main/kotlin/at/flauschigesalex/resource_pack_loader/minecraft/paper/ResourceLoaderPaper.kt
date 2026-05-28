package at.flauschigesalex.resource_pack_loader.minecraft.paper

import at.flauschigesalex.lib.minecraft.paper.base.FlauschigeLibraryPaper
import at.flauschigesalex.resource_pack_loader.Commands
import at.flauschigesalex.resource_pack_loader.utils.Commons
import at.flauschigesalex.resource_pack_loader.utils.Commons.slug
import at.flauschigesalex.resource_pack_loader.utils.scheduleAsync
import at.flauschigesalex.resource_pack_loader.utils.sendNewerVersionMessage
import at.flauschigesalex.rinth.version.checker.VersionChecker
import at.flauschigesalex.rinth.version.listener.PaperVersionUpdateListener
import at.flauschigesalex.rinth.version.onChanges
import org.bstats.bukkit.Metrics
import org.bukkit.plugin.java.JavaPlugin

@Suppress("unused", "UNUSED_EXPRESSION")
class ResourceLoaderPaper: JavaPlugin() {

    companion object {
        lateinit var instance: ResourceLoaderPaper
            private set
    }
    
    override fun onEnable() {
        Commons.dataFolder = this.dataFolder
        
        instance = this
        FlauschigeLibraryPaper.init(this, javaClass.packageName)
        Commands // LOAD COMMANDS

        // BEGIN BSTATS
        val metrics = Metrics(this, 31064)
        
        // BEGIN VERSION CHECKER
        PaperVersionUpdateListener(this) { audience -> 
            scheduleAsync {
                VersionChecker.check(slug).currentVersionDiff(this).onSuccess { changes ->
                    changes.onChanges {
                        audience.sendNewerVersionMessage(this)
                    }
                }.onFailure { it.printStackTrace() }
            }
        }
    }
}