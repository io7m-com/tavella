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


package com.io7m.tavella.native_exec.internal;

import com.io7m.tavella.api.PodmanExecutableConfiguration;
import com.io7m.tavella.api.PodmanImage;
import com.io7m.tavella.api.PodmanProcessRunBuilderType;
import com.io7m.tavella.api.PodmanVolumeFlag;
import com.io7m.tavella.api.PodmanVolumeMount;
import com.io7m.tavella.api.PodmanVolumeMountSourceType;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.TreeMap;
import java.util.stream.Collectors;

/**
 * @see "podman run"
 */

public final class PNRun
  extends PNAbstract
  implements PodmanProcessRunBuilderType
{
  private final TreeMap<String, String> environment;
  private final ArrayList<String> containerArguments;
  private final ArrayList<PodmanVolumeMount> volumes;
  private Optional<String> containerName;
  private boolean interactive;
  private boolean tty;
  private Optional<PodmanImage> image;
  private boolean remove;

  /**
   * @param inConfiguration The configuration
   *
   * @see "podman run"
   */

  public PNRun(
    final PodmanExecutableConfiguration inConfiguration)
  {
    super(inConfiguration);

    this.volumes =
      new ArrayList<PodmanVolumeMount>();
    this.containerArguments =
      new ArrayList<String>();
    this.environment =
      new TreeMap<>();
    this.containerName =
      Optional.empty();
    this.image =
      Optional.empty();
  }

  @Override
  public ProcessBuilder build()
  {
    if (this.image.isEmpty()) {
      throw new IllegalArgumentException("No container image was specified.");
    }

    final var arguments = new ArrayList<String>();
    arguments.add("run");

    if (this.interactive) {
      arguments.add("--interactive");
    }
    if (this.tty) {
      arguments.add("--tty");
    }
    if (this.remove) {
      arguments.add("--rm");
    }

    for (final var entry : this.environment.entrySet()) {
      arguments.add("--env");
      arguments.add("%s=%s".formatted(entry.getKey(), entry.getValue()));
    }

    for (final var mount : this.volumes) {
      arguments.add("--volume");
      arguments.add(volumeSpec(mount));
    }

    if (this.containerName.isPresent()) {
      arguments.add("--name");
      arguments.add(this.containerName.get());
    }

    arguments.add(this.image.get().fullImageName());
    arguments.addAll(this.containerArguments);
    return this.createNewProcessBuilder(List.copyOf(arguments));
  }

  private static String volumeSpec(
    final PodmanVolumeMount mount)
  {
    final var spec = new StringBuilder();
    spec.append(
      switch (mount.source()) {
        case final PodmanVolumeMountSourceType.HostPath hostPath ->
          hostPath.path().toAbsolutePath().toString();
        case final PodmanVolumeMountSourceType.NamedVolume namedVolume ->
          namedVolume.name();
      }
    );
    spec.append(':');
    spec.append(mount.containerPath());

    if (!mount.options().isEmpty()) {
      spec.append(':');
      spec.append(
        mount.options()
          .stream()
          .map(PodmanVolumeFlag::tag)
          .collect(Collectors.joining(","))
      );
    }

    return spec.toString();
  }

  @Override
  public PodmanProcessRunBuilderType setInteractive(
    final boolean i)
  {
    this.interactive = i;
    return this;
  }

  @Override
  public PodmanProcessRunBuilderType setTTY(
    final boolean t)
  {
    this.tty = t;
    return this;
  }

  @Override
  public PodmanProcessRunBuilderType setRemoveAfterExit(
    final boolean r)
  {
    this.remove = r;
    return this;
  }

  @Override
  public PodmanProcessRunBuilderType addEnvironmentVariable(
    final String name,
    final String value)
  {
    this.environment.put(name, value);
    return this;
  }

  @Override
  public PodmanProcessRunBuilderType setContainerName(
    final String name)
  {
    this.containerName = Optional.of(name);
    return this;
  }

  @Override
  public PodmanProcessRunBuilderType setImage(
    final PodmanImage i)
  {
    this.image = Optional.of(i);
    return this;
  }

  @Override
  public PodmanProcessRunBuilderType addArgument(
    final String argument)
  {
    this.containerArguments.add(
      Objects.requireNonNull(argument, "argument")
    );
    return this;
  }

  @Override
  public PodmanProcessRunBuilderType addVolume(
    final PodmanVolumeMount volumeMount)
  {
    this.volumes.add(
      Objects.requireNonNull(volumeMount, "volumeMount")
    );
    return this;
  }
}
