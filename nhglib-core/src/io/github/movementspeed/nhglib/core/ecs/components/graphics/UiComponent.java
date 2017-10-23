package io.github.movementspeed.nhglib.core.ecs.components.graphics;

import com.artemis.Component;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Array;
import io.github.movementspeed.nhglib.assets.Asset;
import io.github.movementspeed.nhglib.core.ecs.utils.UiManager;
import io.github.movementspeed.nhglib.input.handler.InputHandlerOld;
import io.github.movementspeed.nhglib.input.models.InputContext;
import io.github.movementspeed.nhglib.input.models.base.NhgInput;

import java.util.List;

public class UiComponent extends Component {
    public String fileName;
    public State state;
    public Type type;
    public UiManager uiManager;

    public Array<String> actorNames;
    public Array<Asset> dependencies;

    public UiComponent() {
        state = State.NOT_INITIALIZED;
        type = Type.SCREEN;
        dependencies = new Array<>();
        actorNames = new Array<>();
    }

    public void build(InputHandlerOld inputHandler, List<Vector2> supportedRes) {
        uiManager = new UiManager(fileName, inputHandler, supportedRes);
        uiManager.setActorNames(actorNames);
        uiManager.init(
                Gdx.graphics.getWidth(), Gdx.graphics.getHeight(),
                Gdx.graphics.getWidth(), Gdx.graphics.getHeight(),
                dependencies);

        InputContext inputContext = inputHandler.getContext("nhg.input.ui");

        for (String actorName : actorNames) {
            inputContext.addInput(new NhgInput(actorName));
        }

        state = State.READY;
    }

    public enum State {
        NOT_INITIALIZED,
        READY
    }

    public enum Type {
        SCREEN,
        PANEL
    }
}
