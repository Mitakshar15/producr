package org.producr.api.service;

import lombok.RequiredArgsConstructor;
import org.producr.api.data.domain.track.Track;
import org.producr.api.data.repository.TrackRepository;
import org.producr.api.dtos.ProducerDto;
import org.producr.api.dtos.TrackFeedItemDto;
import org.producr.api.dtos.TrackFeedPageResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class TrackFeedService {

  private final TrackRepository trackRepository;
  private static final int DEFAULT_PAGE_SIZE = 10;

  @Transactional(readOnly = true)
  public TrackFeedPageResponse getFeedTracks(int page) {
    PageRequest pageRequest =
        PageRequest.of(page, DEFAULT_PAGE_SIZE, Sort.by(Sort.Direction.DESC, "createdAt"));

    Page<Track> trackPage = trackRepository.findByPublicTrue(pageRequest);
    Page<TrackFeedItemDto> feedItems = trackPage.map(this::mapToFeedItem);

    TrackFeedPageResponse response = new TrackFeedPageResponse();
    response.setData(feedItems);
    return response;
  }

  private TrackFeedItemDto mapToFeedItem(Track track) {
    TrackFeedItemDto feedItem = new TrackFeedItemDto();
    feedItem.setId(track.getId());
    feedItem.setTitle(track.getTitle());
    feedItem.setDescription(track.getDescription());
    feedItem.setProducer(mapToProducerDto(track));
    feedItem.setAudioFileUrl(track.getAudioFileUrl());
    feedItem.setTrackLengthSeconds(track.getTrackLengthSeconds());
    feedItem.setPlaysCount(track.getPlaysCount());
    feedItem.setLikesCount(track.getLikesCount());
    feedItem.setCommentsCount(track.getCommentsCount());
    feedItem.setIsPublic(track.isPublic());
    feedItem.setCreatedAt(String.valueOf(track.getCreatedAt()));
    feedItem.setUpdatedAt(String.valueOf(track.getUpdatedAt()));
    return feedItem;
  }

  private ProducerDto mapToProducerDto(Track track) {
    ProducerDto producer = new ProducerDto();
    producer.setId(track.getProducer().getId());
    producer.setUsername(track.getProducer().getUsername());
    producer.setAvatarUrl(track.getProducer().getProfile().getBannerImageUrl());
    return producer;
  }
}
