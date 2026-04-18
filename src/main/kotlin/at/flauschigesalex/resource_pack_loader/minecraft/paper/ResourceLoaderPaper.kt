package at.flauschigesalex.resource_pack_loader.minecraft.paper

import at.flauschigesalex.lib.minecraft.paper.base.FlauschigeLibraryPaper
import at.flauschigesalex.resource_pack_loader.Commands
import at.flauschigesalex.resource_pack_loader.utils.bStatsPluginId
import org.bstats.bukkit.Metrics
import org.bstats.charts.SimplePie
import org.bukkit.Bukkit
import at.flauschigesalex.resource_pack_loader.utils.dataFolder as internalDataFolder
import org.bukkit.plugin.java.JavaPlugin

@Suppress("unused", "UNUSED_EXPRESSION")
class ResourceLoaderPaper: JavaPlugin() {

    companion object {
        lateinit var instance: ResourceLoaderPaper
            private set
    }
    
    override fun onEnable() {
        internalDataFolder = this.dataFolder
        
        instance = this
        FlauschigeLibraryPaper.init(this, javaClass.packageName)
        Commands // LOAD COMMANDS

        val metrics = Metrics(this, bStatsPluginId)

        metrics.addCustomChart(SimplePie("server_brand") { Bukkit.getServer().name })
        metrics.addCustomChart(SimplePie("server_version") { Bukkit.getServer().minecraftVersion })
    }
}