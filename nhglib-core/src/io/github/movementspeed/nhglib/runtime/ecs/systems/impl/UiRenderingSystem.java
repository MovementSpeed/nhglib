package io.github.movementspeed.nhglib.runtime.ecs.systems.impl;

import com.artemis.Aspect;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.math.Vector2;
import io.github.movementspeed.nhglib.input.handler.InputHandler;
import io.github.movementspeed.nhglib.runtime.ecs.components.graphics.ModelComponent;
import io.github.movementspeed.nhglib.runtime.ecs.components.graphics.UiComponent;
import io.github.movementspeed.nhglib.runtime.ecs.systems.base.BaseRenderingSystem;
import io.github.movementspeed.nhglib.runtime.ecs.utils.Entities;
import io.github.movementspeed.nhglib.runtime.ecs.utils.UiManager;

import java.util.ArrayList;
import java.util.List;

public class UiRenderingSystem extends BaseRenderingSystem {
    private UiManager uiManager;

    public UiRenderingSystem(Entities entities, InputHandler inputHandler) {
        super(Aspect.all(ModelComponent.class, UiComponent.class), entities);

        // TODO : spostare UiManager in UiComponent? Ogni UiComponent potrebbe avere una UI completamente diversa,
        // TODO : quindi stage diversi (es.: uno può voler renderizzare su modello 3D, un altro su schermo)
        List<Vector2> supportedRes = new ArrayList<>();
        supportedRes.add(new Vector2(1280, 720));
        supportedRes.add(new Vector2(1920, 1080));

        uiManager = new UiManager(inputHandler, supportedRes);
        uiManager.init(
                RenderingSystem.renderWidth, RenderingSystem.renderHeight,
                Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
    }

    @Override
    protected void begin() {
        super.begin();
        uiManager.renderUi(Gdx.graphics.getDeltaTime());
    }

    @Override
    protected void process(int entityId) {

    }
}
