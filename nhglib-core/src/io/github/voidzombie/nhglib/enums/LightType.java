package io.github.voidzombie.nhglib.enums;

/**
 * Created by Fausto Napoli on 14/03/2017.
 */
public enum LightType {
    DIRECTIONAL_LIGHT,
    AMBIENT_LIGHT,
    POINT_LIGHT,
    SPOT_LIGHT;

    public static LightType fromString(String value) {
        LightType res = null;

        switch (value) {
            case "directionalLight":
                res = DIRECTIONAL_LIGHT;
                break;

            case "ambientLight":
                res = AMBIENT_LIGHT;
                break;

            case "pointLight":
                res = POINT_LIGHT;
                break;

            case "spotLight":
                res = SPOT_LIGHT;
                break;
        }

        return res;
    }
}
