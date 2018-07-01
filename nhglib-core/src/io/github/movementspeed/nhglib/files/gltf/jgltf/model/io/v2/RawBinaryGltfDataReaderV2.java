package io.github.movementspeed.nhglib.files.gltf.jgltf.model.io.v2;

import io.github.movementspeed.nhglib.files.gltf.jgltf.model.io.Buffers;
import io.github.movementspeed.nhglib.files.gltf.jgltf.model.io.RawGltfData;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.List;

/**
 * A class for reading a {@link RawGltfData} from a buffer that contains
 * binary glTF 2.0 data.
 */
public class RawBinaryGltfDataReaderV2
{
    /**
     * The length of the binary glTF header for glTF 2.0, in bytes
     */
    private static final int BINARY_GLTF_VERSION_2_HEADER_LENGTH_IN_BYTES = 12;

    /**
     * The constant indicating JSON chunk type for glTF 2.0
     * This is an integer corresponding to the ASCII string <code>"JSON"</code>
     */
    private static final int CHUNK_TYPE_JSON = 0x4E4F534A;
    
    /**
     * The constant indicating BIN chunk type for glTF 2.0
     * This is an integer corresponding to the ASCII string <code>"BIN"</code>
     */
    private static final int CHUNK_TYPE_BIN = 0x004E4942;
    
    /**
     * Read the {@link RawGltfData} from the given buffer, which contains
     * the binary glTF 2.0 data
     * 
     * @param data The input data
     * @return The {@link RawGltfData}
     * @throws IOException If an IO error occurs
     */
    public static RawGltfData readBinaryGltf(ByteBuffer data) 
        throws IOException
    {
        int headerLength = BINARY_GLTF_VERSION_2_HEADER_LENGTH_IN_BYTES;
        if (data.capacity() < headerLength)
        {
            throw new IOException("Expected header of size " + headerLength
                + ", but only found " + data.capacity() + " bytes");
        }
        int length = data.getInt(8);
        if (length != data.capacity())
        {
            throw new IOException(
                "Data length is " + data.capacity() + ", expected " + length);
        }
        
        List<Chunk> chunks = readChunks(data);
        if (chunks.isEmpty())
        {
            throw new IOException(
                "Found no chunks in binary glTF data");
        }
        Chunk jsonChunk = chunks.get(0);
        if (jsonChunk.type != CHUNK_TYPE_JSON)
        {
            throw new IOException("First chunk must be of type JSON ("
                + CHUNK_TYPE_JSON + "), but found " + jsonChunk.type);
        }
        ByteBuffer jsonData = jsonChunk.data;
        ByteBuffer binData = ByteBuffer.allocateDirect(0);
        if (chunks.size() > 1)
        {
            Chunk binChunk = chunks.get(1);
            if (binChunk.type != CHUNK_TYPE_BIN)
            {
                throw new IOException("Second chunk must be of type BIN ("
                    + CHUNK_TYPE_BIN + "), but found " + jsonChunk.type);
            }
            binData = binChunk.data;
        }
        return new RawGltfData(jsonData, binData);
    }
    
    /**
     * A class representing a chunk in binary glTF 2.0
     */
    private static class Chunk
    {
        /**
         * The length
         */
        int length;
        
        /**
         * The type
         */
        int type;
        
        /**
         * The actual chunk data
         */
        ByteBuffer data;
    }
    
    /**
     * Read the {@link Chunk} objects from the given binary glTF 2.0 data
     *  
     * @param data The input data
     * @return The chunks
     * @throws IOException If an IO error occurs
     */
    private static List<Chunk> readChunks(ByteBuffer data) throws IOException
    {
        int headerLength = BINARY_GLTF_VERSION_2_HEADER_LENGTH_IN_BYTES;
        List<Chunk> chunks = new ArrayList<Chunk>();
        int offset = headerLength;
        while (offset < data.capacity())
        {
            Chunk chunk = new Chunk();
            chunk.length = data.getInt(offset);
            offset += 4;
            chunk.type = data.getInt(offset);
            offset += 4;
            if (offset + chunk.length > data.capacity())
            {
                throw new IOException("The offset for the data of chunk "
                    + chunks.size() + " is " + offset + ", its length is "
                    + chunk.length + ", but " + (offset + chunk.length)
                    + " is larger than capacity of the buffer, which is only "
                    + data.capacity());
            }
            chunk.data = Buffers.createSlice(data, offset, chunk.length);
            offset += chunk.length;
            chunks.add(chunk);
        }
        return chunks;
    }

    /**
     * Private constructor to prevent instantiation
     */
    private RawBinaryGltfDataReaderV2()
    {
        // Private constructor to prevent instantiation
    }
}
