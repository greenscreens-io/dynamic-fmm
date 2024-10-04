/*
* Copyright (C) 2015, 2024 Green Screens Ltd.
*/
package io.greenscreens.cryptojs;

import java.lang.foreign.MemorySegment;

import io.greenscreens.foreign.ExternalFactory;
//import io.greenscreens.util.ByteUtil;

/**
 * Test Green Screens CryptoWasm GO library compiled as Windows dll. 
 */
public enum TestLowLevel {
;
    final static String data = "The quick brown fox jumps over the lazy dog";
    
    public static void main(String[] args) {

        final CryptoJSLowLevel crypto = ExternalFactory.create(CryptoJSLowLevel.class);
        test_random(crypto);
        test_md5(crypto);
        test_sha1(crypto);
        test_rsa(crypto);
    }
    
    static String getAndPrintError(final CryptoJSLowLevel crypto) {
        if (!crypto.IsError()) return "";
        final String error = getString(crypto, crypto.GetError());
        System.out.println(error);
        return error;
    }
    
    
    static String getString(final CryptoJSLowLevel crypto, final MemorySegment seg) {
        final String id = seg.reinterpret(Integer.MAX_VALUE).getUtf8String(0);
        crypto.FreePointer(seg);
        return id;
    }
    
    static void test_rsa(final CryptoJSLowLevel crypto) {
        
        final MemorySegment _id = crypto.RSA_Generate_Key(1024, 65535);
        final String id = _id.reinterpret(Integer.MAX_VALUE).getUtf8String(0);
        crypto.FreePointer(_id);

        getAndPrintError(crypto);
        
        System.out.println(id);
        final boolean ok = crypto.RSA_Has_Key(id, true);
        System.out.println(ok);
        
        MemorySegment ret = null;
        ret = crypto.RSA_Export_Private_Key_Jwk(id);
        System.out.println(getString(crypto, ret));
        
        ret = crypto.RSA_Export_Private_Key_Pem(id);
        System.out.println(getString(crypto, ret));
        
        ret = crypto.RSA_Export_Public_Key_Pem(id);
        System.out.println(getString(crypto, ret));
        
        ret = crypto.RSA_Export_Public_Key_Pem(id);
        System.out.println(getString(crypto, ret));
        
    }
    
    static void test_random(final CryptoJSLowLevel crypto ){       
        
        final MemorySegment seg = crypto.Random(10);

        getAndPrintError(crypto);
        
        // use only of @Size not defined
        //System.out.println(ByteUtil.bufferToHex(seg.reinterpret(10).asByteBuffer()));
        //System.out.println(ByteUtil.bufferToHex(seg.asByteBuffer()));
        crypto.FreePointer(seg);
    }
    
    static void test_md5(final CryptoJSLowLevel crypto ) {        
        final MemorySegment ret = crypto.MD5(data.getBytes(), data.length());
        //System.out.println(ByteUtil.bufferToHex(ret.asByteBuffer()));
        crypto.FreePointer(ret);
        
        //final byte[] r = ByteUtil.digest("MD5", data.getBytes());
        //System.out.println(ByteUtil.bytesToHex(r));
    }
    
    static void test_sha1(final CryptoJSLowLevel crypto ){        
        final MemorySegment ret = crypto.Sha_1(data.getBytes(), data.length());
        //System.out.println(ByteUtil.bufferToHex(ret.asByteBuffer()));
        crypto.FreePointer(ret);
        
        //final byte[] r = ByteUtil.digest("SHA1", data.getBytes());
        //System.out.println(ByteUtil.bytesToHex(r));
    }
    
}
