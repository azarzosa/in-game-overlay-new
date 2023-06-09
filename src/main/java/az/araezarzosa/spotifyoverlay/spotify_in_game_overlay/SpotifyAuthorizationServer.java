package az.araezarzosa.spotifyoverlay.spotify_in_game_overlay;

import org.apache.hc.core5.http.ParseException;
import org.eclipse.jetty.server.Request;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.server.handler.AbstractHandler;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import se.michaelthelin.spotify.exceptions.SpotifyWebApiException;
import se.michaelthelin.spotify.model_objects.credentials.AuthorizationCodeCredentials;
import se.michaelthelin.spotify.model_objects.miscellaneous.CurrentlyPlaying;
import se.michaelthelin.spotify.model_objects.specification.Track;
import se.michaelthelin.spotify.model_objects.specification.User;
import se.michaelthelin.spotify.requests.authorization.authorization_code.AuthorizationCodeRequest;
import se.michaelthelin.spotify.requests.data.player.GetUsersCurrentlyPlayingTrackRequest;
import se.michaelthelin.spotify.requests.data.users_profile.GetCurrentUsersProfileRequest;
import java.awt.Desktop;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;

import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JSlider;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;

public class SpotifyAuthorizationServer extends HttpServlet {
    /**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private static final int PORT = 8888;
    private static final String REDIRECT_URI = "http://localhost:" + PORT + "/callback";
    
    public static String code = "";
    
    public static void startServer() throws Exception {
        Server server = new Server(PORT);
        SpotifyAuthorizationServer.AuthorizationHandler handler = new SpotifyAuthorizationServer.AuthorizationHandler();
        server.setHandler(handler);
        server.start();
        System.out.println("Server started on port " + PORT);
        server.join();
    }

    private static class AuthorizationHandler extends AbstractHandler {
    	private static final int REDIRECT_DELAY_SECONDS = 3;
        
        @Override
        public void handle(String target, Request baseRequest, HttpServletRequest request,
                           HttpServletResponse response) throws IOException, ServletException {
            if (target.equals("/callback")) {
                code = request.getParameter("code");
                // Handle the authorization code here, e.g., store it for later use
                AuthorizationCodeRequest authorizationCodeRequest = App.getSpotifyApi().authorizationCode(code)
                	    .build();

                AuthorizationCodeCredentials authorizationCodeCredentials;
				try {
					authorizationCodeCredentials = authorizationCodeRequest.execute();
				
                
					App.getSpotifyApi().setAccessToken(authorizationCodeCredentials.getAccessToken());
					App.getSpotifyApi().setRefreshToken(authorizationCodeCredentials.getRefreshToken());
                
				} catch (ParseException | SpotifyWebApiException | IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				
				
                // Send a response to the browser
                response.setContentType("text/html;charset=utf-8");
                response.setStatus(HttpServletResponse.SC_OK);
                baseRequest.setHandled(true);
                response.getWriter().println("Authorization successful!");
                response.getWriter().println("<script>");
                response.getWriter().println("setTimeout(function() { window.close(); }, " + (REDIRECT_DELAY_SECONDS * 1000) + ");");
                response.getWriter().println("</script>");
                
                App.afterAuth();
                OverlayWindow.showOverlay();
                
            }
        }
    }
    
    public static String getAuthorizationUrl() {
        // Construct the authorization URL with your desired parameters
        // Replace with your actual implementation to obtain the authorization URL
        String authorizationUrl = "https://accounts.spotify.com/authorize?client_id=" + App.getSpotifyApi().getClientId() + "&response_type=code&redirect_uri=" + REDIRECT_URI + "&scope=user-read-playback-state%20user-modify-playback-state";

        return authorizationUrl;
    }
    
    public static void initiateAuthorizationFlow() {
        // Open the authorization URL in the user's default browser
        String authorizationUrl = getAuthorizationUrl();
        openWebpage(authorizationUrl);
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
	
	public static void stopServer(Server server) throws Exception {
        if (server != null) {
            server.stop();
            System.out.println("Server stopped");
        }
    }
}