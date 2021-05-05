package net.testusuke.randomteleport

import net.testusuke.randomteleport.Main.Companion.plugin
import org.bukkit.ChatColor

class Configuration {

    private val _points = mutableMapOf<String, Point>()

    init {
        //  logger
        plugin.logger.info("loading configuration...")
        //  load
        this.loadConfig()
    }

    fun loadConfig() {
        //  init
        _points.clear()

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
            val worldName = config.getString("point.$it.world") ?: return@forEach
            val world = plugin.server.getWorld(worldName) ?: return@forEach

            val point = Point(name = name, world = world)
            //  insert
            _points[it] = point
            //  logger
            plugin.logger.info("load point <name:$name world:$worldName")
        }

        //  logger
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
