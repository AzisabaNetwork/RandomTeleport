package net.testusuke.randomteleport

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
            //  sendHelp
            sender.sendHelp()
            return true
        }

        //  /randomtp <...>
        if (args.isNotEmpty()) {
            when (args[0]) {
                "reload" -> {
                    if (!sender.hasPermission(Permission.ADMIN)) {
                        sender.sendPermissionError()
                        return false
                    }
                    //  reload
                    configuration.loadConfig()
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
                    val list = configuration.listOfPoint()
                    list.forEach {
                        sender.sendMessage("${it.name} -> ${it.world.name}")
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
            §d    created by testusuke  
            §e========================================
        """.trimIndent()
        this.sendMessage(msg)
    }

    private fun Player.sendPermissionError() {
        this.sendMessage("${prefix}§cあなたに権限はありません")
    }

    private fun Player.sendMessagePrefix(message: String) {
        this.sendMessage("${prefix}${message}")
    }
}