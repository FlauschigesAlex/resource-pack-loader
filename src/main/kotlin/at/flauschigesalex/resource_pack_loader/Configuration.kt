package at.flauschigesalex.resource_pack_loader

import at.flauschigesalex.lib.base.file.FileManager
import at.flauschigesalex.lib.base.file.JsonManager
import at.flauschigesalex.lib.base.file.readJson
import at.flauschigesalex.resource_pack_loader.data.ResourcePackData
import at.flauschigesalex.resource_pack_loader.utils.dataFolder
import at.flauschigesalex.resource_pack_loader.utils.scheduleAsync
import net.kyori.adventure.text.minimessage.MiniMessage

object Configuration {
    @Deprecated("Internal")
    const val VERSION = 1
    
    private val file = FileManager(dataFolder, "config.json").apply { 
        if (!this.exists) createFile()
    }
    private val json = file.readJson() ?: JsonManager()

    @Deprecated("Internal")
    internal val configVersion: Int = json.getInt("_version") ?: 1
    
    init {
        @Suppress("DEPRECATION")
        this.updateConfig()
    }
    
    val packs = json.getJson("packs")?.let { ResourcePackData(it) }?.let { setOf(it) }
        ?: json.getJsonList("packs").mapNotNull { ResourcePackData(it) }.toSet()
    
    val isRequired: Boolean = json.getBoolean("required") ?: false
    val isReplace: Boolean = json.getBoolean("replace") ?: false
    
    internal val richPrompt: String? = json.getString("prompt")
    val prompt = richPrompt?.let { MiniMessage.miniMessage().deserialize(it) }
    
    val useCommand: Boolean = json.getBoolean("useCommand") ?: true

    fun saveConfig(async: Boolean) {
        if (json.isOriginalContent()) return
        
        if (async) return scheduleAsync {
            this.saveConfig(false)
        }
        
        file.createFile()
        file.write(json)
    }

    @Suppress("DEPRECATION")
    private fun createConfig() {
        if (file.file.isDirectory)
            file.delete()
            
        if (file.exists) return
        file.createFile()
        
        file.write(JsonManager(
            "_version" to VERSION
        ))
    }

    @Suppress("DEPRECATION")
    @Deprecated("Reserved for future use")
    private fun updateConfig() {
        if (configVersion >= VERSION) return
        
        throw UnsupportedOperationException()
    }
}