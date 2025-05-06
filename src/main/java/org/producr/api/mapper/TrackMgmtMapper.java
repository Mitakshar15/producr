package org.producr.api.mapper;


import jakarta.validation.Valid;
import org.mapstruct.Mapper;
import org.producr.api.data.domain.pack.SamplePack;
import org.producr.api.data.domain.track.AudioSample;
import org.producr.api.data.domain.track.Track;
import org.producr.api.dto.BaseApiResponse;
import org.producr.api.dtos.*;
import org.producr.api.utils.enums.AudioCategory;
import org.producr.api.utils.enums.SampleType;

import java.util.List;
import java.util.Set;

@Mapper(componentModel = "spring")
public interface TrackMgmtMapper {

  TrackUploadResponseDto toTrackUploadResponseDto(Track savedTrack);

  SampleType toSampleType(TrackUploadRequest.SampleTypeEnum sampleType);

  AudioCategory toAudioCategoryEnum(TrackUploadRequest.AudioCategoryEnum audioCategory);

  TrackUploadResponse toTrackUploadResponse(BaseApiResponse trackUploadSuccess);

  TrackFeedPageResponse toTrackFeedPageResponse(BaseApiResponse baseApiResponse);

  TrackFeedItemDto toTrackFeedItemDto(Track track);

  SamplePackResponse toSamplePackResponse(BaseApiResponse baseApiResponse);

  SamplePackDto toSamplePackDto(SamplePack samplePack);

  AudioSampleResponse toAudioSampleResponse(BaseApiResponse baseApiResponse);

  List<@Valid AudioSampleDto> toAudioSampleDtoList(Set<AudioSample> packPreviews);

  TrackUploadRequest.SampleTypeEnum toSampleTypeEnum(SampleType sampleType);

  PublishedSamplePackResponse toPublishedSamplePackResponse(BaseApiResponse baseApiResponse);

  List<@Valid SamplePackDto> toSamplePackDtoList(List<SamplePack> publishedPacks);

  TrackBaseApiResponse toTrackBaseApiResponse(BaseApiResponse baseApiResponse);
}
