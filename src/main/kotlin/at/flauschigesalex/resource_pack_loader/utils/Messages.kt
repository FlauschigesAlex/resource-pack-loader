package at.flauschigesalex.resource_pack_loader.utils

import java.util.Locale
import java.util.ResourceBundle

object Messages {
    
    fun translate(locale: Locale, key: String, vararg args: Any?): String = runCatching {
        ResourceBundle.getBundle("i18n.messages", locale).getString(key).format(*args)
    }.also { println(it) }.getOrNull() ?: key
}