package listeners;

import commands.ICommand;
import commands.ICommandAsync;
import commands.IStringSelectMenu;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.events.interaction.component.ButtonInteractionEvent;
import net.dv8tion.jda.api.events.interaction.component.EntitySelectInteractionEvent;
import net.dv8tion.jda.api.events.interaction.component.StringSelectInteractionEvent;
import net.dv8tion.jda.api.events.session.ReadyEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class CommandManagerListener extends ListenerAdapter {
    private final List<ICommand> commands = new ArrayList<>();

    public CommandManagerListener(ICommand... commands) {
        this.commands.addAll(Arrays.asList(commands));
    }

    @Override
    public void onReady(@NotNull ReadyEvent event) {
        for (Guild guild : event.getJDA().getGuilds()) {
            for (ICommand command : commands) {
                guild.upsertCommand(command.getName(), command.getDescription()).addOptions(command.getOptions()).queue();
            }
        }
    }

    @Override
    public void onSlashCommandInteraction(SlashCommandInteractionEvent event) {
        if (!event.isFromGuild()) return;
        for (ICommand command : commands) {
            if (command.getName().equals(event.getName())) {
                if (command instanceof ICommandAsync) {
                    ((ICommandAsync) command).executeAsync(event);
                } else {
                    command.execute(event);
                }

                break;
            }
        }
    }

    @Override
    public void onStringSelectInteraction(StringSelectInteractionEvent event) {
        for (ICommand command : commands) {
            if (command instanceof IStringSelectMenu) {
                ((IStringSelectMenu) command).onStringSelectionInteraction(event);
            }
        }
    }

    // ButtonTest.java in commands.chat.test
    @Override
    public void onButtonInteraction(ButtonInteractionEvent event) {
        if (event.getComponentId().equals("hello")) {
            event.reply("Hello :)").queue(); // send a message in the channel
        } else if (event.getComponentId().equals("emoji")) {
            event.editMessage("That button didn't say click me").queue(); // update the message
        }
    }

    // EntityDDTest.java in commands.chat.test


    @Override
    public void onEntitySelectInteraction(EntitySelectInteractionEvent event) {
        if (event.getComponentId().equals("edrop-test")) {
            List<User> users = event.getMentions().getUsers();
            event.reply("You high-fived " + users.get(0).getAsMention()).queue();
        }
    }

    public void addCommand(ICommand command) {
        commands.add(command);
    }
}
