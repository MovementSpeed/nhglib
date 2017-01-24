package io.github.voidzombie.nhglib.input;

/**
 * Created by Fausto Napoli on 09/01/2017.
 */
public interface InputListener {
    void onKeyInput(NhgInput input);

    void onStickInput(NhgInput input);
}
