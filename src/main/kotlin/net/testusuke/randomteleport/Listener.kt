package net.testusuke.randomteleport

import net.testusuke.randomteleport.Main.Companion.configuration
import net.testusuke.randomteleport.Main.Companion.plugin
import net.testusuke.randomteleport.Main.Companion.prefix
import org.bukkit.Bukkit
import org.bukkit.Location
import org.bukkit.Material
import org.bukkit.block.Sign
import org.bukkit.entity.Player
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.block.BlockBreakEvent
import org.bukkit.event.block.SignChangeEvent
import org.bukkit.event.player.PlayerInteractEvent
import java.util.*
import org.bukkit.World

object Listener : Listener {

    //  onClick
    @EventHandler
    fun onBlockClick(event: PlayerInteractEvent) {
        val player = event.player
        //  block type
        val block = event.clickedBlock ?: return

        val state = block.state
        if (state is Sign) {
            //  cast
            val sign = state
            //  check
            if (!sign.isTeleportSign()) return

            //  Permission
            if (!player.hasPermission(Permission.GENERAL)) {
                player.sendPermissionError()
                return
            }

            //  get point
            val name = sign.getLine(2)
            //  isExist
            if (!configuration.isExistPoint(name)) {
                player.sendMessagePrefix("§cテレポート先が見つかりませんでした...")
                return
            }
            val point = configuration.getPoint(name) ?: return

            //  Teleport
            player.sendMessagePrefix("§aテレポートします...")
            player.randomTeleport(point)
        }
    }

    //  onBlockBreak
    @EventHandler
    fun onBlockBreak(event: BlockBreakEvent) {
        val player = event.player
        val state = event.block.state
        //  block
        if (state is Sign) {
            //  is tp's sign
            val sign = state
            //  check
            if (!sign.isTeleportSign()) return

            //  Permission
            if (!player.hasPermission(Permission.ADMIN)) {
                player.sendPermissionError()
                event.isCancelled = true
                return
            }
            //  break
            player.sendMessagePrefix("§aテレポーターを削除しました。")
        }
    }

    //  onSign
    @EventHandler
    fun onSign(event: SignChangeEvent) {
        val player = event.player
        //  is sign for random tp
        if (event.getLine(0).equals("[RandomTP]")) {
            //  permission
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
            //  message
            player.sendMessagePrefix("§aテレポーターを作成しました。")
        }
    }

    //  Utils
    private fun Player.sendPermissionError() {
        this.sendMessage("${Main.prefix}§cあなたに権限はありません")
    }

    private fun Player.sendMessagePrefix(message: String) {
        this.sendMessage("${prefix}${message}")
    }

    //  check sign for teleport
    private fun Sign.isTeleportSign(): Boolean {
        return this.getLine(1) == prefix
    }

    //////////////////
    //  Teleport    //
    //////////////////
    fun Player.randomTeleport(point: Point) {
        plugin.randomGenerateThread.execute( Runnable {
            val location = point.world.getRandomLocation()
            if (location == null) {
                this.sendMessagePrefix("§c安全地域が見つかりませんでした...")
                return@Runnable
            }
            //  teleport
            this.sendMessagePrefix("§a安全地域を見つけました!")
            //  main thread
            Bukkit.getServer().scheduler.runTask(plugin, Runnable {
                this.teleport(location)
            })
        })
    }

    private fun World.getRandomLocation(): Location? {
        //  time
        val start = System.currentTimeMillis()
        //  generate
        var location = generateRandomLocation(this)
        while (!location.isSafetyLocation()) {
            //  check time
            if(start + 3000 < System.currentTimeMillis()){
                return null
            }
            //  generate
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