package az.araezarzosa.spotifyoverlay.spotify_in_game_overlay;

import javax.swing.*;
import javax.swing.border.LineBorder;
import javax.swing.plaf.ColorUIResource;
import javax.swing.plaf.FontUIResource;
import javax.swing.plaf.SliderUI;
import javax.swing.plaf.basic.BasicComboBoxEditor;
import javax.swing.plaf.basic.BasicComboBoxUI;
import javax.swing.plaf.basic.BasicSliderUI;

import se.michaelthelin.spotify.SpotifyApi;
import se.michaelthelin.spotify.SpotifyHttpManager;
import se.michaelthelin.spotify.exceptions.SpotifyWebApiException;
import se.michaelthelin.spotify.model_objects.miscellaneous.CurrentlyPlaying;
import se.michaelthelin.spotify.model_objects.miscellaneous.CurrentlyPlayingContext;
import se.michaelthelin.spotify.model_objects.specification.Track;
import se.michaelthelin.spotify.requests.data.player.GetInformationAboutUsersCurrentPlaybackRequest;
import se.michaelthelin.spotify.requests.data.player.GetUsersCurrentlyPlayingTrackRequest;
import se.michaelthelin.spotify.requests.data.player.PauseUsersPlaybackRequest;
import se.michaelthelin.spotify.requests.data.player.SeekToPositionInCurrentlyPlayingTrackRequest;
import se.michaelthelin.spotify.requests.data.player.SetVolumeForUsersPlaybackRequest;
import se.michaelthelin.spotify.requests.data.player.SkipUsersPlaybackToNextTrackRequest;
import se.michaelthelin.spotify.requests.data.player.SkipUsersPlaybackToPreviousTrackRequest;
import se.michaelthelin.spotify.requests.data.player.StartResumeUsersPlaybackRequest;
import se.michaelthelin.spotify.requests.data.tracks.GetTrackRequest;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.Dictionary;
import java.util.concurrent.CancellationException;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionException;


public class App{

	
	private static JFrame frame;
	private static JButton button;
	private static JSlider opacitySlider;
	private static JComboBox positionList;
	private static JComboBox sizeList;
	/**public static JTextField colorField;
	public static JButton updateColorButton;**/
	

    private static boolean guiInitialized = false;
    private static final int OPACITY_MIN = 20;
    private static final int OPACITY_MAX = 100;
    private static final int OPACITY_INIT = 100;
    private static final String[] positionStrings = { "Top Left", "Top Right", "Top Center", "Bottom Left", "Bottom Right", "Bottom Center"};
    private static final String[] sizeStrings = { "Small", "Medium", "Large" };
	
	private static final SpotifyApi  spotifyApi = new SpotifyApi.Builder()
			  .setClientId("<client_id>")
			  .setClientSecret("<client_secret>")
			  .setRedirectUri(
					  SpotifyHttpManager.makeUri("http://localhost:8888/callback"))
			  .build();
	
	public static Track getTrack(String id) {
		try {
	    	GetTrackRequest trackRequest = spotifyApi.getTrack(id).build();
            Track track;
			track = trackRequest.execute();
            return track;
	    } catch (IOException e) {
	    	System.out.println("Error: " + e.getMessage());
	    	return null;
	    } catch(SpotifyWebApiException e) {
	    	System.out.println("Error: " + e.getMessage());
	    	return null;
	    } catch (org.apache.hc.core5.http.ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
	    	return null;
		}
	}
	
	public static CurrentlyPlaying getUsersCurrentlyPlayingTrack(){
	    try {
	    	GetUsersCurrentlyPlayingTrackRequest playingTrackRequest = spotifyApi.getUsersCurrentlyPlayingTrack().build();
            CurrentlyPlaying track;
			track = playingTrackRequest.execute();
            return track;
	    } catch (IOException e) {
	    	System.out.println("Error: " + e.getMessage());
	    	return null;
	    } catch(SpotifyWebApiException e) {
	    	System.out.println("Error: " + e.getMessage());
	    	return null;
	    } catch (org.apache.hc.core5.http.ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
	    	return null;
		}
	}
	
	public static CurrentlyPlayingContext getUsersCurrentPlaybackInfo() {
		try {
			GetInformationAboutUsersCurrentPlaybackRequest currentPlaybackRequest = spotifyApi.getInformationAboutUsersCurrentPlayback().build();
            CurrentlyPlayingContext info;
			info = currentPlaybackRequest.execute();
			return info;
	    } catch (IOException e) {
	    	System.out.println("Error: " + e.getMessage());
	    	return null;
	    } catch(SpotifyWebApiException e) {
	    	System.out.println("Error: " + e.getMessage());
	    	return null;
	    } catch (org.apache.hc.core5.http.ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return null;
		}
	}
	
	public static void seekToPositionInCurrentlyPlayingTrack(int position) {
		try {
	    	SeekToPositionInCurrentlyPlayingTrackRequest seekToPositionRequest = spotifyApi.seekToPositionInCurrentlyPlayingTrack(position).build();
            seekToPositionRequest.execute();
	    } catch (IOException e) {
	    	System.out.println("Error: " + e.getMessage());
	    } catch(SpotifyWebApiException e) {
	    	System.out.println("Error: " + e.getMessage());
	    } catch (org.apache.hc.core5.http.ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public static void skipUsersPlaybackToPreviousTrack() {
		try {
	    	SkipUsersPlaybackToPreviousTrackRequest skipUsersPlaybackToPreviousRequest = spotifyApi.skipUsersPlaybackToPreviousTrack().build();
	    	skipUsersPlaybackToPreviousRequest.execute();
	    } catch (IOException e) {
	    	System.out.println("Error: " + e.getMessage());
	    } catch(SpotifyWebApiException e) {
	    	System.out.println("Error: " + e.getMessage());
	    } catch (org.apache.hc.core5.http.ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public static void skipUsersPlaybackToNextTrack() {
		try {
	    	SkipUsersPlaybackToNextTrackRequest skipUsersPlaybackToNextRequest = spotifyApi.skipUsersPlaybackToNextTrack().build();
	    	skipUsersPlaybackToNextRequest.execute();
	    } catch (IOException e) {
	    	System.out.println("Error: " + e.getMessage());
	    } catch(SpotifyWebApiException e) {
	    	System.out.println("Error: " + e.getMessage());
	    } catch (org.apache.hc.core5.http.ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public static void pauseUsersPlayback() {
		try {
	    	PauseUsersPlaybackRequest pauseUsersPlaybackRequest = spotifyApi.pauseUsersPlayback().build();
	    	pauseUsersPlaybackRequest.execute();
	    } catch (IOException e) {
	    	System.out.println("Error: " + e.getMessage());
	    } catch(SpotifyWebApiException e) {
	    	System.out.println("Error: " + e.getMessage());
	    } catch (org.apache.hc.core5.http.ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public static void startResumeUsersPlayback() {
		try {
	    	StartResumeUsersPlaybackRequest startResumeUsersPlaybackRequest = spotifyApi.startResumeUsersPlayback().build();
	    	startResumeUsersPlaybackRequest.execute();
	    } catch (IOException e) {
	    	System.out.println("Error: " + e.getMessage());
	    } catch(SpotifyWebApiException e) {
	    	System.out.println("Error: " + e.getMessage());
	    } catch (org.apache.hc.core5.http.ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public static void setVolumePlayback(int volume) {
		try {
	    	SetVolumeForUsersPlaybackRequest setVolumeForUsersPlaybackRequest = spotifyApi.setVolumeForUsersPlayback(volume).build();
	    	setVolumeForUsersPlaybackRequest.execute();
	    } catch (IOException e) {
	    	System.out.println("Error: " + e.getMessage());
	    } catch(SpotifyWebApiException e) {
	    	System.out.println("Error: " + e.getMessage());
	    } catch (org.apache.hc.core5.http.ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	
	public static void main(String[] args) throws Exception {
		
		
		
		try {
		      UIManager.setLookAndFeel("javax.swing.plaf.nimbus.NimbusLookAndFeel");
		    } catch (Exception e) {
		      e.printStackTrace();
		    }
		
		SwingUtilities.invokeLater(() -> {
			frame = new JFrame("Spotify In-Game Overlay");
			URL iconURL = App.class.getResource("icon.png");
			ImageIcon icon = new ImageIcon(iconURL);
			Image iconImage = icon.getImage();
			Image resizedImage = iconImage.getScaledInstance(32, 32, Image.SCALE_SMOOTH);
			frame.setIconImage(resizedImage);
		    frame.setSize(400,440);
		    frame.setResizable(false);
		    frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		    
		    
		    JPanel panel = new JPanel();
		    panel.setLayout(new GridBagLayout());
		    panel.setBackground(new Color(25, 20, 20));
		    
		    JLabel label = new JLabel("Spotify In-Game Overlay");
		    label.setFont(new Font("Circular", Font.BOLD, 24));
		    label.setForeground(new Color(30, 215, 96));
		    GridBagConstraints labelConstraints = new GridBagConstraints();
		    labelConstraints.gridx = 0;
		    labelConstraints.gridy = 0;
		    labelConstraints.insets = new Insets(0, 0, 10, 0);
		    panel.add(label, labelConstraints);
		    
		    button = new JButton("Connect to Spotify");
		    button.setFont(new Font("Circular", Font.BOLD, 12));
		    button.setBackground(new Color(30, 215, 96));
		    button.setForeground(Color.BLACK);
		    button.setBorder(BorderFactory.createEmptyBorder());
		    button.setBorderPainted(false);
		    button.setPreferredSize(new Dimension(125, 35)); 
		    GridBagConstraints buttonConstraints = new GridBagConstraints();
		    buttonConstraints.gridx = 0;
		    buttonConstraints.gridy = 1;
		    buttonConstraints.insets = new Insets(5, 0, 10, 0);
		    panel.add(button, buttonConstraints);
		    button.addActionListener(new ActionListener() {
		    	public void actionPerformed(ActionEvent e) {
		    		if(!guiInitialized) {
			    		Thread serverThread = new Thread(() -> {
			                try {
			                	SpotifyAuthorizationServer.startServer();
			                } catch (Exception e1) {
			                    e1.printStackTrace();
			                }
			            });
			            serverThread.start();
	                    guiInitialized = true;
			            SpotifyAuthorizationServer.initiateAuthorizationFlow();
			            //button.setText("Connected to Spotify");
			            //button.setEnabled(false);
		    		}
		    	}
		    });

		    
		    JLabel opacityLabel = new JLabel("Overlay Opacity:");
		    opacityLabel.setFont(new Font("Circular", Font.BOLD, 12));
		    opacityLabel.setForeground(new Color(30, 215, 96));
		    GridBagConstraints opacityLabelConstraints = new GridBagConstraints();
		    opacityLabelConstraints.gridx = 0;
		    opacityLabelConstraints.gridy = 2;
		    opacityLabelConstraints.insets = new Insets(20, 0, 0, 0);
		    panel.add(opacityLabel, opacityLabelConstraints);
		    
		    BasicSliderUI sliderUI = new BasicSliderUI(null) {
                @Override
                public void paintTrack(Graphics g) {
                    Graphics2D g2 = (Graphics2D) g;
                    Rectangle trackBounds = trackRect;

                    // Set the left side of the track as green
                    g2.setColor(new Color(29, 185, 84));
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
		    
		    
		    opacitySlider = new JSlider(JSlider.HORIZONTAL, OPACITY_MIN, OPACITY_MAX, OPACITY_INIT);
		    opacitySlider.setFont(new Font("Circular", Font.PLAIN, 12));
		    opacitySlider.setUI(sliderUI);
		    opacitySlider.setOpaque(false);
		    opacitySlider.setMajorTickSpacing(10);
		    opacitySlider.setPaintTicks(false);
		    opacitySlider.setPaintLabels(false);
		    opacitySlider.setEnabled(false);
		    GridBagConstraints opacitySliderConstraints = new GridBagConstraints();
		    opacitySliderConstraints.gridx = 0;
		    opacitySliderConstraints.gridy = 3;
		    opacitySliderConstraints.insets = new Insets(0, 0, 0, 0);
		    panel.add(opacitySlider, opacitySliderConstraints);
		    
		    opacitySlider.addChangeListener(e -> {
		        JSlider source = (JSlider) e.getSource();
		        if (!source.getValueIsAdjusting()) {
		            float opacity = (float) source.getValue() / 100.0f; // Get the slider value and convert it to a float between 0 and 1
		            OverlayWindow.setWindowOpacity(opacity); // Call the static method in OverlayWindow to set the window opacity
		        }
		    });
            
            BasicComboBoxUI comboBoxUI1 = new BasicComboBoxUI() {
                @Override
                protected JButton createArrowButton() {
                    JButton button = new JButton();
                    button.setBorder(BorderFactory.createEmptyBorder());
                    button.setContentAreaFilled(false);
                    button.setOpaque(false);
                    button.setFocusPainted(false);
                    button.setIcon(new ImageIcon("arrow_icon.png")); // Replace with your arrow icon
                    return button;
                }
            };
            
		    JLabel positionLabel = new JLabel("Overlay Position:");
		    positionLabel.setFont(new Font("Circular", Font.BOLD, 12));
		    positionLabel.setForeground(new Color(30, 215, 96));
		    GridBagConstraints positionLabelConstraints = new GridBagConstraints();
		    positionLabelConstraints.gridx = 0;
		    positionLabelConstraints.gridy = 4;
		    positionLabelConstraints.insets = new Insets(10, 0, 0, 0);
		    panel.add(positionLabel, positionLabelConstraints);
		    positionList = new JComboBox(positionStrings);
		    positionList.setUI(comboBoxUI1);
		    positionList.setBorder(new LineBorder(Color.WHITE));
		    positionList.setBackground(new Color(25, 20, 20));
		    positionList.setForeground(Color.WHITE);
		    positionList.setSelectedIndex(0);
		    GridBagConstraints positionListConstraints = new GridBagConstraints();
		    positionListConstraints.gridx = 0;
		    positionListConstraints.gridy = 5;
		    positionListConstraints.insets = new Insets(5, 0, 0, 0);
		    positionList.setEnabled(false);
		    panel.add(positionList, positionListConstraints);
		    
		    positionList.addActionListener(new ActionListener() {
		        @Override
		        public void actionPerformed(ActionEvent e) {
		            String selectedPosition = (String) positionList.getSelectedItem();
	                OverlayWindow.setPositionString(selectedPosition);
		            switch (selectedPosition) {
			            case "Top Left":
			                OverlayWindow.setWindowPosition(50, 50);
			                break;
			            case "Top Right":
			                OverlayWindow.setWindowPosition(OverlayWindow.getScreenSize().width - OverlayWindow.getWidth() - 50, 50);
			                break;
			            case "Top Center":
			                OverlayWindow.setWindowPosition((OverlayWindow.getScreenSize().width - OverlayWindow.getWidth()) / 2, 50);
			                break;
			            case "Bottom Left":
			                OverlayWindow.setWindowPosition(50, OverlayWindow.getScreenSize().height - OverlayWindow.getHeight() - 50);
			                break;
			            case "Bottom Right":
			                OverlayWindow.setWindowPosition(OverlayWindow.getScreenSize().width - OverlayWindow.getWidth() - 50, OverlayWindow.getScreenSize().height - OverlayWindow.getHeight() - 50);
			                break;
			            case "Bottom Center":
			                OverlayWindow.setWindowPosition((OverlayWindow.getScreenSize().width - OverlayWindow.getWidth()) / 2, OverlayWindow.getScreenSize().height - OverlayWindow.getHeight() - 50);
			                break;
			            default:
			                break;
		            }
		        }
		    });
		    
		    
		    BasicComboBoxUI comboBoxUI2 = new BasicComboBoxUI() {
                @Override
                protected JButton createArrowButton() {
                    JButton button = new JButton();
                    button.setBorder(BorderFactory.createEmptyBorder());
                    button.setContentAreaFilled(false);
                    button.setOpaque(false);
                    button.setFocusPainted(false);
                    button.setIcon(new ImageIcon("arrow_icon.png")); // Replace with your arrow icon
                    return button;
                }
            };
		    
		    JLabel sizeLabel = new JLabel("Overlay Size:");
		    sizeLabel.setFont(new Font("Circular", Font.BOLD, 12));
		    sizeLabel.setForeground(new Color(30, 215, 96));
		    GridBagConstraints sizeLabelConstraints = new GridBagConstraints();
		    sizeLabelConstraints.gridx = 0;
		    sizeLabelConstraints.gridy = 6;
		    sizeLabelConstraints.insets = new Insets(10, 0, 0, 0);
		    panel.add(sizeLabel, sizeLabelConstraints);
		    sizeList = new JComboBox(sizeStrings);
		    sizeList.setUI(comboBoxUI2);
		    sizeList.setBorder(new LineBorder(Color.WHITE));
		    sizeList.setBackground(new Color(25, 20, 20));
		    sizeList.setForeground(Color.WHITE);
		    sizeList.setSelectedIndex(1);
		    GridBagConstraints sizeListConstraints = new GridBagConstraints();
		    sizeListConstraints.gridx = 0;
		    sizeListConstraints.gridy = 7;
		    sizeListConstraints.insets = new Insets(5, 0, 0, 0);
		    sizeList.setEnabled(false);
		    panel.add(sizeList, sizeListConstraints);
		    
		    sizeList.addActionListener(new ActionListener() {
		        @Override
		        public void actionPerformed(ActionEvent e) {
		            OverlayWindow.setWindowSize((String) sizeList.getSelectedItem());
		        }
		    });
		    
		    /**JLabel colorLabel = new JLabel("Progress/Volume Bar Color:");
		    colorLabel.setFont(new Font("Circular", Font.BOLD, 12));
		    colorLabel.setForeground(new Color(30, 215, 96));
		    GridBagConstraints colorLabelConstraints = new GridBagConstraints();
		    colorLabelConstraints.gridx = 0;
		    colorLabelConstraints.gridy = 8;
		    colorLabelConstraints.insets = new Insets(10, 0, 0, 80);
		    panel.add(colorLabel, colorLabelConstraints);
		    colorField = new JTextField();
		    colorField.setBorder(BorderFactory.createLineBorder(Color.WHITE));
            colorField.setBackground(new Color(25, 20, 20));
            colorField.setForeground(Color.WHITE);
		    colorField.setFont(new Font("Circular", Font.PLAIN, 12));
		    colorField.setEditable(false);
		    colorField.setText("#1DB954");
		    GridBagConstraints colorFieldConstraints = new GridBagConstraints();
		    colorFieldConstraints.gridx = 0;
		    colorFieldConstraints.gridy = 8;
		    colorFieldConstraints.insets = new Insets(10, 175, 0, 0);
		    panel.add(colorField, colorFieldConstraints);
		    updateColorButton = new JButton("Update Color");
		    updateColorButton.setFont(new Font("Circular", Font.BOLD, 12));
		    updateColorButton.setBackground(new Color(30, 215, 96));
		    updateColorButton.setForeground(Color.BLACK);
		    updateColorButton.setBorder(BorderFactory.createEmptyBorder());
		    updateColorButton.setBorderPainted(false);
		    updateColorButton.setPreferredSize(new Dimension(125, 35)); 
		    updateColorButton.setEnabled(false);
		    GridBagConstraints colorButtonConstraints = new GridBagConstraints();
		    colorButtonConstraints.gridx = 0;
		    colorButtonConstraints.gridy = 9;
		    colorButtonConstraints.insets = new Insets(5, 0, 10, 0);
		    panel.add(updateColorButton, colorButtonConstraints);
		    
		    updateColorButton.addActionListener(new ActionListener() {
		        @Override
		        public void actionPerformed(ActionEvent e) {
		        	if(colorField.getText() != null) {
		        		OverlayWindow.setBarColor(colorField.getText());
		        	}
		        	else {
		        		ErrorDialog.showErrorMessage();
		        	}
		        }
		    });**/
		    
		    
		    JLabel madeBy = new JLabel("<html><div style='text-align: center;'>Made by <a href=\\\"https://araezarzosa.dev/\\\">Arae Zarzosa</a> for Spotify®</div></html>");
		    madeBy.setFont(new Font("Circular", Font.PLAIN, 12));
		    madeBy.setForeground(new Color(30, 215, 96));
		    madeBy.setVerticalAlignment(JLabel.CENTER);
		    madeBy.setHorizontalAlignment(JLabel.CENTER);
		    GridBagConstraints madeByConstraints = new GridBagConstraints();
		    madeByConstraints.gridx = 0;
		    madeByConstraints.gridy = GridBagConstraints.LAST_LINE_END;
		    madeByConstraints.insets = new Insets(40, 0, 0, 0);
		    madeByConstraints.anchor = GridBagConstraints.PAGE_END;
		    
		    madeBy.addMouseListener(new MouseAdapter() {
		        @Override
		        public void mouseClicked(MouseEvent e) {
		            try {
		                Desktop.getDesktop().browse(new URI("https://araezarzosa.dev/"));
		            } catch (IOException | URISyntaxException ex) {
		                ex.printStackTrace();
		            }
		        }
		    });
		    
		    panel.add(madeBy, madeByConstraints);
		    
		    
		    frame.add(panel);
		    frame.setVisible(true);

		});
	  }
	
	public static void afterAuth() {
		SwingUtilities.invokeLater(() -> {
			button.setText("Connected to Spotify");
			button.setEnabled(false);
			opacitySlider.setEnabled(true);
			positionList.setEnabled(true);
			sizeList.setEnabled(true);
			/**colorField.setEditable(true);
			updateColorButton.setEnabled(true);**/
		});
	}
	
	public static SpotifyApi getSpotifyApi() {
		return spotifyApi;
	}
}
