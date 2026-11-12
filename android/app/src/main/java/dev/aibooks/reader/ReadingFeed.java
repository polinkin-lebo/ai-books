package dev.aibooks.reader;

import static dev.aibooks.reader.BookUi.*;

import android.view.Gravity;
import android.view.View;
import android.widget.*;
import java.util.*;

final class ReadingFeed {
    enum Section {
        HOME,
        INTRO,
        RECAP(10),
        FIRST,
        CHARACTER(35),
        SECOND,
        BREAK(20),
        THIRD,
        ACHIEVEMENT(90),
        FOURTH,
        HISTORY(45),
        FIFTH,
        REVIEWS(60),
        LAST,
        FINISH(10),
        CINEMA(75),
        MORE_BOOKS(55),
        LIKED(70),
        RECOMMEND(85),
        CHEKHOV;
        final int threshold;

        Section() {
            this(0);
        }

        Section(int threshold) {
            this.threshold = threshold;
        }
    }

    final BookUi u;
    final MainActivity host;
    final LinearLayout content;
    final EnumMap<Section, LinearLayout> sections = new EnumMap<>(Section.class);
    final List<TextView> annaLines = new ArrayList<>(), chekhovLines = new ArrayList<>();
    final List<ImageView> stars = new ArrayList<>();
    private final List<LinearLayout> replies = new ArrayList<>();
    private LinearLayout current;
    private TextView userReview;

    ReadingFeed(MainActivity host, BookUi ui) {
        this.host = host;
        u = ui;
        content = u.column();
        section(Section.HOME, 0, 0, 0);
        home();
        section(Section.INTRO, 20, 48, 24);
        heading(false);
        section(Section.RECAP, 20, 20, 0);
        recap();
        passage(Section.FIRST, "anna_first", "anna_second");
        section(Section.CHARACTER, 20, 20, 20);
        character();
        passage(Section.SECOND, "anna_long");
        section(Section.BREAK, 20, 20, 20);
        chapterBreak();
        passage(Section.THIRD, "anna_first", "anna_second");
        section(Section.ACHIEVEMENT, 20, 20, 20);
        award(current, false);
        passage(Section.FOURTH, "anna_first", "anna_second");
        section(Section.HISTORY, 20, 20, 20);
        history();
        passage(Section.FIFTH, "anna_long");
        section(Section.REVIEWS, 20, 20, 20);
        reviews();
        passage(Section.LAST, "anna_long");
        section(Section.FINISH, 20, 20, 20);
        finish();
        section(Section.CINEMA, 0, 20, 20);
        cinema();
        section(Section.MORE_BOOKS, 0, 20, 24);
        shelf(
                current,
                "Почитать дальше",
                NEXT,
                NEXT_FILES,
                new String[] {
                    "Снова сложная любовь — только в нескольких десятках страниц",
                    "Нежнее и светлее Карениной",
                    "Больше горькой любви"
                },
                true);
        section(Section.LIKED, 0, 20, 24);
        shelf(current, "Понравилось ранее", SAVED, SAVED_FILES, null, false);
        section(Section.RECOMMEND, 20, 20, 20);
        recommendation();
        section(Section.CHEKHOV, 20, 20, 8);
        heading(true);
        u.gap(current, 20);
        paragraph("chekhov_first", true);
        paragraph("chekhov_second", true);
    }

    static final String ANNA = "Анна Каренина",
            TOLSTOY = "Лев Толстой",
            DOG = "Дама с собачкой",
            CHEKHOV_AUTHOR = "Антон Чехов";
    private static final String[] NEXT = {DOG, "Ася", "Первая любовь"},
            NEXT_FILES = {"666ab.png", "068ff.png", "ed691.png"};
    private static final String[] SAVED = {"Страшные сказки", "Мирэ", "Самая страшная книга"},
            SAVED_FILES = {"dbece.png", "4528b.png", "9edd4.png"};
    private static final String[] HEROES = {"Анна", "Вронский", "Каренин", "Левин", "Китти"};
    private static final String[] PORTRAITS = {
        "df600.png", "10b5a.png", "c799d.png", "91410.png", "70534.png"
    };
    private static final String[] BIOS = {
        "Анна Аркадьевна Каренина — сестра Стивы Облонского, жена Алексея Каренина.",
        "Алексей Кириллович Вронский — молодой офицер. Петрицкий — его сослуживец и сосед по квартире.",
        "Алексей Александрович Каренин — петербургский чиновник, муж Анны.",
        "Константин Дмитриевич Левин — помещик, знакомый семьи Щербацких.",
        "Княжна Екатерина Александровна Щербацкая — младшая сестра Долли."
    };

    private void section(Section key, int edge, int top, int bottom) {
        current = u.column();
        u.pad(current, edge, top, edge, bottom);
        sections.put(key, current);
        u.put(content, current, FILL, WRAP);
    }

    private TextView words(
            LinearLayout box,
            String key,
            String font,
            int size,
            int line,
            int color,
            boolean center) {
        return u.label(box, u.str(key), font, size, line, color, center);
    }

    private LinearLayout card(LinearLayout p, int padding) {
        LinearLayout box = u.column();
        u.surface(box, u.card, 16);
        u.pad(box, padding, 16, padding, 16);
        u.put(p, box, FILL, WRAP);
        return box;
    }

    private void eyebrow(LinearLayout box, String text, boolean center) {
        TextView v =
                u.label(box, text.toUpperCase(new Locale("ru")), MEDIUM, 11, 15, u.muted, center);
        v.setLetterSpacing(.09f);
    }

    private void chip(LinearLayout box, String text, Runnable action) {
        u.put(box, u.pill(text, false, true, action), WRAP, WRAP);
    }

    private void bubble(String text, Runnable click) {
        TextView v = u.text(text, MEDIUM, 16, 20, u.ink);
        u.surface(v, u.card, 20);
        u.pad(v, 16, 10, 16, 10);
        v.setMaxWidth(u.px(303));
        LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(WRAP, WRAP);
        lp.gravity = Gravity.END;
        current.addView(v, lp);
        if (click != null) u.action(v, null, click);
        u.gap(current, 12);
    }

    private void heading(boolean dog) {
        u.label(current, dog ? DOG : ANNA, TITLE, 32, 32, u.ink, false);
        u.gap(current, 8);
        u.label(current, dog ? CHEKHOV_AUTHOR : TOLSTOY, BOOK, 20, 20, u.ink, false);
        u.gap(current, 12);
        u.label(current, dog ? "Глава I" : "Глава XXXIV", SANS, 14, 15, u.muted, false);
    }

    private void passage(Section key, String... words) {
        section(key, 20, key == Section.FIRST ? 12 : 20, 8);
        for (String text : words) paragraph(text, false);
    }

    private void paragraph(String key, boolean dog) {
        TextView p =
                words(
                        current,
                        key,
                        SERIF,
                        host.fontSize,
                        Math.round(host.fontSize * 1.48f),
                        u.ink,
                        false);
        (dog ? chekhovLines : annaLines).add(p);
        u.gap(current, 24);
    }

    private void home() {
        LinearLayout portrait = u.row();
        u.pad(portrait, 20, 20, 0, 0);
        u.put(current, portrait, FILL, 100);
        u.put(portrait, u.image(art("b686d.png"), 80, 80, 40), 80, 80);
        LinearLayout greeting = u.column();
        u.pad(greeting, 20, 20, 20, 20);
        u.put(current, greeting, FILL, WRAP);
        words(greeting, "greeting", TITLE, 32, 32, u.ink, false);
        LinearLayout books = u.carousel(current, 20);
        started(books, ANNA, TOLSTOY, "cbdea.png", 69, false, () -> host.go(Section.RECAP));
        u.gap(books, 8);
        started(
                books,
                "Скандинавские мифы",
                "Кэролин Ларрингон",
                "4d3fe.png",
                13,
                false,
                () -> host.compose("Хочу скандинавские мифы"));
        LinearLayout suggestions = u.column();
        u.pad(suggestions, 20, 20, 20, 20);
        u.put(current, suggestions, FILL, WRAP);
        words(suggestions, "explore", TITLE, 22, 30, u.ink, false);
        u.gap(suggestions, 12);
        chip(suggestions, "короткие вечерние истории", () -> host.go(Section.RECOMMEND));
        u.gap(suggestions, 12);
        chip(suggestions, "классика не про любовь", () -> host.go(Section.RECOMMEND));
        u.gap(suggestions, 12);
        chip(
                suggestions,
                "популярные цитаты из Анны Карениной",
                () ->
                        host.info(
                                "Цитаты из романа",
                                "«Все счастливые семьи похожи друг на друга, каждая несчастливая семья несчастлива по-своему».\n\n«Уважение выдумали для того, чтобы скрывать пустое место, где должна быть любовь»."));
        LinearLayout outer = u.column();
        u.pad(outer, 20, 20, 20, 20);
        u.put(current, outer, FILL, WRAP);
        award(outer, true);
    }

    private String art(String file) {
        return file.contains("/") ? file : "design/" + file;
    }

    private void started(
            LinearLayout row,
            String title,
            String author,
            String file,
            int progress,
            boolean small,
            Runnable click) {
        int inset = small ? 10 : 12;
        LinearLayout b = u.row();
        u.surface(b, u.card, 12);
        u.pad(b, inset, inset, inset, inset);
        u.put(row, b, small ? 226 : 255, small ? 74 : 84);
        u.put(
                b,
                u.image(art(file), small ? 36 : 41, small ? 53 : 60, 4),
                small ? 36 : 41,
                small ? 53 : 60);
        u.gap(b, small ? 10 : 12);
        LinearLayout lines = u.column();
        u.weight(b, lines, WRAP);
        u.label(lines, title, MEDIUM, small ? 14 : 16, 19, u.ink, false).setSingleLine();
        u.gap(lines, 2);
        u.label(lines, author, SANS, small ? 11 : 12, 14, u.ink, false);
        u.gap(lines, 2);
        u.label(lines, progress + "% прочитано", SANS, small ? 11 : 12, 14, u.muted, false);
        u.action(b, title, click);
    }

    private void award(LinearLayout outer, boolean first) {
        LinearLayout v = card(outer, 16);
        v.setGravity(Gravity.CENTER_HORIZONTAL);
        u.put(v, u.image(first ? "4079-21664.png" : "4079-21740.png", 123, 123, 0), 123, 123);
        u.gap(v, 12);
        words(v, first ? "rhythm" : "achievement", TITLE, 32, 32, u.ink, true);
        u.gap(v, 12);
        words(v, "achievement_body", SANS, 16, 19, u.ink, true);
        if (first) {
            u.gap(v, 12);
            words(v, "streak", SANS, 16, 19, u.muted, true);
        }
    }

    private void recap() {
        LinearLayout v = card(current, 20);
        eyebrow(v, u.str("recap_label"), false);
        u.gap(v, 12);
        words(v, "recap_title", TITLE, 22, 30, u.ink, false);
        words(v, "recap_note", SANS, 12, 17, u.accent, false);
        u.gap(v, 12);
        words(v, "recap_body", SANS, 15, 21, u.ink, false);
    }

    private void character() {
        bubble(
                "Напомни, кто такой петрицкий",
                () -> host.compose("А как Петрицкий связан с Вронским?"));
        LinearLayout card = card(current, 16);
        card.setGravity(Gravity.CENTER_HORIZONTAL);
        u.put(card, u.image("4079-21696.png", 98, 98, 49), 98, 98);
        u.gap(card, 12);
        words(card, "character", TITLE, 32, 38, u.ink, true);
        u.gap(card, 4);
        words(card, "character_body", SANS, 15, 21, u.ink, true);
        u.gap(card, 12);
        words(card, "character_link", MEDIUM, 14, 19, u.accent, true);
        u.gap(current, 12);
        LinearLayout others = card(current, 0);
        words(others, "connections", TITLE, 22, 30, u.ink, true);
        u.gap(others, 12);
        LinearLayout row = u.carousel(others, 20);
        for (int i = 0; i < HEROES.length; i++) {
            int index = i;
            if (i > 0) u.gap(row, 12);
            LinearLayout item = u.column();
            u.put(row, item, 60, WRAP);
            u.put(item, u.image(art(PORTRAITS[i]), 60, 60, 30), 60, 60);
            u.gap(item, 6);
            u.label(item, HEROES[i], MEDIUM, 12, 14, u.ink, true);
            u.action(item, "О герое: " + HEROES[i], () -> host.info(HEROES[index], BIOS[index]));
        }
    }

    private void chapterBreak() {
        for (String key : new String[] {"break", "next"}) {
            LinearLayout v = card(current, 20);
            eyebrow(v, u.str(key + "_label"), false);
            u.gap(v, 12);
            words(v, key + "_title", TITLE, 22, 30, u.ink, false);
            u.gap(v, 6);
            words(v, key + "_body", SANS, 15, 21, u.ink, false);
            if (key.equals("next")) {
                u.gap(v, 12);
                words(v, "next_time", SANS, 12, 17, u.accent, false);
                u.action(v, u.str("next_title"), () -> host.go(Section.THIRD));
            } else u.gap(current, 12);
        }
    }

    private void history() {
        LinearLayout v = card(current, 20);
        eyebrow(v, u.str("history_label"), false);
        u.gap(v, 12);
        words(v, "history_title", TITLE, 22, 30, u.ink, false);
        u.gap(v, 6);
        words(v, "history_body", SANS, 15, 21, u.ink, false);
        u.gap(v, 12);
        String[] values = {"1875", "1877", "1878"},
                keys = {"history_one", "history_two", "history_three"};
        for (int i = 0; i < 3; i++) {
            LinearLayout r = u.row();
            u.put(v, r, FILL, WRAP);
            u.put(r, u.text(values[i], TITLE, 16, 19, u.ink), 38, WRAP);
            u.weight(r, u.text(u.str(keys[i]), SANS, 13, 17, u.ink), WRAP);
            u.gap(v, 8);
        }
        chip(
                v,
                "Об истории романа",
                () ->
                        host.info(
                                "Об истории романа",
                                "1875 — первые главы в «Русском вестнике».\n\n1877 — восьмая часть отдельным изданием.\n\n1878 — первое книжное издание."));
        u.gap(v, 12);
        TextView pause = words(v, "history_pause", SANS, 12, 16, u.muted, false);
        u.action(pause, null, () -> host.changeDensity(45));
    }

    private void reviews() {
        bubble("Мне близка Анна, а другим?", () -> host.compose("Почему читатели спорят об Анне?"));
        LinearLayout panel = card(current, 0);
        TextView label =
                u.label(
                        panel,
                        u.str("reviews_label").toUpperCase(new Locale("ru")),
                        MEDIUM,
                        11,
                        15,
                        u.muted,
                        false);
        u.pad(label, 20, 0, 0, 0);
        label.setLetterSpacing(.09f);
        u.gap(panel, 12);
        LinearLayout row = u.carousel(panel, 16);
        for (int i = 0; i < 2; i++) {
            if (i > 0) u.gap(row, 12);
            LinearLayout v = u.column();
            u.surface(v, u.inset, 16);
            u.pad(v, 16, 16, 16, 16);
            u.put(row, v, 240, WRAP);
            LinearLayout by = u.row();
            u.put(v, by, FILL, WRAP);
            u.put(by, u.image(art(i == 0 ? "3f0aa.png" : "d8563.png"), 40, 40, 20), 40, 40);
            u.gap(by, 12);
            LinearLayout name = u.column();
            u.weight(by, name, WRAP);
            u.label(name, i == 0 ? "Марина" : "Алексей", MEDIUM, 14, 17, u.ink, false);
            u.label(name, "Книга прочитана", SANS, 12, 15, u.muted, false);
            u.gap(v, 8);
            words(v, i == 0 ? "marina" : "alexey", SANS, 15, 20, u.ink, false);
            u.gap(v, 12);
            words(v, i == 0 ? "marina_tag" : "alexey_tag", SANS, 12, 17, u.accent, false);
        }
    }

    private void finish() {
        eyebrow(current, "Конец", true);
        u.gap(current, 8);
        words(current, "finished", TITLE, 24, 29, u.ink, true);
        u.gap(current, 12);
        LinearLayout v = card(current, 16);
        v.setGravity(Gravity.CENTER_HORIZONTAL);
        words(v, "rating", MEDIUM, 16, 19, u.ink, true);
        u.gap(v, 14);
        LinearLayout row = u.row();
        u.put(v, row, WRAP, WRAP);
        for (int i = 1; i <= 5; i++) {
            int n = i;
            ImageView star = new ImageView(u.ctx);
            star.setImageResource(R.drawable.ic_rating_star);
            if (i > 1) u.gap(row, 4);
            u.put(row, star, 48, 48);
            stars.add(star);
            u.action(
                    star,
                    "Оценка " + i + " из 5",
                    () -> {
                        host.rating = n;
                        updateRating();
                        host.save();
                        host.notice("Ваша оценка: " + n + " из 5");
                    });
        }
        updateRating();
        u.gap(v, 14);
        u.put(v, u.pill("Написать пару слов", false, false, host::writeReview), WRAP, 40);
        userReview = u.label(v, "", SANS, 15, 21, u.ink, false);
        u.pad(userReview, 0, 12, 0, 0);
        updateReview();
    }

    private void updateRating() {
        for (int i = 0; i < stars.size(); i++) {
            int color =
                    host.rating == 0
                            ? (i < 4 ? 0xffffd675 : u.paper)
                            : i < host.rating ? 0xffffb838 : u.inset;
            stars.get(i).setColorFilter(color);
        }
    }

    void updateReview() {
        userReview.setText(host.review);
        userReview.setVisibility(host.review.isEmpty() ? View.GONE : View.VISIBLE);
    }

    private void cinema() {
        TextView head = u.label(current, "Другой взгляд на сюжет", BOOK, 22, 26, u.ink, false);
        u.pad(head, 20, 0, 20, 0);
        u.gap(current, 12);
        LinearLayout row = u.carousel(current, 20);
        for (int i = 0; i < 2; i++) {
            boolean film = i == 0;
            if (!film) u.gap(row, 12);
            LinearLayout v = u.column();
            u.surface(v, u.card, 16);
            u.put(row, v, 280, WRAP);
            u.put(v, u.image(art(film ? "1cda8.png" : "329c0.png"), 280, 170, 0), 280, 170);
            LinearLayout body = u.column();
            u.pad(body, 20, 16, 20, 16);
            u.put(v, body, FILL, WRAP);
            String key = film ? "film" : "theatre";
            eyebrow(body, u.str(key + "_label"), false);
            u.gap(body, 10);
            u.label(body, ANNA, TITLE, 28, 32, u.ink, false);
            words(body, key + "_meta", SANS, 12, 15, u.muted, false);
            u.gap(body, 12);
            TextView desc = words(body, key + "_body", SANS, 15, 21, film ? u.ink : u.muted, false);
            desc.setMinHeight(u.px(84));
            u.gap(body, 16);
            String action = film ? "Смотреть кино" : "Купить билет";
            u.put(
                    body,
                    u.pill(
                            action,
                            true,
                            false,
                            () ->
                                    host.info(
                                            action,
                                            "В прототипе показан переход к фильму или спектаклю. Доступность и покупка билетов демонстрационные.")),
                    WRAP,
                    36);
        }
        LinearLayout extra = u.column();
        u.pad(extra, 20, 16, 20, 0);
        u.put(current, extra, FILL, WRAP);
        u.label(extra, "Может быть интересно", TITLE, 18, 24, u.ink, false);
        for (String q : new String[] {"Что изменилось относительно книги?", "Что есть в театре?"}) {
            u.gap(extra, 12);
            chip(extra, q, () -> host.compose(q));
        }
    }

    private void recommendation() {
        bubble(u.str("recommend_query"), null);
        LinearLayout v = card(current, 16);
        v.setGravity(Gravity.CENTER_HORIZONTAL);
        u.put(v, u.image(art("666ab.png"), 106, 160, 6), 106, 160);
        u.gap(v, 12);
        u.label(v, DOG, TITLE, 24, 29, u.ink, true);
        u.label(v, CHEKHOV_AUTHOR, SANS, 15, 21, u.muted, true);
        u.gap(v, 12);
        words(v, "recommend_body", SANS, 16, 22, u.ink, true);
        u.gap(v, 12);
        LinearLayout why = card(v, 16);
        u.surface(why, u.inset, 16);
        LinearLayout whyTitle = u.row();
        whyTitle.setGravity(Gravity.CENTER);
        u.put(why, whyTitle, FILL, WRAP);
        u.put(whyTitle, u.image(art("lumen-head.png"), 16, 16, 0), 16, 16);
        u.gap(whyTitle, 8);
        TextView whyText =
                u.text(u.str("why").toUpperCase(new Locale("ru")), MEDIUM, 11, 15, u.accent);
        u.put(whyTitle, whyText, WRAP, WRAP);
        u.gap(why, 8);
        words(why, "why_body", MEDIUM, 14, 16, u.ink, true);
        u.gap(v, 12);
        u.put(
                v,
                u.pill("Попробовать первые строки", true, false, () -> host.go(Section.CHEKHOV)),
                WRAP,
                36);
    }

    private void shelf(
            LinearLayout parent,
            String heading,
            String[] names,
            String[] files,
            String[] captions,
            boolean directChekhov) {
        TextView h = u.label(parent, heading, BOOK, 22, 26, u.ink, false);
        u.pad(h, 20, 0, 20, 0);
        u.gap(parent, 9);
        LinearLayout row = u.carousel(parent, 20);
        for (int i = 0; i < names.length; i++) {
            String title = names[i];
            boolean open = i == 0 && directChekhov;
            if (i > 0) u.gap(row, 8);
            LinearLayout box = u.column();
            u.put(row, box, 128, WRAP);
            u.put(box, u.image(art(files[i]), 128, 192, 8), 128, 192);
            if (captions != null) {
                u.gap(box, 8);
                u.label(box, captions[i], MEDIUM, 14, 17, u.ink, false);
            }
            u.action(
                    box,
                    title,
                    () -> {
                        if (open) host.go(Section.CHEKHOV);
                        else host.compose("Хочу прочитать «" + title + "»");
                    });
        }
    }

    LinearLayout discovery() {
        LinearLayout page = u.column();
        u.pad(page, 0, 20, 0, 176);
        LinearLayout avatar = u.row();
        u.pad(avatar, 20, 0, 0, 0);
        u.put(page, avatar, FILL, WRAP);
        u.put(avatar, u.image(art("b686d.png"), 64, 64, 32), 64, 64);
        u.gap(page, 20);
        TextView h = u.label(page, "Начинали читать", BOOK, 22, 26, u.ink, false);
        u.pad(h, 20, 0, 20, 0);
        u.gap(page, 8);
        LinearLayout row = u.carousel(page, 20);
        started(
                row,
                "Мастер и Маргарита",
                "Михаил Булгаков",
                "catalog/master.jpg",
                36,
                true,
                () -> host.compose("Хочу прочитать «Мастер и Маргарита»"));
        u.gap(row, 7);
        started(
                row,
                "Скандинавские мифы",
                "Кэролин Ларрингон",
                "4d3fe.png",
                13,
                true,
                () -> host.compose("Хочу прочитать «Скандинавские мифы»"));
        u.gap(page, 32);
        shelf(
                page,
                "Хотели прочитать",
                SAVED,
                SAVED_FILES,
                new String[] {
                    "Знакомые сказки\nс неожиданной\nтёмной стороной",
                    "Для вечера\nс необычной\nгероиней",
                    "Когда хочется\nпощекотать\nсебе нервы"
                },
                false);
        u.gap(page, 32);
        shelf(
                page,
                "Новое про любовь",
                new String[] {"Тысячеликая героиня", "Ася", "Первая любовь"},
                new String[] {"f5167.png", "068ff.png", "ed691.png"},
                new String[] {
                    "Истории женщин,\nменяющие\nвзгляд на мир",
                    "Нежнее\nи светлее\nКарениной",
                    "Больше горькой\nлюбви"
                },
                false);
        u.gap(page, 32);
        shelf(
                page,
                "Любопытное из популярного",
                new String[] {"Дочери Колыбели", "Смех лисы", "Семь способов засолки душ"},
                new String[] {"catalog/daughters.jpg", "catalog/fox.png", "catalog/souls.jpg"},
                new String[] {
                    "Магия и дружба\nс испытаниями\nна прочность",
                    "Каникулы,\nкоторые хочется\nпродлить",
                    "Прошлое не спит.\nСамое время\nузнать его тайны"
                },
                false);
        u.gap(page, 32);
        shelf(
                page,
                "Чуть бережнее к себе",
                new String[] {
                    "К себе нежно",
                    "Я больше не пытаюсь все успеть",
                    "Если все кошки в мире исчезнут"
                },
                new String[] {"catalog/gentle.jpg", "catalog/slow.jpg", "catalog/cats.jpg"},
                new String[] {
                    "Чуть больше\nтепла и заботы\nк самому себе",
                    "Разрешение\nзамедлиться\nи просто жить",
                    "О мелочах,\nкоторые значат\nочень многое"
                },
                false);
        u.gap(page, 32);
        shelf(
                page,
                "Понять себя и других",
                new String[] {
                    "Искусство любить",
                    "Думай медленно… Решай быстро",
                    "Как разговаривать с мудаками"
                },
                new String[] {"catalog/love.jpg", "catalog/thinking.jpg", "catalog/talk.jpg"},
                new String[] {
                    "Любовь — это\nтоже искусство.\nПопробуем понять",
                    "Как мы думаем\nи почему порой\nсебе мешаем",
                    "Спокойнее\nв непростых\nразговорах"
                },
                false);
        return page;
    }

    void density(int value) {
        sections.forEach(
                (k, v) -> v.setVisibility(value >= k.threshold ? View.VISIBLE : View.GONE));
    }

    Section at(int y) {
        Section at = Section.HOME;
        for (Section s : Section.values()) {
            View v = sections.get(s);
            if (v.getVisibility() == View.VISIBLE && v.getTop() <= y) at = s;
        }
        return at;
    }

    View reply(Section after, String question, String answer) {
        LinearLayout outer = u.column();
        u.pad(outer, 20, 16, 20, 20);
        LinearLayout box = card(outer, 20);
        u.label(box, question, TITLE, 22, 28, u.ink, false);
        u.gap(box, 10);
        u.label(box, answer, SANS, 15, 21, u.ink, false);
        u.gap(box, 12);
        u.label(box, "Ответ по текущему фрагменту · демо", SANS, 12, 17, u.muted, false);
        u.gap(box, 12);
        u.put(
                box,
                u.pill("Продолжить чтение ↓", false, false, () -> host.continueAfter(outer)),
                WRAP,
                36);
        int i = content.indexOfChild(sections.get(after)) + 1;
        while (i < content.getChildCount() && replies.contains(content.getChildAt(i))) i++;
        content.addView(outer, i, new LinearLayout.LayoutParams(FILL, WRAP));
        replies.add(outer);
        return outer;
    }
}
