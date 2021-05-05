package net.testusuke.randomteleport

import org.bukkit.plugin.java.JavaPlugin
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors

class Main : JavaPlugin() {

    companion object {
        lateinit var plugin: Main
        lateinit var configuration: Configuration

        val prefix = "§e[§6Random§aTeleport§e]"
    }

    lateinit var randomGenerateThread: ExecutorService

    override fun onEnable() {
        plugin = this
        randomGenerateThread = Executors.newFixedThreadPool(10)
        server.pluginManager.registerEvents(Listener, this)
        getCommand("randomtp")?.setExecutor(Command)
        saveDefaultConfig()
        configuration = Configuration()
    }

    override fun onDisable() {
        randomGenerateThread.shutdown()
    }
}
