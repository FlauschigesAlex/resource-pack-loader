package at.flauschigesalex.resource_pack_loader.minecraft.utils

@Suppress("unused")
enum class PrefixType(internal val prefix: String) {

    DEFAULT("<dark_gray>» <gray>"),
    INFO("<dark_aqua>ℹ <aqua>"),
    WARN("<gold>⚐ <yellow>"),
    ERROR("<dark_red>☹ <red>"),
    ;

}