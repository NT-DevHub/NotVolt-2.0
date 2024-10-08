package dev.nottekk.notvolt.commands.impl.music;

import best.azura.eventbus.handler.EventHandler;
import com.sedmelluq.discord.lavaplayer.track.AudioTrackInfo;
import dev.nottekk.notvolt.api.events.MusicPlayerStateChangeEvent;
import dev.nottekk.notvolt.audio.music.GuildMusicManager;
import dev.nottekk.notvolt.commands.Category;
import dev.nottekk.notvolt.commands.CommandEvent;
import dev.nottekk.notvolt.commands.interfaces.Command;
import dev.nottekk.notvolt.commands.interfaces.ICommand;
import dev.nottekk.notvolt.language.LanguageService;
import dev.nottekk.notvolt.main.Main;
import dev.nottekk.notvolt.utils.EAccessLevel;
import dev.nottekk.notvolt.utils.data.ArrayUtil;
import dev.nottekk.notvolt.bot.BotConfig;
import dev.nottekk.notvolt.utils.others.FormatUtil;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.interactions.commands.build.CommandData;
import net.dv8tion.jda.api.interactions.components.buttons.Button;
import net.dv8tion.jda.api.interactions.components.buttons.ButtonStyle;
import net.dv8tion.jda.api.utils.messages.MessageCreateBuilder;
import net.dv8tion.jda.api.utils.messages.MessageEditBuilder;

import java.awt.*;

/**
 * Creates a small typeof UI to control music in a channel.
 */
@Command(name = "musicpanel", description = "command.description.musicpanel", category = Category.MUSIC)
public class MusicPanel implements ICommand {

    /**
     * Constructor to subscribe to the event bus.
     */
    public MusicPanel() {
        Main.getInstance().getEventBus().subscribe(this);
    }

    /**
     * Event handler for the MusicPlayerStateChangeEvent.
     *
     * @param event the event that has been fired.
     */
    @EventHandler
    public void onMusicPlayerStateChangeEvent(MusicPlayerStateChangeEvent event) {
        Message currentMessage = ArrayUtil.musicPanelList.get(event.getGuild().getIdLong());

        if (currentMessage == null) return;

        currentMessage = currentMessage.getChannel().retrieveMessageById(currentMessage.getIdLong()).complete();

        AudioTrackInfo audioTrackInfo1 =
                event.getTrack() != null ?
                        event.getTrack().getInfo() : null;

        MessageEditBuilder messageEditBuilder = new MessageEditBuilder().applyMessage(currentMessage);

        if (messageEditBuilder.getEmbeds().isEmpty()) return;

        EmbedBuilder embedBuilder1 = new EmbedBuilder(messageEditBuilder.getEmbeds().get(0));

        if (event.getState() == MusicPlayerStateChangeEvent.State.PLAYING) {

            embedBuilder1 = embedBuilder1
                    .setImage(audioTrackInfo1 != null && (audioTrackInfo1.artworkUrl != null && !audioTrackInfo1.artworkUrl.isBlank()) ? audioTrackInfo1.artworkUrl : "https://cdna.artstation.com/p/assets/images/images/060/603/250/original/lemo-arts-imhacked12-banner-animado-3.gif?1678917688")
                    .setTitle("**" + (audioTrackInfo1 != null ? LanguageService.getByGuild(event.getGuild(), "message.music.songInfoSlim", audioTrackInfo1.title, audioTrackInfo1.author)
                            : LanguageService.getByGuild(event.getGuild(), "message.music.notPlaying")) + "**");
        } else if (event.getState() != MusicPlayerStateChangeEvent.State.QUEUE_ADD) {
            embedBuilder1 = embedBuilder1
                    .setImage("https://cdna.artstation.com/p/assets/images/images/060/603/250/original/lemo-arts-imhacked12-banner-animado-3.gif?1678917688")
                    .setTitle("**" + LanguageService.getByGuild(event.getGuild(), "message.music.notPlaying") + "**");
        }

        messageEditBuilder.setEmbeds(embedBuilder1.build());
        currentMessage.editMessage(messageEditBuilder.build()).queue();
    }

    /**
     * @inheritDoc
     */
    @Override
    public void onPerform(CommandEvent commandEvent) {
        if (!commandEvent.getMember().hasPermission(Permission.MANAGE_SERVER)) {
            commandEvent.reply(commandEvent.getResource("message.default.insufficientPermission", Permission.MANAGE_SERVER.name()), 5);
            return;
        }

        MessageCreateBuilder messageCreateBuilder = new MessageCreateBuilder();

        GuildMusicManager guildMusicManager = Main.getInstance().getMusicWorker().getGuildAudioPlayer(commandEvent.getGuild());

        AudioTrackInfo audioTrackInfo =
                guildMusicManager != null && guildMusicManager.getPlayer().getPlayingTrack() != null ?
                        guildMusicManager.getPlayer().getPlayingTrack().getInfo() : null;

        EmbedBuilder embedBuilder = new EmbedBuilder()
                .setColor(new Color(83, 4, 139))
                .setImage(audioTrackInfo != null && (audioTrackInfo.artworkUrl != null && !audioTrackInfo.artworkUrl.isBlank()) ? audioTrackInfo.artworkUrl : "https://cdna.artstation.com/p/assets/images/images/060/603/250/original/lemo-arts-imhacked12-banner-animado-3.gif?1678917688")
                .setTitle("**" + (audioTrackInfo != null ? commandEvent.getResource("message.music.songInfoSlim", audioTrackInfo.title, audioTrackInfo.author)
                        : commandEvent.getResource("message.music.notPlaying")) + "**")
                .setFooter(commandEvent.getGuild().getName() + " - " + BotConfig.getAdvertisement(), commandEvent.getGuild().getIconUrl());

        messageCreateBuilder.setEmbeds(embedBuilder.build());

        messageCreateBuilder.addActionRow(Button.of(ButtonStyle.SECONDARY, "nv_music_play", FormatUtil.PLAY_EMOJI),
                        Button.of(ButtonStyle.SECONDARY, "nv_music_pause", FormatUtil.PAUSE_EMOJI),
                        Button.of(ButtonStyle.SECONDARY, "nv_music_skip", FormatUtil.PLAY_EMOJI + FormatUtil.PLAY_EMOJI),
                        Button.of(ButtonStyle.SECONDARY, "nv_music_loop", FormatUtil.LOOP_EMOJI),
                        Button.of(ButtonStyle.SECONDARY, "nv_music_shuffle", FormatUtil.SHUFFLE_EMOJI))
                .addActionRow(Button.success("nv_music_add", commandEvent.getResource("label.queueAdd")));

        Message message;

        if (commandEvent.isSlashCommand()) {
            commandEvent.reply(commandEvent.getResource("message.default.checkBelow"));
        }

        if (commandEvent.getChannel().canTalk()) {
            message = commandEvent.getChannel().sendMessage(messageCreateBuilder.build()).complete();
        } else {
            message = null;
        }

        if (message == null) return;

        ArrayUtil.musicPanelList.remove(commandEvent.getGuild().getIdLong());
        ArrayUtil.musicPanelList.put(commandEvent.getGuild().getIdLong(), message);
    }

    /**
     * @inheritDoc
     */
    @Override
    public CommandData getCommandData() {
        return null;
    }

    /**
     * @inheritDoc
     */
    @Override
    public String[] getAlias() {
        return new String[0];
    }

    /**
     * @inheritDoc
     */
    @Override
    public EAccessLevel getCommandLevel() {
        return EAccessLevel.USER;
    }
}
