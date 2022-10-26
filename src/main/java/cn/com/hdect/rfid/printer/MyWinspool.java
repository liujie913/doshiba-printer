package cn.com.hdect.rfid.printer;

import com.sun.jna.Native;
import com.sun.jna.platform.win32.WinNT;
import com.sun.jna.platform.win32.Winspool;
import com.sun.jna.ptr.IntByReference;
import com.sun.jna.win32.W32APIOptions;

public interface MyWinspool extends Winspool {

    MyWinspool INSTANCE = Native.load("Winspool.drv", MyWinspool.class, W32APIOptions.DEFAULT_OPTIONS);

    boolean StartDocPrinter(WinNT.HANDLE hPrinter, int level, Winspool.JOB_INFO_1 doc);

    boolean StartPagePrinter(WinNT.HANDLE hPrinter);

    boolean EndPagePrinter(WinNT.HANDLE hPrinter);

    boolean EndDocPrinter(WinNT.HANDLE hPrinter);

    boolean WritePrinter(WinNT.HANDLE hPrinter, byte[] pBytes, int dwCount, byte[] result);

}
