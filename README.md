[![Clojars Project](https://img.shields.io/clojars/v/nl.mediquest/logback.masking-pattern-layouts.svg)](https://clojars.org/nl.mediquest/logback.masking-pattern-layouts)

# Logback masking pattern layouts

Logback encoder layouts for scrubbing sensitive data from logs.

## Usage

#### Leiningen/Boot

`[nl.mediquest/logback.masking-pattern-layouts "1.0.6"]`

#### Clojure CLI/deps.edn

`nl.mediquest/logback.masking-pattern-layouts {:mvn/version "1.0.6"}`

#### Gradle

`compile 'nl.mediquest:logback.masking-pattern-layouts:1.0.6'`

#### Maven

```sh
<dependency>
  <groupId>nl.mediquest</groupId>
  <artifactId>logback.masking-pattern-layouts</artifactId>
  <version>1.0.6</version>
</dependency>
```

Include the `LayoutWrappingEncoder` with the `MaskingPatternLayout` in your
appender and provide regexes and their replacements. E.g.,:

```xml
<appender name="SENTRY" class="io.sentry.logback.SentryAppender">
  <encoder class="ch.qos.logback.core.encoder.LayoutWrappingEncoder">
    <layout class="nl.mediquest.logback.MaskingPatternLayout">
      <pattern>%.-250msg</pattern>
      <regex>some-xml-encoded-regex</regex>
      <replacement>a-replacement</replacement
    </layout>
  </encoder>
  <filter class="ch.qos.logback.classic.filter.ThresholdFilter">
    <level>WARN</level>
  </filter>
</appender>
```

The regexes and their replacements will be applied in order to the message
returned after applying the pattern.

`pattern` is a [standard PatternLayout conversion
pattern](http://logback.qos.ch/manual/layouts.html#ClassicPatternLayout) that
closely follows the `printf()` funtion in the C programming language.

A replacement can contain capture groups via the `%1` and `%2` variables. See
[examples](#examples) below. For all replacement options see
[`clojure.string/replace`](https://clojuredocs.org/clojure.string/replace).

For Stackdriver use the `StackdriverMaskingPatternLayout` in the 
`ConsoleAppender` for logging to stdout in the Stackdriver format:

```xml
<appender name="GCLOUD" class="ch.qos.logback.core.ConsoleAppender">
  <encoder class="ch.qos.logback.core.encoder.LayoutWrappingEncoder">
    <layout class="nl.mediquest.logback.StackdriverMaskingPatternLayout">
      <pattern>%msg</pattern>
      <regex>some-xml-encoded-regex</regex>
      <replacement>a-replacement</replacement
      <regex>another-xml-encoded-regex</regex>
      <replacement>another-replacement</replacement
    </layout>
  </encoder>
</appender>
```

To use default Mediquest patterns and replacements (for IBAN, BSN, AGB, names
and passwords) include `useDefaultMediquestReplacements` with a true value:

```xml
<encoder class="ch.qos.logback.core.encoder.LayoutWrappingEncoder">
  <layout class="nl.mediquest.logback.StackdriverMaskingPatternLayout">
    <pattern>%msg</pattern>
    <useDefaultMediquestReplacements>true</useDefaultMediquestReplacements>
  </layout>
</encoder>
```

Custom regexes and replacements take precedence over the added default ones.

## Examples

### Masking a password

Below masks strings that contain password=xxx or password="xxx" as
password=***** and password="*****" respectively.

```xml
<encoder class="ch.qos.logback.core.encoder.LayoutWrappingEncoder">
  <layout class="nl.mediquest.logback.StackdriverMaskingPatternLayout">
    <pattern>%msg</pattern>
    <regex>(?i)(password(?:=\s*|\:\s*|\s*|=\s*))\w+</regex>
    <replacement>$1*****</replacement>
    <regex>(?i)(password(?:=\s*|\:\s*|\s*|=\s*))\&quot;.*\&quot;</regex>
    <replacement>$1*****</replacement>
  </layout>
</encoder>
```

Note that regex strings are XML-escaped and that the replacement contains the
first captured group `$1`.

### Stackdriver logging output

```sh
{
    "message":":nl.mediquest.upload-backend.main/init {:password *****}",
    "severity":"INFO",
    "thread":"main",
    "logger":"nl.mediquest.upload-backend.main"
}
```

## Tests

Run unit tests via:

```sh
lein test :unit
```
