package io.github.movementspeed.nhglib.graphics.shaders.tiled;

import com.badlogic.gdx.graphics.PerspectiveCamera;
import com.badlogic.gdx.graphics.g3d.RenderableProvider;
import com.badlogic.gdx.graphics.glutils.FrameBuffer;
import com.badlogic.gdx.utils.Array;
import io.github.movementspeed.nhglib.graphics.lighting.tiled.LightGrid;
import io.github.movementspeed.nhglib.graphics.lighting.tiled.MinMax;
import io.github.movementspeed.nhglib.graphics.lights.NhgLight;
import io.github.movementspeed.nhglib.graphics.lights.NhgLightsAttribute;
import io.github.movementspeed.nhglib.graphics.rendering.RenderPass;

public class TiledPBRRenderPass extends RenderPass {
    private FrameBuffer forwardFBO;
    private LightGrid lightGrid;

    private Array<NhgLight> lights;
    private Array<MinMax> tileDepthRanges;

    public TiledPBRRenderPass() {
        super(false);
        lightGrid = new LightGrid();
    }

    @Override
    public void created() {
        TiledPBRShaderProvider tiledPBRShaderProvider = new TiledPBRShaderProvider(environment);
        setShaderProvider(tiledPBRShaderProvider);

        //tileDepthRanges = (Array<MinMax>) previousRenderPass.bundle.get("tileDepthRanges");
        tileDepthRanges = new Array<>();

        tiledPBRShaderProvider.setLightGrid(lightGrid);
        lights = new Array<>();

        //forwardFBO = ((DepthPreRenderPass) previousRenderPass.previousRenderPass).forwardFBO;
    }

    @Override
    public void begin(PerspectiveCamera camera) {
        NhgLightsAttribute lightsAttribute = (NhgLightsAttribute) environment.get(NhgLightsAttribute.Type);

        if (lightsAttribute != null) {
            lights = lightsAttribute.lights;
        }

        if (lights.size > 0) {
            lightGrid.buildLightGrid(tileDepthRanges, lights, camera.near, camera.view, camera.projection);
        }

        //forwardFBO.begin();
        //GLUtils.clearScreen(Color.BLACK);
    }

    @Override
    public void render(PerspectiveCamera camera, Array<RenderableProvider> renderableProviders) {
        renderer.begin(camera);
        renderer.render(renderableProviders);
        renderer.end();
    }

    @Override
    public void end() {

    }
}
