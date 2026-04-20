package com.tomcode.api.blog.post.entity;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class TitleTest {
  @Test
  public void shouldCreateTitle() {
    // given
    String validTitle = "Title";

    // when
    Title title = new Title(validTitle);

    // then
    assertEquals(validTitle, title.getTitle());
  }

  @Test
  void shouldCreateTitle_whenLengthIsExactly3() {
    Title title = new Title("abc");
    assertEquals("abc", title.getTitle());
  }

  @Test
  void shouldCreateTitle_whenLengthIsExactly50() {
    String str = "a".repeat(50);
    Title title = new Title(str);
    assertEquals(str, title.getTitle());
  }

  @Test
  public void shouldThrowException_whenTitleTooLong() {
    String tooLongTitle =
        "errkfzSf1XKLBMsEnsqedaD9gZxHJrFHSSH9dk0JEE03IziaK0B"; // 51 characters (max 50)

    assertThrows(IllegalArgumentException.class, () -> new Title(tooLongTitle));
  }

  @Test
  public void shouldThrowException_whenTitleTooShort() {
    String tooShortTitle = "as"; // (min 3)

    assertThrows(IllegalArgumentException.class, () -> new Title(tooShortTitle));
  }

  @Test
  public void shouldThrowException_whenTitleIsNull() {
    assertThrows(IllegalArgumentException.class, () -> new Title(null));
  }

  @Test
  void shouldThrowException_whenTitleIsEmpty() {
    assertThrows(IllegalArgumentException.class, () -> new Title(""));
  }
}
