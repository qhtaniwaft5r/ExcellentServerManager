//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by FernFlower decompiler)
//

package utils;

import org.bukkit.Bukkit;

public enum ServerVersion {
    v1_8_R1,
    v1_8_R2,
    v1_8_R3,
    v1_9_R1,
    v1_9_R2,
    v1_10_R1,
    v1_11_R1,
    v1_12_R1,
    v1_13_R1,
    v1_13_R2,
    v1_14_R1,
    v1_14_R2,
    v1_15_R1,
    v1_15_R2,
    v1_16_R1,
    v1_16_R2,
    v1_16_R3,
    v1_17_R1,
    v1_17_R2,
    v1_18_R1,
    v1_18_R2,
    v1_19_R1,
    v1_19_R2,
    v1_19_R3,
    v1_20_R1;

    private final int value = Integer.parseInt(this.name().replaceAll("[^\\d.]", ""));
    private static String[] arrayVersion;
    private static ServerVersion current;

    ServerVersion (){

    }
    public static ServerVersion getCurrent() {

        if (current == null) {
            String[] v = getArrayVersion();
            String vv = v[v.length - 1];
            ServerVersion[] arrayOfServerVersion;
            int i = (arrayOfServerVersion = values()).length;

            for (byte b = 0; b < i; ++b) {
                ServerVersion one = arrayOfServerVersion[b];
                if (one.name().equalsIgnoreCase(vv)) {
                    current = one;
                    break;
                }
            }

            if (current == null) {
                current = v1_16_R3;
            }

        }
        return current;
    }

    public static String[] getArrayVersion() {
        if (arrayVersion == null) {
            arrayVersion = Bukkit.getServer().getClass().getPackage().getName().split("\\.");
        }

        return arrayVersion;
    }

    public static boolean isCurrentLower(ServerVersion v) {
        return getCurrent().value < v.value;
    }
}
