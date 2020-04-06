[![Clojars Project](https://img.shields.io/clojars/v/nl.mediquest/logback.masking-pattern-layouts.svg)](https://clojars.org/nl.mediquest/logback.masking-pattern-layouts)

# logback masking pattern layouts

Logback appenders for scrubbing sensitive data from logs

## Usage

Require `[nl.mediquest/logback.masking-pattern-layouts "1.0.3"]`.

Include the pattern layouts in your appender. E.g.,:

```
<appender name="GCLOUD" class="ch.qos.logback.core.ConsoleAppender">
  <encoder class="ch.qos.logback.core.encoder.LayoutWrappingEncoder">
    <layout class="nl.mediquest.logback.StackdriverMaskingPatternLayout">
      <pattern>%msg</pattern>
    </layout>
  </encoder>
</appender>
```

```
<appender name="SENTRY" class="io.sentry.logback.SentryAppender">
  <encoder class="ch.qos.logback.core.encoder.LayoutWrappingEncoder">
    <layout class="nl.mediquest.logback.MaskingPatternLayout">
      <pattern>%msg</pattern>
    </layout>
  </encoder>
  <filter class="ch.qos.logback.classic.filter.ThresholdFilter">
    <level>WARN</level>
  </filter>
</appender>
```

## Examples

### Stackdriver logging

```sh
{"message":":nl.mediquest.upload-backend.main/init {:password *****}","severity":"INFO","thread":"main","logger":"nl.mediquest.upload-backend.main"}
```

## Tests

Run unit tests via:

```sh
lein test :unit
```
