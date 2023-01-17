package services.latex;

import com.google.inject.Inject;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.codec.digest.DigestUtils;
import org.apache.commons.io.FileUtils;
import services.RedisService;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Base64;
import java.util.List;
import java.util.Optional;

import static constants.CacheConstants.LATEX_CACHE_EXPIRE_SECONDS;
import static org.apache.commons.codec.digest.MessageDigestAlgorithms.SHA_256;

@Slf4j
public class LatexServiceCached extends BaseLatexService implements LatexService {
  private final LatexServiceNonCached latexService;
  private final RedisService redisService;

  @Inject
  public LatexServiceCached(LatexServiceNonCached latexService, RedisService redisService) {
    this.latexService = latexService;
    this.redisService = redisService;
  }

  @Override
  public File genTable(List<List<String>> data, String redisKeyPrefix) {
    String redisKey = getKey(data, redisKeyPrefix);
    var cacheResult = getFromCache(redisKey, redisKeyPrefix);
    if (cacheResult.isPresent()) {
      return cacheResult.get();
    }
    File file = latexService.genTable(data, redisKeyPrefix);
    saveToCache(redisKey, file);
    return file;
  }

  private String getKey(List<List<String>> data, String redisKeyPrefix) {
    return redisKeyPrefix + "::" + new DigestUtils(SHA_256).digestAsHex(String.valueOf(data));
  }

  private Optional<File> getFromCache(String key, String redisKeyPrefix) {
    String redisContent = redisService.get(key);
    if (redisContent != null) {
      log.debug("Found cached latex table for key {}", key);
      byte[] decodedBytes = Base64.getDecoder().decode(redisContent);
      File file = new File(getFileName(redisKeyPrefix));
      try {
        FileUtils.writeByteArrayToFile(file, decodedBytes);
      } catch (IOException e) {
        log.error("Error writing table " + file.getAbsolutePath() + " to file", e);
        throw new RuntimeException(e);
      }
      return Optional.of(file);
    }
    log.debug("No cached latex table for key {}", key);
    return Optional.empty();
  }

  private void saveToCache(String key, File file) {
    byte[] dataBytes;
    try {
      dataBytes = Files.readAllBytes(Path.of(file.getAbsolutePath()));
    } catch (IOException e) {
      log.error("Error reading table " + file.getAbsolutePath() + " from file", e);
      throw new RuntimeException(e);
    }
    String encodedString = Base64.getEncoder().encodeToString(dataBytes);
    redisService.setex(key, LATEX_CACHE_EXPIRE_SECONDS, encodedString);
  }
}
