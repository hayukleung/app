package com.hayukleung.app.util.text;

import android.content.res.ColorStateList;
import android.text.Spannable;
import android.text.Spanned;
import android.text.style.CharacterStyle;
import android.text.style.TextAppearanceSpan;

import java.util.regex.Pattern;

public class SpanUtils {

    public static void addStyle(Spannable s, Pattern pattern, String scheme) {
        Linkify.addLinks(s, pattern, scheme, null);
    }

    public static void addStyle(Spannable s, Pattern pattern, String scheme, com.hayukleung.app.util.text.URLSpan.SpanCreator spanCreator) {
        Linkify.addLinks(s, pattern, scheme, spanCreator);
    }

    public static void addStyle(Spannable s, Pattern pattern, String scheme, Linkify.MatchFilter matchFilter, com.hayukleung.app.util.text.URLSpan.SpanCreator spanCreator) {
        Linkify.addLinks(s, pattern, scheme, matchFilter, spanCreator);
    }

    public static void addStyle(Spannable s, Pattern pattern, String scheme, Linkify.TransformFilter transformFilter, com.hayukleung.app.util.text.URLSpan.SpanCreator spanCreator) {
        Linkify.addLinks(s, pattern, scheme, transformFilter, spanCreator);
    }

    public static void addStyle(Spannable s, int start, int end, CharacterStyle span) {
        s.setSpan(span, start, end, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
    }

    public static void addStyle(Spannable s, int start, int end, int size, int color) {
        setStyle(s, start, end, null, 0, size, ColorStateList.valueOf(color), null);
    }

    public static void addStyle(Spannable s, int start, int end, int size, ColorStateList color) {
        setStyle(s, start, end, null, 0, size, color, null);
    }

    public static void setStyle(Spannable s, int start, int end, String family, int style, int size, ColorStateList color, ColorStateList linkColor) {
        addStyle(s, start, end, new TextAppearanceSpan(family, style, size, color, linkColor));
    }
}
