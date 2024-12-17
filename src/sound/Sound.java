package sound;

import java.io.File;
import java.io.IOException;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;
import javax.sound.sampled.FloatControl;
import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.UnsupportedAudioFileException;
import main.Game;

public class Sound {
    Clip musicPlayer;
    Clip soundEffectPlayer;
    File[] files = new File[15];
    FloatControl musicFloatControl;
    FloatControl soundEffectFloatControl;
    Game game;

    public Sound(Game game){
        loadSounds();
        this.game = game;
    }

    private void loadSounds(){
        // Starting Menu Theme Music: index = 0
        files[0] = new File("res/music_and_sound/deltarune-chapter-1-ost-015-lantern.wav");
        // Level 1 theme Music: index = 1
        files[1] = new File("res/music_and_sound/deltarune-chapter-1-ost-013-field-of-hopes-and-dreams.wav");
        // Level 2 theme Music: index = 2
        files[2] = new File("res/music_and_sound/deltarune-chapter-1-ost-013-field-of-hopes-and-dreams.wav");
        // Game Over Music: index = 3
        files[3] = new File("res/music_and_sound/undertale-ost-038-spooktune.wav");
        // Level Completed Music: index = 4
        files[4] = new File("res/music_and_sound/deltarune-chapter-2-ost-12-when-i-get-happy-i-dance-like-this.wav");
        // Game over Sound Effect: index = 5
        files[5] = new File("res/music_and_sound/mixkit-game-over-trombone-1940.wav");
        // Button Sound Effect: index = 6
        files[6] = new File("res/music_and_sound/button_09-190435.wav");
        // Jump Sound Effect: index = 7
        files[7] = new File("res/music_and_sound/thump-105302.wav");
        // Dah Sound Effect: index = 8
        files[8] = new File("res/music_and_sound/energy-90321.wav");
        // Player got hit Sound Effect: index = 9
        files[9] = new File("res/music_and_sound/mixkit-air-in-a-hit-2161.wav");
        // Footstep Sound Effect: index = 10
        files[10] = new File("res/music_and_sound/snow-step-1-81064.wav");
        // Climbing Sound Effect: index = 11
        files[11] = new File("res/music_and_sound/snow-step-1-81064.wav");
    }

    public void setMusic(int musicNumber){
        try {
            AudioInputStream ais = AudioSystem.getAudioInputStream(files[musicNumber]);
            musicPlayer = AudioSystem.getClip();
            musicPlayer.open(ais);
            musicFloatControl = (FloatControl)musicPlayer.getControl(FloatControl.Type.MASTER_GAIN);
            updateMusic();
        } catch (IOException | LineUnavailableException | UnsupportedAudioFileException e) {
            System.out.println("Error" + e);
        }
    }

    public void setSoundEffect(int soundEffectNumber){
        try {
            AudioInputStream ais = AudioSystem.getAudioInputStream(files[soundEffectNumber]);
            soundEffectPlayer = AudioSystem.getClip();
            soundEffectPlayer.open(ais);
            soundEffectFloatControl = (FloatControl)soundEffectPlayer.getControl(FloatControl.Type.MASTER_GAIN);
            updateSoundEffect();
        } catch (IOException | LineUnavailableException | UnsupportedAudioFileException e) {
            System.out.println("Error" + e);
        }
    }

    public void playMusic(){
        musicPlayer.start();
    }

    public void loop(){
        musicPlayer.loop(Clip.LOOP_CONTINUOUSLY);
    }

    public void stopMusic(){
        musicPlayer.stop();
    }

    public void playSoundEffect(){
        soundEffectPlayer.start();
    }

    public void stopSoundEffect(){
        soundEffectPlayer.stop();
    }

    public void updateMusic(){
        float trueVolume = -80f;
        switch (game.musicVolume) {
            case 0 -> trueVolume = -80f;
            case 1 -> trueVolume = -20f;
            case 2 -> trueVolume = -12f;
            case 3 -> trueVolume = -5f;
            case 4 -> trueVolume = 1f;
            case 5 -> trueVolume = 6f;
        }
        musicFloatControl.setValue(trueVolume);
    }

    public void updateSoundEffect(){
        float trueVolume = -80f;
        switch (game.soundEffectVolume) {
            case 0 -> trueVolume = -80f;
            case 1 -> trueVolume = -20f;
            case 2 -> trueVolume = -12f;
            case 3 -> trueVolume = -5f;
            case 4 -> trueVolume = 1f;
            case 5 -> trueVolume = 6f;
        }
        soundEffectFloatControl.setValue(trueVolume);
    }

    public void update(){
        updateMusic();
        updateSoundEffect();
    }
}
