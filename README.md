# Selenium Java Automation Framework

Maven + Selenium + TestNG framework targeting the OrangeHRM demo site
(`https://opensource-demo.orangehrmlive.com`), built for parallel-safe
execution with ExtentReports HTML reporting and automatic retry of flaky
tests.

## Prerequisites

- JDK 17+
- Maven 3.8+
- Chrome, Firefox, or Edge installed locally (driver binaries are fetched
  automatically at runtime by WebDriverManager - no manual driver setup)

## Project structure

```
src/main/java/com/qa/framework/
  base/BaseTest.java              - @BeforeMethod/@AfterMethod driver lifecycle
  managers/DriverManager.java     - ThreadLocal<WebDriver> holder
  utils/
    ConfigReader.java             - Singleton config loader (env-aware)
    DriverFactory.java            - Chrome/Firefox/Edge instantiation
    WaitUtils.java                - explicit wait helpers
    ScreenshotUtils.java          - timestamped screenshot capture
    ExtentReportManager.java      - Singleton report + ThreadLocal<ExtentTest>
    RetryAnalyzer.java            - IRetryAnalyzer, retries failed tests
    RetryListener.java            - auto-applies RetryAnalyzer to all @Test
  listeners/TestListener.java     - ITestListener -> ExtentReports bridge
  pages/                          - BasePage, LoginPage, DashboardPage

src/test/java/com/qa/tests/       - test classes (LoginTest)
src/test/resources/               - config.properties, config-qa.properties,
                                     log4j2.xml, .env.example
testng.xml                        - suite definition (parallel="methods")
```

## Configuration

Config is layered: `config.properties` (defaults) is loaded first, then
`config-<env>.properties` is loaded on top of it, where `<env>` comes from
the `-Denv` system property (defaults to `qa`). Any property can also be
overridden directly with a matching `-D` system property, e.g.:

```
mvn clean test -Denv=qa -Dbrowser=firefox -Dheadless=false
```

See `src/test/resources/.env.example` for the full list of externally
configurable values (base URL, browser, headless flag, timeouts,
credentials).

## Running the tests

```
mvn clean test
```

This runs `testng.xml`, which executes `LoginTest` with
`parallel="methods"` and `thread-count="2"` - both test methods run
concurrently on separate threads, each with its own WebDriver instance and
its own ExtentReports log node.

Run against a specific browser or in headed mode:

```
mvn clean test -Dbrowser=chrome -Dheadless=false
```

## Reports and artifacts

After a run, check:

- `extent-reports/TestReport_<timestamp>.html` - full HTML report with
  pass/fail status, logs, and embedded failure screenshots
- `screenshots/` - raw PNG screenshots captured on failure
- `logs/framework.log` - framework/infrastructure log (driver setup,
  config loading, retries)
- `target/surefire-reports/` - default TestNG/Surefire output (XML/HTML)

## CI

`.github/workflows/ci.yml` runs the suite on every push/PR to `main`:
checks out the repo, sets up JDK 17 with Maven dependency caching, runs
`mvn clean test`, and uploads the Extent HTML report, screenshots, and
framework log as workflow artifacts - even when the run fails.

## Framework design decisions

**ThreadLocal for the WebDriver.** `DriverManager` stores the active
`WebDriver` in a `ThreadLocal<WebDriver>` rather than a plain static field.
`testng.xml` runs with `parallel="methods"` across multiple threads; a
shared static driver would let two tests silently drive the same browser
window and corrupt each other's state. ThreadLocal gives every worker
thread - and therefore every concurrently running test - its own isolated
driver instance, with `unload()` clearing the reference after each test so
threads reused by TestNG's pool never see a stale driver.

**ConfigReader and ExtentReportManager as Singletons.** Config should be
parsed from disk once per run, not once per test - `ConfigReader` uses a
thread-safe double-checked-locking singleton so all threads read from the
same cached `Properties` instance instead of re-reading files under
concurrent load. `ExtentReportManager` is a singleton for a stronger
reason: ExtentReports is meant to produce *one* HTML file per run, so the
underlying `ExtentReports` object must be shared across every thread.
What must *not* be shared per-thread is the "current test" pointer used to
log to that report - that part is a separate `ThreadLocal<ExtentTest>`
layered on top of the singleton, so two tests running in parallel each log
to their own node in the same report instead of interleaving lines into
each other's results.

**Log4j2 scoped to framework-level logging, not per-step.** `log4j2.xml`
only logs `com.qa.framework` at INFO (driver setup/teardown, config
loading, retry attempts, suite start/finish) to the console and a rolling
file. It deliberately does not log individual test steps (clicks, waits,
assertions) - that level of detail already lives in ExtentReports, which
is the artifact designed for human-readable, per-test, per-step review
with embedded screenshots. Duplicating that detail into a text log would
just be noise to sift through when debugging a CI failure; the log file
is for "why did the framework itself fail to start/behave", the HTML
report is for "why did this test fail".

**Retry strategy.** The target application is a public, shared demo
instance (`opensource-demo.orangehrmlive.com`) with no uptime or
performance guarantees, so occasional slow responses or timing-related
flakiness are expected and are not necessarily real defects. `RetryAnalyzer`
retries a failed test up to 2 additional times before it's recorded as a
true failure, and `RetryListener` (an `IAnnotationTransformer`) applies it
to every `@Test` automatically via `testng.xml`'s `<listeners>` block, so
no one has to remember to add `retryAnalyzer = RetryAnalyzer.class` to each
new test method by hand.
