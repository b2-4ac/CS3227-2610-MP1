# Study Tracker Developer Guide

## Acknowledgements

| Dependency | Purpose |
| --- | --- |
| Java 25 | Runtime and language. |
| Gradle | Builds, dependencies, and tests. |
| OpenJFX and its Gradle plugin | JavaFX GUI, FXML, charts, and native dependencies. |
| Jackson Databind and JSR-310 | JSON persistence and Java-time support. |
| JUnit Jupiter | Automated tests. |
| Gradle Shadow Plugin | Distribution JAR packaging. |

Consult the OpenJFX, Java `java.time`, Jackson, and JUnit 5 documentation for the dependencies above.

## Architecture of the App

The diagram below depicts the general structure and architecture of the application.

![Application Architecture](/docs/images/application_architecture.png)

Study Tracker uses an MVC-inspired design, consisting of four main components:
 - UI accepts user input and displays results
 - Controllers serve as a connector between the user interface and the rest of the application
 - Models represent the structure of the objects that are manipulated within the application
 - Storage ensures data persistence in JSON format.

Another component not depicted in the diagram is the start of the applciation: `StudyTrackerApp`.
`StudyTrackerApp` creates one shared `StudyTimer`, storage objects, and application controllers. It loads `app.fxml`, and `AppController` passes the shared controllers to whichever FXML screen it loads. Timer actions flow from Dashboard to `TimerController`, then `StudyTimer`; stopping saves a `StudySession`. Statistics load sessions and calculate active-interval overlap with the requested dates.

## Interaction Between Components

The *Sequence Diagram* below provides an example as to how different components interact with one another within the system.

![Start Timer Sequence Diagram](/docs/images/starttimer_sequence_diagram.png)

In this scenario, the user has just chosen to start the timer by clicking on the start button.
 - The `DashboardController` accepts the input and attempts to retrieve the selected subject from the dropdown.
 - If successful, it passes that information to the `TimerController` which serves as the connection between the UI and the `StudyTimer` object, starting the study session.
 - `DashboardController` then repeatedly refreshes the current `TimerState` as well as the `elapsedTime` to continuously update the timer on the user interface until it is paused or stopped.

## UI Components

| Resource | Responsibility |
| --- | --- |
| `app.fxml` / `AppController` | Root shell, navigation, view loading, and dependency injection. |
| `dashboard.fxml` / `DashboardController` | Timer lifecycle, live `Timeline`, daily totals, and recent sessions. |
| `subjects.fxml` / `SubjectViewController` | Subject add/delete actions and subject table. |
| `sessions.fxml` / `SessionViewController` | Session history and start-date filtering. |
| `statistics.fxml` / `StatisticsViewController` | Date validation, totals, and subject bar chart. |
| `UiSupport` | Duration formatting and error dialogs. |
| `app.css` | Navigation, card, table, chart, and button styling. |

`AppController` disposes the Dashboard timeline when navigating away, avoiding background refresh work.

## Controller Components

### `TimerController`

Starts, pauses, resumes, and stops `StudyTimer`; saves stopped sessions; exposes state, active duration, and past sessions.

### `SubjectController`

Loads and adds subjects, rejects case-insensitive duplicate names, and deletes subjects by index.

### `StatisticsController`

Calculates total active time and time by subject for inclusive date ranges using active-interval overlap rather than only session start dates.

## Model Components

| Component | Responsibility |
| --- | --- |
| `Subject` | Subject name. |
| `TimerState` | `NOT_STARTED`, `RUNNING`, and `PAUSED`. |
| `StudyInterval` | One validated uninterrupted active period. |
| `StudySession` | Subject plus non-empty immutable interval list; derives start, end, and duration. |
| `StudyTimer` | Current timer and interval creation on pause/stop. |

## Storage Components

`SubjectStorage` reads and writes `subjects.json`. `StudySessionStorage` reads and appends `sessions.json`, configures Jackson Java-time support, and creates parent folders on save. Sessions persist their subject and active intervals. Model field, constructor, or JSON annotation changes should preserve this schema or provide a migration.

## Testing

Run the JUnit suite with `./gradlew test`, or `.\gradlew.bat test` on Windows. Tests are in `src/test/java/`; JSON fixtures are in `src/test/resources/studytracker/fixtures/`.

| Area | Example coverage |
| --- | --- |
| Model | Subject changes, interval validation, derived session values, timer state transitions, and reset. |
| Storage | Missing files, directory creation, JSON round trips, append/replace behavior, and interval comparisons. |
| Subject controller | Addition, duplicate rejection, deletion, and invalid indices. |
| Statistics controller | Single-day totals, inclusive bounds, empty periods, cross-midnight pauses, partial overlap, and multi-year ranges. |

### Cross-Midnight Test Example

```text
Active interval: 23 Aug 2026, 23:00-24 Aug 2026, 00:30
Requested date: 24 Aug 2026
Expected active time: 30 minutes
```

This verifies that statistics clip an interval to the requested period instead of excluding it because the session began on the previous date.

## Appendix: Product Context and User Stories

### Target User Audience

Study Tracker is intended for students of all ages who struggle to manage their study time. It is particularly useful for students who want a simple local tool to record focused work, distinguish study time from breaks, and understand how their effort is distributed across subjects.

### Value Proposition

Study Tracker gives students a lightweight way to turn study intentions into visible, trustworthy records. It tracks active study time rather than merely the time a timer was open, keeps data locally, and presents clear subject and date-range summaries. Its interval-based design ensures that breaks and study occurring after midnight are represented accurately in statistics.

### User Stories

| Priority | I am a... | I want to... | So that I... |
| --- | --- | --- | --- |
| High | student | add the subjects that I am studying | can assign each study session to the correct subject. |
| High | student | start a timer for a selected subject | can record focused study time as I work. |
| High | student | pause and resume my timer | can take breaks without counting them as study time. |
| High | student | stop a session and save it | can keep a permanent record of completed study. |
| High | student | view total study time for a selected date range | can assess whether I am studying consistently. |
| High | student | view study time grouped by subject | can identify which subjects receive more or less attention. |
| Medium | student | review my completed sessions | can recall when and what I studied. |
| Medium | student | filter completed sessions by date | can focus on study records relevant to a particular period. |
| Medium | student | have study after midnight attributed to the correct day | can rely on daily statistics even when I study late. |
| Medium | student | delete a subject I no longer study | can keep future timer choices relevant and uncluttered. |

### Use Cases

#### Use Case: UC01 - Adding a Subject
**Preconditions:** The application is running
**Guarantees:** A new subject is saved and available for future study sessions

**MSS:**
1. User provides subject name.
2. StudyTracker checks if the name is valid.
3. StudyTracker saves the new subject and updates subject list.

    Use Case Ends.

**Extensions:**

* 2a. Subject Name is empty.
    * 2a1. Studytracker reports that a subject name is required.
    * 2a2. User re-enters subject name.

      Use case resumes from step 2.

* 2b. Subject with the same name already exists.
    * 2b1. StudyTracker reports that the subject already exists.

      Use case ends.

* 3a. Subject data cannot be saved.
    * 3a1. StudyTracker reports that the subject could not be saved.

      Use case ends.

#### Use Case: UC02 - Deleting a Subject
**Preconditions:** The application is running and at least one subject exists.
**Guarantees:** The selected subject is removed from future study session choices. Existing completed sessions will remain unchanged.

**MSS:**
1. User selects the subject to remove.
2. StudyTracker removes the subject from saved subjects.
3. StudyTracker displays the updated subject list.

    Use Case Ends.

**Extensions:**
* 3a. Subject data cannot be saved.
    * 3a1. StudyTracker reports that the subject could not be deleted.

      Use case ends.

#### Use Case: UC03 - Starting the Timer
**Preconditions:** The application is running, the timer is not running, and at least one subject exists.
**Guarantees:** The timer runs for the selected subject and displays active elapsed time.

**MSS:**
1. User selects a subject.
2. User starts the timer.
3. StudyTracker verifies a subject is selected.
3. StudyTracker records the start time of the active study interval.
4. StudyTracker sets the timer state to running.
5. StudyTracker displays the running timer and elapsed active time.

    Use Case Ends.

**Extensions:**

* 3a. No Subject is selected.
    * 3a1. StudyTracker requests that the user select a subject.
    * 3a2. User re-selectes subject.

      Use case resumes from step 2.
      

#### Use Case: UC04 - Pausing the Timer
**Preconditions:** The timer is running
**Guarantees:** The current active interval is recorded and the timer enters the paused state.

**MSS:**
1. User pauses the timer.
2. StudyTracker records the end time of the current active interval and stores it.
3. StudyTracker sets the timer state to paused.

    Use Case Ends.

#### Use Case: UC05 - Resuming the Timer
**Preconditions:** The timer is paused.
**Guarantees:** The timer runs again and a new active study interval begins

**MSS:**
1. User resumes the timer.
2. StudyTracker records the start time of a new active interval.
3. StudyTracker sets the timer state to running.
4. StudyTracker displays the running timer and elapsed active time.

    Use Case Ends.

#### Use Case: UC06 - Stopping the Timer
**Preconditions:** The timer is running or paused.
**Guarantees:** A completed session containng all active intervals is saved, and the timer is reset.

**MSS:**
1. User stops the timer.
2. If the timer is running, StudyTracker closes the current active interval.
3. StudyTracker creates a completed session using the selected subject and recorded active intervals.
4. StudyTracker saves the completed session.
5. StudyTracker resets the timer.
6. StudyTracker updates displayed session information.

    Use Case Ends.

**Extensions:**

* 4a. Session data could not be saved.
    * 4a1. StudyTracker reports that the session could not be saved.

      Use case ends.

#### Use Case: UC07 - Viewing Completed Sessions
**Preconditions:** The application is running.
**Guarantees:** Completed sessions matching the current filter are displayed with their subject, start time, end time, and active duration.

**MSS:**
1. User decides to review completed study sessions.
2. StudyTracker loads saved sessions.
3. StudyTracker applies any active date filter.
4. StudyTracker displays the matching sessions.

    Use Case Ends.

**Extensions:**
* 2a. Session data cannot be loaded.
    * 2a1. StudyTracker reports that sessions could not be loaded.

      Use case ends.

#### Use Case: UC08 - Filtering Completed Sessions by Date
**Preconditions:** The application is running.
**Guarantees:** Only sessions whose derived start dates fall within the selected filter range are displayed.

**MSS:**
1. User provides a start date, an end date, or both.
2. StudyTracker verifies that the date range is valid.
3. StudyTracker loads saved sessions.
4. StudyTracker filters sessions by their derived start dates.
5. StudyTracker displays matching sessions.

    Use Case Ends.

**Extensions:**

* 2a. The end date is before the start date
    * 2a1. StudyTracker reports an invalid date range
    * 2a2. User re-enters date range.

      Use case resumes from step 2.

* 3a. Session data cannot be loaded.
    * 3a1. StudyTracker reports that sessions could not be loaded.

      Use case ends.

#### Use Case: UC09 - Viewing Study Statistics
**Preconditions:** The application is running.
**Guarantees:** Total active study time and active study time by subject are dispayed for the selected date range.

**MSS:**
1. User provides a start date and an end date.
2. StudyTracker validates date range.
3. StudyTracker loads completed sessions.
4. StudyTracker calculates the overlap between each active interval and the requested date range.
5. StudyTracker totals overlapping active time and groups it by subject.
6. StudyTracker displays the total active time and the subject breakdown.

    Use Case Ends.

**Extensions:**

* 2a. One or both dates are missing.
    * 2a1. StudyTracker requests a complete date range.
    * 2a2. User re-enters a new date range.

      Use case resumes from step 2.

* 2b. The end date is before the start date.
    * 2b1. StudyTracker reports an invalid date range.
    * 2b2. User re-enters a new date range

      Use case resumes from step 2.

* 3a. Session data could not be loaded.
    * 3a1. StudyTracker reports that statistics could not be loaded.

      Use case Ends.

* 4a. No active intervals overlap the requested range.
    * 4a1. StudyTracker displays zero active study time and no subject activity.

      Use case Ends.
