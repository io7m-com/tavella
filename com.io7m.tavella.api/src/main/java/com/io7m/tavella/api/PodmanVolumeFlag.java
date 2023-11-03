/*
 * Copyright © 2023 Mark Raynsford <code@io7m.com> https://www.io7m.com
 *
 * Permission to use, copy, modify, and/or distribute this software for any
 * purpose with or without fee is hereby granted, provided that the above
 * copyright notice and this permission notice appear in all copies.
 *
 * THE SOFTWARE IS PROVIDED "AS IS" AND THE AUTHOR DISCLAIMS ALL WARRANTIES
 * WITH REGARD TO THIS SOFTWARE INCLUDING ALL IMPLIED WARRANTIES OF
 * MERCHANTABILITY AND FITNESS. IN NO EVENT SHALL THE AUTHOR BE LIABLE FOR ANY
 * SPECIAL, DIRECT, INDIRECT, OR CONSEQUENTIAL DAMAGES OR ANY DAMAGES
 * WHATSOEVER RESULTING FROM LOSS OF USE, DATA OR PROFITS, WHETHER IN AN
 * ACTION OF CONTRACT, NEGLIGENCE OR OTHER TORTIOUS ACTION, ARISING OUT OF OR
 * IN CONNECTION WITH THE USE OR PERFORMANCE OF THIS SOFTWARE.
 */


package com.io7m.tavella.api;

import java.util.Objects;

/**
 * Volume mount flags.
 */

public enum PodmanVolumeFlag
{
  /**
   * The volume is mounted read/write.
   */

  READ_WRITE("rw"),

  /**
   * The volume is mounted read-only.
   */

  READ_ONLY("ro"),

  /**
   * The volume is labelled such that the volume can be shared between
   * containers.
   */

  SELINUX_LABEL_SHARED("z"),

  /**
   * The volume is labelled such that the volume cannot be shared between
   * containers.
   */

  SELINUX_LABEL_PRIVATE("Z"),

  /**
   * Mount the host directory as an overlay filesystem.
   */

  MOUNT_AS_OVERLAY("O"),

  /**
   * Copy content into the container.
   */

  COPY("copy"),

  /**
   * Do not copy content into the container.
   */

  NO_COPY("nocopy"),

  /**
   * Devices on the source filesystem are accessible to the container.
   */

  DEVICES("dev"),

  /**
   * Devices on the source filesystem are not accessible to the container.
   */

  NO_DEVICES("nodev"),

  /**
   * Executables on the source filesystem can be executed by the container.
   */

  EXECUTABLE("exec"),

  /**
   * Executables on the source filesystem cannot be executed by the container.
   */

  NO_EXECUTABLE("noexec"),

  /**
   * Setuid executables on the source filesystem remain setuid in the container.
   */

  SETUID("suid"),

  /**
   * Setuid executables on the source filesystem do not remain setuid in the container.
   */

  NO_SETUID("nosuid"),

  /**
   * Map container UID/GID to the correct UID/GID on the host.
   */

  MAP_HOST_UID_GID("U");

  /**
   * @return The option as it would appear on the command-line
   */

  public String tag()
  {
    return this.tag;
  }

  private final String tag;

  PodmanVolumeFlag(
    final String inTag)
  {
    this.tag = Objects.requireNonNull(inTag, "tag");
  }
}
