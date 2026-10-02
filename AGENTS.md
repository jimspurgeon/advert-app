# AGENTS.md

Guidance for AI agents and human contributors working on **retarget**, an open source
Android application that lets users apply advertiser-style nudging techniques to their own
goals and habits. Everything in this file applies to every contribution: code, docs,
issues, commit messages, and pull request descriptions.

## Project Overview

- **What it is:** An Android app (Kotlin, Gradle) that borrows engagement and nudging
  techniques from the advertising industry and turns them toward user-chosen goals.
- **License:** See [LICENSE](LICENSE). All contributions are made under that license.
- **Repo:** https://github.com/jimspurgeon/retarget
- **Plan & workflow:** [DEVELOPMENT.md](DEVELOPMENT.md) is the canonical product plan,
  architecture, and branching strategy. **Read it before working on anything.**
- **Evidence:** [docs/research/](docs/research/README.md) is the research library that
  justifies design decisions. Features cite it; check it before proposing
  behavior-affecting changes.

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
- **Don't push** unless asked — and when asked, only after the pre-push gate in
  [§5](#5-agent-change-reporting--human-review-gates) is satisfied. Leave commits
  local or in a branch for review.

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

## 5. Agent Change Reporting & Human Review Gates

These rules govern how any AI agent working in this repository reports its changes and
seeks approval. The objective: the human operator can review, verify, and veto every
change with minimal manual effort, **without having to trust the agent's self-
assessment**. The human is the reviewer of record. The agent proposes; the human
disposes. Scrutiny is proportional: small changes move through lightweight reports,
while large ones earn deep, evidence-dense review before they go anywhere (§5.2).

**Prime directive.** An agent's job is to reduce the human's review workload by
collecting and presenting evidence — not to substitute its own judgment for the
human's. Anything the agent concludes is a *proposal*, not a fact. Every claim about
the code, the build, or the tests must be backed by evidence the human can check:
a copy-pasteable command, or a `file:line` reference with a verbatim quote. If the
human would have to re-derive a fact to trust it, the agent hasn't finished the job.

### 5.1 Mandatory gates — when the agent MUST stop and ask

Do not proceed past any of these points without explicit human approval. Silence,
absence of objection, or a stalled conversation is **not** approval:

1. **Design gate** — before writing nontrivial code (roughly >50 lines, or anything
   touching architecture, permissions, dependencies, data persistence, or nudge
   behavior). Present the problem, the options considered, and a recommendation, per
   §5.5 below. Iterate here; code is cheaper to rewrite before it exists.
2. **Edge-case gate** — the moment the agent notices an ambiguous requirement or an
   unhandled edge case and chooses a behavior on the human's behalf. Report every such
   choice in the change report even if it seemed obvious; promote it to an immediate
   question if the choice could plausibly be wrong or is user-visible.
3. **Pre-commit gate** — before running `git commit`. Present the proposed commit
   message and the diff, organized per §5.3.
4. **Pre-push gate** — before `git push` (this reinforces §3's "don't push unless
   asked"). Push only after the human approves the complete change report. If anything
   material changed since the last approval, re-present.

Additionally, stop **immediately** — mid-task, not at the next gate — when any of these
occur:

- A secret, personal data, or proprietary material is discovered anywhere (§1 rules
  then take over: stop, do not push, report).
- A change touches an ethical guardrail (§2): nudge behavior, permissions, data
  handling.
- Verification fails in a way the agent cannot explain or fix confidently.
- The agent realizes something it previously told the human was inaccurate. Correct
  the record explicitly; never quietly supersede an earlier claim.

**Approvals are scoped, never blanket.** An approval covers exactly the commits and
diff it was given, identified by hash — never "future similar changes." Minor-tier
changes may share one expedited report; anything Standard or Major gets its own gate.

### 5.2 Proportionate review intensity

Every gate in §5.1 is mandatory regardless of size, but review *depth* scales with the
blast radius of the change. Classify the change before reporting it, state the
classification and its justification, and let the human re-classify at will — in
particular upward:

- **Minor** — typo, comment, or doc-only fixes, formatting, resource-string
  additions, or a logic change under ~20 LoC whose blast radius is contained to
  itself. Expedited report: one compact message with the full diff, the verification
  command and its output, and a one-line annotation per relevant checklist box.
  Small is not exempt from the gates — just cheap to review.
- **Standard** — a single-component logic change with tests, no impact on
  architecture, permissions, persistence, or nudge behavior. Full change report
  per §5.3; decisions already cleared at the design gate may be summarized with
  pointers to that conversation.
- **Major** — anything that adds or changes a permission, touches persistence or
  migrations, alters nudge behavior or scheduling, changes public API or module
  boundaries, introduces a dependency, exceeds roughly 400 LoC of diff, spans
  multiple logical commits, or is destined for a PR into `main`. Full change report
  **plus** every deep-scrutiny requirement below, before the pre-push gate.

Deep-scrutiny requirements for Major changes (all required):

- **Per-commit walkthrough.** Each commit gets its own verification evidence and an
  explicit statement of what a reviewer should check in *that* commit — no "see the
  diff."
- **Adversarial self-review.** The agent writes, before presenting, the strongest
  case *against* its own change: inputs that would break it, interactions it might
  have missed, assumptions that could be false, and what a hostile reviewer would
  attack first. Included in the report verbatim — the human decides how much weight
  it deserves.
- **Judgement-call cadence.** At least one §5.5-format decision per logical
  component. If none surfaced, the agent has either stopped looking or the change is
  more mechanical than it appears — say which, and defend it.
- **Review kit.** An ordered file list for review, exact reproduction commands, and
  the two to four highest-risk spots each with pinned evidence per §5.6, so the human
  can verify the risky parts first and skim the rest.

When torn between tiers, round up and let the human round down.

### 5.3 The change report (required at the pre-commit and pre-push gates)

Gates 3 and 4 use the same artifact, built incrementally so the human can review early
and often. A change report without all of these sections is incomplete:

1. **Requested vs. delivered.** Quote the task as given. List every deviation from it
   and the reason. "Did what was asked plus a drive-by refactor" must be visible, not
   buried in the diff.
2. **Commit map.** For each commit: hash, message, files changed with
   insertions/deletions, and one sentence of intent. Commits must isolate logical
   changes (see §5.6) so the human can approve or reject them independently.
3. **Design decisions.** Every point where the agent chose among alternatives during
   implementation — library choices, API shapes, data structures, naming, ordering,
   defaults. For each: the options, the chosen one, and why, in the §5.5 format.
   Decisions already cleared at the design gate may be summarized with a pointer to
   that conversation.
4. **Edge cases.** A numbered list. For each: (a) the triggering condition, stated
   precisely; (b) the behavior implemented; (c) a verbatim quote of the code that
   handles it with `file:line`; (d) the test that exercises it, or an honest
   explanation of why no test exists and what risk that leaves. Explicitly list edge
   cases the agent *identified but deliberately did not handle*, and why.
5. **Verification.** The exact commands run, the real exit codes, and the tail of the
   actual output — verbatim, never paraphrased. Include failed runs and flaky
   reruns; a red-then-green story is information the human needs. If verification
   could not be run (no SDK, etc.), say so plainly rather than implying success.
6. **Self-audit.** Run the §6 checklist and annotate *every* box with its evidence
   (command, output, or `file:line` quote) — not just a checkmark. "I looked at the
   diff and it seems fine" is not evidence.
7. **Uncertainty ledger.** Things the agent is unsure about, ranked by severity, each
   with the cheapest command or inspection that would resolve it. Honest
   uncertainty here is a feature; its absence is a red flag.
8. **Questions for the human.** Open decisions, each presented per §5.5. If there are
   none, state that explicitly — an agent with zero questions after nontrivial work
   has probably stopped looking.

### 5.4 Evidence rules

- **Quote the codebase.** Support every nontrivial claim about the code with the file
  path, line numbers, and a verbatim excerpt. Never describe code the human needs to
  judge when you can show it — a prose summary forces the human to open the file to
  confirm, which defeats the purpose.
- **Separate observation from inference.** "CreativeRotatorTest passes (ran
  `./gradlew test --tests CreativeRotatorTest`, exit 0)" is an observation. "This
  should be thread-safe" is an inference and must be labeled as such, with the
  reasoning shown so it can be attacked.
- **Make commands reproducible.** Every verification command must be copy-pasteable
  as-is from the repo root, and deterministic wherever possible.
- **Report failures verbatim.** Include stderr tails and exit codes. Re-running a
  failing build until it passes, without reporting the failures, is grounds for
  automatic rejection of the change.
- **Anchor to commits.** When describing the state of the repo, reference commit
  hashes so statements survive subsequent changes.

### 5.5 Presenting options and reasoning

For every decision submitted to the human (design gate, edge-case gate, or open
question in the report), use this format:

- **Decision:** one sentence stating what is being chosen.
- **Context:** two to four sentences of background, with `file:line` anchors to the
  code or docs that make this decision necessary.
- **Options:** each option gets: what changes concretely; blast radius (files and
  behaviors affected); cost to implement; cost to reverse later; the specific risk
  of being wrong and how that risk would manifest; supporting evidence.
- **Recommendation:** one option, with the reasoning chain spelled out so the human
  can find the weak link. If the recommendation depends on an assumption about intent
  or requirements, name the assumption.

Ground rules:

- Never present exactly one option unless no alternative genuinely exists — and then
  say so explicitly, so the human knows it's exhaustive rather than lazy.
- **Bias toward asking.** When torn between presenting a decision and choosing
  silently, present it. One unnecessary question costs the human minutes; a silently
  wrong choice costs a re-review and erodes trust in every other claim in the
  report.
- **Interact with challenges.** If the human disputes a recommendation, re-present
  the options with their objection incorporated as a constraint — don't relitigate
  the old framing.
- **Define terminology on first use** rather than avoiding it. Clarity of explanation
  is required; oversimplification is not.
- No false balance. If one option is clearly correct, say so and explain why, rather
  than staging a debate. But if a rejected option is plausible, keep it in the list;
  humans are good at catching what silently disappeared.
- One decision per message. Bundling unrelated decisions forces all-or-nothing
  answers.
- Quantify where possible ("~40 LoC in 2 files", "adds one Gradle module", "no DB
  migration") instead of adjectives.
- **Don't dumb it down.** Use precise terminology and name the actual mechanism —
  "race between WorkManager enqueue and the BOOT_COMPLETED receiver" not "a timing
  thing". The operator's expertise should be the bottleneck, not the report's
  vocabulary. Explain *why* exhaustively; summarize *what* faithfully.
- Present the human with the strongest version of each option, steelmanned — not
  strawmen set up to make the recommendation obvious.

### 5.6 Minimizing the human's verification workload

The agent bears the cost of making review cheap:

- **Isolate changes.** Separate commits for mechanical churn (renames, formatting,
  package moves) vs. behavior changes, and say which commits are which. The human's
  scarce attention should go to behavior diffs, not re-reading renamed code.
- **Call out hard-to-review hotspots.** Concurrency and ordering, time/timezone/DST
  arithmetic, persistence and migration, permission flows, null-handling fan-out,
  and diff hunks where moved code resembles changed code. For each hotspot: why it's
  risky, what to look at, and what evidence the agent can offer beyond "looks right".
- **Pre-verify edge cases yourself.** Before asking the human to weigh in on edge-case
  behavior, demonstrate it: a targeted unit test that pins the behavior, or a scratch
  run whose inputs and outputs are shown. The human should never have to construct
  test inputs by hand just to see what the code does.
- **Suggest a review order.** Tell the human where a limited review budget is best
  spent ("if you check only one thing, check the FreshStartCalendar DST boundary in
  commit X").
- **Give exact pointers, not areas.** `FreshStartCalendar.kt:87-103`, not "around the
  middle of the calendar file". Quote the minimum sufficient context inline so many
  questions are answerable without leaving the report.
- **For UI changes,** provide build-and-navigate steps (menu path, taps, observable
  outcome) so behavior can be checked on-device even when screenshots aren't
  available in the session.
- **Offer to split.** If the report reveals the change is bigger than one review can
  comfortably hold, propose splitting the branch before the push rather than after.

### 5.7 Anti-patterns (automatic grounds for rejection)

An agent doing any of the following has failed the review process, regardless of
whether the underlying code is good:

- Claiming tests or builds pass without a verbatim command, real output, and exit
  code.
- Describing a diff in prose instead of showing it, or summarizing what a file now
  contains without quoting it.
- Presenting a completed checklist without per-item evidence.
- "I considered alternatives" without naming any.
- Omitting an edge case, deviation, or failure the agent knew about.
- Pushing — or committing — past a gate without explicit approval.
- Slicing review depth the wrong way: steamrolling a Major change through a
  Minor-style report, or burying a typo fix in Major-level ceremony. Depth must
  track the tier, in both directions.
- Applying social pressure to the operator ("this is probably fine to push",
  repeated re-asking after a rejection). The gates exist to be used.

## 6. Review Checklist (run through before every PR)

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
- [ ] Change report (§5.3) presented with per-item evidence for each box above —
      a bare checkmark is not a completed checklist.

When in doubt about whether something belongs in a public commit: **it doesn't.**
Ask in an issue instead.
