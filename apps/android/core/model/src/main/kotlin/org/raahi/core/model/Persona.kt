package org.raahi.core.model

/** Who Didi detects in onboarding (S2 question 2); decides which home the person gets. */
enum class Persona { Class9To10, Class11To12, GraduateItiDropout, Teacher }

/**
 * Reading-level estimate (Add-on A, rule 8). Low = more audio and pictures, less text,
 * on the same screens. Estimated from onboarding and tap-to-hear frequency (Phase 2).
 */
enum class ReadingLevel { Low, Standard }
