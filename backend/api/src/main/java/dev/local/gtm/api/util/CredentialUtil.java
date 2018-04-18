package dev.local.gtm.api.util;

import java.util.Random;

public class CredentialUtil {
    private static final int COUNT = 20;

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
        Random rnd = new Random();
        int n = 10^(CredentialUtil.COUNT -1) + rnd.nextInt(9*10^(CredentialUtil.COUNT -1));
        return String.valueOf(n);
    }
}
