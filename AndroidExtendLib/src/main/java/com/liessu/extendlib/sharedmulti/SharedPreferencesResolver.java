package com.liessu.extendlib.sharedmulti;

import android.content.ContentValues;
import android.content.Context;
import android.content.SharedPreferences;
import android.database.ContentObserver;
import android.database.Cursor;
import android.net.Uri;
import android.os.Handler;
import android.os.Looper;
import android.support.annotation.Nullable;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * <p>A content resolver for SharedPreferences  in multi process .
 */
public class SharedPreferencesResolver implements SharedPreferences {
    private static final String TAG = "SharedPrefResolver";
    private static final String TYPE = "type";
    private static final String KEY = "key";
    private static final String INT_TYPE = "integer";
    private static final String LONG_TYPE = "long";
    private static final String FLOAT_TYPE = "float";
    private static final String BOOLEAN_TYPE = "boolean";
    private static final String STRING_TYPE = "string";
    public static String PREFERENCE_AUTHORITY;
    public static Uri BASE_URI;

    private Context context;
    public SharedPreferences sharedPreferences;
    private List<OnSharedPreferenceChangeListener> changeListeners = new ArrayList<>();

    public SharedPreferencesResolver(Context context){
        this.context = context;
        this.sharedPreferences = this;
    }

    private void init(Context context) {
        PREFERENCE_AUTHORITY = "com.liessu.extendlib.sharedmulti.SharedPreferencesProvider";
        BASE_URI = Uri.parse("content://" + PREFERENCE_AUTHORITY);

        if(this.context == null){
            this.context = context;
        }
        this.context.getContentResolver().registerContentObserver(BASE_URI ,true , contentObserver);
    }


    /****************************************************************************************************
     class Editor implements SharedPreferences.Editor
     ******************************************************************************************************/
    public class EditorImp implements SharedPreferences.Editor{
        private Context context;
        private ContentValues values = new ContentValues();

        private EditorImp(Context context) {
            this.context = context;
        }

        /**
         * Set a String value in the preferences editor, to be written back once
         * {@link #commit} or {@link #apply} are called.
         *
         * @param key The name of the preference to modify.
         * @param value The new value for the preference.
         *
         * @return Returns a reference to the same Editor object, so you can
         * chain put calls together.
         */
        public Editor putString(String key, String value) {
            values.put(key, value);
            return this;
        }

        /**
         * Set an int value in the preferences editor, to be written back once
         * {@link #commit} or {@link #apply} are called.
         *
         * @param key The name of the preference to modify.
         * @param value The new value for the preference.
         *
         * @return Returns a reference to the same Editor object, so you can
         * chain put calls together.
         */
        public Editor putInt(String key, int value) {
            values.put(key, value);
            return this;
        }

        /**
         * Set a long value in the preferences editor, to be written back once
         * {@link #commit} or {@link #apply} are called.
         *
         * @param key The name of the preference to modify.
         * @param value The new value for the preference.
         *
         * @return Returns a reference to the same Editor object, so you can
         * chain put calls together.
         */
        public Editor putLong(String key, long value) {
            values.put(key, value);
            return this;
        }

        /**
         * Set a float value in the preferences editor, to be written back once
         * {@link #commit} or {@link #apply} are called.
         *
         * @param key The name of the preference to modify.
         * @param value The new value for the preference.
         *
         * @return Returns a reference to the same Editor object, so you can
         * chain put calls together.
         */
        public Editor putFloat(String key, float value) {
            values.put(key, value);
            return this;
        }

        /**
         * Set a boolean value in the preferences editor, to be written back
         * once {@link #commit} or {@link #apply} are called.
         *
         * @param key The name of the preference to modify.
         * @param value The new value for the preference.
         *
         * @return Returns a reference to the same Editor object, so you can
         * chain put calls together.
         */
        public Editor putBoolean(String key, boolean value) {
            values.put(key, value);
            return this;
        }

        /**
         * Mark in the editor that a preference value should be removed, which
         * will be done in the actual preferences once {@link #commit} is
         * called.
         *
         * <p>Note that when committing back to the preferences, all removals
         * are done first, regardless of whether you called remove before
         * or after put methods on this editor.
         *
         * @param key The name of the preference to remove.
         *
         * @return Returns a reference to the same Editor object, so you can
         * chain put calls together.
         */
        public Editor remove(String key) {
            values.putNull(key);
            return this;
        }

        /**
         * Commit your preferences changes back from this Editor to the
         * {@link SharedPreferences} object it is editing.  This atomically
         * performs the requested modifications, replacing whatever is currently
         * in the SharedPreferences.
         *
         * <p>Note that when two editors are modifying preferences at the same
         * time, the last one to call apply wins.
         *
         * <p>Unlike {@link #commit}, which writes its preferences out
         * to persistent storage synchronously, {@link #apply}
         * commits its changes to the in-memory
         * {@link SharedPreferences} immediately but starts an
         * asynchronous commit to disk and you won't be notified of
         * any failures.  If another editor on this
         * {@link SharedPreferences} does a regular {@link #commit}
         * while a {@link #apply} is still outstanding, the
         * {@link #commit} will block until all async commits are
         * completed as well as the commit itself.
         *
         * <p>As {@link SharedPreferences} instances are singletons within
         * a process, it's safe to replace any instance of {@link #commit} with
         * {@link #apply} if you were already ignoring the return value.
         *
         * <p>You don't need to worry about Android component
         * lifecycles and their interaction with <code>apply()</code>
         * writing to disk.  The framework makes sure in-flight disk
         * writes from <code>apply()</code> complete before switching
         * states.
         *
         * <p class='note'>The SharedPreferences.Editor interface
         * isn't expected to be implemented directly.  However, if you
         * previously did implement it and are now getting errors
         * about missing <code>apply()</code>, you can simply call
         * {@link #commit} from <code>apply()</code>.
         */
        public void apply() {
            Log.d(TAG,"Send value,size:"+values.size());
            context.getContentResolver().insert(getContentUri(context, KEY, TYPE), values);
        }

        /**
         * The Same with {@link #apply()}
         */
        public boolean commit() {
            apply();
            return true;
        }


        /**
         * Mark in the editor to remove <em>all</em> values from the
         * preferences.  Once commit is called, the only remaining preferences
         * will be any that you have defined in this editor.
         *
         * <p>Note that when committing back to the preferences, the clear
         * is done first, regardless of whether you called clear before
         * or after put methods on this editor.
         *
         * Add：Call content provider method immediately. apply or commit is not required for this case
         * So it's sync method.
         *
         * @return Returns a reference to the same Editor object, so you can
         * chain put calls together.
         */
        public Editor clear() {
            context.getContentResolver().delete(getContentUri(context, KEY, TYPE), null, null);
            return this;
        }

        /**Not implement . DO NOT call any time.**/
        @Override @Deprecated
        public SharedPreferences.Editor putStringSet(String key, Set<String> values) {
            throw new UnsupportedOperationException();
        }
    }


    /**
     * Retrieve a String value from the preferences.
     *
     * @param key The name of the preference to retrieve.
     * @param defValue Value to return if this preference does not exist.
     *
     * @return Returns the preference value if it exists, or defValue.  Throws
     * ClassCastException if there is a preference with this name that is not
     * a String.
     *
     * @throws ClassCastException
     */
    @Nullable
    @Override
    public String getString(String key, String defValue) {
        Cursor cursor = context.getContentResolver().query(getContentUri(context, key, STRING_TYPE), null, null, null, null);
        return getStringValue(cursor, defValue);
    }


    /**
     * Retrieve an int value from the preferences.
     *
     * @param key      The name of the preference to retrieve.
     * @param defValue Value to return if this preference does not exist.
     * @return Returns the preference value if it exists, or defValue.  Throws
     * ClassCastException if there is a preference with this name that is not
     * an int.
     * @throws ClassCastException
     */
    @Override
    public int getInt(String key, int defValue) {
        Cursor cursor = context.getContentResolver().query(getContentUri(context, key, INT_TYPE), null, null, null, null);
        return getIntValue(cursor, defValue);
    }

    /**
     * Retrieve a long value from the preferences.
     *
     * @param key The name of the preference to retrieve.
     * @param defValue Value to return if this preference does not exist.
     *
     * @return Returns the preference value if it exists, or defValue.  Throws
     * ClassCastException if there is a preference with this name that is not
     * a long.
     *
     * @throws ClassCastException
     */
    @Override
    public long getLong(String key, long defValue) {
        Cursor cursor = context.getContentResolver().query(getContentUri(context, key, LONG_TYPE), null, null, null, null);
        return getLongValue(cursor, defValue);
    }

    /**
     * Retrieve a float value from the preferences.
     *
     * @param key The name of the preference to retrieve.
     * @param defValue Value to return if this preference does not exist.
     *
     * @return Returns the preference value if it exists, or defValue.  Throws
     * ClassCastException if there is a preference with this name that is not
     * a float.
     *
     * @throws ClassCastException
     */
    @Override
    public float getFloat(String key, float defValue) {
        Cursor cursor = context.getContentResolver().query(getContentUri(context, key, FLOAT_TYPE), null, null, null, null);
        return getFloatValue(cursor, defValue);
    }

    /**
     * Retrieve a boolean value from the preferences.
     *
     * @param key      The name of the preference to retrieve.
     * @param defValue Value to return if this preference does not exist.
     * @return Returns the preference value if it exists, or defValue.  Throws
     * ClassCastException if there is a preference with this name that is not
     * a boolean.
     * @throws ClassCastException
     */
    @Override
    public boolean getBoolean(String key, boolean defValue) {
        Cursor cursor = context.getContentResolver().query(getContentUri(context, key, BOOLEAN_TYPE), null, null, null, null);
        return getBooleanValue(cursor, defValue);
    }


    /**
     * Create a new Editor for these preferences, through which you can make
     * modifications to the data in the preferences and atomically commit those
     * changes back to the SharedPreferences object.
     * <p>
     * <p>Note that you <em>must</em> call {@link Editor#commit} to have any
     * changes you perform in the Editor actually show up in the
     * SharedPreferences.
     *
     * @return Returns a new instance of the {@link Editor} interface, allowing
     * you to modify the values in this SharedPreferences object.
     */
    @Override
    public SharedPreferences.Editor edit() {
        return new EditorImp(context);
    }

    /**
     * Registers a callback to be invoked when a change happens to a preference.
     *
     * <p class="caution"><strong>Caution:</strong> The preference manager does
     * not currently store a strong reference to the listener. You must store a
     * strong reference to the listener, or it will be susceptible to garbage
     * collection. We recommend you keep a reference to the listener in the
     * instance data of an object that will exist as long as you need the
     * listener.</p>
     *
     * @param listener The callback that will run.
     * @see #unregisterOnSharedPreferenceChangeListener
     */
    @Override
    public void registerOnSharedPreferenceChangeListener(OnSharedPreferenceChangeListener listener) {
        changeListeners.add(listener);
    }


    /**
     * Unregisters a previous callback.
     *
     * @param listener The callback that should be unregistered.
     * @see #registerOnSharedPreferenceChangeListener
     */
    @Override
    public void unregisterOnSharedPreferenceChangeListener(OnSharedPreferenceChangeListener listener) {
        changeListeners.remove(listener);
    }


    private ContentObserver contentObserver = new ContentObserver(new Handler(Looper.getMainLooper())) {
        /**
         * This method is called when a content change occurs.
         * Includes the changed content Uri when available.
         * <p>
         * Subclasses should override this method to handle content changes.
         * To ensure correct operation on older versions of the framework that
         * did not provide a Uri argument, applications should also implement
         * the {@link #onChange(boolean)} overload of this method whenever they
         * implement the {@link #onChange(boolean, Uri)} overload.
         * </p><p>
         * Example implementation:
         * <pre><code>
         * // Implement the onChange(boolean) method to delegate the change notification to
         * // the onChange(boolean, Uri) method to ensure correct operation on older versions
         * // of the framework that did not have the onChange(boolean, Uri) method.
         * {@literal @Override}
         * public void onChange(boolean selfChange) {
         *     onChange(selfChange, null);
         * }
         *
         * // Implement the onChange(boolean, Uri) method to take advantage of the new Uri argument.
         * {@literal @Override}
         * public void onChange(boolean selfChange, Uri uri) {
         *     // Handle change.
         * }
         * </code></pre>
         * </p>
         *
         * @param selfChange True if this is a self-change notification.
         * @param uri        The Uri of the changed content, or null if unknown.
         */
        @Override
        public void onChange(boolean selfChange, Uri uri) {
            super.onChange(selfChange, uri);
            for(OnSharedPreferenceChangeListener listener : changeListeners){
                listener.onSharedPreferenceChanged(sharedPreferences,"Loglevel");
            }
        }
    };


    /****************************************************************************************************
                                                                     Static helper method
    ******************************************************************************************************/
    private Uri getContentUri(Context context, String key, String type) {
        if (BASE_URI == null) {
            init(context);
        }
        return BASE_URI.buildUpon().appendPath(key).appendPath(type).build();
    }


    private static String getStringValue(Cursor cursor, String def) {
        if (cursor == null)
            return def;
        String value = def;
        if (cursor.moveToFirst()) {
            value = cursor.getString(0);
        }
        cursor.close();
        return value;
    }

    private static boolean getBooleanValue(Cursor cursor, boolean def) {
        if (cursor == null)
            return def;
        boolean value = def;
        if (cursor.moveToFirst()) {
            value = cursor.getInt(0) > 0;
        }
        cursor.close();
        return value;
    }

    private static int getIntValue(Cursor cursor, int def) {
        if (cursor == null)
            return def;
        int value = def;
        if (cursor.moveToFirst()) {
            value = cursor.getInt(0);
        }
        cursor.close();
        return value;
    }

    private static long getLongValue(Cursor cursor, long def) {
        if (cursor == null)
            return def;
        long value = def;
        if (cursor.moveToFirst()) {
            value = cursor.getLong(0);
        }
        cursor.close();
        return value;
    }

    private static float getFloatValue(Cursor cursor, float def) {
        if (cursor == null)
            return def;
        float value = def;
        if (cursor.moveToFirst()) {
            value = cursor.getFloat(0);
        }
        cursor.close();
        return value;
    }


    /****************************************************************************************************
      Deprecated method , this class is not really SharedPreference .
     ******************************************************************************************************/
    /**Not implement . DO NOT call any time.**/
    @Override @Deprecated
    public Map<String, ?> getAll() {
        throw new UnsupportedOperationException();
    }
    /**Not implement . DO NOT call any time.**/
    @Nullable @Override @Deprecated
    public Set<String> getStringSet(String key, Set<String> defValues) {
        throw new UnsupportedOperationException();
    }
    /**Not implement . DO NOT call any time.**/
    @Override @Deprecated
    public boolean contains(String key) {
        throw new UnsupportedOperationException();
    }
}
