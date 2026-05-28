package at.flauschigesalex.resource_pack_loader.utils

import at.flauschigesalex.rinth.version.ProjectVersionDiff
import net.kyori.adventure.audience.Audience
import java.io.File

object Commons {
    const val slug = "resource-pack-loader"

    internal lateinit var dataFolder: File
}

internal fun Audience.sendNewerVersionMessage(changes: ProjectVersionDiff) {
    this.sendTranslated("version.update.line1", changes.newer.slug, "<gold>${changes.newer.version}</gold>") { "<yellow>$it" }
    this.sendTranslated("version.update.line2", "<red>${changes.older.version}</red>", "<yellow>${changes.indexDifference}</yellow>")
    this.sendTranslated("version.update.line3", "<green><u><click:open_url:'${changes.newer.downloadUrl}'>${changes.newer.downloadUrl}</click></u></green>")
}