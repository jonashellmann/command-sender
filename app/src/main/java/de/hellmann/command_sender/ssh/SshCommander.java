package de.hellmann.command_sender.ssh;

import com.jcraft.jsch.ChannelExec;
import com.jcraft.jsch.JSch;
import com.jcraft.jsch.JSchException;
import com.jcraft.jsch.Session;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

import de.hellmann.command_sender.ssh.domain.HostConfiguration;

/**
 * Created by hellm on 27.08.2017.
 */

public class SshCommander {

    public List<String> sendCommand(String command, HostConfiguration hostConfiguration)
            throws JSchException, IOException
    {
        if (hostConfiguration.usesKeyAuthentication())
        {
            return sendCommandWithKeyAuthentication(command, hostConfiguration);
        }
        else
        {
            return sendCommandWithPasswordAuthentification(command, hostConfiguration);
        }
    }

    private List<String> sendCommandWithPasswordAuthentification(
            String command,
            HostConfiguration hostConfiguration) throws JSchException, IOException
    {
        JSch jsch = new JSch();
        Session session =
                initializeSessionForPasswordAuthentication(
                        jsch,
                        hostConfiguration.getUsername(),
                        hostConfiguration.getPassword(),
                        hostConfiguration.getHost(),
                        hostConfiguration.getSshPort());
        ChannelExec channelSsh = sendCommand(session, command);
        List<String> lines = readConsoleOutput(channelSsh);
        disconnect(channelSsh, session);
        return lines;
    }

    private List<String> sendCommandWithKeyAuthentication(
            String command,
            HostConfiguration hostConfiguration) throws JSchException, IOException
    {
        JSch jsch = initializeJSchObjectForKeyAuthentication(
                hostConfiguration.getPrivateKeyPath(),
                hostConfiguration.getKeyPassphrase());
        Session session = initializeSessionForKeyAuthentication(
                jsch,
                hostConfiguration.getUsername(),
                hostConfiguration.getHost(),
                hostConfiguration.getSshPort());
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
        System.out.println(privateKeyPath);
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
