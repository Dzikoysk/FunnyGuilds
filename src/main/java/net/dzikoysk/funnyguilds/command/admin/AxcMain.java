package net.dzikoysk.funnyguilds.command.admin;

import org.bukkit.command.CommandSender;

import net.dzikoysk.funnyguilds.command.util.Executor;
import net.dzikoysk.funnyguilds.data.Messages;

public class AxcMain implements Executor {

    @Override
    public void execute(CommandSender sender, String[] args) {
        for (String line : Messages.getInstance().adminHelpList) {
            sender.sendMessage(line);
        }
    }
}
