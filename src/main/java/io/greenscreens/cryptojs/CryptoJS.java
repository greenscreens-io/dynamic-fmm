/*
* Copyright (C) 2015, 2023 Green Screens Ltd.
*/
package io.greenscreens.cryptojs;

import java.lang.foreign.MemorySegment;

import io.greenscreens.foreign.annotations.External;
import io.greenscreens.foreign.annotations.GarbageCollector;
import io.greenscreens.foreign.annotations.Size;

/**
 * Example with automatic type conversion and pointer release
 */
@External(name = "libs/cryptojs")
public interface CryptoJS {

    boolean IsError();
    String GetError();
    
    @GarbageCollector
    void FreePointer(final MemorySegment pointer);
     
    @Size(index = 0) byte[] Random(final int length);
    
    @Size(16) byte[] MD5(final byte[] data, final int length);    
    @Size(20) byte[] Sha_1(final byte[] data, final int length);
    @Size(28) byte[] Sha_224(final byte[] data, final int length);
    @Size(32) byte[] Sha_256(final byte[] data, final int length);
    @Size(48) byte[] Sha_384(final byte[] data, final int length);
    @Size(64) byte[] Sha_512(final byte[] data, final int length);
    
    @Size(16) byte[] Hmac_1_Sign(final byte[] data, final int length, final byte[] key);
    @Size(32) byte[] Hmac_256_Sign(final byte[] data, final int length, final byte[] key);
    @Size(48) byte[] Hmac_384_Sign(final byte[] data, final int length, final byte[] key);
    @Size(64) byte[] Hmac_512_Sign(final byte[] data, final int length, final byte[] key);
    
    @Size(16) byte[] Hmac_1_Verify(final byte[] data, final int length, final byte[] mac, final byte[] key);
    @Size(32) byte[] Hmac_256_Verify(final byte[] data, final int length, final byte[] mac, final byte[] key);
    @Size(48) byte[] Hmac_384_Verify(final byte[] data, final int length, final byte[] mac, final byte[] key);
    @Size(64) byte[] Hmac_512_Verify(final byte[] data, final int length, final byte[] mac, final byte[] key);
    
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
    @Size(index = 6) byte[] HKDF_Generate_Key(final byte[] secret, final byte[] salt, final byte[] info, final int l1, final int l2, final int l3, final int size);
    
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
    @Size(index = 5) byte[] PBKDF2_Generate_Key(final byte[] secret, final byte[] salt, final int l1, final int l2, final int iter, final int keyLen, final int hashLen);
    
    String ECDH_Generate_Key(final int size);
    String ECDSA_Generate_Key(final int size);
    String ED25519_Generate_Key();
    String X25519_Generate_Key();

    
    String RSA_Generate_Key(final int size, final int exponent);
    
    /**
     * Check if RSA key exist in cache
     * @param id - RSA key id
     * @param pub - check for public or private 
     * @return
     */
    boolean RSA_Has_Key(final String id, final boolean pub);
    String RSA_Export_Private_Key_Pem(final String id);
    String RSA_Export_Private_Key_JWK(final String id);
    byte[] RSA_Export_Private_Key_Raw(final String id);
    
    String RSA_Export_Public_Key_Pem(final String id);
    String RSA_Export_Public_Key_Jwk(final String id);
    byte[] RSA_Export_Public_Key_Raw(final String id);

    byte[] RSA_Decrypt(final String id, final int size, final byte[] data, final int length);
    byte[] RSA_Encrypt(final String id, final int size, final byte[] data, final int length);
}
