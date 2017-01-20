package za.co.clearcell.cordova.zebra;

import java.io.IOException;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.Intent;

import org.apache.cordova.CallbackContext;
import org.apache.cordova.CordovaPlugin;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import android.util.Log;
import com.zebra.android.discovery.*;
import com.zebra.sdk.comm.*;
import com.zebra.sdk.printer.*;

public class ZebraBluetoothPrint extends CordovaPlugin {

    private static final String LOG_TAG = "ZebraBluetoothPrint";
    //String mac = "AC:3F:A4:1D:7A:5C";

    public ZebraBluetoothPrint() {
    }

    @Override
    public boolean execute(String action, JSONArray args, CallbackContext callbackContext) throws JSONException {

         Log.e(LOG_TAG, action);
        if (action.equals("print")) {
            try {
                String mac = args.getString(0);
                String msg = args.getString(1);
                sendData(callbackContext, mac, msg);
            } catch (Exception e) {
                Log.e(LOG_TAG, e.getMessage());
                e.printStackTrace();
                return false; 
            }
            return true;
        }
        if (action.equals("list")) {
            try {
                listPrinters(callbackContext);
            } catch (Exception e) {
                Log.e(LOG_TAG, e.getMessage());
                e.printStackTrace();
                return false; 
            }
            return true;
        }
        return false;
    }


    //This will return the array list of paired bluetooth printers
	void listPrinters(CallbackContext callbackContext) {
        Log.e(LOG_TAG, "listPrinters");

		BluetoothAdapter mBluetoothAdapter = null;
		String errMsg = null;
		try {
			mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
			if (mBluetoothAdapter == null) {
				errMsg = "No bluetooth adapter available";
				Log.e(LOG_TAG, errMsg);
				callbackContext.error(errMsg);
				return;
			}
			if (!mBluetoothAdapter.isEnabled()) {
				Intent enableBluetooth = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
				this.cordova.getActivity().startActivityForResult(enableBluetooth, 0);
			}
			Set<BluetoothDevice> pairedDevices = mBluetoothAdapter.getBondedDevices();
			if (pairedDevices.size() > 0) {
				JSONArray json = new JSONArray();
				for (BluetoothDevice device : pairedDevices) {
					/*
					Hashtable map = new Hashtable();
					map.put("type", device.getType());
					map.put("address", device.getAddress());
					map.put("name", device.getName());
					JSONObject jObj = new JSONObject(map);
					*/
					json.put(device.getName());
				}
				callbackContext.success(json);
			} else {
				callbackContext.error("No Bluetooth Device Found");
			}
			//Log.d(LOG_TAG, "Bluetooth Device Found: " + mmDevice.getName());
		} catch (Exception e) {
			errMsg = e.getMessage();
			Log.e(LOG_TAG, errMsg);
			e.printStackTrace();
			callbackContext.error(errMsg);
		}
	}
   

    /*
     * This will send data to be printed by the bluetooth printer
     */
    void sendData(final CallbackContext callbackContext, final String mac, final String msg) throws IOException {
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    // Instantiate insecure connection for given Bluetooth MAC Address.
                    Connection thePrinterConn = new BluetoothConnectionInsecure(mac);

                    // Verify the printer is ready to print
                    if (isPrinterReady(thePrinterConn)) {

                        // Open the connection - physical connection is established here.
                        thePrinterConn.open();

                        // Send the data to printer as a byte array.
//                        thePrinterConn.write("^XA^FO0,20^FD^FS^XZ".getBytes());
                        thePrinterConn.write(msg.getBytes());


                        // Make sure the data got to the printer before closing the connection
                        Thread.sleep(500);

                        // Close the insecure connection to release resources.
                        thePrinterConn.close();
                        callbackContext.success("Done");
                    } else {
						callbackContext.error("Printer is not ready");
					}
                } catch (Exception e) {
                    // Handle communications error here.
                    callbackContext.error(e.getMessage());
                }
            }
        }).start();
    }

    private Boolean isPrinterReady(Connection connection) throws ConnectionException, ZebraPrinterLanguageUnknownException {
        Boolean isOK = false;
        connection.open();
        // Creates a ZebraPrinter object to use Zebra specific functionality like getCurrentStatus()
        ZebraPrinter printer = ZebraPrinterFactory.getInstance(connection);
        PrinterStatus printerStatus = printer.getCurrentStatus();
        if (printerStatus.isReadyToPrint) {
            isOK = true;
        } else if (printerStatus.isPaused) {
            throw new ConnectionException("Cannot print because the printer is paused");
        } else if (printerStatus.isHeadOpen) {
            throw new ConnectionException("Cannot print because the printer media door is open");
        } else if (printerStatus.isPaperOut) {
            throw new ConnectionException("Cannot print because the paper is out");
        } else {
            throw new ConnectionException("Cannot print");
        }
        return isOK;
    }
}

