package com.leclowndu93150.keepequipment;

public class KeepEquipmentUtil {

    public static int levelsToXP(float level) {
        if (level <= 0.0F) {
            return 0;
        } else if (level <= 16.0F) {
            return (int)(level * level + 6.0F * level);
        } else {
            return level <= 31.0F ? (int)(2.5F * level * level - 40.5F * level + 360.0F) : (int)(4.5F * level * level - 162.5F * level + 2220.0F);
        }
    }

    public static float xpToLevels(int xp) {
        if (xp <= 0) {
            return 0.0F;
        } else if (xp <= 352) {
            return (float)(Math.sqrt((double)((float)xp + 9.0F)) - 3.0D);
        } else {
            return xp <= 1507 ? (float)(Math.sqrt((double)(0.4F * ((float)xp - 195.975F))) + 8.100000381469727D) : (float)(Math.sqrt((double)(0.22222222F * ((float)xp - 752.9861F))) + 18.05555534362793D);
        }
    }
}
