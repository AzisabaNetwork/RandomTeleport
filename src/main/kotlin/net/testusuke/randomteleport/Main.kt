package net.testusuke.randomteleport

import org.bukkit.plugin.java.JavaPlugin

class Main : JavaPlugin() {

    companion object {
        lateinit var plugin: Main
        lateinit var configuration: Configuration

        val prefix = "§e[§6Random§aTeleport§e]"
    }

    override fun onEnable() {
        //  instance
        plugin = this

        //  Listener
        server.pluginManager.registerEvents(Listener, this)
        //  Command
        getCommand("randomtp")?.setExecutor(Command)
        //  config
        saveDefaultConfig()
        //  load config
        configuration = Configuration()

    }

    override fun onDisable() {

    }
}