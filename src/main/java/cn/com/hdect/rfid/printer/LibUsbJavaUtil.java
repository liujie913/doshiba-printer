package cn.com.hdect.rfid.printer;

import ch.ntb.inf.libusb.Context;
import ch.ntb.inf.libusb.Device;
import ch.ntb.inf.libusb.exceptions.LibusbException;

import java.nio.charset.StandardCharsets;

public class LibUsbJavaUtil {

    private static Device device = null;
    private static int VID = 0x08A6;
    private static int PID = 0xB002;
    private static String feedLength = "0180";

    public static String convertHexToASC(String hex){
        StringBuilder sb = new StringBuilder();
        StringBuilder temp = new StringBuilder();
        for( int i=0; i<hex.length()-1; i+=2 ){
            String output = hex.substring(i, (i + 2));
            int decimal = Integer.parseInt(output, 16);
            sb.append((char)decimal);
            temp.append(decimal);
        }
        return sb.toString();
    }


    public static void main(String[] args) throws InterruptedException {
        //init();
        for(int i = 0; i< 1; i++) {
            if (findDevice()) {
                openDevice();
                commandBackFeed(feedLength);
//                commandStatus();
                Thread.sleep(600);
                String tid = "";
                tid = commandRead();
                System.out.println("------tid-----" + tid);
                Thread.sleep(600);

//                if(!tid.startsWith("E280")){
//                    commandBeforeFeed(feedLength);
//                    Thread.sleep(500);
//                    System.out.println("======打印失败=======");
//                    return;
//                }
//                boolean writeStatus = false;
//                for(int j = 1; j < 3; j++){
//                    System.out.println("---writeTime----" + j);
//                    writeStatus = commandWriteAndLock("11559933");
//                    Thread.sleep(300);
//                    if(writeStatus){
//                        break;
//                    }
//                }
//                if(!writeStatus){
//                    commandBeforeFeed(feedLength);
//                    Thread.sleep(500);
//                    System.out.println("======写入失败=======");
//                    return;
//                }
                commandBeforeFeed(feedLength);
                Thread.sleep(600);
//                commandStyle();
                commandPrint();
                Thread.sleep(600);
//                commandWriteAndLock("11225599");
                closeDevice();
            }
        }
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

    public static boolean findDevice(){
        if(device != null && device.isOpen()){
            return true;
        }
        Context useCtx = null;
        try {
            useCtx = new Context();
        } catch (LibusbException e) {
            System.out.println("Init failed:");
            e.printStackTrace();
        }

        System.out.println("Search Device:");
        try {
            device = Device.search(useCtx, VID, PID);
            if(device == null) {
                return false;
            }
            device.open();
        } catch (LibusbException e) {
            System.out.println("Error occured: search");
            e.printStackTrace();
        }
        return true;
    }

    public static void openDevice(){
        try {
            //device.open();
            device.claimInterface(0);
        } catch (LibusbException e) {
            System.out.println("Error occured: openDevice");
            e.printStackTrace();
        }
    }

    public static void closeDevice(){
        try {
            device.releaseInterface(0);
            //device.close();
        } catch (LibusbException e) {
            System.out.println("Error occured: closeDevice");
            e.printStackTrace();
        }
    }

    public static void commandStatus(){
        try {
            String command = "{WS|}";
            byte[] data = command.getBytes(StandardCharsets.UTF_8);
            int res = device.bulkTransfer(1, data, data.length, 0);
            if(res == data.length){
                System.out.println("-----状态成功");
                byte[] readData = new byte[50];
                int readResult = device.readBulk(2, readData, readData.length, 0);
                String s = DataConverter.bytesToHexString(readData);
                System.out.println(s);
            }
            else{
                System.out.println("-----状态失败");
            }
        } catch (LibusbException e) {
            System.out.println("Error occured: commandStatus");
            e.printStackTrace();
        }
    }

    public static void commandBackFeed(String feedLength){
        try {
            String command = "{U2;"+feedLength+"|}";
            byte[] data = command.getBytes(StandardCharsets.UTF_8);
            int res = device.bulkTransfer(1, data, data.length, 0);
            if(res == data.length){
                System.out.println("-----向后进纸成功");
            }
            else{
                System.out.println("-----向后进纸失败");
            }
        } catch (LibusbException e) {
            System.out.println("Error occured: commandBackFeed");
            e.printStackTrace();
        }
    }

    public static String commandRead(){
        String tid = "";
        try {
            String command = "{WF;A000,T24,I4,U1|}";
            byte[] data = command.getBytes(StandardCharsets.UTF_8);
            int res = device.bulkTransfer(1, data, data.length, 0);
            if(res == data.length){
                byte[] readData = new byte[50];
                int readResult = device.readBulk(2, readData, readData.length, 0);     /* 3 */
                String s = DataConverter.bytesToHexString(readData);
                if(s.length() > 35){
                    tid = s.substring(10,34);
                }
            }
            if(tid.length() != 24){
                System.out.println("tid 读取出错" + tid);
            }
        } catch (LibusbException e) {
            System.out.println("Error occured: commandRead");
            e.printStackTrace();
        }
        return tid;
    }

    public static boolean commandWriteAndLock(String tid){
        try {
            String command = "{@012;w,T24,G2,R10100000,L00300,B01=" + tid + "|}";
            byte[] data = command.getBytes(StandardCharsets.UTF_8);
            int res = device.bulkTransfer(1, data, data.length, 0);
            if(res == data.length){
                byte[] readData = new byte[50];
                device.readBulk(2, readData, readData.length, 0);
                String s = DataConverter.bytesToHexString(readData);
                System.out.println(s);
                if("3633".equals(s.substring(4,8))){
                    System.out.println("-----写入成功");
                    return true;
                }else {
                    System.out.println("-----写入失败");
                }
            }
            else{
                System.out.println("-----写入失败");
            }
        } catch (LibusbException e) {
            System.out.println("Error occured: commandWriteAndLock");
            e.printStackTrace();
        }
        return false;
    }

    public static void commandBeforeFeed(String feedLength){
        try {
            String command = "{U1;"+feedLength+"|}";
            byte[] data = command.getBytes(StandardCharsets.UTF_8);
            int res = device.bulkTransfer(1, data, data.length, 0);
            if(res == data.length){
                System.out.println("-----向前进纸成功");
            }
            else{
                System.out.println("-----向前进纸失败");
            }
        } catch (LibusbException e) {
            System.out.println("Error occured: commandBackFeed");
            e.printStackTrace();
        }
    }

    public static void commandStyle(){
        try {
            String command = "{D0321,0735,0301|}" +
                " {AY;+01,0|}" +
                " {C|}" +
                " {PC000;0041,0074,15,15,r,00,B=资产编码:YSGZ-10000-YWJS20113-YJ285|}" +
                " {PC001;0041,0147,15,15,r,00,B=资产名称:特定流量服务器（固网）|}" +
                " {XB00;0120,0280,C,3,05,0,0128,+0000000000,000,1,00=a8552122|}";
            byte[] data = command.getBytes(StandardCharsets.UTF_8);
            int res = device.bulkTransfer(1, data, data.length, 0);
            if(res == data.length){
                System.out.println("-----样式设置成功");
            }
            else{
                System.out.println("-----样式设置失败");
            }
        } catch (LibusbException e) {
            System.out.println("Error occured: commandStyle");
            e.printStackTrace();
        }
    }

    public static void commandPrint(){
        try {
            String command = "{XS;I,0001,0002C4201,S01|}";
            byte[] data = command.getBytes(StandardCharsets.UTF_8);
            int res = device.bulkTransfer(1, data, data.length, 0);
            if(res == data.length){
                System.out.println("-----打印成功");
            }
            else{
                System.out.println("-----打印失败");
            }
        } catch (LibusbException e) {
            System.out.println("Error occured: commandPrint");
            e.printStackTrace();
        }
    }
}
