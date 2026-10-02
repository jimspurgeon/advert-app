# Digital Wellbeing — Avoiding the Backfire

The flip side of effectiveness: this app's core risk is becoming indistinguishable
from the notification spam users hate. Everything here constrains feature design.

## 1. Notification Fatigue Is Real and Measurable

**Evidence:**

- Median smartphone user receives ~56 notifications/day (Pielot et al. 2018,
  794k notifications study); high volume correlates with dismiss-without-read and
  app disabling.
- Sahami Shirazi et al. (CHI 2014): negative sentiment concentrates on perceived-as-
  commercial, irrelevant, or badly-timed notifications.
- Randomized disabling trials (Fitz et al. / FU Berlin RCT, N=205): turning
  notifications off for a week *increased* subjective digital wellbeing during the
  intervention period — a direct warning that more notifications ≠ more engagement
  ≠ happier users.
- Mehrotra et al.: receptivity drops after recent high notification throughput.

**Implication:** Hard caps and honest budgets are product features. The app's success
metric is NOT engagement-with-app; it's goal adherence + user-reported satisfaction
with pacing. We instrument accordingly (on-device metrics only).

## 2. Friction Research Cuts Both Ways

**Evidence:** "one sec" longitudinal study (Grüning et al., CHI 2024, N≈1,039,
13.4 weeks): small design frictions (breathing screen before social apps)
effectively reduced social media use over months, and users kept them. Friction
can help *for the user's chosen objectives* when transparent.

**Implication:** Friction is a legitimate tool for the user's own goals (e.g.,
opt-in app-blocker-lite features pairing "want" apps with goal check-ins), but
must be user-installed, transparent, and easily removable. We never apply friction
to app-exit or settings (dark patterns ban).

## 3. Ethical Line: Persuasive Design Without Manipulation

**Framework refs:** Thaler & Sunstein's transparency principle (nudges should be
easy to opt out of); Western digital-ethics guidance on persuasive tech; the
ACM Code of Ethics. Persuasion ≠ coercion: we preserve user agency at all times.

**Concrete product rules (binding for all agents/contributors):**

1. No manufactured urgency, scarcity, or countdown-to-loss pressure.
2. No guilt/shame framing, especially post-lapse. Fresh-start + self-compassion
   framing only. (Loss aversion is only ever used defensively — e.g., "you built
   this routine for 3 weeks" — and gently.)
3. Every nudge channel individually toggleable; global kill switch prominent.
4. Per-channel frequency caps exist in code, not just settings UI (defense in
   depth: even bugs can't spam).
5. Quiet hours sacred by default (22:00–07:00).
6. No attention-maximizing analytics goals, ever. No badge counts that induce
   obligation, no infinite feeds inside the app.
7. "Why am I seeing this?" — any nudge explains itself on tap (which goal, which
   technique, how to turn it down).
8. Data stays on device. Full stop. No analytics SDKs. Optional export for the
   user's own analysis.
9. Feature-caused distress has an escape hatch: rapid "this is too much" flow
   reduces volume in one tap and offers pacing audit.
10. The app never uses fear appeals (research: they drive avoidance, not adoption,
    for wellness behaviors without accompanying efficacy boosts).

## 4. Balance Metrics (what we optimize instead of engagement)

- **Adherence:** logged goal behaviors per week (user's own reports).
-  **Nudge acceptance:** actionable-response rate (opened/acted-on vs dismissed).
- **Pacing satisfaction:** periodic in-app pulse (1-5 scale: "Are nudges at the
  right amount for you?"). Trend down → auto-reduce budgets and surface notice.
- **Retention of intent:** does the user still have the goal installed after 8
  weeks? (Success = user graduates or maintains, not churn-maximization.)

These metrics are local-only. No engagement-growth optimization loops that don't
serve the user's stated goal.

## 5. The "Weaponized but user-owned" test

Every feature PR must answer: **"Would the user, watching a full replay of what
the app did today, say 'that's what I asked for'?"** If a reasonable user might
feel tricked, ashamed, trapped, or nagged against their wishes, the feature fails
review — regardless of measured effectiveness.
