package l2p.commons.crypt;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Scanner;

public class Start {

    /**
     * @param args
     * @throws IOException
     */
    public static void main(String[] args) throws IOException {
        //String command = "";
        //BufferedReader read = new BufferedReader(new InputStreamReader(System.in));

        System.out.println("Path to file:");
        String path = Scanning(System.in).next();

        System.out.println("User name:");
        String user = Scanning(System.in).next();

        System.out.println("Key:");
        int key = Integer.valueOf(Scanning(System.in).next());

        System.out.println("IP adress:");
        String ipadr = Scanning(System.in).next();

        System.out.println("Crypting file...");
        String newKey = key + ipadr + user;
        NewCrypt crypt = new NewCrypt(newKey.getBytes());

        File file = new File(path);
        if (!file.exists()) {
            System.out.println("File not found..");
            System.exit(0);
        }

        if (file.length() >= Integer.MAX_VALUE) {
            System.out.println("Big file!");
            System.exit(0);
        }

        byte[] data = new byte[(int) file.length()];

        data = crypt.crypt(data);

        File file2 = new File("start.bin");
        if (file2.exists()) {
            file2.createNewFile();
            FileOutputStream out = new FileOutputStream(file2);
            out.write(data);
            out.flush();
            out.close();

        }
    }

    private static Scanner Scanning(InputStream in) {
        return new Scanner(in);
    }
}