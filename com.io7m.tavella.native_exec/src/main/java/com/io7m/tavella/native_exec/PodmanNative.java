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


package com.io7m.tavella.native_exec;

import com.io7m.tavella.api.PodmanBackend;
import com.io7m.tavella.api.PodmanExecutableConfiguration;
import com.io7m.tavella.api.PodmanExecutableFactoryType;
import com.io7m.tavella.api.PodmanExecutableType;
import com.io7m.tavella.api.PodmanProcessInfoBuilderType;
import com.io7m.tavella.api.PodmanProcessRunBuilderType;
import com.io7m.tavella.native_exec.internal.PNInfo;
import com.io7m.tavella.native_exec.internal.PNRun;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Objects;
import java.util.Optional;
import java.util.TreeMap;
import java.util.concurrent.TimeUnit;

import static java.lang.ProcessBuilder.Redirect.PIPE;

/**
 * The native podman executable.
 */

public final class PodmanNative implements PodmanExecutableFactoryType
{
  private static final Logger LOG =
    LoggerFactory.getLogger(PodmanNative.class);

  /**
   * The native podman executable.
   */

  public PodmanNative()
  {

  }

  @Override
  public Optional<PodmanBackend> isSupported(
    final PodmanExecutableConfiguration configuration)
    throws InterruptedException
  {
    Objects.requireNonNull(configuration, "configuration");

    try {
      final var process =
        new ProcessBuilder()
          .command(configuration.podmanExecutable(), "version")
          .redirectOutput(PIPE)
          .redirectError(PIPE)
          .start();

      try (var output = bufferedReader(process.getInputStream())) {
        try (var errors = bufferedReader(process.getErrorStream())) {
          final var outputLines =
            output.lines().toList();
          final var errorLines =
            errors.lines().toList();

          process.waitFor(5L, TimeUnit.SECONDS);
          final var code = process.exitValue();
          if (code != 0) {
            errorLines.forEach(s -> LOG.error("{}", s));
          }

          final var attributes = new TreeMap<String, String>();
          for (final var line : outputLines) {
            final var segments = line.split(":", 2);
            if (segments.length == 2) {
              attributes.put(segments[0], segments[1].trim());
            }
          }
          return Optional.of(new PodmanBackend(attributes));
        }
      }
    } catch (final IOException e) {
      LOG.debug("Failed to run {}: ", configuration.podmanExecutable(), e);
      return Optional.empty();
    }
  }

  private static BufferedReader bufferedReader(
    final InputStream stream)
  {
    return new BufferedReader(new InputStreamReader(stream));
  }

  @Override
  public PodmanExecutableType createExecutable(
    final PodmanExecutableConfiguration configuration)
  {
    return new Executable(configuration);
  }

  private static final class Executable implements PodmanExecutableType
  {
    private final PodmanExecutableConfiguration configuration;

    Executable(
      final PodmanExecutableConfiguration inConfiguration)
    {
      this.configuration =
        Objects.requireNonNull(inConfiguration, "configuration");
    }

    @Override
    public PodmanProcessInfoBuilderType info()
    {
      return new PNInfo(this.configuration);
    }

    @Override
    public PodmanProcessRunBuilderType run()
    {
      return new PNRun(this.configuration);
    }
  }
}
