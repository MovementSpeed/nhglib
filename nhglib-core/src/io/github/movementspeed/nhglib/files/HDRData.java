package io.github.movementspeed.nhglib.files;

import com.badlogic.gdx.utils.Array;

import java.io.*;


// This class is used to convert a HDR format image into a three-dimension float array represents the
//RGB channels of the original image.
public class HDRData {
    //the width of the HDR image
    private int width;
    //the height of the HDR image
    private int height;
    //This three-dimension float array is storing the three channels' information of the image.
    //For example, pixels[2][3][1] presents the red channel[][][1] of row No.2 and col No.3 's pixel
    private float[][][] pixels;
    //This three-dimension int array is storing the four channels' information.
    //[][][] the first and second is location information
    //[][][] the third one is the R,G,B,E. which is its associated information.
    private int[][][] buffers;
    //This two-dimension float array is storing the luminance information of the pixel.
    //We use the YUV format to calculate the luminance by lum=0.299*R+0.587*G+0.114*B
    private float[][] lum;

    private float[] flatPixelArray;

    //The mean value of the lum[][]
    private float lummean;
    //The maximum value of the lum[][]
    private float lummax;
    //The minimum value of the lum[][]
    private float lummin;

    public int getWidth() {
        return width;
    }

    public int getHeight() {
        return height;
    }

    public float[][] getLumArray() {
        return lum;
    }

    public float[][][] getPixelArray() {
        return pixels;
    }

    public float[] getFlatPixelArray() {
        Array<Float> floats = new Array<>();
        flatPixelArray = new float[width * height * 3];

        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                float r = pixels[y][x][0];
                float g = pixels[y][x][1];
                float b = pixels[y][x][2];

                floats.addAll(r, g, b);
            }
        }

        for (int i = 0; i < floats.size; i++) {
            flatPixelArray[i] = floats.get(i);
        }

        return flatPixelArray;
    }

    public float getLummax() {
        return lummax;
    }

    public float getLummin() {
        return lummin;
    }

    public float getLummean() {
        return lummean;
    }

    //Construction method if the input is a file.
    public HDRData(File file) throws IOException {
        if (file == null)
            throw new NullPointerException();
        InputStream in = new BufferedInputStream(new FileInputStream(file));
        try {
            read(in);
        } finally {
            in.close();
        }
    }

    //Construction method if the input is a InputStream.
    //Parse the HDR file by its format. HDR format encode can be seen in Radiance HDR(.pic,.hdr) file format
    private void read(InputStream in) throws IOException {
        //Parse HDR file's header line
        //readLine(InputStream in) method will be introduced later.

        //The first line of the HDR file. If it is a HDR file, the first line should be "#?RADIANCE"
        //If not, we will throw a IllegalArgumentException.
        String isHDR = readLine(in);
        if (!isHDR.equals("#?RADIANCE"))
            throw new IllegalArgumentException("Unrecognized format: " + isHDR);

        //Besides the first line, there are serval lines describe the different information of this HDR file.
        //Maybe it will have the exposure time, format(Must be either"32-bit_rle_rgbe" or "32-bit_rle_xyze")
        //Also the owner's information, the software's versioin, etc.

        //The above information is not so important for us.
        //The only important information for us is the Resolution which shows the size of the HDR image
        //The resolution information's format is fixed. Usaually, it will be -Y 1024 +X 2048 something like this.
        String inform = readLine(in);
        while (!inform.equals("")) {
            inform = readLine(in);
        }
        inform = readLine(in);
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

        DataInput din = new DataInputStream(in);
        buffers = new int[height][width][4];


        //We read the information row by row. In each row, the first four bytes store the column number information.
        //The first and second bytes store "2". And the third byte stores the higher 8 bits of col num, the fourth byte stores the lower 8 bits of col num.
        //After these four bytes, these are the real pixel data.
        for (int i = 0; i < height; i++) {
            //The following code patch is checking whether the hdr file is compressed by run length encode(RLE).
            //For every line of the data part, the first and second byte should be 2(DEC).
            //The third*2^8+the fourth should equals to the width. They combined the width information.
            //For every line, we need check this kind of informatioin. And the starting four nums of every line is the same
            int a = din.readUnsignedByte();
            int b = din.readUnsignedByte();
            int c = din.readUnsignedByte();
            int d = din.readUnsignedByte();
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
                    int num = din.readUnsignedByte();
                    if (num > 128) {//This means the following one data is duplicate item. And
                        int duplicate = din.readUnsignedByte();
                        num -= 128;
                        while (num > 0) {
                            buffers[i][w++][j] = duplicate;
                            num--;
                        }
                    } else {  //This situation is the no duplicate case.
                        while (num > 0) {
                            buffers[i][w++][j] = din.readUnsignedByte();
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
        pixels = new float[height][width][3];
        lum = new float[height][width];
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
    }

    private String readLine(InputStream in) throws IOException {
        ByteArrayOutputStream bout = new ByteArrayOutputStream();
        for (int i = 0; ; i++) {
            int b = in.read();
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

}