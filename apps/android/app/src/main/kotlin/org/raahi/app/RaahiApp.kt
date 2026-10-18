package org.raahi.app

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.consumeWindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBars
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import org.raahi.core.designsystem.component.PriceType
import org.raahi.core.designsystem.component.RaahiTab
import org.raahi.core.designsystem.language.ProvideAppLanguage
import org.raahi.core.designsystem.theme.RaahiColors
import org.raahi.core.designsystem.theme.RaahiTheme
import org.raahi.core.model.AppLanguage
import org.raahi.core.model.Persona
import org.raahi.feature.askdidi.AskDidiScreen
import org.raahi.feature.askdidi.askDidiDesignReference
import org.raahi.feature.courses.CoursesScreen
import org.raahi.feature.courses.ExamPlanScreen
import org.raahi.feature.courses.PayAfterEarnScreen
import org.raahi.feature.courses.coursesDesignReference
import org.raahi.feature.courses.examPlanDesignReference
import org.raahi.feature.earn.DocumentsScreen
import org.raahi.feature.earn.EarnScreen
import org.raahi.feature.earn.EnrollmentGuideScreen
import org.raahi.feature.earn.SchemeCheckScreen
import org.raahi.feature.earn.VerdictScreen
import org.raahi.feature.earn.documentsDesignReference
import org.raahi.feature.earn.earnDesignReference
import org.raahi.feature.earn.enrollmentDesignReference
import org.raahi.feature.earn.schemeQuestionDesignReference
import org.raahi.feature.earn.verdictDesignReference
import org.raahi.feature.events.EventsScreen
import org.raahi.feature.events.eventsDesignReference
import org.raahi.feature.home.HomeScreen
import org.raahi.feature.home.homeDesignReference
import org.raahi.feature.learn.LessonQuestionScreen
import org.raahi.feature.learn.TrapClearedScreen
import org.raahi.feature.learn.TrapSummaryScreen
import org.raahi.feature.learn.TrapTestScreen
import org.raahi.feature.learn.VideoSegmentScreen
import org.raahi.feature.learn.lessonDesignReference
import org.raahi.feature.learn.trapClearedDesignReference
import org.raahi.feature.learn.trapTestDesignReference
import org.raahi.feature.learn.trapsDesignReference
import org.raahi.feature.learn.videoDesignReference
import org.raahi.feature.onboarding.LanguagePickerScreen
import org.raahi.feature.onboarding.OnboardingScreen
import org.raahi.feature.onboarding.OnboardingStep
import org.raahi.feature.onboarding.OnboardingUiState
import org.raahi.feature.progress.ProgressScreen
import org.raahi.feature.progress.progressDesignReference
import org.raahi.feature.roadmap.CareerMountainScreen
import org.raahi.feature.roadmap.mountainDesignReference
import org.raahi.feature.teacher.AssignScreen
import org.raahi.feature.teacher.ClassScreen
import org.raahi.feature.teacher.CourseStudioScreen
import org.raahi.feature.teacher.EarningsScreen
import org.raahi.feature.teacher.HeatmapScreen
import org.raahi.feature.teacher.LessonPlannerScreen
import org.raahi.feature.teacher.PricingScreen
import org.raahi.feature.teacher.RecommendScreen
import org.raahi.feature.teacher.TaggedQuestion
import org.raahi.feature.teacher.classDesignReference
import org.raahi.feature.teacher.heatmapDesignReference
import org.raahi.core.designsystem.R as DsR
import org.raahi.feature.teacher.R as TeacherR

/**
 * Phase 1 app shell. Every screen runs on design-reference state (labelled by the banner);
 * Phase 2 swaps each `*DesignReference()` for a ViewModel reading Room. Nothing here touches
 * the network, so the whole app works in airplane mode from install.
 */
@Composable
fun RaahiApp(showScreenIndex: Boolean) {
    var language by rememberSaveable { mutableStateOf(AppLanguage.Default) }
    ProvideAppLanguage(language) {
        RaahiTheme {
            val nav = rememberNavController()
            Column(Modifier.fillMaxSize().background(RaahiColors.Surface)) {
                DesignReferenceBanner(onClick = { if (showScreenIndex) nav.navigate(Route.ScreenIndex) })
                Box(Modifier.weight(1f).consumeWindowInsets(WindowInsets.statusBars)) {
                    RaahiNavHost(nav, language, onLanguage = { language = it })
                }
            }
        }
    }
}

@Composable
private fun DesignReferenceBanner(onClick: () -> Unit) {
    Box(
        Modifier
            .fillMaxWidth()
            .background(RaahiColors.GoldTint)
            .statusBarsPadding()
            .clickable(onClick = onClick)
            .padding(horizontal = 16.dp, vertical = 3.dp),
        contentAlignment = Alignment.Center,
    ) {
        Text(stringResource(DsR.string.design_reference), style = RaahiTheme.typography.en, color = RaahiColors.Gold)
    }
}

private fun NavHostController.tab(tab: RaahiTab) {
    val route: Route = when (tab) {
        RaahiTab.Home -> Route.Home
        RaahiTab.Learn -> Route.Mountain
        RaahiTab.Earn -> Route.Earn
    }
    navigate(route) {
        popUpTo(Route.Home) { saveState = true }
        launchSingleTop = true
        restoreState = true
    }
}

@Composable
private fun RaahiNavHost(nav: NavHostController, language: AppLanguage, onLanguage: (AppLanguage) -> Unit) {
    val back: () -> Unit = { nav.popBackStack() }
    val none: () -> Unit = {}
    NavHost(nav, startDestination = Route.LanguagePick) {
        composable<Route.LanguagePick> {
            LanguagePickerScreen(language, onSelect = { onLanguage(it); nav.navigate(Route.Onboarding) }, onMic = none, onType = none)
        }
        composable<Route.Onboarding> {
            var state by remember { mutableStateOf(OnboardingUiState(language = language)) }
            val defaultName = stringResource(org.raahi.feature.onboarding.R.string.designref_name)
            OnboardingScreen(
                state = state,
                onNameChange = { state = state.copy(name = it) },
                onPersona = { state = state.copy(persona = it) },
                onLanguage = { state = state.copy(language = it); onLanguage(it) },
                onInterest = { i -> state = state.copy(interests = if (i in state.interests) state.interests - i else state.interests + i) },
                onGoal = { state = state.copy(goal = it) },
                onNext = {
                    val next = OnboardingStep.entries.getOrNull(state.step.ordinal + 1)
                    if (next != null) {
                        state = state.copy(step = next, name = state.name.ifBlank { defaultName })
                    } else {
                        nav.navigate(if (state.persona == Persona.Teacher) Route.ClassView else Route.Home) {
                            popUpTo(Route.LanguagePick) { inclusive = true }
                        }
                    }
                },
                onMic = none,
                onType = none,
            )
        }
        composable<Route.Home> {
            HomeScreen(
                homeDesignReference(),
                onLesson = { nav.navigate(Route.Lesson) },
                onMoney = { nav.tab(RaahiTab.Earn) },
                onPath = { nav.tab(RaahiTab.Learn) },
                onEvent = { nav.navigate(Route.Events) },
                onProgress = { nav.navigate(Route.Progress) },
                onMic = { nav.navigate(Route.AskDidi) },
                onTab = { nav.tab(it) },
            )
        }
        composable<Route.Lesson> {
            var picked by remember { mutableStateOf<Int?>(null) }
            LessonQuestionScreen(lessonDesignReference(picked), onPick = { picked = it }, onAskDidi = { nav.navigate(Route.AskDidi) }, onBack = back)
        }
        composable<Route.AskDidi> {
            AskDidiScreen(askDidiDesignReference(), onListenAgain = none, onDocuments = { nav.navigate(Route.Documents) }, onMic = none, onType = none)
        }
        composable<Route.Earn> {
            EarnScreen(
                earnDesignReference(),
                onOpportunity = { nav.navigate(Route.Enrollment) },
                onSchemeCheck = { nav.navigate(Route.SchemeCheck) },
                onDocuments = { nav.navigate(Route.Documents) },
                onFillForm = { nav.navigate(Route.Enrollment) },
                onProgress = { nav.navigate(Route.Progress) },
                onTab = { nav.tab(it) },
            )
        }
        composable<Route.Progress> { ProgressScreen(progressDesignReference(), onPassport = none, onBack = back) }
        composable<Route.ClassView> {
            ClassScreen(
                classDesignReference(),
                onLearner = { nav.navigate(Route.Home) },
                onAlert = { nav.navigate(Route.Heatmap) },
                onSharePack = none,
                onRecommend = { nav.navigate(Route.Recommend) },
                onEarnings = { nav.navigate(Route.Earnings) },
                onStudio = { nav.navigate(Route.Studio) },
                onPlanner = { nav.navigate(Route.Planner) },
            )
        }
        composable<Route.Mountain> {
            CareerMountainScreen(
                mountainDesignReference(),
                onNode = {},
                onStart = { nav.navigate(Route.TrapTest) },
                onCourses = { nav.navigate(Route.Courses) },
                onTab = { nav.tab(it) },
            )
        }
        composable<Route.TrapTest> {
            TrapTestScreen(trapTestDesignReference(), onPick = { nav.navigate(Route.TrapSummary) }, onBack = back, onMic = none, onType = none)
        }
        composable<Route.TrapSummary> {
            TrapSummaryScreen(trapsDesignReference(), onWatch = { nav.navigate(Route.VideoSegment) }, onRetry = { nav.navigate(Route.TrapCleared) }, onBack = back)
        }
        composable<Route.VideoSegment> {
            var line by remember { mutableIntStateOf(1) }
            var speed by remember { mutableStateOf(1f) }
            VideoSegmentScreen(
                videoDesignReference(line, speed),
                onSpeed = { speed = it },
                onLine = { line = it },
                onPlay = none,
                onRetry = { nav.navigate(Route.TrapCleared) },
                onBack = back,
            )
        }
        composable<Route.TrapCleared> {
            TrapClearedScreen(trapClearedDesignReference(), onContinue = {
                nav.navigate(Route.Mountain) { popUpTo(Route.Mountain) { inclusive = true } }
            })
        }
        composable<Route.Courses> {
            CoursesScreen(
                coursesDesignReference(),
                onFilter = {},
                onCourse = {},
                onExamPrep = { nav.navigate(Route.ExamPlan) },
                onPayAfterEarn = { nav.navigate(Route.PayAfterEarn) },
                onBack = back,
            )
        }
        composable<Route.ExamPlan> { ExamPlanScreen(examPlanDesignReference(), onExam = {}, onItem = { nav.navigate(Route.VideoSegment) }, onLiveClass = none, onBack = back) }
        composable<Route.PayAfterEarn> { PayAfterEarnScreen(sponsor = null, onListenAgain = none, onParentMessage = none, onBack = back) }
        composable<Route.Events> { EventsScreen(eventsDesignReference(), onRsvp = {}, onRemind = {}, onCheckIn = {}, onBack = back) }
        composable<Route.Documents> { DocumentsScreen(documentsDesignReference(), onToggle = {}, onRemind = none, onBack = back) }
        composable<Route.SchemeCheck> {
            SchemeCheckScreen(schemeQuestionDesignReference(), onPick = { nav.navigate(Route.Verdict) }, onBack = back, onMic = none, onType = none)
        }
        composable<Route.Verdict> { VerdictScreen(verdictDesignReference(), onApply = { nav.navigate(Route.Enrollment) }, onBack = back) }
        composable<Route.Enrollment> {
            EnrollmentGuideScreen(
                enrollmentDesignReference(),
                onStep = {},
                onDone = none,
                onCopy = {},
                onOpenForm = none,
                onWithTeacher = none,
                onApplied = none,
                onListenAgain = none,
                onBack = back,
            )
        }
        composable<Route.Heatmap> { HeatmapScreen(heatmapDesignReference(), onSendEasier = { nav.navigate(Route.Assign) }, onPlanner = { nav.navigate(Route.Planner) }, onBack = back) }
        composable<Route.Assign> {
            var saved by remember { mutableStateOf(false) }
            AssignScreen(
                topic = stringResource(TeacherR.string.designref_trap_fractions),
                segment = stringResource(TeacherR.string.designref_segment),
                questions = 5,
                stuckChildren = 6,
                saved = saved,
                onSend = { saved = true },
                onBack = back,
            )
        }
        composable<Route.Studio> {
            CourseStudioScreen(
                tagged = listOf(
                    TaggedQuestion(stringResource(TeacherR.string.designref_trap_units), stringResource(TeacherR.string.designref_segment)),
                ),
                onRecord = none,
                onUpload = none,
                onTag = {},
                onNext = { nav.navigate(Route.Pricing) },
                onBack = back,
            )
        }
        composable<Route.Pricing> {
            var type by remember { mutableStateOf(PriceType.Free) }
            var amount by remember { mutableStateOf("") }
            PricingScreen(type, amount, onSelect = { type = it }, onAmount = { amount = it }, onPublish = none, onBack = back)
        }
        composable<Route.Earnings> { EarningsScreen(onNewCourse = { nav.navigate(Route.Studio) }, onBack = back) }
        composable<Route.Planner> {
            var selected by remember { mutableStateOf(setOf(1)) }
            LessonPlannerScreen(
                classes = listOf(stringResource(TeacherR.string.s8_class, "9"), stringResource(TeacherR.string.s8_class, "10")),
                selectedClasses = selected,
                topic = stringResource(TeacherR.string.designref_topic_electricity),
                minutes = 40,
                online = false,
                onClass = { i -> selected = if (i in selected) selected - i else selected + i },
                onMake = none,
                onBack = back,
            )
        }
        composable<Route.Recommend> {
            RecommendScreen(
                student = stringResource(TeacherR.string.designref_name),
                opportunity = stringResource(TeacherR.string.designref_naps),
                opportunityMeta = "apprenticeshipindia.gov.in",
                reasons = listOf(stringResource(TeacherR.string.designref_strength), stringResource(TeacherR.string.designref_interest)),
                onSend = none,
                onBack = back,
            )
        }
        composable<Route.ScreenIndex> { ScreenIndex(onOpen = { nav.navigate(it) }) }
    }
}

@Composable
private fun ScreenIndex(onOpen: (Route) -> Unit) {
    LazyColumn(Modifier.fillMaxSize().statusBarsPadding().padding(horizontal = 16.dp)) {
        item { Text(stringResource(R.string.screen_index), style = RaahiTheme.typography.title, modifier = Modifier.padding(vertical = 12.dp)) }
        items(AllScreens) { (label, route) ->
            Text(
                label,
                style = RaahiTheme.typography.tile,
                modifier = Modifier.fillMaxWidth().heightIn(min = 56.dp).clickable { onOpen(route) }.padding(vertical = 14.dp),
            )
        }
    }
}
