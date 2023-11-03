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

/**
 * @see "podman run"
 */

public interface PodmanProcessRunBuilderType
  extends PodmanProcessBuilderType
{
  /**
   * @param interactive {@code true if interactive}
   *
   * @return this
   *
   * @see "--interactive"
   */

  PodmanProcessRunBuilderType setInteractive(
    boolean interactive);

  /**
   * @param tty {@code true if tty}
   *
   * @return this
   *
   * @see "--tty"
   */

  PodmanProcessRunBuilderType setTTY(
    boolean tty);

  /**
   * @param remove {@code true if remove-after-exit}
   *
   * @return this
   *
   * @see "--rm"
   */

  PodmanProcessRunBuilderType setRemoveAfterExit(
    boolean remove);

  /**
   * @param ro {@code true} if the root partition is read-only
   *
   * @return this
   *
   * @see "--read-only"
   */

  PodmanProcessRunBuilderType setRootReadOnly(
    boolean ro);

  /**
   * @param name  The name
   * @param value The value
   *
   * @return this
   *
   * @see "--env"
   */

  PodmanProcessRunBuilderType addEnvironmentVariable(
    String name,
    String value);

  /**
   * @param name The container name
   *
   * @return this
   *
   * @see "--name"
   */

  PodmanProcessRunBuilderType setContainerName(
    String name);

  /**
   * @param image The container image
   *
   * @return this
   */

  PodmanProcessRunBuilderType setImage(
    PodmanImage image);

  /**
   * Add a container argument.
   *
   * @param argument The container argument
   *
   * @return this
   */

  PodmanProcessRunBuilderType addArgument(
    String argument);

  /**
   * Add a volume mount
   *
   * @param volumeMount The volume mount
   *
   * @return this
   */

  PodmanProcessRunBuilderType addVolume(
    PodmanVolumeMount volumeMount);

  /**
   * Add a volume mount
   *
   * @param mount The volume mount
   *
   * @return this
   */

  PodmanProcessRunBuilderType addTmpFS(
    PodmanTmpFSMount mount);
}
