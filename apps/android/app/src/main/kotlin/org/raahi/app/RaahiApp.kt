package org.raahi.app

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.content.Intent
import android.net.Uri
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.consumeWindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBars
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import kotlinx.coroutines.delay
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavDestination.Companion.hasRoute
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import org.raahi.core.designsystem.component.MountainNode
import org.raahi.core.designsystem.component.NodeState
import org.raahi.core.designsystem.component.PriceType
import org.raahi.core.designsystem.component.RaahiIcons
import org.raahi.core.designsystem.component.RaahiTab
import org.raahi.core.designsystem.component.TranscriptLine
import org.raahi.core.designsystem.language.ProvideAppLanguage
import org.raahi.core.designsystem.speech.LocalSpeaker
import org.raahi.core.designsystem.speech.Speaker
import org.raahi.core.designsystem.theme.RaahiColors
import org.raahi.core.designsystem.theme.RaahiTheme
import org.raahi.core.designsystem.theme.Tone
import org.raahi.core.engine.Verdict as EngineVerdict
import org.raahi.core.engine.VerdictExplainer
import org.raahi.core.model.AppLanguage
import org.raahi.core.model.Localized
import org.raahi.core.model.Persona
import org.raahi.core.voice.DidiVoice
import org.raahi.feature.askdidi.AskDidiScreen
import org.raahi.feature.askdidi.AskDidiUiState
import org.raahi.feature.courses.CoursesScreen
import org.raahi.feature.courses.ExamPlanScreen
import org.raahi.feature.courses.PayAfterEarnScreen
import org.raahi.feature.courses.coursesDesignReference
import org.raahi.feature.courses.examPlanDesignReference
import org.raahi.feature.earn.DocStatus
import org.raahi.feature.earn.DocumentItem
import org.raahi.feature.earn.DocumentsScreen
import org.raahi.feature.earn.DocumentsUiState
import org.raahi.feature.earn.DraftField
import org.raahi.feature.earn.EarnScreen
import org.raahi.feature.earn.EarnUiState
import org.raahi.feature.earn.EnrollmentGuideScreen
import org.raahi.feature.earn.EnrollmentUiState
import org.raahi.feature.earn.Opportunity
import org.raahi.feature.earn.SchemeCheckScreen
import org.raahi.feature.earn.SchemeQuestionUiState
import org.raahi.feature.earn.Verdict
import org.raahi.feature.earn.VerdictItem
import org.raahi.feature.earn.VerdictScreen
import org.raahi.feature.events.EventsScreen
import org.raahi.feature.events.eventsDesignReference
import org.raahi.feature.home.HomeScreen
import org.raahi.feature.home.HomeUiState
import org.raahi.feature.learn.LessonQuestionScreen
import org.raahi.feature.learn.LessonQuestionUiState
import org.raahi.feature.learn.TrapClearedScreen
import org.raahi.feature.learn.TrapClearedUiState
import org.raahi.feature.learn.TrapSummaryScreen
import org.raahi.feature.learn.TrapTestScreen
import org.raahi.feature.learn.TrapTestUiState
import org.raahi.feature.learn.VideoSegmentScreen
import org.raahi.feature.learn.VideoSegmentUiState
import org.raahi.feature.onboarding.LanguagePickerScreen
import org.raahi.feature.onboarding.OnboardingScreen
import org.raahi.feature.onboarding.OnboardingStep
import org.raahi.feature.onboarding.OnboardingUiState
import org.raahi.feature.progress.Badge
import org.raahi.feature.progress.ProgressScreen
import org.raahi.feature.progress.ProgressUiState
import org.raahi.feature.roadmap.CareerMountainScreen
import org.raahi.feature.roadmap.CareerMountainUiState
import org.raahi.feature.roadmap.NodeDetail
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
import org.raahi.feature.askdidi.R as AskR
import org.raahi.feature.earn.R as EarnR
import org.raahi.feature.learn.R as LearnR
import org.raahi.feature.teacher.R as TeacherR

/** Screens that still show Phase 1 design-reference data; only these carry the gold banner. */
private val DesignReferenceRoutes = listOf(
    Route.Courses::class, Route.ExamPlan::class, Route.PayAfterEarn::class, Route.Events::class, Route.ClassView::class,
    Route.Heatmap::class, Route.Assign::class, Route.Studio::class, Route.Pricing::class, Route.Earnings::class,
    Route.Planner::class, Route.Recommend::class,
)

/**
 * The student flow runs on real data: the bundled content pack (data/curated) and the
 * encrypted on-phone database. Nothing here touches the network; the app has no INTERNET
 * permission. Teacher, course and event screens still show labelled design-reference data.
 */
@Composable
fun RaahiApp(
    showScreenIndex: Boolean,
    voice: DidiVoice,
    voiceInput: VoiceInput,
    askMicPermission: () -> Unit,
    vm: RaahiViewModel = hiltViewModel(),
) {
    val profile by vm.profile.collectAsStateWithLifecycle()
    val content by vm.content.collectAsStateWithLifecycle()
    var picked by rememberSaveable { mutableStateOf<AppLanguage?>(null) }
    val p = (profile as? ProfileState.Some)?.p
    val language = picked ?: p?.let { AppLanguage.fromTag(it.languageTag) } ?: AppLanguage.Default
    LaunchedEffect(language) { voice.setLanguage(language) }

    ProvideAppLanguage(language) {
        voiceInput.uiContext = LocalContext.current
        RaahiTheme {
            CompositionLocalProvider(LocalSpeaker provides Speaker { voice.speak(it) }) {
                Box(Modifier.fillMaxSize().background(RaahiColors.Surface)) {
                    if (profile != null && content != null) {
                        val nav = rememberNavController()
                        val entry by nav.currentBackStackEntryAsState()
                        val designRef = DesignReferenceRoutes.any { entry?.destination?.hasRoute(it) == true }
                        Column(Modifier.fillMaxSize()) {
                            TopStrip(designRef, showScreenIndex, onIndex = { nav.navigate(Route.ScreenIndex) })
                            Box(Modifier.weight(1f).consumeWindowInsets(WindowInsets.statusBars)) {
                                val start: Route = when {
                                    p == null -> Route.LanguagePick
                                    p.persona == Persona.Teacher.name -> Route.ClassView
                                    else -> Route.Home
                                }
                                RaahiNavHost(nav, start, language, onLanguage = { picked = it; vm.setLanguage(it) }, vm, voice, voiceInput, askMicPermission)
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun TopStrip(designReference: Boolean, debug: Boolean, onIndex: () -> Unit) {
    val text = when {
        designReference -> stringResource(DsR.string.design_reference)
        debug -> stringResource(R.string.debug_index)
        else -> null
    }
    if (text == null) {
        Box(Modifier.fillMaxWidth().statusBarsPadding())
        return
    }
    Box(
        Modifier
            .fillMaxWidth()
            .background(if (designReference) RaahiColors.GoldTint else RaahiColors.Surface2)
            .statusBarsPadding()
            .clickable(enabled = debug, onClick = onIndex)
            .padding(horizontal = 16.dp, vertical = 3.dp),
        contentAlignment = Alignment.Center,
    ) {
        Text(text, style = RaahiTheme.typography.en, color = if (designReference) RaahiColors.Gold else RaahiColors.Muted)
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

private fun pictureFor(name: String): Int = when (name) {
    "battery" -> RaahiIcons.Tool
    else -> RaahiIcons.Bulb
}

private fun copy(context: Context, label: String, text: String) {
    val cm = context.getSystemService(ClipboardManager::class.java)
    cm.setPrimaryClip(ClipData.newPlainText(label, text))
}

@Composable
private fun RaahiNavHost(
    nav: NavHostController,
    start: Route,
    language: AppLanguage,
    onLanguage: (AppLanguage) -> Unit,
    vm: RaahiViewModel,
    voice: DidiVoice,
    voiceInput: VoiceInput,
    askMicPermission: () -> Unit,
) {
    val back: () -> Unit = { nav.popBackStack() }
    val none: () -> Unit = {}
    val content = vm.content.collectAsStateWithLifecycle().value ?: return
    val profileState by vm.profile.collectAsStateWithLifecycle()
    val profile = (profileState as? ProfileState.Some)?.p
    val progress by vm.progress.collectAsStateWithLifecycle()
    val answers by vm.answers.collectAsStateWithLifecycle()
    val traps by vm.traps.collectAsStateWithLifecycle()
    val scheme by vm.scheme.collectAsStateWithLifecycle()
    val documents by vm.documents.collectAsStateWithLifecycle()
    val applications by vm.applications.collectAsStateWithLifecycle()
    val context = LocalContext.current
    fun L(text: Localized) = text.get(language)
    val lesson = content.lesson
    val name = profile?.name.orEmpty()

    /** Mic on a tile screen: listen for the tile labels, then act like a tap. */
    fun listenFor(options: List<String>, onIndex: (Int) -> Unit) {
        voiceInput.listen(VoiceIntents.grammar(options), askMicPermission) { text ->
            text?.let { VoiceIntents.match(it, options) }?.let(onIndex)
        }
    }

    NavHost(nav, startDestination = start) {
        composable<Route.LanguagePick> {
            LanguagePickerScreen(language, onSelect = { onLanguage(it); nav.navigate(Route.Onboarding) }, onMic = none, onType = none)
        }
        composable<Route.Onboarding> {
            var state by remember { mutableStateOf(OnboardingUiState(language = language)) }
            val friend = stringResource(R.string.friend)
            OnboardingScreen(
                // Later questions address the student by name; a blank name reads as "friend".
                state = if (state.step == OnboardingStep.Name) state else state.copy(name = state.name.ifBlank { friend }),
                onNameChange = { state = state.copy(name = it) },
                onPersona = { state = state.copy(persona = it) },
                onLanguage = { state = state.copy(language = it); onLanguage(it) },
                onInterest = { i -> state = state.copy(interests = if (i in state.interests) state.interests - i else state.interests + i) },
                onGoal = { state = state.copy(goal = it) },
                onNext = {
                    val next = OnboardingStep.entries.getOrNull(state.step.ordinal + 1)
                    if (next != null) {
                        state = state.copy(step = next)
                    } else {
                        val persona = state.persona ?: Persona.Class9To10
                        vm.saveProfile(state.name, state.language, persona.name, state.interests.map { it.name }.toSet(), state.goal?.name)
                        nav.navigate(if (persona == Persona.Teacher) Route.ClassView else Route.Home) {
                            popUpTo(Route.LanguagePick) { inclusive = true }
                        }
                    }
                },
                onMic = none,
                onType = none,
            )
        }
        composable<Route.Home> {
            val rightOnce = answers.filter { a -> lesson.questions.firstOrNull { it.id == a.questionId }?.correctIndex == a.picked }.map { it.questionId }.toSet()
            val nextQ = lesson.questions.firstOrNull { it.id !in rightOnce }
            val doneTopics = lesson.topics.count { t -> lesson.questions.filter { it.topic == t.id }.all { it.id in rightOnce } }
            val eligible = if (vm.hasSchemeResults()) vm.results().count { it.verdict == EngineVerdict.Eligible } else 0
            val due = traps.count { it.clearedAt != null && (it.dueAt ?: Long.MAX_VALUE) <= System.currentTimeMillis() }
            HomeScreen(
                HomeUiState(
                    name = name.ifBlank { stringResource(R.string.friend) },
                    streakDays = progress.streakDays,
                    lastSyncedAgo = null,
                    lessonMeta = if (due > 0) {
                        stringResource(R.string.home_review_due, due)
                    } else {
                        stringResource(
                            R.string.home_lesson_meta,
                            L(lesson.title),
                            (lesson.questions.indexOf(nextQ).takeIf { it >= 0 } ?: lesson.questions.lastIndex) + 1,
                            lesson.questions.size,
                        )
                    },
                    scholarshipCount = if (vm.hasSchemeResults()) eligible else null,
                    moneyMeta = stringResource(if (vm.hasSchemeResults()) R.string.home_money_meta else R.string.home_money_none),
                    pathMeta = stringResource(R.string.home_path_meta, doneTopics, lesson.topics.size),
                    nudgeMinutes = 5,
                    event = null,
                ),
                onLesson = {
                    // A due re-check comes first: Didi asks again about traps cleared earlier.
                    if (due > 0) { vm.startReview(); nav.navigate(Route.TrapTest) } else { vm.openLesson(); nav.navigate(Route.Lesson) }
                },
                onMoney = { nav.tab(RaahiTab.Earn) },
                onPath = { nav.tab(RaahiTab.Learn) },
                onEvent = none,
                onProgress = { nav.navigate(Route.Progress) },
                onMic = { nav.navigate(Route.AskDidi) },
                onTab = { nav.tab(it) },
            )
        }
        composable<Route.Lesson> {
            val q = vm.lessonQuestion.collectAsStateWithLifecycle().value
            val pickedLesson by vm.lessonPicked.collectAsStateWithLifecycle()
            if (q == null) {
                LaunchedEffect(Unit) { vm.openLesson() }
                return@composable
            }
            LaunchedEffect(q.id) { voice.speak(L(q.prompt)) }
            // Right answer: show the reward for a moment, then the next question.
            LaunchedEffect(q.id, pickedLesson) { if (pickedLesson == q.correctIndex) { delay(1800); vm.openLesson() } }
            val topic = lesson.topics.first { it.id == q.topic }
            LessonQuestionScreen(
                LessonQuestionUiState(
                    header = "${L(lesson.title)} · ${L(topic.title)}",
                    picture = pictureFor(q.picture),
                    question = L(q.prompt),
                    options = q.options.map { L(it.text) },
                    picked = pickedLesson,
                    correct = q.correctIndex,
                    reward = 10,
                    queuedDoubts = 0,
                    source = stringResource(LearnR.string.lesson_authored),
                ),
                onPick = { i -> vm.pickLesson(i) },
                onAskDidi = { nav.navigate(Route.AskDidi) },
                onBack = back,
            )
        }
        composable<Route.AskDidi> {
            val ask by vm.ask.collectAsStateWithLifecycle()
            var draft by remember { mutableStateOf<String?>(null) }
            val hit = ask.hit
            val notKnown = stringResource(AskR.string.s5_not_known)
            LaunchedEffect(ask) {
                if (ask.searched) voice.speak(hit?.entry?.lines?.joinToString(" ") { L(it) } ?: notKnown)
            }
            AskDidiScreen(
                AskDidiUiState(
                    online = false,
                    question = ask.query,
                    voiceSeconds = ask.voiceSeconds,
                    answer = hit?.entry?.lines?.joinToString("\n") { L(it) },
                    source = hit?.let { stringResource(DsR.string.source_label, it.entry.sourceTitle) },
                    draft = draft,
                ),
                onListenAgain = { hit?.let { h -> voice.speak(h.entry.lines.joinToString(" ") { L(it) }) } },
                onDocuments = { nav.navigate(Route.Documents) },
                onMic = {
                    // Free speech: the whole question is transcribed, and the answer search finds
                    // the keywords even when the small model mishears a word or two.
                    val started = System.currentTimeMillis()
                    voiceInput.listen(null, askMicPermission) { text ->
                        text?.let { vm.askDidi(it, ((System.currentTimeMillis() - started) / 1000).toInt().coerceAtLeast(1)) }
                    }
                },
                onType = { draft = "" },
                onDraft = { draft = it },
                onSend = { draft?.takeIf { it.isNotBlank() }?.let { vm.askDidi(it) }; draft = null },
            )
        }
        composable<Route.Earn> {
            val results = if (vm.hasSchemeResults()) vm.results().filter { it.verdict != EngineVerdict.NotEligible } else emptyList()
            EarnScreen(
                EarnUiState(
                    name = name,
                    matchMeta = if (vm.hasSchemeResults()) stringResource(R.string.earn_meta, content.schemes.checkedOn) else stringResource(R.string.earn_meta_none),
                    opportunities = results.map { r ->
                        val applied = applications[r.scheme.id] == "applied"
                        Opportunity(
                            title = L(r.scheme.name),
                            meta = listOfNotNull(L(r.scheme.benefit), deadlineText(r.scheme.deadline)).joinToString(" · "),
                            pill = stringResource(
                                when {
                                    applied -> R.string.earn_applied
                                    r.verdict == EngineVerdict.Eligible -> EarnR.string.s6_eligible
                                    else -> R.string.earn_maybe
                                },
                            ),
                            pillTone = if (r.verdict == EngineVerdict.Eligible) Tone.Leaf else Tone.Gold,
                        )
                    },
                ),
                onOpportunity = { i -> vm.openGuide(results[i].scheme); nav.navigate(Route.Enrollment) },
                onSchemeCheck = { nav.navigate(if (vm.hasSchemeResults()) Route.Verdict else Route.SchemeCheck) },
                onDocuments = { nav.navigate(Route.Documents) },
                onFillForm = {
                    val first = results.firstOrNull()
                    if (first != null) { vm.openGuide(first.scheme); nav.navigate(Route.Enrollment) } else nav.navigate(Route.SchemeCheck)
                },
                onProgress = { nav.navigate(Route.Progress) },
                onTab = { nav.tab(it) },
            )
        }
        composable<Route.Progress> {
            val cleared by vm.trapsCleared.collectAsStateWithLifecycle()
            ProgressScreen(
                ProgressUiState(
                    name = name.ifBlank { stringResource(R.string.friend) },
                    streakDays = progress.streakDays,
                    streakMeta = stringResource(R.string.progress_meta, progress.diyas, progress.answered),
                    badges = listOf(
                        Badge(stringResource(R.string.badge_first_answer), RaahiIcons.Star, Tone.Gold, locked = progress.answered == 0),
                        Badge(stringResource(R.string.badge_trap_cleared), RaahiIcons.Trophy, Tone.Leaf, locked = cleared == 0),
                        Badge(stringResource(R.string.badge_scheme_check), RaahiIcons.Coin, Tone.Neel, locked = !vm.hasSchemeResults()),
                    ),
                    schoolRank = null,
                    verifiedSkills = 0,
                ),
                onPassport = none,
                onBack = back,
            )
        }
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
            var selected by rememberSaveable { mutableIntStateOf(-1) }
            val rightOnce = answers.filter { a -> lesson.questions.firstOrNull { it.id == a.questionId }?.correctIndex == a.picked }.map { it.questionId }.toSet()
            val openTrapTopics = traps.filter { it.clearedAt == null }.mapNotNull { t -> lesson.traps.firstOrNull { it.id == t.trapId }?.topic }.toSet()
            val done = lesson.topics.map { t -> t.id !in openTrapTopics && lesson.questions.filter { it.topic == t.id }.all { it.id in rightOnce } }
            val current = done.indexOfFirst { !it }
            // Summit first, base last.
            val nodes = listOf(MountainNode(stringResource(R.string.summit, L(lesson.title)), null, NodeState.Summit, RaahiIcons.Flag)) +
                lesson.topics.mapIndexed { i, t ->
                    val state = when {
                        done[i] -> NodeState.Done
                        i == current -> NodeState.Current
                        else -> NodeState.Locked
                    }
                    MountainNode(L(t.title), lesson.traps.firstOrNull { it.topic == t.id }?.let { L(it.name) }, state, pictureFor(t.picture))
                }.reversed()
            val sel = if (selected in nodes.indices) selected else nodes.indexOfFirst { it.state == NodeState.Current }.coerceAtLeast(0)
            val selTopic = lesson.topics.getOrNull(lesson.topics.size - sel)
            CareerMountainScreen(
                CareerMountainUiState(
                    career = "${L(lesson.subject)} · ${L(lesson.title)}",
                    careerMeta = stringResource(LearnR.string.lesson_authored),
                    nodes = nodes,
                    selected = sel,
                    detail = selTopic?.let { t ->
                        val trap = lesson.traps.firstOrNull { it.topic == t.id }
                        NodeDetail(
                            what = L(t.title),
                            why = trap?.let { stringResource(R.string.common_trap, L(it.misconception)) } ?: "",
                            time = stringResource(R.string.n_questions, lesson.questions.count { it.topic == t.id }),
                        )
                    },
                ),
                onNode = { selected = it },
                onStart = { vm.startTrapTest(); nav.navigate(Route.TrapTest) },
                onCourses = { nav.navigate(Route.Courses) },
                onTab = { nav.tab(it) },
            )
        }
        composable<Route.TrapTest> {
            val session = vm.trapTest.collectAsStateWithLifecycle().value
            if (session == null) {
                LaunchedEffect(Unit) { vm.startTrapTest() }
                return@composable
            }
            val q = session.questions.getOrNull(session.index) ?: return@composable
            LaunchedEffect(q.id) { voice.speak(L(q.prompt)) }
            val options = q.options.map { L(it.text) }
            val onPick: (Int) -> Unit = { i ->
                if (vm.pickTrapTest(i)) nav.navigate(Route.TrapSummary) { popUpTo(Route.TrapTest) { inclusive = true } }
            }
            TrapTestScreen(
                TrapTestUiState(
                    topic = L(lesson.topics.first { it.id == q.topic }.title),
                    index = session.index,
                    total = session.questions.size,
                    picture = pictureFor(q.picture),
                    question = L(q.prompt),
                    options = options,
                    picked = session.picked,
                ),
                onPick = onPick,
                onBack = back,
                onMic = { listenFor(options) { if (it >= 0) onPick(it) } },
                onType = none,
            )
        }
        composable<Route.TrapSummary> {
            val found = remember { vm.foundTraps() }
            LaunchedEffect(Unit) {
                found.firstOrNull()?.let { voice.speak(L(it.name)) }
            }
            TrapSummaryScreen(
                found.map { L(it.name) },
                onWatch = { i -> vm.focusTrap(found[i]); nav.navigate(Route.VideoSegment) },
                onRetry = { i -> vm.focusTrap(found[i]); nav.navigate(Route.TrapCleared) },
                onBack = back,
                explainOnly = true,
            )
        }
        composable<Route.VideoSegment> {
            val retry = vm.retry.collectAsStateWithLifecycle().value ?: return@composable
            var line by remember { mutableIntStateOf(0) }
            var speed by remember { mutableStateOf(1f) }
            val lines = retry.trap.explain.map { L(it) }
            LaunchedEffect(retry.trap.id) { voice.speakLines(lines, 0, speed) { line = it } }
            VideoSegmentScreen(
                VideoSegmentUiState(
                    trap = L(retry.trap.name),
                    segmentLabel = "",
                    segmentStart = 0f,
                    segmentEnd = 0f,
                    played = 0f,
                    speed = speed,
                    lines = lines.mapIndexed { i, t -> TranscriptLine("${i + 1}", t) },
                    currentLine = line,
                    noVideoNote = stringResource(LearnR.string.s12_trap_said, L(retry.trap.misconception)) + "\n" + stringResource(LearnR.string.s12_no_video),
                    source = stringResource(LearnR.string.s12_source, lesson.basedOn),
                ),
                onSpeed = { speed = it; voice.speakLines(lines, line, it) { l -> line = l } },
                onLine = { line = it; voice.speakLines(lines, it, speed) { l -> line = l } },
                onPlay = { voice.speakLines(lines, 0, speed) { l -> line = l } },
                onRetry = { voice.stop(); nav.navigate(Route.TrapCleared) },
                onBack = { voice.stop(); back() },
            )
        }
        composable<Route.TrapCleared> {
            // Retry question(s) for the focused trap; S13 shows once it is cleared.
            val retry = vm.retry.collectAsStateWithLifecycle().value ?: return@composable
            if (retry.clearedNow) {
                val box = traps.firstOrNull { it.trapId == retry.trap.id }?.box ?: 0
                val topicIndex = lesson.topics.indexOfFirst { it.id == retry.trap.topic }
                LaunchedEffect(Unit) { voice.speak(context.getString(LearnR.string.s13_title, L(retry.trap.name))) }
                TrapClearedScreen(
                    TrapClearedUiState(
                        trap = L(retry.trap.name),
                        badge = stringResource(R.string.badge_trap_cleared),
                        fromNode = L(lesson.topics[topicIndex].title),
                        toNode = lesson.topics.getOrNull(topicIndex + 1)?.let { L(it.title) } ?: stringResource(R.string.summit, L(lesson.title)),
                        recheckDays = org.raahi.core.engine.SpacedRepetition.daysFor(box),
                    ),
                    onContinue = { nav.navigate(Route.Mountain) { popUpTo(Route.Mountain) { inclusive = true } } },
                )
                return@composable
            }
            val q = retry.question ?: return@composable
            LaunchedEffect(q.id) { voice.speak(L(q.prompt)) }
            val options = q.options.map { L(it.text) }
            val onPick: (Int) -> Unit = { i ->
                vm.pickRetry(i) { _, correct -> if (!correct) nav.navigate(Route.VideoSegment) { popUpTo(Route.TrapCleared) { inclusive = true } } }
            }
            TrapTestScreen(
                TrapTestUiState(
                    topic = L(retry.trap.name),
                    index = 0,
                    total = 1,
                    picture = pictureFor(q.picture),
                    question = L(q.prompt),
                    options = options,
                    picked = retry.picked,
                ),
                onPick = onPick,
                onBack = back,
                onMic = { listenFor(options) { if (it >= 0) onPick(it) } },
                onType = none,
            )
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
        composable<Route.ExamPlan> { ExamPlanScreen(examPlanDesignReference(), onExam = {}, onItem = {}, onLiveClass = none, onBack = back) }
        composable<Route.PayAfterEarn> { PayAfterEarnScreen(sponsor = null, onListenAgain = none, onParentMessage = none, onBack = back) }
        composable<Route.Events> { EventsScreen(eventsDesignReference(), onRsvp = {}, onRemind = {}, onCheckIn = {}, onBack = back) }
        composable<Route.Documents> {
            val ids = vm.neededDocuments()
            DocumentsScreen(
                DocumentsUiState(
                    opportunity = stringResource(EarnR.string.s18_for_schemes),
                    items = ids.mapNotNull { id ->
                        docName(id)?.let { res -> DocumentItem(res, if (documents[id] == true) DocStatus.Have else DocStatus.Missing, null) }
                    },
                ),
                onToggle = { i -> ids.filter { docName(it) != null }.getOrNull(i)?.let(vm::toggleDocument) },
                onRemind = none,
                onBack = back,
            )
        }
        composable<Route.SchemeCheck> {
            val fact = remember(scheme) { vm.nextSchemeQuestion() }
            if (fact == null) {
                LaunchedEffect(scheme) { nav.navigate(Route.Verdict) { popUpTo(Route.SchemeCheck) { inclusive = true } } }
                return@composable
            }
            val options = fact.options.map { L(it.label) }
            LaunchedEffect(fact.id) { voice.speak(L(fact.question)) }
            val answer: (Int) -> Unit = { i -> vm.answerScheme(fact, i.takeIf { it in options.indices }) }
            SchemeCheckScreen(
                SchemeQuestionUiState(
                    index = scheme.asked.size,
                    maxQuestions = scheme.limit,
                    question = L(fact.question),
                    options = options,
                    picked = null,
                    onDeviceOnly = fact.onDeviceOnly,
                ),
                onPick = answer,
                onBack = back,
                onMic = { listenFor(options) { i -> answer(if (i == VoiceIntents.DONT_KNOW) options.size else i) } },
                onType = none,
            )
        }
        composable<Route.Verdict> {
            val results = remember(scheme) { vm.results() }
            val explained = results.map { it to VerdictExplainer.explain(it, language, vm.facts) }
            LaunchedEffect(results) {
                voice.speak(explained.filter { it.first.verdict != EngineVerdict.NotEligible }.joinToString(" ") { it.second.spoken })
            }
            val confirm = stringResource(R.string.check_confirm, "%s")
            VerdictScreen(
                explained.map { (r, e) ->
                    VerdictItem(
                        scheme = L(r.scheme.name),
                        verdict = when (r.verdict) {
                            EngineVerdict.Eligible -> Verdict.Eligible
                            EngineVerdict.Maybe -> Verdict.Maybe
                            EngineVerdict.NotEligible -> Verdict.NotEligible
                        },
                        reason = (listOf(e.reason) + if (r.verdict == EngineVerdict.NotEligible) emptyList() else e.toConfirm.map { confirm.format(it) }).joinToString("\n"),
                        source = listOfNotNull(
                            stringResource(R.string.checked_on, r.scheme.sourceTitle, content.schemes.checkedOn),
                            deadlineText(r.scheme.deadline),
                        ).joinToString(" · "),
                    )
                },
                onApply = { i -> vm.openGuide(results[i].scheme); nav.navigate(Route.Enrollment) },
                onBack = back,
                onRestart = { vm.restartSchemeCheck(); nav.navigate(Route.SchemeCheck) { popUpTo(Route.Verdict) { inclusive = true } } },
                onMore = if (vm.canAskMore()) {
                    { vm.askMore(); nav.navigate(Route.SchemeCheck) { popUpTo(Route.Verdict) { inclusive = true } } }
                } else {
                    null
                },
            )
        }
        composable<Route.Enrollment> {
            val guide = vm.guide.collectAsStateWithLifecycle().value ?: return@composable
            val (s, step) = guide
            val steps = s.steps.map { L(it.text) }
            LaunchedEffect(s.id, step) { voice.speak(steps[step]) }
            val factLabel = { factId: String ->
                scheme.answers[factId]?.let { opt -> content.facts.facts.first { it.id == factId }.options.firstOrNull { it.id == opt }?.let { L(it.label) } }
            }
            val draft = listOfNotNull(
                name.takeIf { it.isNotBlank() }?.let { DraftField(stringResource(R.string.draft_name), it) },
                factLabel("stage")?.let { DraftField(stringResource(R.string.draft_class), it) },
                factLabel("state")?.let { DraftField(stringResource(R.string.draft_state), it) },
            )
            val detail = listOfNotNull(
                s.deadline?.let { stringResource(R.string.deadline, it) },
                s.helpline?.let { stringResource(R.string.helpline, it) },
                stringResource(DsR.string.source_label, "${s.sourceTitle} (${s.steps[step].where})"),
            ).joinToString("\n")
            EnrollmentGuideScreen(
                EnrollmentUiState(
                    scheme = L(s.name),
                    steps = steps,
                    current = step,
                    currentDetail = detail,
                    portal = s.applyUrl ?: stringResource(R.string.portal_not_verified),
                    draft = draft,
                ),
                onStep = { vm.guideStep(it) },
                onDone = { vm.guideStep(step + 1) },
                onCopy = { copy(context, it.label, it.value) },
                onOpenForm = { s.applyUrl?.let { url -> context.startActivity(Intent(Intent.ACTION_VIEW, Uri.parse(url))) } },
                onWithTeacher = none,
                onApplied = { vm.markApplied(); nav.navigate(Route.Earn) { popUpTo(Route.Earn) { inclusive = true } } },
                onListenAgain = { voice.speak(steps[step]) },
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
                tagged = listOf(TaggedQuestion(stringResource(TeacherR.string.designref_trap_units), stringResource(TeacherR.string.designref_segment))),
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
        composable<Route.ScreenIndex> { ScreenIndex(onOpen = { nav.navigate(it) }, onMakeChecksDue = { vm.debugMakeChecksDue(); nav.navigate(Route.Home) }) }
    }
}

/** "13 days left" from a scheme's official last date (ISO yyyy-mm-dd), or null if none is published. */
@Composable
private fun deadlineText(deadline: String?): String? {
    val date = deadline?.let { runCatching { java.time.LocalDate.parse(it) }.getOrNull() } ?: return null
    val days = java.time.temporal.ChronoUnit.DAYS.between(java.time.LocalDate.now(), date).toInt()
    return if (days < 0) stringResource(EarnR.string.s6_deadline_passed) else stringResource(EarnR.string.s6_days_left, days)
}

private fun docName(id: String): Int? = when (id) {
    "income_certificate" -> EarnR.string.doc_income
    "caste_certificate" -> EarnR.string.doc_caste
    "domicile_certificate" -> EarnR.string.doc_domicile
    "marksheet_last" -> EarnR.string.doc_marksheet
    "marksheet_10" -> EarnR.string.doc_marksheet_10
    "marksheet_12" -> EarnR.string.doc_marksheet_12
    "bank_account" -> EarnR.string.doc_bank
    "admission_receipt" -> EarnR.string.doc_admission
    "samagra_id" -> EarnR.string.doc_samagra
    "fee_receipt" -> EarnR.string.doc_fee_receipt
    "bonafide_certificate" -> EarnR.string.doc_bonafide
    else -> null
}

@Composable
private fun ScreenIndex(onOpen: (Route) -> Unit, onMakeChecksDue: () -> Unit) {
    LazyColumn(Modifier.fillMaxSize().statusBarsPadding().padding(horizontal = 16.dp)) {
        item { Text(stringResource(R.string.screen_index), style = RaahiTheme.typography.title, modifier = Modifier.padding(vertical = 12.dp)) }
        item {
            Text(
                stringResource(R.string.debug_make_due),
                style = RaahiTheme.typography.tile,
                color = RaahiColors.Clay,
                modifier = Modifier.fillMaxWidth().heightIn(min = 56.dp).clickable(onClick = onMakeChecksDue).padding(vertical = 14.dp),
            )
        }
        items(AllScreens) { (label, route) ->
            Text(
                label,
                style = RaahiTheme.typography.tile,
                modifier = Modifier.fillMaxWidth().heightIn(min = 56.dp).clickable { onOpen(route) }.padding(vertical = 14.dp),
            )
        }
    }
}
