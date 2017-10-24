package io.github.movementspeed.nhglib.input.utils;

public class InputUtil {
    public static int keyCodeFromName(String keyName) {
        int keyCode = -1;

        switch (keyName) {
            case "ANY_KEY":
                keyCode = -1;
                break;
            case "NUM_0":
                keyCode = 7;
                break;
            case "NUM_1":
                keyCode = 8;
                break;
            case "NUM_2":
                keyCode = 9;
                break;
            case "NUM_3":
                keyCode = 10;
                break;
            case "NUM_4":
                keyCode = 11;
                break;
            case "NUM_5":
                keyCode = 12;
                break;
            case "NUM_6":
                keyCode = 13;
                break;
            case "NUM_7":
                keyCode = 14;
                break;
            case "NUM_8":
                keyCode = 15;
                break;
            case "NUM_9":
                keyCode = 16;
                break;
            case "A":
                keyCode = 29;
                break;
            case "ALT_LEFT":
                keyCode = 57;
                break;
            case "ALT_RIGHT":
                keyCode = 58;
                break;
            case "APOSTROPHE":
                keyCode = 75;
                break;
            case "AT":
                keyCode = 77;
                break;
            case "B":
                keyCode = 30;
                break;
            case "BACK":
                keyCode = 4;
                break;
            case "BACKSLASH":
                keyCode = 73;
                break;
            case "C":
                keyCode = 31;
                break;
            case "CALL":
                keyCode = 5;
                break;
            case "CAMERA":
                keyCode = 27;
                break;
            case "CLEAR":
                keyCode = 28;
                break;
            case "COMMA":
                keyCode = 55;
                break;
            case "D":
                keyCode = 32;
                break;
            case "DEL":
                keyCode = 67;
                break;
            case "BACKSPACE":
                keyCode = 67;
                break;
            case "FORWARD_DEL":
                keyCode = 112;
                break;
            case "DPAD_CENTER":
                keyCode = 23;
                break;
            case "DPAD_DOWN":
                keyCode = 20;
                break;
            case "DPAD_LEFT":
                keyCode = 21;
                break;
            case "DPAD_RIGHT":
                keyCode = 22;
                break;
            case "DPAD_UP":
                keyCode = 19;
                break;
            case "CENTER":
                keyCode = 23;
                break;
            case "DOWN":
                keyCode = 20;
                break;
            case "LEFT":
                keyCode = 21;
                break;
            case "RIGHT":
                keyCode = 22;
                break;
            case "UP":
                keyCode = 19;
                break;
            case "E":
                keyCode = 33;
                break;
            case "ENDCALL":
                keyCode = 6;
                break;
            case "ENTER":
                keyCode = 66;
                break;
            case "ENVELOPE":
                keyCode = 65;
                break;
            case "EQUALS":
                keyCode = 70;
                break;
            case "EXPLORER":
                keyCode = 64;
                break;
            case "F":
                keyCode = 34;
                break;
            case "FOCUS":
                keyCode = 80;
                break;
            case "G":
                keyCode = 35;
                break;
            case "GRAVE":
                keyCode = 68;
                break;
            case "H":
                keyCode = 36;
                break;
            case "HEADSETHOOK":
                keyCode = 79;
                break;
            case "HOME":
                keyCode = 3;
                break;
            case "I":
                keyCode = 37;
                break;
            case "J":
                keyCode = 38;
                break;
            case "K":
                keyCode = 39;
                break;
            case "L":
                keyCode = 40;
                break;
            case "LEFT_BRACKET":
                keyCode = 71;
                break;
            case "M":
                keyCode = 41;
                break;
            case "MEDIA_FAST_FORWARD":
                keyCode = 90;
                break;
            case "MEDIA_NEXT":
                keyCode = 87;
                break;
            case "MEDIA_PLAY_PAUSE":
                keyCode = 85;
                break;
            case "MEDIA_PREVIOUS":
                keyCode = 88;
                break;
            case "MEDIA_REWIND":
                keyCode = 89;
                break;
            case "MEDIA_STOP":
                keyCode = 86;
                break;
            case "MENU":
                keyCode = 82;
                break;
            case "MINUS":
                keyCode = 69;
                break;
            case "MUTE":
                keyCode = 91;
                break;
            case "N":
                keyCode = 42;
                break;
            case "NOTIFICATION":
                keyCode = 83;
                break;
            case "NUM":
                keyCode = 78;
                break;
            case "O":
                keyCode = 43;
                break;
            case "P":
                keyCode = 44;
                break;
            case "PERIOD":
                keyCode = 56;
                break;
            case "PLUS":
                keyCode = 81;
                break;
            case "POUND":
                keyCode = 18;
                break;
            case "POWER":
                keyCode = 26;
                break;
            case "Q":
                keyCode = 45;
                break;
            case "R":
                keyCode = 46;
                break;
            case "RIGHT_BRACKET":
                keyCode = 72;
                break;
            case "S":
                keyCode = 47;
                break;
            case "SEARCH":
                keyCode = 84;
                break;
            case "SEMICOLON":
                keyCode = 74;
                break;
            case "SHIFT_LEFT":
                keyCode = 59;
                break;
            case "SHIFT_RIGHT":
                keyCode = 60;
                break;
            case "SLASH":
                keyCode = 76;
                break;
            case "SOFT_LEFT":
                keyCode = 1;
                break;
            case "SOFT_RIGHT":
                keyCode = 2;
                break;
            case "SPACE":
                keyCode = 62;
                break;
            case "STAR":
                keyCode = 17;
                break;
            case "SYM":
                keyCode = 63;
                break;
            case "T":
                keyCode = 48;
                break;
            case "TAB":
                keyCode = 61;
                break;
            case "U":
                keyCode = 49;
                break;
            case "UNKNOWN":
                keyCode = 0;
                break;
            case "V":
                keyCode = 50;
                break;
            case "VOLUME_DOWN":
                keyCode = 25;
                break;
            case "VOLUME_UP":
                keyCode = 24;
                break;
            case "W":
                keyCode = 51;
                break;
            case "X":
                keyCode = 52;
                break;
            case "Y":
                keyCode = 53;
                break;
            case "Z":
                keyCode = 54;
                break;
            case "META_ALT_LEFT_ON":
                keyCode = 16;
                break;
            case "META_ALT_ON":
                keyCode = 2;
                break;
            case "META_ALT_RIGHT_ON":
                keyCode = 32;
                break;
            case "META_SHIFT_LEFT_ON":
                keyCode = 64;
                break;
            case "META_SHIFT_ON":
                keyCode = 1;
                break;
            case "META_SHIFT_RIGHT_ON":
                keyCode = 128;
                break;
            case "META_SYM_ON":
                keyCode = 4;
                break;
            case "CONTROL_LEFT":
                keyCode = 129;
                break;
            case "CONTROL_RIGHT":
                keyCode = 130;
                break;
            case "ESCAPE":
                keyCode = 131;
                break;
            case "END":
                keyCode = 132;
                break;
            case "INSERT":
                keyCode = 133;
                break;
            case "PAGE_UP":
                keyCode = 92;
                break;
            case "PAGE_DOWN":
                keyCode = 93;
                break;
            case "PICTSYMBOLS":
                keyCode = 94;
                break;
            case "SWITCH_CHARSET":
                keyCode = 95;
                break;
            case "BUTTON_CIRCLE":
                keyCode = 255;
                break;
            case "BUTTON_A":
                keyCode = 96;
                break;
            case "BUTTON_B":
                keyCode = 97;
                break;
            case "BUTTON_C":
                keyCode = 98;
                break;
            case "BUTTON_X":
                keyCode = 99;
                break;
            case "BUTTON_Y":
                keyCode = 100;
                break;
            case "BUTTON_Z":
                keyCode = 101;
                break;
            case "BUTTON_L1":
                keyCode = 102;
                break;
            case "BUTTON_R1":
                keyCode = 103;
                break;
            case "BUTTON_L2":
                keyCode = 104;
                break;
            case "BUTTON_R2":
                keyCode = 105;
                break;
            case "BUTTON_THUMBL":
                keyCode = 106;
                break;
            case "BUTTON_THUMBR":
                keyCode = 107;
                break;
            case "BUTTON_START":
                keyCode = 108;
                break;
            case "BUTTON_SELECT":
                keyCode = 109;
                break;
            case "BUTTON_MODE":
                keyCode = 110;
                break;
            case "NUMPAD_0":
                keyCode = 144;
                break;
            case "NUMPAD_1":
                keyCode = 145;
                break;
            case "NUMPAD_2":
                keyCode = 146;
                break;
            case "NUMPAD_3":
                keyCode = 147;
                break;
            case "NUMPAD_4":
                keyCode = 148;
                break;
            case "NUMPAD_5":
                keyCode = 149;
                break;
            case "NUMPAD_6":
                keyCode = 150;
                break;
            case "NUMPAD_7":
                keyCode = 151;
                break;
            case "NUMPAD_8":
                keyCode = 152;
                break;
            case "NUMPAD_9":
                keyCode = 153;
                break;
            case "COLON":
                keyCode = 243;
                break;
            case "F1":
                keyCode = 244;
                break;
            case "F2":
                keyCode = 245;
                break;
            case "F3":
                keyCode = 246;
                break;
            case "F4":
                keyCode = 247;
                break;
            case "F5":
                keyCode = 248;
                break;
            case "F6":
                keyCode = 249;
                break;
            case "F7":
                keyCode = 250;
                break;
            case "F8":
                keyCode = 251;
                break;
            case "F9":
                keyCode = 252;
                break;
            case "F10":
                keyCode = 253;
                break;
            case "F11":
                keyCode = 254;
                break;
            case "F12":
                keyCode = 255;
                break;
        }

        return keyCode;
    }
}
