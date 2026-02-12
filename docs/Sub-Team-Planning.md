# Sub-Team Planning Document

**Sub-Team Name:** [Your Sub-Team Name Here]
**Product Owner (TA):** Mahboobe Shakeri  
**Team Members:** [List all members with UCIDs]

---

## Executive Summary

**Sub-Team Responsibility:** [Brief description of what your sub-team is responsible for building]

**Key Integration Points:** [List the other sub-teams you need to coordinate with]

**Current Status:** [Design Phase / Implementation Phase / Integration Phase]

---

## Design Phase (Iterations 1 & 2)

### Problem Statement

[Describe the specific problem or platform need your sub-team is addressing. Be clear about the scope and boundaries of your work.]

### Design Solution Overview

[Provide a high-level explanation of your proposed solution. How does it address the problem? What are the main components?]

### Architecture & Design Decisions

#### Key Components

| Component Name | Responsibility | Interfaces With |
|----------------|----------------|-----------------|
| [Component 1] | [What it does] | [Other components/teams] |
| [Component 2] | [What it does] | [Other components/teams] |

#### Design Patterns Used

- **[Pattern Name]:** [Why this pattern fits your solution]
- **[Pattern Name]:** [Why this pattern fits your solution]

### Integration Strategy

#### Dependencies on Other Sub-Teams

| Dependency | Sub-Team | Interface/Contract | Status |
|------------|----------|-------------------|--------|
| [What you need] | [Team name] | [API/method signature] | [Defined/In Progress/Complete] |

#### Services Provided to Other Sub-Teams

| Service | Sub-Team Using It | Interface/Contract | Status |
|---------|-------------------|-------------------|--------|
| [What you provide] | [Team name] | [API/method signature] | [Defined/In Progress/Complete] |

### Database & Server Stubs

[Describe where database and server integration points will be located in your code. These should be clearly marked stubs.]

**Database Stub Locations:**
- [File/Class name] - [Purpose]
- [File/Class name] - [Purpose]

**Server Communication Stub Locations:**
- [File/Class name] - [Purpose]
- [File/Class name] - [Purpose]

### Design Artifacts

#### Structure (Class) Diagram

[Include or link to your class diagram. Focus on core platform classes and their relationships.]

**Diagram Location:** [class_diagram.png in /diagrams folder]

**Key Classes:**
- **[ClassName]:** [Responsibility and key methods]
- **[ClassName]:** [Responsibility and key methods]

#### Use Cases

| Use Case ID | Use Case Name | Actor | Priority |
|-------------|---------------|-------|----------|
| UC-[##] | [Name] | [User/System] | [High/Medium/Low] |

**Detailed Use Case Descriptions:** [Link to use_case_descriptions.pdf]

#### State Machines

[Describe any important state transitions in your components]

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
