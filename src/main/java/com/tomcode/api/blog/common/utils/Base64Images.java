package com.tomcode.api.blog.common.utils;

import java.util.Base64;
import java.util.Set;

public final class Base64Images {
  private static final Set<String> ALLOWED_MIME =
      Set.of("image/jpeg", "image/png", "image/webp", "image/gif");

  public static String[] splitDataUrl(String input) {
    if (input == null) return new String[] {null, null};
    String s = input.trim();
    if (s.regionMatches(true, 0, "data:", 0, 5)) {
      int comma = s.indexOf(',');
      if (comma < 0) return new String[] {null, null};
      String meta = s.substring(5, comma);
      String data = s.substring(comma + 1);
      String mime = meta.contains(";") ? meta.substring(0, meta.indexOf(';')) : meta;
      return new String[] {mime, data};
    }
    return new String[] {null, s};
  }

  public static String normalizeRawBase64(String raw) {
    return raw == null ? null : raw.replaceAll("\\s+", "");
  }

  public static boolean isValidImageBase64(String input) {
    if (input == null || input.isBlank()) return false;
    String[] parts = splitDataUrl(input);
    String mime = parts[0];
    String raw = normalizeRawBase64(parts[1]);

    if (raw == null) return false;

    if (mime != null && !ALLOWED_MIME.contains(mime.toLowerCase())) {
      return false;
    }
    try {
      Base64.getDecoder().decode(raw);
      return true;
    } catch (IllegalArgumentException e) {
      return false;
    }
  }
}
