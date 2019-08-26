package io.github.movementspeed.nhglib.utils.data;

import com.badlogic.gdx.math.Vector3;

/**
 * Created by Fausto Napoli on 02/01/2017.
 * Simple data structure to hold 3D bounds data.
 */
public class Bounds {
    private float halfWidth;
    private float halfHeight;
    private float halfDepth;

    private float width;
    private float height;
    private float depth;

    public Bounds(float width, float height, float depth) {
        setWidth(width);
        setHeight(height);
        setDepth(depth);
    }

    public void setWidth(float width) {
        if (width > 0f) {
            this.width = width;
            this.halfWidth = width / 2f;
        } else {
            this.width = 0f;
            this.halfWidth = 0f;
        }
    }

    public void setHeight(float height) {
        if (height > 0f) {
            this.height = height;
            this.halfHeight = height / 2f;
        } else {
            this.height = 0f;
            this.halfHeight = 0f;
        }
    }

    public void setDepth(float depth) {
        if (depth > 0f) {
            this.depth = depth;
            this.halfDepth = depth / 2f;
        } else {
            this.depth = 0f;
            this.halfDepth = 0f;
        }
    }

    public float getWidth() {
        return width;
    }

    public float getHeight() {
        return height;
    }

    public float getDepth() {
        return depth;
    }

    public boolean inBounds(Vector3 point) {
        boolean res = true;

        if (point.x > halfWidth || point.x < -halfWidth) {
            res = false;
        } else if (point.y > halfHeight || point.y < -halfHeight) {
            res = false;
        } else if (point.z > halfDepth || point.z < -halfDepth) {
            res = false;
        }

        return res;
    }

    public Info boundsInfo(Vector3 point) {
        Info info = new Info();
        info.inBounds = inBounds(point);

        if (!info.inBounds) {
            if (point.x > halfWidth) {
                info.widthSide = 1;
            } else if (point.x < -halfWidth) {
                info.widthSide = -1;
            }

            if (point.y > halfHeight) {
                info.heightSide = 1;
            } else if (point.y < -halfHeight) {
                info.heightSide = -1;
            }

            if (point.z > halfDepth) {
                info.depthSide = 1;
            } else if (point.z < -halfDepth) {
                info.depthSide = -1;
            }
        }

        return info;
    }

    public class Info {
        public boolean inBounds;
        public int widthSide;
        public int heightSide;
        public int depthSide;

        public Info() {
            inBounds = false;
            widthSide = 0;
            heightSide = 0;
            depthSide = 0;
        }
    }
}
