package io.github.movementspeed.nhglib.graphics.shaders.tiledForward;

import com.badlogic.gdx.graphics.PerspectiveCamera;
import com.badlogic.gdx.math.Frustum;
import com.badlogic.gdx.math.Plane;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.IntArray;

public class SmallFrustums {
    private int sizeX;
    private int sizeY;

    private Plane[][] verticalPlanes;
    private Plane[][] horizontalPlanes;
    private Vector3[] planePoints;

    private Vector3 nearBottomLeft;
    private Vector3 nearBottomRight;
    private Vector3 nearTopRight;
    private Vector3 nearTopLeft;
    private Vector3 farBottomLeft;
    private Vector3 farBottomRight;
    private Vector3 farTopRight;
    private Vector3 farTopLeft;
    private Vector3 ndw;
    private Vector3 ndh;
    private Vector3 fdw;
    private Vector3 fdh;
    private Vector3 temp;
    private Vector3 tempNearBotLeft;
    private Vector3 tempFarBotLeft;

    public SmallFrustums(int x, int y) {
        planePoints = new Vector3[8];

        for (int i = 0; i < 8; i++) {
            planePoints[i] = new Vector3();
        }

        sizeX = x;
        sizeY = y;

        verticalPlanes = new Plane[2][10];
        horizontalPlanes = new Plane[2][10];

        for (int i = 0; i < 10; i++) {
            for (int j = 0; j < 2; j++) {
                verticalPlanes[j][i] = new Plane(new Vector3(), 0);
                horizontalPlanes[j][i] = new Plane(new Vector3(), 0);
            }
        }

        nearBottomLeft = new Vector3();
        nearBottomRight = new Vector3();
        nearTopRight = new Vector3();
        nearTopLeft = new Vector3();
        farBottomLeft = new Vector3();
        farBottomRight = new Vector3();
        farTopRight = new Vector3();
        farTopLeft = new Vector3();
        ndw = new Vector3();
        ndh = new Vector3();
        fdw = new Vector3();
        fdh = new Vector3();
        temp = new Vector3();
        tempNearBotLeft = new Vector3();
        tempFarBotLeft = new Vector3();
    }

    /* Builds the planes used for the sub frustums.
    */
    public void setFrustums(PerspectiveCamera cam) {
        Frustum bigFrustum = cam.frustum;

        nearBottomLeft.set(bigFrustum.planePoints[0]);
        nearBottomRight.set(bigFrustum.planePoints[1]);

        nearTopRight.set(bigFrustum.planePoints[2]);
        nearTopLeft.set(bigFrustum.planePoints[3]);

        farBottomLeft.set(bigFrustum.planePoints[4]);
        farBottomRight.set(bigFrustum.planePoints[5]);

        farTopRight.set(bigFrustum.planePoints[6]);
        farTopLeft.set(bigFrustum.planePoints[7]);

        ndw.set(nearBottomRight).sub(nearBottomLeft).scl(1 / sizeX);
        ndh.set(nearTopLeft).sub(nearBottomLeft).scl(1 / sizeY);

        fdw.set(farBottomRight).sub(farBottomLeft).scl(1 / sizeX);
        fdh.set(farTopRight).sub(farBottomRight).scl(1 / sizeY);

        for (int x = 0; x < sizeX; x++) {
            temp.set(ndw).scl(x);
            tempNearBotLeft.set(nearBottomLeft);
            tempNearBotLeft.add(temp);

            planePoints[0].set(tempNearBotLeft);
            planePoints[1].set(tempNearBotLeft).add(ndw);
            planePoints[2].set(tempNearBotLeft).add(ndw).add(ndh);
            planePoints[3].set(tempNearBotLeft).add(ndh);

            temp.set(fdw).scl(x);
            tempFarBotLeft.set(farBottomLeft);
            tempFarBotLeft.add(temp);

            planePoints[4].set(tempFarBotLeft);
            planePoints[5].set(tempFarBotLeft).add(fdw);
            planePoints[6].set(tempFarBotLeft).add(fdw).add(fdh);
            planePoints[7].set(tempFarBotLeft).add(fdh);

            verticalPlanes[0][x].set(planePoints[0], planePoints[4], planePoints[3]);
            verticalPlanes[1][x].set(planePoints[5], planePoints[1], planePoints[6]);
        }

        for (int y = 0; y < sizeY; y++) {
            tempNearBotLeft.set(nearBottomLeft);
            temp.set(ndh).scl(y);
            tempNearBotLeft.set(tempNearBotLeft);
            tempNearBotLeft.add(temp);

            planePoints[0].set(tempNearBotLeft);
            planePoints[1].set(tempNearBotLeft).add(ndw);
            planePoints[2].set(tempNearBotLeft).add(ndw).add(ndh);
            planePoints[3].set(tempNearBotLeft).add(ndh);

            tempFarBotLeft.set(farBottomLeft);

            temp.set(fdh).scl(y);
            tempFarBotLeft.set(tempFarBotLeft);
            tempFarBotLeft.add(temp);

            planePoints[4].set(tempFarBotLeft);
            planePoints[5].set(tempFarBotLeft).add(fdw);
            planePoints[6].set(tempFarBotLeft).add(fdw).add(fdh);
            planePoints[7].set(tempFarBotLeft).add(fdh);

            horizontalPlanes[0][y].set(planePoints[2], planePoints[3], planePoints[6]);
            horizontalPlanes[1][y].set(planePoints[4], planePoints[0], planePoints[1]);
        }
    }

    /* Check what tiles a given light source intersects
     * and updates the array lights2 accordingly.
     */
    public void checkFrustums(Vector3 pos, float radius, Array<IntArray> lights2, int lightID) {
        boolean foundStart = false;
        boolean foundEnd = false;

        int startX = 0;
        int endX = 0;
        int startY = 0;
        int endY = 0;

        for (int x = 0; x < sizeX; x++) {
            if (insideColumn(x, pos, radius)) {
                if (!foundStart) {
                    startX = x;
                }

                foundStart = true;
            } else {
                if (foundStart) {
                    endX = x - 1;
                    foundEnd = true;
                    break;
                }
            }
        }

        if (!foundEnd && foundStart) {
            endX = 9;
        }

        foundStart = false;
        foundEnd = false;

        for (int y = 0; y < sizeY; y++) {
            if (insideRow(y, pos, radius)) {
                if (!foundStart) {
                    startY = y;
                }

                foundStart = true;
            } else {
                if (foundStart) {
                    endY = y - 1;
                    foundEnd = true;
                    break;
                }
            }
        }

        if (!foundEnd && foundStart) {
            endY = 9;
        }

        for (int x = startX; x <= endX; x++) {
            for (int y = startY; y <= endY; y++) {
                lights2.get(y * 10 + x).add(lightID);
            }
        }
    }

    public boolean insideColumn(int x, Vector3 pos, float radius) {
        boolean out = true;

        if (verticalPlanes[0][x].distance(pos) < -radius) {
            out = false;
        }

        if (verticalPlanes[1][x].distance(pos) < -radius) {
            out = false;
        }

        return out;
    }

    public boolean insideRow(int y, Vector3 pos, float radius) {
        boolean out = true;

        if (horizontalPlanes[0][y].distance(pos) < -radius) {
            out = false;
        }

        if (horizontalPlanes[1][y].distance(pos) < -radius) {
            out = false;
        }

        return out;
    }
}
