package io.github.voidzombie.nhglib.runtime;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.ai.fsm.DefaultStateMachine;
import io.github.voidzombie.nhglib.enums.GameState;
import io.github.voidzombie.nhglib.interfaces.Updatable;

/**
 * Created by Fausto Napoli on 19/10/2016.
 * Extend this class to manage your own game.
 */
public class NHGGame extends Game implements Updatable {
    public final DefaultStateMachine<NHGGame, GameState> fsm;

    public NHGGame() {
        fsm = new DefaultStateMachine<NHGGame, GameState>(this, GameState.NOT_INITIALIZED);
    }

    @Override
    public void create() {
    }

    @Override
    public void update() {
        fsm.update();
    }

    @Override
    public void render() {
        update();
        super.render();
    }
}
