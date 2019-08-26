package io.github.movementspeed.nhglib.core.ecs.utils

import com.badlogic.gdx.graphics.Pixmap
import com.badlogic.gdx.graphics.g2d.TextureRegion
import com.badlogic.gdx.graphics.glutils.FrameBuffer
import com.badlogic.gdx.math.Vector2
import com.badlogic.gdx.scenes.scene2d.Stage
import com.badlogic.gdx.utils.Array
import com.badlogic.gdx.utils.ArrayMap
import com.badlogic.gdx.utils.viewport.ExtendViewport
import io.github.movementspeed.nhglib.assets.Asset
import io.github.movementspeed.nhglib.input.models.base.NhgInput
import net.peakgames.libgdx.stagebuilder.core.assets.Assets
import net.peakgames.libgdx.stagebuilder.core.assets.ResolutionHelper
import net.peakgames.libgdx.stagebuilder.core.assets.StageBuilderFileHandleResolver
import net.peakgames.libgdx.stagebuilder.core.builder.StageBuilder
import net.peakgames.libgdx.stagebuilder.core.services.LocalizationService

import java.util.Collections
import java.util.Comparator

class UiManager(private val fileName: String, private val changesOrientation: Boolean, private val initiallyEmptyStage: Boolean, private val supportedResolutions: List<Vector2>) {

    private var width: Int = 0
    private var height: Int = 0
    private var virtualWidth: Int = 0
    private var virtualHeight: Int = 0

    var stage: Stage? = null
        private set
    private var assets: Assets? = null
    private var fileHandleResolver: StageBuilderFileHandleResolver? = null
    private var stageBuilder: StageBuilder? = null
    private var resolutionHelper: ResolutionHelper? = null
    private var localizationService: NhgLocalizationService? = null
    private var frameBuffer: FrameBuffer? = null
    private var textureRegion: TextureRegion? = null

    private val actorNames: Array<String>? = null
    private val actorInputs: ArrayMap<String, NhgInput>? = null

    constructor(fileName: String, supportedResolutions: List<Vector2>) : this(fileName, false, supportedResolutions) {}

    constructor(fileName: String, changesOrientation: Boolean, supportedResolutions: List<Vector2>) : this(fileName, changesOrientation, false, supportedResolutions) {}

    init {

        Collections.sort(this.supportedResolutions) { o1, o2 -> (o1.x - o2.x).toInt() }
    }

    fun init(virtualWidth: Float, virtualHeight: Float, width: Float, height: Float, dependencies: Array<Asset>) {
        this.width = width.toInt()
        this.height = height.toInt()
        this.virtualWidth = virtualWidth.toInt()
        this.virtualHeight = virtualHeight.toInt()

        this.resolutionHelper = ResolutionHelper(
                virtualWidth, virtualHeight,
                width, height,
                findBestResolution().x)

        this.localizationService = NhgLocalizationService()

        fileHandleResolver = StageBuilderFileHandleResolver(this.width, supportedResolutions)
        this.assets = Assets(fileHandleResolver, resolutionHelper)

        for (dependency in dependencies) {
            assets!!.addAssetConfiguration(fileName, dependency.source, dependency.assetClass)
        }

        assets!!.loadAssetsSync(fileName)

        this.stageBuilder = StageBuilder(assets, resolutionHelper, localizationService)
        createStage(initiallyEmptyStage)
    }

    fun renderUi(delta: Float) {
        stage!!.act(delta)
        stage!!.draw()
    }

    fun resize(newWidth: Int, newHeight: Int) {
        if (this.width == newWidth && this.height == newHeight) {
            return
        }

        this.width = newWidth
        this.height = newHeight

        var newTargetWidth = virtualWidth
        var newTargetHeight = virtualHeight

        fileHandleResolver = StageBuilderFileHandleResolver(this.width, supportedResolutions)

        if (this.height > this.width) {
            newTargetWidth = virtualHeight
            newTargetHeight = virtualWidth
        }

        this.resolutionHelper!!.resize(newTargetWidth.toFloat(),
                newTargetHeight.toFloat(),
                this.width.toFloat(),
                this.height.toFloat())

        createStage(initiallyEmptyStage)
    }

    fun dispose() {
        stage!!.dispose()
    }

    /**
     * @return the resolution which has the the closest width value.
     */
    fun findBestResolution(): Vector2 {
        var minDiff = Integer.MAX_VALUE
        var bestResIndex = 0

        for (i in supportedResolutions.indices) {
            val diff = Math.abs(width - supportedResolutions[i].x.toInt())
            if (diff < minDiff) {
                minDiff = diff
                bestResIndex = i
            }
        }

        return supportedResolutions[bestResIndex]
    }

    fun renderUiToTexture(delta: Float): TextureRegion {
        if (frameBuffer == null) {
            frameBuffer = FrameBuffer(Pixmap.Format.RGBA8888, width, height, false)
            textureRegion = TextureRegion(frameBuffer!!.colorBufferTexture)
        }

        frameBuffer!!.begin()
        renderUi(delta)
        frameBuffer!!.end()

        textureRegion!!.setRegion(frameBuffer!!.colorBufferTexture)
        //textureRegion.flip(true, true);
        return textureRegion
    }

    private fun createStage(initiallyEmptyStage: Boolean) {
        if (initiallyEmptyStage) {
            stage = Stage()
            stage!!.addActor(stageBuilder!!.createRootGroup(null))
        } else {
            stage = stageBuilder!!.build(fileName, ExtendViewport(width.toFloat(), height.toFloat()))
        }

        stage!!.isDebugAll = true
    }

    inner class NhgLocalizationService : LocalizationService {
        override fun getString(s: String): String? {
            return null
        }

        override fun getString(s: String, vararg args: Any): String? {
            return null
        }
    }
}
