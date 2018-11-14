package com.qualcomm.lbat.Utils;

/**
 * Created by shrishn on 9/3/2017.
 */

public class Constants {

    public static final String HYPHEN = "-";
    public static final String EMPTYSTRING = "";

    public static final String SDCARD = "/sdcard";
    public static final String LOCAL_LOGS =  SDCARD + "/logs/";
    public static final String RIDL_LOGS = SDCARD + "/SmarTestLogs/";
    public static final String CONFIG_MAP = SDCARD + "/SmarTestConfig.map";

    public static final String MUTETOGGLE = "MUTETOGGLE";
    public static final String HOLD = "HOLD";
    public static final String UNHOLD = "UNHOLD";
    public static final String SPEAKERTOGGLE = "SPEAKERTOGGLE";
    public static final String CAMERATOGGLE = "CAMERATOGGLE";
    public static final String SWAP = "SWAP";

    public static final String REGULAR = "REGULAR";
    public static final String VOLTE = "VOLTE";
    public static final String VT_TX = "VT_TX";
    public static final String VT_RX = "VT_RX";
    public static final String VT = "VT";

    public static final String SUCCESS = "Success";
    public static final String FAILURE = "FAILURE";

    public static final String SEREIS = "SERIES";
    public static final String PARALLLEL  = "PARALLEL";

    public static final String FTP_UPLOAD = "FTP-UPLOAD";
    public static final String FTP_DOWNLOAD = "FTP-DOWNLOAD";
    public static final String CALL = "CALL";
    public static final String SMS = "SMS";
    public static final String MMS = "MMS";
    public static final String GMAIL = "GMAIL";
    public static final String INCOMINGCALL = "INCOMINGCALL";
    public static final String DISPLAY = "DISPLAY";
    public static final String BROWSE = "BROWSE";
    public static final String PING = "PING";
    public static final String APM = "APM";
    public static final String BLUETOOTH = "BT";
    public static final String NETWORK ="NW";
    public static final String REBOOT = "REBOOT";
    public static final String SHUTDOWN = "SHUTDOWN";
    public static final String SHARED_SHUT_DOWN_STATUS = "SHUT_DOWN_STATUS";
    public static final String SHARED_SHUT_DOWN_COUNT = "SHUT_DOWN_COUNT";
    public static final String SHUTDOWN_STATUS_ABBRUPT = "ABRUPT_SHUTDOWN";
    public static final String SHUTDOWN_STATUS_NORMAL = "NORMAL_SHUTDOWN";
    public static final String SHUTDOWN_STATUS_SMT = "SMT_SHUTDOWN";
    public static final String SHUTDOWN_START_TIME = "SHUTDOWN_START_TIME";
    public static final String SHUTDOWN_LOG_PATH = "SHUTDOWN_LOG_PATH";
    public static final String WIFI = "WIFI";


    public static final String MO_CONF_IMS = "MO-CONF-IMS";
    public static final String MO_CONF_REGULAR = "MO-CONF-REGULAR";


    public static final String FTP_LOG_HEADER =  "Iteration"+ "," + "ScenarioIter" + "," + "Action" + "," + "Throughput" + ","
            + "RunTime" + ","  + "StartTime" + "," + "StopTime" + "," + "ConnectionResult" + ","
            + "Result" + ","  + "DisconnectionResult" + "," + "NetworkBefore" + "," + "NetworkAfter" + ","
            + "GPSLatitudeBefore" + "," + "GPSLongitudeBefore" + "," +  "GPSLatitudeAfter" + "," + "GPSLongitudeAfter" + ","
            + "WiFiBefore" + "," + "WiFiAfter" + "," + "FileSize" + "," + "ApmStatusBefore" + "," + "ApmStatusAFter" + "," + "DownloadFileName" + "," + "CallStatus" + "," + "\r";

    public static  final String CALL_LOG_HEADER =  "Iteration" + "," + "ScenarioIter" + "," + "Action" + ","  +"StartTime"
            + "," + "StopTime" + "," + "Result"+ "," + "NetworkBefore" + "," + "NetworkAfter" + "," + "CameraToggleStates" + ","
            + "GPSLatitudeBefore" + "," + "GPSLongitudeBefore" + "," + "GPSLatitudeAfter" + ","+ "GPSLongitudeAfter" + ","
            + "SimSlot" + "," + "WiFiBefore" + "," + "WiFiAfter" + "," + "Conference" + ","
            + "MoNumber" + "," + "ActualCallType" + "," + "IMS-Registered" + "," + "CallStates" + "," + "ApmStatus" + "," + "CallTypeBefore"
            + "," + "CallTypeAfter" + "," + "\r";

    public static final String IN_CALL_LOG_HEADER = "Iteration" + "," + "Action" + "," + "CallStatus" + "," + "StartTime" + "," +
            "StopTime" + "," + "Resut" + "," + "Network" + "," + "GPSLatitude" + "," + "GPSLongitude" + "," + "WifiStatus" + "," +
            "MTNumber" + "," + "\r";

    public static final String BLUETOOTH_LOG_HEADER = "Iteration"+ "," + "Action" + "," +"StartTime" + "," + "StopTime" + "," + "Result" + "," + "DevicePairedCount"
            + "," + "ConnectedDeviceName" + "," + "NetworkBefore" + "," + "NetworkAfter" + "," + "WifiStateBefore" + "," + "WifiStateAfter" + ","
            + "GPSLatitudeBefore" + "," + "GPSLongitudeBefore" + "," + "GPSLatitudeAfter"
            + "," + "GPSLongitudeAfter" + "," + "\r";

    public static final String WIFI_LOG_HEADER = "Iteration"+ "," + "Action" + "," +"StartTime" + "," + "StopTime" + "," + "Result"
            + "," + "NetworkBefore" + "," + "NetworkAfter" + "," + "GPSLatitudeBefore" + "," + "GPSLongitudeBefore" + "," + "GPSLatitudeAfter"
            + "," + "GPSLongitudeAfter" + "," + "\r";

    public static final String APM_LOG_HEADER = "Iteration"+ "," + "Action" + "," +"StartTime" + "," + "StopTime" + "," + "Result" + "," + "NetworkBefore"
            + "," + "NetworkAfter" + "," + "WiFi" + "," + "CallStatus" + "," + "GPSLatitudeBefore" + "," + "GPSLongitudeBefore" + "," + "GPSLatitudeAfter"
            + "," + "GPSLongitudeAfter" + "," + "\r";

    public static final String NW_MODE_LOG_HEADER = "Iteration"+ "," + "Action" + "," +"StartTime" + "," + "StopTime" + "," + "NetworkBeforeSub1"
            + "," + "NetworkAfterSub1" + "," + "NetworkModeSub1" + "," + "NetworkBeforeSub2"
            + "," + "NetworkAfterSub2" + "," + "NetworkModeSub2" + "," +"WiFi" + "," + "CallStatus" + ","
            + "GPSLatitudeBefore" + "," + "GPSLongitudeBefore" + "," + "GPSLatitudeAfter"
            + "," + "GPSLongitudeAfter" + "," + "ApmStatus" + "," + "\r";

    public static final String PING_LOG_HEADER = "Iteration" + "," + "Action" + "," +"StartTime" + "," + "StopTime" + "," + "Result"+ "," + "Packet-transmitted"
            + "," + "Packet-received" + ","  +"min ping-time" + ","+ "avg ping-time"+ "," + "max ping-time" + ","+ "mdev"+ "," + "NetworkType"+ "," + "WiFi"+ "," + "\r";

    public static final String SMS_LOG_HEADER = "Iteration" + "," + "Action" + "," + "StartTime" + "," + "StopTime" + "," + "Result" + "," + "NetworkBefore"
            + "," + "NetworkAfter" + "," + "GPSLatitudeBefore" + ","+ "GPSLongitudeBefore" + "," + "GPSLatitudeAfter" + "," + "GPSLongitudeAfter" +","
            + "SimSlot" + "," + "SentStatus" + "," + "DeliveredStatus"  + "," + "\r";

    public static final String GPS_LOG_HEADER = "Iteration"+ "," + "Action" + "," +"StartTime" + "," + "StopTime" + "," + "Result" + "," + "NetworkBefore"
            + "," + "NetworkAfter" + "," + "WifiStateBefore" + "," + "WifiStateAfter" + ","
            + "GPSLatitudeBefore" + "," + "GPSLongitudeBefore" + "," + "GPSAltitudeBefore" + ","
            + "GPSLatitudeAfter" + "," + "GPSLongitudeAfter" + "," + "GPSAltitudeAfter" + "," + "\r";

    public static final String DISPLAY_LOG_HEADER = "Iteration"+ "," + "Action" + "," +"StartTime" + "," + "StopTime" + "," + "Result"
            + "," + "WifiStatus" + "," + "NetworkStatus" + "," + "SelectedSubId" + "," + "StartedVideoId" + "," + "StopedVideoId" +"," + "\r";

    public static final String REBOOT_LOG_HEADER = "Iteration"+ "," + "Action" + "," + "StartTime" + "," + "WifiStatus" + "," + "NetworkStatus" + "," + "\r";

    public static final String SHUTDOWN_LOG_RESULT = "Iteration" + "," + "Action" + "," + "StartTime" + "," + "StopTime" + "," + "Result" + "," + "\r";

    public static final String GMAIL_LOG_RESULT = "Iteration" + "," + "Action" + "," + "StartTime" + "," + "StopTime" + "," + "WifiStatus" + ","
            + "NetworkStatus" + "," + "ApmStatus" + "," + "EmailStatus" + "," +"\r";

}
