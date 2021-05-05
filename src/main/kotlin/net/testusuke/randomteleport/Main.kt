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
        //  instance
        plugin = this

        //  prepare thread pool
        randomGenerateThread = Executors.newFixedThreadPool(10)

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

        //  kill thread pool
        randomGenerateThread.shutdown()
    }
}
