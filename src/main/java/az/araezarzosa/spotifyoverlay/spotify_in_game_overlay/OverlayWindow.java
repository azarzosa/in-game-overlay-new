package az.araezarzosa.spotifyoverlay.spotify_in_game_overlay;


import java.awt.*;
import java.awt.event.*;
import java.awt.geom.RoundRectangle2D;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import javax.imageio.ImageIO;
import javax.swing.*;
import javax.swing.border.LineBorder;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.event.MouseInputAdapter;
import javax.swing.plaf.basic.BasicSliderUI;

import se.michaelthelin.spotify.model_objects.miscellaneous.CurrentlyPlaying;
import se.michaelthelin.spotify.model_objects.miscellaneous.CurrentlyPlayingContext;
import se.michaelthelin.spotify.model_objects.specification.Track;

public class OverlayWindow {
	
    private static final int MARGIN = 50; // Margin in pixels
    private static JFrame frame;
    private static int x = MARGIN;
    private static int y = MARGIN;
    private static Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
    private static int width = 350;
    private static int height = 150;
    private static Color barColor = new Color(29, 185, 84);
    
    private static int albumArtSize = 100;
    private static int progressWidth = 200;
    private static int progressHeight = 10;
    private static int volumeWidth = 100;
    private static int volumeHeight = 30;
    private static int buttonSize = 25;
    private static int volumeOffset = 10;
    private static int barOffset = 15;
    
    private static JPanel contentPanel;
    private static JLabel albumArtLabel;
    private static JPanel albumArtPanel;
    private static JLabel songNameLabel;
    private static JLabel artistNameLabel;
    private static BasicSliderUI progressSliderUI;
    private static BasicSliderUI volumeSliderUI;
    private static JSlider progressBar;
    private static JButton previousButton;
    private static JButton pauseButton;
    private static JButton nextButton;
    private static JSlider volumeSlider;
    private static String positionString = "Top Left";

    private static final int VOLUME_MIN = 0;
    private static final int VOLUME_MAX = 100;
    private static final int VOLUME_INIT = 100;
    
    private static boolean isPlaying;
    private static Track currentTrack;
    private static CurrentlyPlaying currentlyPlaying = App.getUsersCurrentlyPlayingTrack();
    private static CurrentlyPlayingContext currentInfo = App.getUsersCurrentPlaybackInfo();
    private static BufferedImage currentArt;
    
    private static final int TRACK_CHANGE_UPDATE_INTERVAL = 2000;
    private static final int TRACK_PLAYING_UPDATE_INTERVAL = 500;
    private static final int TRACK_PROGRESS_CHANGE_UPDATE_INTERVAL = 500;
    
    private static ChangeListener progressListener;
    private static ScheduledExecutorService TrackChangeExecutor;
    private static ScheduledExecutorService TrackPlayingExecutor;
    private static ScheduledExecutorService TrackProgressChangeExecutor;

    public static void showOverlay() {
        // Check if translucency is supported
        GraphicsEnvironment ge = GraphicsEnvironment.getLocalGraphicsEnvironment();
        GraphicsDevice gd = ge.getDefaultScreenDevice();

        GraphicsConfiguration gc = gd.getDefaultConfiguration();
        boolean isTranslucencySupported = gd.isWindowTranslucencySupported(GraphicsDevice.WindowTranslucency.TRANSLUCENT);

        if (isTranslucencySupported) {
            // Create the overlay window
            frame = new JFrame(gc);
            URL iconURL = App.class.getResource("icon.png");
            ImageIcon icon = new ImageIcon(iconURL);
            Image iconImage = icon.getImage();
            Image resizedImage = iconImage.getScaledInstance(32, 32, Image.SCALE_SMOOTH);
            frame.setIconImage(resizedImage);
            frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            frame.setUndecorated(true); // Remove window decorations
            frame.setBackground(new Color(0, 0, 0, 0)); // Set transparent background

            // Add a window listener to track fullscreen changes
            frame.addWindowListener(new WindowAdapter() {
                @Override
                public void windowStateChanged(WindowEvent e) {
                    if ((e.getNewState() & Frame.MAXIMIZED_BOTH) == Frame.MAXIMIZED_BOTH) {
                        // Fullscreen state is active
                        frame.setVisible(true);
                    } else {
                        // Fullscreen state is inactive
                        frame.setVisible(false);
                    }
                }
            });

            /**int x = screenSize.width - width - MARGIN;
            int y = MARGIN;**/
            int x = MARGIN;
            int y = MARGIN;
            frame.setBounds(x, y, width, height);
            
            frame.setShape(new RoundRectangle2D.Double(0, 0, width, height, 20, 20));
            frame.setOpacity(1f);
            frame.setAlwaysOnTop(true);
            
            

            // Create the content panel with a gray background
            contentPanel = new JPanel() {
                @Override
                protected void paintComponent(Graphics g) {
                    super.paintComponent(g);
                    g.setColor(new Color(25, 20, 20));
                    g.fillRect(0, 0, getWidth(), getHeight());
                }
            };
            contentPanel.setLayout(null); // Use absolute positioning
            
            
            // Create the album art panel (square) with a white background
            albumArtPanel = new JPanel() {
                @Override
                protected void paintComponent(Graphics g) {
                    super.paintComponent(g);
                    g.setColor(Color.WHITE);
                    g.fillRect(0, 0, getWidth(), getHeight());
                }
            };
            albumArtPanel.setBounds(10, (height - albumArtSize) / 2, albumArtSize, albumArtSize);
            contentPanel.add(albumArtPanel);

            // Create the song name label
            songNameLabel = new JLabel("Song Name");
		    songNameLabel.setFont(new Font("Circular", Font.BOLD, 12));
            songNameLabel.setForeground(Color.WHITE);
            songNameLabel.setBounds(albumArtPanel.getWidth() + 20, albumArtPanel.getY(), progressWidth, 20);
            contentPanel.add(songNameLabel);
            
            //songNameLabel.add

            // Create the artist name label
            artistNameLabel = new JLabel("Artist Name");
		    artistNameLabel.setFont(new Font("Circular", Font.PLAIN, 12));
            artistNameLabel.setForeground(Color.WHITE);
            artistNameLabel.setBounds(albumArtPanel.getWidth() + 20, songNameLabel.getY() + songNameLabel.getHeight(), progressWidth, 20);
            contentPanel.add(artistNameLabel);
            
            progressSliderUI = new BasicSliderUI(null) {
                @Override
                public void paintTrack(Graphics g) {
                    Graphics2D g2 = (Graphics2D) g;
                    Rectangle trackBounds = trackRect;

                    // Set the left side of the track as green
                    g2.setColor(barColor);
                    g2.fillRect(trackBounds.x, trackBounds.y, thumbRect.x - trackBounds.x, trackBounds.height);

                    // Set the right side of the track as gray
                    g2.setColor(Color.GRAY);
                    g2.fillRect(thumbRect.x, trackBounds.y, trackBounds.width, trackBounds.height);
                }

                @Override
                public void paintThumb(Graphics g) {
                    Graphics2D g2 = (Graphics2D) g;
                    Rectangle thumbBounds = thumbRect;
                    
                    // Set the thumb color as white
                    g2.setColor(Color.WHITE);
                    g2.fillRect(thumbBounds.x, thumbBounds.y, thumbBounds.width, thumbBounds.height);
                }

            };
            
            
            // Create the progress bar with a green fill
            progressBar = new JSlider(JSlider.HORIZONTAL, 0, 100, 0);
            progressBar.setPreferredSize(new Dimension(progressWidth, progressHeight));
            progressBar.setBounds(albumArtPanel.getWidth() + 23, artistNameLabel.getY() + artistNameLabel.getHeight() + 10, progressWidth, progressHeight);
            progressBar.setFocusable(false);
            progressBar.setOpaque(false);
            progressBar.setUI(progressSliderUI);
            progressBar.setBackground(Color.BLACK);
            progressBar.setForeground(Color.GREEN);
            contentPanel.add(progressBar);
            
            progressListener = new ChangeListener() {
            	@Override
                public void stateChanged(ChangeEvent e) {
	            	if (!progressBar.getValueIsAdjusting()) {
			            int newPosition = progressBar.getValue();
			            App.seekToPositionInCurrentlyPlayingTrack(newPosition);
			        }
            	}
		    };
		    
		    progressBar.addChangeListener(progressListener);

         // Create the previous button
            previousButton = new JButton("⏮");
            previousButton.setBounds(albumArtPanel.getWidth() + 25, progressBar.getY() + progressBar.getHeight() + 12, buttonSize, buttonSize);

            previousButton.setBorder(new LineBorder(Color.WHITE));
            previousButton.setContentAreaFilled(false);
            previousButton.setBackground(new Color(25, 20, 20));
            previousButton.setForeground(Color.WHITE);
            
            previousButton.addMouseListener(new MouseAdapter() {
                @Override
                public void mouseEntered(MouseEvent e) {
                	SwingUtilities.invokeLater(() -> {
                        previousButton.setForeground(barColor);
                        previousButton.setBorder(new LineBorder(barColor));
            		});
                }
                @Override
                public void mouseExited(MouseEvent e) {
                    previousButton.setForeground(Color.WHITE); // Restore the original image when not hovered
                    previousButton.setBorder(new LineBorder(Color.WHITE));
                }
            });
            
            previousButton.addActionListener(new ActionListener() {
            	public void actionPerformed(ActionEvent e) {
            		App.skipUsersPlaybackToPreviousTrack();
            	}
            });
            
            contentPanel.add(previousButton);

            // Create the pause button
            if(currentInfo != null) {
	            if(currentInfo.getIs_playing() || currentlyPlaying.getIs_playing()) {
	            	pauseButton = new JButton("⏸");
	            	isPlaying = true;
	            }else if(!currentInfo.getIs_playing() || !currentlyPlaying.getIs_playing()) {
	            	pauseButton = new JButton("⏵");
	            	isPlaying = false;
	            }
            }
            else {
            	pauseButton = new JButton("⏵");
            	isPlaying = false;
            }
            pauseButton.setBounds(previousButton.getX() + buttonSize + 5, previousButton.getY(), buttonSize, buttonSize);

            pauseButton.setBorder(new LineBorder(Color.WHITE));
            pauseButton.setContentAreaFilled(false);
            pauseButton.setBackground(new Color(25, 20, 20));
            pauseButton.setForeground(Color.WHITE);
            
            pauseButton.addMouseListener(new MouseAdapter() {
                @Override
                public void mouseEntered(MouseEvent e) {
                	SwingUtilities.invokeLater(() -> {
                		pauseButton.setForeground(barColor);
                		pauseButton.setBorder(new LineBorder(barColor));
            		});
                }
                @Override
                public void mouseExited(MouseEvent e) {
                	pauseButton.setForeground(Color.WHITE); // Restore the original image when not hovered
            		pauseButton.setBorder(new LineBorder(Color.WHITE));
                }
            });
            
            pauseButton.addActionListener(new ActionListener() {
            	public void actionPerformed(ActionEvent e) {
            		if(isPlaying) {
            			App.pauseUsersPlayback();
            			isPlaying = false;
            			SwingUtilities.invokeLater(() -> {
                    		pauseButton.setText("⏵");
                		});
            		}
            		else {
            			App.startResumeUsersPlayback();
            			isPlaying = true;
            			SwingUtilities.invokeLater(() -> {
                    		pauseButton.setText("⏸");
                		});
            		}
            	}
            });
            
            
            
            contentPanel.add(pauseButton);

            // Create the next button
            nextButton = new JButton("⏭");
            nextButton.setBounds(pauseButton.getX() + buttonSize + 5, pauseButton.getY(), buttonSize, buttonSize);
            ImageIcon buttonImageIconNext = new ImageIcon("skip.png");
            Image buttonImageNext = buttonImageIconNext.getImage();

            // Resize the image to fit the button
            Image resizedButtonImageNext = buttonImageNext.getScaledInstance(buttonSize, buttonSize, Image.SCALE_SMOOTH);

            // Create a new ImageIcon with the resized image
            ImageIcon resizedButtonIconNext = new ImageIcon(resizedButtonImageNext);

            // Set the resized image as the button's icon
            nextButton.setIcon(resizedButtonIconNext);
            
            nextButton.setBorder(new LineBorder(Color.WHITE));
            nextButton.setContentAreaFilled(false);
            nextButton.setBackground(new Color(25, 20, 20));
            nextButton.setForeground(Color.WHITE);
            
            nextButton.addMouseListener(new MouseAdapter() {
                @Override
                public void mouseEntered(MouseEvent e) {
                	SwingUtilities.invokeLater(() -> {
                		nextButton.setForeground(barColor);
                		nextButton.setBorder(new LineBorder(barColor));
            		});
                }
                @Override
                public void mouseExited(MouseEvent e) {
                	nextButton.setForeground(Color.WHITE); // Restore the original image when not hovered
            		nextButton.setBorder(new LineBorder(Color.WHITE));
                }
            });
            
            nextButton.addActionListener(new ActionListener() {
            	public void actionPerformed(ActionEvent e) {
            		App.skipUsersPlaybackToNextTrack();
            	}
            });
            
            contentPanel.add(nextButton);

            volumeSliderUI = new BasicSliderUI(null) {
                @Override
                public void paintTrack(Graphics g) {
                    Graphics2D g2 = (Graphics2D) g;
                    Rectangle trackBounds = trackRect;

                    // Set the left side of the track as green
                    g2.setColor(barColor);
                    g2.fillRect(trackBounds.x, trackBounds.y, thumbRect.x - trackBounds.x, trackBounds.height);

                    // Set the right side of the track as gray
                    g2.setColor(Color.GRAY);
                    g2.fillRect(thumbRect.x, trackBounds.y, trackBounds.width, trackBounds.height);
                }

                @Override
                public void paintThumb(Graphics g) {
                    Graphics2D g2 = (Graphics2D) g;
                    Rectangle thumbBounds = thumbRect;
                    
                    // Set the thumb color as white
                    g2.setColor(Color.WHITE);
                    g2.fillRect(thumbBounds.x, thumbBounds.y, thumbBounds.width, thumbBounds.height);
                }

            };
            
            // Create the volume slider
            if(currentInfo != null) {
            	volumeSlider = new JSlider(JSlider.HORIZONTAL, VOLUME_MIN, VOLUME_MAX, currentInfo.getDevice().getVolume_percent());
            }else {
            	volumeSlider = new JSlider(JSlider.HORIZONTAL, VOLUME_MIN, VOLUME_MAX, VOLUME_INIT);
                App.setVolumePlayback(VOLUME_INIT);
            }
            volumeSlider.setBounds(nextButton.getX() + buttonSize + 10, progressBar.getY() + progressBar.getHeight() + volumeOffset, volumeWidth, volumeHeight);
            volumeSlider.setUI(volumeSliderUI);
            volumeSlider.setEnabled(isTranslucencySupported);
            volumeSlider.setBackground(new Color(25, 20, 20));
            volumeSlider.setForeground(Color.WHITE);
            contentPanel.add(volumeSlider);
            
            volumeSlider.addChangeListener(e -> {
		        JSlider source = (JSlider) e.getSource();
		        if (!source.getValueIsAdjusting()) {
		            int volume = (int) source.getValue(); 
		            App.setVolumePlayback(volume);
		        }
		    });
            

            // Add the content panel to the frame
            frame.getContentPane().add(contentPanel);

            // Show the window
            frame.setVisible(true);
            //startTrackUpdateTimer();
            
            // TODO ADD LISTENERS
            startTrackChangeUpdateExecutor();
            startTrackPlayingExecutor();
            startTrackProgressExecutor();
            
            Runtime.getRuntime().addShutdownHook(new Thread() {
                @Override
                public void run() {
                    stopExecutorServices();
                }
            });
            
        } else {
            // Translucency is not supported, handle accordingly
            System.out.println("Translucency is not supported on this system.");
        }
    }
    
    public static void setWindowOpacity(float newOpacity) {
    	SwingUtilities.invokeLater(() -> {
            frame.setOpacity(newOpacity);
		});
    }
    
    public static void setWindowPosition(int newX, int newY) {
    	x = newX;
        y = newY;
        SwingUtilities.invokeLater(() -> {
            frame.setBounds(x, y, width, height);
		});
    }
    
    public static void setWindowSize(String size) {
    	SwingUtilities.invokeLater(() -> {
    		switch (size) {
            case "Small":
            	width = (int) Math.round(350 - (350 * 0.2) - 15);
            	height = (int) Math.round(150 - (150 * 0.2));
            	frame.setShape(new RoundRectangle2D.Double(0, 0, width, height, 10, 10));
    		    songNameLabel.setFont(new Font("Circular", Font.BOLD, 10));
    		    artistNameLabel.setFont(new Font("Circular", Font.PLAIN, 10));
                albumArtSize = 80;
                if(currentArt != null) {
                	albumArtLabel = new JLabel(new ImageIcon(currentArt.getScaledInstance(albumArtSize, albumArtSize, Image.SCALE_SMOOTH)));
                }
                progressWidth = 160;
                progressHeight = 6;
                volumeWidth = 80;
                volumeHeight = 8;
                buttonSize = 20;
                volumeOffset = 8;
                barOffset = 15;
                break;
            case "Medium":
            	width = 350;
            	height = 150;
            	frame.setShape(new RoundRectangle2D.Double(0, 0, width, height, 20, 20));
    		    songNameLabel.setFont(new Font("Circular", Font.BOLD, 12));
    		    artistNameLabel.setFont(new Font("Circular", Font.PLAIN, 12));
                albumArtSize = 100;
                if(currentArt != null) {
                	albumArtLabel = new JLabel(new ImageIcon(currentArt.getScaledInstance(albumArtSize, albumArtSize, Image.SCALE_SMOOTH)));
                }
                progressWidth = 200;
                progressHeight = 10;
                volumeWidth = 100;
                volumeHeight = 30;
                buttonSize = 30;
                volumeOffset = 10;
                barOffset = 20;
                break;
            case "Large":
            	width = (int) Math.round(350 + (350 * 0.2));
            	height = (int) Math.round(140 + (140 * 0.2));
            	frame.setShape(new RoundRectangle2D.Double(0, 0, width, height, 30, 30));
    		    songNameLabel.setFont(new Font("Circular", Font.BOLD, 14));
    		    artistNameLabel.setFont(new Font("Circular", Font.PLAIN, 14));
                albumArtSize = 120;
                if(currentArt != null) {
                	albumArtLabel = new JLabel(new ImageIcon(currentArt.getScaledInstance(albumArtSize, albumArtSize, Image.SCALE_SMOOTH)));
                }
                progressWidth = 240;
                progressHeight = 12;
                volumeWidth = 120;
                volumeHeight = 36;
                buttonSize = 36;
                volumeOffset = 12;
                barOffset = 20;
                break;
            default:
                break;
    		}
    		
    		switch (positionString) {
	            case "Top Left":
	                setWindowPosition(50, 50);
	                break;
	            case "Top Right":
	                setWindowPosition(OverlayWindow.screenSize.width - OverlayWindow.width - 50, 50);
	                break;
	            case "Top Center":
	                setWindowPosition((OverlayWindow.screenSize.width - OverlayWindow.width) / 2, 50);
	                break;
	            case "Bottom Left":
	                setWindowPosition(50, OverlayWindow.screenSize.height - OverlayWindow.height - 50);
	                break;
	            case "Bottom Right":
	                setWindowPosition(OverlayWindow.screenSize.width - OverlayWindow.width - 50, OverlayWindow.screenSize.height - OverlayWindow.height - 50);
	                break;
	            case "Bottom Center":
	                setWindowPosition((OverlayWindow.screenSize.width - OverlayWindow.width) / 2, OverlayWindow.screenSize.height - OverlayWindow.height - 50);
	                break;
	            default:
	                break;
            }
    		
            albumArtPanel.setBounds(10, (height - albumArtSize) / 2, albumArtSize, albumArtSize);
            songNameLabel.setBounds(albumArtPanel.getWidth() + 20, albumArtPanel.getY(), progressWidth, 20);
            artistNameLabel.setBounds(albumArtPanel.getWidth() + 20, songNameLabel.getY() + songNameLabel.getHeight(), progressWidth, 20);
            progressBar.setBounds(albumArtPanel.getWidth() + barOffset, artistNameLabel.getY() + artistNameLabel.getHeight() + 10, progressWidth, progressHeight);
            previousButton.setBounds(albumArtPanel.getWidth() + 20, progressBar.getY() + progressBar.getHeight() + 10, buttonSize, buttonSize);
            pauseButton.setBounds(previousButton.getX() + buttonSize + 5, previousButton.getY(), buttonSize, buttonSize);
            nextButton.setBounds(pauseButton.getX() + buttonSize + 5, pauseButton.getY(), buttonSize, buttonSize);
            volumeSlider.setBounds(nextButton.getX() + buttonSize + 10, progressBar.getY() + progressBar.getHeight() + volumeOffset, volumeWidth, volumeHeight);
            if(currentArt != null) {
            	albumArtLabel.setBounds(0, -1, albumArtSize, albumArtSize);
    	    	albumArtPanel.removeAll();
    	    	albumArtPanel.setLayout(null);
    	        albumArtPanel.add(albumArtLabel);

    	        // Optional: Call revalidate() and repaint() on the albumArtPanel to update the layout and repaint
    	        albumArtPanel.revalidate();
    	        albumArtPanel.repaint();
            }
    		
		});
    }
    public static Dimension getScreenSize() {
    	return screenSize;
    }
    public static int getWidth() {
    	return width;
    }
    public static int getHeight() {
    	return height;
    }
    public static String getPositionString() {
    	return positionString;
    }
    public static void setPositionString(String string) {
    	if(string != null) {
    		positionString = string;
    	}
    	return;
    }
    
    public static void setAlbumArt(BufferedImage image) {
    	if(image != null) {
    		SwingUtilities.invokeLater(() -> {
    			albumArtLabel = new JLabel(new ImageIcon(image.getScaledInstance(albumArtSize, albumArtSize, Image.SCALE_SMOOTH)));
    	        albumArtLabel.setBounds(0, -1, albumArtSize, albumArtSize);
    	    	albumArtPanel.removeAll();
    	    	albumArtPanel.setLayout(null);
    	        albumArtPanel.add(albumArtLabel);

    	        // Optional: Call revalidate() and repaint() on the albumArtPanel to update the layout and repaint
    	        albumArtPanel.revalidate();
    	        albumArtPanel.repaint();
    	    });
    	}
    }
    
    public static void setSongName(String name) {
    	if(name != null) {
    		SwingUtilities.invokeLater(() -> {
    	        songNameLabel.setText(name);
    	    });
    	}
    	return;
    }
    public static void setArtistName(String name) {
    	if(name != null) {
    		SwingUtilities.invokeLater(() -> {
    	        artistNameLabel.setText(name);
    	    });
    	}
    	return;
    }
    public static void setProgress(int progress) {
    	SwingUtilities.invokeLater(() -> {
    		progressBar.removeChangeListener(progressListener);
            progressBar.setValue(progress);
            progressBar.addChangeListener(progressListener);
	    });
    }
    public static void setProgressMax(int max) {
        SwingUtilities.invokeLater(() -> {
    		progressBar.removeChangeListener(progressListener);
            progressBar.setMaximum(max);
            progressBar.addChangeListener(progressListener);
	    });
    }
    public static void setVolume(int volume) {
    	SwingUtilities.invokeLater(() -> {
    		volumeSlider.setValue(volume);
	    });
    }
    
    public static void startTrackChangeUpdateExecutor() {
    	TrackChangeExecutor = Executors.newSingleThreadScheduledExecutor();
    	TrackChangeExecutor.scheduleAtFixedRate(() -> updateTrackChange(), 0, TRACK_CHANGE_UPDATE_INTERVAL, TimeUnit.MILLISECONDS);
    }

    private static void updateTrackChange() {
    	CurrentlyPlaying currentlyPlayingUpdate = App.getUsersCurrentlyPlayingTrack();
    	try {
	    	if(currentlyPlayingUpdate != null) {
	    		if(currentlyPlayingUpdate.getItem() != null) {
	    			if(!currentlyPlaying.equals(currentlyPlayingUpdate)) {
	    				currentlyPlaying = currentlyPlayingUpdate;
		            	currentTrack = App.getTrack(currentlyPlayingUpdate.getItem().getId());
		                try {
							URL imageURL = new URL(currentTrack.getAlbum().getImages()[0].getUrl());
							currentArt = ImageIO.read(imageURL);
			                String trackName = currentTrack.getName();
			                String artistName = currentTrack.getArtists()[0].getName();
			                int songLength = currentTrack.getDurationMs();
			                int songPosition = currentlyPlayingUpdate.getProgress_ms();
		
			                // Update the song name label
			                setAlbumArt(currentArt);
			                setSongName(trackName);
			                setArtistName(artistName);
			                setProgressMax(songLength);
			                setProgress(songPosition);
						} catch (IOException e1) {
							// TODO Auto-generated catch block
							e1.printStackTrace();
						}
	    			}
	    		}
	    	}
    	} catch(NullPointerException e1) {
    		System.out.println("Null Track");
    	}
    }
    
    private static void startTrackPlayingExecutor() {
    	TrackPlayingExecutor = Executors.newSingleThreadScheduledExecutor();
    	TrackPlayingExecutor.scheduleAtFixedRate(() -> updateTrackPlaying(), 0, TRACK_PLAYING_UPDATE_INTERVAL, TimeUnit.MILLISECONDS);
    }
    
    private static void updateTrackPlaying() {
    	CurrentlyPlayingContext currentPlaybackInfoUpdate = App.getUsersCurrentPlaybackInfo();
    	CurrentlyPlaying currentlyPlayingUpdate = App.getUsersCurrentlyPlayingTrack();
    	try {
			if(!currentPlaybackInfoUpdate.getIs_playing() || !currentlyPlayingUpdate.getIs_playing()) {
				isPlaying = false;
				SwingUtilities.invokeLater(() -> {
	        		pauseButton.setText("⏵");
	    		});
			}
			else if(currentPlaybackInfoUpdate.getIs_playing() || currentlyPlayingUpdate.getIs_playing()) {
				isPlaying = true;
				SwingUtilities.invokeLater(() -> {
	        		pauseButton.setText("⏸");
	    		});
			}
	    } catch(NullPointerException e1) {
			System.out.println("Null Track");
		}
    }
    
    private static void startTrackProgressExecutor() {
    	TrackProgressChangeExecutor = Executors.newSingleThreadScheduledExecutor();
    	TrackProgressChangeExecutor.scheduleAtFixedRate(() -> updateTrackProgress(), 0, TRACK_PROGRESS_CHANGE_UPDATE_INTERVAL, TimeUnit.MILLISECONDS);
    }
    
    private static void updateTrackProgress() {
    	CurrentlyPlayingContext currentPlaybackInfoUpdate = App.getUsersCurrentPlaybackInfo();
    	CurrentlyPlaying currentlyPlayingUpdate = App.getUsersCurrentlyPlayingTrack();
    	try {
	    	if(currentlyPlayingUpdate != null) {
	    		if(currentlyPlayingUpdate.getItem() != null) {
			    	if(currentlyPlaying.equals(currentlyPlayingUpdate)) {
			            int songPosition = currentlyPlayingUpdate.getProgress_ms();
			            int volumeValue = currentPlaybackInfoUpdate.getDevice().getVolume_percent();
			            setProgress(songPosition);
			            setVolume(volumeValue);
					}
	    		}
	    	}
	    } catch(NullPointerException e1) {
			System.out.println("Null Track");
		}
    }

    private static void stopExecutorServices() {
        if (TrackChangeExecutor != null && !TrackChangeExecutor.isShutdown()) {
            TrackChangeExecutor.shutdown();
        }

        if (TrackPlayingExecutor != null && !TrackPlayingExecutor.isShutdown()) {
            TrackPlayingExecutor.shutdown();
        }

        if (TrackProgressChangeExecutor != null && !TrackProgressChangeExecutor.isShutdown()) {
            TrackProgressChangeExecutor.shutdown();
        }
    }
}