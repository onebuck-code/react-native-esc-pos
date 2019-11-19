package leesiongchan.reactnativeescpos;

import java.util.List;

import android.content.Context;
import android.hardware.usb.UsbDevice;
import android.hardware.usb.UsbConstants;
import android.hardware.usb.UsbDeviceConnection;
import android.hardware.usb.UsbEndpoint;
import android.hardware.usb.UsbInterface;
import android.hardware.usb.UsbManager;

import io.github.escposjava.print.Printer;
import java.io.ByteArrayOutputStream;

import java.io.IOException;
import java.io.OutputStream;

public class UsbPrinter implements Printer {

	private final UsbDeviceConnection mConnection;
	private final UsbInterface mInterface;
    private final UsbEndpoint mEndpoint;
    
    private static final int TRANSFER_TIMEOUT = 1000;

    private static int[] PRINTER_VID = {
		34918, 1659, 1137, 1155, 26728, 17224, 7358
	};

	private static final byte[] CD_PIN_2 = {0x1b,0x70,0x00, 0x00,0x00};
	private Context context;
	
    public UsbPrinter(Context context, UsbDevice device) throws IOException {
		UsbInterface iface = null;
		UsbEndpoint epout = null;
		
		for(int i=0; i<device.getInterfaceCount(); i++) {
			iface = device.getInterface(i);
			if (iface == null)
				throw new IOException("failed to get interface "+i);

			int epcount = iface.getEndpointCount();
			for (int j = 0; j < epcount; j++) {
				UsbEndpoint ep = iface.getEndpoint(j);
				if (ep.getDirection() == UsbConstants.USB_DIR_OUT) {
					epout = ep;
					break;
				}
			}
			
			if(epout != null)
				break;
		}

		if (epout == null) {
			throw new IOException("no output endpoint.");
		}

		//mDevice = device;
		mInterface = iface;
		mEndpoint = epout;

		UsbManager usbman = (UsbManager) context
				.getSystemService(Context.USB_SERVICE);
		mConnection = usbman.openDevice(device);

		if (mConnection == null) {
			throw new IOException("failed to open usb device.");
		}

		mConnection.claimInterface(mInterface, true);
	}

    public void open() throws IOException {
		
	}
	
	public void write(byte[] command) {
		mConnection.bulkTransfer(mEndpoint,command,command.length, TRANSFER_TIMEOUT);
	}

	public void close() {
		mConnection.releaseInterface(mInterface);
		mConnection.close();
	}
	
	public void feedLine(int count) throws IOException {
		StringBuilder sb = new StringBuilder();
		for(int i=0; i<count; i++)
			sb.append('\n');
		write(sb.toString().getBytes());
	}
	
	// public void printString(ByteArrayOutputStream string)
	// 		throws IOException {

    //     write(string.toByteArray());
	// }
	

    public void kickCashDrawerPin2() throws IOException{

        write(CD_PIN_2);
    }
    
    public void printEmptyLine(int numb) throws IOException{
        byte[] CTL_LF = {0x0a};
        for(int x = 0; x < numb; x ++ ){
            write(CTL_LF);
        } 
	}
	
	public void requestUsbPrinter(Context c) {
		UsbPrinterUtil u = new UsbPrinterUtil(c);
		List<UsbDevice> devs = u.findDevicesByVid(PRINTER_VID);
		for(UsbDevice d : devs) {
			u.requestPermission(d);
		}
	}
}





// public class UsbPrinter  {
    
    // //private final UsbDevice mDevice;
	// private final UsbDeviceConnection mConnection;
	// private final UsbInterface mInterface;
    // private final UsbEndpoint mEndpoint;
    
    // private static final int TRANSFER_TIMEOUT = 1000;

    // private static int[] PRINTER_VID = {
	// 	34918, 1659, 1137, 1155, 26728, 17224, 7358
    // };
    
    // private static final byte[] CD_PIN_2 = {0x1b,0x70,0x00, 0x00,0x00};

    // public UsbPrinter(Context context, UsbDevice device) throws IOException {
	// 	UsbInterface iface = null;
	// 	UsbEndpoint epout = null;
		
	// 	for(int i=0; i<device.getInterfaceCount(); i++) {
	// 		iface = device.getInterface(i);
	// 		if (iface == null)
	// 			throw new IOException("failed to get interface "+i);

	// 		int epcount = iface.getEndpointCount();
	// 		for (int j = 0; j < epcount; j++) {
	// 			UsbEndpoint ep = iface.getEndpoint(j);
	// 			if (ep.getDirection() == UsbConstants.USB_DIR_OUT) {
	// 				epout = ep;
	// 				break;
	// 			}
	// 		}
			
	// 		if(epout != null)
	// 			break;
	// 	}

	// 	if (epout == null) {
	// 		throw new IOException("no output endpoint.");
	// 	}

	// 	//mDevice = device;
	// 	mInterface = iface;
	// 	mEndpoint = epout;

	// 	UsbManager usbman = (UsbManager) context
	// 			.getSystemService(Context.USB_SERVICE);
	// 	mConnection = usbman.openDevice(device);

	// 	if (mConnection == null) {
	// 		throw new IOException("failed to open usb device.");
	// 	}

	// 	mConnection.claimInterface(mInterface, true);
	// }

    // public static  UsbPrinter open(Context c, int vid, int pid) throws IOException {
	// 	UsbPrinterUtil u = new UsbPrinterUtil(c);
	// 	//for(UsbDevice d : u.findDevicesByVid(PRINTER_VID)) {
	// 	List<UsbDevice> devs = u.findDevicesByVid(new int[] {vid});
	// 	for(UsbDevice d : devs) {
	// 		if(d.getVendorId() == vid) {
	// 			if(pid != 0) { // ignore pid
	// 				if(d.getProductId() == pid) {
	// 					return new UsbPrinter(c, d);
	// 				}
	// 			} else {
	// 				return new UsbPrinter(c, d);
	// 			}
	// 		}
	// 	}
	// 	return null;
	// }
	
	// public static UsbPrinter open(Context c) throws IOException {
	// 	UsbPrinterUtil u = new UsbPrinterUtil(c);
	// 	List<UsbDevice> devs = u.findDevicesByVid(PRINTER_VID);
	// 	if(devs.size() > 0) {
	// 		return new UsbPrinter(c, devs.get(0));
	// 	}
	// 	return null;

    // }
    
    // public static void requestUsbPrinter(Context c) {
	// 	UsbPrinterUtil u = new UsbPrinterUtil(c);
	// 	List<UsbDevice> devs = u.findDevicesByVid(PRINTER_VID);
	// 	for(UsbDevice d : devs) {
	// 		u.requestPermission(d);
	// 	}
	// }

    // public void write(byte[] command) throws IOException {
    //         mConnection.bulkTransfer(mEndpoint,command,command.length, TRANSFER_TIMEOUT);

    // }

//     public void close() {
// 		mConnection.releaseInterface(mInterface);
// 		mConnection.close();
//     }

// 	public void feedLine(int count) throws IOException {
// 		StringBuilder sb = new StringBuilder();
// 		for(int i=0; i<count; i++)
// 			sb.append('\n');
// 		write(sb.toString().getBytes());
// 	}
	
// 	public void printString(ByteArrayOutputStream string)
// 			throws IOException {

//         write(string.toByteArray());
//     }
    
//     public void kickCashDrawerPin2() throws IOException{

//         write(CD_PIN_2);
//     }
    
//     public void printEmptyLine(int numb) throws IOException{
//         byte[] CTL_LF = {0x0a};
//         for(int x = 0; x < numb; x ++ ){
//             write(CTL_LF);
//         } 
//     }
// }
