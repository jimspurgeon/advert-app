# Interruption & Timing Research — When to Deliver Nudges

Authoritative guidance for the scheduling engine, drawing on interruption science,
mobile HCI studies, and just-in-time adaptive intervention (JITAI) methodology.

## 1. Notification Response Rates & Timing

**Findings (large-scale notification studies):**

- **Fischer et al. (2010)**, ~large in-situ study: responses to notifications depend
  strongly on presentation context (attended vs. not), with median response times in
  seconds when attended; unattended notifications linger. Response rates improved when
  delivered at "opportune moments" identified by mobile sensing.
- **Sahami Shirazi et al. (2014)** (Large-Scale Assessment of Mobile Notifications,
  CHI): subjective tolerance for notifications varies by app type; users dislike
  feeling interrupted but tolerate task-relevant, well-timed ones. Content perceived
  as commercially motivated or irrelevant received the most negative sentiment.
- **Mehrotra et al. (2015-2017)** (interruptibility studies, ~70k-200k notifications):
  receptivity depends on location, activity (stationary vs. walking), ringer state,
  and recent notification throughput. Notably, **receiving many notifications recently
  lowers receptivity to the next one** — an on-device interruptibility model
  (MiniVM / RIPMiT) predicts receptivity using only sensors/usage features.
  Observable: being at home vs. office changed tolerance profiles.
- **Pielot et al. (2018)** (794,525 notifications, 278 users): median 56
  notifications/day/user; message-type apps dominate attention; dismissal-without-read
  correlates with volume and sender. (Warns us: at high volumes, users dismiss
  without reading.)

**Design implication:** a nudge app must be *volume-disciplined* and
*context-aware*. Deliver few, high-quality, image-led prompts, and monitor context
signals where permitted. Practically: hard caps by default (e.g., ≤2-4/day),
quiet hours, and "recent-notification crowding" backoff.

## 2. Opportune Moments / Interruptibility Models

**Findings:** Studies of opportune moments for interruptions (e.g., Okoshi et al.
2015-2017 "Attelia"/breadcrumbs research; Mehrotra's receptivity modeling) show that
delivering at predicted break-then-engage transitions (end of app sessions, phone
unlock, walking bouts) improves acceptance and reduces perceived interruptions.
"Momentary receptivity" is a measurable state: passive sensing (app usage transitions,
screen state, activity recognition) can estimate it without content analysis.

**Design implication:** the scheduler should prefer unlock moments, post-session
boundaries, and user-declared contexts (e.g., "at work mornings"). Where permissions
are unavailable, degrade gracefully to time-window scheduling with jitter. A
receptivity signal worth tracking (fully on-device): time-to-dismiss and time-to-open
after each nudge, feeding back into timing decisions (bandit-style self-optimization).

## 3. Just-In-Time Adaptive Intervention (JITAI) Framework (Nahum-Shani et al., 2018)

**Framework:** JITAIs deliver "the right type/amount of support at the right time"
by adapting to the user's changing **internal state** (motivation, fatigue) and
**contextual state** (location, activity). Core components: decision points,
tailoring variables, intervention options, decision rules, and proximal outcomes.
Robust JITAIs also spec **load** (burden) constraints, increasingly formalized via
micro-randomized trials (Klasnja et al. 2015 HeartSteps MRT; Künzler et al. Ally
app MRT for receptivity).

**Design implication:** The nudge engine treats each potential delivery slot as a
decision point with a decision rule (deterministic rules + learned weights per user).
Respect "support thresholds" — only intervene when state indicates benefit > burden.
Burden awareness is a first-class constraint, not an afterthought: daily/weekly
budgets, cooldown timers between channels (no overlay within X hours of dismissed
overlay), and saturation signals (dismissal rate rising → cut volume).

## 4. Notification Content: Imagery & Personalization

**Findings:** Studies on notification personalization/contextual tailoring (JITAI/
MRT literature, e.g., HeartSteps suggestions; receptivity research above) show that
contextually-tailored suggestions outperform generic ones, and notification imagery
raises visual capture but must be reserved for genuinely important content.
Heads-up style (peeks) raises immediate response but contributes to perceived
interruption at volume.

**Design implication:** Our defaults: `IMPORTANCE_DEFAULT` (no heads-up) for most
nudges with rich imagery attached, reserving heads-up for user-flagged critical
moments; per-channel budgets; copy varies (fatigue-aware) as per advertising
psych research.

## 5. Resumption & Review

- Reassess pacing every 4 weeks: sample receptivity metrics, adjust default budgets
  (the "campaign review").
- Quarterly "creative refresh" cadence per goal — mirrors ad industry's seasonal
  campaign cycles.
- Track long-term metrics: weekly exposure counts, check-in adherence, opt-out rate
  per channel. Rising opt-outs = pacing failure alarm.
- Micro-randomize *within safe bounds* when uncertain between options (e.g., 50/50
  copy A/B for first week of a new goal) — light-touch MRT discipline.

## 6. Worked Example: Hydration Nudge Scheduling

User declares: water goal, anchor contexts "after waking" and "mid-morning" +
"afternoon slump", daily cap 3/day, quiet hours 22:00–07:00.

1. 07:10±15m — wallpaper switches to morning-fresh water glass scene (mere
   exposure, zero interruption cost).
2. 09:30±30m — first notification: image-led, IMPORTANCE_DEFAULT, copy drawn from
   "refresh" theme pool, fatigue-weighted (least-recently-used weighting).
3. 14:30±45m — afternoon-slump slot with "cellular hydration/energy" theme.
4. Saturday — expanded flex window for off-cycle catch-ups; weekly report card with
   7-day imagery history strip.
5. Post-dismiss backoff: 2h cooldown after a dismissed notification; if two
   dismissals in 6h, skip the next scheduled slot entirely (saturation signal).

## 7. Defaults Summary (recommended, research-aligned)

| Parameter | Default | Rationale |
|---|---|---|
| Daily notification cap | 3/day/goal | Volume discipline (Pielot 2018; Sahami 2014) |
| Quiet hours | 22:00–07:00 | Sleep protection; interruption tolerance low |
| Heads-up | Off by default | Interruptiveness vs. capture tradeoff |
| Overlay frequency | ≤1/day, never during quiet hours | Interruptive channel tier |
| Cooldown after dismissal | 2h min | Saturation/dismissal signal (Mehrotra) |
| Wallpaper change interval | 2–4 days per image, 3+ images in rotation | Wearout avoidance (ad psych §4) |
| Weekly review & pacing tune | Every 4 weeks | Campaign review cadence |
| Fresh-start boosts | Mondays, 1st of month, post-lapse | Dai/Milkman temporal landmarks |

Note: defaults above are starting hypotheses, not dogma — treat them as things to
validate with in-app A/B (micro-randomization within user-consented, bounded
variations), mirroring how advertisers optimize campaigns. The "optimality" the user
buys into at install is *adaptive discipline*, not omniscient perfection.
