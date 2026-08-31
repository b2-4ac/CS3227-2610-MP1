# Study Tracker User Guide

Study Tracker is a desktop application for recording active study time by subject,
reviewing completed sessions, and viewing date-range statistics. Paused time is not
counted as active study time.

## Quick Start

1. Download the Study Tracker `.jar` file.
2. Install Java 25 or later if it is not already installed.
3. Start the application in either of these ways:
   - Double-click the `.jar` file, if `.jar` files are associated with Java on your
     computer.
   - Open a terminal in the folder containing the downloaded file and run:

     ```text
     java -jar StudyTracker.jar
     ```

     Replace `StudyTracker.jar` with the actual name of the file you downloaded.

4. Open **Subjects** and add at least one subject.
5. Return to **Dashboard**, select a subject, and press **Start** to begin recording
   study time.

## First-Use Setup

You must add a subject before you can start a study session. Examples include
`Computer Science`, `Mathematics`, and `Physics`.

Subject names must be unique without regard to letter case. For example, if
`Mathematics` already exists, you cannot also add `mathematics`.

## Features

### Dashboard and Timer

The Dashboard is the main screen for starting and monitoring a study session.

1. Select a subject from the subject list.
2. Press **Start** to begin a session.
3. Press **Pause** when taking a break.
4. Press **Resume** when studying again.
5. Press **Stop session** to save the completed session.

The live timer displays active study time in `HH:MM:SS` format. Only one study
session can be active or paused at a time.

For example:

```text
Subject: Computer Science
Start → study for 25 minutes
Pause → take a 10-minute break
Resume → study for 20 minutes
Stop session

Recorded active time: 45 minutes
```

The Dashboard also shows today's total active study time, the number of completed
sessions, the number of subjects studied, and a table of today's completed sessions.

### Subjects

Use the **Subjects** screen to manage the subjects available when starting a timer.

- Enter a name such as `Physics` and select **Add subject** to create it.
- Select an existing subject in the table and choose **Delete selected** to remove
  it from future timer choices.

Deleting a subject does not delete past study sessions that were recorded under that
subject.

### Sessions

The **Sessions** screen lists completed study sessions. Each entry shows:

- Subject
- Started time
- Ended time
- Active time in `HH:MM:SS`

Use the **From** and **To** date pickers, then select **Apply**, to filter the list.
For example:

```text
From: 01/08/2026
To:   31/08/2026
```

The session-list filter uses a session's derived start date. Select **Clear** to
remove the date filter.

### Statistics

Use the **Statistics** screen to review active study time for an inclusive date
range.

1. Select a start and end date.
2. Choose **Update statistics**.
3. Review the total active study time, the number of subjects with activity, and the
   study-time-by-subject chart.

For example:

```text
From: 01/08/2026
To:   31/08/2026
```

Select **This week** to automatically use the current Monday through today.

## Pauses, Active Time, and Overnight Sessions

When you pause and resume a session, Study Tracker records separate active study
intervals. Breaks are never included in active study time.

Statistics use these active intervals rather than only a session's overall start
date. This means study time that crosses midnight is counted on the correct dates.

For example:

```text
Session span: 23 Aug 2026, 23:00–00:30

Active intervals:
23:00–23:20
00:10–00:30

Statistics for 24 Aug 2026:
20 minutes
```

## Data and Intended Behaviour

- Do not manually edit the JSON files in the application's `data/` directory.
  Study Tracker manages `subjects.json` and `sessions.json` automatically. Invalid
  or inconsistent changes can prevent the application from loading your data.
- Stop a session before closing the application. An active or paused timer is not
  saved as a completed session until **Stop session** is selected.
- Study data is stored locally in the `data/` directory relative to the folder from
  which the application is launched.
- All dates and times use your computer's local date and time. The application does
  not perform timezone conversion.

## Feature Summary

| Feature | Description |
| --- | --- |
| Desktop application | Start Study Tracker from the downloadable `.jar` file. |
| Local storage | Subjects and completed sessions are saved locally. |
| Subject management | Add subjects and remove subjects from future timer choices. |
| Live timer | View current active study time in `HH:MM:SS`. |
| Pause and resume | Record only active study intervals; breaks are excluded. |
| Session history | Review completed sessions and filter them by date. |
| Statistics | View total active time and time by subject for a selected date range. |
| Overnight accuracy | Attribute active time to the correct dates when a session crosses midnight. |
