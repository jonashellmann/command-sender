package de.hellmann.command_sender.ssh;

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
            String keyPassphrase)
    {
        this.username = username;
        this.host = host;
        this.sshPort = sshPort;
        this.privateKeyPath = privateKeyPath;
        this.keyPassphrase = keyPassphrase;
    }

    public HostConfiguration(
            String username,
            String password,
            String host,
            int sshPort)
    {
        this.username = username;
        this.password = password;
        this.host = host;
        this.sshPort = sshPort;
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
