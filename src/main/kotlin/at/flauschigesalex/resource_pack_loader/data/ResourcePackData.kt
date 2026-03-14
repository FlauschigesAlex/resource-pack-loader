package at.flauschigesalex.resource_pack_loader.data

import at.flauschigesalex.lib.base.file.JsonManager
import at.flauschigesalex.lib.base.general.HttpRequestHandler
import at.flauschigesalex.resource_pack_loader.Configuration
import net.kyori.adventure.resource.ResourcePackInfo
import java.net.URI
import java.net.http.HttpResponse
import java.nio.file.Files
import java.security.MessageDigest
import java.util.UUID
import kotlin.io.path.createTempFile

@Suppress("unused")
class ResourcePackData private constructor(private val json: JsonManager) {
    
    companion object {
        operator fun invoke(json: JsonManager): ResourcePackData? {
            if (json["_id"] == null || json["url"] == null)
                return null
            
            return ResourcePackData(json)
        }
        operator fun invoke(id: UUID, url: String): ResourcePackData {
            val json = JsonManager(
                "_id" to id,
                "url" to url,
            )
            
            return this.invoke(json)!!
        }
        
        fun deleteCached() = Configuration.packs.forEach {
            it.deleteCache()
        }
    }
    
    val id: UUID = json.getUUID("_id") ?: throw IllegalArgumentException("'_id' cannot be null or empty")
    val url: String = json.getString("url") ?: throw IllegalArgumentException("'url' cannot be null or empty")
    
    private var _cached: ResourcePackInfo? = null
    fun deleteCache() {
        _cached = null
    }
    
    suspend fun toResourcePackInfo(): ResourcePackInfo? {
        _cached?.run {
            return this
        }
        
        val result = runCatching {
            val url = URI.create(this.url)
            val connection = HttpRequestHandler(url).get(HttpResponse.BodyHandlers.ofByteArray())
                ?: throw Exception("Could not connect to uri: $url")
            
            val bytes = connection.body() ?: throw Exception("Could not get body from ur.: $url")
            val tempFile = createTempFile(prefix = id.toString(), suffix = ".zip")
            Files.write(tempFile, bytes)
            
            val sha = MessageDigest.getInstance("SHA-1").digest(bytes).toHexString()

            return@runCatching ResourcePackInfo.resourcePackInfo(id, url, sha)
        }
        
        result.onFailure {
            it.printStackTrace()
            _cached = null
            return null
        }
        
        val data = result.getOrNull() ?: return null
        
        _cached = data
        return data
    }
    
    fun toJson() = json.clone()

    override fun hashCode(): Int = id.hashCode()
    override fun equals(other: Any?): Boolean = other is ResourcePackData && id == other.id
}