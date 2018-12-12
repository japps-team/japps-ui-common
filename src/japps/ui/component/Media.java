/*
 * Copyright (C) 2018 Williams Lopez - JApps
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License
 * as published by the Free Software Foundation; either version 2
 * of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place - Suite 330, Boston, MA  02111-1307, USA.
 */
package japps.ui.component;

import japps.ui.DesktopApp;
import static japps.ui.component.Media.VIDEO;
import static japps.ui.util.Resources.*;
import japps.ui.util.Util;
import java.awt.Component;
import java.awt.ComponentOrientation;
import java.awt.Dimension;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.nio.file.Path;
import javax.swing.SwingUtilities;
import uk.co.caprica.vlcj.component.EmbeddedMediaPlayerComponent;
import uk.co.caprica.vlcj.player.MediaPlayer;
import uk.co.caprica.vlcj.player.MediaPlayerEventAdapter;
import uk.co.caprica.vlcj.player.MediaPlayerEventListener;



/**
 * 
 * This is a panel media player it supports Images, videos and sounds
 * for sounds and videos it shows some controls
 *
 * @author Williams Lopez - JApps
 */
public class Media extends Panel{
    
    /**
     * VIDEO media type
     */
    public static final int VIDEO = 1;
    /**
     * IMAGE Media type
     */
    public static final int IMAGE = 2;
    /**
     * Sound media type
     */
    public static final int SOUND = 3;

    private Path media;
    private EmbeddedMediaPlayerComponent player;
    
    private Panel panelControls;
    private Button btnPlay;
    private Button btnPause;
    private Button btnStop;
    
    private Label lblMediaType;
    
    private ImageComponent imageComponent;
    
    private int mediaType = 0;
    
    private MediaPlayerEventListener mediaListener;
    
    /**
     * Creates a new media panel
     */
    public Media() {
        imageComponent = new ImageComponent();
        lblMediaType = new Label();
        panelControls = new Panel();
        btnPlay = new Button((e)->{ play(); });
        btnPause = new Button((e)->{ pause();});
        btnStop = new Button((e)->{stop();});
        btnPlay.setIcon("arrow-play.png",25,25);
        btnPause.setIcon("pause.png",25,25);
        btnStop.setIcon("stop.png",25,25);
        btnPause.setEnabled(false);
        btnStop.setEnabled(false);
        
        panelControls.setComponents(new Component[][]{
            {null,btnPlay,btnPause,btnStop }
        });

        
        mediaListener = new MediaPlayerEventAdapter(){

            @Override
            public void playing(MediaPlayer mediaPlayer) {
                btnPlay.setEnabled(false);
                btnPause.setEnabled(true);
                btnStop.setEnabled(true);
            }

            @Override
            public void stopped(MediaPlayer mediaPlayer) {
                btnPlay.setEnabled(true);
                btnPause.setEnabled(false);
                btnStop.setEnabled(false);
            }

            @Override
            public void paused(MediaPlayer mediaPlayer) {
                btnPlay.setEnabled(true);
                btnPause.setEnabled(false);
                btnStop.setEnabled(true);
            }

            @Override
            public void finished(MediaPlayer mediaPlayer) {
                btnPlay.setEnabled(true);
                btnPause.setEnabled(false);
                btnStop.setEnabled(false);
            }

            
        };
        

        DesktopApp.APP.getMainWindow().addWindowListener(new WindowAdapter() {

            @Override
            public void windowClosing(WindowEvent e) {
                if(player!=null){
                    player.release();
                }
            }
            
        });
        
    }
    
    /**
     * Sets the media in the component
     * 
     * @param path path to the media
     * @param mediaType media type IMAGE, VIDEO, SOUND
     */
    public void setMedia(Path path, int mediaType){
/*
        if(player.getMediaPlayer().isPlaying()){
                player.getMediaPlayer().stop();
        }
        
        
        
        this.mediaType = mediaType;
        this.media = path;
        
        switch(mediaType){
            case IMAGE:
                imageComponent.setImage(Util.readImage(this.media));
                setComponents(new java.awt.Component[][]{ {imageComponent} });
                break;
            case VIDEO:
                lblMediaType.setIcon("movie.png",35,35);
                setComponents(new java.awt.Component[][]{
                    {player,player},
                    {panelControls,lblMediaType}
                   },
                   new String[]{ FILL_GROW_CENTER, RIGHT },
                   new String[]{ FILL_GROW_CENTER , NONE }
                );
                player.getMediaPlayer().prepareMedia(this.media.toAbsolutePath().toString());
                player.revalidate();
                break;
            case SOUND:
                lblMediaType.setIcon("headset.png",35,35);
                player.setPreferredSize(new Dimension(100, 50));
                setComponents(new java.awt.Component[][]{
                    {player,player},
                    {panelControls,lblMediaType}
                   },
                   new String[]{ FILL_GROW_CENTER, RIGHT },
                   new String[]{ FILL_GROW_CENTER , NONE }
                );
                player.getMediaPlayer().prepareMedia(this.media.toAbsolutePath().toString());
                player.revalidate();
                break;
        }
        SwingUtilities.invokeLater(()->{ revalidate(); });
  */    
        
        if(this.player!= null && this.player.getMediaPlayer().isPlaying()){
            this.player.getMediaPlayer().stop();
        }
        
        if(this.mediaType != mediaType){
            
            if(this.player != null) player.release();
            
            switch(mediaType){
                case IMAGE: setComponents(new java.awt.Component[][]{ {imageComponent} }); break;
                case VIDEO: case SOUND:
                    player = new uk.co.caprica.vlcj.component.EmbeddedMediaPlayerComponent();
                    player.getMediaPlayer().addMediaPlayerEventListener(mediaListener);
                    setComponents(new java.awt.Component[][]{
                        {player, player},
                        {panelControls, lblMediaType}
                    },
                            new String[]{FILL_GROW_CENTER, RIGHT},
                            new String[]{FILL_GROW_CENTER, NONE}
                    );
                    if(mediaType == VIDEO) lblMediaType.setIcon("movie.png", 35, 35);
                    if(mediaType == SOUND) lblMediaType.setIcon("headset.png", 35, 35);
                    break;
            }
        }
        
        this.mediaType = mediaType;
        this.media = path;
        
        
        switch(mediaType){
            case IMAGE: imageComponent.setImage(Util.readImage(this.media)); break;
            case VIDEO: case SOUND: player.getMediaPlayer().prepareMedia(this.media.toAbsolutePath().toString()); break; 
        }
        
        SwingUtilities.invokeLater(()->{ Media.this.revalidate(); Media.this.repaint(); });
    }
    
    /**
     * Get the path of media source
     * @return 
     */
    public Path getMedia(){
        return this.media;
    }
    
    /**
     * Play the component media
     */
    public void play(){
        if(player!=null && player.isShowing()){
            player.getMediaPlayer().play();
        }
    }
    
    /**
     * Pause the media
     */
    public void pause(){    
        if(player!=null){
            player.getMediaPlayer().pause();
        }
    }
    
    /**
     * Stops media
     */
    public void stop(){
        if(player!= null && player.getMediaPlayer().isPlaying()){
            player.getMediaPlayer().stop();
        }
    }
    
    /**
     * Get current media type
     * @return 
     */
    public int getMediaType(){
        return mediaType;
    }

    @Override
    public boolean isOptimizedDrawingEnabled() {
        return false;
    }
    
    /**
     * Enable/disable media controls
     * @param visible 
     */
    public void setVisibleControls(boolean visible){
        this.panelControls.setVisible(visible);
    }
    
    /**
     * Release media resources
     */
    public void release(){
        if(player!=null){
            this.player.release();
        }
    }
    
}
