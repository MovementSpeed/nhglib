/*
 * www.javagl.de - JglTF
 *
 * Copyright 2015-2016 Marco Hutter - http://www.javagl.de
 *
 * Permission is hereby granted, free of charge, to any person
 * obtaining a copy of this software and associated documentation
 * files (the "Software"), to deal in the Software without
 * restriction, including without limitation the rights to use,
 * copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the
 * Software is furnished to do so, subject to the following
 * conditions:
 *
 * The above copyright notice and this permission notice shall be
 * included in all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND,
 * EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES
 * OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND
 * NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT
 * HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY,
 * WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING
 * FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR
 * OTHER DEALINGS IN THE SOFTWARE.
 */
package io.github.movementspeed.nhglib.files.gltf.jgltf.model.io.v1;

import io.github.movementspeed.nhglib.files.gltf.jgltf.impl.v1.Buffer;
import io.github.movementspeed.nhglib.files.gltf.jgltf.impl.v1.GlTF;
import io.github.movementspeed.nhglib.files.gltf.jgltf.impl.v1.Image;
import io.github.movementspeed.nhglib.files.gltf.jgltf.impl.v1.Shader;
import io.github.movementspeed.nhglib.files.gltf.jgltf.model.BufferModel;
import io.github.movementspeed.nhglib.files.gltf.jgltf.model.GltfException;
import io.github.movementspeed.nhglib.files.gltf.jgltf.model.ImageModel;
import io.github.movementspeed.nhglib.files.gltf.jgltf.model.Optionals;
import io.github.movementspeed.nhglib.files.gltf.jgltf.model.gl.ShaderModel;
import io.github.movementspeed.nhglib.files.gltf.jgltf.model.impl.UriStrings;
import io.github.movementspeed.nhglib.files.gltf.jgltf.model.io.GltfAsset;
import io.github.movementspeed.nhglib.files.gltf.jgltf.model.io.IO;
import io.github.movementspeed.nhglib.files.gltf.jgltf.model.v1.BinaryGltfV1;
import io.github.movementspeed.nhglib.files.gltf.jgltf.model.v1.GltfExtensionsV1;
import io.github.movementspeed.nhglib.files.gltf.jgltf.model.v1.GltfModelV1;

import java.nio.ByteBuffer;
import java.util.Collection;
import java.util.Objects;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * A class for creating a {@link GltfAssetV1} with a default data
 * representation from a {@link GltfModelV1}.<br>
 * <br>
 * In the default data representation, elements are referred to via URIs.
 * Data elements like {@link Buffer}, {@link Image} or {@link Shader}
 * objects that used the binary glTF extension or data URIs will be
 * converted to refer to their data using URIs.
 */
final class DefaultAssetCreatorV1 {
    /**
     * The {@link GltfAssetV1} that is currently being created
     */
    private GltfAssetV1 gltfAsset;

    /**
     * The set of {@link Buffer} URI strings that are already used
     */
    private Set<String> existingBufferUriStrings;

    /**
     * The set of {@link Image} URI strings that are already used
     */
    private Set<String> existingImageUriStrings;

    /**
     * The set of {@link Shader} URI strings that are already used
     */
    private Set<String> existingShaderUriStrings;

    /**
     * Creates a new asset creator
     */
    DefaultAssetCreatorV1() {
        // Default constructor
    }

    /**
     * Create a default {@link GltfAssetV1} from the given {@link GltfModelV1}.
     *
     * @param gltfModel The input {@link GltfModelV1}
     * @return The default {@link GltfAssetV1}
     */
    GltfAssetV1 create(GltfModelV1 gltfModel) {
        GlTF inputGltf = gltfModel.getGltf();
        GlTF outputGltf = GltfUtilsV1.copy(inputGltf);

        // Remove the binary glTF extension, if it was used
        GltfExtensionsV1.removeExtensionUsed(outputGltf,
                BinaryGltfV1.getBinaryGltfExtensionName());

        existingBufferUriStrings = collectUriStrings(
                Optionals.of(inputGltf.getBuffers()).values(),
                Buffer::getUri);
        existingImageUriStrings = collectUriStrings(
                Optionals.of(inputGltf.getImages()).values(),
                Image::getUri);
        existingShaderUriStrings = collectUriStrings(
                Optionals.of(inputGltf.getShaders()).values(),
                Shader::getUri);

        this.gltfAsset = new GltfAssetV1(outputGltf, null);

        Optionals.of(outputGltf.getBuffers()).forEach((id, value) ->
                storeBufferAsDefault(gltfModel, id, value));
        Optionals.of(outputGltf.getImages()).forEach((id, value) ->
                storeImageAsDefault(gltfModel, id, value));
        Optionals.of(outputGltf.getShaders()).forEach((id, value) ->
                storeShaderAsDefault(gltfModel, id, value));

        return gltfAsset;
    }

    /**
     * Collect all strings that are obtained from the given elements by
     * applying the given function, if these strings are not <code>null</code>
     * and no data URI strings
     *
     * @param elements    The elements
     * @param uriFunction The function to obtain the string
     * @return The strings
     */
    private static <T> Set<String> collectUriStrings(Collection<T> elements,
                                                     Function<? super T, ? extends String> uriFunction) {
        return elements.stream()
                .map(uriFunction)
                .filter(Objects::nonNull)
                .filter(uriString -> !IO.isDataUriString(uriString))
                .collect(Collectors.toSet());
    }


    /**
     * Store the given {@link Buffer} with the given ID in the current
     * output asset. <br>
     * <br>
     * If the {@link Buffer#getUri() buffer URI} is <code>null</code> or a
     * data URI, it will receive a new URI, which refers to the buffer data,
     * which is then stored as {@link GltfAsset#getReferenceData(String)
     * reference data} in the asset.<br>
     * <br>
     * If the given ID is the binary glTF buffer ID, <code>"binary_glTF"</code>,
     * then the buffer will also receive a new URI.
     * <br>
     * The given {@link Buffer} object will be modified accordingly, if
     * necessary: Its URI will be set to be the new URI.
     *
     * @param gltfModel The {@link GltfModelV1}
     * @param id        The ID of the {@link Buffer}
     * @param buffer    The {@link Buffer}
     */
    private void storeBufferAsDefault(
            GltfModelV1 gltfModel, String id, Buffer buffer) {
        BufferModel bufferModel = gltfModel.getBufferModelById(id);
        ByteBuffer bufferData = bufferModel.getBufferData();

        String oldUriString = buffer.getUri();
        String newUriString = oldUriString;
        if (oldUriString == null ||
                IO.isDataUriString(oldUriString) ||
                BinaryGltfV1.isBinaryGltfBufferId(id)) {
            newUriString = UriStrings.createBufferUriString(
                    existingBufferUriStrings);
            buffer.setUri(newUriString);
            existingBufferUriStrings.add(newUriString);
        }
        gltfAsset.putReferenceData(newUriString, bufferData);
    }

    /**
     * Store the given {@link Image} with the given ID in the current
     * output asset. <br>
     * <br>
     * If the {@link Image#getUri() image URI} is <code>null</code> or a
     * data URI, it will receive a new URI, which refers to the image data,
     * which is then stored as {@link GltfAsset#getReferenceData(String)
     * reference data} in the asset.<br>
     * <br>
     * The given {@link Image} object will be modified accordingly, if
     * necessary: Its URI will be set to be the new URI. If it referred
     * to a buffer view, using the binary glTF extension, then this
     * reference will be removed.
     *
     * @param gltfModel The {@link GltfModelV1}
     * @param id        The id of the {@link Image}
     * @param image     The {@link Image}
     * @throws GltfException If the image format (and thus, the MIME type)
     *                       can not be determined from the image data
     */
    private void storeImageAsDefault(
            GltfModelV1 gltfModel, String id, Image image) {
        ImageModel imageModel = gltfModel.getImageModelById(id);
        ByteBuffer imageData = imageModel.getImageData();

        String oldUriString = image.getUri();
        String newUriString = oldUriString;
        if (oldUriString == null ||
                IO.isDataUriString(oldUriString) ||
                BinaryGltfV1.hasBinaryGltfExtension(image)) {
            newUriString = UriStrings.createImageUriString(
                    imageModel, existingImageUriStrings);
            image.setUri(newUriString);
            existingImageUriStrings.add(newUriString);

            // Remove the extension object, if necessary
            image.removeExtensions(BinaryGltfV1.getBinaryGltfExtensionName());
        }
        gltfAsset.putReferenceData(newUriString, imageData);
    }


    /**
     * Store the given {@link Shader} with the given ID in the current
     * output asset. <br>
     * <br>
     * If the {@link Shader#getUri() shader URI} is <code>null</code> or a
     * data URI, it will receive a new URI, which refers to the shader data,
     * which is then stored as {@link GltfAsset#getReferenceData(String)
     * reference data} in the asset.<br>
     * <br>
     * The given {@link Shader} object will be modified accordingly, if
     * necessary: Its URI will be set to be the new URI. If it referred
     * to a buffer view, using the binary glTF extension, then this
     * reference will be removed.
     *
     * @param gltfModel The {@link GltfModelV1}
     * @param id        The id of the {@link Shader}
     * @param shader    The {@link Shader}
     */
    private void storeShaderAsDefault(
            GltfModelV1 gltfModel, String id, Shader shader) {
        ShaderModel shaderModel = gltfModel.getShaderModelById(id);
        ByteBuffer shaderData = shaderModel.getShaderData();

        String oldUriString = shader.getUri();
        String newUriString = oldUriString;
        if (oldUriString == null ||
                IO.isDataUriString(oldUriString) ||
                BinaryGltfV1.hasBinaryGltfExtension(shader)) {
            newUriString = UriStrings.createShaderUriString(
                    shaderModel, existingShaderUriStrings);
            shader.setUri(newUriString);
            existingShaderUriStrings.add(newUriString);

            // Remove the extension object, if necessary
            shader.removeExtensions(BinaryGltfV1.getBinaryGltfExtensionName());
        }
        gltfAsset.putReferenceData(newUriString, shaderData);
    }


}
