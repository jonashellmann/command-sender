package de.hellmann.command_sender.ssh;

/**
 * Created by hellm on 07.09.2017.
 */

public class CommandConfiguration {

    private String command;
    private String name;
    private HostConfiguration hostConfiguration;

    public CommandConfiguration(String command, String name, HostConfiguration hostConfiguration)
    {
        this.command = command;
        this.name = name;
        this.hostConfiguration = hostConfiguration;
    }

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
