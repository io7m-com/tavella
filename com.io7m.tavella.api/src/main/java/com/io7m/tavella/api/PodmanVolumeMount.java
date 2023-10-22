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
import java.util.Set;

/**
 * Mount a host file, directory, or volume at the given location in the container.
 *
 * @param source        The mount source
 * @param containerPath The path inside the container
 * @param options       The mount options
 */

public record PodmanVolumeMount(
  PodmanVolumeMountSourceType source,
  String containerPath,
  Set<PodmanVolumeFlag> options)
{
  /**
   * Mount a host file, directory, or volume at the given location in the container.
   *
   * @param source        The mount source
   * @param containerPath The path inside the container
   * @param options       The mount options
   */

  public PodmanVolumeMount
  {
    Objects.requireNonNull(source, "source");
    Objects.requireNonNull(containerPath, "containerPath");
    options = Set.copyOf(options);
  }
}
