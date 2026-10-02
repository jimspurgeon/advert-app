# Behavior Change Science — Habits, Goals, Nudges

The scientific backbone for *what* the app helps users do (beyond advertising theory,
which informs *how* it's presented).

## 1. Implementation Intentions (Gollwitzer & Sheeran)

**Finding:** Gollwitzer & Sheeran's (2006) meta-analysis of 94 studies (8,000+
participants) found a **medium-to-large effect (d ≈ 0.65)** of if-then planning
("When situation Y arises, I will do Z") on goal achievement, on top of mere goal
intentions. The 2024 follow-up meta-analysis (642 tests) confirms the effect across
domains and refines *which formats* work: plans specifying when/where/how in concrete
terms outperform vague resolutions.

**App mapping — ONBOARDING CAPTURES IF-THEN PLANS.** Goal setup asks not just
"what do you want?" but "when/where do you want to do it?" and pre-fills plausible
triggers ("after waking", "with lunch", "when I get home from work"). The scheduler
aligns nudge delivery windows to these anchor contexts. When users log completions,
the app reinforces the link ("You said after breakfast — nice").

## 2. Habit Formation Trajectory (Lally et al., 2010)

**Finding:** 96 participants performing a daily eating/drinking/activity behavior in
a consistent context for 12 weeks; automaticity followed an **asymptotic curve**,
median time to plateau ≈ **66 days** (range 18–254). Missing a single occasion did
not materially derail formation. Complexity negatively predicted automaticity gains.

**Implication:** Advert-app is a *long-game* companion, not a 30-day sprint app. UX
should celebrate "milestones" on realistic timelines, avoid implying habits form in
21 days, and treat missed days gently (no punitive streak mechanics as default — see
loss aversion guardrail below). Defaults should expect (and forgive) lapses.

## 3. Fogg Behavior Model (B = MAP)

**Finding:** Behavior happens when **Motivation, Ability, and a Prompt** converge.
Prompts ("triggers") fail when motivation/ability are low; tiny behaviors succeed
because ability requirement is minimal. Timing the prompt to moments of naturally
higher motivation/ability (or reducing ability cost — "make it easy") is the model's
core prescription.

**App mapping:** Every nudge channel is a *prompt* in Fogg terms. The scheduler's job
is maximizing P(motivation × ability) at delivery: avoid dead-of-night delivery,
align to user's active window, and make the suggested action tiny ("sip water" not
"drink 64oz"). Nudge copy emphasizes ease and immediacy.

## 4. Fresh Start Effect (Dai, Milkman & Riis, 2014)

**Finding:** Google searches for "diet" and gym attendance spike after temporal
landmarks (new year, birthdays, Mondays, month starts, post-birthday). People
are more likely to initiate goal pursuit at these boundaries because they feel
like a "clean slate" separating past-failure self from current self.

**App mapping — LANDMARK-AWARE SCHEDULING.** The engine recognizes temporal landmarks
(waking up, Monday, the 1st, equinoxes/solstices, user-selected personal dates) and
gently boosts goal-priming content near them — e.g., fresh imagery themes and
"new chapter" copy. Never manufacture urgency beyond reality; the landmark itself
provides the motivation window.

## 5. Temptation Bundling (Milkman et al.)

**Finding:** Bundling a "want" (page-turner audiobooks) with a "should" (gym visits)
increased gym attendance 51-62% in field experiments; teaching the technique also
works (Kirgios et al. 2020). Works by pairing immediate gratification with delayed
benefit behaviors.

**App mapping:** Offer (opt-in) "bundle pairing" at goal setup: user names an enjoyment
they'll allow only alongside the goal behavior (podcast + meal prep, favorite playlist
only while walking). The app can then remind of the bundle ("Your audiobook is waiting")
instead of the chore framing.

## 6. Loss Aversion & Streaks — USE WITH CARE

**Finding:** Losses loom larger than gains (~2:1, Kahneman & Tversky); streak-styled
design leveraging loss aversion can motivate but creates fragile motivation and
backfires at lapse ("what-the-hell effect" — breaking a streak triggers abandonment
due to abstinence violation effect).

**App mapping:** Streaks are available but OFF by default; framed as "consistency
notes" rather than fragile chains. After any lapse, messaging pivots to
self-compassion + fresh-start framing (never guilt). This is a deliberate ethical
stance, not just UX taste — see AGENTS.md ethics section.

## 7. Self-Determination Theory (SDT — Deci & Ryan)

**Finding:** Autonomous motivation (intrinsic, self-endorsed) predicts sustained
behavior change; controlling/external pressure undermines intrinsic motivation.

**App mapping:** The user sets the goals; the app never decides what's "good for"
them. All framing is identity-affirming and choice-preserving. Settings always expose
control (frequency caps, quiet hours, per-goal pausing). The "advertiser" in this app
works for the user's elected goals — it never overrides user intent (informed by
autonomy-supportive message framing, e.g., SDT-based mHealth framing studies).

## 8. Nudge Theory (Thaler & Sunstein)

**Finding:** Choice architecture shapes behavior predictably without forbidding
options or significantly changing incentives; nudges work best when transparent
and easily rejected.

**App mapping:** The app literally implements choice architecture for one's own
environment (wallpaper = default option visibility, notifications = salience,
friction design for competing options). Being transparent about the technique being
used ("This is a salience nudge") is both ethical per Thaler & Sunstein and builds
trust. In-app "Why am I seeing this?" affordances expose the playbook.

## 9. Self-monitoring & Feedback Loop

**Finding:** Self-monitoring is among the most effective behavior change techniques
(BCTs); immediate, specific feedback outperforms delayed/aggregate feedback.

**App mapping:** Lightweight logging (one-tap check-ins) with elegant visualization
per goal. Feedback ties exposures → check-ins ("your wallpaper saw 40 glances today,
you logged 3 sips — the campaign is working"). Over time, correlate exposure to
behavior (opt-in, on-device only).
