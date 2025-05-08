package org.producr.api.service.interfaces;


import org.producr.api.data.domain.track.Track;
import org.producr.api.data.domain.user.User;
import org.producr.api.dtos.TrackFeedPageResponse;

import java.util.List;

public interface AudioTrackService {
  TrackFeedPageResponse getFeedTracks(User user, int page);

  List<Track> getUserTracks(User user);
}
