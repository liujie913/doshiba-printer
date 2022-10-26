package cn.com.hdect.rfid.printer;

import com.sun.jna.platform.win32.WinNT;
import com.sun.jna.platform.win32.Winspool;
import com.sun.jna.ptr.IntByReference;
import java.nio.charset.StandardCharsets;

public class MyWinspoolUtil {

    public static void main(String[] args) {
        String name = "TOSHIBA BA400 (300 dpi)";
        WinNT.HANDLEByReference handle =  new WinNT.HANDLEByReference();
        boolean opened = MyWinspool.INSTANCE.OpenPrinter(name, handle, null);
        System.out.println(opened);

        Winspool.JOB_INFO_1 doc = new Winspool.JOB_INFO_1();
        doc.pDocument = "标签打印";
        doc.pDatatype = "RAW";
        try {
            boolean b1 = MyWinspool.INSTANCE.StartDocPrinter(handle.getValue(), 1,  doc);
            boolean b2 =MyWinspool.INSTANCE.StartPagePrinter(handle.getValue());
            IntByReference r = new IntByReference();

            String commandStatus = "{WS|}";
            String commandTag = "{D0331,0700,0300|}";
            String commandCleanIcon = "{C|}";
            String commandText1 = "{PC000;0020,0030,1,1,A,00,B=资产编码:YSGZ-10000-YWJS20113-YJ285|}";
            String commandText2 = "{PC000;0020,0120,1,1,A,00,B=资产名称:特定流量服务器(固网)|}";
            String commandText3 = "{PC000;0020,0210,1,1,A,00,B=资产类别:服务器|}";
            String commandPrint = "{XS;I,0001,0003C6001|}";
            byte[] result = new byte[1];

            MyWinspool.INSTANCE.WritePrinter(handle.getValue(), commandStatus.getBytes(StandardCharsets.UTF_8), commandStatus.length(), result);
            MyWinspool.INSTANCE.WritePrinter(handle.getValue(), commandTag.getBytes(StandardCharsets.UTF_8), commandTag.length(), result);
            MyWinspool.INSTANCE.WritePrinter(handle.getValue(), commandCleanIcon.getBytes(StandardCharsets.UTF_8), commandCleanIcon.length(), result);
            MyWinspool.INSTANCE.WritePrinter(handle.getValue(), commandText1.getBytes(StandardCharsets.UTF_8), commandText1.length(), result);
            MyWinspool.INSTANCE.WritePrinter(handle.getValue(), commandText2.getBytes(StandardCharsets.UTF_8), commandText2.length(), result);
            MyWinspool.INSTANCE.WritePrinter(handle.getValue(), commandText3.getBytes(StandardCharsets.UTF_8), commandText3.length(), result);
            MyWinspool.INSTANCE.WritePrinter(handle.getValue(), commandPrint.getBytes(StandardCharsets.UTF_8), commandPrint.length(), result);
            MyWinspool.INSTANCE.EndPagePrinter(handle.getValue());
            MyWinspool.INSTANCE.EndDocPrinter(handle.getValue());
            boolean closed = Winspool.INSTANCE.ClosePrinter(handle.getValue());
            System.out.println(closed);
        }catch (Exception e){
            System.out.println(e.getMessage());
        }
    }
}
