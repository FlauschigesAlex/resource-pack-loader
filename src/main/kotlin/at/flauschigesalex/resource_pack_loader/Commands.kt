package at.flauschigesalex.resource_pack_loader

import at.flauschigesalex.lib.minecraft.brigadier.CommandBuilder
import at.flauschigesalex.lib.minecraft.brigadier.types.internal.LiteralArgumentType
import at.flauschigesalex.resource_pack_loader.data.ResourcePackData
import at.flauschigesalex.resource_pack_loader.minecraft.paper.ResourceLoaderPaper
import at.flauschigesalex.resource_pack_loader.minecraft.velocity.ResourceLoaderVelocity
import at.flauschigesalex.resource_pack_loader.minecraft.utils.PrefixType
import at.flauschigesalex.resource_pack_loader.minecraft.utils.locale
import at.flauschigesalex.resource_pack_loader.minecraft.utils.sendMiniMessage
import at.flauschigesalex.resource_pack_loader.utils.Messages
import at.flauschigesalex.resource_pack_loader.utils.PaperPlayer
import at.flauschigesalex.resource_pack_loader.utils.ProxyPlayer
import at.flauschigesalex.resource_pack_loader.utils.scheduleAsync
import at.flauschigesalex.resource_pack_loader.utils.sendServerResourcePacks
import net.kyori.adventure.audience.Audience
import at.flauschigesalex.lib.minecraft.paper.base.command.types.PlayerArgumentType as PaperPlayerArgumentType
import at.flauschigesalex.lib.minecraft.velocity.base.command.types.PlayerArgumentType as VelocityPlayerArgumentType

object Commands {
    init {

        if (Configuration.useCommand) CommandBuilder("resource-pack-loader") {
            this.permission("rpl.admin")
            this.alias("rpl")
            
            runCatching {
                ResourceLoaderPaper.instance // ASSERT THAT SERVER IS PAPER
                this.alias("resource-pack-loader-paper", "rpl-paper")
            }
            runCatching {
                ResourceLoaderVelocity.instance // ASSERT THAT SERVER IS VELOCITY
                this.alias("resource-pack-loader-velocity", "rpl-velocity")
            }

            this.argument("reload", LiteralArgumentType.literal()) {
                runCatching {
                    ResourceLoaderPaper.instance // ASSERT THAT SERVER IS PAPER
                    this@argument.argument("paper_player", PaperPlayerArgumentType.player()) {
                        this.execute { context ->
                            val sender = context.sender
                            val player = context.arguments.byType<PaperPlayer>("paper_player")?.value ?: return@execute

                            scheduleAsync {
                                ResourcePackData.deleteCached()
                                player.sendServerResourcePacks()
                                sender.sendMiniMessage(Messages.translate(sender.locale, "rpl.reload.single", player.name))
                            }
                        }
                    }
                }
                runCatching {
                    ResourceLoaderVelocity.instance // ASSERT THAT SERVER IS VELOCITY
                    this@argument.argument("proxied_player", VelocityPlayerArgumentType.player()) {
                        this.execute { context ->
                            val sender = context.sender
                            val player = context.arguments.byType<ProxyPlayer>("proxied_player")?.value ?: return@execute

                            scheduleAsync {
                                ResourcePackData.deleteCached()
                                player.sendServerResourcePacks()
                                sender.sendMiniMessage(Messages.translate(sender.locale, "rpl.reload.single", player.username))
                            }
                        }
                    }
                }

                this.execute { context ->
                    val sender = context.sender
                    scheduleAsync {
                        ResourcePackData.deleteCached()
                        val list: List<Audience>? = runCatching {
                            // ASSERT THAT SERVER IS PAPER
                            return@runCatching ResourceLoaderPaper.instance.server.onlinePlayers.map { paperPlayer ->
                                paperPlayer.sendServerResourcePacks()
                                paperPlayer as Audience
                            }
                        }.getOrNull() ?: runCatching {
                            // ASSERT THAT SERVER IS VELOCITY
                            return@runCatching ResourceLoaderVelocity.instance.server.allPlayers.map { proxiedPlayer ->
                                proxiedPlayer.sendServerResourcePacks()
                                proxiedPlayer as Audience
                            }
                        }.getOrNull()

                        val size = list?.size ?: 0
                        if (size == 0) {
                            sender.sendMiniMessage(
                                Messages.translate(sender.locale, "rpl.reload.none"),
                                PrefixType.WARN
                            )
                            return@scheduleAsync
                        }

                        sender.sendMiniMessage(Messages.translate(sender.locale, "rpl.reload.num", size))
                    }
                }
            }
        }
    }
}
