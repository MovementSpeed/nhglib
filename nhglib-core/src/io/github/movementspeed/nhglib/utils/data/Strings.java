package io.github.movementspeed.nhglib.utils.data;

/**
 * Created by Fausto Napoli on 25/10/2016.
 */
public class Strings {
    public static class Messages {
        public final static String cannotQueueAssetFileNotFound = "Cannot queue asset \"%s\". File does not exist.";
        public final static String assetLoaded = "Asset \"%s\" has been loaded.";
        public final static String nullAssetSource = "Asset source cannot be null.";
        public final static String nullOrEmptyAssetSource = "Asset source cannot be null or empty.";
        public final static String controllerConnected = "Controller %s connected.";
        public final static String controllerDisconnected = "Controller %s disconnected.";
    }

    public static class Defaults {
        public final static String assetKey = "nhg_asset";
        public final static String assetPackageKey = "nhg_asset_package";
        public final static String inputKey = "nhg_input";
        public final static String sceneKey = "nhg_scene";
    }

    public static class Events {
        public final static String engineDestroy = "nhg_event_engine_destroy";
        public final static String assetLoadingFinished = "nhg_event_asset_loading_finished";
        public final static String assetLoaded = "nhg_event_asset_loaded";
        public final static String assetPackageLoaded = "nhg_event_asset_package_loaded";
        public final static String assetUnloaded = "nhg_event_asset_unloaded";
        public final static String keyInputTriggered = "nhg_event_input_triggered";
        public final static String sceneLoaded = "nhg_event_scene_loaded";
    }

    public static class RenderingSettings {
        public final static String forceUnlitKey = "nhg_force_unlit";
    }
}
