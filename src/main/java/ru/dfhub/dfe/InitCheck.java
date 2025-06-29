package ru.dfhub.dfe;

import java.io.File;

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

    public static File requestAesFile(MODE mode) throws Exception {
        System.out.print(mode == MODE.ENCRYPT ?
                "File path: " :
                ".dfe file path: "
        );

        File file = new File(Main.SCANNER.nextLine());
        if (!file.exists()) throw new Exception("File not exists!");

        if (mode == MODE.DECRYPT && !file.getName().endsWith(".dfe")) throw new Exception("Not a .dfe file!");
        return file;
    }

    public static String requestAesPassword() throws Exception {
        System.out.print("Password: ");

        String password = Main.SCANNER.nextLine();
        if (password.length() < 8) throw new Exception("Password is too short! Password length should be >=8");
        return password;
    }
}
