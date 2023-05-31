package az.araezarzosa.spotifyoverlay.spotify_in_game_overlay;

import jakarta.servlet.ServletContext;
import jakarta.servlet.ServletContextEvent;
import jakarta.servlet.ServletContextListener;
import jakarta.servlet.ServletRegistration;

public class SpotifyOverlayContextListener implements ServletContextListener {

    @Override
    public void contextInitialized(ServletContextEvent sce) {
    	ServletContext servletContext = sce.getServletContext();
        
        ServletRegistration.Dynamic registration = servletContext.addServlet("SpotifyAuthorizationServer",
                new SpotifyAuthorizationServer());
        registration.addMapping("/callback");
    }

    @Override
    public void contextDestroyed(ServletContextEvent sce) {
        // Cleanup code, if needed
    }
}