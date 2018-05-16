package de.hellmann.command_sender.ssh.domain;

/**
 * Created by hellm on 07.09.2017.
 */

public class CommandConfiguration {

    private int id;
    private String command;
    private String name;
    private HostConfiguration hostConfiguration;

    public CommandConfiguration(int id, String command, String name, HostConfiguration hostConfiguration)
    {
        this.id = id;
        this.command = command;
        this.name = name;
        this.hostConfiguration = hostConfiguration;
    }

    public int getId() { return id; }

    public String getCommand()
    {
        return command;
    }

    public String getName()
    {
        return name;
    }

    public HostConfiguration getHostConfiguration()
    {
        return hostConfiguration;
    }

}
