package net.testusuke.randomteleport

import net.testusuke.randomteleport.Listener.randomTeleport
import net.testusuke.randomteleport.Main.Companion.configuration
import net.testusuke.randomteleport.Main.Companion.plugin
import net.testusuke.randomteleport.Main.Companion.prefix
import org.bukkit.command.Command
import org.bukkit.command.CommandExecutor
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player

object Command : CommandExecutor {

    override fun onCommand(sender: CommandSender, command: Command, label: String, args: Array<out String>): Boolean {
        if (sender !is Player) return false
        if (!sender.hasPermission(Permission.GENERAL)) {
            sender.sendPermissionError()
            return false
        }

        when (args.getOrNull(0)) {
            null -> {
                val world = sender.world
                val worldName = world.name
                //  get point of sender's world
                val point = configuration.points.values.firstOrNull { it.world.name == worldName }
                return if (point != null) {
                    sender.sendMessagePrefix("§aテレポートします...")
                    sender.randomTeleport(point)
                    true
                } else {
                    sender.sendMessagePrefix("§cこのワールドでは許可されていません。")
                    false
                }
            }
            "help" -> {
                sender.sendHelp()
                return true
            }

            "reload" -> {
                if (!sender.hasPermission(Permission.ADMIN)) {
                    sender.sendPermissionError()
                    return false
                }
                plugin.reloadConfig()
                configuration.loadConfig()
                sender.sendMessagePrefix("§aコンフィグを再読み込みしました。")
            }

            "list" -> {
                if (!sender.hasPermission(Permission.ADMIN)) {
                    sender.sendPermissionError()
                    return false
                }

                sender.sendMessage(
                    """
                    §e========================================
                    §d登録済みテレポート先
                    """.trimIndent()
                )
                configuration.points.forEach { (name, point) ->
                    sender.sendMessage("$name -> ${point.world.name}")
                }
                sender.sendMessage("§e========================================")
            }
        }
        return false
    }

    private fun Player.sendHelp() {
        sendMessage(
            """
            §e========================================
            §6/randomtp <- ヘルプの表示
            §6/randomtp reload <- コンフィグのリロード
            §6/randomtp list <- 登録済みテレポート先を表示
            §dcreated by testusuke  
            §e========================================
            """.trimIndent()
        )
    }

    private fun Player.sendPermissionError() {
        sendMessagePrefix("§cあなたに権限はありません")
    }

    private fun Player.sendMessagePrefix(message: String) {
        sendMessage("${prefix}$message")
    }
}
