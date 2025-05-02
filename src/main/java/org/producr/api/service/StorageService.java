package org.producr.api.service;


import lombok.extern.slf4j.Slf4j;
import org.jaudiotagger.audio.AudioFile;
import org.jaudiotagger.audio.AudioFileIO;
import org.jaudiotagger.audio.AudioHeader;
import org.jaudiotagger.tag.FieldKey;
import org.jaudiotagger.tag.Tag;
import org.producr.api.utils.enums.TrackType;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.UnsupportedAudioFileException;
import java.io.*;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

@Service
@Slf4j
public class StorageService {


  @Value("${app.upload.storage-location}")
  private String audioStoragePath;

  @Value("${app.upload.base-url}")
  private String baseUrl;

  /**
   * Processes an audio file from the given URL/path and saves it to the storage
   *
   * @param audioFilePath URL or path to the audio file
   * @param userId ID of the user uploading the file
   * @param trackType Type of track (BEAT or SAMPLE)
   * @return The URL for accessing the stored file
   */
  public String processAndStoreAudioFile(String audioFilePath, String userId, TrackType trackType)
      throws IOException {
    try {
      // 1. Download or access the file from the provided path/URL
      byte[] audioData = downloadAudioFile(audioFilePath);

      // 2. Generate a unique filename
      String fileExtension = getFileExtensionFromPath(audioFilePath);
      String uniqueFilename = generateUniqueFilename(userId, trackType, fileExtension);

      // 3. Create the directory structure
      String directoryPath = getTrackTypePath(trackType, userId);
      Path uploadDir = Paths.get(audioStoragePath, directoryPath);

      if (!Files.exists(uploadDir)) {
        Files.createDirectories(uploadDir);
      }

      // 4. Save the file
      Path filePath = uploadDir.resolve(uniqueFilename);
      Files.write(filePath, audioData);

      // 5. Generate waveform data (optional, could be a separate method)use in service mthod only
      // generateWaveformData(audioData, filePath.toString());

      // 6. Return the public URL for accessing the file
      return baseUrl + "/media/" + directoryPath + "/" + uniqueFilename;

    } catch (Exception e) {
      log.error("Failed to process and store audio file", e);
      throw new IOException("Failed to process audio file: " + e.getMessage());
    }
  }

  /**
   * Downloads audio file from a URL or reads from a local path
   */
  private byte[] downloadAudioFile(String audioFilePath) throws IOException {
    // Check if it's a URL or local path
    if (audioFilePath.startsWith("http://") || audioFilePath.startsWith("https://")) {
      // It's a URL, download it
      URL url = new URL(audioFilePath);
      try (InputStream in = url.openStream()) {
        return in.readAllBytes();
      }
    } else {
      // It's a local path, read it
      Path path = Paths.get(audioFilePath);
      return Files.readAllBytes(path);
    }
  }

  public Map<String, Object> extractAudioMetadata(String audioFilePath) {
    Map<String, Object> metadata = new HashMap<>();

    try {
      File file = new File(audioFilePath);
      if (!file.exists()) {
        throw new IllegalArgumentException("Audio file does not exist: " + audioFilePath);
      }

      // Read audio file using JAudioTagger
      AudioFile audioFile = AudioFileIO.read(file);
      AudioHeader audioHeader = audioFile.getAudioHeader();
      Tag tag = audioFile.getTag();

      // Extract file information
      metadata.put("fileSizeBytes", file.length());
      metadata.put("fileFormat", audioHeader.getFormat());

      // Extract technical audio properties
      metadata.put("sampleRate", audioHeader.getSampleRateAsNumber());
      metadata.put("bitRate", audioHeader.getBitRateAsNumber());
      metadata.put("channels", audioHeader.getChannels());
      metadata.put("durationSeconds", audioHeader.getTrackLength());
      metadata.put("isVariableBitRate", audioHeader.isVariableBitRate());
      metadata.put("isLossless", audioHeader.isLossless());

      // Extract tag metadata if available
      if (tag != null) {
        metadata.put("artist", getTagField(tag, FieldKey.ARTIST));
        metadata.put("album", getTagField(tag, FieldKey.ALBUM));
        metadata.put("title", getTagField(tag, FieldKey.TITLE));
        metadata.put("track", getTagField(tag, FieldKey.TRACK));
        metadata.put("year", getTagField(tag, FieldKey.YEAR));
        metadata.put("genre", getTagField(tag, FieldKey.GENRE));
        metadata.put("bpm", getTagField(tag, FieldKey.BPM));
      }

      return metadata;
    } catch (Exception e) {
      log.error("Failed to extract audio metadata for file: " + audioFilePath + ", error: "
          + e.getMessage());
      // Return default values on failure
      metadata.put("fileSizeBytes", 0L);
      metadata.put("fileFormat", "UNKNOWN");
      metadata.put("sampleRate", 0);
      metadata.put("bitRate", 0);
      metadata.put("channels", "0");
      metadata.put("durationSeconds", 0);
      metadata.put("isVariableBitRate", false);
      return metadata;
    }
  }

  private String getTagField(Tag tag, FieldKey fieldKey) {
    try {
      String value = tag.getFirst(fieldKey);
      return value != null && !value.isEmpty() ? value : null;
    } catch (Exception e) {
      return null;
    }
  }

  public String generateWaveformData(String audioFilePath)
      throws IOException, UnsupportedAudioFileException {
    log.info("Generating waveform data for {}", audioFilePath);

    File audioFile = new File(audioFilePath);
    if (!audioFile.exists()) {
      throw new IOException("Audio file does not exist: " + audioFilePath);
    }


    if (getFileExtensionFromPath(audioFilePath).equals(".mp3")) {
      return generateMP3WaveformData(audioFilePath);
    }

    // Open the audio file
    AudioInputStream audioInputStream = AudioSystem.getAudioInputStream(audioFile);
    AudioFormat format = audioInputStream.getFormat();

    // Get audio format details
    int channels = format.getChannels();
    int frameSize = format.getFrameSize();
    int sampleSizeInBits = format.getSampleSizeInBits();

    // Read the audio data
    byte[] audioBytes = new byte[(int) (audioInputStream.getFrameLength() * frameSize)];
    int bytesRead = audioInputStream.read(audioBytes);

    if (bytesRead <= 0) {
      throw new IOException("Failed to read audio data from file");
    }

    // For waveform visualization, we typically want ~1000-2000 data points
    // regardless of audio length to keep visualization size reasonable
    int desiredDataPoints = 1000;
    int samplesPerDataPoint =
        Math.max(1, audioBytes.length / (desiredDataPoints * (sampleSizeInBits / 8) * channels));

    List<Integer> waveformData = new ArrayList<>();

    // Process audio data to generate waveform points
    for (int i = 0; i < audioBytes.length - frameSize; i += samplesPerDataPoint * frameSize) {
      // Calculate amplitude for this sample
      int amplitude = 0;

      if (sampleSizeInBits == 8) {
        // 8-bit audio is unsigned
        amplitude = audioBytes[i] & 0xff;
      } else if (sampleSizeInBits == 16) {
        // 16-bit audio is signed, little endian
        amplitude = (audioBytes[i + 1] << 8) | (audioBytes[i] & 0xff);
      }

      // Normalize to a 0-100 range for consistent visualization
      int normalizedAmplitude = Math.abs(amplitude * 100 / (1 << (sampleSizeInBits - 1)));
      waveformData.add(normalizedAmplitude);
    }

    // Close the input stream
    audioInputStream.close();

    // Convert waveform data to JSON
    StringBuilder jsonBuilder = new StringBuilder();
    jsonBuilder.append("[");
    for (int i = 0; i < waveformData.size(); i++) {
      jsonBuilder.append(waveformData.get(i));
      if (i < waveformData.size() - 1) {
        jsonBuilder.append(",");
      }
    }
    jsonBuilder.append("]");

    return jsonBuilder.toString();
  }


  /**
   * Generates waveform data for MP3 files.
   *
   * @param mp3FilePath Path to the MP3 file
   * @return JSON string containing the waveform data points
   * @throws IOException If there is an error reading the file
   * @throws UnsupportedAudioFileException If the file format is not supported
   */
  public String generateMP3WaveformData(String mp3FilePath)
      throws IOException, UnsupportedAudioFileException {
    log.info("Generating waveform data for MP3 file: {}", mp3FilePath);

    File mp3File = new File(mp3FilePath);
    if (!mp3File.exists()) {
      throw new IOException("MP3 file does not exist: " + mp3FilePath);
    }

    // Ensure MP3SPI is registered with the system
    try {
      Class.forName("javazoom.spi.mpeg.sampled.file.MpegAudioFileReader");
    } catch (ClassNotFoundException e) {
      throw new IOException("MP3SPI library is required to process MP3 files", e);
    }

    // Open the MP3 file
    AudioInputStream mp3Stream = AudioSystem.getAudioInputStream(mp3File);
    AudioFormat baseFormat = mp3Stream.getFormat();

    // Convert MP3 format to PCM if necessary
    AudioInputStream audioInputStream;
    if (baseFormat.getEncoding() != AudioFormat.Encoding.PCM_SIGNED) {
      AudioFormat decodedFormat = new AudioFormat(AudioFormat.Encoding.PCM_SIGNED,
          baseFormat.getSampleRate(), 16, baseFormat.getChannels(), baseFormat.getChannels() * 2,
          baseFormat.getSampleRate(), false);
      audioInputStream = AudioSystem.getAudioInputStream(decodedFormat, mp3Stream);
    } else {
      audioInputStream = mp3Stream;
    }

    AudioFormat format = audioInputStream.getFormat();
    int channels = format.getChannels();
    int frameSize = format.getFrameSize();
    int sampleSizeInBits = format.getSampleSizeInBits();

    // Create a buffer for reading
    int bufferSize = 4096;
    byte[] buffer = new byte[bufferSize];
    ByteArrayOutputStream byteStream = new ByteArrayOutputStream();

    // Read the entire audio stream
    int bytesRead;
    while ((bytesRead = audioInputStream.read(buffer, 0, buffer.length)) != -1) {
      byteStream.write(buffer, 0, bytesRead);
    }

    byte[] audioBytes = byteStream.toByteArray();

    // For waveform visualization, aim for ~1000-2000 data points
    int desiredDataPoints = 1000;
    int samplesPerDataPoint =
        Math.max(1, audioBytes.length / (desiredDataPoints * (sampleSizeInBits / 8) * channels));

    List<Integer> waveformData = new ArrayList<>();

    // Process audio data to generate waveform points
    for (int i = 0; i < audioBytes.length - frameSize; i += samplesPerDataPoint * frameSize) {
      // Calculate amplitude for this sample
      int amplitude = 0;

      if (sampleSizeInBits == 8) {
        // 8-bit audio is unsigned
        amplitude = audioBytes[i] & 0xff;
      } else if (sampleSizeInBits == 16) {
        // 16-bit audio is signed, little endian
        amplitude = (audioBytes[i + 1] << 8) | (audioBytes[i] & 0xff);
      }

      // Normalize to a 0-100 range for consistent visualization
      int normalizedAmplitude = Math.abs(amplitude * 100 / (1 << (sampleSizeInBits - 1)));
      waveformData.add(normalizedAmplitude);
    }

    // Close the input streams
    audioInputStream.close();
    mp3Stream.close();

    // Convert waveform data to JSON
    StringBuilder jsonBuilder = new StringBuilder();
    jsonBuilder.append("[");
    for (int i = 0; i < waveformData.size(); i++) {
      jsonBuilder.append(waveformData.get(i));
      if (i < waveformData.size() - 1) {
        jsonBuilder.append(",");
      }
    }
    jsonBuilder.append("]");

    return jsonBuilder.toString();
  }



  private String getTrackTypePath(TrackType trackType, String userId) {
    return trackType.toString().toLowerCase() + "/" + userId.substring(0, 2) + "/" + userId;
  }

  private String generateUniqueFilename(String userId, TrackType trackType, String fileExtension) {
    String timestamp = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMddHHmmss"));
    String uuid = UUID.randomUUID().toString().substring(0, 8);
    return trackType.toString().toLowerCase() + "_" + timestamp + "_" + uuid + fileExtension;
  }

  private String getFileExtensionFromPath(String path) {
    int lastDotIndex = path.lastIndexOf(".");
    if (lastDotIndex > 0) {
      return path.substring(lastDotIndex);
    }
    // Default to .mp3 if no extension found
    return ".mp3";
  }
  // For cloud storage integration (e.g., AWS S3, Google Cloud Storage)
  // Implement alternative versions of the above methods using cloud SDKs
}
