package io.github.movementspeed.nhglib.graphics.shaders.shadows.system.classical;

import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.graphics.PerspectiveCamera;
import com.badlogic.gdx.graphics.g3d.ModelBatch;
import com.badlogic.gdx.graphics.g3d.RenderableProvider;
import com.badlogic.gdx.utils.Array;
import io.github.movementspeed.nhglib.graphics.rendering.RenderPass;
import io.github.movementspeed.nhglib.graphics.shaders.attributes.ShadowSystemAttribute;
import io.github.movementspeed.nhglib.graphics.shaders.shadows.system.ShadowSystem;

public class ClassicalShadowsRenderPass extends RenderPass {
    private ShadowSystem shadowSystem;
    private Array<ModelBatch> passBatches;

    public ClassicalShadowsRenderPass() {
        super(false);
    }

    @Override
    public void created() {
        shadowSystem = ((ShadowSystemAttribute) environment.get(ShadowSystemAttribute.Type)).shadowSystem;

        passBatches = new Array<>();
        for (int i = 0; i < shadowSystem.getPassQuantity(); i++) {
            passBatches.add(new ModelBatch(shadowSystem.getPassShaderProvider(i)));
        }
    }

    @Override
    public void begin(PerspectiveCamera camera) {
    }

    @Override
    public void render(PerspectiveCamera camera, Array<RenderableProvider> renderableProviders) {
        shadowSystem.begin(camera, renderableProviders);
        shadowSystem.update();

        for (int i = 0; i < shadowSystem.getPassQuantity(); i++) {
            shadowSystem.begin(i);
            Camera lightCamera;

            while ((lightCamera = shadowSystem.next()) != null) {
                passBatches.get(i).begin(lightCamera);
                passBatches.get(i).render(renderableProviders, environment);
                passBatches.get(i).end();
            }

            shadowSystem.end(i);
        }

        shadowSystem.end();
    }

    @Override
    public void end() {
        mainFBO.begin();
    }
}
