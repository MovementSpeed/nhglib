package io.github.movementspeed.nhglib.graphics.ogl;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.GL30;

import java.nio.Buffer;

public class GlBufferObject {
    private int id;
    private int elements;

    public GlBufferObject() {
        id = 0;
        elements = 0;
    }

    public void init(int elements, Buffer hostData, int dataSizeBytes) {
        Gdx.gl.glGenBuffer();

        this.elements = elements;
        if (elements > 0) {
            copyFromHost(hostData, elements, dataSizeBytes);
        }
    }

    public void copyFromHost(Buffer hostData, int elements, int dataSizeBytes) {
        this.elements = elements;

        Gdx.gl.glBindBuffer(GL30.GL_ARRAY_BUFFER, id);
        Gdx.gl.glBufferData(GL30.GL_ARRAY_BUFFER, elements * dataSizeBytes, hostData, GL30.GL_DYNAMIC_COPY);
        Gdx.gl.glBindBuffer(GL30.GL_ARRAY_BUFFER, 0);
    }

    public void bind() {
        Gdx.gl.glBindBuffer(GL30.GL_ARRAY_BUFFER, id);
    }

    public void bindSlot(int target, int slot) {
        Gdx.gl30.glBindBufferBase(target, slot, id);
    }
}
