package io.github.movementspeed.nhglib.graphics.lighting.tiled;

import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Matrix4;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.math.collision.BoundingBox;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.IntArray;
import io.github.movementspeed.nhglib.graphics.lights.NhgLight;
import io.github.movementspeed.nhglib.math.Vector4;

public class LightGrid {
    public static int RES_X = 1280;
    public static int RES_Y = 720;

    //quads
    public static int QUAD_WIDTH = (RES_X - (RES_X / 4)) / 4;
    public static int QUAD_HEIGHT = RES_Y / 6;
    public static int QUAD_POS = RES_X - (QUAD_WIDTH + RES_X / 20);

    //lights
    public static int MAX_LIGHTS = 1024;
    public static float LIGHT_RADIUS_MIN = 100.0f;
    public static float LIGHT_RADIUS_MAX = 400.0f;

    //lightgrid constants
    public static int TILE_SIZE_XY = 32;
    public static int LIGHT_GRID_DIM_X = ((RES_X + TILE_SIZE_XY - 1) / TILE_SIZE_XY);
    public static int LIGHT_GRID_DIM_Y = ((RES_Y + TILE_SIZE_XY - 1) / TILE_SIZE_XY);
    public static int TILES_COUNT = LIGHT_GRID_DIM_X * LIGHT_GRID_DIM_Y;

    public static Vector2 gridSize = new Vector2(LIGHT_GRID_DIM_X, LIGHT_GRID_DIM_Y);

    private int lightListLength;

    private int offsets[] = new int[TILES_COUNT];
    private int counts[] = new int[TILES_COUNT];

    private Vector2 resolution = new Vector2(RES_X, RES_Y);
    private Vector2 tileSize = new Vector2(TILE_SIZE_XY, TILE_SIZE_XY);

    private IntArray globalLightList = new IntArray();
    private Array<BoundingBox> quads = new Array<>();
    private Array<NhgLight> viewSpaceLights = new Array<>();
    private Array<TileArea> affectedTiles = new Array<>();
    private Array<MinMax> gridMinMax;

    public void buildLightGrid(Array<MinMax> minMax, Array<NhgLight> lights, float n, Matrix4 view, Matrix4 projection) {
        //store minimum/maximum depth to lightgrid
        gridMinMax = minMax;

        //compute ss bbs (bounding quads)
        computeBoundingQuads(lights, view, projection, n);

        //initialize light lists
        for (int i = 0; i < offsets.length; i++) {
            offsets[i] = 0;
            counts[i] = 0;
        }

        lightListLength = 0;

        //Find light count for each tile
        for (int i = 0; i < quads.size; i++) {
            NhgLight l = viewSpaceLights.get(i);

            for (int x = (int) affectedTiles.get(i).x.x; x < affectedTiles.get(i).x.y - 1; x++) {
                for (int y = (int) affectedTiles.get(i).y.x; y < affectedTiles.get(i).y.y - 1; y++) {
                    //tests light against the minimum/maximum of depth buffer 
                    if (gridMinMax.size == 0 || gridMinMax.get(y * (int) gridSize.x + x).max < (l.position.z + l.radius) &&
                            gridMinMax.get(y * (int) gridSize.x + x).min > (l.position.z - l.radius)) {
                        lightListLength++;
                        addToCounts(x, y, 1);
                    }
                }
            }
        }

        //set offsets
        int offset = 0;

        for (int y = 0; y < LIGHT_GRID_DIM_Y; y++) {
            for (int x = 0; x < LIGHT_GRID_DIM_X; x++) {
                int count = counts(x, y);

                setToOffsets(x, y, offset + count);
                offset += count;
            }
        }

        globalLightList.setSize(lightListLength);

        if (globalLightList.size != 0) {
            IntArray data = globalLightList;

            for (int i = 0; i < quads.size; ++i) {
                int lightId = i;

                NhgLight l = viewSpaceLights.get(i);

                for (int x = (int) affectedTiles.get(i).x.x; x < affectedTiles.get(i).x.y - 1; x++) {
                    for (int y = (int) affectedTiles.get(i).y.x; y < affectedTiles.get(i).y.y - 1; y++) {
                        //tests light against the minimum/maximum of depth buffer 
                        if (gridMinMax.size == 0 || gridMinMax.get(y * (int) gridSize.x + x).max < (l.position.z + l.radius) &&
                                gridMinMax.get(y * (int) gridSize.x + x).min > (l.position.z - l.radius)) {

                            // store reversely into next free slot
                            offset = offsets(x, y) - 1;
                            data.set(offset, lightId);
                            setToOffsets(x, y, offset);
                        }
                    }
                }
            }
        }
    }

    public int getLightListLength() {
        return globalLightList.size;
    }

    public int[] getOffsets() {
        return offsets;
    }

    public int[] getCounts() {
        return counts;
    }

    public IntArray getLightList() {
        return globalLightList;
    }

    public Array<NhgLight> getViewSpaceLights() {
        return viewSpaceLights;
    }

    private void computeBoundingQuads(Array<NhgLight> lights, Matrix4 view, Matrix4 projection, float n) {
        //clear vectors
        quads.clear();
        viewSpaceLights.clear();
        affectedTiles.clear();

        for (int i = 0; i < lights.size; i++) {
            NhgLight l = lights.get(i).copy();

            //transform world light position to view space
            //Vector3 posVS = glm::vec3(view * glm::vec4(l.position, 1.0));
            Vector3 posVS = new Vector3(l.position);
            posVS.mul(view);

            //compute bounding quad in clip space
            Vector4 clip = computeBoundingQuad(posVS, l.radius, n, projection);

            //transform quad to viewport
            clip.scl(-1);

            float clipZ = clip.z;
            clip.z = clip.x;
            clip.x = clipZ;

            float clipK = clip.k;
            clip.k = clip.y;
            clip.y = clipK;

            //convert to the [0.0, 1.0] range
            clip.scl(0.5f).add(0.5f);

            //convert clip region to viewport
            BoundingBox quad = new BoundingBox();
            quad.min.x = clip.x * resolution.x;
            quad.min.y = clip.y * resolution.y;
            quad.max.x = clip.z * resolution.x;
            quad.max.y = clip.k * resolution.y;

            //store viewspace lights and their quads
            //lights are stored in world space
            if (quad.min.x < quad.max.x && quad.min.y < quad.max.y) {
                //store ss quad
                quads.add(quad);

                //convert light to view space and store it as viewspace light
                l.position.set(posVS);
                viewSpaceLights.add(l);

                computeLightAffectedTiles(quad.min.x, quad.max.x, quad.min.y, quad.max.y);
            }
        }
    }

    private void computeLightAffectedTiles(float minx, float maxx, float miny, float maxy) {
        Vector2 x = new Vector2(minx / TILE_SIZE_XY, (maxx + TILE_SIZE_XY - 1) / TILE_SIZE_XY);
        Vector2 y = new Vector2(miny / TILE_SIZE_XY, (maxy + TILE_SIZE_XY - 1) / TILE_SIZE_XY);

        TileArea tmp = new TileArea();

        tmp.x = clamp(x, new Vector2(), new Vector2(gridSize.x + 1, gridSize.x + 1));
        tmp.y = clamp(y, new Vector2(), new Vector2(gridSize.y + 1, gridSize.y + 1));

        affectedTiles.add(tmp);
    }

    private Vector2 clamp(Vector2 value, Vector2 min, Vector2 max) {
        value.x = MathUtils.clamp(value.x, min.x, max.x);
        value.y = MathUtils.clamp(value.y, min.y, max.y);
        return value;
    }

    private Vector4 computeBoundingQuad(Vector3 Lp, float Lr, float n, Matrix4 projectionMatrix) {
        Vector4 boundingQuad = new Vector4(1.0f, 1.0f, -1.0f, -1.0f);

        if (Lp.z - Lr <= -n) {
            boundingQuad = new Vector4(-1.0f, -1.0f, 1.0f, 1.0f);

            MinMax minMax = new MinMax();

            minMax.min = boundingQuad.x;
            minMax.max = boundingQuad.z;
            computeRoots(Lp.x, Lp.z, Lr, projectionMatrix.val[Matrix4.M00], minMax);
            boundingQuad.x = minMax.min;
            boundingQuad.z = minMax.max;

            minMax.min = boundingQuad.y;
            minMax.max = boundingQuad.k;
            computeRoots(Lp.y, Lp.z, Lr, projectionMatrix.val[Matrix4.M11], minMax);
            boundingQuad.y = minMax.min;
            boundingQuad.k = minMax.max;
        }

        return boundingQuad;
    }

    void computeRoots(float Lc, float Lz, float Lr, float proj, MinMax minMax) {
        float LrSquare = Lr * Lr;
        float LcSquare = Lc * Lc;
        float LzSquare = Lz * Lz;

        float denominator = LcSquare + LzSquare;

        //eq (4.8)
        float D = LrSquare * LcSquare - denominator * (LrSquare - LzSquare);

        //check if point light does not fill whole screen
        if (D < 0.0) {
            return;
        } else {
            float Nx1 = (Lc * Lr + (float) Math.sqrt(D)) / denominator;
            float Nx2 = (Lc * Lr - (float) Math.sqrt(D)) / denominator;

            updateRoots(Nx1, Lc, Lz, Lr, proj, minMax);
            updateRoots(Nx2, Lc, Lz, Lr, proj, minMax);
        }
    }

    private void updateRoots(float Nc, float Lc, float Lz, float Lr, float proj, MinMax minMax) {
        float Nz = (Lr - Nc * Lc) / Lz;
        float Pz = (Lc * Lc + Lz * Lz - Lr * Lr) / (Lz - (Nz / Nc) * Lc);

        //check if point P lies in front of camera (z coords must be less than 0)
        if (Pz < 0.0f) {
            float c = -Nz * proj / Nc;
            if (Nc < 0.0f) {
                minMax.min = Math.max(minMax.min, c);
            } else {
                minMax.max = Math.max(minMax.max, c);
            }
        }
    }

    private void addToCounts(int i, int j, int n) {
        counts[i + j * LIGHT_GRID_DIM_Y] += n;
    }

    private void setToOffsets(int i, int j, int n) {
        offsets[i + j * LIGHT_GRID_DIM_X] = n;
    }

    private int offsets(int i, int j) {
        return offsets[i + j * LIGHT_GRID_DIM_X];
    }

    private int counts(int i, int j) {
        return counts[i + j * LIGHT_GRID_DIM_Y];
    }
}
