/*
* Copyright (C) 2015, 2023 Green Screens Ltd.
*/
package io.greenscreens.cryptojs;

import java.lang.foreign.MemorySegment;

import io.greenscreens.foreign.annotations.External;
import io.greenscreens.foreign.annotations.Size;

/**
 * Example for more complicated use cases when pointer release will be manual.
 * Return type i memorySegment (kind of pointer from foreign library) which we must release
 * to prevent memory leak.
 */

@External(name = "libs/cryptojs")
public interface CryptoJSLowLevel {

    boolean IsError();
    MemorySegment GetError();
    
    void FreePointer(final MemorySegment pointer);
     
    @Size(index = 0) MemorySegment Random(final int length);
    
    @Size(16) MemorySegment MD5(final byte[] data, final int length);    
    @Size(20) MemorySegment Sha_1(final byte[] data, final int length);
    @Size(28) MemorySegment Sha_224(final byte[] data, final int length);
    @Size(32) MemorySegment Sha_256(final byte[] data, final int length);
    @Size(48) MemorySegment Sha_384(final byte[] data, final int length);
    @Size(64) MemorySegment Sha_512(final byte[] data, final int length);
    
    @Size(16) MemorySegment Hmac_1_Sign(final byte[] data, final int length, final byte[] key);
    @Size(32) MemorySegment Hmac_256_Sign(final byte[] data, final int length, final byte[] key);
    @Size(48) MemorySegment Hmac_384_Sign(final byte[] data, final int length, final byte[] key);
    @Size(64) MemorySegment Hmac_512_Sign(final byte[] data, final int length, final byte[] key);
    
    @Size(16) MemorySegment Hmac_1_Verify(final byte[] data, final int length, final byte[] mac, final byte[] key);
    @Size(32) MemorySegment Hmac_256_Verify(final byte[] data, final int length, final byte[] mac, final byte[] key);
    @Size(48) MemorySegment Hmac_384_Verify(final byte[] data, final int length, final byte[] mac, final byte[] key);
    @Size(64) MemorySegment Hmac_512_Verify(final byte[] data, final int length, final byte[] mac, final byte[] key);
    
    /**
     * 
     * @param secret
     * @param salt
     * @param info
     * @param l1 length of secret argument
     * @param l2 length of salt argument
     * @param l3 length of info argument
     * @param size size of key to return
     * @return
     */
    @Size(index = 6) MemorySegment HKDF_Generate_Key(final byte[] secret, final byte[] salt, final byte[] info, final int l1, final int l2, final int l3, final int size);
    
    /**
     * 
     * @param secret
     * @param salt
     * @param l1 length of secret argument
     * @param l2 length of salt argument
     * @param iter number of generative iterations
     * @param keyLen size of a key 
     * @param hashLen hash algorithm size
     * @return
     */
    @Size(index = 5) MemorySegment PBKDF2_Generate_Key(final byte[] secret, final byte[] salt, final int l1, final int l2, final int iter, final int keyLen, final int hashLen);
    
    MemorySegment ECDH_Generate_Key(final int size);
    MemorySegment ECDSA_Generate_Key(final int size);
    MemorySegment ED25519_Generate_Key();
    MemorySegment X25519_Generate_Key();

    
    MemorySegment RSA_Generate_Key(final int size, final int exponent);
    
    /**
     * Check if RSA key exist in cache
     * @param id - RSA key id
     * @param pub - check for public or private 
     * @return
     */
    boolean RSA_Has_Key(final String id, final boolean pub);
    MemorySegment RSA_Export_Private_Key_Pem(final String id);
    MemorySegment RSA_Export_Private_Key_JWK(final String id);
    byte[] RSA_Export_Private_Key_Raw(final String id);
    
    MemorySegment RSA_Export_Public_Key_Pem(final String id);
    MemorySegment RSA_Export_Public_Key_Jwk(final String id);
    byte[] RSA_Export_Public_Key_Raw(final String id);
}
