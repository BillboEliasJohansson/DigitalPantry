package com.grouponetwo.digitalpantry.ui.scanReceipt;

import android.os.AsyncTask;
import android.util.Log;
import android.widget.TextView;

import com.jcraft.jsch.ChannelExec;
import com.jcraft.jsch.JSch;
import com.jcraft.jsch.JSchException;

import java.io.IOException;
import java.io.InputStream;

public class SSH {
    private static final String USERNAME = "DP";
    private static final String PASSWORD = "DP123";
    private static final String IP_ADRESS = "192.168.1.126";
    private static final int PORT = 22;

    public static void executeSSHcommand(String command) {
        executeSSHcommand(command, null);
    }

    public static void executeSSHcommand(String command, TextView textView) {

        new AsyncTask<Integer, String, Void>() {
            @Override
            protected Void doInBackground(Integer... params) {

                String output = "";

                try {
                    JSch jsch = new JSch();
                    com.jcraft.jsch.Session sess = jsch.getSession(USERNAME, IP_ADRESS, PORT);
                    sess.setPassword(PASSWORD);
                    sess.setConfig("StrictHostKeyChecking", "no");
                    sess.setTimeout(Integer.MAX_VALUE);
                    sess.connect();

                    ChannelExec exec = (ChannelExec) sess.openChannel("exec");
                    exec.setCommand(command);

                    // Get the InputStream from the ChannelExec to capture the command output
                    InputStream in = exec.getInputStream();

                    exec.connect();

                    // Read the command output from the InputStream
                    byte[] tmp = new byte[1024];

                    while (true) {
                        while (in.available() > 0) {
                            int i = in.read(tmp, 0, 1024);
                            if (i < 0) break;
                            publishProgress(new String(tmp, 0, i));
                        }
                        if (exec.isClosed()) {
                            int exitStatus = exec.getExitStatus();
                            Log.i("SSH", "Exit status: " + exitStatus);
                            break;
                        }
                        try {
                            Thread.sleep(1000);
                        } catch (Exception ee) {
                            Log.i("SSH", ee.getMessage());
                        }
                    }

                    exec.disconnect();
                    sess.disconnect();

                } catch (JSchException | IOException e) {
                    Log.i("SSH", "Failed: " + e.getMessage());
                }


                return null;
            }

            @Override
            protected void onProgressUpdate(String... values) {
                super.onProgressUpdate(values);
                if (textView != null) {
                    textView.setText(values[0]);
                }
            }

        }.execute(1);
    }

}
