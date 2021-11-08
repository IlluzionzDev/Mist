package com.illuzionzstudios.mist.ui.type

import com.cryptomorin.xseries.XMaterial
import com.illuzionzstudios.mist.config.locale.PluginLocale
import com.illuzionzstudios.mist.item.ItemCreator
import com.illuzionzstudios.mist.ui.UserInterface
import com.illuzionzstudios.mist.ui.button.Button
import com.illuzionzstudios.mist.ui.button.Button.ButtonListener
import org.bukkit.entity.Player
import org.bukkit.event.inventory.ClickType
import org.bukkit.event.inventory.InventoryClickEvent
import org.bukkit.inventory.ItemStack
import java.util.function.Consumer

/**
 * Simple interface to confirm an action
 */
class ConfirmUI(confirmAction: Consumer<Boolean>) : UserInterface(null, false) {
    /**
     * Action to run when after answered.
     * Returns whether was accepted or not
     */
    val confirmAction: Consumer<Boolean>

    /**
     * Button to deny
     */
    val denyButton: Button

    /**
     * Button to confirm
     */
    val confirmButton: Button
    override fun getItemAt(slot: Int): ItemStack? {
        if (slot == 11) {
            return confirmButton.item
        } else if (slot == 15) {
            return denyButton.item
        }

        // Else placeholder item
        return ItemCreator.builder().name(" ").material(XMaterial.BLACK_STAINED_GLASS_PANE).build().makeUIItem()
    }

    /**
     * @param confirmAction Action to run after confirmed/answered
     */
    init {
        title = "&8Are you sure?"
        size = 27
        this.confirmAction = confirmAction
        denyButton = Button.Companion.of(ItemCreator.builder()
            .material(XMaterial.RED_DYE)
            .name(PluginLocale.Companion.INTERFACE_CONFIRM_CONFIRM_NAME.toString())
            .lore(PluginLocale.Companion.INTERFACE_CONFIRM_CONFIRM_LORE.toString())
            .glow(true)
            .build(),
            ButtonListener { player: Player?, ui: UserInterface?, clickType: ClickType?, event: InventoryClickEvent? ->
                confirmAction.accept(
                    false
                )
            })
        confirmButton = Button.Companion.of(ItemCreator.builder()
            .material(XMaterial.LIME_DYE)
            .name(PluginLocale.Companion.INTERFACE_CONFIRM_DENY_NAME.toString())
            .lore(PluginLocale.Companion.INTERFACE_CONFIRM_DENY_LORE.toString())
            .glow(true)
            .build(),
            ButtonListener { player: Player?, ui: UserInterface?, clickType: ClickType?, event: InventoryClickEvent? ->
                confirmAction.accept(
                    true
                )
            })
    }
}