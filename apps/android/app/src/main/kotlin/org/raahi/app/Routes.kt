package org.raahi.app

import kotlinx.serialization.Serializable

/** Every destination. Tab roots: Home (घर), Mountain (सीखो), Earn (कमाओ). */
@Serializable sealed interface Route {
    @Serializable data object LanguagePick : Route // S1
    @Serializable data object Onboarding : Route // S2
    @Serializable data object Home : Route // S3
    @Serializable data object Lesson : Route // S4
    @Serializable data object AskDidi : Route // S5
    @Serializable data object Earn : Route // S6
    @Serializable data object Progress : Route // S7
    @Serializable data object ClassView : Route // S8
    @Serializable data object Mountain : Route // S9
    @Serializable data object TrapTest : Route // S10
    @Serializable data object TrapSummary : Route // S11
    @Serializable data object VideoSegment : Route // S12
    @Serializable data object TrapCleared : Route // S13
    @Serializable data object Courses : Route // S14
    @Serializable data object ExamPlan : Route // S15
    @Serializable data object PayAfterEarn : Route // S16
    @Serializable data object Events : Route // S17
    @Serializable data object Documents : Route // S18
    @Serializable data object SchemeCheck : Route // S19
    @Serializable data object Verdict : Route // S20
    @Serializable data object Enrollment : Route // S21
    @Serializable data object Heatmap : Route // T1
    @Serializable data object Assign : Route // T2
    @Serializable data object Studio : Route // T3
    @Serializable data object Pricing : Route // T4
    @Serializable data object Earnings : Route // T5
    @Serializable data object Planner : Route // T6
    @Serializable data object Recommend : Route // T7
    @Serializable data object ScreenIndex : Route // debug only
}

/** Screen index for the Phase 1 click-through checkpoint (debug builds only). */
val AllScreens: List<Pair<String, Route>> = listOf(
    "S1 Language picker" to Route.LanguagePick,
    "S2 Didi onboarding" to Route.Onboarding,
    "S3 Home" to Route.Home,
    "S4 Lesson question" to Route.Lesson,
    "S5 Ask Didi" to Route.AskDidi,
    "S6 Earn" to Route.Earn,
    "S7 Progress" to Route.Progress,
    "S8 Teacher class view" to Route.ClassView,
    "S9 Career mountain" to Route.Mountain,
    "S10 Trap test" to Route.TrapTest,
    "S11 Trap summary" to Route.TrapSummary,
    "S12 Video segment" to Route.VideoSegment,
    "S13 Trap cleared" to Route.TrapCleared,
    "S14 Courses" to Route.Courses,
    "S15 Exam prep plan" to Route.ExamPlan,
    "S16 Pay after you earn" to Route.PayAfterEarn,
    "S17 NGO events" to Route.Events,
    "S18 Documents ready" to Route.Documents,
    "S19 Scheme check" to Route.SchemeCheck,
    "S20 Verdict" to Route.Verdict,
    "S21 Enrollment guide" to Route.Enrollment,
    "T1 Class traps heatmap" to Route.Heatmap,
    "T2 Assign" to Route.Assign,
    "T3 Course studio" to Route.Studio,
    "T4 Pricing" to Route.Pricing,
    "T5 Earnings" to Route.Earnings,
    "T6 Lesson planner" to Route.Planner,
    "T7 Recommend" to Route.Recommend,
)
