package net.testusuke.randomteleport

import org.bukkit.plugin.java.JavaPlugin

fun JavaPlugin.runTaskSync(runnable: Runnable) {
    server.scheduler.runTask(this, runnable)
}
