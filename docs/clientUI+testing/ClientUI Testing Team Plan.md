# Sub-Team Planning Document

**Sub-Team Name:** Client UI / Testing 
**Product Owner (TA):** Mahboobe Shakeri  
**Team Members:** Kade Peters 30252567 
Jarod Rideout
Hamnah Suleman 30254434
Morgan Lee 30208363

---

## Executive Summary

**Sub-Team Responsibility:** Our team is responsible for designing and building all the UI aspects of this project. Screens for each game and menu. Also, responsible for creating testing strategies and methods of testing for the project. 

**Key Integration Points:** We need to coordinate closely with both Rules + validation for the design of the games and also Platform Core for the Main menu screens.

**Current Status:** Design Phase

---

## Design Phase (Iterations 1 & 2)

### Problem Statement

We are currently addressing the specifics of UI design and how our UI components are going to work together.

### Design Solution Overview

We are currently creating UI Mock up designs as well as use cases and class diagrams for our UI components to address how they will all look, work together, and function individually.

### Architecture & Design Decisions

#### Key Components

| Component Name    | Responsibility                                                                                                                      | Interfaces With                                                  |
|-------------------|-------------------------------------------------------------------------------------------------------------------------------------|------------------------------------------------------------------|
| Blackjack UI      | Display for running the blackjack game                                                                                              | Game Dashboard, Settings                                         |
| Chess UI          | Display for running the Chess game                                                                                                  | Game Dashboard, Settings                                         |
| Game Dashboard    | Dashboard for displaying game joining options                                                                                       | Settings, Chess UI, Blackjack UI, Lobby?                         |
| Settings          | Settings screen for user to change account settings                                                                                 | Menu Dashboard, Game Dashboard, Chess UI, BlackJack UI, settings |
| Menu Dashboard    | Display Menu items, Allows user to look at other player's profiles, their own profile, go to the games dashboard and check settings | Game Dashboard, Settings, Profile Dashboard, login               |
| Profile Dashboard | Allows player's to check their own stats as well as look at other player's stats                                                    | Settings, Menu Dashboard                                         |
| Login             | Login to check player's credentials and load their profiles                                                                         | Menu Dashboard                                                   |
#### Design Patterns Used

- **[Pattern Name]:** [Why this pattern fits your solution]
- **[Pattern Name]:** [Why this pattern fits your solution]

### Integration Strategy

#### Dependencies on Other Sub-Teams

| Dependency                                           | Sub-Team           | Interface/Contract | Status  |
|------------------------------------------------------|--------------------|--------------------|---------|
| Rules for games and logic behind them                | Rules + Validation | ?                  | Defined |
| Logic for platform core in menus and player profiles | Platform Core      | ?                  | Defined |
#### Services Provided to Other Sub-Teams

| Service         | Sub-Team Using It | Interface/Contract                     | Status  |
|-----------------|-------------------|----------------------------------------|---------|
| Testing methods | All Teams         | Various methods within a testing class | Defined |


### Design Artifacts

#### Structure (Class) Diagram

![UI Class Diagram](docs/clientUI+testing/UI_Class_Diagram.drawio.xml)

**Diagram Location:** docs/clientUI+testing/UI_Class_Diagram.drawio.xml

**Key Classes:**
- **[ClassName]:** [Responsibility and key methods]
- **[ClassName]:** [Responsibility and key methods]

#### Use Cases

| Use Case ID | Use Case Name | Actor | Priority |
|-------------|---------------|-------|----------|
| UC-[##] | [Name] | [User/System] | [High/Medium/Low] |

**Detailed Use Case Descriptions:** [Link to use_case_descriptions.pdf]

#### State Machines

There will need to be important state changes when users and opponents makes moves in the games. The state will have to change to reflect those changes. There will also need to be State changes for updates in player's profile stats as well as changes in other's profiles stats that the user can see.

**Example: Game Room Lifecycle**
- WAITING → STARTING → IN_PROGRESS → COMPLETED
- [Describe transition conditions]

### Design Trade-offs & Assumptions

**Key Assumptions:**
1. [Assumption 1 - e.g., "Maximum 4 players per game room"]
2. [Assumption 2]
3. [Assumption 3]

**Trade-offs Made:**
1. **[Decision]:** [Why we chose option A over option B]
2. **[Decision]:** [Why we chose option A over option B]

### Design Changes from Iteration 1 to Iteration 2

[Document any significant changes to the design during the design phase]

| Original Design | Revised Design | Reason for Change |
|-----------------|----------------|-------------------|
| [What it was] | [What it became] | [Why we changed it] |

---

## Implementation Phase (Iteration 3)

### Implementation Status

**Overall Progress:** [X]% Complete

| Feature/Component | Status | Assignee | Notes |
|-------------------|--------|----------|-------|
| [Feature name] | [Not Started/In Progress/Complete/Testing] | [Name] | [Any blockers or notes] |

### Code Organization

**Package Structure:**
```
src/
├── [package name]/
│   ├── [sub-package]/
│   │   ├── [Class].java
│   │   └── [Class].java
│   └── [sub-package]/
└── test/
    └── [package name]/
```

**Key Files:**
- **[Filename]:** [Purpose]
- **[Filename]:** [Purpose]

### Testing Strategy

#### Unit Tests

[Describe your unit testing approach]

**Test Coverage Target:** [X]%

**Key Test Classes:**
- [TestClass] - [What it tests]
- [TestClass] - [What it tests]

#### Integration Tests

[Describe how you're testing integration with other sub-teams]

**Integration Test Scenarios:**
1. [Scenario 1]
2. [Scenario 2]

### Implementation Challenges & Resolutions

| Challenge | Impact | Resolution | Status |
|-----------|--------|------------|--------|
| [What went wrong] | [High/Medium/Low] | [How we fixed it] | [Resolved/In Progress] |

### Integration Issues

[Document any problems discovered during integration with other sub-teams]

**Open Integration Issues:**
- [ ] [Issue description] - Blocked by [Sub-team name]
- [ ] [Issue description] - Waiting for [Component/API]

**Resolved Integration Issues:**
- [x] [Issue description] - Resolved by [Solution]

### Code Review Notes

[Track feedback from TA code reviews]

**Review Date:** [Date]  
**Reviewer:** [TA Name]  
**Status:** [Approved / Revisions Requested]

**Feedback Summary:**
1. [Feedback item 1]
2. [Feedback item 2]

**Action Items:**
- [ ] [Action item from review]
- [ ] [Action item from review]

---

## Iteration Planning & Task Breakdown

### Iteration 1 Tasks (Design - Initial)

| Task | Assignee | Estimated Hours | Status | Actual Hours |
|------|----------|-----------------|--------|--------------|
| Create architecture overview | [Name] | [#] | [ ] | [#] |
| Design class diagram | [Name] | [#] | [ ] | [#] |
| Write use case descriptions | [Name] | [#] | [ ] | [#] |
| Create state machine diagrams | [Name] | [#] | [ ] | [#] |
| Prepare design presentation | [Name] | [#] | [ ] | [#] |

### Iteration 2 Tasks (Implementation & Integration)

| Task | Assignee | Estimated Hours | Status | Actual Hours |
|------|----------|-----------------|--------|--------------|
| Complete core functionality | [Name] | [#] | [ ] | [#] |
| Implement validation logic | [Name] | [#] | [ ] | [#] |
| Integration testing with [Sub-team] | [Name] | [#] | [ ] | [#] |
| Complete test coverage | [Name] | [#] | [ ] | [#] |
| Bug fixes and refinements | [Name] | [#] | [ ] | [#] |
| Documentation updates | [Name] | [#] | [ ] | [#] |
| Record technical demo videos | [Name] | [#] | [ ] | [#] |

---

## Team Contribution Tracking

### Individual Worklogs

[Link to individual Kanban/Scrum boards]

**Weekly Contribution Summary:**

| Team Member | Week of [Date] | Hours Logged | Key Contributions |
|-------------|----------------|--------------|-------------------|
| [Name] | [Date] | [#] | [Brief description] |
| [Name] | [Date] | [#] | [Brief description] |

### Git Activity Summary

[Track meaningful commits and merge requests]

**Branch Strategy:** [e.g., feature branches merged to dev, then to main]

**Recent Merges:**
- [Date] - [Branch name] - [Description] - [Author]
- [Date] - [Branch name] - [Description] - [Author]

---

## Presentation Materials

### Iteration 1 Design Presentation

**Presentation Date:** [Date]  
**Presenters:** [Names]  
**Duration:** [X] minutes

**Key Slides:**
1. Problem statement and scope
2. Proposed solution architecture
3. Class diagram walkthrough
4. Integration points
5. Key design decisions

**Feedback Received:**
- [Feedback point 1]
- [Feedback point 2]

### Iteration 3 Final Demonstration

**Video Submission:** [Link to group showcase video]  
**Timestamp File:** [Link to timestamps document]

**Demo Coverage:**
- [ ] Setup and execution instructions
- [ ] Core functionality demonstration
- [ ] Integration with other sub-teams
- [ ] Testing demonstration
- [ ] Design evolution explanation

---

## Appendix

### Meeting Notes

**[Date] - Design Planning Meeting**
- **Attendees:** [Names]
- **Decisions Made:** [List key decisions]
- **Action Items:** [List follow-ups]

**[Date] - Integration Sync with [Other Sub-team]**
- **Attendees:** [Names]
- **Decisions Made:** [List key decisions]
- **Action Items:** [List follow-ups]

### References

- [Link to project specification]
- [Link to shared platform documentation]
- [Link to GitLab repository]
- [Link to design artifacts folder]

---

## Document Change History

| Date | Version | Author | Changes |
|------|---------|--------|---------|
| [Date] | 1.0 | [Name] | Initial document creation |
| [Date] | 1.1 | [Name] | Updated after Iteration 1 feedback |
| [Date] | 2.0 | [Name] | Added Iteration 2 implementation details |
| [Date] | 3.0 | [Name] | Final updates for Iteration 3 |

---

**Last Updated:** [Date]  
**Document Owner:** [Primary contact name and UCID]