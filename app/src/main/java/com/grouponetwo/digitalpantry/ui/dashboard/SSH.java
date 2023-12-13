package com.grouponetwo.digitalpantry.ui.dashboard;

import android.os.AsyncTask;
import android.util.Log;

import com.jcraft.jsch.ChannelExec;
import com.jcraft.jsch.JSch;
import com.jcraft.jsch.JSchException;

public class SSH {
    private static final String USERNAME = "DP";
    private static final String PASSWORD = "DP123";
    private static final String IP_ADRESS = "192.168.1.126";
    private static final int PORT = 22;
    
    public static void executeSSHcommand(String command) {

        new AsyncTask<Integer, Void, Void>(){
            @Override
            protected Void doInBackground(Integer... params) {

                try {
                    JSch jsch = new JSch();
                    com.jcraft.jsch.Session sess = jsch.getSession(USERNAME, IP_ADRESS, PORT);
                    sess.setPassword(PASSWORD);
                    sess.setConfig("StrictHostKeyChecking", "no");
                    sess.setTimeout(10000);
                    sess.connect();
                    ChannelExec exec = (ChannelExec) sess.openChannel("exec");
                    exec.setCommand(command);
                    exec.connect();
                    exec.disconnect();
                    sess.disconnect();

                } catch (JSchException e) {
                    Log.i("SSH", "Failed");
                }
                return null;
            }
            @Override
            protected void onPostExecute(Void v) {
                Log.i("SSH", "Success");
            }
        }.execute(1);

    }
}
