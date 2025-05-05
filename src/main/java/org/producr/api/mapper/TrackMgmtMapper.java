package org.producr.api.mapper;


import org.mapstruct.Mapper;
import org.producr.api.data.domain.pack.SamplePack;
import org.producr.api.data.domain.track.Track;
import org.producr.api.dto.BaseApiResponse;
import org.producr.api.dtos.*;
import org.producr.api.utils.enums.AudioCategory;
import org.producr.api.utils.enums.SampleType;

@Mapper(componentModel = "spring")
public interface TrackMgmtMapper {

  TrackUploadResponseDto toTrackUploadResponseDto(Track savedTrack);

  SampleType toSampleTypeEnum(TrackUploadRequest.SampleTypeEnum sampleType);

  AudioCategory toAudioCategoryEnum(TrackUploadRequest.AudioCategoryEnum audioCategory);

  TrackUploadResponse toTrackUploadResponse(BaseApiResponse trackUploadSuccess);

  TrackFeedPageResponse toTrackFeedPageResponse(BaseApiResponse baseApiResponse);

  TrackFeedItemDto toTrackFeedItemDto(Track track);

  SamplePackResponse toSamplePackResponse(BaseApiResponse baseApiResponse);

  SamplePackDto toSamplePackDto(SamplePack samplePack);
}
