package at.flauschigesalex.resource_pack_loader.minecraft.utils

import at.flauschigesalex.resource_pack_loader.utils.PaperPlayer
import at.flauschigesalex.resource_pack_loader.utils.ProxyPlayer
import net.kyori.adventure.audience.Audience
import net.kyori.adventure.text.minimessage.MiniMessage
import java.util.*

val Audience.locale: Locale
    get() {
        return when (this) {
            is PaperPlayer -> this.locale()
            is ProxyPlayer -> this.effectiveLocale ?: Locale.getDefault()
            else -> Locale.getDefault()
        }
    }

fun Audience.sendMiniMessage(message: String, prefix: String) = sendMessage(MiniMessage.miniMessage().deserialize("$prefix$message"))
fun Audience.sendMiniMessage(message: String, prefix: PrefixType = PrefixType.DEFAULT) = this.sendMiniMessage(message, prefix.prefix)