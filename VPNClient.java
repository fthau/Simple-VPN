import java.io.*;
import java.net.*;
import javax.crypto.*;
import javax.crypto.spec.*;
import java.security.*;

public class VPNClient {

    private static final String AES_KEY = "AESEncryptionKey"; // 16-character key for AES encryption

    public static void main(String[] args) {
        String serverHostname = "127.0.0.1"; // Change this to the server IP address or hostname
        int portNumber = 12345; // Match the server's port number
        Socket vpnSocket = null;
        DataInputStream in = null;
        DataOutputStream out = null;
        BufferedReader stdIn = null;

        try {
            vpnSocket = new Socket(serverHostname, portNumber);
            System.out.println("Connected to server.");

            in = new DataInputStream(vpnSocket.getInputStream());
            out = new DataOutputStream(vpnSocket.getOutputStream());
            stdIn = new BufferedReader(new InputStreamReader(System.in));

            // Simulating tunneling
            while (true) {
                System.out.print("Enter message: ");
                String userInput = stdIn.readLine();

                // Encrypt message
                byte[] encryptedMessage = encrypt(userInput, AES_KEY);
                out.writeInt(encryptedMessage.length);
                out.write(encryptedMessage);
                out.flush();

                // Receive response
                int length = in.readInt();
                byte[] encryptedResponse = new byte[length];
                in.readFully(encryptedResponse);

                // Decrypt and display response
                String decryptedResponse = decrypt(encryptedResponse, AES_KEY);
                System.out.println("Server: " + decryptedResponse);
            }

        } catch (UnknownHostException e) {
            System.err.println("Unknown host: " + serverHostname);

        } catch (IOException e) {
            System.err.println("Error in client: " + e.getMessage());

        } catch (NoSuchAlgorithmException e) {
            System.err.println("Encryption algorithm not available: " + e.getMessage());

        } catch (NoSuchPaddingException e) {
            System.err.println("Padding scheme not available: " + e.getMessage());

        } catch (InvalidKeyException e) {
            System.err.println("Invalid encryption key: " + e.getMessage());

        } catch (IllegalBlockSizeException e) {
            System.err.println("Illegal block size in encryption: " + e.getMessage());

        } catch (BadPaddingException e) {
            System.err.println("Bad padding in encryption: " + e.getMessage());

        } finally {
            try {
                if (stdIn != null) stdIn.close();
                if (out != null) out.close();
                if (in != null) in.close();
                if (vpnSocket != null) vpnSocket.close();
            } catch (IOException e) {
                System.err.println("Error closing streams/sockets: " + e.getMessage());
            }
        }
    }

    // Encryption method using AES
    private static byte[] encrypt(String message, String key) throws NoSuchAlgorithmException, NoSuchPaddingException, InvalidKeyException, IllegalBlockSizeException, BadPaddingException 
    {
        Cipher cipher = Cipher.getInstance("AES");
        SecretKeySpec secretKey = new SecretKeySpec(key.getBytes(), "AES");
        cipher.init(Cipher.ENCRYPT_MODE, secretKey);
        return cipher.doFinal(message.getBytes());
    }

    // Decryption method using AES
    private static String decrypt(byte[] encryptedMessage, String key) throws NoSuchAlgorithmException, NoSuchPaddingException, InvalidKeyException, IllegalBlockSizeException, BadPaddingException 
    {
        Cipher cipher = Cipher.getInstance("AES");
        SecretKeySpec secretKey = new SecretKeySpec(key.getBytes(), "AES");
        cipher.init(Cipher.DECRYPT_MODE, secretKey);
        byte[] decryptedBytes = cipher.doFinal(encryptedMessage);
        return new String(decryptedBytes);
    }
}
