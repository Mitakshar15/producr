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

import javax.sound.sampled.*;
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
      //generateWaveformData(audioData, filePath.toString());

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
      metadata.put("isLossless",audioHeader.isLossless());

      // Extract tag metadata if available
      if (tag != null) {
        metadata.put("artist", getTagField(tag, FieldKey.ARTIST));
        metadata.put("album", getTagField(tag, FieldKey.ALBUM));
        metadata.put("title", getTagField(tag, FieldKey.TITLE));
        metadata.put("track", getTagField(tag, FieldKey.TRACK));
        metadata.put("year", getTagField(tag, FieldKey.YEAR));
        metadata.put("genre", getTagField(tag, FieldKey.GENRE));
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

  public String generateWaveformData(String audioFilePath) throws IOException, UnsupportedAudioFileException {
    log.info("Generating waveform data for {}", audioFilePath);
    File audioFile = new File(audioFilePath);
    if (!audioFile.exists()) {
      throw new IOException("Audio file does not exist: " + audioFilePath);
    }

    File wavFile = audioFile;
    boolean isTemporaryFile = false;

    // Check if the file is an MP3 file
    if (audioFilePath.toLowerCase().endsWith(".mp3")) {
      log.info("Converting MP3 to WAV format for processing");
      // Create a temporary WAV file
      wavFile = File.createTempFile("temp_converted_audio", ".wav");
      isTemporaryFile = true;

      // Convert MP3 to WAV
      try {
        convertMp3ToWavUsingJavaLayer(audioFile, wavFile);
        log.info("Successfully converted MP3 to WAV: {}", wavFile.getAbsolutePath());
      } catch (Exception e) {
        if (isTemporaryFile && wavFile.exists()) {
          wavFile.delete();
        }
        throw new IOException("Failed to convert MP3 to WAV: " + e.getMessage(), e);
      }
    }

    return getWaveFormDataFromFile(wavFile);

  }


  private String getWaveFormDataFromFile(File audioFile) throws IOException, UnsupportedAudioFileException {
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
    int samplesPerDataPoint = Math.max(1, audioBytes.length / (desiredDataPoints * (sampleSizeInBits / 8) * channels));

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
   * Converts an MP3 file to WAV format
   * @param mp3File the source MP3 file
   * @param wavFile the destination WAV file
   */
  private void convertMp3ToWavUsingJavaLayer(File mp3File, File wavFile) throws Exception {
    FileOutputStream fos = null;
    BufferedOutputStream bos = null;

    try {
      // Set up MP3 input
      FileInputStream fis = new FileInputStream(mp3File);
      BufferedInputStream bis = new BufferedInputStream(fis);

      // Set up JavaLayer MP3 decoder
      javazoom.jl.decoder.Bitstream bitstream = new javazoom.jl.decoder.Bitstream(bis);
      javazoom.jl.decoder.Decoder decoder = new javazoom.jl.decoder.Decoder();

      // Get header to extract format information
      javazoom.jl.decoder.Header header = bitstream.readFrame();
      int channels = (header.mode() == javazoom.jl.decoder.Header.SINGLE_CHANNEL) ? 1 : 2;
      int sampleRate = header.frequency();

      // Reset stream position
      fis.close();
      fis = new FileInputStream(mp3File);
      bis = new BufferedInputStream(fis);
      bitstream = new javazoom.jl.decoder.Bitstream(bis);

      // Set up WAV output
      fos = new FileOutputStream(wavFile);
      bos = new BufferedOutputStream(fos);

      // Write WAV header
      writeWavHeader(bos, channels, sampleRate, 16);

      // Process frames
      int frameCount = 0;
      boolean done = false;

      while (!done) {
        try {
          header = bitstream.readFrame();
          if (header == null) {
            done = true;
          } else {
            javazoom.jl.decoder.SampleBuffer output = (javazoom.jl.decoder.SampleBuffer) decoder.decodeFrame(header, bitstream);
            short[] pcm = output.getBuffer();

            // Write PCM data to WAV file
            for (short sample : pcm) {
              bos.write(sample & 0xff);
              bos.write((sample >> 8) & 0xff);
            }

            frameCount++;
            bitstream.closeFrame();
          }
        } catch (Exception e) {
          log.warn("Error processing MP3 frame, skipping: {}", e.getMessage());
          bitstream.closeFrame();
        }
      }

      // Update WAV header with final size
      updateWavHeader(wavFile);

      log.info("Converted MP3 to WAV using JavaLayer decoder, processed {} frames", frameCount);
    } catch (Exception e) {
      log.error("Error in JavaLayer MP3 to WAV conversion", e);
      throw new Exception("JavaLayer MP3 conversion failed: " + e.getMessage(), e);
    } finally {
      if (bos != null) try { bos.close(); } catch (IOException e) { /* ignore */ }
      if (fos != null) try { fos.close(); } catch (IOException e) { /* ignore */ }
    }
  }

  private void writeWavHeader(OutputStream out, int channels, int sampleRate, int bitsPerSample) throws IOException {
    // RIFF header
    writeString(out, "RIFF"); // chunk id
    writeInt(out, 0); // chunk size (placeholder, will be updated later)
    writeString(out, "WAVE"); // format

    // fmt subchunk
    writeString(out, "fmt "); // subchunk1 id
    writeInt(out, 16); // subchunk1 size (16 for PCM)
    writeShort(out, (short) 1); // audio format (1 for PCM)
    writeShort(out, (short) channels); // number of channels
    writeInt(out, sampleRate); // sample rate
    int byteRate = sampleRate * channels * bitsPerSample / 8;
    writeInt(out, byteRate); // byte rate
    writeShort(out, (short) (channels * bitsPerSample / 8)); // block align
    writeShort(out, (short) bitsPerSample); // bits per sample

    // data subchunk
    writeString(out, "data"); // subchunk2 id
    writeInt(out, 0); // subchunk2 size (placeholder, will be updated later)
  }

  private void updateWavHeader(File wavFile) throws IOException {
    RandomAccessFile raf = new RandomAccessFile(wavFile, "rw");
    long fileLength = raf.length();

    // Update chunk size in the RIFF header (fileLength - 8)
    raf.seek(4);
    raf.writeInt(Integer.reverseBytes((int) (fileLength - 8)));

    // Update subchunk2 size in the data header (fileLength - 44)
    raf.seek(40);
    raf.writeInt(Integer.reverseBytes((int) (fileLength - 44)));

    raf.close();
  }

  private void writeInt(OutputStream out, int value) throws IOException {
    out.write(value & 0xff);
    out.write((value >> 8) & 0xff);
    out.write((value >> 16) & 0xff);
    out.write((value >> 24) & 0xff);
  }

  private void writeShort(OutputStream out, short value) throws IOException {
    out.write(value & 0xff);
    out.write((value >> 8) & 0xff);
  }

  private void writeString(OutputStream out, String value) throws IOException {
    for (int i = 0; i < value.length(); i++) {
      out.write(value.charAt(i));
    }
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
