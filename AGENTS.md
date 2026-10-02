# AGENTS.md

Guidance for AI agents and human contributors working on **advert-app**, an open source
Android application that lets users apply advertiser-style nudging techniques to their own
goals and habits. Everything in this file applies to every contribution: code, docs,
issues, commit messages, and pull request descriptions.

## Project Overview

- **What it is:** An Android app (Kotlin, Gradle) that borrows engagement and nudging
  techniques from the advertising industry and turns them toward user-chosen goals.
- **License:** See [LICENSE](LICENSE). All contributions are made under that license.
- **Repo:** https://github.com/jimspurgeon/advert-app

---

## 1. Security & Confidentiality — Non-Negotiable Rules

This is a **public repository**. Assume everything committed is visible to the world,
forever (including in git history). Before committing, pushing, or pasting anything:

### Never commit or disclose
- **Secrets of any kind**: API keys, signing keys, keystores (`.jks`/`.keystore` are
  gitignored — never work around that), tokens, passwords, OAuth client secrets,
  `google-services.json`, `local.properties`, or debug/base64-encoded variants of these.
- **Personal or user data**: real names, emails, phone numbers, device identifiers,
  location traces, or analytics dumps. Use synthetic/fake data in tests, fixtures, and
  docs. This includes data belonging to the maintainer — never paste real exports from
  a personal device, calendar, or usage logs.
- **Private infrastructure**: internal hostnames, IPs, SSH configs, CI credentials,
  or anything from the maintainer's local environment.
- **Proprietary material**: no code, assets, or documentation copied from a current or
  former employer or any closed-source project. Advertising techniques used as
  *inspiration* should be described generically or sourced from public material.

### Operational rules
- If a secret is discovered in the repo or in a working file, **stop, do not push**,
  report it, and let the maintainer rotate the credential and handle history rewriting
  (e.g., with `git filter-repo`). Do not attempt to "fix" history silently.
- Never disable or weaken `.gitignore` entries for secret-like files. Extend it when new
  config types appear.
- New dependencies must be declared in version catalogs / Gradle files only — never
  vendored as compiled binaries or tarballs of unknown provenance.
- Keys needed for local development go in untracked files (e.g., a template checked in
  as `secrets.example.properties`, with the real file gitignored).

### App-level privacy (this category of work matters extra)
This app is *about* behavioral influence. That makes trust and privacy core product
features, not afterthoughts:

- The app must be **local-first**: user goal data stays on-device by default. Adding
  network transmission of behavioral data requires explicit user consent UI, a written
  rationale in the PR, and maintainer approval.
- Do not add analytics, ads SDKs, trackers, or third-party telemetry. This project's
  entire premise is giving users advertiser techniques *for themselves* — embedding an
  actual ad network would be both hypocritical and a hard rejection.
- Any permission requested (notifications, exact alarms, accessibility, etc.) needs a
  documented, minimal justification. Avoid requesting permissions "for later."

## 2. Ethical Guardrails for Nudge Features

Features implement persuasion techniques intentionally. Keep them on the right side of
the line:

- **User-initiated only.** Every nudge targets a goal the user explicitly created.
  Never add nudges aimed at app retention for its own sake (no "come back" spam, no
  streak guilt-tripping unless the user enabled streaks).
- **No dark patterns.** No manipulative UI toward the user: fake urgency, hidden opt-outs,
  confirm-shaming, obstructed cancellation/deletion, or pre-checked consent boxes.
- **Reversible and controllable.** Every nudge feature needs a discoverable off switch
  at least as easy to find as the feature itself. Users must be able to inspect, edit,
  export, and fully delete their data and goals.
- **Transparency.** Techniques borrowed from advertising should be explainable in plain
  language in-app (e.g., "this reminder is scheduled at your peak responsive time").
- **Framing.** Language in code, docs, and UI should reflect *self-directed* behavior
  change ("nudge," "prompt," "cue"), not covert manipulation of others. This app nudges
  **oneself**; features designed to influence other people without their knowledge are
  out of scope and should be flagged.

If a feature request seems to cross into manipulating third parties or overriding user
intent, raise it in an issue before implementing.

## 3. Open Source Collaboration Practices

### Git hygiene
- **Commit messages:** imperative mood, present tense ("Add streak scheduler", not
  "Added..."). Reference issues where applicable ("Closes #12").
- **Never rewrite published history** (`push --force`, rebase onto main) without
  maintainer instruction. Never commit directly to `main` — work in feature branches
  named `feat/...`, `fix/...`, `chore/...`, `docs/...`.
- **Atomic PRs:** one logical change per PR. Keep them reviewable (< ~400 lines of diff
  where practical). Include *what* and *why* in the description, not just screenshots.
- **Don't push** unless asked. Leave commits local or in a branch for review.

### Community conduct
- Be respectful and assume good faith in issues and reviews.
- Discuss significant design decisions (architecture, new dependencies, permission
  changes) in an issue/PR **before** writing lots of code.
- Generated content (AI-assisted or otherwise) is welcome, but the submitter is
  responsible for every line in the PR — verify it compiles, passes tests, and follows
  these rules.
- Respect the license: no GPL/aggressive-copyleft code into a repo licensed otherwise
  without maintainer sign-off; keep third-party snippets attributed.

### Documentation
- Update `README.md` when adding setup steps, features, or changing build requirements.
- New user-facing features need a brief note in docs or the changelog in the same PR.
- Keep comments explaining *intent*; the code shows the *how*.

## 4. Android Engineering Standards

- **Language/build:** Kotlin first. New modules use Gradle Kotlin DSL. Don't introduce
  Groovy DSL, XML-heavy patterns, or Java without reason.
- **Runs locally:** before marking work done, the project must assemble
  (`./gradlew assembleDebug`) and pass whatever test suite exists
  (`./gradlew test`). Report honestly if you couldn't run them (e.g., no SDK/AVD
  available) rather than claiming success.
- **Compatibility:** respect the declared `minSdk`; avoid gated APIs without version
  checks. Target the latest stable SDK unless the repo says otherwise.
- **Testing:** new logic (schedulers, nudge engines, scoring/ranking algorithms) needs
  unit tests. UI changes should be verifiable via screenshot or simple instrumentation
  test where feasible.
- **Architecture:** follow existing structure (view models, repositories, DI setup) —
  don't invent a parallel pattern mid-project. If no structure is established yet,
  propose one in an issue first.
- **Dependencies:** prefer well-known, actively maintained libraries; pin versions;
  check licenses for compatibility.
- **Resources:** no hardcoded user-visible strings — use `strings.xml` and keep future
  localization possible.

## 5. Review Checklist (run through before every PR)

- [ ] No secrets, personal data, or proprietary material anywhere in the diff
      (double-check added files, not just edited ones).
- [ ] `.gitignore` updated if new config/credential file types were introduced.
- [ ] Nudges are user-initiated, reversible, and explained; no dark patterns; no
      manipulation of third parties.
- [ ] No analytics/tracking/ad SDKs added.
- [ ] Permissions additions are justified and minimized.
- [ ] Builds cleanly; tests added/updated and passing (or limitations stated).
- [ ] Strings externalized; minSdk respected.
- [ ] Commit message and PR description follow conventions; docs updated.
- [ ] Nothing references private information about anyone, including from
      outside the repo (issue text, screenshots, fixture data).

When in doubt about whether something belongs in a public commit: **it doesn't.**
Ask in an issue instead.
