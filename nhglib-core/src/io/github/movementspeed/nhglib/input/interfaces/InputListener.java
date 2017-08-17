package io.github.movementspeed.nhglib.input.interfaces;

import io.github.movementspeed.nhglib.input.models.NhgInput;

/**
 * Created by Fausto Napoli on 09/01/2017.
 */
public interface InputListener {
    void onKeyInput(NhgInput input);

    void onStickInput(NhgInput input);

    void onPointerInput(NhgInput input);

    void onMouseInput(NhgInput input);
}
