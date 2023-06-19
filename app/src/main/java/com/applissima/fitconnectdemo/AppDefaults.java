package com.applissima.fitconnectdemo;

/**
 * Created by ilkerkuscan on 08/02/17.
 */

public class AppDefaults {

    // --- This App Version ---
    public static final String CURRENT_VERSION = "2.6";

    // --- Log Defaults ---
    public static final String LOG_SRC_TYPE = "FcCollector";
    //public static final String LOG_SRC_ID = "COLL" + Settings.siteId + "001";
    public static final String REMOTE_LOGCAT_APIKEY = "59b2509945e98";
    //public static final String LOG_LOCATION_ID = "212901"; -> transferred to Settings

    // --- API Defaults ---
    public static final String SCRT_KEY = "";
    public static final String API_PASS = "";
    public static final String API_RETRIEVE_KEY = "retrieveProfile";
    public static final String API_INSERTDATA_KEY = "insertExerciseData";
    public static final String API_INSERTLOGDATA_KEY = "insertLogData";
    public static final String API_RETRIEVE_URL = "http://portal.fitconnect.club/api/retrieveProfileForCollector.ashx";
    public static final String API_INSERTDATA_URL = "http://portal.fitconnect.club/api/insertActivityData.ashx";
    public static final String API_INSERTLOGDATA_URL = "http://portal.fitconnect.club/api/insertLogData.ashx";
    //public static final String EMAIL = "00024";

    public static final int PERMISSION_WRITE_STORAGE = 1;

    public static final String PREFS_NAME = "FitConnectPrefs";

    // --- Time Format Defaults ---
    public static final String DATE_FORMAT_DEFAULT = "dd.MM.yyyy";
    public static final String TIME_FORMAT_DEFAULT = "dd.MM.yyyy HH:mm:ss";
    public static final String TIME_FORMAT_FCT = "yyyy.MM.dd HH:mm";
    public static final String TIME_FORMAT_UPLOADLOGS = "yyyy.MM.dd HH:mm:ss";
    public static final String TIME_FORMAT_CLOCK = "HH:mm";
    public static final String TIME_FORMAT_BACKUP = "yyyyMMdd";
    public static final String TIME_FORMAT_FILENAME = "yyyy_MM_dd_HH_mm_ss";
    public static final String TIME_FORMAT_MINAGGID = "yyyyMMddHHmmss";
    public static final String COLLECTOR_EMAIL = "";

    // --- REALM Defaults ---
    public static final String REALM_FILENAME = "fitConnect.realm";
    public static final String REALM_REP_FILENAME = "fitConnect_REP.realm"; // Replica DB Filename
    public static final String REALMLOG_FILENAME = "fitConLogDb.realm";
    public static final String REALMLOG_REP_FILENAME = "fitConLogDb_REP.realm"; // Replica LogDB Filename
    public static final String REALM_REP_DIR = "RealmReplicas";
    public static final String REALM_BKP_DIR = "RealmBackups";
    //public static final String REALM_BKP_OLD_DIR = "RealmBackupFiles";

    // --- Crashing Defaults ---
    public static final String CRASH_UNCAUGHT = "UncaughtException";  // Uncaught
    public static final String CRASH_ANR = "ANRException";     // App Not Responding
    public static final String CRASH_REPORTS_DIR = "FitConnectCrashReports";

    // --- Settings Defaults ---
    public static final String SETTINGS_PW = "qw";

    // --- Email Defaults ---
    public static final String GMAIL_USER = "";
    public static final String GMAIL_PW = "";
    public static final String STR_END_OF_DAY = "21:00";
    public static final String STR_MIDNIGHT = "00:05";
    public static final String STR_MORNING = "07:00";

    // --- Event Messaging Defaults ---
    public static final int EVENT_CARDS = 0;
    public static final int EVENT_TIMER = 1;
    public static final int EVENT_NETWORKS = 2;
    public static final int EVENT_WORKSUMMARY = 3;
    public static final int EVENT_IPCHANGE = 4;
    public static final int EVENT_ANTPLUS_INFO = 5;
    public static final int EVENT_RESTART = 6;
    public static final int EVENT_TEMP_BELTS = 7;
    public static final int EVENT_EXP_TEMP_BELTS = 8;

    public static final String ACTION_BRDC_UPDATE = "com.applissima.FcUpdateBroadcast";
    public static final String ACTION_BRDC_SIGNAL = "com.applissima.FcSignalBroadcast";
    public static final String ACTION_BRDC_SNACKBAR = "com.applissima.SnackbarBroadcast";

    // Firestore Defaults
    public static final String FS_VERSIONS = "versions";
    public static final String FS_CLUB_BELTS = "clubBelts";
    public static final String FS_CLUBS = "clubs";
    public static final String FS_MEMBERS = "members";

    // ANT Defaults
    public static final String DATA_MSG = "info.hmm.antplusreceiver.antplusservice.DATA_MSG";
    public static final String DATA_HR = "info.hmm.antplusreceiver.antplusservice.DATA_HR";
    public static final String DATA_SC = "info.hmm.antplusreceiver.antplusservice.DATA_SC";
    public static final String DATA_STATUS = "info.hmm.antplusreceiver.antplusservice.DATA_STATUS";

    // --- Javascript Defaults ---
    /*public static final String JS_GET = "(function() { return document.getElementById(\"div_5_3_add\").value; })();";
    public static final String JS_SET = "(function() { document.getElementById(\"div_5_3_add\").value = \""
            + StaticVariables.machineIP + "\"; })();";
    public static final String JS_SAVE = "net_setting_apply();";
    public static final String JS_RESTART = "javascript:document.form_do_cmd.submit()";*/


}
