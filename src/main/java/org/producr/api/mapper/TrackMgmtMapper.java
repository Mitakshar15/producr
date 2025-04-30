package org.producr.api.mapper;


import org.mapstruct.Mapper;
import org.producr.api.data.domain.track.Track;
import org.producr.api.dto.BaseApiResponse;
import org.producr.api.dtos.TrackUploadRequest;
import org.producr.api.dtos.TrackUploadResponse;
import org.producr.api.dtos.TrackUploadResponseDto;
import org.producr.api.utils.enums.AudioCategory;
import org.producr.api.utils.enums.SampleType;

@Mapper(componentModel = "spring")
public interface TrackMgmtMapper {

  TrackUploadResponseDto toTrackUploadResponseDto(Track savedTrack);

  SampleType toSampleTypeEnum(TrackUploadRequest.SampleTypeEnum sampleType);

  AudioCategory toAudioCategoryEnum(TrackUploadRequest.AudioCategoryEnum audioCategory);

  TrackUploadResponse toTrackUploadResponse(BaseApiResponse trackUploadSuccess);
}
