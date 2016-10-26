package io.github.voidzombie.nhglib.utils.data;

/**
 * Created by Fausto Napoli on 25/10/2016.
 */
public class Strings {
    public final Messages messages = new Messages();
    public final Defaults defaults = new Defaults();
    public final Notifications notifications = new Notifications();

    public class Messages {
        public final String cannotQueueAssetFileNotFound = "Cannot queue asset \"%s\". File does not exist.";
        public final String assetLoaded = "Asset \"%s\" has been loaded.";
        public final String nullLoadingListener = "Attempt to add a null AssetLoadingListener.";
        public final String nullAssetSource = "Asset source cannot be null.";
    }

    public class Defaults {
        public final String modelsPath = "models/";
    }

    public class Notifications {
        public final String assetLoadingFinished = "nhg_notification_asset_loading_finished";
        public final String assetLoaded = "nhg_notification_asset_loaded";
    }
}
