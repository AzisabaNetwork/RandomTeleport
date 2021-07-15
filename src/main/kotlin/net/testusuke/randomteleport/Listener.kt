package net.testusuke.randomteleport

import net.testusuke.randomteleport.Main.Companion.configuration
import net.testusuke.randomteleport.Main.Companion.plugin
import net.testusuke.randomteleport.Main.Companion.prefix
import org.bukkit.Location
import org.bukkit.World
import org.bukkit.block.Block
import org.bukkit.block.Sign
import org.bukkit.entity.Player
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.block.BlockBreakEvent
import org.bukkit.event.block.SignChangeEvent
import org.bukkit.event.player.PlayerInteractEvent
import java.util.Random

object Listener : Listener {

    @EventHandler
    fun onBlockClick(event: PlayerInteractEvent) {
        val player = event.player
        val block = event.clickedBlock ?: return
        val sign = block.state
        if (sign is Sign) {
            if (!sign.isTeleportSign()) return
            if (!player.hasPermission(Permission.GENERAL)) {
                player.sendPermissionError()
                return
            }
            val name = sign.getLine(2)
            if (!configuration.isExistPoint(name)) {
                player.sendMessagePrefix("§cテレポート先が見つかりませんでした...")
                return
            }
            val point = configuration.getPoint(name) ?: return
            player.sendMessagePrefix("§aテレポートします...")
            player.randomTeleport(point)
        }
    }

    @EventHandler
    fun onBlockBreak(event: BlockBreakEvent) {
        val player = event.player
        val sign = event.block.state
        if (sign is Sign) {
            if (!sign.isTeleportSign()) return
            if (!player.hasPermission(Permission.ADMIN)) {
                player.sendPermissionError()
                event.isCancelled = true
                return
            }
            player.sendMessagePrefix("§aテレポーターを削除しました。")
        }
    }

    @EventHandler
    fun onSign(event: SignChangeEvent) {
        val player = event.player
        if (event.getLine(0).equals("[RandomTP]")) {
            if (!player.hasPermission(Permission.ADMIN)) {
                player.sendPermissionError()
            }
            val name = event.getLine(1)
            if (name == null || !configuration.isExistPoint(name)) {
                player.sendMessagePrefix("§cそのようなポイントは見つかりませんでした。")
                return
            }

            event.setLine(0, "§e===============")
            event.setLine(1, prefix)
            event.setLine(2, name)
            event.setLine(3, "§e===============")
            player.sendMessagePrefix("§aテレポーターを作成しました。")
        }
    }

    private fun Player.sendPermissionError() {
        sendMessage("$prefix§cあなたに権限はありません")
    }

    private fun Player.sendMessagePrefix(message: String) {
        sendMessage("$prefix$message")
    }

    private fun Sign.isTeleportSign(): Boolean {
        return getLine(1) == prefix
    }

    fun Player.randomTeleport(point: Point) {
        plugin.randomGenerateThread.execute {
            val location = point.world.getRandomLocation()
            if (location == null) {
                sendMessagePrefix("§c安全地域が見つかりませんでした...")
                return@execute
            }
            sendMessagePrefix("§a安全地域を見つけました!")
            plugin.runTaskSync {
                teleport(location)
            }
        }
    }

    private fun World.getRandomLocation(): Location? {
        val start = System.currentTimeMillis()
        var location = generateRandomLocation(this)
        while (!location.isSafetyLocation()) {
            if (start + 3000 < System.currentTimeMillis()) {
                return null
            }
            location = generateRandomLocation(this)
        }
        return location
    }

    private fun Location.isSafetyLocation(): Boolean {
        fun Block.isUnSafety() = type.isOccluding || isLiquid

        if (block.isUnSafety()) return false
        if (block.getRelative(0, 1, 0).isUnSafety()) return false
        if (!block.getRelative(0, -1, 0).type.isOccluding) return false
        return true
    }

    private fun generateRandomLocation(world: World): Location {
        val size = world.worldBorder.size
        val rand = Random()
        val x = rand.nextInt(size.toInt()) - (size.toInt() / 2)
        val z = rand.nextInt(size.toInt()) - (size.toInt() / 2)
        val y = world.getHighestBlockYAt(x, z) + 1
        return Location(world, x.toDouble(), y.toDouble(), z.toDouble())
    }
}
