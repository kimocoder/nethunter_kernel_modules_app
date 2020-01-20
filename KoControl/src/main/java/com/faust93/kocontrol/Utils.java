package com.faust93.kocontrol;

import android.util.Log;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;

/**
 * Created by faust93 on 05.09.13.
 */
public class Utils implements Constants {

    public static boolean spawnCmd(String cmd) {

        Process process = null;
        BufferedReader input = null;
        boolean rc;

        Log.d(APP_TAG, "spawnCmd() entry: " + cmd);

        try {

            process = Runtime.getRuntime().exec("su");

            DataOutputStream os = new DataOutputStream(process.getOutputStream());
            input = new BufferedReader(new InputStreamReader(process.getInputStream()),4096);

            os.writeBytes(cmd + "\n");
            os.flush();

            os.writeBytes("exit\n");
            os.flush();

            input.close();
            process.waitFor();

        } catch (Exception err) {
            err.printStackTrace();
        }
        finally
        {
            try {
                if (input != null) {
                    input.close();
                }

                rc = process.exitValue() != 0;

                process.destroy();
                return rc;
            }
            catch (IOException e) {
                return true;
            }
        }
    }
}
