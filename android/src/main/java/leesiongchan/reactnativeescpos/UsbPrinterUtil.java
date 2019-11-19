package leesiongchan.reactnativeescpos;

import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.hardware.usb.UsbDevice;
import android.hardware.usb.UsbManager;
import android.util.Log;

public class UsbPrinterUtil {

    private final String LOG_TAG = "UsbPrinter";
    private Context mContext;
    private final UsbManager mUsbManager;
	private String ACTION_USB_PERMISSION = "asia.merchant.weeat.USB_PERMISSION";
    
    public UsbPrinterUtil(Context context) {
        mContext = context;
        mUsbManager = (UsbManager) context.getSystemService(Context.USB_SERVICE);
    }
    
    final BroadcastReceiver mReceiver = new BroadcastReceiver() {
		@Override
		public void onReceive(Context context, Intent intent) {
			Log.d(LOG_TAG, intent.getAction());
			context.unregisterReceiver(this);
		}
    };
    
    public void requestPermission(UsbDevice usbDevice) {
		if (!mUsbManager.hasPermission(usbDevice)) {
			IntentFilter ifilter = new IntentFilter(ACTION_USB_PERMISSION);
			mContext.registerReceiver(mReceiver, ifilter);

			PendingIntent pi = PendingIntent.getBroadcast(mContext, 0, new Intent(
					ACTION_USB_PERMISSION), 0);
			mUsbManager.requestPermission(usbDevice, pi);
		}
	}
	
	private static boolean contains(int[] ids, int id) {
		for(int i : ids) {
			if(i == id)
				return true;
		}
		return false;
	}
	
	public List<UsbDevice> findDevicesByVid(int[] vids) {
        final List<UsbDevice> result = new ArrayList<UsbDevice>();

        Log.d(LOG_TAG, "find usb device ...");
        for (final UsbDevice usbDevice : mUsbManager.getDeviceList().values()) {
        	if(contains(vids, usbDevice.getVendorId())) {
            	Log.d(LOG_TAG, String.format("usb device %04X:%04X : device_id=%d, device_name=%s",
            			usbDevice.getVendorId(), usbDevice.getProductId(),
            			usbDevice.getDeviceId(), usbDevice.getDeviceName()));
                result.add(usbDevice);
            }
        }

        return result;
    }

}
