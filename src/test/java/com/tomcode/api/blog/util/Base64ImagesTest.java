package com.tomcode.api.blog.util;

import static org.junit.jupiter.api.Assertions.*;

import com.tomcode.api.blog.common.utils.Base64Images;
import org.junit.jupiter.api.Test;

class Base64ImagesTest {

  // Simple valid Base64 - "hello"
  private static final String VALID_BASE64 = "aGVsbG8=";

  // Same but with data url
  private static final String VALID_DATA_URL = "data:image/jpeg;base64," + VALID_BASE64;

  // Invalid Base64. Characters not from alphabet
  private static final String INVALID_BASE64 = "not@base64!!";

  // Valid mime; invalid Base64
  private static final String BAD_DATA_URL = "data:image/png;base64," + INVALID_BASE64;

  // Unsupported mime
  private static final String UNSUPPORTED_MIME_URL = "data:text/plain;base64," + VALID_BASE64;

  // -----------------------------
  // isValidImageBase64 tests
  // -----------------------------

  @Test
  void shouldReturnTrue_forValidRawBase64() {
    assertTrue(Base64Images.isValidImageBase64(VALID_BASE64));
  }

  @Test
  void shouldReturnTrue_forValidDataUrl() {
    assertTrue(Base64Images.isValidImageBase64(VALID_DATA_URL));
  }

  @Test
  void shouldReturnTrue_forValidDataUrlWithWhitespaces() {
    String spaced = "data:image/jpeg;base64, aG V s b G8 = ";
    assertTrue(Base64Images.isValidImageBase64(spaced));
  }

  @Test
  void shouldReturnFalse_forInvalidBase64() {
    assertFalse(Base64Images.isValidImageBase64(INVALID_BASE64));
  }

  @Test
  void shouldReturnFalse_forBadDataUrl() {
    assertFalse(Base64Images.isValidImageBase64(BAD_DATA_URL));
  }

  @Test
  void shouldReturnFalse_forUnsupportedMime() {
    assertFalse(Base64Images.isValidImageBase64(UNSUPPORTED_MIME_URL));
  }

  @Test
  void shouldReturnFalse_forNullOrBlank() {
    assertFalse(Base64Images.isValidImageBase64(null));
    assertFalse(Base64Images.isValidImageBase64(""));
    assertFalse(Base64Images.isValidImageBase64("   "));
  }

  @Test
  void shouldReturnFalse_forBrokenDataUrl_noComma() {
    String broken = "data:image/jpeg;base64" + VALID_BASE64; // brak przecinka
    assertFalse(Base64Images.isValidImageBase64(broken));
  }

  // -----------------------------
  // splitDataUrl tests
  // -----------------------------

  @Test
  void shouldSplitDataUrlIntoMimeAndData() {
    String[] parts = Base64Images.splitDataUrl(VALID_DATA_URL);
    assertEquals("image/jpeg", parts[0]);
    assertEquals(VALID_BASE64, parts[1]);
  }

  @Test
  void shouldReturnNullMimeForRawBase64() {
    String[] parts = Base64Images.splitDataUrl(VALID_BASE64);
    assertNull(parts[0]);
    assertEquals(VALID_BASE64, parts[1]);
  }

  @Test
  void shouldReturnNullsForBrokenDataUrl() {
    String[] parts = Base64Images.splitDataUrl("data:image/jpeg;base64"); // brak przecinka
    assertNull(parts[0]);
    assertNull(parts[1]);
  }

  // -----------------------------
  // normalizeRawBase64 tests
  // -----------------------------

  @Test
  void shouldRemoveWhitespacesFromBase64() {
    String dirty = "a G V s b G 8 =  ";
    String normalized = Base64Images.normalizeRawBase64(dirty);
    assertEquals("aGVsbG8=", normalized);
  }

  @Test
  void shouldReturnNullWhenInputNull() {
    assertNull(Base64Images.normalizeRawBase64(null));
  }
}
