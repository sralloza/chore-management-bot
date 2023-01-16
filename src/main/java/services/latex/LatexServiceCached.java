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
import java.nio.file.Paths;
import java.util.Base64;
import java.util.List;

import static constants.CacheConstants.LATEX_CACHE_EXPIRE_SECONDS;
import static org.apache.commons.codec.digest.MessageDigestAlgorithms.SHA_256;

@Slf4j
public class LatexServiceCached implements LatexService {
  private final LatexServiceNonCached latexService;
  private final RedisService redisService;

  @Inject
  public LatexServiceCached(LatexServiceNonCached latexService, RedisService redisService) {
    this.latexService = latexService;
    this.redisService = redisService;
  }

  @Override
  public void genTable(List<List<String>> data, String path) {
    String redisKey = getKey(data, path);
    if (getFromCache(redisKey, path)) {
      return;
    }
    latexService.genTable(data, path);
    saveToCache(redisKey, path);
  }

  private String getKey(List<List<String>> data, String prefix) {
    return prefix + "::" + new DigestUtils(SHA_256).digestAsHex(String.valueOf(data));
  }

  private boolean getFromCache(String key, String path) {
    String redisContent = redisService.get(key);
    if (redisContent != null) {
      log.debug("Found cached latex table for key {}", key);
      byte[] decodedBytes = Base64.getDecoder().decode(redisContent);
      try {
        FileUtils.writeByteArrayToFile(new File(path), decodedBytes);
      } catch (IOException e) {
        log.error("Error writing table " + path + " to file", e);
        throw new RuntimeException(e);
      }
      return true;
    }
    log.debug("No cached latex table for key {}", key);
    return false;
  }

  private void saveToCache(String key, String path) {
    Path file = Paths.get(path);
    byte[] dataBytes;
    try {
      dataBytes = Files.readAllBytes(file);
    } catch (IOException e) {
      log.error("Error reading table " + path + " from file", e);
      throw new RuntimeException(e);
    }
    String encodedString = Base64.getEncoder().encodeToString(dataBytes);
    redisService.setex(key, LATEX_CACHE_EXPIRE_SECONDS, encodedString);
  }
}
