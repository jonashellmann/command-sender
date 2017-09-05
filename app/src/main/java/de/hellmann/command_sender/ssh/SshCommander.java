package de.hellmann.command_sender.ssh;

import com.jcraft.jsch.ChannelExec;
import com.jcraft.jsch.JSch;
import com.jcraft.jsch.JSchException;
import com.jcraft.jsch.Session;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

/**
 * Created by hellm on 27.08.2017.
 */

public class SshCommander {

    public List<String> sendCommandWithPasswordAuthentification()
    {
        return null;
    }

    public List<String> sendCommandWithKeyAuthentication(
            String username,
            String serverIpAddress,
            int sshPort,
            String privateKeyPath,
            String keyPassphrase) throws JSchException, IOException
    {
        JSch jsch = new JSch();
        jsch.addIdentity(privateKeyPath, keyPassphrase);
        Session session = jsch.getSession(username, serverIpAddress, sshPort);
        session.setConfig("PreferredAuthentications", "publickey,keyboard-interactive,password");
        java.util.Properties config = new java.util.Properties();
        config.put("StrictHostKeyChecking", "no");
        session.setConfig(config);
        String command = "echo \"Sit down, relax, mix yourself a drink and enjoy the show...\" >> /tmp/test.out";

        session.connect();
        ChannelExec channelssh = (ChannelExec) session.openChannel("exec");
        channelssh.setCommand(command);
        channelssh.setPty(false);
        channelssh.connect();

        InputStream stdOut = channelssh.getInputStream();
        BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(stdOut));
        List<String> ausgabeZeilen = new ArrayList<>();
        String zeile = null;
        while((zeile = bufferedReader.readLine()) != null) {
            ausgabeZeilen.add(zeile);
        }

        channelssh.disconnect();
        session.disconnect();


        return ausgabeZeilen;
    }

}
