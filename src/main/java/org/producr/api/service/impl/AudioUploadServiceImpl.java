package org.producr.api.service.impl;

import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.producr.api.data.domain.track.AudioSample;
import org.producr.api.data.domain.track.Beat;
import org.producr.api.data.domain.track.Track;
import org.producr.api.data.domain.track.TrackMetadata;
import org.producr.api.data.domain.user.User;
import org.producr.api.data.repository.TrackRepository;
import org.producr.api.data.repository.UserRepository;
import org.producr.api.dtos.TrackUploadRequest;
import org.producr.api.dtos.TrackUploadResponseDto;
import org.producr.api.mapper.TrackMgmtMapper;
import org.producr.api.service.StorageService;
import org.producr.api.service.interfaces.AudioUploadService;
import org.producr.api.utils.enums.SampleType;
import org.producr.api.utils.enums.TrackType;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.sound.sampled.UnsupportedAudioFileException;
import java.io.IOException;
import java.util.Map;
import java.util.Objects;

@Service
@RequiredArgsConstructor
@Slf4j
public class AudioUploadServiceImpl implements AudioUploadService {

  private final UserRepository userRepository;
  private final StorageService storageService;
  private final TrackRepository trackRepository;
  private final TrackMgmtMapper mapper;

  @Override
  @Transactional
    public TrackUploadResponseDto uploadTrack(String userName, TrackUploadRequest requestDto) throws IOException, UnsupportedAudioFileException {
        try {
            // 1. Validate user exists
            User producer = userRepository.findByEmailOrUsername(userName)
                    .orElseThrow(() -> new EntityNotFoundException("User not found with ID: " + userName));
            TrackType trackType = null;
            switch (Objects.requireNonNull(requestDto.getTrackType())) {
                case BEAT -> trackType = TrackType.BEAT;
                case SAMPLE -> trackType = TrackType.SAMPLE;
            }
            // 2. Process and store the audio file
            String audioFileUrl = storageService.processAndStoreAudioFile(
                    requestDto.getAudioFilePath(), userName, trackType);

            // 3. Extract metadata from the audio file
            Map<String, Object> metadata = storageService.extractAudioMetadata(requestDto.getAudioFilePath());
            Integer trackLengthSeconds = (Integer) metadata.getOrDefault("durationSeconds", 0);

            // 4. Create the appropriate track entity based on type
            Track track;
            if (requestDto.getTrackType().getValue().equalsIgnoreCase(TrackType.BEAT.toString())) {
                track = createBeat(requestDto, producer, audioFileUrl, trackLengthSeconds, metadata);
            } else if (requestDto.getTrackType().getValue().equalsIgnoreCase(TrackType.SAMPLE.toString())) {
                track = createAudioSample(requestDto, producer, audioFileUrl, trackLengthSeconds, metadata);
            } else {
                throw new IllegalArgumentException("Unsupported track type: " + requestDto.getTrackType());
            }
            String waveformData = storageService.generateWaveformData(requestDto.getAudioFilePath());
            track.setWaveformData(waveformData);
            // 5. Save the track
            Track savedTrack = trackRepository.save(track);

            TrackUploadResponseDto dto = mapper.toTrackUploadResponseDto(savedTrack);
            return dto;

        } catch (IOException e) {
            log.error("Failed to upload track", e);
            throw new RuntimeException("Failed to upload track: " + e.getMessage(), e);
        }
    }

  private Beat createBeat(TrackUploadRequest requestDto, User producer, String audioFileUrl,
      Integer trackLengthSeconds, Map<String, Object> metadata) {
    // Create base track
    Track baseTrack = new Track();
    baseTrack.setTitle(requestDto.getTitle());
    baseTrack.setDescription(requestDto.getDescription());
    baseTrack.setProducer(producer);
    baseTrack.setAudioFileUrl(audioFileUrl);
    baseTrack.setTrackLengthSeconds(trackLengthSeconds);
    baseTrack.setPublic(requestDto.getIsPublic());
    baseTrack.setDownloadable(requestDto.getIsDownloadable());

    // Create beat from base track
    Beat beat = new Beat(baseTrack);

    // Set beat-specific properties
    beat.setBpm(requestDto.getBpm());
    beat.setKey(requestDto.getKey());
    beat.setTimeSignature(
        requestDto.getTimeSignature() != null ? requestDto.getTimeSignature() : "4/4");

    if (requestDto.getGenres() != null) {
      beat.setGenres(requestDto.getGenres());
    }

    if (requestDto.getMoods() != null) {
      beat.setMoods(requestDto.getMoods());
    }

    // Create and link track metadata
    TrackMetadata trackMetadata = createTrackMetadata(beat, requestDto, metadata);
    beat.setMetadata(trackMetadata);

    return beat;
  }

  private AudioSample createAudioSample(TrackUploadRequest requestDto, User producer,
      String audioFileUrl, Integer trackLengthSeconds, Map<String, Object> metadata) {
    // Create base track
    Track baseTrack = new Track();
    baseTrack.setTitle(requestDto.getTitle());
    baseTrack.setDescription(requestDto.getDescription());
    baseTrack.setProducer(producer);
    baseTrack.setAudioFileUrl(audioFileUrl);
    baseTrack.setTrackLengthSeconds(trackLengthSeconds);
    // baseTrack.setPublic(requestDto.isPublic());
    // baseTrack.setDownloadable(requestDto.isDownloadable());

    // Create audio sample from base track
    AudioSample sample = new AudioSample(baseTrack);
    SampleType sampleType = null;

    // Set sample-specific properties
    sample.setSampleType(
        requestDto.getSampleType() != null ? mapper.toSampleTypeEnum(requestDto.getSampleType())
            : SampleType.ONE_SHOT);
    sample.setRootNote(requestDto.getRootNote());
    sample.setLoop(requestDto.getIsLoop());

    if (requestDto.getIsLoop()) {
      sample.setLoopTempo(requestDto.getLoopTempo());
    }

    sample.setAudioCategory(mapper.toAudioCategoryEnum(requestDto.getAudioCategory()));

    // Create and link track metadata
    TrackMetadata trackMetadata = createTrackMetadata(sample, requestDto, metadata);
    sample.setMetadata(trackMetadata);

    return sample;
  }

  private TrackMetadata createTrackMetadata(Track track, TrackUploadRequest requestDto,
      Map<String, Object> metadata) {
    TrackMetadata trackMetadata = new TrackMetadata();
    trackMetadata.setTrack(track);

    // Set musical properties
    trackMetadata.setBpm(requestDto.getBpm());
    trackMetadata.setKey(requestDto.getKey());

    // Set genre from beat if available
    if (requestDto.getTrackType().getValue().equalsIgnoreCase(TrackType.BEAT.toString())
        && requestDto.getGenres() != null && !requestDto.getGenres().isEmpty()) {
      trackMetadata.setGenre(requestDto.getGenres().iterator().next()); // Use first genre
    }

    // Set mood from beat if available
    if (requestDto.getTrackType().getValue().equalsIgnoreCase(TrackType.BEAT.toString())
        && requestDto.getMoods() != null && !requestDto.getMoods().isEmpty()) {
      trackMetadata.setMood(requestDto.getMoods().iterator().next()); // Use first mood
    }

    // Set loop information for samples
    if (requestDto.getTrackType().getValue().equalsIgnoreCase(TrackType.SAMPLE.toString())) {
      trackMetadata.setLoop(requestDto.getIsLoop());
    }

    // Set technical metadata from file analysis
    trackMetadata.setSampleRate((Integer) metadata.getOrDefault("sampleRate", 44100));
    trackMetadata.setBitDepth((Integer) metadata.getOrDefault("bitDepth", 16));
    trackMetadata.setFileFormat((String) metadata.getOrDefault("fileFormat", "MP3"));
    trackMetadata.setFileSizeBytes((Long) metadata.getOrDefault("fileSizeBytes", 0L));

    return trackMetadata;
  }
}
