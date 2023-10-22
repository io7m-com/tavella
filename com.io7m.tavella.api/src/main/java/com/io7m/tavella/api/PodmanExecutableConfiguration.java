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
 * The podman executable configuration.
 */

public final class PodmanExecutableConfiguration
{
  private final String executable;

  private PodmanExecutableConfiguration(
    final String inExecutable)
  {
    this.executable =
      Objects.requireNonNull(inExecutable, "executable");
  }

  /**
   * @return A new configuration builder
   */

  public static Builder builder()
  {
    return new Builder();
  }

  /**
   * @return The podman executable
   */

  public String podmanExecutable()
  {
    return this.executable;
  }

  /**
   * A mutable builder for configurations.
   */

  public static final class Builder
  {
    private String executable;

    private Builder()
    {
      this.executable = "podman";
    }

    /**
     * Set the podman executable.
     *
     * @param name The name
     *
     * @return this
     */

    public Builder setPodmanExecutable(
      final String name)
    {
      this.executable = Objects.requireNonNull(name, "name");
      return this;
    }

    /**
     * @return An immutable configuration
     */

    public PodmanExecutableConfiguration build()
    {
      return new PodmanExecutableConfiguration(this.executable);
    }
  }
}
