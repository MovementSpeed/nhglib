package io.github.movementspeed.nhglib.graphics.lights;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.*;
import com.badlogic.gdx.graphics.g3d.Material;
import com.badlogic.gdx.graphics.g3d.Model;
import com.badlogic.gdx.graphics.g3d.loader.G3dModelLoader;
import com.badlogic.gdx.graphics.g3d.utils.ModelBuilder;
import com.badlogic.gdx.graphics.glutils.FrameBuffer;
import com.badlogic.gdx.graphics.glutils.FrameBufferCubemap;
import com.badlogic.gdx.graphics.glutils.ShaderProgram;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.UBJsonReader;
import com.twelvemonkeys.imageio.plugins.hdr.HDRImageReadParam;
import com.twelvemonkeys.imageio.plugins.hdr.HDRImageReader;
import com.twelvemonkeys.imageio.plugins.hdr.tonemap.NullToneMapper;
import io.github.movementspeed.nhglib.graphics.ogl.NhgFloatTextureData;
import io.github.movementspeed.nhglib.graphics.ogl.NhgFrameBufferCubemap;

import javax.imageio.ImageIO;
import javax.imageio.ImageReader;
import javax.imageio.stream.ImageInputStream;
import java.awt.image.BufferedImage;
import java.awt.image.DataBufferFloat;
import java.io.IOException;
import java.util.Iterator;

/**
 * Created by Fausto Napoli on 17/08/2017.
 */
public class LightProbe {
    private Cubemap environmentCubemap;
    private Cubemap irradianceCubemap;
    private Cubemap prefilteredCubemap;
    private Texture brdfTexture;

    private Mesh quadMesh;
    private Mesh cubeMesh;

    private FrameBufferCubemap frameBufferCubemap;

    private Array<PerspectiveCamera> perspectiveCameras;

    public LightProbe() {
        init();
    }

    public void build(String hdrTexturePath, int envWidth, int envHeight) {
        if (hdrTexturePath != null) {
            environmentCubemap = renderEnvironmentFromHdrTexture(hdrTexturePath, envWidth, envHeight);
        } else {
            environmentCubemap = renderEnvironmentFromScene(envWidth, envHeight);
        }

        irradianceCubemap = renderIrradiance(environmentCubemap);
        prefilteredCubemap = renderPrefilter(environmentCubemap);
        brdfTexture = renderBRDF();
    }

    public void build(int envWidth, int envHeight) {
        build(null, envWidth, envHeight);
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
        initCameras();
    }

    private void createMeshes() {
        ModelBuilder mb = new ModelBuilder();
        Model cube = mb.createBox(1, 1, 1, new Material(),
                VertexAttributes.Usage.Position |
                        VertexAttributes.Usage.Normal |
                        VertexAttributes.Usage.TextureCoordinates);
        cubeMesh = cube.meshes.first();

        G3dModelLoader modelLoader = new G3dModelLoader(new UBJsonReader());
        Model quad = modelLoader.loadModel(Gdx.files.internal("models/quad.g3db"));
        quadMesh = quad.meshes.first();
    }

    private void initCameras() {
        perspectiveCameras = new Array<>();

        for (int i = 0; i < 6; i++) {
            PerspectiveCamera pc = new PerspectiveCamera(90, 512, 512);
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

    private Cubemap renderEnvironmentFromHdrTexture(String texturePath, int width, int height) {
        Texture equirectangularTexture;
        ShaderProgram equiToCubeShader = new ShaderProgram(
                Gdx.files.internal("shaders/equi_to_cube_shader.vert"),
                Gdx.files.internal("shaders/equi_to_cube_shader.frag"));

        BufferedImage bufferedImage = getHdrImage(Gdx.files.internal(texturePath));
        float[] rgb = ((DataBufferFloat) bufferedImage.getRaster().getDataBuffer()).getData();

        int bWidth = bufferedImage.getWidth();
        int bHeight = bufferedImage.getHeight();

        NhgFloatTextureData data = new NhgFloatTextureData(bWidth, bHeight, 3);
        data.prepare();
        data.getBuffer().put(rgb);
        data.getBuffer().flip();

        equirectangularTexture = new Texture(data);

        NhgFrameBufferCubemap frameBufferCubemap = new NhgFrameBufferCubemap(Pixmap.Format.RGB888, width, height, true);
        frameBufferCubemap.type = 1;
        frameBufferCubemap.genMipMap = false;
        frameBufferCubemap.buildFBO();

        equirectangularTexture.bind(0);
        equiToCubeShader.begin();
        equiToCubeShader.setUniformMatrix("u_projection", perspectiveCameras.first().projection);
        equiToCubeShader.setUniformi("u_equirectangularMap", 0);
        frameBufferCubemap.begin();
        for (int i = 0; i < 6; i++) {
            equiToCubeShader.setUniformMatrix("u_view", perspectiveCameras.get(i).view);
            Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT | GL20.GL_DEPTH_BUFFER_BIT);
            cubeMesh.render(equiToCubeShader, GL20.GL_TRIANGLES);
            frameBufferCubemap.nextSide(0);
        }
        frameBufferCubemap.end();
        equiToCubeShader.end();

        return frameBufferCubemap.getColorBufferTexture();
    }

    private Cubemap renderEnvironmentFromScene(int width, int height) {
        return null;
    }

    private Cubemap renderIrradiance(Cubemap environmentCubemap) {
        ShaderProgram irradianceShader = new ShaderProgram(
                Gdx.files.internal("shaders/equi_to_cube_shader.vert"),
                Gdx.files.internal("shaders/irradiance_shader.frag"));

        frameBufferCubemap = new FrameBufferCubemap(Pixmap.Format.RGB888,
                32, 32, true);

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

        return frameBufferCubemap.getColorBufferTexture();
    }

    private Cubemap renderPrefilter(Cubemap environmentCubemap) {
        ShaderProgram prefilterShader = new ShaderProgram(
                Gdx.files.internal("shaders/equi_to_cube_shader.vert"),
                Gdx.files.internal("shaders/prefilter_shader.frag"));

        Array<PerspectiveCamera> perspectiveCameras = new Array<>();

        for (int i = 0; i < 6; i++) {
            PerspectiveCamera pc = new PerspectiveCamera(90, 128, 128);
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

        NhgFrameBufferCubemap frameBufferCubemap = new NhgFrameBufferCubemap(Pixmap.Format.RGB888,
                128, 128, true);
        frameBufferCubemap.genMipMap = true;
        frameBufferCubemap.type = 0;
        frameBufferCubemap.buildFBO();

        prefilterShader.begin();
        prefilterShader.setUniformMatrix("u_projection", perspectiveCameras.first().projection);
        prefilterShader.setUniformi("u_environment", 0);
        frameBufferCubemap.begin();
        environmentCubemap.bind(0);

        int maxMipLevels = 5;

        for (int mip = 0; mip < maxMipLevels; mip++) {
            // resize framebuffer according to mip-level size.
            double ml = Math.pow(0.5, (double) mip);

            int mipWidth = (int) (128f * ml);
            int mipHeight = (int) (128f * ml);

            Gdx.gl.glBindRenderbuffer(GL20.GL_RENDERBUFFER, frameBufferCubemap.getDepthBufferHandle());
            Gdx.gl.glRenderbufferStorage(GL20.GL_RENDERBUFFER, GL20.GL_DEPTH_COMPONENT16, mipWidth, mipHeight);
            Gdx.gl.glViewport(0, 0, mipWidth, mipHeight);

            float roughness = (float) mip / (float) (maxMipLevels - 1);
            prefilterShader.setUniformf("u_roughness", roughness);

            for (int i = 0; i < 6; ++i) {
                prefilterShader.setUniformMatrix("u_view", perspectiveCameras.get(i).view);

                frameBufferCubemap.bindSide(i, mip);

                Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT | GL20.GL_DEPTH_BUFFER_BIT);
                cubeMesh.render(prefilterShader, GL20.GL_TRIANGLES);
            }
        }
        frameBufferCubemap.end();
        prefilterShader.end();

        return frameBufferCubemap.getColorBufferTexture();
    }

    private Texture renderBRDF() {
        ShaderProgram brdfShader = new ShaderProgram(
                Gdx.files.internal("shaders/brdf_shader.vert"),
                Gdx.files.internal("shaders/brdf_shader.frag"));

        FrameBuffer frameBuffer = new FrameBuffer(Pixmap.Format.RGB888, 512, 512, true);

        brdfShader.begin();
        frameBuffer.begin();
        Gdx.gl.glViewport(0, 0, 512, 512);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT | GL20.GL_DEPTH_BUFFER_BIT);
        quadMesh.render(brdfShader, GL20.GL_TRIANGLES);
        frameBuffer.end();
        brdfShader.end();

        return frameBuffer.getColorBufferTexture();
    }

    private BufferedImage getHdrImage(FileHandle fileHandle) {
        BufferedImage res = null;

        try {
            // Create input stream
            ImageInputStream input = ImageIO.createImageInputStream(fileHandle.file());

            try {
                // Get the reader
                Iterator<ImageReader> readers = ImageIO.getImageReaders(input);

                if (!readers.hasNext()) {
                    throw new IllegalArgumentException("No reader for: " + fileHandle.file());
                }

                HDRImageReader reader = (HDRImageReader) readers.next();

                try {
                    reader.setInput(input);

                    HDRImageReadParam param = (HDRImageReadParam) reader.getDefaultReadParam();
                    param.setToneMapper(new NullToneMapper());

                    // Finally read the image, using settings from param
                    res = reader.read(0, param);
                } finally {
                    // Dispose reader in finally block to avoid memory leaks
                    reader.dispose();
                }
            } finally {
                // Close stream in finally block to avoid resource leaks
                input.close();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        return res;
    }
}
