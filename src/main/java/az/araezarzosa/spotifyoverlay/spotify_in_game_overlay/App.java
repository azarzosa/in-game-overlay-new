package az.araezarzosa.spotifyoverlay.spotify_in_game_overlay;

import javax.swing.*;

import se.michaelthelin.spotify.SpotifyApi;
import se.michaelthelin.spotify.SpotifyHttpManager;
import se.michaelthelin.spotify.exceptions.SpotifyWebApiException;
import se.michaelthelin.spotify.model_objects.miscellaneous.CurrentlyPlaying;
import se.michaelthelin.spotify.requests.data.player.GetUsersCurrentlyPlayingTrackRequest;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.net.URL;
import java.util.concurrent.CancellationException;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionException;


public class App{

	
	public static JFrame frame;
	public static JButton button;
	

    private static boolean guiInitialized = false;
	
	public static final SpotifyApi  spotifyApi = new SpotifyApi.Builder()
			  .setClientId("<clientid>")
			  .setClientSecret("<clientsecret>")
			  .setRedirectUri(
					  SpotifyHttpManager.makeUri("http://localhost:8888/callback"))
			  .build();
	
	private static final GetUsersCurrentlyPlayingTrackRequest getUsersCurrentlyPlayingTrackRequest = spotifyApi
		    .getUsersCurrentlyPlayingTrack()
//		          .market(CountryCode.SE)
//		          .additionalTypes("track,episode")
		    .build();
	
	
	public static void getUsersCurrentlyPlayingTrack_Sync() {
	    try {
	      final CurrentlyPlaying currentlyPlaying = getUsersCurrentlyPlayingTrackRequest.execute();

	      System.out.println("Timestamp: " + currentlyPlaying.getTimestamp());
	    } catch (IOException e) {
	      System.out.println("Error: " + e.getMessage());
	    } catch(SpotifyWebApiException e) {
		      System.out.println("Error: " + e.getMessage());
	    } catch (org.apache.hc.core5.http.ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	  }
	
	public static void getUsersCurrentlyPlayingTrack_Async() {
	    try {
	      final CompletableFuture<CurrentlyPlaying> currentlyPlayingFuture = getUsersCurrentlyPlayingTrackRequest.executeAsync();

	      // Thread free to do other tasks...

	      // Example Only. Never block in production code.
	      final CurrentlyPlaying currentlyPlaying = currentlyPlayingFuture.join();

	      System.out.println("Timestamp: " + currentlyPlaying.getTimestamp());
	    } catch (CompletionException e) {
	      System.out.println("Error: " + e.getCause().getMessage());
	    } catch (CancellationException e) {
	      System.out.println("Async operation cancelled.");
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
		    frame.setSize(400, 200);
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
		    buttonConstraints.insets = new Insets(10, 0, 10, 0);
		    panel.add(button, buttonConstraints);
		    button.addActionListener(new ActionListener() {
		    	public void actionPerformed(ActionEvent e) {
		    		if(!guiInitialized) {
			    		Thread serverThread = new Thread(() -> {
			                try {
			                	SpotifyAuthorizationServer.main(args);
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
		    
		    
		    JLabel madeBy = new JLabel("Made by Arae Zarzosa for Spotify®");
		    madeBy.setFont(new Font("Circular", Font.PLAIN, 12));
		    madeBy.setForeground(new Color(30, 215, 96));
		    GridBagConstraints madeByConstraints = new GridBagConstraints();
		    madeByConstraints.gridx = 0;
		    madeByConstraints.gridy = GridBagConstraints.LAST_LINE_END;
		    madeByConstraints.insets = new Insets(0, 0, 0, 0);
		    madeByConstraints.anchor = GridBagConstraints.PAGE_END;
		    panel.add(madeBy, madeByConstraints);
		    
		    
		    frame.add(panel);
		    frame.setVisible(true);
		});
	  }
}
