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
            String command,
            String username,
            String serverIpAddress,
            int sshPort,
            String privateKeyPath,
            String keyPassphrase) throws JSchException, IOException
    {
        JSch jsch = initializeJSchObjectForKeyAuthentication(privateKeyPath, keyPassphrase);
        Session session = initializeSessionForKeyAuthentication(jsch, username, serverIpAddress, sshPort);
        //String command = "echo \"Sit down, relax, mix yourself a drink and enjoy the show...\" >> /tmp/test.out";
        ChannelExec channelSsh = sendCommand(session, command);
        List<String> lines = readConsoleOutput(channelSsh);
        disconnect(channelSsh, session);
        return lines;
    }

    public JSch initializeJSchObjectForKeyAuthentication(
            String privateKeyPath,
            String passphrase) throws JSchException
    {
        JSch jsch = new JSch();
        jsch.addIdentity(privateKeyPath, passphrase);
        return jsch;
    }

    public Session initializeSessionForKeyAuthentication(
            JSch jsch,
            String username,
            String serverIpAddress,
            int sshPort) throws JSchException
    {
        Session session = jsch.getSession(username, serverIpAddress, sshPort);
        session.setConfig("PreferredAuthentications", "publickey,keyboard-interactive,password");
        java.util.Properties config = new java.util.Properties();
        config.put("StrictHostKeyChecking", "no");
        session.setConfig(config);
        return session;
    }

    public ChannelExec sendCommand(Session session, String command) throws JSchException
    {
        session.connect();
        ChannelExec channelSsh = (ChannelExec) session.openChannel("exec");
        channelSsh.setCommand(command);
        channelSsh.setPty(false);
        channelSsh.connect();
        return channelSsh;
    }

    private List<String> readConsoleOutput(ChannelExec channelSsh) throws IOException
    {
        InputStream stdOut = channelSsh.getInputStream();
        BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(stdOut));
        List<String> lines = new ArrayList<>();
        String line = null;
        while((line = bufferedReader.readLine()) != null) {
            lines.add(line);
        }
        return lines;
    }

    private void disconnect(ChannelExec channelSsh, Session session)
    {
        channelSsh.disconnect();
        session.disconnect();
    }

}
