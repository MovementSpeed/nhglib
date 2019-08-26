package io.github.movementspeed.nhglib.utils.data

/**
 * Created by Fausto Napoli on 25/10/2016.
 */
class Strings {
    object Messages {
        const val cannotQueueAssetFileNotFound = "Cannot queue asset \"%s\". File does not exist."
        const val assetLoaded = "Asset \"%s\" has been loaded."
        const val nullAssetSource = "Asset source cannot be null."
        const val nullOrEmptyAssetSource = "Asset source cannot be null or empty."
        const val controllerConnected = "Controller %s connected."
        const val controllerDisconnected = "Controller %s disconnected."
    }

    object Defaults {
        const val assetKey = "nhg_asset"
        const val assetPackageKey = "nhg_asset_package"
        const val inputKey = "nhg_input"
        const val sceneKey = "nhg_scene"
    }

    object Events {
        const val engineDestroy = "nhg_event_engine_destroy"
        const val enginePause = "nhg_event_engine_pause"
        const val engineResume = "nhg_event_engine_resume"
        const val assetLoadingFinished = "nhg_event_asset_loading_finished"
        const val assetLoaded = "nhg_event_asset_loaded"
        const val assetPackageLoaded = "nhg_event_asset_package_loaded"
        const val assetUnloaded = "nhg_event_asset_unloaded"
        const val keyInputTriggered = "nhg_event_input_triggered"
        const val sceneLoaded = "nhg_event_scene_loaded"
    }

    object RenderingSettings {
        const val forceUnlitKey = "nhg_force_unlit"
    }
}
