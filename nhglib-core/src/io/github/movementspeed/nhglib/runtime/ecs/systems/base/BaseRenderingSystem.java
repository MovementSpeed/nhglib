package io.github.movementspeed.nhglib.runtime.ecs.systems.base;

import com.artemis.Aspect;
import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.graphics.g3d.RenderableProvider;
import com.badlogic.gdx.utils.Array;
import io.github.movementspeed.nhglib.runtime.ecs.interfaces.RenderingSystemInterface;
import io.github.movementspeed.nhglib.runtime.ecs.systems.impl.CameraSystem;
import io.github.movementspeed.nhglib.runtime.ecs.systems.impl.RenderingSystem;
import io.github.movementspeed.nhglib.runtime.ecs.utils.Entities;

public abstract class BaseRenderingSystem extends NhgIteratingSystem implements RenderingSystemInterface {
    private boolean added;

    private Entities entities;

    protected RenderingSystem renderingSystem;
    protected CameraSystem cameraSystem;

    protected Array<Camera> cameras;
    protected Array<RenderableProvider> renderableProviders;

    public BaseRenderingSystem(Aspect.Builder aspect, Entities entities) {
        super(aspect);
        this.entities = entities;

        renderableProviders = new Array<>();
    }

    @Override
    protected void begin() {
        super.begin();

        if (!added) {
            added = true;
            RenderingSystem rs = entities.getEntitySystem(RenderingSystem.class);

            if (rs != null) {
                rs.addRenderingInterfaces(this);
            }
        }

        if (cameras == null) {
            cameras = cameraSystem.cameras;
        }
    }

    @Override
    public Array<RenderableProvider> getRenderableProviders() {
        return renderableProviders;
    }
}
