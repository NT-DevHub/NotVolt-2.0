package dev.nottekk.notvolt.actions.customevents.container;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import dev.nottekk.notvolt.actions.ActionRunContainer;
import dev.nottekk.notvolt.actions.customevents.CustomEventActionEvent;
import dev.nottekk.notvolt.actions.customevents.IEventAction;
import dev.nottekk.notvolt.bot.BotWorker;
import de.presti.ree6.sql.entities.custom.CustomEventAction;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import net.dv8tion.jda.api.entities.Guild;

import java.util.ArrayList;
import java.util.List;

/**
 * A Container used to store all needed Information for a CustomEventAction.
 */
@Slf4j
public class CustomEventContainer {

    @Getter(AccessLevel.PUBLIC)
    long id;

    /**
     * The Guild.
     */
    @Getter(AccessLevel.PUBLIC)
    Guild guild;

    /**
     * The Extra Argument.
     */
    @Getter(AccessLevel.PUBLIC)
    String extraArgument;

    /**
     * The Actions.
     */
    @Getter(AccessLevel.PUBLIC)
    List<ActionRunContainer> actions = new ArrayList<>();

    /**
     * Create a new CustomEventContainer.
     *
     * @param customEventAction The CustomEventAction to create the Container for.
     */
    public CustomEventContainer(CustomEventAction customEventAction) {
        guild = BotWorker.getShardManager().getGuildById(customEventAction.getGuildId());
        id = customEventAction.getId();

        if (customEventAction.getActions() != null && customEventAction.getActions().isJsonArray()) {
            JsonArray jsonArray = customEventAction.getActions().getAsJsonArray();
            for (JsonElement jsonElement : jsonArray) {
                JsonObject jsonObject = jsonElement.getAsJsonObject();

                if (jsonObject.has("action") &&
                        jsonObject.has("value") &&
                        jsonObject.get("action").isJsonPrimitive() &&
                        jsonObject.get("value").isJsonPrimitive()) {
                    String action = jsonObject.getAsJsonPrimitive("action").getAsString();
                    String value = jsonObject.getAsJsonPrimitive("value").getAsString();
                    String[] args = value.split(" ");

                    Class<? extends IEventAction> actionClass = CustomEventContainerCreator.getAction(action);
                    if (actionClass != null) {
                        try {
                            ActionRunContainer actionRunContainer = new ActionRunContainer(actionClass.getConstructor().newInstance(), args);
                            actions.add(actionRunContainer);
                        } catch (Exception e) {
                            log.error("Couldn't parse CustomEvent-action!", e);
                        }
                    }
                }
            }
        }
    }

    /**
     * Run all Actions.
     */
    public void runActions() {
        actions.forEach(run -> run.getAction().runAction(new CustomEventActionEvent(guild, run.getArguments())));
    }
}
