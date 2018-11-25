package org.airsonic.player.service;

import com.github.biconou.AudioPlayer.JavaPlayer;
import com.github.biconou.AudioPlayer.api.PlayerListener;
import org.airsonic.player.domain.Player;
import org.airsonic.player.domain.PlayerTechnology;
import org.airsonic.player.service.jukebox.JavaPlayerFactory;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import java.util.Optional;

import static org.mockito.Mockito.*;
import static org.assertj.core.api.Assertions.*;


@RunWith(value = MockitoJUnitRunner.class)
public class AudioPlayerRegistryServiceUnitTest {

    @Mock
    private JavaPlayerFactory javaPlayerFactory;
    private AudioPlayerRegistryService registry;
    @Mock
    private Player airsonicPlayer;
    @Mock
    private PlayerListener listener;

    @Before
    public void setup() {
        registry = new AudioPlayerRegistryService(javaPlayerFactory);
        when(airsonicPlayer.getTechnology()).thenReturn(PlayerTechnology.JAVA_JUKEBOX);
        when(javaPlayerFactory.createJavaPlayer()).thenReturn(new JavaPlayer());
        when(javaPlayerFactory.createJavaPlayer("mixer")).thenReturn(new JavaPlayer());
    }

    @Test
    public void createAudioPlayer() {
        // When
        registry.createAudioPlayer(airsonicPlayer, listener);
        Optional<com.github.biconou.AudioPlayer.api.Player> player = registry.retrieveAudioPlayer(airsonicPlayer);
        // Then
        assertThat(player.get()).isNotNull();
        assertThat(registry.allPlayersForMixer(null).get().size()).isEqualTo(1);
    }

    @Test
    public void createAudioPlayerForMixer() {
        // When
        when(airsonicPlayer.getJavaJukeboxMixer()).thenReturn("mixer");
        registry.createAudioPlayer(airsonicPlayer, listener);
        Optional<com.github.biconou.AudioPlayer.api.Player> player = registry.retrieveAudioPlayer(airsonicPlayer);
        // Then
        assertThat(player.get()).isNotNull();
        assertThat(registry.allPlayersForMixer("mixer").get().size()).isEqualTo(1);
    }

    @Test(expected = RuntimeException.class)
    public void createAudioPlayerNonJukebox() {
        when(airsonicPlayer.getTechnology()).thenReturn(PlayerTechnology.WEB);
        registry.createAudioPlayer(airsonicPlayer, listener);
    }

    @Test(expected = RuntimeException.class)
    public void createAudioPlayerFailed() {
        when(javaPlayerFactory.createJavaPlayer()).thenReturn(null);
        registry.createAudioPlayer(airsonicPlayer, listener);
    }

}
