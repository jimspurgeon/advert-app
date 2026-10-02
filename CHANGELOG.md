# Changelog

All notable changes to this project are documented here. Format loosely follows
[Keep a Changelog](https://keepachangelog.com/); versioning is SemVer-ish
(`vMAJOR.MINOR.PATCH` — see DEVELOPMENT.md).

## [Unreleased]

### Added
- AGENTS.md: contributor + AI-agent rules (security, ethics, collaboration, Android
  standards, review checklist).
- `docs/research/` evidence library: advertising psychology, behavior-change
  science, interruption & timing, imagery domains, digital wellbeing guardrails,
  Android platform constraints, BibTeX references.
- DEVELOPMENT.md: product vision, domain model, architecture, phased roadmap,
  branching strategy, definition-of-done, pre-registered decisions.
- Core domain skeletons with unit tests: fatigue-aware `CreativeRotator`,
  `BudgetPolicy` (hard notification caps, quiet hours, dismissal cooldowns),
  `FreshStartCalendar` (temporal landmark boosts).
- CI workflow (build + unit tests), PR template embedding review checklist.
