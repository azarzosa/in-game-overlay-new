package az.araezarzosa.spotifyoverlay.spotify_in_game_overlay;

import se.michaelthelin.spotify.model_objects.miscellaneous.CurrentlyPlaying;

public interface TrackChangeListener {
	void onTrackChanged(CurrentlyPlaying track);
}
