package za.co.clearcell.cordova.zebra; 

import java.io.IOException;

import org.apache.cordova.CallbackContext;
import org.apache.cordova.CordovaPlugin;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import android.util.Log;
//import com.zebra.android.discovery.*;
//import com.zebra.sdk.comm.*;
//import com.zebra.sdk.printer.*;

import com.dimagi.android.zebraprinttool.action.*; 

public class ZebraBluetoothPrint extends CordovaPlugin {

    private static final String LOG_TAG = "ZebraBluetoothPrint";
    //String mac = "AC:3F:A4:1D:7A:5C";

    public ZebraBluetoothPrinter() { }

    @Override
    public boolean execute(String action, JSONArray args, CallbackContext callbackContext) throws JSONException {

        if (action.equals("print")) {
            try {
                

                String zebraTemplateFilepath = "/sdcard/download/test.zpl";

                Intent i = new Intent("com.dimagi.android.zebraprinttool.action.PrintTemplate");

                i.putExtra("zebra:template_file_path", zebraTemplateFilepath);

                Bundle labelVariableArguments = new Bundle();
                labelVariableArguments.putString("barcode_data", "1234");
                labelVariableArguments.putString("text_data", "1234");
                i.putExtras(labelVariableArguments);

                this.startActivityForResult(i, ZEBRA_CALLOUT);


            } catch (Exception e) {
                Log.e(LOG_TAG, e.getMessage());
                e.printStackTrace();
                return false; 
            }
            return true;
        }
       
        return false;
    }
  /*  
    public void findPrinter(final CallbackContext callbackContext) {
      try {
          BluetoothDiscoverer.findPrinters(this.cordova.getActivity().getApplicationContext(), new DiscoveryHandler() {

              public void foundPrinter(DiscoveredPrinter printer) {
                  if(printer instanceof DiscoveredPrinterBluetooth) {
                     JSONObject printerObj = new JSONObject();
                     try {
                       printerObj.put("address", printer.address);
                       printerObj.put("friendlyName", ((DiscoveredPrinterBluetooth) printer).friendlyName);
                       callbackContext.success(printerObj);
                     } catch (JSONException e) {
                     }
                  } else {              
                    String macAddress = printer.address;
                    //I found a printer! I can use the properties of a Discovered printer (address) to make a Bluetooth Connection
                    callbackContext.success(macAddress);
                  }
              }

              public void discoveryFinished() {
                  //Discovery is done
              }

              public void discoveryError(String message) {
                  //Error during discovery
                  callbackContext.error(message);
              }
          });
      } catch (Exception e) {
          e.printStackTrace();
      }      
    }

    
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

  */ 

}

