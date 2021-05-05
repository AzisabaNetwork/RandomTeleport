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
        //  permission
        if (!sender.hasPermission(Permission.GENERAL)) {
            sender.sendPermissionError()
            return false
        }

        //  /randomtp
        if (args.isEmpty()) {
            //  get world
            val world = sender.world
            val worldName = world.name
            //  does world include points
            for (point in configuration.points.values) {
                if (point.world.name == worldName) {
                    sender.sendMessagePrefix("§aテレポートします...")
                    sender.randomTeleport(point)
                    return true
                }
            }

            sender.sendMessagePrefix("§cこのワールドでは許可されていません。")
            return false
        }

        //  /randomtp <...>
        if (args.isNotEmpty()) {
            when (args[0]) {
                "help" -> {
                    //  sendHelp
                    sender.sendHelp()
                    return true
                }

                "reload" -> {
                    if (!sender.hasPermission(Permission.ADMIN)) {
                        sender.sendPermissionError()
                        return false
                    }
                    //  reload
                    plugin.reloadConfig()
                    configuration.loadConfig()
                    //  message
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
        }
        return false
    }

    private fun Player.sendHelp() {
        val msg = """
            §e========================================
            §6/randomtp <- ヘルプの表示
            §6/randomtp reload <- コンフィグのリロード
            §6/randomtp list <- 登録済みテレポート先を表示
            §dcreated by testusuke  
            §e========================================
        """.trimIndent()
        this.sendMessage(msg)
    }

    private fun Player.sendPermissionError() {
        this.sendMessage("$prefix§cあなたに権限はありません")
    }

    private fun Player.sendMessagePrefix(message: String) {
        this.sendMessage("${prefix}$message")
    }
}
