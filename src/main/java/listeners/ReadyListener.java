package listeners;

import net.dv8tion.jda.api.events.GenericEvent;
import net.dv8tion.jda.api.events.session.ReadyEvent;
import net.dv8tion.jda.api.hooks.EventListener;

public class ReadyListener implements EventListener {

    @Override
    public void onEvent(GenericEvent e) {
        if (e instanceof ReadyEvent)
            System.out.printf("API is ready! Logged in as: %s\n", e.getJDA().getSelfUser().getName());
    }
}




