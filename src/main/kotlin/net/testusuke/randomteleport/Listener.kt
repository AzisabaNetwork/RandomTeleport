package net.testusuke.randomteleport

import net.testusuke.randomteleport.Main.Companion.configuration
import net.testusuke.randomteleport.Main.Companion.plugin
import net.testusuke.randomteleport.Main.Companion.prefix
import org.bukkit.Bukkit
import org.bukkit.Location
import org.bukkit.Material
import org.bukkit.World
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
        val state = block.state
        if (state is Sign) {
            val sign = state
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
        val state = event.block.state
        if (state is Sign) {
            val sign = state
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
        this.sendMessage("${Main.prefix}§cあなたに権限はありません")
    }

    private fun Player.sendMessagePrefix(message: String) {
        this.sendMessage("${prefix}$message")
    }

    private fun Sign.isTeleportSign(): Boolean {
        return this.getLine(1) == prefix
    }

    fun Player.randomTeleport(point: Point) {
        plugin.randomGenerateThread.execute(
            Runnable {
                val location = point.world.getRandomLocation()
                if (location == null) {
                    this.sendMessagePrefix("§c安全地域が見つかりませんでした...")
                    return@Runnable
                }
                this.sendMessagePrefix("§a安全地域を見つけました!")
                Bukkit.getServer().scheduler.runTask(
                    plugin,
                    Runnable {
                        this.teleport(location)
                    }
                )
            }
        )
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
        val loc = this.clone()
        if (loc.block.type.isOccluding || listOf(Material.LAVA, Material.WATER).contains(loc.block.type)) {
            return false
        }
        loc.add(0.0, 1.0, 0.0)
        if (loc.block.type.isOccluding || listOf(Material.LAVA, Material.WATER).contains(loc.block.type)) {
            return false
        }
        loc.add(0.0, -2.0, 0.0)
        if (loc.block.type.isOccluding || listOf(Material.LAVA, Material.WATER).contains(loc.block.type)) {
            return false
        }

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
