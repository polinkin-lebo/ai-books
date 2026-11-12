package dev.aibooks.reader;

import static dev.aibooks.reader.BookUi.*;
import static dev.aibooks.reader.ReadingFeed.*;
import static dev.aibooks.reader.ReadingFeed.Section;

import android.app.Activity;
import android.app.Dialog;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.media.AudioManager;
import android.os.Bundle;
import android.view.*;
import android.view.inputmethod.InputMethodManager;
import android.widget.*;
import java.util.*;
import org.json.JSONArray;
import org.json.JSONObject;

public final class MainActivity extends Activity {
    int fontSize, density, rating;
    private int theme;
    String review;
    boolean discovery, reading, dog, hidden, densityOpen, editing, scrubbing;
    private boolean moving, following, increasing = true, imeSeen, ready;
    private BookUi u;
    private FrameLayout root;
    private ScrollView scroll, catalog;
    private ReadingFeed feed;
    private ReaderControls controls;
    private Narration narration;
    private SharedPreferences prefs;
    private Dialog dialog;
    private int[] annaTimes, dogTimes;
    private int pausedAt = -1;
    private boolean pausedDog;
    private int layoutRevision;
    private final List<Answer> answers = new ArrayList<>();

    private static final class Answer {
        final Section after;
        final String question, text;

        Answer(Section after, String question, String text) {
            this.after = after;
            this.question = question;
            this.text = text;
        }
    }

    private static final class Bookmark {
        final Section section;
        final int offset;

        Bookmark(Section section, int offset) {
            this.section = section;
            this.offset = offset;
        }
    }

    @Override
    public void onCreate(Bundle state) {
        super.onCreate(state);
        prefs = getSharedPreferences("reader", MODE_PRIVATE);
        fontSize = prefs.getInt("size", 21);
        theme = prefs.getInt("theme", 0);
        density = prefs.getInt("density", 100);
        rating = prefs.getInt("rating", 0);
        review = prefs.getString("review", "");
        discovery = prefs.getBoolean("discovery", false);
        dog = prefs.getBoolean("dog", false);
        pausedAt = prefs.getInt("audioMs", -1);
        pausedDog = prefs.getBoolean("audioDog", false);
        setVolumeControlStream(AudioManager.STREAM_MUSIC);
        narration = new Narration(this);
        root = new FrameLayout(this);
        root.setFocusableInTouchMode(true);
        setContentView(root);
        root.post(
                () -> {
                    u = new BookUi(this, root.getWidth());
                    JSONObject timings = u.json("timing.json");
                    annaTimes = times(timings.optJSONArray("anna"));
                    dogTimes = times(timings.optJSONArray("chekhov"));
                    restoreAnswers();
                    Bookmark mark =
                            new Bookmark(
                                    Section.valueOf(prefs.getString("section", "HOME")),
                                    prefs.getInt("offset", 0));
                    build(mark, prefs.getInt("catalogY", 0));
                });
        root.getViewTreeObserver()
                .addOnGlobalLayoutListener(
                        () -> {
                            if (!editing) return;
                            android.graphics.Rect rect = new android.graphics.Rect();
                            root.getWindowVisibleDisplayFrame(rect);
                            int bottom = getWindow().getDecorView().getHeight() - rect.bottom;
                            boolean shown =
                                    bottom > getResources().getDisplayMetrics().density * 110;
                            if (shown) imeSeen = true;
                            else if (imeSeen) endCompose();
                        });
    }

    private int[] times(JSONArray list) {
        int[] out = new int[list.length()];
        for (int i = 0; i < out.length; i++) out[i] = list.optInt(i);
        return out;
    }

    private void restoreAnswers() {
        try {
            JSONArray arr = new JSONArray(prefs.getString("answers", "[]"));
            for (int i = 0; i < arr.length(); i++) {
                JSONArray a = arr.getJSONArray(i);
                answers.add(
                        new Answer(
                                Section.valueOf(a.getString(0)), a.getString(1), a.getString(2)));
            }
        } catch (Exception ignored) {
            answers.clear();
        }
    }

    private void build(Bookmark mark, int catalogY) {
        int revision = ++layoutRevision;
        ready = false;
        moving = true;
        u.theme(theme);
        root.removeAllViews();
        root.setBackgroundColor(u.paper);
        getWindow().setStatusBarColor(u.paper);
        getWindow().setNavigationBarColor(u.paper);
        getWindow()
                .getDecorView()
                .setSystemUiVisibility(
                        theme == 2
                                ? 0
                                : View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR
                                        | View.SYSTEM_UI_FLAG_LIGHT_NAVIGATION_BAR);
        feed = new ReadingFeed(this, u);
        // Even a short final chapter must be able to reach the header at the smallest font size.
        feed.sections
                .get(Section.CHEKHOV)
                .setMinimumHeight(Math.max(0, root.getHeight() - anchorInset() - u.px(80)));
        feed.density(density);
        for (Answer a : answers) feed.reply(a.after, a.question, a.text);
        scroll =
                new ScrollView(this) {
                    @Override
                    public boolean dispatchTouchEvent(MotionEvent e) {
                        if (e.getActionMasked() == MotionEvent.ACTION_DOWN) {
                            following = false;
                            if (editing) endCompose();
                        }
                        return super.dispatchTouchEvent(e);
                    }
                };
        scroll.setVerticalScrollBarEnabled(false);
        scroll.setFillViewport(false);
        scroll.setOverScrollMode(View.OVER_SCROLL_NEVER);
        // Leave enough space for the final paragraph to pass under the fixed reading controls.
        u.pad(feed.content, 0, 0, 0, 120);
        scroll.addView(feed.content);
        root.addView(scroll, new FrameLayout.LayoutParams(FILL, FILL));
        feed.content.setOnClickListener(
                v -> {
                    if (editing) endCompose();
                    else if (reading) {
                        hidden = !hidden;
                        densityOpen = false;
                        controls.render();
                    }
                });
        catalog = new ScrollView(this);
        catalog.setVerticalScrollBarEnabled(false);
        catalog.setBackgroundColor(u.paper);
        root.addView(catalog, new FrameLayout.LayoutParams(FILL, FILL));
        catalog.setVisibility(discovery ? View.VISIBLE : View.GONE);
        if (discovery) catalog.addView(feed.discovery());
        controls = new ReaderControls(this, u);
        root.addView(controls.layer, new FrameLayout.LayoutParams(FILL, FILL));
        scroll.setOnScrollChangeListener(
                (View v, int x, int y, int oldX, int oldY) -> {
                    if (!ready || discovery) return;
                    if (!moving && !scrubbing) {
                        pausedAt = -1;
                        if (reading && !editing && Math.abs(y - oldY) > u.px(3)) {
                            hidden = y > oldY;
                            densityOpen = false;
                        }
                    }
                    refresh();
                });
        afterLayout(
                () -> {
                    if (revision != layoutRevision) return;
                    restore(mark);
                    catalog.scrollTo(0, catalogY);
                    moving = false;
                    ready = true;
                    refresh();
                });
    }

    private void afterLayout(Runnable run) {
        root.getViewTreeObserver()
                .addOnPreDrawListener(
                        new ViewTreeObserver.OnPreDrawListener() {
                            public boolean onPreDraw() {
                                root.getViewTreeObserver().removeOnPreDrawListener(this);
                                run.run();
                                return true;
                            }
                        });
        root.invalidate();
    }

    private int anchorInset() {
        return u.px(65);
    }

    private Bookmark bookmark() {
        int y = scroll.getScrollY();
        Section k = feed.at(y + anchorInset());
        return new Bookmark(k, y - feed.sections.get(k).getTop());
    }

    private Section visibleFrom(Section from) {
        for (Section k : Section.values())
            if (k.ordinal() >= from.ordinal()
                    && feed.sections.get(k).getVisibility() == View.VISIBLE) return k;
        return Section.CHEKHOV;
    }

    private void restore(Bookmark mark) {
        Section visible = visibleFrom(mark.section);
        int offset = visible == mark.section ? mark.offset : -anchorInset();
        moveTo(feed.sections.get(visible).getTop() + offset);
    }

    private void moveTo(int y) {
        boolean before = moving;
        moving = true;
        scroll.scrollTo(0, Math.max(0, y));
        moving = before;
    }

    private void refresh() {
        if (!discovery) {
            int y = scroll.getScrollY(), anchor = y + anchorInset() + 1;
            // The title switches at the start of each book, including its lead-in.
            boolean next = anchor >= feed.sections.get(Section.CHEKHOV).getTop();
            if (playing() && next != narration.dog) {
                narration.close();
                pausedAt = -1;
            }
            dog = next;
            reading =
                    anchor >= feed.sections.get(Section.INTRO).getTop()
                            || y + scroll.getHeight() - u.px(40)
                                    > paragraphTop(feed.annaLines.get(0));
            if (!reading) {
                hidden = false;
                densityOpen = false;
            }
        }
        controls.render();
    }

    String title() {
        return dog ? DOG : ANNA;
    }

    String author() {
        return dog ? CHEKHOV_AUTHOR : TOLSTOY;
    }

    void go(Section section) {
        endCompose();
        closeSheet();
        if (playing()) pausePlayback();
        discovery = false;
        catalog.setVisibility(View.GONE);
        densityOpen = false;
        hidden = false;
        pausedAt = -1;
        // An explicit question can reveal its answer even at a low insert density.
        feed.sections.get(section).setVisibility(View.VISIBLE);
        afterLayout(
                () -> {
                    moveTo(
                            feed.sections.get(section).getTop()
                                    - (section == Section.HOME ? 0 : anchorInset()));
                    refresh();
                    save();
                });
    }

    void collapse() {
        if (playing()) pausePlayback();
        endCompose();
        discovery = true;
        densityOpen = false;
        hidden = false;
        if (catalog.getChildCount() == 0) catalog.addView(feed.discovery());
        catalog.setVisibility(View.VISIBLE);
        catalog.scrollTo(0, 0);
        controls.render();
        save();
    }

    void resumeReading() {
        discovery = false;
        catalog.setVisibility(View.GONE);
        hidden = false;
        refresh();
        save();
    }

    void changeDensity(int value) {
        Bookmark mark = bookmark();
        if (value != density) increasing = value > density;
        density = value;
        feed.density(value);
        controls.updateValues();
        afterLayout(
                () -> {
                    restore(mark);
                    refresh();
                });
    }

    String densityCaption() {
        if (density == 0) return "Только чтение без подсказок";
        if (density == 100) return "Максимальное количество подсказок";
        return increasing
                ? "Больше вставок, подсказок и рассуждений"
                : "Меньше вставок, подсказок и рассуждений";
    }

    void compose(String prefill) {
        hidden = false;
        densityOpen = false;
        editing = true;
        imeSeen = false;
        controls.render();
        EditText field = controls.question;
        field.setText(prefill);
        field.setSelection(field.length());
        field.requestFocus();
        field.post(
                () -> {
                    if (editing)
                        ((InputMethodManager) getSystemService(INPUT_METHOD_SERVICE))
                                .showSoftInput(field, InputMethodManager.SHOW_IMPLICIT);
                });
    }

    private void endCompose() {
        if (!editing) return;
        editing = false;
        imeSeen = false;
        ((InputMethodManager) getSystemService(INPUT_METHOD_SERVICE))
                .hideSoftInputFromWindow(root.getWindowToken(), 0);
        root.requestFocus();
        controls.render();
    }

    void submit(String q) {
        q = q.trim();
        if (q.isEmpty()) {
            endCompose();
            return;
        }
        endCompose();
        if (discovery) resumeReading();
        String lower = q.toLowerCase(new Locale("ru"));
        if (lower.matches(".*петриц.*")) {
            go(Section.CHARACTER);
            return;
        }
        if (lower.matches(".*(другим|читател|отзыв).*")) {
            go(Section.REVIEWS);
            return;
        }
        if (lower.matches(".*(экрани|театр|фильм|посмотр|относительно книги).*")) {
            go(Section.CINEMA);
            return;
        }
        if (lower.contains("дама с собачкой")) {
            go(Section.CHEKHOV);
            return;
        }
        if (lower.matches(".*(коротк|любов|ново|истори[ию]).*") && !lower.contains("роман")) {
            go(Section.RECOMMEND);
            return;
        }
        String response =
                dog
                        ? "Здесь Чехов знакомит читателя с Гуровым и загадочной дамой. Другие отдыхающие знают о ней только, что она гуляет с белым шпицем."
                        : "В этом фрагменте Петрицкий рассказывает Вронскому о своей петербургской жизни. Его долги и любовные истории создают фон возвращения Вронского.";
        Section anchor = feed.at(scroll.getScrollY() + anchorInset());
        Answer answer = new Answer(anchor, q, response);
        answers.add(answer);
        View card = feed.reply(anchor, q, response);
        afterLayout(
                () -> {
                    moveTo(card.getTop() - anchorInset());
                    hidden = false;
                    refresh();
                    save();
                });
    }

    void continueAfter(View answer) {
        int index = feed.content.indexOfChild(answer) + 1;
        while (index < feed.content.getChildCount()
                && feed.content.getChildAt(index).getVisibility() == View.GONE) index++;
        if (index < feed.content.getChildCount()) {
            moveTo(feed.content.getChildAt(index).getTop() - anchorInset());
        } else {
            List<TextView> bookLines = lines();
            moveTo(paragraphTop(bookLines.get(bookLines.size() - 1)) - anchorInset());
        }
        hidden = true;
        refresh();
    }

    private int paragraphTop(TextView line) {
        return ((View) line.getParent()).getTop() + line.getTop();
    }

    private List<TextView> lines() {
        return dog ? feed.chekhovLines : feed.annaLines;
    }

    private int[] timeline() {
        return dog ? dogTimes : annaTimes;
    }

    private int duration() {
        int[] t = timeline();
        return t[t.length - 1];
    }

    boolean showProgress() {
        if (discovery || !reading) return false;
        return dog
                || scroll.getScrollY() + anchorInset() < feed.sections.get(Section.LAST).getBottom()
                || scrubbing;
    }

    private int finalChapterStart() {
        return feed.sections.get(Section.CHEKHOV).getTop() - anchorInset();
    }

    private int finalChapterTravel() {
        TextView last = feed.chekhovLines.get(feed.chekhovLines.size() - 1);
        int end =
                Math.min(
                        feed.content.getHeight() - scroll.getHeight(),
                        paragraphTop(last) + last.getHeight() - anchorInset());
        return Math.max(1, end - finalChapterStart());
    }

    private int scrollTime() {
        if (dog) {
            float fraction =
                    (scroll.getScrollY() - finalChapterStart()) / (float) finalChapterTravel();
            return Math.round(duration() * Math.max(0f, Math.min(1f, fraction)));
        }
        int y = scroll.getScrollY() + anchorInset();
        List<TextView> lines = lines();
        int[] ts = timeline();
        for (int i = 0; i < lines.size(); i++) {
            TextView l = lines.get(i);
            int top = paragraphTop(l);
            if (y <= top) return ts[i];
            if (y < top + l.getHeight())
                return ts[i] + Math.round((ts[i + 1] - ts[i]) * (y - top) / (float) l.getHeight());
        }
        return duration();
    }

    private int position() {
        if (playing()) return narration.position();
        return pausedAt >= 0 && pausedDog == dog ? pausedAt : scrollTime();
    }

    float fraction() {
        return Math.min(1f, position() / (float) duration());
    }

    private void revealTime(int ms) {
        if (dog) {
            moveTo(
                    finalChapterStart()
                            + Math.round(finalChapterTravel() * (ms / (float) duration())));
            return;
        }
        List<TextView> lines = lines();
        int[] ts = timeline();
        int i = 0;
        while (i < lines.size() - 1 && ts[i + 1] <= ms) i++;
        TextView l = lines.get(i);
        float within = Math.min(1f, Math.max(0f, (ms - ts[i]) / (float) (ts[i + 1] - ts[i])));
        moveTo(paragraphTop(l) + Math.round(within * l.getHeight()) - anchorInset());
    }

    boolean playing() {
        return narration != null && narration.playing();
    }

    void play() {
        int pos = position();
        if (pos >= duration() - 250) pos = 0;
        following = true;
        hidden = false;
        densityOpen = false;
        narration.start(dog, pos);
        if (playing()) revealTime(pos);
        controls.render();
    }

    void pausePlayback() {
        if (playing()) {
            pausedAt = narration.position();
            pausedDog = narration.dog;
        }
        narration.close();
        following = false;
        hidden = false;
        if (controls != null) {
            controls.render();
            save();
        }
    }

    void playbackTick(int ms) {
        if (!ready) return;
        if (following && !scrubbing && !editing && !discovery && !densityOpen && dialog == null)
            revealTime(ms);
        controls.updateValues();
    }

    void audioEnded(boolean completed) {
        following = false;
        hidden = false;
        if (completed) {
            pausedAt = duration();
            pausedDog = dog;
            revealTime(pausedAt);
        }
        controls.render();
        save();
    }

    void skip(int delta) {
        seekTime(Math.max(0, Math.min(duration(), position() + delta)));
        hidden = false;
        controls.render();
    }

    void seekFraction(float v) {
        seekTime(Math.round(duration() * v));
    }

    private void seekTime(int ms) {
        if (playing()) {
            narration.seek(ms);
            following = true;
        }
        pausedAt = ms;
        pausedDog = dog;
        revealTime(ms);
        controls.updateValues();
    }

    private LinearLayout sheet(String title) {
        closeSheet();
        dialog = new Dialog(this);
        LinearLayout box = u.column();
        u.surface(box, u.paper, 22);
        u.pad(box, 20, 10, 20, 24);
        View handle = new View(this);
        u.surface(handle, u.inset, 3);
        LinearLayout.LayoutParams grip = new LinearLayout.LayoutParams(u.px(36), u.px(4));
        grip.gravity = Gravity.CENTER_HORIZONTAL;
        box.addView(handle, grip);
        u.gap(box, 16);
        LinearLayout head = u.row();
        u.put(box, head, FILL, WRAP);
        u.weight(head, u.text(title, TITLE, 26, 30, u.ink), WRAP);
        TextView close = u.text("×", SANS, 23, 24, u.muted);
        close.setGravity(Gravity.CENTER);
        u.action(close, "Закрыть", this::closeSheet);
        u.put(head, close, 40, 40);
        u.gap(box, 12);
        ScrollView surface = new ScrollView(this);
        surface.addView(box);
        dialog.setContentView(surface);
        Window w = dialog.getWindow();
        w.setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        w.setGravity(Gravity.BOTTOM);
        w.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE);
        w.setLayout(FILL, WRAP);
        dialog.setOnDismissListener(
                d -> {
                    if (dialog == d) dialog = null;
                });
        dialog.show();
        w.setLayout(FILL, WRAP);
        return box;
    }

    private void closeSheet() {
        if (dialog != null) {
            Dialog current = dialog;
            dialog = null;
            current.dismiss();
        }
    }

    void info(String title, String body) {
        LinearLayout box = sheet(title);
        u.label(box, body, SANS, 16, 23, u.ink, false);
    }

    void menu() {
        LinearLayout box = sheet(title());
        button(box, "Мои главы", () -> go(dog ? Section.CHEKHOV : Section.BREAK));
        button(
                box,
                "К концу книги",
                () -> {
                    if (dog) {
                        closeSheet();
                        seekTime(duration());
                        hidden = false;
                        refresh();
                    } else go(Section.FINISH);
                });
        button(box, "Начать демонстрацию заново", this::reset);
    }

    private void button(LinearLayout box, String name, Runnable run) {
        u.put(box, u.pill(name, false, false, run), WRAP, WRAP);
        u.gap(box, 8);
    }

    void settings() {
        LinearLayout box = sheet("Настройки чтения");
        u.label(box, "Размер текста", MEDIUM, 16, 20, u.ink, false);
        u.gap(box, 8);
        LinearLayout sizes = u.row();
        u.put(box, sizes, FILL, WRAP);
        u.put(
                sizes,
                u.pill("А−", false, false, () -> appearance(theme, Math.max(17, fontSize - 1))),
                WRAP,
                40);
        u.gap(sizes, 14);
        u.put(sizes, u.text(String.valueOf(fontSize), MEDIUM, 18, 23, u.ink), WRAP, WRAP);
        u.gap(sizes, 14);
        u.put(
                sizes,
                u.pill("А+", false, false, () -> appearance(theme, Math.min(28, fontSize + 1))),
                WRAP,
                40);
        u.gap(box, 20);
        u.label(box, "Цвет страницы", MEDIUM, 16, 20, u.ink, false);
        u.gap(box, 8);
        LinearLayout colors = u.row();
        u.put(box, colors, FILL, WRAP);
        String[] names = {"Белый", "Тёплый", "Тёмный"};
        for (int i = 0; i < 3; i++) {
            int c = i;
            if (i > 0) u.gap(colors, 6);
            u.put(
                    colors,
                    u.pill(names[i], theme == i, false, () -> appearance(c, fontSize)),
                    WRAP,
                    36);
        }
        u.gap(box, 20);
        u.label(box, "Озвучивание", MEDIUM, 16, 20, u.ink, false);
        u.gap(box, 8);
        button(
                box,
                playing() ? "Пауза" : "Слушать фоново",
                () -> {
                    closeSheet();
                    if (playing()) pausePlayback();
                    else play();
                });
    }

    private void appearance(int choice, int size) {
        Bookmark mark = bookmark();
        int y = catalog.getScrollY();
        theme = choice;
        fontSize = size;
        closeSheet();
        build(mark, y);
        afterLayout(
                () -> {
                    settings();
                    save();
                });
    }

    void writeReview() {
        LinearLayout box = sheet("Как тебе эта книга?");
        EditText text = new EditText(this);
        text.setHint("Какая мысль осталась с вами?");
        text.setImeHintLocales(android.os.LocaleList.forLanguageTags("ru"));
        text.setText(review);
        text.setTextColor(u.ink);
        text.setHintTextColor(u.muted);
        text.setTypeface(u.font(SANS));
        text.setMinLines(2);
        text.setMaxLines(5);
        text.setGravity(Gravity.TOP);
        u.put(box, text, FILL, WRAP);
        u.gap(box, 12);
        u.put(
                box,
                u.pill(
                        "Сохранить в прототипе",
                        true,
                        false,
                        () -> {
                            String value = text.getText().toString().trim();
                            if (value.isEmpty()) {
                                text.requestFocus();
                                return;
                            }
                            review = value;
                            feed.updateReview();
                            closeSheet();
                            save();
                            notice("Отзыв сохранён");
                        }),
                WRAP,
                40);
    }

    private void reset() {
        closeSheet();
        endCompose();
        narration.close();
        discovery = hidden = densityOpen = dog = reading = false;
        pausedAt = -1;
        fontSize = 21;
        density = 100;
        theme = rating = 0;
        review = "";
        answers.clear();
        prefs.edit().clear().apply();
        build(new Bookmark(Section.HOME, 0), 0);
    }

    void notice(String text) {
        Toast.makeText(this, text, Toast.LENGTH_SHORT).show();
    }

    void save() {
        if (!ready) return;
        Bookmark b = bookmark();
        JSONArray saved = new JSONArray();
        for (Answer a : answers)
            saved.put(new JSONArray().put(a.after.name()).put(a.question).put(a.text));
        prefs.edit()
                .putInt("size", fontSize)
                .putInt("theme", theme)
                .putInt("density", density)
                .putInt("rating", rating)
                .putString("review", review)
                .putString("answers", saved.toString())
                .putString("section", b.section.name())
                .putInt("offset", b.offset)
                .putBoolean("discovery", discovery)
                .putBoolean("dog", dog)
                .putInt("catalogY", catalog.getScrollY())
                .putInt("audioMs", playing() ? narration.position() : pausedAt)
                .putBoolean("audioDog", playing() ? narration.dog : pausedDog)
                .apply();
    }

    @Override
    public void onBackPressed() {
        if (!ready) {
            super.onBackPressed();
            return;
        }
        if (editing) {
            endCompose();
            return;
        }
        if (densityOpen) {
            densityOpen = false;
            controls.render();
            return;
        }
        if (discovery) {
            resumeReading();
            return;
        }
        if (reading) {
            collapse();
            return;
        }
        super.onBackPressed();
    }

    @Override
    protected void onPause() {
        super.onPause();
        save();
    }

    @Override
    protected void onStop() {
        if (playing()) pausePlayback();
        super.onStop();
    }

    @Override
    protected void onDestroy() {
        narration.close();
        super.onDestroy();
    }
}
