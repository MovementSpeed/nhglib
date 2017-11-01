package io.github.movementspeed.nhglib.core.ecs.systems.impl;

import com.artemis.Aspect;
import com.artemis.ComponentMapper;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Array;
import io.github.movementspeed.nhglib.core.ecs.components.graphics.ModelComponent;
import io.github.movementspeed.nhglib.core.ecs.components.graphics.UiComponent;
import io.github.movementspeed.nhglib.core.ecs.systems.base.BaseRenderingSystem;
import io.github.movementspeed.nhglib.core.ecs.utils.Entities;
import io.github.movementspeed.nhglib.graphics.shaders.attributes.PbrTextureAttribute;

import java.util.ArrayList;
import java.util.List;

public class UiSystem extends BaseRenderingSystem {
    private InputSystem inputSystem;
    private ComponentMapper<UiComponent> uiMapper;
    private ComponentMapper<ModelComponent> modelMapper;

    private List<Vector2> supportedRes;
    private Array<UiComponent> uiComponents;

    public UiSystem(Entities entities) {
        super(Aspect.all(UiComponent.class), entities);

        supportedRes = new ArrayList<>();
        supportedRes.add(new Vector2(1280, 720));
        supportedRes.add(new Vector2(1920, 1080));

        uiComponents = new Array<>();
    }

    @Override
    public void onPostRender() {
        super.onPostRender();
        for (UiComponent uiComponent : uiComponents) {
            if (uiComponent.type == UiComponent.Type.SCREEN) {
                uiComponent.uiManager.renderUi(Gdx.graphics.getDeltaTime());
            }
        }

        uiComponents.clear();
    }

    @Override
    public void onUpdatedRenderer(int renderingWidth, int renderingHeight) {
        super.onUpdatedRenderer(renderingWidth, renderingHeight);

        for (UiComponent uiComponent : uiComponents) {
            uiComponent.uiManager.resize(renderingWidth, renderingHeight);
        }
    }

    @Override
    protected void process(int entityId) {
        UiComponent uiComponent = uiMapper.get(entityId);

        switch (uiComponent.state) {
            case READY:
                switch (uiComponent.type) {
                    case SCREEN:
                        uiComponents.add(uiComponent);
                        break;

                    case PANEL:
                        // Currently not used, not completely implemented.
                        ModelComponent modelComponent = modelMapper.get(entityId);

                        if (modelComponent != null && modelComponent.state == ModelComponent.State.READY) {
                            TextureRegion texture = uiComponent.uiManager.renderUiToTexture(Gdx.graphics.getDeltaTime());
                            PbrTextureAttribute textureAttribute = (PbrTextureAttribute) modelComponent.model.materials
                                    .first().get(PbrTextureAttribute.Albedo);

                            textureAttribute.set(texture);
                        }
                        break;
                }
                break;

            case NOT_INITIALIZED:
                if (inputSystem.getInputProxy() != null) {
                    uiComponent.build(inputSystem.getInputProxy(), supportedRes);
                }
                break;
        }
    }
}
