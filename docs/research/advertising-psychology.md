# Advertising Psychology — Core Persuasion & Branding Theory

How advertisers build brand awareness and attractiveness, and how each technique maps
to a self-nudge mechanism in retarget.

## 1. Mere Exposure Effect (Zajonc, 1968)

**Theory:** Repeated perceptual exposure to a stimulus increases liking toward it,
even without any conscious processing of content. Affective enhancement occurs simply
because the stimulus is made accessible to perception.

**Evidence:** Zajonc's monograph reviewed word-frequency/affect correlations,
experimental exposure studies, and subliminal exposure paradigms; the effect is one of
the most replicated in social psychology (later meta-analyses confirm it, with the
caveat that it plateaus and can reverse with very high exposure frequencies — see
wearout below).

**App mapping — WALLPAPER ROTATION.** This is the app's signature mechanism:
curated imagery of the target lifestyle (clear water, vibrant produce, forest trails)
rotated onto the lockscreen/wallpaper at strategic intervals. The user gets thousands
of low-cost "impressions" of their goal-lifestyle without opening any app. Wallpaper
is ambient, zero-interrupt, and inherently high-exposure (checked dozens of times
daily).

- Key numbers: recognition curves rise steeply over the first ~10 exposures; the effect
  emerges strongest when stimuli are presented briefly/intermittently (as wallpaper
  glances are).
- Design note: frequency must be capped (see wearout, §4) — alternate images rather
  than repeating one.

## 2. Evaluative Conditioning (pairing brand with positive affect)

**Theory:** Attitudes toward a neutral object shift when it repeatedly co-occurs with
affectively charged stimuli (celebrities, pleasant music, beautiful imagery). Two
routes: indirect (memory link between objects) and direct affect transfer (Sweldens,
van Osselaer & Alba's work on branding via conditioning).

**App mapping — CURATED IMAGE PACKS + POSITIVE CONTEXT PAIRING.**

- Each goal ships with imagery *pre-tested* for positive valence: crisp macro shots of
  water droplets, sun-lit produce, calm lakes. The "brand" being conditioned is the
  user's own goal identity ("I'm a hydrated person", "I eat colorful plants").
- Copy that accompanies notifications pairs the behavior with sensory pleasure
  ("crisp, cold glass of water") rather than duty ("drink 8 glasses").

## 3. Effective Frequency & The Three-Hit Theory (Krugman, 1972)

**Theory:** Herb Krugman argued three exposures suffice for TV advertising:
exposure 1 = "What is it?" (orienting/curiosity), exposure 2 = "What of it?"
(evaluative relevance to self), exposure 3 = disengagement/confirmation ("already
know this") — at which point additional repetitions contribute little. Media planning
conventionally adopted "3+" as the effective frequency baseline (Ostrow's 1982 model
adjusts for competitive clutter, message complexity, recency, etc.).

**App mapping — DOSAGE MODEL.**

- Goal activation needs a small but reliable number of exposures per day/week —
  not constant bombardment. Default recommended schedule aims for multiple
  distinct-channel contacts (wallpaper glance + 1-2 notifications + occasional
  overlay) rather than notification spam.
- Different creative assets (images/copy) count as fresh exposures; identical ones
  wear out (see §4).

## 3b. Elaboration Likelihood Model (Petty & Cacioppo, 1986)

**Theory:** Persuasion happens via a central route (careful argument scrutiny — high
effort) or peripheral route (cues: attractiveness, source credibility, positive
affect — low effort). Over a lifetime of brand contacts, attitudes formed via the
central route are more durable, but peripheral cues dominate low-involvement
contexts — which describes phone wallpaper/glances exactly.

**App mapping:** Most app touchpoints are deliberately peripheral (imagery, color,
beauty — no lecturing), while onboarding (goal selection, reasons why, customization)
is deliberately central-route: users articulate their own motivations, which produces
durable, self-generated attitudes. Onboarding captures user's own "brand story".

## 4. Creative Fatigue / Wearout

**Theory:** An advertisement's effectiveness changes with repetition: effects
wear in over initial exposures, plateau, then decline ("wearout") as tedium,
weariness, and counter-arguing set in. Research on creativity × repetition
(Smith et al. 2016) shows creative ads wear out slower; rotating themes delays wearout
(a single ad wears out faster than rotating versions).
Digital advertising research (Braun & Moe 2013; fatigue-aware creative selection,
Fatemi et al. 2019) formalizes "effective frequency capping" per creative per user.

**App mapping — FATIGUE-AWARE CREATIVE ROTATOR.**

- Every creative asset (image, copy variant) carries an exposure ledger: times shown
  per user, time since last shown. The rotation engine scores candidates by
  fatigue-adjusted score = base appeal × novelty decay (recency penalty).
- Theme rotation = the same goal (e.g., hydration) rotates sub-themes: "clarity,"
  "refresh," "energy," "skin glow," "river/cloud imagery."
- Defaults tuned so no single asset hits the steep wearout zone; when a category is
  saturated, introduce rest periods or switch modality.
- This is one of the app's core algorithmic contributions and a key differentiator
  from naive reminder apps. Unit-test heavily.

## 5. Brand Personality & Ideal-Self Positioning (Aaker; Sirgy's self-congruence)

**Theory:** Consumers prefer brands whose personality (Aaker's five dimensions:
sincerity, excitement, competence, sophistication, ruggedness) matches their actual or
*ideal* self-concept (self-congruence, Sirgy). Aspirational positioning ("this brand is
who you want to be") drives preference.

**App mapping — GOAL = BRAND.** The user picks an aspirational identity ("plant-based
eater", "nature-connected person", "hydrated, energized me"). The app "brands" that
identity in the same way a company would its flagship product: consistent visual
identity per goal (color palette, imagery style), taglines, mascot/pattern options,
and a progress-oriented narrative arc (storytelling). Self-congruence theory suggests
letting users choose among goal "voice" options (e.g., caring friend vs. coach) —
and pragmatically, notification tone should match the user's chosen "relationship"
with the goal.

## 6. Memory Encoding: Visual Superiority & Multi-Sense Encoding

**Theory:** Pictures are remembered substantially better than words (picture
superiority effect); concrete imagery encodes deeper than abstract; multimodal
(paired visual+verbal) encoding beats either alone.

**App mapping:** Every nudge must be *visual-first*: notification with image, wallpaper
change with intent, overlay screens with bold imagery and one line of copy. The small
amount of copy should be concrete and sensory ("crisp cold water"), not abstract stats.
Where possible, subtle sound or haptic pairing (optional) can strengthen encoding,
though this needs to stay opt-in (interruptiveness).

## Summary Table

| Advertising technique | Mechanism | App feature |
|---|---|---|
| Repetition to build familiarity/liking | Mere exposure | Wallpaper/image rotation |
| Pair with positive affect | Evaluative conditioning | Curated valenced image packs |
| 3+ exposures across channels | Effective frequency | Multi-channel exposure ledger |
| Peripheral-cue aesthetics | ELM | Visual-first design everywhere |
| Creative rotation & fatigue capping | Wearout research | Fatigue-aware rotator engine |
| Ideal-self branding | Self-congruence | Goal-as-brand, user-tailored voice |
| Picture superiority | Visual encoding | Image-led notifications/overlays |
| Storytelling & brand arcs | Narrative persuasion | Progress narratives in-app |
| Prime the user to convert | Priming effects (cross-modal) | Context-triggered nudges |
| Recency-weighted budgets | Recency planning | Burst-then-rest scheduling |
