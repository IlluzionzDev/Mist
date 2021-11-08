package com.illuzionzstudios.mist.model

import com.google.common.io.Resources
import com.google.common.net.HttpHeaders
import com.illuzionzstudios.mist.config.locale.PluginLocale
import com.illuzionzstudios.mist.plugin.SpigotPlugin
import com.illuzionzstudios.mist.scheduler.MinecraftScheduler
import org.bukkit.command.CommandSender
import java.io.IOException
import java.net.HttpURLConnection
import java.net.URL
import java.nio.charset.Charset
import java.util.*
import java.util.function.BiConsumer
import javax.net.ssl.HttpsURLConnection

/**
 * Util class to check for updates for this plugin
 */
object UpdateChecker {
    private const val SPIGOT_URL = "https://api.spigotmc.org/legacy/update.php?resource=%d"

    /**
     * Check for latest version
     *
     * @param callback Callback to call
     */
    fun check(callback: BiConsumer<VersionType, String?>) {
        MinecraftScheduler.Companion.get()!!.desynchronize(Runnable {
            try {
                val resourceId: Int = SpigotPlugin.Companion.getInstance().getPluginId()
                val httpURLConnection: HttpURLConnection =
                    URL(String.format(SPIGOT_URL, resourceId)).openConnection() as HttpsURLConnection
                httpURLConnection.requestMethod = "GET"
                httpURLConnection.setRequestProperty(HttpHeaders.USER_AGENT, "Mozilla/5.0")
                val currentVersion: String = SpigotPlugin.Companion.getPluginVersion()
                val fetchedVersion = Resources.toString(httpURLConnection.url, Charset.defaultCharset())
                val devVersion = currentVersion.contains("DEV")
                val latestVersion = fetchedVersion.equals(currentVersion, ignoreCase = true)
                MinecraftScheduler.Companion.get()!!.synchronize(Runnable {
                    callback.accept(
                        if (latestVersion) VersionType.LATEST else if (devVersion) VersionType.EXPERIMENTAL else VersionType.OUTDATED,
                        if (latestVersion) currentVersion else fetchedVersion
                    )
                })
            } catch (exception: IOException) {
                MinecraftScheduler.Companion.get()!!
                    .synchronize(Runnable { callback.accept(VersionType.UNKNOWN, null) })
            }
        })
    }

    /**
     * Check with a sender for a new version and notify of anything
     *
     * @param sender The sender to check
     */
    fun checkVersion(sender: CommandSender) {
        if (!SpigotPlugin.Companion.getInstance().isCheckUpdates()) return
        check { version: VersionType, name: String? ->
            // Only notify if new version available in console
            if (version == VersionType.OUTDATED) PluginLocale.Companion.UPDATE_AVAILABLE
                .toString("plugin_name", SpigotPlugin.Companion.getPluginName())
                .toString("current", SpigotPlugin.Companion.getPluginVersion())
                .toString("new", name)
                .toString("status", version.name.lowercase(Locale.getDefault()))
                .toString("resource_id", SpigotPlugin.Companion.getInstance().getPluginId())
                .sendMessage(sender)
        }
    }

    /**
     * Type of version found
     */
    enum class VersionType {
        /**
         * Version couldn't be found
         */
        UNKNOWN,

        /**
         * The version is old and a new is available
         */
        OUTDATED,

        /**
         * The current version is the latest
         */
        LATEST,

        /**
         * Running a development build
         */
        EXPERIMENTAL
    }
}