package at.flauschigesalex.resource_pack_loader

import at.flauschigesalex.lib.base.file.FileManager
import at.flauschigesalex.lib.base.file.json.JsonManager
import at.flauschigesalex.lib.base.file.json.readJson
import at.flauschigesalex.lib.base.file.ResourceManager
import at.flauschigesalex.resource_pack_loader.data.ResourcePackData
import at.flauschigesalex.resource_pack_loader.utils.Commons
import at.flauschigesalex.resource_pack_loader.utils.scheduleAsync
import net.kyori.adventure.text.Component
import net.kyori.adventure.text.minimessage.MiniMessage

@Suppress("unused")
object Configuration {
    internal const val VERSION = 1

    private val file = FileManager(Commons.dataFolder, "config.json")
    private lateinit var json: JsonManager

    init {
        this.attemptCreateConfig()
        this.reloadConfig()
        this.updateConfigVersion()
    }

    fun reloadConfig() {
        json = file.readJson() ?: JsonManager()
    }

    @Deprecated("Internal")
    internal val configVersion: Int
        get() = json.getInt("_version") ?: 1

    val packs: Set<ResourcePackData>
        get() = json.getJson("packs")?.let { ResourcePackData(it) }?.let { setOf(it) }
            ?: json.getJsonList("packs").mapNotNull { ResourcePackData(it) }.toSet()

    val isRequired: Boolean
        get() = json.getBoolean("required") ?: false
    
    val isReplace: Boolean
        get() = json.getBoolean("replace") ?: false

    internal val richPrompt: String?
        get()  = json.getString("prompt")
    
    val prompt: Component?
        get() = richPrompt?.let { MiniMessage.miniMessage().deserialize(it) }

    val useCommand: Boolean
        get() = json.getBoolean("useCommand") ?: true

    fun saveConfig(async: Boolean) {
        if (json.isOriginalContent()) return

        if (async) {
            scheduleAsync { this.saveConfig(false) }
            return
        }

        file.createFile()
        file.write(json)
    }

    private fun attemptCreateConfig() {
        if (file.file.isDirectory)
            file.delete()

        if (file.exists) return
        file.createFile()

        ResourceManager("default-config.json")?.let { default ->
            json = default.readJson() ?: return@let null
            file.write(json)
            return@let json
        } ?: JsonManager()

        json.putIfAbsent("_version", VERSION)
        file.write(json)
    }
    
    @Suppress("DEPRECATION")
    private fun updateConfigVersion() {
        var oldVersion = this.configVersion
        if (oldVersion >= VERSION) return
        
        // TODO
        
        json.put("_version", VERSION)
        this.saveConfig(false)
        this.reloadConfig()
    }
}