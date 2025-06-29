package ru.dfhub.dfe.encryption;

import javax.crypto.Cipher;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.security.*;
import java.security.spec.EncodedKeySpec;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;

public class RSA {

    /**
     * Generate key pair and save it to files
     * @param keyPairName File base name (without extension)
     * @throws Exception File write error
     */
    public static void generateKeyPair(String keyPairName) throws Exception {
        KeyPairGenerator keygen = KeyPairGenerator.getInstance("RSA");
        keygen.initialize(4096);
        KeyPair pair = keygen.generateKeyPair();

        Files.write(Path.of("%s.dfe2k.public".formatted(keyPairName)), pair.getPublic().getEncoded());
        Files.write(Path.of("%s.dfe2k.private".formatted(keyPairName)), pair.getPrivate().getEncoded());
    }

    /**
     * Get public key from file
     * @param file File
     * @throws IOException File read error
     * @throws GeneralSecurityException Key is damaged or invalid
     */
    public static Key getPublicKey(File file) throws IOException, GeneralSecurityException {
        byte[] keyBytes = Files.readAllBytes(Path.of(file.getAbsolutePath()));

        KeyFactory kf = KeyFactory.getInstance("RSA");
        EncodedKeySpec keySpec = new X509EncodedKeySpec(keyBytes);
        return kf.generatePublic(keySpec);
    }

    /**
     * Get private key from file
     * @param file File
     * @throws IOException File read error
     * @throws GeneralSecurityException Key is damaged or invalid
     */
    public static Key getPrivateKey(File file) throws IOException, GeneralSecurityException {
        byte[] keyBytes = Files.readAllBytes(Path.of(file.getAbsolutePath()));

        KeyFactory kf = KeyFactory.getInstance("RSA");
        PKCS8EncodedKeySpec keySpec = new PKCS8EncodedKeySpec(keyBytes);
        return kf.generatePrivate(keySpec);
    }

    /**
     * Encrypt file
     * @param file File
     * @param publicKey Recipient's public key
     * @throws GeneralSecurityException Encryption error, should not arise at this stage
     * @throws IOException File read/write exceptions
     * @throws InvalidKeyException Invalid {@code key} argument. {@code RSA.getKey()} will not cause problems
     */
    public static void encrypt(File file, Key publicKey) throws GeneralSecurityException, InvalidKeyException, IOException {
        Cipher rsa = Cipher.getInstance("RSA");
        rsa.init(Cipher.ENCRYPT_MODE, publicKey);

        byte[] fileContents = Files.readAllBytes(
                Path.of(file.getAbsolutePath())
        );

        Files.write(
                Path.of(file.getAbsolutePath() + ".dfe2r"),
                rsa.doFinal(fileContents)
        );
    }

    /**
     *
     * @param file File
     * @param privateKey Private key, should be in same pair with public key used for {@code RSA.encrypt()}
     * @throws GeneralSecurityException File not encrypted or damaged
     * @throws IOException File read/write exceptions
     */
    public static void decrypt(File file, Key privateKey) throws GeneralSecurityException, IOException {
        Cipher rsa = Cipher.getInstance("RSA");
        rsa.init(Cipher.DECRYPT_MODE, privateKey);

        byte[] encryptedFileContents = Files.readAllBytes(Path.of(file.getAbsolutePath()));

        Files.write(
                Path.of(file.getAbsolutePath().substring(0, file.getAbsolutePath().length() - 5)),
                rsa.doFinal(encryptedFileContents)
        );
    }
}
