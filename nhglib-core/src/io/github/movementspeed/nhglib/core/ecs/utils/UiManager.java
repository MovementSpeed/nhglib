package io.github.movementspeed.nhglib.core.ecs.utils;

import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.glutils.FrameBuffer;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.ArrayMap;
import com.badlogic.gdx.utils.viewport.ExtendViewport;
import io.github.movementspeed.nhglib.assets.Asset;
import io.github.movementspeed.nhglib.input.models.base.NhgInput;
import net.peakgames.libgdx.stagebuilder.core.assets.Assets;
import net.peakgames.libgdx.stagebuilder.core.assets.ResolutionHelper;
import net.peakgames.libgdx.stagebuilder.core.assets.StageBuilderFileHandleResolver;
import net.peakgames.libgdx.stagebuilder.core.builder.StageBuilder;
import net.peakgames.libgdx.stagebuilder.core.services.LocalizationService;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class UiManager {
    private boolean changesOrientation;
    private boolean initiallyEmptyStage;

    private int width;
    private int height;
    private int virtualWidth;
    private int virtualHeight;

    private Stage stage;
    private String fileName;
    private Assets assets;
    private StageBuilderFileHandleResolver fileHandleResolver;
    private StageBuilder stageBuilder;
    private ResolutionHelper resolutionHelper;
    private NhgLocalizationService localizationService;
    private FrameBuffer frameBuffer;
    private TextureRegion textureRegion;

    private Array<String> actorNames;
    private List<Vector2> supportedResolutions;
    private ArrayMap<String, NhgInput> actorInputs;

    public UiManager(String fileName, List<Vector2> supportedResolutions) {
        this(fileName, false, supportedResolutions);
    }

    public UiManager(String fileName, boolean changesOrientation, List<Vector2> supportedResolutions) {
        this(fileName, changesOrientation, false, supportedResolutions);
    }

    public UiManager(String fileName, boolean changesOrientation, boolean initiallyEmptyStage, List<Vector2> supportedResolutions) {
        this.fileName = fileName;
        this.changesOrientation = changesOrientation;
        this.initiallyEmptyStage = initiallyEmptyStage;
        this.supportedResolutions = supportedResolutions;

        Collections.sort(this.supportedResolutions, new Comparator<Vector2>() {
            @Override
            public int compare(Vector2 o1, Vector2 o2) {
                return (int) (o1.x - o2.x);
            }
        });
    }

    public void init(float virtualWidth, float virtualHeight, float width, float height, Array<Asset> dependencies) {
        this.width = (int) width;
        this.height = (int) height;
        this.virtualWidth = (int) virtualWidth;
        this.virtualHeight = (int) virtualHeight;

        this.resolutionHelper = new ResolutionHelper(
                virtualWidth, virtualHeight,
                width, height,
                findBestResolution().x);

        this.localizationService = new NhgLocalizationService();

        fileHandleResolver = new StageBuilderFileHandleResolver(this.width, supportedResolutions);
        this.assets = new Assets(fileHandleResolver, resolutionHelper);

        for (Asset dependency : dependencies) {
            assets.addAssetConfiguration(fileName, dependency.source, dependency.assetClass);
        }

        assets.loadAssetsSync(fileName);

        this.stageBuilder = new StageBuilder(assets, resolutionHelper, localizationService);
        createStage(initiallyEmptyStage);
    }

    public void renderUi(float delta) {
        stage.act(delta);
        stage.draw();
    }

    public void resize(int newWidth, int newHeight) {
        if (this.width == newWidth && this.height == newHeight) {
            return;
        }

        this.width = newWidth;
        this.height = newHeight;

        int newTargetWidth = virtualWidth;
        int newTargetHeight = virtualHeight;

        fileHandleResolver = new StageBuilderFileHandleResolver(this.width, supportedResolutions);

        if (this.height > this.width) {
            newTargetWidth = virtualHeight;
            newTargetHeight = virtualWidth;
        }

        this.resolutionHelper.resize(newTargetWidth,
                newTargetHeight,
                this.width,
                this.height);

        createStage(initiallyEmptyStage);
    }

    public void dispose() {
        stage.dispose();
    }

    /**
     * @return the resolution which has the the closest width value.
     */
    public Vector2 findBestResolution() {
        int minDiff = Integer.MAX_VALUE;
        int bestResIndex = 0;

        for (int i = 0; i < supportedResolutions.size(); i++) {
            int diff = Math.abs(width - (int) supportedResolutions.get(i).x);
            if (diff < minDiff) {
                minDiff = diff;
                bestResIndex = i;
            }
        }

        return supportedResolutions.get(bestResIndex);
    }

    public TextureRegion renderUiToTexture(float delta) {
        if (frameBuffer == null) {
            frameBuffer = new FrameBuffer(Pixmap.Format.RGBA8888, width, height, false);
            textureRegion = new TextureRegion(frameBuffer.getColorBufferTexture());
        }

        frameBuffer.begin();
        renderUi(delta);
        frameBuffer.end();

        textureRegion.setRegion(frameBuffer.getColorBufferTexture());
        //textureRegion.flip(true, true);
        return textureRegion;
    }

    public Stage getStage() {
        return stage;
    }

    private void createStage(final boolean initiallyEmptyStage) {
        if (initiallyEmptyStage) {
            stage = new Stage();
            stage.addActor(stageBuilder.createRootGroup(null));
        } else {
            stage = stageBuilder.build(fileName, new ExtendViewport(width, height));
        }

        stage.setDebugAll(true);
    }

    public class NhgLocalizationService implements LocalizationService {
        @Override
        public String getString(String s) {
            return null;
        }

        @Override
        public String getString(String s, Object... args) {
            return null;
        }
    }
}
