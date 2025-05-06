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
import org.producr.api.utils.ProducrUtils;
import org.producr.api.utils.StorageUtils;
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
  private final StorageUtils storageUtils;
  private final TrackRepository trackRepository;
  private final TrackMgmtMapper mapper;
  private final ProducrUtils producrUtils;

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
            String audioFileUrl = storageUtils.processAndStoreAudioFile(
                    requestDto.getAudioFilePath(), userName, trackType);

            // 3. Extract metadata from the audio file
            Map<String, Object> metadata = storageUtils.extractAudioMetadata(requestDto.getAudioFilePath());
            Integer trackLengthSeconds = (Integer) metadata.getOrDefault("durationSeconds", 0);

            // 4. Create the appropriate track entity based on type
            Track track;
            if (requestDto.getTrackType().getValue().equalsIgnoreCase(TrackType.BEAT.toString())) {
                track = producrUtils.createBeat(requestDto, producer, audioFileUrl, trackLengthSeconds, metadata);
            } else if (requestDto.getTrackType().getValue().equalsIgnoreCase(TrackType.SAMPLE.toString())) {
                track = producrUtils.createAudioSample(requestDto, producer, audioFileUrl, trackLengthSeconds, metadata);
            } else {
                throw new IllegalArgumentException("Unsupported track type: " + requestDto.getTrackType());
            }
            //waveform data marked for removal, as it is uneccesarry
            String waveformData = storageUtils.generateWaveformData(requestDto.getAudioFilePath());
            track.setWaveformData(waveformData);
            // 5. Save the track
            Track savedTrack = trackRepository.save(track);

            return mapper.toTrackUploadResponseDto(savedTrack);

        } catch (IOException e) {
            log.error("Failed to upload track", e);
            throw new RuntimeException("Failed to upload track: " + e.getMessage(), e);
        }
    }


}
