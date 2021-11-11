package com.illuzionzstudios.mist.config.serialization.loader

import com.illuzionzstudios.mist.Logger
import com.illuzionzstudios.mist.Logger.Companion.displayError
import com.illuzionzstudios.mist.plugin.SpigotPlugin
import lombok.*
import java.io.File

/**
 * Loads all files from a directory with a certain file loader
 *
 * @param <T> The type of file to load
</T> */
class DirectoryLoader<T : FileLoader<*>?>(
    clazz: Class<T>,
    /**
     * The directory that is being loaded
     */
    protected val directory: String
) {
    /**
     * All file loaders for files in directory. From these we can then create the objects
     */
    @Getter
    private val loaders: MutableList<T>

    /**
     * Class of the loader
     */
    private val clazz: Class<T>
    fun load() {
        // Reward directory
        val dir = File(SpigotPlugin.instance!!.dataFolder.path + File.separator + directory)

        // If doesn't exist and has to create no point loading
        if (dir.listFiles() == null || !dir.exists()) {
            return
        }

        // Go through files
        for (file in dir.listFiles()) {
            // Get name without extension
            val name = file.name.split("\\.".toRegex()).toTypedArray()[0]
            try {
                val loader = clazz.getConstructor(String::class.java, String::class.java).newInstance(
                    directory, name
                )

                // Make sure the file extension matches the loader
                if (file.name.split("\\.".toRegex()).toTypedArray()[1].equals(
                            loader!!.extension,
                            ignoreCase = true
                        )
                    ) // Add to cache
                        loaders.add(loader)
            } catch (e: Exception) {
                Logger.displayError(e, "Could not not load file " + file.name)
            }
        }
    }

    /**
     * @param directory to load from
     * @param clazz     The class for the file loader
     */
    init {
        loaders = ArrayList()
        this.clazz = clazz
        load()
    }
}