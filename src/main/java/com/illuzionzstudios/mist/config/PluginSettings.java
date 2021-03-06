/**
 * Copyright © 2020 Property of Illuzionz Studios, LLC
 * All rights reserved. No part of this publication may be reproduced, distributed, or
 * transmitted in any form or by any means, including photocopying, recording, or other
 * electronic or mechanical methods, without the prior written permission of the publisher,
 * except in the case of brief quotations embodied in critical reviews and certain other
 * noncommercial uses permitted by copyright law. Any licensing of this software overrides
 * this statement.
 */
package com.illuzionzstudios.mist.config;

import com.illuzionzstudios.mist.Mist;
import com.illuzionzstudios.mist.config.locale.Locale;
import com.illuzionzstudios.mist.plugin.SpigotPlugin;

/**
 * An implementation of basic plugin settings. This handles settings that
 * all plugins with this library will have. For instance locale, main command etc.
 * Typically used as the "config.yml"
 *
 * This should be implemented by our {@link SpigotPlugin} and
 * define our own {@link ConfigSetting} specific to the plugin
 */
public abstract class PluginSettings extends YamlConfig {

    /**
     * The current loaded {@link PluginSettings} instance
     */
    public static YamlConfig SETTINGS_FILE;

    /**
     * @param plugin Make sure we pass owning plugin
     */
    public PluginSettings(SpigotPlugin plugin) {
        super(plugin, getSettingsFileName());
    }

    /**
     * @return Get the file name for these settings, by default config.yml
     */
    protected static String getSettingsFileName() {
        return Mist.SETTINGS_NAME;
    }

    @Override
    public final boolean load() {
        return loadResourceToServer("", "config.yml");
    }

    //  -------------------------------------------------------------------------
    //  Main config settings provided by default
    //  -------------------------------------------------------------------------

    /**
     * Main plugin settings
     */
    public static class Settings {

        /**
         * The locale type to use, for instance
         * "en_US"
         */
        public static ConfigSetting LOCALE;

        public static void init() {
            // Update to set
            LOCALE = new ConfigSetting(SETTINGS_FILE, "Settings.Locale", "en_US",
                    "The language file to use for the plugin",
                    "More language files (if available) can be found in the plugins locale folder.");
        }
    }

    /**
     * Load these {@link PluginSettings} into the server, setting values
     * if not there, or loading the values into memory
     *
     * Call in the {@link SpigotPlugin#onPluginEnable()} to load plugin settings
     *
     * @param settings The instance of {@link PluginSettings} to load
     */
    public static void loadSettings(PluginSettings settings) {
        SETTINGS_FILE = settings;
        // Load settings file
        settings.load();

        // Load all settings
        Settings.init();

        // Load our other custom settings
        settings.loadSettings();
        settings.saveChanges();
    }

    /**
     * Invoked to load all other custom settings that we implement
     * in our own {@link PluginSettings}
     */
    public abstract void loadSettings();

}
