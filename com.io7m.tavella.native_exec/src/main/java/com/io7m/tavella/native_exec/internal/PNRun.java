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
import com.io7m.tavella.api.PodmanTmpFSFlag;
import com.io7m.tavella.api.PodmanTmpFSMount;
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
  private final ArrayList<PodmanTmpFSMount> tmpfs;
  private Optional<String> containerName;
  private boolean interactive;
  private boolean tty;
  private Optional<PodmanImage> image;
  private boolean remove;
  private boolean readOnly;
  private Optional<String> podName;

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
      new ArrayList<>();
    this.tmpfs =
      new ArrayList<>();
    this.containerArguments =
      new ArrayList<>();
    this.environment =
      new TreeMap<>();
    this.containerName =
      Optional.empty();
    this.image =
      Optional.empty();
    this.podName =
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

    this.addArgumentOptions(arguments);
    this.addArgumentEnvironment(arguments);
    this.addArgumentMounts(arguments);
    this.addArgumentTmpFS(arguments);

    if (this.containerName.isPresent()) {
      arguments.add("--name");
      arguments.add(this.containerName.get());
    }

    if (this.podName.isPresent()) {
      arguments.add("--pod");
      arguments.add(this.podName.get());
    }

    arguments.add(this.image.get().fullImageName());
    arguments.addAll(this.containerArguments);
    return this.createNewProcessBuilder(List.copyOf(arguments));
  }

  private void addArgumentMounts(
    final ArrayList<String> arguments)
  {
    for (final var mount : this.volumes) {
      arguments.add("--volume");
      arguments.add(volumeSpec(mount));
    }
  }

  private void addArgumentTmpFS(
    final ArrayList<String> arguments)
  {
    for (final var mount : this.tmpfs) {
      arguments.add("--tmpfs");
      arguments.add(tmpfsSpec(mount));
    }
  }

  private static String tmpfsSpec(
    final PodmanTmpFSMount mount)
  {
    final var spec = new StringBuilder();
    spec.append(mount.containerPath());

    if (!mount.options().isEmpty()) {
      spec.append(':');
      spec.append(
        mount.options()
          .stream()
          .map(PodmanTmpFSFlag::tag)
          .collect(Collectors.joining(","))
      );
    }

    return spec.toString();
  }

  private void addArgumentEnvironment(
    final ArrayList<String> arguments)
  {
    for (final var entry : this.environment.entrySet()) {
      arguments.add("--env");
      arguments.add("%s=%s".formatted(entry.getKey(), entry.getValue()));
    }
  }

  private void addArgumentOptions(
    final ArrayList<String> arguments)
  {
    if (this.interactive) {
      arguments.add("--interactive");
    }
    if (this.tty) {
      arguments.add("--tty");
    }
    if (this.remove) {
      arguments.add("--rm");
    }
    if (this.readOnly) {
      arguments.add("--read-only");
    }
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
  public PodmanProcessRunBuilderType setRootReadOnly(
    final boolean ro)
  {
    this.readOnly = ro;
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

  @Override
  public PodmanProcessRunBuilderType addTmpFS(
    final PodmanTmpFSMount mount)
  {
    this.tmpfs.add(
      Objects.requireNonNull(mount, "mount")
    );
    return this;
  }

  @Override
  public PodmanProcessRunBuilderType setPod(
    final String pod)
  {
    this.podName = Optional.of(pod);
    return this;
  }
}
