package org.fanhuang.cihangbrowser.utils;

import java.nio.charset.StandardCharsets;

import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;

/**
 * Created by fan on 2017/6/12.
 */

public class AESUtils {
    /**
     * 加密
     * @param seed   密码
     * @param cleartext   将被加密的数据
     * @return
     * @throws Exception
     */
        public static String encrypt(String seed, String cleartext) throws Exception {
            //获取秘钥对应的字节数组
            byte[] rawKey = deriveKeyInsecurely(seed,32).getEncoded();
            //获取加密后的字节数组
            byte[] result = encrypt(rawKey, cleartext.getBytes());

            return toHex(result);
        }

    /**
     * 解密
     * @param seed   密码
     * @param encrypted   将被解密的数据
     * @return
     * @throws Exception
     */
        public static String decrypt(String seed, String encrypted) throws Exception {
            //setcretkey.getEnceded(),获取秘钥对应的字节数组
            byte[] rawKey = deriveKeyInsecurely(seed,32).getEncoded();

            byte[] enc = toByte(encrypted);
//            byte[] enc=encrypted.getBytes();
            byte[] result = decrypt(rawKey, enc);

            return new String(result);
        }

    /**
     * 获得秘钥--secretkey对象
     * @param password
     * @param keySizeInBytes
     * @return
     */
        private static SecretKey deriveKeyInsecurely(String password, int
                keySizeInBytes) {
            //以US-ASCPII格式编码,StandardCharsets可避免try--catch,注意:getBytes()默认以utf-8编码
            byte[] passwordBytes = password.getBytes(StandardCharsets.UTF_8);
            //辅助类InsecureSHA1PRNGKeyDerivator,帮助派生秘钥secretkey
//            return new SecretKeySpec(passwordBytes,"AES");
            return new SecretKeySpec(InsecureSHA1PRNGKeyDerivator.deriveInsecureKey(passwordBytes, keySizeInBytes),"AES");
        }

    /**
     * 进行加密,返回加密后的字节数组
     * @param raw
     * @param clear
     * @return
     * @throws Exception
     */
        private static byte[] encrypt(byte[] raw, byte[] clear) throws Exception {
            //把秘钥secretkey对应的字节数组raw,转换成secretkey对象
            SecretKeySpec skeySpec = new SecretKeySpec(raw, "AES");
            //获取一个cipher对象,可加密,解密
            Cipher cipher = Cipher.getInstance("AES");
            //用指定的秘钥初始化cipher对象, 参数三:使用CBC模式，需要一个向量iv，可增加加密算法的强度
            cipher.init(Cipher.ENCRYPT_MODE, skeySpec, new IvParameterSpec(new byte[cipher.getBlockSize()]));
            //cipher进行最终的加密操作
            byte[] encrypted = cipher.doFinal(clear);
            return encrypted;
        }

        private static byte[] decrypt(byte[] raw, byte[] encrypted) throws Exception {

            SecretKeySpec skeySpec = new SecretKeySpec(raw, "AES");

            Cipher cipher = Cipher.getInstance("AES");

            cipher.init(Cipher.DECRYPT_MODE, skeySpec);
//            cipher.init(Cipher.DECRYPT_MODE, skeySpec, new IvParameterSpec(new byte[cipher.getBlockSize()]));

            byte[] decrypted = cipher.doFinal(encrypted);

            return decrypted;
        }

        private static String toHex(String txt) {
            return toHex(txt.getBytes());
        }

        private static String fromHex(String hex) {
            return new String(toByte(hex));
        }

        private static byte[] toByte(String hexString) {
            int len = hexString.length() / 2;
            byte[] result = new byte[len];
            for (int i = 0; i < len; i++)
                result[i] = Integer.valueOf(hexString.substring(2 * i, 2 * i + 2), 16).byteValue();
            return result;
        }

        private static String toHex(byte[] buf) {
            if (buf == null)
                return "";
            StringBuffer result = new StringBuffer(2 * buf.length);
            for (int i = 0; i < buf.length; i++) {
                appendHex(result, buf[i]);
            }
            return result.toString();
        }

        private final static String HEX = "0123456789ABCDEF";

        private static void appendHex(StringBuffer sb, byte b) {

            sb.append(HEX.charAt((b >> 4) & 0x0f)).append(HEX.charAt(b & 0x0f));
        }

}
