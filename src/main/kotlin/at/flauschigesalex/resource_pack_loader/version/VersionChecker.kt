package at.flauschigesalex.resource_pack_loader.version

import at.flauschigesalex.lib.base.general.Cache
import at.flauschigesalex.lib.base.general.version.SemanticVersion
import at.flauschigesalex.resource_pack_loader.utils.sendTranslated
import at.flauschigesalex.rinth.ModrinthAPI
import at.flauschigesalex.rinth.version.*
import net.kyori.adventure.audience.Audience

internal object VersionChecker {
    
    const val slug = "resource-pack-loader"
    private const val cacheKey = "${slug}:versionChecker"
    
    suspend fun loadVersions(minecraftVersion: String?, stability: ProjectVersionType = ProjectVersionType.RELEASE): Result<Set<ProjectVersion>> = Cache[cacheKey] ?: runCatching {
        ModrinthAPI.findAll(slug, "velocity", minecraftVersion).getOrThrow().stability(stability).toSet()
    }.also { Cache.put<Result<Set<ProjectVersion>>>(cacheKey, it) }
    
    fun currentVersion(pluginVersionRaw: String, projectVersions: Set<ProjectVersion>): Result<ProjectVersion?> = runCatching {
        val pluginVersion: SemanticVersion = SemanticVersion.parseOrThrow(pluginVersionRaw)

        return@runCatching projectVersions.find { it.version == pluginVersion }
    }
    
    var newerVersion: Result<ProjectVersionDiff?>? = null
        private set
    
    suspend fun checkVersion(minecraftVersion: String?, pluginVersionRaw: String): Result<ProjectVersionDiff?> = runCatching {
        val projectVersions = loadVersions(minecraftVersion).getOrThrow()
        
        val currentVersion = currentVersion(pluginVersionRaw, projectVersions).getOrThrow()
        val latestVersion = projectVersions.latestOrNull() ?: return@runCatching null

        if (currentVersion == null || currentVersion == latestVersion) return@runCatching null
        
        val diff = ProjectVersionDiff(latestVersion, currentVersion, projectVersions)
        if (diff.indexDifference == 0) return@runCatching null
        
        return@runCatching diff
    }.also { result -> newerVersion = result }
}

internal fun Audience.sendNewerVersionMessage(changes: ProjectVersionDiff) {
    this.sendTranslated("version.update.line1", changes.newer.slug, "<gold>${changes.newer.version}</gold>") { "<yellow>$it" }
    this.sendTranslated("version.update.line2", "<red>${changes.older.version}</red>", "<yellow>${changes.indexDifference}</yellow>")
    this.sendTranslated("version.update.line3", "<green><u><click:open_url:'${changes.newer.downloadUrl}'>${changes.newer.downloadUrl}</click></u></green>")
}