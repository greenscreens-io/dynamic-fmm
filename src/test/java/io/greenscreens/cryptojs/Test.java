/*
* Copyright (C) 2015, 2023 Green Screens Ltd.
*/
package io.greenscreens.cryptojs;

import java.lang.foreign.MemorySegment;

import io.greenscreens.foreign.ExternalFactory;
import io.greenscreens.util.ByteUtil;

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
    }
    
    static void test_random(final CryptoJS crypto ){
        
        /* makes memory leak, we have to call / register FreePointer
        final byte[] seg = crypto.Random(10);
        System.out.println(ByteUtil.bytesToHex(seg));
         */
        final MemorySegment seg = crypto.Random(10);
        System.out.println(crypto.IsError());
        System.out.println(crypto.GetError());
        //System.out.println(ByteUtil.bufferToHex(seg.reinterpret(10).asByteBuffer()));
        System.out.println(ByteUtil.bufferToHex(seg.asByteBuffer()));
        crypto.FreePointer(seg);
    }
    
    static void test_md5(final CryptoJS crypto ){        
        final MemorySegment ret = crypto.MD5(data.getBytes(), data.length());
        System.out.println(ByteUtil.bufferToHex(ret.asByteBuffer()));
        crypto.FreePointer(ret);
        
        final byte[] r = ByteUtil.digest("MD5", data.getBytes());
        System.out.println(ByteUtil.bytesToHex(r));
    }
    
    static void test_sha1(final CryptoJS crypto ){        
        final MemorySegment ret = crypto.Sha_1(data.getBytes(), data.length());
        System.out.println(ByteUtil.bufferToHex(ret.asByteBuffer()));
        crypto.FreePointer(ret);
        
        final byte[] r = ByteUtil.digest("SHA1", data.getBytes());
        System.out.println(ByteUtil.bytesToHex(r));
    }
    
}
