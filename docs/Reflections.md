# Reflections on the use of LLM and Prompting

## 1. AI Discrepancy during planning
In the earlier phases of the project, after brainstorming and deciding on an application to build, I had asked ChatGPT to provide a general architectural structure of the entire application for me based around the MVC (Model-View-Controller) concept. The diagram it provided is shown below:

                    VIEW
        ┌───────────────────────────┐
        │                           │
        │ TimerView                 │
        │ SessionHistoryView        │
        │ DashboardView             │
        │                           │
        └─────────────┬─────────────┘
                      │
                 User actions
                      │
                      ▼
                 CONTROLLER
        ┌───────────────────────────┐
        │                           │
        │ TimerController           │
        │ SessionController         │
        │ DashboardController       │
        │                           │
        └─────────────┬─────────────┘
                      │
               Application actions
                      │
                      ▼
                    MODEL
        ┌───────────────────────────┐
        │                           │
        │ StudySession              │
        │ Subject                   │
        │ StudyTimer                │
        │ TimerState                │
        │                           │
        └───────────────────────────┘

Satsified with the intial diagram, I began prompting the LLM for the code for the respective classes one at a time, starting with the `StudySession` class.

Some context for the next prompt, while it makes sense to request for all code that would logically fall under the "Model" component of the object first, I had some queries earlier about what role the "Controller" component would play in a system like this, and hence asked the LLM for the code for the `TimerController` class.

It then gave me the code which was a class that kept track of things like `TimerState`, `Subject`, `startTime` and `endTime`. Which confused me because the LLM had also earlier mentioned the existence of a `StudyTimer` class, which sounds like the class to use to keep track of these attributes instead of a controller class. I clarified this with the LLM by asking how the code it gave me for `TimerController` differed from the planned `StudyTimer` class, and it admitted that I had caught an "architectural issue".

This was slightly strange to me as for the benefit of both myself and the LLM, I had asked it to describe the MVC model earlier at the start of planning, wanting this information to help guide the LLM in an attempt to ensure correct code later on. It might have been that there were too many messages between when I initially asked about MVC, and now when asking for actual code, such that the context of the previous interaction had been lost.

After the LLM had finished correcting itself, I requested for the `StudyTimer` code and adding "with these modifications in mind", hoping that the LLM would take into consideration what it itself had just explained.

To avoid things like this in the future, especially when multiple interactions have taken place over a decently long period of time, I can help the LLM recall any context I want by adding it into my current prompt, instead of relying on it to pull something from our conversation history, which it might have lost by that point.

## 2. Bug in Implementation only discovered during Testing
In the early stages of development when I had just decided on the application's functionality, I admittedly had nto given much thought into how detailed it should be, in the sense that I had only intended for the timer to have start and stop functionality, logging the duration between, before storing it as a completed session.

While asking for the code for `StudyTimer`, the LLM had, by itself, implemented pause and resume functionality as well. Not thinking much of it, I decided to welcome these new additions to my application as the LLM itself had also detailed how having pause and resume functions would affect actual study time recorded. This was because pausing a session effectively indicates that the user is taking a short break from studying, and thus the time should not be counted towards the overall session duration, and thus the LLM recommended calculating the duration after every pause and adding it to an `elapsedTime` attribute, which would only keep track of active study time.

Much later down the line while I was wanting to move on to test case creation, I wanted to brainstorm some edge cases myself first, before suggesting them to AI and asking that it does the tedious work of adding some obvious test cases like null parameters checking etc. I had realised that there was a part of my implementation that was now unclear due to the newly added pause and resume functionality.

I had wanted a functionality where the user would be able to view their own statistics (time spent studying per subject etc.) while also allowing them to specify a date period to pull data from. Since I was now only keeping track of active study time, what could happen was a user could have a session that lasted from 23/08/2026 23:00 to 24/08/2026 00:30. If the user requests to view statistics from 24/08/2026 onwards, how was this session going to count towards the compilation of statistics?

With the current implementation, since only the total study time is recorded, the session could show as having lasted 50 minutes, but I would have no way of knowing whether the 50 minutes lay entirely before 24/08/2026 00:00 or if it had some of it that was after 24/08/2026 00:00 which made data compilation difficult. Since this app was about allowing users to keep track of their study times, it would be unwise to provide incorrect information, and hence I decided that the implementation of both `StudySession` and `StudyTimer` had to change, keeping track of active intervals instead of only the total elapsed time.

I brought this idea up to the LLM and it agreed with my assessment. At the end of the day, it was my understanding of test case design and Boundary Value Analysis that allowed me to spot this problem before it was too late in the development process, where more code might have made it harder to make changes.

## 3. Incorrect Test Case Implementation
I had asked the LLM to generate some test cases for my Storage component, and for a few test cases it had created a helper function called `assertSessionEquals` which aimed to be a more concise way for the reader to know that the two `StudySession` objects were being compared to make sure the reconstructed `StudySession` had the accurate and correct attributes.

It was only because I had taken the time to check through and verify the implemented code that I stumbled upon the actual implementation of `assertSessionEquals`. Instead of comparing the stored `StudyIntervals` of both sessions, it only checked that both sessions stored the same number of intervals, which made no sense to me. I brought it up to the LLM, to which it then responded admitting it had made a mistake, and changed the method to verify that both set of intervals were the same, instead of only storing the same number of intervals.

I can only imagine this arose from the LLM's understanding that calling a `equals()` method on both interval lists would not work as both would be treated as different objects with different pointers, and would not work. However, instead of comparing the raw values themselves, it weirdly deemed that the only solid way to compare the two session objects was to make sure that they had the same number of intervals stored.

This attitude might also have spawned from its earlier explanation of test case design when it came to time. It mentioned that because matching of `LocalDateTime` and/or `Duration` objects would come down to the smallest of margins, such that it would be unwise to expect that just because you started the timer 10ms ago, that if you compare it to `LocalDateTime.now()` that the difference would be exaclty 10ms. It might have gotten the impression from itself that testing and comparing time in Java is not reliable, and thus only opted to compare the number of intervals, which makes no sense in this separate context.

In the future, perhaps it would be wise for me to explicitly state what it should mean for two objects from a user-defined class to be "equal" so that there is no discrepancy.
