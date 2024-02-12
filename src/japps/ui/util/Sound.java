/*
 * Copyright (C) 2018 Williams Lopez - JApps
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package japps.ui.util;

import japps.ui.component.Dialogs;
import japps.ui.component.Label;
import static japps.ui.util.Resources.$;
import java.awt.Color;
import java.net.URL;
import java.nio.file.Path;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;
import javax.sound.sampled.DataLine;
import javax.sound.sampled.LineEvent;
import javax.swing.ImageIcon;
import javax.swing.JDialog;

/**
 * 
 * Control a singleton for a wav player
 *
 * @author Williams Lopez - JApps
 */
public class Sound {
    
    private static Clip clip;
    private static JDialog dialog;
    
    /**
     * Plays a sound in specified path
     * @param sound
     * @throws Exception 
     */
    public static void play(Path sound) throws Exception{
        
        if(clip!=null && (clip.isRunning() || clip.isActive())){
            clip.close();
        }
        Log.debug("playing audio "+sound+ " - "+AudioSystem.getAudioFileFormat(sound.toFile()));
        AudioInputStream is = AudioSystem.getAudioInputStream(sound.toFile());
        DataLine.Info info = new DataLine.Info(Clip.class, is.getFormat());
        clip = (Clip) AudioSystem.getLine(info);
        clip.open(is);
        clip.start();
    }
    
    public static void playInWindow(Path sound) {
        try {
            if (dialog == null) {
                URL imageURL = Resources.class.getResource("/res/img/audio-spectrum.gif");
                ImageIcon image = new ImageIcon(imageURL);
                Label label = new Label();
                label.setIcon(image);
                dialog = Dialogs.create(label, $("Talking"), true, (e) -> {
                    stop();
                });
                dialog.setBackground(Color.black);
            }
            play(sound);
            clip.addLineListener((LineEvent event) -> {
                if (event.getType() == LineEvent.Type.STOP) {
                    dialog.setVisible(false);
                }
            });
            dialog.pack();
            dialog.setVisible(true);
        } catch (Exception e) {
            Log.error("Error playing sound", e);
        }
    }
    
    /**
     * Stops the current song if exists and if playing
     */
    public static void stop(){
        if(clip != null && clip.isRunning()){
            clip.stop();
            clip.close();
        }
    }
    
    
    

}
