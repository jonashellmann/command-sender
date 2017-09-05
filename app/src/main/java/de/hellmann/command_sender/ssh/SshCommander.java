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

    public List<String> sendCommandWithPasswordAuthentification(
            String command,
            String username,
            String password,
            String host,
            int sshPort) throws JSchException, IOException
    {
        JSch jsch = new JSch();
        Session session = initializeSessionForPasswordAuthentication(jsch, username, password, host, sshPort);
        ChannelExec channelSsh = sendCommand(session, command);
        List<String> lines = readConsoleOutput(channelSsh);
        disconnect(channelSsh, session);
        return lines;
    }

    public List<String> sendCommandWithKeyAuthentication(
            String command,
            String username,
            String host,
            int sshPort,
            String privateKeyPath,
            String keyPassphrase) throws JSchException, IOException
    {
        JSch jsch = initializeJSchObjectForKeyAuthentication(privateKeyPath, keyPassphrase);
        Session session = initializeSessionForKeyAuthentication(jsch, username, host, sshPort);
        ChannelExec channelSsh = sendCommand(session, command);
        List<String> lines = readConsoleOutput(channelSsh);
        disconnect(channelSsh, session);
        return lines;
    }

    private JSch initializeJSchObjectForKeyAuthentication(
            String privateKeyPath,
            String passphrase) throws JSchException
    {
        JSch jsch = new JSch();
        jsch.addIdentity(privateKeyPath, passphrase);
        return jsch;
    }

    private Session initializeSessionForPasswordAuthentication(
            JSch jsch,
            String username,
            String password,
            String host,
            int sshPort) throws JSchException
    {
        Session session = jsch.getSession(username, host, sshPort);
        session.setPassword(password);
        return session;
    }

    private Session initializeSessionForKeyAuthentication(
            JSch jsch,
            String username,
            String host,
            int sshPort) throws JSchException
    {
        Session session = jsch.getSession(username, host, sshPort);
        session.setConfig("PreferredAuthentications", "publickey,keyboard-interactive,password");
        java.util.Properties config = new java.util.Properties();
        config.put("StrictHostKeyChecking", "no");
        session.setConfig(config);
        return session;
    }

    private ChannelExec sendCommand(Session session, String command) throws JSchException
    {
        session.connect(3000);
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
