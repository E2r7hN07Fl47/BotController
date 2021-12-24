package ru.e2r7hn07fl47.botcontroller;

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
        String[] responseArray = getResponce("ls -d */").split("\n");
        ArrayList<Integer> botList = new ArrayList<>();
        if (responseArray.length == 0) {
            return botList;
        }

        for (String response: responseArray) {
            if (!response.contains("_")) {
                try {
                    botList.add(Integer.valueOf(response.replace("/", "").replace("bot", "")));
                } catch (NumberFormatException e) {
                    e.printStackTrace();
                }
            }
        }

        return botList;
    }

    public ArrayList<String> getActiveBotList() {
        ArrayList<String> botList = new ArrayList<>();
        String[] responseArray = getResponce("tmux ls").split("\n");
        if (responseArray[0].contains("error") || responseArray[0].contains("no server")) {
            return botList;
        }

        for (String response: responseArray) {
            botList.add(response.split(":")[0].replace("python_bot", ""));
        }

        return botList;
    }

    public void enableBot(String botNubmer) {
        String command = "cd bot" + botNubmer + "/ && ./start.sh && cd ..";
        getResponce(command);
    }

    public void disableBot(String botNubmer) {
        String command = "cd bot" + botNubmer + "/ && ./stop.sh && cd ..";
        getResponce(command);
    }

    public void enableAllBots() {
        getResponce("./run.sh");
    }

    public void disableAllBots() {
        getResponce("./stop.sh");
    }

    private String getResponce(String command) {
        Session session = null;
        ChannelExec channel = null;
        String response = "";

        try {
            session = new JSch().getSession(login, address, port);
            session.setPassword(password);
            session.setConfig("StrictHostKeyChecking", "no");
            session.connect();

            channel = (ChannelExec) session.openChannel("exec");
            channel.setCommand(command);
            ByteArrayOutputStream responseStream = new ByteArrayOutputStream();
            channel.setOutputStream(responseStream);
            channel.connect();

            while (channel.isConnected()) {
                Thread.sleep(100);
            }

            response = responseStream.toString();
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
        return response;
    }

}
