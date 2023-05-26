package az.araezarzosa.spotifyoverlay.spotify_in_game_overlay;

import javax.swing.*;

import se.michaelthelin.spotify.SpotifyApi;
import se.michaelthelin.spotify.SpotifyHttpManager;
import se.michaelthelin.spotify.exceptions.SpotifyWebApiException;
import se.michaelthelin.spotify.model_objects.credentials.AuthorizationCodeCredentials;
import se.michaelthelin.spotify.model_objects.miscellaneous.CurrentlyPlaying;
import se.michaelthelin.spotify.requests.authorization.authorization_code.AuthorizationCodeRequest;
import se.michaelthelin.spotify.requests.authorization.authorization_code.AuthorizationCodeUriRequest;
import se.michaelthelin.spotify.requests.authorization.client_credentials.ClientCredentialsRequest;
import se.michaelthelin.spotify.requests.data.player.GetUsersCurrentlyPlayingTrackRequest;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.net.URI;
import java.net.URISyntaxException;
import java.io.IOException;
import java.net.URL;
import java.text.ParseException;
import java.util.concurrent.CancellationException;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionException;


public class App  
{

	private static final String code = "";
	
	private static final SpotifyApi  spotifyApi = new SpotifyApi.Builder()
			  .setClientId("ce186274da7a40a098ccec56a2c00404")
			  .setClientSecret("674671e984ea4822b49e68f383a97c82")
			  .setRedirectUri(
					  SpotifyHttpManager.makeUri("http://localhost:8080"))
			  .build();
	private static final ClientCredentialsRequest clientCredentialsRequest = spotifyApi.clientCredentials()
		    .build();
	
	private static final AuthorizationCodeUriRequest authorizationCodeUriRequest = spotifyApi.authorizationCodeUri()
//          .state("x4xkmn9pu3j6ukrs8n")
//          .scope("user-read-birthdate,user-read-email")
//          .show_dialog(true)
    .build();
	
	public static String authorizationCodeUri_Sync() {
	    final URI uri = authorizationCodeUriRequest.execute();

	    System.out.println("URI: " + uri.toString());
	    return uri.toString();
	  }
	
	public static void authorizationCodeUri_Async() {
	    try {
	      final CompletableFuture<URI> uriFuture = authorizationCodeUriRequest.executeAsync();

	      // Thread free to do other tasks...

	      // Example Only. Never block in production code.
	      final URI uri = uriFuture.join();

	      System.out.println("URI: " + uri.toString());
	    } catch (CompletionException e) {
	      System.out.println("Error: " + e.getCause().getMessage());
	    } catch (CancellationException e) {
	      System.out.println("Async operation cancelled.");
	    }
	  }
	private static final AuthorizationCodeRequest authorizationCodeRequest = spotifyApi.authorizationCode(code)
		    .build();
	public static void authorizationCode_Sync() {
	    try {
	      final AuthorizationCodeCredentials authorizationCodeCredentials = authorizationCodeRequest.execute();

	      // Set access and refresh token for further "spotifyApi" object usage
	      spotifyApi.setAccessToken(authorizationCodeCredentials.getAccessToken());
	      spotifyApi.setRefreshToken(authorizationCodeCredentials.getRefreshToken());

	      System.out.println("Expires in: " + authorizationCodeCredentials.getExpiresIn());
	    } catch (IOException e) {
	      System.out.println("Error: " + e.getMessage());
	    } catch (SpotifyWebApiException e) {
		      System.out.println("Error: " + e.getMessage());
		} catch (org.apache.hc.core5.http.ParseException e) {
			// TODO Auto-generated catch block
			System.out.println("Error: " + e.getMessage());
		} 
	  }

	  public static void authorizationCode_Async() {
	    try {
	      final CompletableFuture<AuthorizationCodeCredentials> authorizationCodeCredentialsFuture = authorizationCodeRequest.executeAsync();

	      // Thread free to do other tasks...

	      // Example Only. Never block in production code.
	      final AuthorizationCodeCredentials authorizationCodeCredentials = authorizationCodeCredentialsFuture.join();

	      // Set access and refresh token for further "spotifyApi" object usage
	      spotifyApi.setAccessToken(authorizationCodeCredentials.getAccessToken());
	      spotifyApi.setRefreshToken(authorizationCodeCredentials.getRefreshToken());

	      System.out.println("Expires in: " + authorizationCodeCredentials.getExpiresIn());
	    } catch (CompletionException e) {
	      System.out.println("Error: " + e.getCause().getMessage());
	    } catch (CancellationException e) {
	      System.out.println("Async operation cancelled.");
	    }
	  }
	
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
	
	public static void openWebpage(String url) {
        if (Desktop.isDesktopSupported() && Desktop.getDesktop().isSupported(Desktop.Action.BROWSE)) {
            try {
                Desktop.getDesktop().browse(new URI(url));
            } catch (IOException e) {
                e.printStackTrace();
            } catch (URISyntaxException e) {
                e.printStackTrace();
            }
        }
    }
	
	public static void main(String[] args) throws URISyntaxException {
		
		
		try {
		      UIManager.setLookAndFeel("javax.swing.plaf.nimbus.NimbusLookAndFeel");
		    } catch (Exception e) {
		      e.printStackTrace();
		    }
		
		JFrame frame = new JFrame("Spotify In-Game Overlay");
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
	    
	    JButton button = new JButton("Connect to Spotify");
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
	    		App.openWebpage(authorizationCodeUri_Sync());
	    	    authorizationCodeUri_Async();
	    	}
	    });
	    
	    JLabel madeBy = new JLabel("Made by Arae Zarzosa for Spotify®");
	    madeBy.setFont(new Font("Circular", Font.PLAIN, 12));
	    madeBy.setForeground(new Color(30, 215, 96));
	    GridBagConstraints madeByConstraints = new GridBagConstraints();
	    madeByConstraints.gridx = 0;
	    madeByConstraints.gridy = 2;
	    madeByConstraints.insets = new Insets(0, 0, 0, 0);
	    panel.add(madeBy, madeByConstraints);
	    
	    /**ImageIcon spotifyIcon = new ImageIcon("./Spotify_Logo_RGB_Green.png"); // replace "spotify.png" with the filename of your image
	    Image originalImage = spotifyIcon.getImage();
	    Image resizedImage = originalImage.getScaledInstance(70, 21, Image.SCALE_SMOOTH);
	    ImageIcon resizedIcon = new ImageIcon(resizedImage);
	    JLabel spotifyLabel = new JLabel(resizedIcon);
	    GridBagConstraints spotifyConstraints = new GridBagConstraints();
	    spotifyConstraints.gridx = 1;
	    spotifyConstraints.gridy = 2;
	    spotifyConstraints.insets = new Insets(0, 0, 0, 0);
	    panel.add(spotifyLabel, spotifyConstraints);**/
	    
	    frame.add(panel);
	    frame.setVisible(true);
	    //frame.setLocationRelativeTo(null);
	  }
}
