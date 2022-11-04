package cn.com.hdect.rfid.printer;

import ch.ntb.inf.libusb.Context;
import ch.ntb.inf.libusb.Device;
import ch.ntb.inf.libusb.exceptions.LibusbException;

import javax.usb.UsbDevice;
import java.nio.charset.StandardCharsets;

public class LibUsbJavaUtil {

    public static void main(String[] args) {
        init();
    }



    public static String init(){
        String tid = "";
        Context useCtx = null;
        Device usbDev = null;
        try {
            useCtx = new Context();                                                  /* 1 */
        } catch (LibusbException e) {
            System.out.println("Init failed:");
            e.printStackTrace();
        }

        System.out.println("Search Device:");
        try {
            usbDev = Device.search(useCtx, 0x08A6, 0xB002);                           /* 2 */

        } catch (LibusbException e) {
            System.out.println("Error occured: search");
            e.printStackTrace();
        }

        if(usbDev == null) return "";

        try {
            usbDev.open();                                                    /* 4 */
            usbDev.claimInterface(0);                                                /* 5 */
            String readCommand = "{WF;A000,T24,I4,U1|}";
            byte[] data = readCommand.getBytes(StandardCharsets.UTF_8);
            int res = usbDev.bulkTransfer(1, data, data.length, 0);                  /* 6 */
            if(res == data.length){
                System.out.println("Bulk tranfer 1 successful.");
                byte[] readData = new byte[50];
                int readResult = usbDev.readBulk(2, readData, readData.length, 0);     /* 3 */
                String s = DataConverter.bytesToHexString(readData);
                if(s.length() > 35){
                    tid = s.substring(10,34);
                }
                System.out.println("-------readData-------:" + tid);
            }
            else{
                System.out.println("Bulk transfer 1 failed.");
            }
            usbDev.releaseInterface(0);                                              /* 7 */
            usbDev.close();                                                          /* 8 */
            System.out.println("Device closed.");
        } catch (LibusbException e) {
            System.out.println("Error occured: transfer");
            e.printStackTrace();
        }
        return tid;
    }
}
