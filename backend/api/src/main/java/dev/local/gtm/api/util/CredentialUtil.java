package dev.local.gtm.api.util;

import java.util.Random;

public final class CredentialUtil {
    private static final int COUNT = 10;

    /**
     * 生成一个重置密码的随机数，作为激活密钥
     *
     * @return 生成的密钥
     */
    public static String generateActivationKey() {
        return randomNumeric();
    }

    /**
     * 生成一个重置密码的随机数，作为验证密钥
     *
     * @return 生成的密钥
     */
    public static String generateResetKey() {
        return randomNumeric();
    }

    private static String randomNumeric() {
        return String.valueOf(new Random().nextInt((9 * (int) Math.pow(10, CredentialUtil.COUNT - 1)) - 1)
                + (int) Math.pow(10, CredentialUtil.COUNT - 1));
    }
}
