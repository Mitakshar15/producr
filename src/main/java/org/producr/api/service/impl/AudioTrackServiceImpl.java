package org.producr.api.service.impl;

import lombok.RequiredArgsConstructor;
import org.producr.api.data.domain.track.Track;
import org.producr.api.data.domain.user.User;
import org.producr.api.data.repository.TrackRepository;
import org.producr.api.dtos.TrackFeedItemDto;
import org.producr.api.dtos.TrackFeedPageResponse;
import org.producr.api.mapper.TrackMgmtMapper;
import org.producr.api.service.interfaces.AudioTrackService;
import org.producr.api.utils.constants.Constants;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class AudioTrackServiceImpl implements AudioTrackService {

  private final TrackRepository trackRepository;
  private final TrackMgmtMapper mapper;


  @Override
  @Transactional(readOnly = true)
  public TrackFeedPageResponse getFeedTracks(User user, int page) {

    // Implement Feed Recomendation Logic Later

    PageRequest pageRequest = PageRequest.of(page, Constants.FEED_DEFAULT_PAGE_SIZE,
        Sort.by(Sort.Direction.DESC, "createdAt"));
    Page<Track> trackPage = trackRepository.findByPublicTrue(pageRequest);
    Page<TrackFeedItemDto> feedItems = trackPage.map(mapper::toTrackFeedItemDto);
    TrackFeedPageResponse response = new TrackFeedPageResponse();
    response.setData(feedItems);
    return response;
  }
}
