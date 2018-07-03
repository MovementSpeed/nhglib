package io.github.movementspeed.nhgpg;

import io.github.movementspeed.nhglib.Nhg;
import io.github.movementspeed.nhglib.core.entry.NhgEntry;
import io.github.movementspeed.nhglib.graphics.scenes.Scene;
import io.github.movementspeed.nhglib.graphics.worlds.NhgWorld;
import io.github.movementspeed.nhglib.graphics.worlds.strategies.impl.DefaultWorldStrategy;
import io.github.movementspeed.nhglib.input.interfaces.InputListener;
import io.github.movementspeed.nhglib.input.models.base.NhgInput;
import io.github.movementspeed.nhglib.utils.data.Bounds;

public class Playground extends NhgEntry implements InputListener {
	private Scene scene;
	private NhgWorld world;

	@Override
	public void onStart() {
		super.onStart();
		Nhg.debugLogs = true;
		Nhg.debugFpsLogs = true;
	}

	@Override
	public void onInitialized() {
		super.onInitialized();
		world = new NhgWorld(nhg.messaging, nhg.entities, nhg.assets,
				new DefaultWorldStrategy(),
				new Bounds(2f, 2f, 2f));
	}

	@Override
	public void onUpdate(float delta) {
		super.onUpdate(delta);
	}

	@Override
	public void onInput(NhgInput input) {

	}
}
