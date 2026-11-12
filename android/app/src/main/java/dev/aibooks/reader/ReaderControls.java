package dev.aibooks.reader;

import static dev.aibooks.reader.BookUi.*;
import static dev.aibooks.reader.ReadingFeed.*;

import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.drawable.GradientDrawable;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.*;

final class ReaderControls {
    private final MainActivity a;
    private final BookUi u;
    final FrameLayout layer;
    EditText question;
    private Track progress;
    private TextView caption;
    private FrameLayout rewind;
    private String state = "";
    private static final int BOTTOM = Gravity.BOTTOM | Gravity.CENTER_HORIZONTAL;

    ReaderControls(MainActivity a, BookUi u) {
        this.a = a;
        this.u = u;
        layer = new FrameLayout(a);
        layer.setClipChildren(false);
        layer.setClipToPadding(false);
    }

    void invalidate() {
        state = "";
        render();
    }

    void render() {
        String next =
                a.reading
                        + " "
                        + a.dog
                        + " "
                        + a.discovery
                        + " "
                        + a.hidden
                        + " "
                        + a.densityOpen
                        + " "
                        + a.editing
                        + " "
                        + a.playing()
                        + " "
                        + a.showProgress();
        if (next.equals(state)) {
            updateValues();
            return;
        }
        state = next;
        layer.removeAllViews();
        progress = null;
        caption = null;
        rewind = null;
        question = null;
        boolean reading = a.reading && !a.discovery;
        if (reading && !a.hidden && !a.editing) header();
        if (a.editing) {
            editor();
            return;
        }
        if (!reading) {
            fade(a.discovery ? 160 : 78);
            if (a.discovery) mini();
            LinearLayout query = u.row();
            query.setGravity(Gravity.CENTER);
            u.glass(query, 24);
            u.put(query, u.image("design/lumen-head.png", 20, 20, 0), 20, 20);
            u.gap(query, 9);
            u.put(query, u.text("Что будем читать?", SANS, 14, 17, u.muted), WRAP, WRAP);
            u.action(query, "Что будем читать?", () -> a.compose(""));
            u.place(layer, query, 220, 40, BOTTOM, 0, 14);
            return;
        }
        if (!a.hidden || a.densityOpen) fade(133);
        if (!a.hidden && !a.densityOpen && a.showProgress()) {
            progress = new Track(false);
            u.place(layer, progress, FILL, 24, BOTTOM, 16, 65);
        }
        LinearLayout row = u.row();
        row.setClipChildren(false);
        row.setClipToPadding(false);
        u.place(layer, row, FILL, 40, BOTTOM, 16, 25);
        FrameLayout spark =
                circle(
                        36,
                        "Количество ИИ-вставок",
                        () -> {
                            a.densityOpen = !a.densityOpen;
                            render();
                        });
        u.put(row, spark, 36, 36);
        u.place(spark, u.image("design/lumen-head.png", 24, 24, 0), 24, 24, Gravity.CENTER, 0, 0);
        u.gap(row, 8);
        if (a.densityOpen) {
            Track slider = new Track(true);
            slider.setProgress(a.density);
            u.glass(slider, 22);
            u.weight(row, slider, 36);
        } else if (!a.hidden) {
            TextView ask = u.text("Спросить про книгу и не только", SANS, 14, 17, u.muted);
            ask.setGravity(Gravity.CENTER);
            ask.setSingleLine();
            ask.setEllipsize(android.text.TextUtils.TruncateAt.END);
            u.pad(ask, 12, 0, 12, 0);
            u.glass(ask, 22);
            u.action(ask, null, () -> a.compose(""));
            u.weight(row, ask, 36);
            u.gap(row, 8);
        } else u.weight(row, new Space(a), 1);
        if (!a.densityOpen && (a.playing() || !a.hidden)) {
            if (a.playing()) {
                rewind =
                        mediaButton(
                                row,
                                R.drawable.ic_rewind,
                                "Назад 15 секунд",
                                "15",
                                () -> a.skip(-15000));
                u.gap(row, 8);
                mediaButton(row, R.drawable.ic_pause, "Пауза", "", a::pausePlayback);
                u.gap(row, 8);
                mediaButton(
                        row, R.drawable.ic_forward, "Вперёд 30 секунд", "30", () -> a.skip(30000));
            } else mediaButton(row, R.drawable.ic_play, "Слушать книгу", "", a::play);
        }
        if (!a.hidden || a.densityOpen) {
            caption = u.text("", SANS, 11, 15, u.muted);
            caption.setGravity(Gravity.CENTER);
            u.place(layer, caption, FILL, 18, BOTTOM, 8, 3);
        }
        updateValues();
    }

    void updateValues() {
        if (caption != null)
            caption.setText(
                    a.densityOpen
                            ? a.densityCaption()
                            : "В контексте: «"
                                    + a.title()
                                    + "» · глава "
                                    + (a.dog ? "I" : "XXXIV"));
        if (progress != null && !a.scrubbing)
            progress.setProgress(Math.round(10000 * a.fraction()));
        if (rewind != null) {
            boolean enabled = a.fraction() > .001f;
            rewind.setEnabled(enabled);
            rewind.setAlpha(enabled ? 1f : .45f);
        }
    }

    private FrameLayout circle(int size, String desc, Runnable click) {
        FrameLayout button = new FrameLayout(a);
        u.glass(button, size / 2);
        u.action(button, desc, click);
        return button;
    }

    private FrameLayout mediaButton(
            LinearLayout row, int icon, String label, String number, Runnable click) {
        FrameLayout v = circle(36, label, click);
        u.put(row, v, 36, 36);
        ImageView img = icon(icon);
        u.place(v, img, 20, 20, Gravity.CENTER, 0, 0);
        if (!number.isEmpty()) {
            TextView text = u.text(number, MEDIUM, 8, 10, u.ink);
            u.place(
                    v,
                    text,
                    WRAP,
                    WRAP,
                    Gravity.BOTTOM | (number.equals("15") ? Gravity.LEFT : Gravity.RIGHT),
                    7,
                    7);
        }
        return v;
    }

    private ImageView icon(int res) {
        ImageView v = new ImageView(a);
        v.setImageResource(res);
        v.setColorFilter(u.ink);
        return v;
    }

    private void headerIcon(LinearLayout row, int res, String label, Runnable click) {
        FrameLayout b = new FrameLayout(a);
        u.surface(b, u.inset, 18);
        u.action(b, label, click);
        u.place(b, icon(res), 20, 20, Gravity.CENTER, 0, 0);
        u.put(row, b, 32, 32);
    }

    private void header() {
        View mask = new View(a);
        mask.setBackgroundColor(u.paper);
        u.place(layer, mask, FILL, 24, Gravity.TOP, 0, 0);
        LinearLayout bar = u.row();
        u.glass(bar, 16);
        u.pad(bar, 12, 0, 12, 0);
        u.place(layer, bar, FILL, 50, Gravity.TOP, 16, 4);
        headerIcon(bar, R.drawable.ic_collapse, "Свернуть чтение", a::collapse);
        u.gap(bar, 12);
        LinearLayout title = u.column();
        u.weight(bar, title, WRAP);
        u.label(title, a.title(), MEDIUM, 14, 17, u.ink, false);
        u.gap(title, 2);
        u.label(title, a.author(), SANS, 12, 14, u.muted, false);
        u.gap(bar, 8);
        headerIcon(bar, R.drawable.ic_settings, "Настройки чтения", a::settings);
        u.gap(bar, 8);
        headerIcon(bar, R.drawable.ic_more, "Меню книги", a::menu);
    }

    private void fade(int height) {
        View gradient = new View(a);
        gradient.setBackground(
                new GradientDrawable(
                        GradientDrawable.Orientation.TOP_BOTTOM,
                        new int[] {u.paper & 0xffffff, u.paper, u.paper}));
        u.place(layer, gradient, FILL, height, BOTTOM, 0, 0);
    }

    private void mini() {
        LinearLayout v = u.row();
        u.glass(v, 16);
        u.pad(v, 12, 0, 12, 0);
        u.place(layer, v, FILL, 64, BOTTOM, 16, 67);
        u.put(v, u.image("design/" + (a.dog ? "666ab.png" : "cbdea.png"), 30, 44, 4), 30, 44);
        u.gap(v, 12);
        LinearLayout title = u.column();
        u.weight(v, title, WRAP);
        u.label(title, a.title(), MEDIUM, 14, 17, u.ink, false);
        u.gap(title, 2);
        u.label(title, a.author(), SANS, 12, 14, u.muted, false);
        TextView cta = u.text("К чтению", MEDIUM, 14, 17, u.ink);
        u.surface(cta, u.inset, 20);
        u.pad(cta, 14, 8, 14, 8);
        u.put(v, cta, WRAP, WRAP);
        u.action(v, "К чтению · " + a.title(), a::resumeReading);
    }

    private void editor() {
        fade(68);
        LinearLayout row = u.row();
        row.setClipChildren(false);
        u.place(layer, row, FILL, 46, BOTTOM, 16, 6);
        question = new EditText(a);
        question.setSingleLine(true);
        question.setTextSize(android.util.TypedValue.COMPLEX_UNIT_PX, u.px(15));
        question.setTypeface(u.font(SANS));
        question.setTextColor(u.ink);
        question.setHintTextColor(u.muted);
        question.setContentDescription("Запрос");
        question.setHint("Ваш вопрос");
        question.setImeOptions(EditorInfo.IME_ACTION_SEND);
        question.setImeHintLocales(android.os.LocaleList.forLanguageTags("ru"));
        u.glass(question, 24);
        u.pad(question, 14, 0, 14, 0);
        u.weight(row, question, 38);
        u.gap(row, 8);
        u.put(
                row,
                u.pill("Отправить", true, false, () -> a.submit(question.getText().toString())),
                WRAP,
                38);
        question.setOnEditorActionListener(
                (v, id, e) -> {
                    if (id == EditorInfo.IME_ACTION_SEND
                            || (e != null
                                    && e.getKeyCode() == KeyEvent.KEYCODE_ENTER
                                    && e.getAction() == KeyEvent.ACTION_DOWN)) {
                        a.submit(v.getText().toString());
                        return true;
                    }
                    return false;
                });
    }

    private final class Track extends SeekBar {
        final boolean density;
        final Paint paint = new Paint(Paint.ANTI_ALIAS_FLAG);

        Track(boolean density) {
            super(a);
            this.density = density;
            setMax(density ? 100 : 10000);
            u.pad(this, density ? 12 : 0, 0, density ? 12 : 0, 0);
            setContentDescription(density ? "Степень ИИ" : "Прогресс чтения книги");
            setOnSeekBarChangeListener(
                    new OnSeekBarChangeListener() {
                        public void onStartTrackingTouch(SeekBar s) {
                            if (!density) a.scrubbing = true;
                            invalidate();
                        }

                        public void onProgressChanged(SeekBar s, int value, boolean user) {
                            if (!user) return;
                            if (density) a.changeDensity(value);
                            else a.seekFraction(value / 10000f);
                            invalidate();
                        }

                        public void onStopTrackingTouch(SeekBar s) {
                            a.scrubbing = false;
                            invalidate();
                            a.save();
                        }
                    });
        }

        @Override
        protected synchronized void onDraw(Canvas c) {
            float l = u.px(density ? 8 : 0), r = getWidth() - l, mid = getHeight() / 2f;
            float half = u.px(density ? 10 : 2), fraction = (float) getProgress() / getMax();
            float x = density ? l + half + (r - l - 2 * half) * fraction : l + (r - l) * fraction;
            paint.setColor(u.inset);
            c.drawRoundRect(l, mid - half, r, mid + half, half, half, paint);
            paint.setColor(density ? u.ink : u.muted);
            c.drawRoundRect(l, mid - half, x, mid + half, half, half, paint);
            if (density) {
                for (int i = 0; i < 10; i++) {
                    float point = l + u.px(16) + (r - l - u.px(32)) * i / 9;
                    paint.setColor(point < x ? u.paper : u.muted);
                    c.drawCircle(point, mid, u.px(1), paint);
                }
            }
            if (density || a.scrubbing) {
                paint.setColor(density ? u.paper : u.ink);
                c.drawCircle(x, mid, u.px(density ? 9 : 5), paint);
            }
        }
    }
}
