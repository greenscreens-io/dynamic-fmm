/*
* Copyright (C) 2015, 2023 Green Screens Ltd.
*/
package io.greenscreens.cryptojs;

import java.lang.foreign.MemorySegment;
import io.greenscreens.foreign.External;
import io.greenscreens.foreign.Size;

/**
 * https://github.com/greenscreens-io/cryptowasm.git
 */
@External(name = "libs/cryptojs")
public interface CryptoJS {

    boolean IsError();
    String GetError();
    void FreePointer(final MemorySegment pointer);
    
    // Cause memory leak, add "register pointer cleanup" 
    //@Size(index = 0) byte[] Random(final int length);
    @Size(index = 0) MemorySegment Random(final int length);
    
    @Size(16) MemorySegment MD5(final byte[] data, final int length);    
    @Size(20) MemorySegment Sha_1(final byte[] data, final int length);
    @Size(28) MemorySegment Sha_224(final byte[] data, final int length);
    @Size(32) MemorySegment Sha_256(final byte[] data, final int length);
    @Size(48) MemorySegment Sha_384(final byte[] data, final int length);
    @Size(64) MemorySegment Sha_512(final byte[] data, final int length);
    
    @Size(16) MemorySegment Hmac_1_Sign(final MemorySegment data, final int length, final MemorySegment key);
    @Size(32) MemorySegment Hmac_256_Sign(final MemorySegment data, final int length, final MemorySegment key);
    @Size(48) MemorySegment Hmac_384_Sign(final MemorySegment data, final int length, final MemorySegment key);
    @Size(64) MemorySegment Hmac_512_Sign(final MemorySegment data, final int length, final MemorySegment key);
    
    @Size(16) MemorySegment Hmac_1_Verify(final MemorySegment data, final int length, final MemorySegment mac, final MemorySegment key);
    @Size(32) MemorySegment Hmac_256_Verify(final MemorySegment data, final int length, final MemorySegment mac, final MemorySegment key);
    @Size(48) MemorySegment Hmac_384_Verify(final MemorySegment data, final int length, final MemorySegment mac, final MemorySegment key);
    @Size(64) MemorySegment Hmac_512_Verify(final MemorySegment data, final int length, final MemorySegment mac, final MemorySegment key);
}
