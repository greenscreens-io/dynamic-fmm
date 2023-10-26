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

    /**
     * Verify if last foreign function created an error
     *
     * @return Returns true if last foreign function resulted in error
     */
    boolean IsError();

    /**
     * Get last error description if exists
     *
     * @return last error description
     */
    String GetError();

    /**
     * Automatic pointer release for MemorySegments return values used for
     * automatic Java type conversion
     *
     * @param pointer
     */
    @GarbageCollector
    void FreePointer(final MemorySegment pointer);

    /**
     * Generate random bytes
     *
     * @param length Number of bytes to return
     * @return
     *
     * C/C++ requires size of a pointer. Annotation here define from which input
     * argument to take size of bytes in return value
     */
    @Size(index = 0)
    byte[] Random(final int length);

    /**
     * Calculate MD5 for provided byte array,
     *
     * @param data Data to calculate hash
     * @param length Length of data (required for C/C++ pointer)
     * @return Calculated hash
     *
     * C/C++ requires size of a pointer. Annotation here contains fixed byte
     * length returned, as we know that MD5 always return 16 bytes.
     */
    @Size(16)
    byte[] MD5(final byte[] data, final int length);

    @Size(20)
    byte[] Sha_1(final byte[] data, final int length);

    @Size(28)
    byte[] Sha_224(final byte[] data, final int length);

    @Size(32)
    byte[] Sha_256(final byte[] data, final int length);

    @Size(48)
    byte[] Sha_384(final byte[] data, final int length);

    @Size(64)
    byte[] Sha_512(final byte[] data, final int length);

    /**
     * Calculate HMAC SHA-1 hash
     *
     * @param data Data to calculate hash
     * @param length Length of data (required for C/C++ pointer)
     * @param key Always 20 bytes length, so we don't need to pass pointer byte
     * size
     * @return Calculated hash
     */
    @Size(16)
    byte[] Hmac_1_Sign(final byte[] data, final int length, final byte[] key);

    @Size(32)
    byte[] Hmac_256_Sign(final byte[] data, final int length, final byte[] key);

    @Size(48)
    byte[] Hmac_384_Sign(final byte[] data, final int length, final byte[] key);

    @Size(64)
    byte[] Hmac_512_Sign(final byte[] data, final int length, final byte[] key);

    /**
     * Calculate HMAC SHA-1 hash
     *
     * @param data Data to calculate hash
     * @param length Length of data (required for C/C++ pointer)
     * @param mac Always 20 bytes length, so we don't need to pass pointer byte
     * size
     * @param key Always 20 bytes length, so we don't need to pass pointer byte
     * size
     * @return Calculated hash
     */
    @Size(16)
    byte[] Hmac_1_Verify(final byte[] data, final int length, final byte[] mac, final byte[] key);

    @Size(32)
    byte[] Hmac_256_Verify(final byte[] data, final int length, final byte[] mac, final byte[] key);

    @Size(48)
    byte[] Hmac_384_Verify(final byte[] data, final int length, final byte[] mac, final byte[] key);

    @Size(64)
    byte[] Hmac_512_Verify(final byte[] data, final int length, final byte[] mac, final byte[] key);

    /**
     * Generate random key based on HKDF algorithm
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
    @Size(index = 6)
    byte[] HKDF_Generate_Key(final byte[] secret, final byte[] salt, final byte[] info, final int l1, final int l2, final int l3, final int size);

    /**
     * Generate random key based on PBKDF2 algorithm
     *
     * @param secret
     * @param salt
     * @param l1 length of secret argument
     * @param l2 length of salt argument
     * @param iter number of generative iterations
     * @param keyLen size of a key in bytes
     * @param hashLen Size in bytes (1, 28, 32, 48, 64, 65) or bits (20, 224,
     * 256, 384, 512, 521)
     * @return
     */
    @Size(index = 5)
    byte[] PBKDF2_Generate_Key(final byte[] secret, final byte[] salt, final int l1, final int l2, final int iter, final int keyLen, final int hashLen);

    /**
     * Generate RSA key
     *
     * @param size 1024, 2058, 4096
     * @param exponent recommended 65535
     * @return
     */
    String RSA_Generate_Key(final int size, final int exponent);

    /**
     * Check if RSA key exist in cache
     *
     * @param id - RSA key id
     * @param pub - check for public or private
     * @return
     */
    boolean RSA_Has_Key(final String id, final boolean pub);

    boolean RSA_Remove_Key(final String id, final boolean pub);

    String RSA_Export_Private_Key_Pem(final String id);

    String RSA_Export_Private_Key_Jwk(final String id);

    byte[] RSA_Export_Private_Key_Raw(final String id);

    String RSA_Export_Public_Key_Pem(final String id);

    String RSA_Export_Public_Key_Jwk(final String id);

    byte[] RSA_Export_Public_Key_Raw(final String id);

    /**
     * Decrypt RSA encrypted data
     *
     * @param id Cache id of a key used for signing the data
     * @param size Size of a hash algorithm in bytes (1, 28, 32, 48, 64) or bits
     * (20, 224, 256, 384, 512)
     * @param data Raw data to be encrypted
     * @param length Size of bytes in data argument to be encrypted
     * @return
     * @TODO Need to know size of data in advance (C/C++ pointer)
     */
    byte[] RSA_Decrypt(final String id, final int size, final byte[] data, final int length);

    byte[] RSA_Encrypt(final String id, final int size, final byte[] data, final int length);

    /**
     * Import JWK key from JSON format
     *
     * @param json JWK json as String
     * @return Cache ID of a key
     */
    String RSA_Import_Jwk(final String json);

    /**
     * Import Private RSA key
     *
     * @param data Raw bytes of a key
     * @param length Length of data argument
     * @return Cache ID of a key
     */
    String RSA_Import_Private_Key(final byte[] data, final int length);

    /**
     * Import Public RSA key
     *
     * @param data Raw bytes of a key
     * @param length Length of data argument
     * @return Cache ID of a key
     */
    String RSA_Import_Public_Key(final byte[] data, final int length);

    /**
     *
     * @param id Cache id of a key used for signing the data
     * @param raw Raw data to be signed (usually hash of a real data)
     * @param len Length of a raw argument (C standard)
     * @param size Size in bytes (1, 28, 32, 48, 64)
     * @return
     */
    @Size(index = 3)
    byte[] RSA_Sign_PKCS_1v15(final String id, final byte[] raw, final int len, final int size);

    /**
     *
     * @param id Cache id of a key used for signing the data
     * @param raw Data to be verified against signature
     * @param len Length of a raw argument
     * @param hashLength Size in bytes (1, 28, 32, 48, 64, 65) or bits (20, 224,
     * 256, 384, 512, 521)
     * @param saltLength Size of returning salt
     * @return
     */
    @Size(index = 4)
    byte[] RSA_Sign_PSS(final String id, final byte[] raw, final int len, final int hashLength, final int saltLength);

    /**
     *
     * @param id Cache id of a key used for signing the data
     * @param data Raw data to be verified against signature
     * @param signature Signature to be verified to generated data signature
     * @param l1 data arg length
     * @param l2 signature arg length
     * @param size Size in bytes (1, 28, 32, 48, 64)
     * @return
     */
    boolean RSA_Verify_PKCS_1v15(final String id, final byte[] data, final byte[] signature, final int l1, final int l2, final int size);

    /**
     *
     * @param id Cache id of a key used for signing the data
     * @param data Raw data to be verified against signature
     * @param signature Signature to be verified to generated data signature
     * @param l1 data arg length
     * @param l2 signature arg length
     * @param hashLength Size in bytes (1, 28, 32, 48, 64, 65) or bits (20, 224,
     * 256, 384, 512, 521)
     * @param saltLength Size of calculated salt
     * @return
     */
    boolean RSA_Verify_PSS(final String id, final byte[] data, final byte[] signature, final int l1, final int l2, final int hashLength, final int saltLength);

    /**
     * Generate ECDH key for encrypt/decrypt
     *
     * @param size Key size in bytes (28, 32, 48, 64, 65) or bits (224, 256,
     * 384, 512, 521)
     * @return Cache ID of a key
     */
    String ECDH_Generate_Key(final int size);

    /**
     * Generate ECDSA key for signing
     *
     * @param size Key size in bytes (28, 32, 48, 64, 65) or bits (224, 256,
     * 384, 512, 521)
     * @return Cache ID of a key
     */
    String ECDSA_Generate_Key(final int size);

    String ED25519_Generate_Key();

    String X25519_Generate_Key();
}
