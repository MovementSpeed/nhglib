package io.github.movementspeed.nhglib.input.handler;

import com.badlogic.gdx.InputMultiplexer;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Group;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.ArrayMap;
import com.badlogic.gdx.utils.ObjectMap;
import io.github.movementspeed.nhglib.input.enums.InputAction;
import io.github.movementspeed.nhglib.input.interfaces.InputHandler;
import io.github.movementspeed.nhglib.input.models.base.NhgInput;
import io.github.movementspeed.nhglib.input.models.impls.virtual.NhgVirtualButtonInput;

public class VirtualInputHandler implements InputHandler {
    private Interface virtualInputInterface;
    private Vector2 vec0;
    private InputMultiplexer inputMultiplexer;

    private Array<String> activeVirtualInputs;
    private Array<NhgInput> originalVirtualInputArray;
    private ArrayMap<String, NhgVirtualButtonInput> virtualInputs;
    private ArrayMap<String, Stage> stages;

    public VirtualInputHandler(Interface virtualInputInterface, InputMultiplexer inputMultiplexer, Array<NhgInput> virtualInputArray) {
        this.virtualInputInterface = virtualInputInterface;
        this.inputMultiplexer = inputMultiplexer;
        this.originalVirtualInputArray = virtualInputArray;

        vec0 = new Vector2();
        activeVirtualInputs = new Array<>();
        stages = new ArrayMap<>();
        virtualInputs = new ArrayMap<>();

        processVirtualInputArray(originalVirtualInputArray);
    }

    @Override
    public void update() {
        for (String actorName : activeVirtualInputs) {
            virtualInputInterface.onVirtualInput(virtualInputs.get(actorName));
        }
    }

    public void addStage(String name, Stage value) {
        stages.put(name, value);
        inputMultiplexer.addProcessor(value);
        processStages();
    }

    public void removeStage(String name) {
        Stage stage = stages.get(name);
        inputMultiplexer.removeProcessor(stage);
        stages.removeKey(name);
        processStages();
    }

    private void processVirtualInputArray(Array<NhgInput> virtualInputArray) {
        for (NhgInput input : virtualInputArray) {
            NhgVirtualButtonInput virtualInput = ((NhgVirtualButtonInput) input);
            String actorName = virtualInput.getActorName();
            virtualInputs.put(actorName, virtualInput);
        }
    }

    private void processStages() {
        activeVirtualInputs.clear();

        for (Stage stage : stages.values()) {
            Group stageRoot = stage.getRoot();

            for (ObjectMap.Entry<String, NhgVirtualButtonInput> entry : virtualInputs.entries()) {
                final String actorName = entry.key;
                Actor actor = stageRoot.findActor(actorName);

                if (actor != null) {
                    final NhgVirtualButtonInput virtualInput = entry.value;

                    actor.clearListeners();
                    actor.addListener(new ClickListener() {
                        @Override
                        public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) {
                            virtualInput.setAction(InputAction.DOWN);
                            virtualInput.setValue(vec0.set(x, y));

                            switch (virtualInput.getMode()) {
                                case REPEAT:
                                    if (!activeVirtualInputs.contains(actorName, false)) {
                                        activeVirtualInputs.add(actorName);
                                    }
                                    break;

                                default:
                                    virtualInputInterface.onVirtualInput(virtualInput);
                                    break;
                            }
                            return true;
                        }

                        @Override
                        public void touchUp(InputEvent event, float x, float y, int pointer, int button) {
                            virtualInput.setAction(InputAction.UP);
                            virtualInput.setValue(vec0.set(x, y));

                            switch (virtualInput.getMode()) {
                                case REPEAT:
                                    activeVirtualInputs.removeValue(actorName, false);
                                    break;

                                default:
                                    virtualInputInterface.onVirtualInput(virtualInput);
                                    break;
                            }
                        }
                    });
                }
            }
        }
    }

    public interface Interface {
        void onVirtualInput(NhgVirtualButtonInput input);
    }
}
