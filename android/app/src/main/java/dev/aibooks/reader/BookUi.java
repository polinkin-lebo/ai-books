package dev.aibooks.reader;

import android.content.Context;
import android.content.res.ColorStateList;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Typeface;
import android.graphics.drawable.GradientDrawable;
import android.graphics.drawable.RippleDrawable;
import android.text.Layout;
import android.util.LruCache;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.View;
import android.widget.*;
import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import org.json.JSONObject;

final class BookUi {
    static final int FILL = -1, WRAP = -2;
    static final String SANS = "YSText.ttf",
            MEDIUM = "YSText-Medium.ttf",
            BOOK = "YSBooks.otf",
            TITLE = "YSBooks-Semibold.otf",
            SERIF = "Literata.ttf";
    final Context ctx;
    final float scale;
    int paper, ink, muted, card, inset, accent;
    private final HashMap<String, Typeface> fonts = new HashMap<>();
    private final LruCache<String, Bitmap> images =
            new LruCache<String, Bitmap>(12 * 1024 * 1024) {
                @Override
                protected int sizeOf(String key, Bitmap value) {
                    return value.getByteCount();
                }
            };
    final JSONObject copy;

    BookUi(Context ctx, int width) {
        this.ctx = ctx;
        scale = width / 375f;
        copy = json("copy.json");
    }

    JSONObject json(String file) {
        try (InputStream stream = ctx.getAssets().open(file);
                BufferedReader reader =
                        new BufferedReader(new InputStreamReader(stream, StandardCharsets.UTF_8))) {
            StringBuilder body = new StringBuilder();
            String line;
            while ((line = reader.readLine()) != null) body.append(line);
            return new JSONObject(body.toString());
        } catch (Exception e) {
            throw new IllegalStateException("Cannot read " + file, e);
        }
    }

    String str(String key) {
        return copy.optString(key);
    }

    void theme(int choice) {
        boolean dark = choice == 2;
        paper = dark ? 0xff252526 : choice == 1 ? 0xfff7eedc : 0xffffffff;
        ink = dark ? 0xffece9e2 : 0xff2d2d2d;
        muted = dark ? 0xffb5b1aa : 0xff85817e;
        card = dark ? 0xff333335 : choice == 1 ? 0xffede3d1 : 0xfff4f4f4;
        inset = dark ? 0xff414145 : choice == 1 ? 0xffe2d8c5 : 0xffeaeaea;
        accent = dark ? 0xff86d0d5 : 0xff027d88;
    }

    int px(float n) {
        return Math.round(scale * n);
    }

    private int size(int n) {
        return n < 0 ? n : px(n);
    }

    LinearLayout column() {
        LinearLayout v = new LinearLayout(ctx);
        v.setOrientation(LinearLayout.VERTICAL);
        return v;
    }

    LinearLayout row() {
        LinearLayout v = new LinearLayout(ctx);
        v.setGravity(Gravity.CENTER_VERTICAL);
        return v;
    }

    void pad(View v, int l, int t, int r, int b) {
        v.setPadding(px(l), px(t), px(r), px(b));
    }

    void put(LinearLayout p, View v, int w, int h) {
        p.addView(v, new LinearLayout.LayoutParams(size(w), size(h)));
    }

    void weight(LinearLayout p, View v, int h) {
        p.addView(v, new LinearLayout.LayoutParams(0, size(h), 1));
    }

    void gap(LinearLayout p, int n) {
        View space = new Space(ctx);
        boolean vertical = p.getOrientation() == LinearLayout.VERTICAL;
        put(p, space, vertical ? 1 : n, vertical ? n : 1);
    }

    void place(FrameLayout p, View v, int w, int h, int gravity, int marginX, int marginY) {
        FrameLayout.LayoutParams lp = new FrameLayout.LayoutParams(size(w), size(h), gravity);
        lp.leftMargin = lp.rightMargin = px(marginX);
        if ((gravity & Gravity.VERTICAL_GRAVITY_MASK) == Gravity.BOTTOM)
            lp.bottomMargin = px(marginY);
        else lp.topMargin = px(marginY);
        p.addView(v, lp);
    }

    GradientDrawable shape(int color, float radius) {
        GradientDrawable d = new GradientDrawable();
        d.setColor(color);
        d.setCornerRadius(px(radius));
        return d;
    }

    void surface(View v, int color, int radius) {
        v.setBackground(shape(color, radius));
        v.setClipToOutline(true);
    }

    void glass(View v, int radius) {
        surface(v, paper, radius);
        v.setElevation(px(3));
        v.setOutlineAmbientShadowColor(0x50000000);
        v.setOutlineSpotShadowColor(0x45000000);
    }

    void action(View v, String label, Runnable run) {
        if (label != null) v.setContentDescription(label);
        v.setFocusable(true);
        v.setForeground(
                new RippleDrawable(
                        ColorStateList.valueOf(0x18298085), null, shape(0xffffffff, 12)));
        v.setOnClickListener(ignored -> run.run());
    }

    Typeface font(String file) {
        return fonts.computeIfAbsent(
                file, k -> Typeface.createFromAsset(ctx.getAssets(), "fonts/" + k));
    }

    TextView text(String value, String font, int sp, int line, int color) {
        TextView v = new TextView(ctx);
        v.setIncludeFontPadding(false);
        v.setTypeface(font(font));
        v.setTextSize(TypedValue.COMPLEX_UNIT_PX, px(sp));
        v.setTextColor(color);
        v.setText(value);
        v.setBreakStrategy(Layout.BREAK_STRATEGY_SIMPLE);
        v.setHyphenationFrequency(Layout.HYPHENATION_FREQUENCY_NONE);
        v.setLineHeight(px(line));
        return v;
    }

    TextView label(
            LinearLayout p,
            String value,
            String font,
            int sz,
            int line,
            int color,
            boolean center) {
        TextView t = text(value, font, sz, line, color);
        if (center) t.setGravity(Gravity.CENTER);
        put(p, t, FILL, WRAP);
        return t;
    }

    ImageView image(String file, int width, int height, int radius) {
        int tw = Math.max(1, px(width)), th = Math.max(1, px(height));
        String key = file + ":" + tw + "x" + th;
        Bitmap bmp = images.get(key);
        if (bmp == null) {
            BitmapFactory.Options opts = new BitmapFactory.Options();
            opts.inJustDecodeBounds = true;
            try (InputStream in = ctx.getAssets().open(file)) {
                BitmapFactory.decodeStream(in, null, opts);
            } catch (IOException e) {
                throw new IllegalStateException(file, e);
            }
            int sample = 1;
            while (opts.outWidth / (sample * 2) >= tw && opts.outHeight / (sample * 2) >= th)
                sample *= 2;
            opts.inJustDecodeBounds = false;
            opts.inSampleSize = sample;
            try (InputStream in = ctx.getAssets().open(file)) {
                bmp = BitmapFactory.decodeStream(in, null, opts);
            } catch (IOException e) {
                throw new IllegalStateException(file, e);
            }
            if (bmp == null) throw new IllegalStateException("Invalid image: " + file);
            images.put(key, bmp);
        }
        ImageView img = new ImageView(ctx);
        img.setImageBitmap(bmp);
        img.setScaleType(ImageView.ScaleType.CENTER_CROP);
        if (radius > 0) surface(img, 0x00000000, radius);
        return img;
    }

    TextView pill(String title, boolean primary, boolean outline, Runnable click) {
        TextView t = text(title, MEDIUM, 14, 19, primary ? paper : ink);
        t.setGravity(Gravity.CENTER);
        pad(t, 16, 4, 16, 4);
        GradientDrawable bg = shape(primary ? ink : outline ? paper : inset, 40);
        if (outline) bg.setStroke(Math.max(1, px(.55f)), accent);
        t.setBackground(bg);
        t.setMinHeight(px(36));
        t.setClipToOutline(true);
        action(t, null, click);
        return t;
    }

    LinearLayout carousel(LinearLayout parent, int edge) {
        HorizontalScrollView scroll = new HorizontalScrollView(ctx);
        scroll.setHorizontalScrollBarEnabled(false);
        scroll.setOverScrollMode(View.OVER_SCROLL_NEVER);
        LinearLayout items = row();
        items.setGravity(Gravity.TOP);
        pad(items, edge, 0, edge, 0);
        scroll.addView(items);
        put(parent, scroll, FILL, WRAP);
        return items;
    }
}
