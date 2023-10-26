/*
* Copyright (C) 2015, 2023 Green Screens Ltd.
*/
package io.greenscreens.cryptojs;

import io.greenscreens.foreign.ExternalFactory;
// import io.greenscreens.util.ByteUtil;

/**
 * Test Green Screens CryptoWasm GO library compiled as Windows dll. 
 */
public enum Test {
;
    final static String data = "The quick brown fox jumps over the lazy dog";
    
    public static void main(String[] args) {

        final CryptoJS crypto = ExternalFactory.create(CryptoJS.class);
        test_random(crypto);
        test_md5(crypto);
        test_sha1(crypto);
        test_rsa(crypto);
    }
    
    static void test_rsa(final CryptoJS crypto) {
        final String id = crypto.RSA_Generate_Key(1024, 65535);
        System.out.println(crypto.IsError());
        System.out.println(crypto.GetError());
        System.out.println(id);
        final boolean ok = crypto.RSA_Has_Key(id, true);
        System.out.println(ok);
        System.out.println(crypto.RSA_Export_Private_Key_Jwk(id));
        System.out.println(crypto.RSA_Export_Private_Key_Pem(id));
        System.out.println(crypto.RSA_Export_Public_Key_Pem(id));
        System.out.println(crypto.RSA_Export_Public_Key_Pem(id));
        
    }
    
    static void test_random(final CryptoJS crypto ){
        final byte[] seg = crypto.Random(10);
        //System.out.println(ByteUtil.bytesToHex(seg));
    }
    
    static void test_md5(final CryptoJS crypto ){        
        final byte[] ret = crypto.MD5(data.getBytes(), data.length());
        //System.out.println(ByteUtil.bytesToHex(ret));
        
        //final byte[] r = ByteUtil.digest("MD5", data.getBytes());
        //System.out.println(ByteUtil.bytesToHex(r));
    }
    
    static void test_sha1(final CryptoJS crypto ){        
        final byte[] ret = crypto.Sha_1(data.getBytes(), data.length());
        //System.out.println(ByteUtil.bytesToHex(ret));
        
        //final byte[] r = ByteUtil.digest("SHA1", data.getBytes());
        //System.out.println(ByteUtil.bytesToHex(r));
    }
    
}
