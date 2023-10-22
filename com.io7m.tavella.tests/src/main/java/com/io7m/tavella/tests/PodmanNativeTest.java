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


package com.io7m.tavella.tests;

import com.io7m.tavella.api.PodmanExecutableConfiguration;
import com.io7m.tavella.api.PodmanImage;
import com.io7m.tavella.api.PodmanVolumeFlag;
import com.io7m.tavella.api.PodmanVolumeMount;
import com.io7m.tavella.api.PodmanVolumeMountSourceType;
import com.io7m.tavella.native_exec.PodmanNative;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Timeout;
import org.junit.jupiter.api.io.TempDir;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.nio.file.Path;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

import static com.io7m.tavella.api.PodmanVolumeFlag.READ_ONLY;
import static com.io7m.tavella.api.PodmanVolumeFlag.SELINUX_LABEL_PRIVATE;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assumptions.assumeTrue;

public final class PodmanNativeTest
{
  private static final Logger LOG =
    LoggerFactory.getLogger(PodmanNativeTest.class);

  private PodmanNative executables;
  private PodmanExecutableConfiguration configuration;

  @BeforeEach
  public void setup()
  {
    this.executables =
      new PodmanNative();
    this.configuration =
      PodmanExecutableConfiguration.builder()
        .build();
  }

  @Test
  @Timeout(value = 10L, unit = TimeUnit.SECONDS)
  public void testIsSupported()
    throws Exception
  {
    final var support =
      this.executables.isSupported(this.configuration);

    support.ifPresent(backend -> {
      backend.attributes()
        .forEach((key, value) -> System.out.printf("%-24s %s%n", key, value));
    });
  }

  @Test
  @Timeout(value = 10L, unit = TimeUnit.SECONDS)
  public void testInfo()
    throws Exception
  {
    assumeTrue(this.isSupported());

    final var exec =
      this.executables.createExecutable(this.configuration);

    final var proc =
      exec.info()
        .execute();

    final var input = proc.inputReader();
    LOG.debug("{}", input.lines().collect(Collectors.joining("\n")));
    proc.waitFor(5L, TimeUnit.SECONDS);
    assertEquals(0, proc.exitValue());
  }

  @Test
  @Timeout(value = 10L, unit = TimeUnit.SECONDS)
  public void testRun(
    final @TempDir Path directory)
    throws Exception
  {
    assumeTrue(this.isSupported());

    final var exec =
      this.executables.createExecutable(this.configuration);

    final var proc =
      exec.run()
        .setImage(new PodmanImage(
          "quay.io",
          "prometheus/busybox",
          "latest",
          Optional.of(
            "sha256:b2273588d9afbfb580e4421c6b41ded2a3e25b889ed002e7bab64e9119835c45")
        ))
        .setTTY(true)
        .setInteractive(true)
        .addVolume(new PodmanVolumeMount(
          new PodmanVolumeMountSourceType.HostPath(directory),
          "/z",
          Set.of(READ_ONLY, SELINUX_LABEL_PRIVATE)
        ))
        .addArgument("uname")
        .addArgument("-a")
        .execute();

    final var input = proc.inputReader();
    LOG.debug("{}", input.lines().collect(Collectors.joining("\n")));
    proc.waitFor(5L, TimeUnit.SECONDS);
    assertEquals(0, proc.exitValue());
  }

  private boolean isSupported()
    throws InterruptedException
  {
    return this.executables.isSupported(this.configuration)
      .isPresent();
  }
}
