package at.flauschigesalex.resource_pack_loader.utils

import at.flauschigesalex.lib.minecraft.paper.base.utils.sendRichMessage
import net.kyori.adventure.audience.Audience
import java.util.*

internal object Translate {
    
    fun translate(key: String, locale: Locale): String = runCatching {
        
        require(key.isNotEmpty()) { "Key must not be empty!" }

        val bundle = ResourceBundle.getBundle(
            "i18n/messages",
            locale,
            ResourceBundle.Control.getNoFallbackControl(ResourceBundle.Control.FORMAT_PROPERTIES)
        )
        
        return bundle.getString(key)
    }.getOrNull() ?: "?($key)"
}

val Audience.locale: Locale get() {
    runCatching { this as PaperPlayer }.onSuccess { return it.locale() }
    runCatching { this as ProxyPlayer }.onSuccess { return it.effectiveLocale ?: Locale.getDefault() }
    return Locale.getDefault()
}

fun Audience.sendTranslated(key: String, vararg args: Any?, richConsumer: Audience.(String) -> String = { it }) {
    val translation = Translate.translate(key, this.locale)
    val richTranslation = richConsumer.invoke(this, translation)
    this.sendRichMessage("<dark_gray>[<gradient:blue:dark_aqua>ResourcePackLoader</gradient><dark_gray>] <gray>$richTranslation".format(*args))
}