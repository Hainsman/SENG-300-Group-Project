# Game Abstraction – High-Level Design

## Purpose

This document defines the shared screen abstraction used by all the UI elements of the platform. The Goal of this abstraction is to allow multiple UI elements to run on the same infrastructure while sharing 

This abstraction is owned primarily by the **Client UI/ Testing** sub-team, but is designed to integrate cleanly with Platform Core and Rules + Validation.

---

## Design Goals

---

## High-Level Concept


## Core Abstraction: `Screen`


### Responsibilities



### Conceptual Interface (Java-oriented)

```text

```

> Note: This is a conceptual interface. Exact method signatures are intentionally omitted at this stage.

---

## Player Actions


### PlayerAction (Conceptual)


---

## Rules & Validation Abstraction


### RuleValidator Responsibilities


### Conceptual Structure

```text

```


---

## Game-Specific Implementations

### Blackjack Implementation


### Chess Implementation


## Interaction Flow (High-Level)

