package org.producr.api.service.interfaces;


import org.producr.api.data.domain.user.User;
import org.producr.api.dtos.TrackFeedPageResponse;

public interface AudioTrackService {
  TrackFeedPageResponse getFeedTracks(User user, int page);
}
