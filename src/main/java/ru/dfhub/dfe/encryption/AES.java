package ru.dfhub.dfe.encryption;

import org.bouncycastle.crypto.generators.Argon2BytesGenerator;
import org.bouncycastle.crypto.params.Argon2Parameters;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.spec.SecretKeySpec;
import java.io.*;
import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.security.GeneralSecurityException;
import java.security.InvalidKeyException;
import java.security.Key;
import java.security.NoSuchAlgorithmException;

public class AES {

    /**
     * Get encryption key from password and salt
     * @param password Password
     * @param salt Salt, random bytes
     * @return AES encryption key
     */
    public static Key getKey(String password, byte[] salt) {
        Argon2Parameters argonParams = new Argon2Parameters.Builder(Argon2Parameters.ARGON2_id)
                .withSalt(salt)
                .withMemoryAsKB(65536)
                .withIterations(10)
                .withParallelism(1)
                .build();

        Argon2BytesGenerator argon2 = new Argon2BytesGenerator();
        argon2.init(argonParams);

        byte[] hash = new byte[16];
        argon2.generateBytes(password.getBytes(StandardCharsets.UTF_8), hash, 0, hash.length);

        return new SecretKeySpec(hash, "AES");
    }

    /**
     * Get encryption key from password and encrypted file (file contains salt)
     * @param password Password
     * @param encryptedFile Encrypted file
     * @return AES Encryption key
     */
    public static Key getKey(String password, File encryptedFile) throws Exception {
        byte[] salt = new byte[16];
        try (FileInputStream fis = new FileInputStream(encryptedFile)) {
            fis.read(salt);
        }

        return getKey(password, salt);
    }

    /**
     * Encrypt file
     * @param file File
     * @param key Encryption key from {@code AES.getKey(password, salt)}
     * @param salt Salt. Same with salt used in {@code AES.getKey()}
     * @throws GeneralSecurityException Encryption error, should not arise at this stage
     * @throws IOException File read/write exceptions
     * @throws InvalidKeyException Invalid {@code key} argument. {@code AES.getKey()} will not cause problems
     */
    public static void encrypt(File file, Key key, byte[] salt) throws GeneralSecurityException, InvalidKeyException, IOException {
        Cipher aes = Cipher.getInstance("AES");
        aes.init(Cipher.ENCRYPT_MODE, key);

        byte[] fileContents = Files.readAllBytes(
                Path.of(file.getAbsolutePath())
        );
        byte[] encryptedResult = aes.doFinal(fileContents);

        Files.write(Path.of(file.getAbsolutePath() + ".dfe2"), ByteBuffer.allocate(salt.length + encryptedResult.length)
                .put(salt)
                .put(encryptedResult)
                .array()
        );
    }

    /**
     * Decrypt encrypted file
     * @param file File with {@code .dfe2} extension
     * @param key Encryption key
     * @throws GeneralSecurityException File not encrypted or damaged
     * @throws IOException File read/write exceptions
     */
    public static void decrypt(File file, Key key) throws GeneralSecurityException, IOException {
        Cipher aes = Cipher.getInstance("AES");
        aes.init(Cipher.DECRYPT_MODE, key);

        byte[] encryptedFileContent;
        try (InputStream in = new FileInputStream(file)) {
            in.skip(16); // Salt
            encryptedFileContent = in.readAllBytes();
        }
        byte[] fileContents = aes.doFinal(encryptedFileContent);

        Files.write(
                Path.of(file.getAbsolutePath().substring(0, file.getAbsolutePath().length() - 4)),
                fileContents
        );
    }
}
