package io.github.movementspeed.tests;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Mesh;
import com.badlogic.gdx.graphics.VertexAttributes;
import com.badlogic.gdx.graphics.g3d.Environment;
import com.badlogic.gdx.graphics.g3d.Material;
import com.badlogic.gdx.graphics.g3d.Model;
import com.badlogic.gdx.graphics.g3d.utils.ModelBuilder;
import com.badlogic.gdx.graphics.glutils.ShaderProgram;

/**
 * Created by Fausto Napoli on 26/10/2016.
 */
public class MainDef extends ApplicationAdapter {
    private Environment environment;

    private ShaderProgram geometryPass;
    private ShaderProgram lightingPass;

    private Mesh cubeMesh;

    @Override
    public void create() {
        super.create();

        geometryPass = new ShaderProgram(
                Gdx.files.internal("shaders/g_buffer.vert"),
                Gdx.files.internal("shaders/g_buffer.frag"));

        lightingPass = new ShaderProgram(
                Gdx.files.internal("shaders/deferred_shader.vert"),
                Gdx.files.internal("shaders/deferred_shader.frag"));

        ModelBuilder mb = new ModelBuilder();
        Model cube = mb.createBox(1, 1, 1, new Material(),
                VertexAttributes.Usage.Position |
                        VertexAttributes.Usage.Normal |
                        VertexAttributes.Usage.TextureCoordinates);
        cubeMesh = cube.meshes.first();
    }

    @Override
    public void resize(int width, int height) {
        super.resize(width, height);
    }

    @Override
    public void render() {
        super.render();
    }

    @Override
    public void pause() {
        super.pause();
    }

    @Override
    public void resume() {
        super.resume();
    }

    @Override
    public void dispose() {
        super.dispose();
    }
}