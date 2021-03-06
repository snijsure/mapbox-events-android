package com.mapbox.android.telemetry;

/**
 * Simplified version of Google HTTP Client Library for Java's ExponentialBackOff.
 * https://developers.google.com/api-client-library/java/google-http-java-client/backoff
 *
 * In this version, the interval creation is deterministic and has no concept of
 * maxElapsedTimeMillis, it's up to the application to stop. It also adjusts its default values.
 */
class ExponentialBackoff {

  /** The default initial interval value in milliseconds (10 seconds). */
  private static final int DEFAULT_INITIAL_INTERVAL_MILLIS = 10_000;

  /** The default multiplier value (2.0 which is 100% increase per back off). */
  private static final double DEFAULT_MULTIPLIER = 2.0;

  /** The default maximum back off time in milliseconds (10 minutes). */
  private static final int DEFAULT_MAX_INTERVAL_MILLIS = 600_000;

  /** The default maximum back off time in milliseconds (10 minutes). */
  private static final int NANO_TO_MILLIS_CONVERSION_NUMBER = 1000000;

  /** The current retry interval in milliseconds. */
  private int currentIntervalMillis;

  /** The initial retry interval in milliseconds. */
  private int initialIntervalMillis;

  /** The value to multiply the current interval with for each retry attempt. */
  private double multiplier;

  /**
   * The maximum value of the back off period in milliseconds. Once the retry interval reaches this
   * value it stops increasing.
   */
  private int maxIntervalMillis;

  /**
   * The system time in nanoseconds. It is calculated when an ExponentialBackOffPolicy instance is
   * created and is reset when {@link #reset()} is called.
   */
  private long startTimeNanos;

  ExponentialBackoff() {
    initialIntervalMillis = DEFAULT_INITIAL_INTERVAL_MILLIS;
    multiplier = DEFAULT_MULTIPLIER;
    maxIntervalMillis = DEFAULT_MAX_INTERVAL_MILLIS;
    reset();
  }

  /** Sets the interval back to the initial retry interval and restarts the timer. */
  final void reset() {
    currentIntervalMillis = initialIntervalMillis;
    startTimeNanos = System.nanoTime();
  }

  /**
   * Get the next back off interval
   */
  long nextBackOffMillis() {
    int value = currentIntervalMillis;
    incrementCurrentInterval();
    return value;
  }

  /** Returns the initial retry interval in milliseconds. */
  final int getInitialIntervalMillis() {
    return initialIntervalMillis;
  }

  /**
   * Returns the current retry interval in milliseconds.
   */
  final int getCurrentIntervalMillis() {
    return currentIntervalMillis;
  }

  /**
   * Returns the value to multiply the current interval with for each retry attempt.
   */
  final double getMultiplier() {
    return multiplier;
  }

  /**
   * Returns the maximum value of the back off period in milliseconds. Once the current interval
   * reaches this value it stops increasing.
   */
  final int getMaxIntervalMillis() {
    return maxIntervalMillis;
  }

  /**
   * Returns the elapsed time in milliseconds since an {@link ExponentialBackoff} instance is
   * created and is reset when {@link #reset()} is called.
   *
   * <p>
   * The elapsed time is computed using {@link System#nanoTime()}.
   * </p>
   */
  final long getElapsedTimeMillis() {
    return (System.nanoTime() - startTimeNanos) / NANO_TO_MILLIS_CONVERSION_NUMBER;
  }

  /**
   * Increments the current interval by multiplying it with the multiplier.
   */
  private void incrementCurrentInterval() {
    // Check for overflow, if overflow is detected set the current interval to the max interval.
    if (currentIntervalMillis >= maxIntervalMillis / multiplier) {
      currentIntervalMillis = maxIntervalMillis;
    } else {
      currentIntervalMillis *= multiplier;
    }
  }

}