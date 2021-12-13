package ru.e2r7hn07fl47.botcontroller;

import android.util.Log;

import com.jcraft.jsch.ChannelExec;
import com.jcraft.jsch.JSch;
import com.jcraft.jsch.JSchException;
import com.jcraft.jsch.Session;

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;

public class SshUtils {

    private final String login;
    private final String password;
    private final String address;
    private final int port;

    public SshUtils() {
        this.login = MainActivity.sPref.getString("login", "");
        this.password = MainActivity.sPref.getString("password", "");
        this.address = MainActivity.sPref.getString("address", "");
        this.port = MainActivity.sPref.getInt("port", 0);

    }

    public ArrayList<Integer> getBotList() {
        ArrayList<Integer> botList = new ArrayList<>();

        Session session = null;
        ChannelExec channel = null;

        try {
            session = new JSch().getSession(login, address, port);
            session.setPassword(password);
            session.setConfig("StrictHostKeyChecking", "no");
            session.connect();

            channel = (ChannelExec) session.openChannel("exec");
            channel.setCommand("ls -d */");
            ByteArrayOutputStream responseStream = new ByteArrayOutputStream();
            channel.setOutputStream(responseStream);
            channel.connect();

            while (channel.isConnected()) {
                Thread.sleep(100);
            }

            String[] responseArray = responseStream.toString().split("\n");
            for (String response: responseArray) {
                if (!response.contains("_")) {
                    botList.add(Integer.valueOf(response.replace("/", "").replace("bot", "")));
                }
            }
        } catch (InterruptedException | JSchException e) {
            e.printStackTrace();
        } finally {
            if (session != null) {
                session.disconnect();
            }
            if (channel != null) {
                channel.disconnect();
            }
        }
        return botList;
    }

}
