package ru.dfhub.dfe;

import ru.dfhub.dfe.encryption.RSA;

import java.io.File;
import java.io.IOException;
import java.security.GeneralSecurityException;
import java.security.Key;

public class InitCheck {
    public enum MODE {
        ENCRYPT,
        DECRYPT,
        RSA_GENERATE_KEY_PAIR
    }

    public static MODE requestAesMode() {
        System.out.printf("""
        %n
        Select mode:
        1. Encrypt
        2. Decrypt
        """);
        System.out.print(">> ");
        switch (Main.SCANNER.nextLine()) {
            case "1" -> { return MODE.ENCRYPT; }
            case "2" ->  { return MODE.DECRYPT; }
            default -> throw new IllegalArgumentException("Selected mode not found!");
        }
    }

    public static MODE requestRsaMode() {
        System.out.printf("""
        %n
        Select mode:
        1. Encrypt
        2. Decrypt
        3. Generate key pair
        """);
        System.out.print(">> ");
        switch (Main.SCANNER.nextLine()) {
            case "1" -> { return MODE.ENCRYPT; }
            case "2" ->  { return MODE.DECRYPT; }
            case "3" -> { return MODE.RSA_GENERATE_KEY_PAIR; }
            default -> throw new IllegalArgumentException("Selected mode not found!");
        }
    }

    public static File requestAesFile(MODE mode) throws Exception {
        System.out.print(mode == MODE.ENCRYPT ?
                "File path: " :
                ".dfe2 file path: "
        );

        File file = new File(Main.SCANNER.nextLine());
        if (!file.exists()) throw new Exception("File not exists!");

        if (mode == MODE.DECRYPT && !file.getName().endsWith(".dfe2")) throw new Exception("Not a .dfe2 file!");
        return file;
    }

    public static File requestRsaFile(MODE mode) throws Exception {
        System.out.print(mode == MODE.ENCRYPT ?
                "File path: " :
                ".dfe2r file path: "
        );

        File file = new File(Main.SCANNER.nextLine());
        if (!file.exists()) throw new Exception("File not exists!");

        if (mode == MODE.DECRYPT && !file.getName().endsWith(".dfe2r")) throw new Exception("Not a .dfe2r file!");
        return file;
    }

    public static String requestAesPassword() throws Exception {
        System.out.print("Password: ");

        String password = Main.SCANNER.nextLine();
        if (password.length() < 8) throw new Exception("Password is too short! Password length should be >=8");
        return password;
    }

    public static Key requestRsaKey(MODE mode) throws IOException, GeneralSecurityException {
        System.out.print(mode == MODE.ENCRYPT ?
                "Recipient's public key: " :
                "Your private key: "
        );

        File file = new File(Main.SCANNER.nextLine());
        if (!file.exists()) throw new IllegalArgumentException("File not exists!");
        if (mode == MODE.ENCRYPT && !file.getName().endsWith(".dfe2k.public")) throw new IllegalArgumentException("Not .dfe2k.public file!");
        if (mode == MODE.DECRYPT && !file.getName().endsWith(".dfe2k.private")) throw new IllegalArgumentException("Not .dfe2k.private file!");

        switch (mode) {
            case ENCRYPT -> {
                return RSA.getPublicKey(file);
            }
            case DECRYPT -> {
                return RSA.getPrivateKey(file);
            }
            default -> {
                return null;
            }
        }
    }
}
