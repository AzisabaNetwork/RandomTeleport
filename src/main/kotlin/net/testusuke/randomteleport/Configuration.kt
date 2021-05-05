package net.testusuke.randomteleport

import net.testusuke.randomteleport.Main.Companion.plugin
import org.bukkit.ChatColor

class Configuration {

    private val _points = mutableMapOf<String, Point>()

    init {
        plugin.logger.info("loading configuration...")
        this.loadConfig()
    }

    fun loadConfig() {
        _points.clear()

        val config = plugin.config
        val section = config.getConfigurationSection("point")
        if (section == null) {
            plugin.logger.warning("${ChatColor.RED}failed to load configuration!")
            return
        }
        section.getKeys(false).forEach {
            val name = it
            val worldName = config.getString("point.$it.world") ?: return@forEach
            val world = plugin.server.getWorld(worldName) ?: return@forEach

            val point = Point(name = name, world = world)
            _points[it] = point
            plugin.logger.info("load point <name:$name world:$worldName")
        }

        plugin.logger.info("loaded configuration")
    }

    val points: Map<String, Point>
        get() = _points.toMap()

    fun isExistPoint(name: String): Boolean {
        return _points.contains(name)
    }

    fun getPoint(name: String): Point? {
        return _points[name]
    }
}
