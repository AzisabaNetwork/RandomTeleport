package net.testusuke.randomteleport

import net.testusuke.randomteleport.Main.Companion.plugin
import org.bukkit.ChatColor
import java.security.cert.PolicyNode

class Configuration {

    private val nameLocationMap = mutableMapOf<String, Point>()

    init {
        //  logger
        plugin.logger.info("loading configuration...")
        //  load
        this.loadConfig()
    }

    fun loadConfig() {
        //  init
        nameLocationMap.clear()

        //  get config
        val config = plugin.config
        //  get section
        val section = config.getConfigurationSection("point")
        if (section == null) {
            plugin.logger.warning("${ChatColor.RED}failed to load configuration!")
            return
        }
        //  load
        section.getKeys(false).forEach {
            val name = it
            val worldName = config.getString("point.${it}.world") ?: return@forEach
            val world = plugin.server.getWorld(worldName) ?: return@forEach

            val point = Point(name = name, world = world)
            //  insert
            nameLocationMap[it] = point
        }

        //  logger
        plugin.logger.info("loaded configuration")
    }

    fun listOfPoint(): MutableCollection<Point> {
        return nameLocationMap.values
    }

    fun isExistPoint(name: String): Boolean {
        return nameLocationMap.contains(name)
    }

    fun getPoint(name: String): Point? {
        return nameLocationMap[name]
    }
}