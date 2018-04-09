package io.github.movementspeed.nhglib.files;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.utils.FloatArray;
import io.github.movementspeed.nhglib.graphics.ogl.NhgFloatTextureData;
import io.github.movementspeed.nhglib.utils.graphics.GLUtils;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

public class HDRData {
    private int width;
    private int height;

    private float lummean;
    private float lummax;
    private float lummin;

    private Texture texture;
    private FloatArray flatArray;

    public HDRData(byte[] in) throws IOException {
        if (in == null)
            throw new NullPointerException();

        float[][][] pixels = read(in);
        texture = toTexture(pixels);
        flatArray.clear();
        flatArray = null;
    }

    public void dispose() {
        if (texture != null) {
            texture.dispose();
            texture = null;
        }
    }

    public Texture getTexture() {
        return texture;
    }

    private float[][][] read(byte[] in) throws IOException {
        ByteCounter counter = new ByteCounter();
        //Parse HDR file's header line
        //readLine(InputStream in) method will be introduced later.

        //The first line of the HDR file. If it is a HDR file, the first line should be "#?RADIANCE"
        //If not, we will throw a IllegalArgumentException.
        String isHDR = readLine(counter, in);
        if (!isHDR.equals("#?RADIANCE"))
            throw new IllegalArgumentException("Unrecognized format: " + isHDR);

        //Besides the first line, there are serval lines describe the different information of this HDR file.
        //Maybe it will have the exposure time, format(Must be either"32-bit_rle_rgbe" or "32-bit_rle_xyze")
        //Also the owner's information, the software's versioin, etc.

        //The above information is not so important for us.
        //The only important information for us is the Resolution which shows the size of the HDR image
        //The resolution information's format is fixed. Usaually, it will be -Y 1024 +X 2048 something like this.
        String inform = readLine(counter, in);
        while (!inform.equals("")) {
            inform = readLine(counter, in);
        }
        inform = readLine(counter, in);
        String[] tokens = inform.split(" ", 4);
        if (tokens[0].charAt(1) == 'Y') {
            width = Integer.parseInt(tokens[3]);
            height = Integer.parseInt(tokens[1]);
        } else {
            width = Integer.parseInt(tokens[1]);
            height = Integer.parseInt(tokens[3]);
        }

        if (width <= 0)
            throw new IllegalArgumentException("Width must be positive");
        if (height <= 0)
            throw new IllegalArgumentException("Height must be positive");

        //In the above, the basic information has been collected. Now, we will deal with the pixel data.
        //According to the HDR format document, each pixel is stored as 4 bytes, one bytes mantissa for each r,g,b and a shared one byte exponent.
        //The pixel data may be stored uncompressed or using a straightforward run length encoding scheme.

        //DataInput din = new DataInputStream(in);
        int[][][] buffers = new int[height][width][4];

        //We read the information row by row. In each row, the first four bytes store the column number information.
        //The first and second bytes store "2". And the third byte stores the higher 8 bits of col num, the fourth byte stores the lower 8 bits of col num.
        //After these four bytes, these are the real pixel data.
        for (int i = 0; i < height; i++) {
            //The following code patch is checking whether the hdr file is compressed by run length encode(RLE).
            //For every line of the data part, the first and second byte should be 2(DEC).
            //The third*2^8+the fourth should equals to the width. They combined the width information.
            //For every line, we need check this kind of informatioin. And the starting four nums of every line is the same
            int a = unsignedToBytes(in[counter.get()]);
            int b = unsignedToBytes(in[counter.get()]);
            int c = unsignedToBytes(in[counter.get()]);
            int d = unsignedToBytes(in[counter.get()]);

            if (a != 2 || b != 2)
                throw new IllegalArgumentException("This hdr file is not made by RLE run length encoded ");
            if (((c << 8) + d) != width)
                throw new IllegalArgumentException("Wrong width information");

            //This inner loop is for the four channels. The way they compressed the data is in this way:
            //Firstly, they compressed a row.
            //Inside that row, they firstly compressed the red channel information. If there are duplicate data, they will use RLE to compress.
            //First data shows the numbers of duplicates(which should minus 128), and the following data is the duplicate one.
            //If there is no duplicate, they will store the information in order.
            //And the first data is the number of how many induplicate items, and the following data stream is their associated data.
            for (int j = 0; j < 4; j++) { //This loop controls the four channel. R,G,B and Exp.
                for (int w = 0; w < width; ) {//This w controls the Wth col to readin.
                    int num = unsignedToBytes(in[counter.get()]);
                    if (num > 128) {//This means the following one data is duplicate item. And
                        int duplicate = unsignedToBytes(in[counter.get()]);
                        num -= 128;
                        while (num > 0) {
                            buffers[i][w++][j] = duplicate;
                            num--;
                        }
                    } else {  //This situation is the no duplicate case.
                        while (num > 0) {
                            buffers[i][w++][j] = unsignedToBytes(in[counter.get()]);
                            num--;
                        }
                    }
                }
            }

        }

        //The above for loop is used to generated the four channel of each pixel. RGBE. The next patch of codes are used to generate float pixel values of each three channel by using the transition expression.
        /*The transition relationship between rgbe and HDR FP32(RGB) is as follow:
         * 1. From rgbe to FP 32 (RGB)   this relationship is used to input and decode the HDR file.
		 * if(e==0) R=G=B=0.0;
		 * else R=r*2^(e-128-8);
		 *      G=g*2^(e-128-8);
		 *      B=b*2^(e-128-8);
		 *
		 * 2.From FP32(RGB) to rgbe   This relationship is used to output and encode the HDR file.
		 * v=max(R,G,B);
		 * if(v<1e-32),r=g=b=0;
		 * else  we present v as v=m*2^n(0.5<=m<=1)
		 *       r=R*m*256.0/v;
		 *       g=G*m*256.0/v;
		 *       b=B*m*256.0/v;
		 *       e=n+128;
		 *
		 *
		 *pixels[][][] stores the FP32(RGB) information. pixels[i][j][0] stores the R channel.
		 *
		 *By the way, we need generate the luminance of each pixel. By using the expressing:
		 *Y=0.299*R+0.587*G+0.114*B;
		 */
        float[][][] pixels = new float[height][width][3];
        float[][] lum = new float[height][width];
        float lmax = 0.0F;     //This float value is storing the max value of FP32 (RGB) data.
        for (int i = 0; i < height; i++) {
            for (int j = 0; j < width; j++) {
                int exp = buffers[i][j][3];
                if (exp == 0) {
                    pixels[i][j][0] = 0.0F;
                    pixels[i][j][1] = 0.0F;
                    pixels[i][j][2] = 0.0F;
                    lum[i][j] = 0.0F;
                } else {
                    float exppart = (float) Math.pow(2, exp - 128 - 8);
                    pixels[i][j][0] = buffers[i][j][0] * exppart;
                    pixels[i][j][1] = buffers[i][j][1] * exppart;
                    pixels[i][j][2] = buffers[i][j][2] * exppart;

                    lum[i][j] = (float) (0.299 * pixels[i][j][0] + 0.587 * pixels[i][j][1] + 0.114 * pixels[i][j][2]);

                    if (lum[i][j] > lmax) {
                        lmax = lum[i][j];
                    }
                }

                flatArray.addAll(pixels[i][j][0], pixels[i][j][1], pixels[i][j][2]);
            }
        }

        //The next step is normalize to 1; In the above loop, we already find the max value of the FP32(RGB) data.
        lummax = 0.0F;
        lummin = 1.0F;
        float lumsum = 0.0F;
        for (int i = 0; i < height; i++) {
            for (int j = 0; j < width; j++) {
                lum[i][j] /= lmax;
                if (lum[i][j] > lummax) {
                    lummax = lum[i][j];
                }
                if (lum[i][j] < lummin) {
                    lummin = lum[i][j];
                }
                lumsum += lum[i][j];
            }
        }

        lummean = lumsum / (height * width);
        return pixels;
    }

    private Texture toTexture(float[][][] pixels) {
        Texture texture;

        float[] rgb = new float[width * height * 3];
        for (int i = 0; i < flatArray.size; i++) {
            rgb[i] = flatArray.get(i);
        }

        float min = Float.MAX_VALUE, max = Float.MIN_VALUE;

        for (float f : rgb) {
            if (f < min) {
                min = f;
            }

            if (f > max) {
                max = f;
            }
        }

        if (GLUtils.isFloatTextureSupported()) {
            NhgFloatTextureData data = new NhgFloatTextureData(width, height, 3);
            data.prepare();
            data.getBuffer().put(rgb);
            data.getBuffer().flip();

            texture = new Texture(data);
        } else {
            Pixmap pixmap = new Pixmap(width, height, Pixmap.Format.RGB888);

            for (int y = 0; y < height; y++) {
                for (int x = 0; x < width; x++) {
                    float r = pixels[y][x][0];
                    float g = pixels[y][x][1];
                    float b = pixels[y][x][2];

                    r /= r + 1.0f;
                    g /= g + 1.0f;
                    b /= b + 1.0f;

                    pixmap.drawPixel(x, y, Color.rgba8888(r, g, b, 1.0f));
                }
            }

            texture = new Texture(pixmap);
            pixmap.dispose();
        }

        return texture;
    }

    private String readLine(ByteCounter counter, byte[] bytes) throws IOException {
        ByteArrayOutputStream bout = new ByteArrayOutputStream();
        for (int i = 0; ; i++) {
            int b = bytes[counter.get()];
            if (b == '\n' || b == -1) {
                break;
            } else if (i == 100) {
                throw new IllegalArgumentException("Line too long");
            } else {
                bout.write(b);
            }
        }
        return new String(bout.toByteArray(), "US-ASCII");
    }

    public static int unsignedToBytes(byte b) {
        return b & 0xFF;
    }

    private class ByteCounter {
        private int count;

        public void inc() {
            count++;
        }

        public int get() {
            int c = count;
            inc();
            return c;
        }
    }
}
