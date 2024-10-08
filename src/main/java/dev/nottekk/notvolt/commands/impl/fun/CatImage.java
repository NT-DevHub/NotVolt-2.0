package dev.nottekk.notvolt.commands.impl.fun;

import com.google.gson.JsonArray;
import dev.nottekk.notvolt.bot.BotConfig;
import dev.nottekk.notvolt.bot.BotWorker;
import dev.nottekk.notvolt.commands.Category;
import dev.nottekk.notvolt.commands.CommandEvent;
import dev.nottekk.notvolt.commands.interfaces.Command;
import dev.nottekk.notvolt.commands.interfaces.ICommand;
import dev.nottekk.notvolt.utils.EAccessLevel;
import dev.nottekk.notvolt.utils.external.RequestUtility;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.interactions.commands.build.CommandData;

/**
 * A command to get a random cat images.
 */
@Command(name = "randomcat", description = "command.description.randomcat", category = Category.FUN)
public class CatImage implements ICommand {

    /**
     * @see ICommand#onPerform(CommandEvent)
     */
    @Override
    public void onPerform(CommandEvent commandEvent) {

        JsonArray js = RequestUtility.requestJson(RequestUtility.Request.builder().url("https://api.thecatapi.com/v1/images/search").build()).getAsJsonArray();

        EmbedBuilder em = new EmbedBuilder();

        em.setTitle(commandEvent.getResource("label.randomCatImage"));
        em.setColor(BotWorker.randomEmbedColor());
        em.setImage(js.get(0).getAsJsonObject().get("url").getAsString());
        em.setFooter(commandEvent.getResource("label.footerMessage", commandEvent.getMember().getEffectiveName(), BotConfig.getAdvertisement()), commandEvent.getMember().getEffectiveAvatarUrl());

        commandEvent.reply(em.build());
    }

    /**
     * @see ICommand#getCommandData()
     */
    @Override
    public CommandData getCommandData() {
        return null;
    }

    /**
     * @see ICommand#getAlias()
     */
    @Override
    public String[] getAlias() {
        return new String[] { "cat", "catimage" };
    }

    /**
     * @see ICommand#getCommandLevel()
     */
    @Override
    public EAccessLevel getCommandLevel() {
        return EAccessLevel.USER;
    }
}
