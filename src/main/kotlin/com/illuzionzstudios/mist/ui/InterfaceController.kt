package com.illuzionzstudios.mist.ui

import com.illuzionzstudios.mist.Logger.Companion.displayError
import com.illuzionzstudios.mist.controller.PluginController
import com.illuzionzstudios.mist.plugin.SpigotPlugin
import com.illuzionzstudios.mist.scheduler.MinecraftScheduler
import com.illuzionzstudios.mist.scheduler.rate.Rate
import com.illuzionzstudios.mist.scheduler.rate.Sync
import com.illuzionzstudios.mist.ui.render.ClickLocation
import com.illuzionzstudios.mist.util.TextUtil
import com.illuzionzstudios.mist.util.TextUtil.formatText
import org.bukkit.Bukkit
import org.bukkit.entity.Player
import org.bukkit.event.*
import org.bukkit.event.inventory.InventoryAction
import org.bukkit.event.inventory.InventoryClickEvent
import org.bukkit.event.inventory.InventoryCloseEvent
import org.bukkit.event.inventory.InventoryType

/**
 * This controller handles events for [UserInterface]
 */
enum class InterfaceController : PluginController, Listener {
    INSTANCE;

    override fun initialize(plugin: SpigotPlugin?) {
        MinecraftScheduler.Companion.get()!!.registerSynchronizationService(this)
    }

    override fun stop(plugin: SpigotPlugin?) {
        MinecraftScheduler.Companion.get()!!.dismissSynchronizationService(this)

        // Try close inventories
        try {
            for (online in Bukkit.getServer().onlinePlayers) {
                val userInterface: UserInterface = UserInterface.Companion.getInterface(online)
                if (userInterface != null) online.closeInventory()
            }
        } catch (t: Throwable) {
            Logger.displayError(t, "Error closing menu inventories for players..")
            t.printStackTrace()
        }
    }

    /**
     * Tick all interfaces
     */
    @Sync(rate = Rate.TICK)
    fun tick() {
        for (player in Bukkit.getOnlinePlayers()) {
            val userInterface: UserInterface = UserInterface.Companion.getInterface(player)
            if (userInterface != null) userInterface.tick()
        }
    }

    /**
     * This event handles closing of [UserInterface]
     */
    @EventHandler(priority = EventPriority.NORMAL, ignoreCancelled = true)
    fun onInterfaceClose(event: InventoryCloseEvent) {
        if (event.player !is Player) return
        val player = event.player as Player
        val userInterface: UserInterface = UserInterface.Companion.getInterface(player)
        if (userInterface != null) {
            userInterface.onInterfaceClose(player, event.inventory)
            player.removeMetadata(UserInterface.Companion.TAG_CURRENT, SpigotPlugin.Companion.getInstance())
        }
    }

    /**
     * Handles invoking the [UserInterface] click listeners
     */
    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    fun onMenuClick(event: InventoryClickEvent) {
        if (event.whoClicked !is Player) return
        val player = event.whoClicked as Player
        val userInterface: UserInterface = UserInterface.Companion.getInterface(player)
        if (userInterface != null) {
            val slotItem = event.currentItem
            val cursor = event.cursor
            val clickedInv = event.clickedInventory
            val action = event.action
            val whereClicked =
                if (clickedInv != null) if (clickedInv.type == InventoryType.CHEST) ClickLocation.INTERFACE else ClickLocation.PLAYER_INVENTORY else ClickLocation.OUTSIDE
            if (action.toString().contains("PICKUP") || action.toString().contains("PLACE")
                || action.toString() == "SWAP_WITH_CURSOR" || action == InventoryAction.CLONE_STACK
            ) {
                if (whereClicked == ClickLocation.INTERFACE) try {
                    val button = userInterface.getButton(slotItem)
                    if (button != null) userInterface.onButtonClick(
                        player,
                        event.slot,
                        action,
                        event.click,
                        button,
                        event
                    ) else userInterface.onInterfaceClick(
                        player, event.slot, action, event.click, cursor, slotItem,
                        false, event
                    )
                } catch (t: Throwable) {
                    // Notify of error
                    player.sendMessage(TextUtil.formatText("&cOops! There was a problem with this menu! Please contact the administrator to review the console for details."))
                    player.closeInventory()
                    Logger.displayError(t, "Error clicking in menu $userInterface")
                }
            } else if (action == InventoryAction.MOVE_TO_OTHER_INVENTORY
                || whereClicked != ClickLocation.PLAYER_INVENTORY
            ) {
                event.result = Event.Result.DENY
                player.updateInventory()
            }
        }
    }
}