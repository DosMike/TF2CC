package com.github.dosmike.tf2cc;

import java.util.Base64;
import java.util.Random;

public class CodeGenerator {
    int codecnt=0;
    static Random rng = new Random();

    public String generate() {
        long systimedec = (System.currentTimeMillis() / 100) % 0xFFFFF;
        int rand = rng.nextInt(256);
        String code = Base64.getUrlEncoder().encodeToString(new byte[]{
                (byte) ((rand & 0xf0) | ((systimedec >> 16) & 0x0f)),
                (byte) ((systimedec >> 8) & 0xff),
                (byte) ((systimedec >> 4) & 0xff),
                (byte) ((codecnt << 4) | (rand & 0x0f))
        }).substring(0,6); // 32 bits always fit in 6+6+6+6+6+2 bits = 6 bytes + 2*2 bits padding, so we cut off the ==
        codecnt = (codecnt + 1) % 16;
        return code;
    }

}
