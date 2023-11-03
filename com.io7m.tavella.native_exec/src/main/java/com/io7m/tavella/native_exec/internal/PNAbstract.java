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
import com.io7m.tavella.api.PodmanProcessBuilderType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

abstract class PNAbstract implements PodmanProcessBuilderType
{
  private static final Logger LOG =
    LoggerFactory.getLogger(PNAbstract.class);

  private final PodmanExecutableConfiguration configuration;

  PNAbstract(
    final PodmanExecutableConfiguration inConfiguration)
  {
    this.configuration =
      Objects.requireNonNull(inConfiguration, "configuration");
  }

  protected final ProcessBuilder createNewProcessBuilder(
    final List<String> arguments)
  {
    final var command = new ArrayList<String>();
    command.add(this.configuration.podmanExecutable());
    command.addAll(arguments);

    LOG.debug("Execute: {}", command);
    return new ProcessBuilder(command);
  }
}
