package com.raival.fileexplorer.util;

import com.pixplicity.easyprefs.library.Prefs;

public class PrefsUtils {
    public final static int SORT_NAME_A2Z = 1;
    public final static int SORT_NAME_Z2A = 2;
    public final static int SORT_SIZE_SMALLER = 3;
    public final static int SORT_SIZE_BIGGER = 4;
    public final static int SORT_DATE_OLDER = 5;
    public final static int SORT_DATE_NEWER = 6;

    public static int getSortingMethod() {
        return Prefs.getInt("sorting_method", SORT_NAME_A2Z);
    }

    public static void setSortingMethod(int method) {
        Prefs.putInt("sorting_method", method);
    }

    public static boolean listFoldersFirst() {
        return Prefs.getBoolean("list_folders_first", true);
    }

    public static void setListFoldersFirst(boolean b) {
        Prefs.putBoolean("list_folders_first", b);
    }

    public static boolean getTextEditorWordwrap() {
        return Prefs.getBoolean("text_editor_wordwrap", false);
    }

    public static void setTextEditorWordwrap(boolean wordwrap) {
        Prefs.putBoolean("text_editor_wordwrap", wordwrap);
    }

    public static boolean getTextEditorShowLineNumber() {
        return Prefs.getBoolean("text_editor_show_line_number", true);
    }

    public static void setTextEditorShowLineNumber(boolean showLineNumber) {
        Prefs.putBoolean("text_editor_show_line_number", showLineNumber);
    }

    public static boolean getTextEditorPinLineNumber() {
        return Prefs.getBoolean("text_editor_pin_line_number", true);
    }

    public static void setTextEditorPinLineNumber(boolean pinLineNumber) {
        Prefs.putBoolean("text_editor_pin_line_number", pinLineNumber);
    }

    public static boolean getTextEditorMagnifier() {
        return Prefs.getBoolean("text_editor_magnifier", true);
    }

    public static void setTextEditorMagnifier(boolean magnifier) {
        Prefs.putBoolean("text_editor_magnifier", magnifier);
    }

    public static boolean getTextEditorReadOnly() {
        return Prefs.getBoolean("text_editor_read_only", false);
    }

    public static void setTextEditorReadOnly(boolean readOnly) {
        Prefs.putBoolean("text_editor_read_only", readOnly);
    }

    public static void setTextEditorAutocomplete(boolean autocomplete) {
        Prefs.putBoolean("text_editor_autocomplete", autocomplete);
    }

    public static boolean getTextEditorAutocomplete() {
        return Prefs.getBoolean("text_editor_autocomplete", false);
    }
}
