package io.github.movementspeed.nhglib.runtime.ecs.systems.impl;

import com.artemis.Aspect;
import com.artemis.ComponentMapper;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g3d.RenderableProvider;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Array;
import io.github.movementspeed.nhglib.input.handler.InputHandler;
import io.github.movementspeed.nhglib.runtime.ecs.components.graphics.UiComponent;
import io.github.movementspeed.nhglib.runtime.ecs.systems.base.BaseRenderingSystem;
import io.github.movementspeed.nhglib.runtime.ecs.utils.Entities;

import java.util.ArrayList;
import java.util.List;

public class UiRenderingSystem extends BaseRenderingSystem {
    private ComponentMapper<UiComponent> uiComponentMapper;

    private InputHandler inputHandler;
    private List<Vector2> supportedRes;
    private Array<UiComponent> uiComponents;

    public UiRenderingSystem(Entities entities, InputHandler inputHandler) {
        super(Aspect.all(UiComponent.class), entities);
        this.inputHandler = inputHandler;

        supportedRes = new ArrayList<>();
        supportedRes.add(new Vector2(1280, 720));
        supportedRes.add(new Vector2(1920, 1080));

        uiComponents = new Array<>();
    }

    @Override
    public void onPostRender() {
        super.onPostRender();
        for (UiComponent uiComponent : uiComponents) {
            switch (uiComponent.type) {
                case SCREEN:
                    uiComponent.uiManager.renderUi(Gdx.graphics.getDeltaTime());
                    break;

                case PANEL:
                    Texture texture = uiComponent.uiManager.renderUiToTexture(Gdx.graphics.getDeltaTime());
                    break;
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
        UiComponent uiComponent = uiComponentMapper.get(entityId);

        switch (uiComponent.state) {
            case READY:
                uiComponents.add(uiComponent);
                break;

            case NOT_INITIALIZED:
                uiComponent.build(inputHandler, supportedRes);
                break;
        }
    }

    @Override
    public Array<RenderableProvider> getRenderableProviders() {
        return super.getRenderableProviders();
    }
}
