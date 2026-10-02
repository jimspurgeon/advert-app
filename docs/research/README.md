# Research Index

Annotated bibliography of the research that informs advert-app's design. Each entry
includes practical takeaways for this project. Anyone (human or agent) proposing
feature changes should check whether the research here supports or contradicts the idea.

## Contents

- [advertising-psychology.md](advertising-psychology.md) — Core persuasion/branding theory
- [behavior-change-science.md](behavior-change-science.md) — Habits, goals, nudges
- [interruption-timing.md](interruption-timing.md) — When/where to deliver prompts
- [imagery-domains.md](imagery-domains.md) — Domain-specific evidence (hydration, plants, nature)
- [digital-wellbeing.md](digital-wellbeing.md) — Avoiding notification fatigue / the backfire risk
- [android-platform.md](android-platform.md) — Platform capabilities and constraints
- [references.bib](references.bib) — BibTeX for all citations

## How to use this library

1. **Designing a nudge feature:** read advertising-psychology.md + the relevant domain
   file, then check digital-wellbeing.md for the backfire/fatigue constraints.
2. **Choosing timing:** interruption-timing.md is authoritative on scheduling strategy.
3. **Citing claims:** use references.bib keys when documenting why a design decision
   was made, e.g., `zajonc1968mere` for the mere exposure effect.
4. **Adding research:** append entries to the relevant file AND references.bib together,
   in the same PR as the feature or doc that uses them.
