package io.github.voidzombie.nhglib.utils.data;

/**
 * Created by Fausto Napoli on 25/10/2016.
 */
public class Strings {
    public final Messages messages = new Messages();
    public final Defaults defaults = new Defaults();
    public final Events events = new Events();

    public class Messages {
        public final String cannotQueueAssetFileNotFound = "Cannot queue asset \"%s\". File does not exist.";
        public final String assetLoaded = "Asset \"%s\" has been loaded.";
        public final String nullAssetSource = "Asset source cannot be null.";
    }

    public class Defaults {
        public final String modelsPath = "models/";
        public final String assetKey = "asset";
    }

    public class Events {
        public final String engineDestroy = "nhg_event_engine_destroy";
        public final String assetLoadingFinished = "nhg_event_asset_loading_finished";
        public final String assetLoaded = "nhg_event_asset_loaded";
        public final String sceneLoaded = "nhg_event_scene_loaded";
    }
}
