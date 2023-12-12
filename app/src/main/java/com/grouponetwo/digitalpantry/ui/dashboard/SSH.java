package com.grouponetwo.digitalpantry.ui.dashboard;

import android.os.AsyncTask;

import com.jcraft.jsch.ChannelExec;
import com.jcraft.jsch.JSch;
import com.jcraft.jsch.JSchException;

import java.io.IOException;
import java.io.InputStream;

public class SSH {
    private static StringBuilder output = new StringBuilder();

    public static String executeSSHcommand(String command) {

        new AsyncTask<Integer, Void, Void>(){
            @Override
            protected Void doInBackground(Integer... params) {
                String user = "DP";
                String password = "DP123";
                String host = "192.168.1.126";
                int port = 22;

                try {
                    JSch jsch = new JSch();
                    com.jcraft.jsch.Session session = jsch.getSession(user, host, port);
                    session.setPassword(password);
                    session.setConfig("StrictHostKeyChecking", "no");
                    session.setTimeout(10000);
                    session.connect();
                    ChannelExec channel = (ChannelExec) session.openChannel("exec");
                    channel.setCommand(command);

                    // Capture the output
                    InputStream commandOutput = channel.getInputStream();

                    channel.connect();

                    // Read the output
                    byte[] tmp = new byte[1024];
                    while (true) {
                        while (commandOutput.available() > 0) {
                            int i = commandOutput.read(tmp, 0, 1024);
                            if (i < 0) break;
                            output.append(new String(tmp, 0, i));
                        }

                        if (channel.isClosed()) {
                            if (commandOutput.available() > 0) continue;
                            break;
                        }

                        try {
                            Thread.sleep(1000);
                        } catch (InterruptedException ignored) {
                        }
                    }

                    channel.disconnect();
                    session.disconnect();

                } catch (JSchException | IOException e) {

                }
                return null;
            }
            @Override
            protected void onPostExecute(Void v) {

            }
        }.execute(1);

        return output.toString();
    }
}
