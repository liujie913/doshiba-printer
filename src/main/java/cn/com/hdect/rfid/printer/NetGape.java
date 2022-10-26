package cn.com.hdect.rfid.printer;

import java.io.IOException;
import java.net.Socket;
import java.net.UnknownHostException;

public class NetGape {
    private final static String host = "192.168.2.56";
    private final static int port = 9100;

    public static void main(String[] args) {
        String command = "{XB00;0000,0000,f,I4|} {XS;I,0001,0003C6001|}";
        ClientMethod(host, port, command);
    }

    private static void ClientMethod(String host, int port, String sendStr)
    {
        try {
            Socket client = new Socket(host, port);
            client.getOutputStream().write(sendStr.getBytes());
            byte[] buffer = new byte[1024];
            int c = client.getInputStream().read(buffer);
            System.out.println(c);
            if (c > 0) {
                System.out.println(new String(buffer));
            }
            client.close();
        } catch (UnknownHostException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
