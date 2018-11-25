package org.airsonic.player.service;

import com.github.biconou.AudioPlayer.api.PlayerListener;
import org.airsonic.player.domain.Player;
import org.airsonic.player.domain.PlayerTechnology;
import org.airsonic.player.service.jukebox.JavaPlayerFactory;
import org.apache.commons.lang.StringUtils;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
class AudioPlayerRegistryService {

    private static final org.slf4j.Logger log = LoggerFactory.getLogger(AudioPlayerRegistryService.class);

    private static final float DEFAULT_GAIN = 0.75f;
    private static final String DEFAULT_MIXER_ENTRY_KEY = "_default";

    private JavaPlayerFactory javaPlayerFactory;

    private Map<Integer, com.github.biconou.AudioPlayer.api.Player> activeAudioPlayers = new HashMap<>();
    private Map<String, List<com.github.biconou.AudioPlayer.api.Player>> activeAudioPlayersPerMixer = new HashMap<>();


    AudioPlayerRegistryService(JavaPlayerFactory javaPlayerFactory) {
        this.javaPlayerFactory = javaPlayerFactory;
    }

    com.github.biconou.AudioPlayer.api.Player createAudioPlayer(Player airsonicPlayer, PlayerListener listener) {
        if (!airsonicPlayer.getTechnology().equals(PlayerTechnology.JAVA_JUKEBOX)) {
            throw new RuntimeException("The player " + airsonicPlayer.getName() + " is not a java jukebox player");
        }

        log.info("Create a new AudioPlayer for player {}", airsonicPlayer.getId());

        com.github.biconou.AudioPlayer.api.Player audioPlayer;

        if (StringUtils.isNotBlank(airsonicPlayer.getJavaJukeboxMixer())) {
            log.info("use mixer : {}", airsonicPlayer.getJavaJukeboxMixer());
            audioPlayer = javaPlayerFactory.createJavaPlayer(airsonicPlayer.getJavaJukeboxMixer());
        } else {
            log.info("use default mixer");
            audioPlayer = javaPlayerFactory.createJavaPlayer();
        }
        if (audioPlayer != null) {
            audioPlayer.setGain(DEFAULT_GAIN);
            audioPlayer.registerListener(listener);
            activeAudioPlayers.put(airsonicPlayer.getId(), audioPlayer);
            log.info("New audio player {} has been initialized.", audioPlayer.toString());
            indexPlayerPerMixer(airsonicPlayer.getJavaJukeboxMixer(), audioPlayer);
            return audioPlayer;
        } else {
            throw new RuntimeException("AudioPlayer has not been initialized properly");
        }
    }

    private void indexPlayerPerMixer(String mixer, com.github.biconou.AudioPlayer.api.Player audioPlayer) {
        if (StringUtils.isBlank(mixer)) {
            mixer = DEFAULT_MIXER_ENTRY_KEY;
        }
        List<com.github.biconou.AudioPlayer.api.Player> playersForMixer = activeAudioPlayersPerMixer.get(mixer);
        if (playersForMixer == null) {
            playersForMixer = new ArrayList<>();
            activeAudioPlayersPerMixer.put(mixer, playersForMixer);
        }
        playersForMixer.add(audioPlayer);
    }

    /**
     * Finds the corresponding active audio player for a given airsonic player.
     */
    public Optional<com.github.biconou.AudioPlayer.api.Player> retrieveAudioPlayer(Player airsonicPlayer) {
        return Optional.ofNullable(activeAudioPlayers.get(airsonicPlayer.getId()));
    }

    public Optional<List<com.github.biconou.AudioPlayer.api.Player>> allPlayersForMixer(String mixer) {
        if (StringUtils.isEmpty(mixer)) {
            mixer = DEFAULT_MIXER_ENTRY_KEY;
        }
        return Optional.ofNullable(activeAudioPlayersPerMixer.get(mixer));
    }
}
