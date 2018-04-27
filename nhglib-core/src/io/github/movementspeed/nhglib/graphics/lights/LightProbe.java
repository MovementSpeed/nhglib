package io.github.movementspeed.nhglib.graphics.lights;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.*;
import com.badlogic.gdx.graphics.g3d.Material;
import com.badlogic.gdx.graphics.g3d.Model;
import com.badlogic.gdx.graphics.g3d.loader.G3dModelLoader;
import com.badlogic.gdx.graphics.g3d.utils.ModelBuilder;
import com.badlogic.gdx.graphics.glutils.FrameBuffer;
import com.badlogic.gdx.graphics.glutils.FrameBufferCubemap;
import com.badlogic.gdx.graphics.glutils.GLFrameBuffer;
import com.badlogic.gdx.graphics.glutils.ShaderProgram;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.UBJsonReader;
import io.github.movementspeed.nhglib.Nhg;
import io.github.movementspeed.nhglib.files.HDRData;

/**
 * Created by Fausto Napoli on 17/08/2017.
 */
public class LightProbe {
    private float environmentWidth;
    private float environmentHeight;

    private float irradianceWidth;
    private float irradianceHeight;

    private float prefilterWidth;
    private float prefilterHeight;

    private float brdfWidth;
    private float brdfHeight;

    private Cubemap environmentCubemap;
    private Cubemap irradianceCubemap;
    private Cubemap prefilteredCubemap;

    private Texture brdfTexture;

    private Model quadModel;
    private Model cubeModel;

    private Mesh quadMesh;
    private Mesh cubeMesh;

    private Array<PerspectiveCamera> perspectiveCameras;

    public LightProbe() {
        init();
    }

    public void build(Texture environment,
                      float environmentWidth, float environmentHeight,
                      float irradianceWidth, float irradianceHeight,
                      float prefilterWidth, float prefilterHeight,
                      float brdfWidth, float brdfHeight) {
        this.environmentWidth = environmentWidth;
        this.environmentHeight = environmentHeight;
        this.irradianceWidth = irradianceWidth;
        this.irradianceHeight = irradianceHeight;
        this.prefilterWidth = prefilterWidth;
        this.prefilterHeight = prefilterHeight;
        this.brdfWidth = brdfWidth;
        this.brdfHeight = brdfHeight;

        initCameras();

        environmentCubemap = renderEnvironmentFromTexture(environment);
        irradianceCubemap = renderIrradiance(environmentCubemap);
        prefilteredCubemap = renderPrefilter(environmentCubemap);
        brdfTexture = renderBRDF();

        cubeModel.dispose();
        quadModel.dispose();
    }

    public void build(HDRData hdrData,
                      float environmentWidth, float environmentHeight,
                      float irradianceWidth, float irradianceHeight,
                      float prefilterWidth, float prefilterHeight,
                      float brdfWidth, float brdfHeight) {
        this.environmentWidth = environmentWidth;
        this.environmentHeight = environmentHeight;
        this.irradianceWidth = irradianceWidth;
        this.irradianceHeight = irradianceHeight;
        this.prefilterWidth = prefilterWidth;
        this.prefilterHeight = prefilterHeight;
        this.brdfWidth = brdfWidth;
        this.brdfHeight = brdfHeight;

        initCameras();

        if (hdrData != null) {
            environmentCubemap = renderEnvironmentFromHDRData(hdrData);
        } else {
            environmentCubemap = renderEnvironmentFromScene();
        }

        irradianceCubemap = renderIrradiance(environmentCubemap);
        prefilteredCubemap = renderPrefilter(environmentCubemap);
        brdfTexture = renderBRDF();

        cubeModel.dispose();
        quadModel.dispose();
    }

    public void build(float environmentWidth, float environmentHeight) {
        build((HDRData) null, environmentWidth, environmentHeight, 32f, 32f,
                128f, 128f, environmentWidth, environmentHeight);
    }

    public Cubemap getEnvironment() {
        return environmentCubemap;
    }

    public Cubemap getIrradiance() {
        return irradianceCubemap;
    }

    public Cubemap getPrefilter() {
        return prefilteredCubemap;
    }

    public Texture getBrdf() {
        return brdfTexture;
    }

    private void init() {
        createMeshes();
    }

    private void createMeshes() {
        ModelBuilder mb = new ModelBuilder();
        cubeModel = mb.createBox(1, 1, 1, new Material(),
                VertexAttributes.Usage.Position |
                        VertexAttributes.Usage.Normal |
                        VertexAttributes.Usage.TextureCoordinates);
        cubeMesh = cubeModel.meshes.first();

        G3dModelLoader modelLoader = new G3dModelLoader(new UBJsonReader());
        quadModel = modelLoader.loadModel(Gdx.files.internal("models/quad.g3db"));
        quadMesh = quadModel.meshes.first();
    }

    private void initCameras() {
        perspectiveCameras = new Array<>();

        for (int i = 0; i < 6; i++) {
            PerspectiveCamera pc = new PerspectiveCamera(90, environmentWidth, environmentHeight);
            pc.near = 0.1f;
            pc.far = 10.0f;
            perspectiveCameras.add(pc);
        }

        PerspectiveCamera pc1 = perspectiveCameras.get(0);
        pc1.lookAt(1, 0, 0);
        pc1.rotate(Vector3.X, 180);
        pc1.update();

        PerspectiveCamera pc2 = perspectiveCameras.get(1);
        pc2.lookAt(0, 0, -1);
        pc2.rotate(Vector3.X, 180);
        pc2.update();

        PerspectiveCamera pc3 = perspectiveCameras.get(2);
        pc3.lookAt(0, 0, 1);
        pc3.rotate(Vector3.X, 180);
        pc3.update();

        PerspectiveCamera pc4 = perspectiveCameras.get(3);
        pc4.lookAt(0, 1, 0);
        pc4.rotate(Vector3.Y, 270);
        pc4.update();

        PerspectiveCamera pc5 = perspectiveCameras.get(4);
        pc5.lookAt(0, -1, 0);
        pc5.rotate(Vector3.Y, 270);
        pc5.update();

        PerspectiveCamera pc6 = perspectiveCameras.get(5);
        pc6.lookAt(-1, 0, 0);
        pc6.rotate(Vector3.X, 180);
        pc6.update();
    }

    private Cubemap renderEnvironmentFromHDRData(HDRData data) {
        Texture equirectangularTexture;

        String folder = "shaders/gl3/";

        switch (Nhg.glVersion) {
            case VERSION_2:
                folder = "shaders/gl2/";
                break;
        }

        ShaderProgram equiToCubeShader = new ShaderProgram(
                Gdx.files.internal(folder + "equi_to_cube_shader.vert"),
                Gdx.files.internal(folder + "equi_to_cube_shader.frag"));

        equirectangularTexture = data.getTexture();

        GLFrameBuffer.FrameBufferCubemapBuilder builder = new GLFrameBuffer.FrameBufferCubemapBuilder(
                (int) environmentWidth, (int) environmentHeight);
        builder.addColorTextureAttachment(GL30.GL_RGB8, GL30.GL_RGB, GL30.GL_UNSIGNED_BYTE);
        builder.addDepthRenderBufferAttachment();
        FrameBufferCubemap frameBufferCubemap = builder.build();

        equirectangularTexture.bind(0);
        equiToCubeShader.begin();
        equiToCubeShader.setUniformMatrix("u_projection", perspectiveCameras.first().projection);
        equiToCubeShader.setUniformi("u_equirectangularMap", 0);
        frameBufferCubemap.begin();
        for (int i = 0; i < 6; i++) {
            equiToCubeShader.setUniformMatrix("u_view", perspectiveCameras.get(i).view);
            Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT | GL20.GL_DEPTH_BUFFER_BIT);
            cubeMesh.render(equiToCubeShader, GL20.GL_TRIANGLES);
            frameBufferCubemap.nextSide();
        }
        frameBufferCubemap.end();
        equiToCubeShader.end();
        equiToCubeShader.dispose();

        return frameBufferCubemap.getColorBufferTexture();
    }

    private Cubemap renderEnvironmentFromTexture(Texture equirectangularTexture) {
        String folder = "shaders/gl3/";

        switch (Nhg.glVersion) {
            case VERSION_2:
                folder = "shaders/gl2/";
                break;
        }

        ShaderProgram equiToCubeShader = new ShaderProgram(
                Gdx.files.internal(folder + "equi_to_cube_shader.vert"),
                Gdx.files.internal(folder + "equi_to_cube_shader.frag"));

        GLFrameBuffer.FrameBufferCubemapBuilder builder = new GLFrameBuffer.FrameBufferCubemapBuilder(
                (int) environmentWidth, (int) environmentHeight);
        builder.addColorTextureAttachment(GL30.GL_RGB8, GL30.GL_RGB, GL30.GL_UNSIGNED_BYTE);
        builder.addDepthRenderBufferAttachment();
        FrameBufferCubemap frameBufferCubemap = builder.build();

        equirectangularTexture.bind(0);
        equiToCubeShader.begin();
        equiToCubeShader.setUniformMatrix("u_projection", perspectiveCameras.first().projection);
        equiToCubeShader.setUniformi("u_equirectangularMap", 0);
        frameBufferCubemap.begin();
        for (int i = 0; i < 6; i++) {
            equiToCubeShader.setUniformMatrix("u_view", perspectiveCameras.get(i).view);
            Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT | GL20.GL_DEPTH_BUFFER_BIT);
            cubeMesh.render(equiToCubeShader, GL20.GL_TRIANGLES);
            frameBufferCubemap.nextSide();
        }
        frameBufferCubemap.end();
        equiToCubeShader.end();
        equiToCubeShader.dispose();

        return frameBufferCubemap.getColorBufferTexture();
    }

    private Cubemap renderEnvironmentFromScene() {
        return null;
    }

    private Cubemap renderIrradiance(Cubemap environmentCubemap) {
        String folder = "shaders/gl3/";

        switch (Nhg.glVersion) {
            case VERSION_2:
                folder = "shaders/gl2/";
                break;
        }

        ShaderProgram irradianceShader = new ShaderProgram(
                Gdx.files.internal(folder + "equi_to_cube_shader.vert"),
                Gdx.files.internal(folder + "irradiance_shader.frag"));

        FrameBufferCubemap frameBufferCubemap = FrameBufferCubemap.createFrameBufferCubemap(Pixmap.Format.RGB888,
                (int) irradianceWidth, (int) irradianceHeight, true);

        environmentCubemap.bind(0);
        irradianceShader.begin();
        irradianceShader.setUniformMatrix("u_projection", perspectiveCameras.first().projection);
        irradianceShader.setUniformi("u_environmentMap", 0);
        frameBufferCubemap.begin();
        for (int i = 0; i < 6; i++) {
            irradianceShader.setUniformMatrix("u_view", perspectiveCameras.get(i).view);
            Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT | GL20.GL_DEPTH_BUFFER_BIT);
            cubeMesh.render(irradianceShader, GL20.GL_TRIANGLES);
            frameBufferCubemap.nextSide();
        }
        frameBufferCubemap.end();
        irradianceShader.end();
        irradianceShader.dispose();

        return frameBufferCubemap.getColorBufferTexture();
    }

    private Cubemap renderPrefilter(Cubemap environmentCubemap) {
        String folder = "shaders/gl3/";

        switch (Nhg.glVersion) {
            case VERSION_2:
                folder = "shaders/gl2/";
                break;
        }

        ShaderProgram prefilterShader = new ShaderProgram(
                Gdx.files.internal(folder + "equi_to_cube_shader.vert"),
                Gdx.files.internal(folder + "prefilter_shader.frag"));

        Array<PerspectiveCamera> perspectiveCameras = new Array<>();

        for (int i = 0; i < 6; i++) {
            PerspectiveCamera pc = new PerspectiveCamera(90, prefilterWidth, prefilterHeight);
            pc.near = 0.1f;
            pc.far = 10.0f;
            perspectiveCameras.add(pc);
        }

        PerspectiveCamera pc1 = perspectiveCameras.get(0);
        pc1.lookAt(0, 0, 1);
        pc1.rotate(Vector3.Z, 180);
        pc1.update();

        PerspectiveCamera pc2 = perspectiveCameras.get(1);
        pc2.lookAt(0, 0, -1);
        pc2.rotate(Vector3.Z, 180);
        pc2.update();

        // top
        PerspectiveCamera pc3 = perspectiveCameras.get(2);
        pc3.rotate(Vector3.Z, 90);
        pc3.lookAt(0, 1, 0);
        pc3.update();

        // down
        PerspectiveCamera pc4 = perspectiveCameras.get(3);
        pc4.rotate(Vector3.Z, 270);
        pc4.lookAt(0, -1, 0);
        pc4.update();

        // forward
        PerspectiveCamera pc5 = perspectiveCameras.get(4);
        pc5.lookAt(-1, 0, 0);
        pc5.rotate(Vector3.X, 180);
        pc5.update();

        // back
        PerspectiveCamera pc6 = perspectiveCameras.get(5);
        pc6.lookAt(1, 0, 0);
        pc6.rotate(Vector3.X, 180);
        pc6.update();

        FrameBufferCubemap frameBufferCubemap = FrameBufferCubemap.createFrameBufferCubemap(Pixmap.Format.RGB888,
                (int) prefilterWidth, (int) prefilterHeight, true);

        Cubemap cubemap = frameBufferCubemap.getColorBufferTexture();
        cubemap.setFilter(Texture.TextureFilter.MipMapLinearLinear, Texture.TextureFilter.Linear);
        cubemap.setWrap(Texture.TextureWrap.ClampToEdge, Texture.TextureWrap.ClampToEdge);

        Gdx.gl.glBindTexture(cubemap.glTarget, cubemap.getTextureObjectHandle());
        Gdx.gl.glGenerateMipmap(GL20.GL_TEXTURE_CUBE_MAP);

        prefilterShader.begin();
        prefilterShader.setUniformMatrix("u_projection", perspectiveCameras.first().projection);
        prefilterShader.setUniformi("u_environment", 0);
        frameBufferCubemap.begin();
        environmentCubemap.bind(0);

        int maxMipLevels = 5;

        for (int mip = 0; mip < maxMipLevels; mip++) {
            // resize framebuffer according to mip-level size.
            double ml = Math.pow(0.5, (double) mip);

            int mipWidth = (int) (prefilterWidth * ml);
            int mipHeight = (int) (prefilterHeight * ml);

            Gdx.gl.glBindRenderbuffer(GL20.GL_RENDERBUFFER, frameBufferCubemap.getDepthBufferHandle());
            Gdx.gl.glRenderbufferStorage(GL20.GL_RENDERBUFFER, GL20.GL_DEPTH_COMPONENT16, mipWidth, mipHeight);
            Gdx.gl.glViewport(0, 0, mipWidth, mipHeight);

            float roughness = (float) mip / (float) (maxMipLevels - 1);
            prefilterShader.setUniformf("u_roughness", roughness);

            for (int i = 0; i < 6; ++i) {
                prefilterShader.setUniformMatrix("u_view", perspectiveCameras.get(i).view);

                Cubemap.CubemapSide side = Cubemap.CubemapSide.values()[i];
                Gdx.gl20.glFramebufferTexture2D(GL20.GL_FRAMEBUFFER, GL20.GL_COLOR_ATTACHMENT0, side.glEnum,
                        cubemap.getTextureObjectHandle(), mip);

                Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT | GL20.GL_DEPTH_BUFFER_BIT);
                cubeMesh.render(prefilterShader, GL20.GL_TRIANGLES);
            }
        }
        frameBufferCubemap.end();
        prefilterShader.end();
        prefilterShader.dispose();

        return frameBufferCubemap.getColorBufferTexture();
    }

    private Texture renderBRDF() {
        String folder = "shaders/gl3/";

        switch (Nhg.glVersion) {
            case VERSION_2:
                folder = "shaders/gl2/";
                break;
        }

        ShaderProgram brdfShader = new ShaderProgram(
                Gdx.files.internal(folder + "brdf_shader.vert"),
                Gdx.files.internal(folder + "brdf_shader.frag"));

        FrameBuffer frameBuffer = FrameBuffer.createFrameBuffer(Pixmap.Format.RGB888, (int) brdfWidth, (int) brdfHeight, true);

        brdfShader.begin();
        frameBuffer.begin();
        Gdx.gl.glViewport(0, 0, (int) brdfWidth, (int) brdfHeight);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT | GL20.GL_DEPTH_BUFFER_BIT);
        quadMesh.render(brdfShader, GL20.GL_TRIANGLES);
        frameBuffer.end();
        brdfShader.end();
        brdfShader.dispose();

        return frameBuffer.getColorBufferTexture();
    }
}