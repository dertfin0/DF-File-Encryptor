package ru.dfhub.dfe;

import ru.dfhub.dfe.encryption.AES;
import ru.dfhub.dfe.encryption.RSA;

import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.security.*;
import java.util.Scanner;

public class Main {

    public static final Scanner SCANNER = new Scanner(System.in);
    private static final String VERSION = "2.0";

    public static void main(String[] args) {
        System.out.printf("""
        Welcome to DF File Encryptor!
        Version: %s
        
        Select mode:
        1. AES
        2. RSA
        """, VERSION);

        System.out.print(">> ");
        try {
            switch (SCANNER.nextLine()) {
                case "1" -> aes();
                case "2" ->  rsa();
                default -> throw new IllegalArgumentException("Invalid mode!");
            }
        } catch (Exception e) {
            System.out.println("Selected mode not found!");
        }
    }

    private static void aes() {
        InitCheck.MODE mode;
        File file;
        String password;

        try {
            mode = InitCheck.requestAesMode();
            file = InitCheck.requestAesFile(mode);
            password = InitCheck.requestAesPassword();
        } catch (Exception e) {
            System.out.println(e.getMessage());
            return;
        }

        switch (mode) {
            case ENCRYPT -> {
                byte[] salt = new byte[16];
                new SecureRandom().nextBytes(salt);

                try {
                    AES.encrypt(
                            file,
                            AES.getKey(password, salt),
                            salt
                    );
                } catch (GeneralSecurityException e) {
                    System.out.println("An unknown error has occurred!");
                } catch (IOException e) {
                    System.out.println("An error occurred reading/writing file: ".concat(e.getMessage()));
                }
            }
            case DECRYPT -> {
                try {
                    AES.decrypt(file, AES.getKey(password, file));
                } catch (IllegalBlockSizeException e) {
                    System.out.println("File is damaged or invalid!");
                } catch (IOException e) {
                    System.out.println("An error occurred reading/writing file: ".concat(e.getMessage()));
                } catch (NoSuchAlgorithmException | NoSuchPaddingException e) {
                    System.out.println("An unknown error has occurred!");
                } catch (KeyException | BadPaddingException e) {
                    System.out.println("Password is not correct!");
                }
            }
        }
    }

    private static void rsa() {
        InitCheck.MODE mode;
        File file;
        Key key;

        try {
            mode = InitCheck.requestRsaMode();

            if (mode == InitCheck.MODE.RSA_GENERATE_KEY_PAIR) {
                System.out.print("Name of new key-pair: ");
                String pairName = SCANNER.nextLine();
                try {
                    RSA.generateKeyPair(pairName);
                    System.out.println("Generated new key pair: %name%.dfe2k.public, %name%.dfe2k.private");
                    return;
                } catch (Exception e) {
                    System.out.println("Error occurred writing file: ".concat(e.getMessage()));
                    return;
                }
            }
        } catch (Exception e) {
            System.out.println("Selected mode not found!");
            return;
        }

        try {
            file = InitCheck.requestRsaFile(mode);
        } catch (Exception e) {
            System.out.println(e.getMessage());
            return;
        }

        try {
            key = InitCheck.requestRsaKey(mode);
        } catch (IOException e) {
            System.out.println(e.getMessage());
            return;
        } catch (GeneralSecurityException e) {
            System.out.println("Encryption key is invalid! Generate key pair using RSA > Generate key pair");
            return;
        }

        switch (mode) {
            case ENCRYPT -> {
                try {
                    RSA.encrypt(file, key);
                } catch (GeneralSecurityException e) {
                    System.out.println("An unknown error has occurred!");
                } catch (IOException e) {
                    System.out.println("An error occurred reading/writing file: ".concat(e.getMessage()));
                }
            }
            case DECRYPT -> {
                try {
                    RSA.decrypt(file, key);
                } catch (IllegalBlockSizeException e) {
                    System.out.println("File is damaged or invalid!");
                } catch (IOException e) {
                    System.out.println("An error occurred reading/writing file: ".concat(e.getMessage()));
                } catch (NoSuchAlgorithmException | NoSuchPaddingException e) {
                    System.out.println("An unknown error has occurred!");
                } catch (KeyException | BadPaddingException e) {
                    System.out.println("Password is not correct!");
                }
            }
        }
    }


}
