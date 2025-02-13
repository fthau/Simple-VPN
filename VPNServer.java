import java.io.*;
import java.net.*;
import javax.crypto.*;
import javax.crypto.spec.*;
import java.security.*;

public class VPNServer {
    
    private static final String AES_KEY = "AESEncryptionKey"; // 16-character key for AES encryption

    public static void main(String[] args) {
        int portNumber = 12345; // Choose a port number for the server
        ServerSocket serverSocket = null;
        Socket clientSocket = null;
        DataInputStream in = null;
        DataOutputStream out = null;

        try {
            serverSocket = new ServerSocket(portNumber);
            System.out.println("Server waiting for client on port " + portNumber);

            clientSocket = serverSocket.accept();
            System.out.println("Client connected.");

            in = new DataInputStream(clientSocket.getInputStream());
            out = new DataOutputStream(clientSocket.getOutputStream());

            // Simulating tunneling
            while (true) {
                int length = in.readInt();
                byte[] encryptedMessage = new byte[length];
                in.readFully(encryptedMessage);

                // Decrypt message
                String decryptedMessage = decrypt(encryptedMessage, AES_KEY);

                System.out.println("Client: " + decryptedMessage);

                // Echo back to client
                String response = "Message received: " + decryptedMessage;
                byte[] encryptedResponse = encrypt(response, AES_KEY);
                out.writeInt(encryptedResponse.length);
                out.write(encryptedResponse);
                out.flush();
            }
            
        } catch (IOException e) {
            System.err.println("Error in server: " + e.getMessage());
            
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
                if (out != null) out.close();
                if (in != null) in.close();
                if (clientSocket != null) clientSocket.close();
                if (serverSocket != null) serverSocket.close();
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
