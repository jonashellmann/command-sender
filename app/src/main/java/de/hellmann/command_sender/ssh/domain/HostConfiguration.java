package de.hellmann.command_sender.ssh.domain;

/**
 * Created by hellm on 07.09.2017.
 */

public class HostConfiguration
{

    private String username;
    private String host;
    private int sshPort;
    private String privateKeyPath;
    private String keyPassphrase;
    private String password;

    public HostConfiguration(
            String username,
            String host,
            int sshPort,
            String privateKeyPath,
            String keyPassphrase,
            String password)
    {
        this.username = username;
        this.host = host;
        this.sshPort = sshPort;
        this.privateKeyPath = privateKeyPath;
        this.keyPassphrase = keyPassphrase;
        this.password = password;
    }

    public String getUsername()
    {
        return username;
    }

    public String getHost()
    {
        return host;
    }

    public int getSshPort()
    {
        return sshPort;
    }

    public String getPrivateKeyPath()
    {
        return privateKeyPath;
    }

    public String getKeyPassphrase()
    {
        return keyPassphrase;
    }

    public String getPassword()
    {
        return password;
    }

    public boolean usesKeyAuthentication()
    {
        return keyPassphrase != null;
    }
}
