/*
 * Copyright 2014 Google Inc. All rights reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.liessu.andex.content;

import android.content.ContentValues;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.UriMatcher;
import android.database.ContentObserver;
import android.database.Cursor;
import android.net.Uri;
import android.os.Handler;
import android.os.Looper;
import android.support.annotation.Nullable;
import android.util.Log;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * <p>A content resolver , like SharedPreferences , in multi process .
 */
public class SharedPreferencesResolver implements SharedPreferences {
    //Provider params
    static final String PREFERENCE_AUTHORITY = "com.liessu.andex.content.SharedPreferencesProvider";
    static final Uri BASE_URI = Uri.parse("content://" + PREFERENCE_AUTHORITY);
    private static final String TAG = "SharedPrefResolver";
    //Type names
    private static final String INT_TYPE = "integer";
    private static final String LONG_TYPE = "long";
    private static final String FLOAT_TYPE = "float";
    private static final String BOOLEAN_TYPE = "boolean";
    private static final String STRING_TYPE = "string";

    //Uris
    private static final String URI_ADD = "add";
    private static final String URI_DEL = "delete/key/*";
    private static final String URI_CONTAINS = "contains/key/*";
    private static final String URI_CLEAR = "clear";
    private static final String URI_UPDATE = "update";
    private static final String URI_QUERY = "query/key/*/default/*/type/*";
    private static final String URI_QUERY_ALL = "queryAll";
    private static final String URI_CHANGE = "change/key/*";
    private static final int MATCH_CHANGE = 0x01;

    private Context context;
    private UriMatcher uriMatcher;
    private SharedPreferences sharedPreferences;
    private List<OnSharedPreferenceChangeListener> changeListeners = new ArrayList<>();
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
            //change/key/*
            Log.d(TAG, "Onchange uri:" + uri.toString());
            switch (uriMatcher.match(uri)) {
                case MATCH_CHANGE:
                    for (OnSharedPreferenceChangeListener listener : changeListeners) {
                        listener.onSharedPreferenceChanged(sharedPreferences, uri.getPathSegments().get(2));
                    }
                    break;
                default:
                    throw new IllegalArgumentException("Unsupported uri " + uri);
            }


        }

        /**
         * This method is called when a content change occurs.
         * <p>
         * Subclasses should override this method to handle content changes.
         * </p>
         *
         * @param selfChange True if this is a self-change notification.
         */
        @Override
        public void onChange(boolean selfChange) {
            onChange(selfChange, null);
        }
    };


    /**
     * Create SharedPreferencesResolver instance .
     *
     * @param context context of application .
     */
    public SharedPreferencesResolver(Context context) {
        this.context = context;
        this.sharedPreferences = this;

        uriMatcher = new UriMatcher(UriMatcher.NO_MATCH);
        uriMatcher.addURI(PREFERENCE_AUTHORITY, URI_CHANGE, MATCH_CHANGE);
        context.getContentResolver().registerContentObserver(BASE_URI, true, contentObserver);
    }


    /****************************************************************************************************
     Static helper method
     ******************************************************************************************************/

    private static Map<String , ?> getAllValue(Cursor cursor){
        if(cursor == null || cursor.isClosed())
            return null;

        Map<String , String > mapAll = new HashMap<>();
        if(cursor.moveToFirst()){
            while (!cursor.isAfterLast()){
                mapAll.put(cursor.getString(0),cursor.getString(1));
                cursor.moveToNext();
            }
        }
        Log.d(TAG , mapAll.toString());
        return mapAll;
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

    /**
     * Retrieve a String value from the preferences.
     *
     * @param key      The name of the preference to retrieve.
     * @param defValue Value to return if this preference does not exist.
     * @return Returns the preference value if it exists, or defValue.  Throws
     * ClassCastException if there is a preference with this name that is not
     * a String.
     * @throws ClassCastException
     */
    @Nullable
    @Override
    public String getString(String key, String defValue) {
        String path = getUriPath(URI_QUERY, key, defValue, STRING_TYPE);
        Log.d(TAG, "getString , path = " + path);
        Cursor cursor = context.getContentResolver().query(BASE_URI.buildUpon().path(path).build(), null, null, null, null);
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
        String path = getUriPath(URI_QUERY, key, String.valueOf(defValue), INT_TYPE);
        Log.d(TAG, "getInt , path = " + path);

        Cursor cursor = context.getContentResolver().query(BASE_URI.buildUpon().path(path).build(), null, null, null, null);
        return getIntValue(cursor, defValue);
    }

    /**
     * Retrieve a long value from the preferences.
     *
     * @param key      The name of the preference to retrieve.
     * @param defValue Value to return if this preference does not exist.
     * @return Returns the preference value if it exists, or defValue.  Throws
     * ClassCastException if there is a preference with this name that is not
     * a long.
     * @throws ClassCastException
     */
    @Override
    public long getLong(String key, long defValue) {
        String path = getUriPath(URI_QUERY, key, String.valueOf(defValue), LONG_TYPE);
        Log.d(TAG, "getLong , path = " + path);

        Cursor cursor = context.getContentResolver().query(BASE_URI.buildUpon().path(path).build(), null, null, null, null);
        return getLongValue(cursor, defValue);
    }

    /**
     * Retrieve a float value from the preferences.
     *
     * @param key      The name of the preference to retrieve.
     * @param defValue Value to return if this preference does not exist.
     * @return Returns the preference value if it exists, or defValue.  Throws
     * ClassCastException if there is a preference with this name that is not
     * a float.
     * @throws ClassCastException
     */
    @Override
    public float getFloat(String key, float defValue) {
        String path = getUriPath(URI_QUERY, key, String.valueOf(defValue), FLOAT_TYPE);
        Log.d(TAG, "getFloat , path = " + path);

        Cursor cursor = context.getContentResolver().query(BASE_URI.buildUpon().path(path).build(), null, null, null, null);
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
        String path = getUriPath(URI_QUERY, key, String.valueOf(defValue), BOOLEAN_TYPE);
        Log.d(TAG, "getBoolean , path = " + path);

        Cursor cursor = context.getContentResolver().query(BASE_URI.buildUpon().path(path).build(), null, null, null, null);
        return getBooleanValue(cursor, defValue);
    }

    /**
     * Create a new Editor for these preferences, through which you can make
     * modifications to the data in the preferences and atomically commit those
     * changes back to the SharedPreferences object.
     * <p/>
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
     * Checks whether the preferences contains a preference.
     *
     * @param key The name of the preference to check.
     * @return Returns true if the preference exists in the preferences,
     *         otherwise false.
     */
    @Override
    @Deprecated
    public boolean contains(String key) {
        String path = getUriPath(URI_CONTAINS, key);
        Log.d(TAG, "contains , path = " + path);

        Cursor cursor = context.getContentResolver().query(BASE_URI.buildUpon().path(path).build(), null, null, null, null);
        return getBooleanValue(cursor, false);
    }

    /**
     * Registers a callback to be invoked when a change happens to a preference.
     * <p/>
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

    /**
     * Create Uri
     *
     * @param path uri path
     * @param args  param list
     * @return path
     */
    private String getUriPath(String path, String... args) {
        for (String arg : args) {
            path = path.replaceFirst("\\*", arg);
        }
        return path;
    }

    /**
     * Retrieve all values from the preferences.
     *
     * <p>Note that you <em>must not</em> modify the collection returned
     * by this method, or alter any of its contents.  The consistency of your
     * stored data is not guaranteed if you do.
     *
     * @return Returns a map containing a list of pairs key/value representing
     * the preferences.
     *
     * @throws NullPointerException
     */
    @Deprecated
    public Map<String, ?> getAll() {
        String path = getUriPath(URI_QUERY_ALL);
        Log.d(TAG, "getAll , path = " + path);

        Cursor cursor = context.getContentResolver().query(BASE_URI.buildUpon().path(path).build(), null, null, null, null);
        return getAllValue(cursor);
    }


    /**
     * Not implement . DO NOT call any time.
     **/
    @Nullable
    @Override
    @Deprecated
    public Set<String> getStringSet(String key, Set<String> defValues) {
        throw new UnsupportedOperationException();
    }


    /****************************************************************************************************
     * class Editor implements SharedPreferences.Editor
     ******************************************************************************************************/
    public class EditorImp implements SharedPreferences.Editor {
        private Context context;
        private ContentValues values = new ContentValues();

        private EditorImp(Context context) {
            this.context = context;
        }

        /**
         * Set a String value in the preferences editor, to be written back once
         * {@link #commit} or {@link #apply} are called.
         *
         * @param key   The name of the preference to modify.
         * @param value The new value for the preference.
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
         * @param key   The name of the preference to modify.
         * @param value The new value for the preference.
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
         * @param key   The name of the preference to modify.
         * @param value The new value for the preference.
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
         * @param key   The name of the preference to modify.
         * @param value The new value for the preference.
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
         * @param key   The name of the preference to modify.
         * @param value The new value for the preference.
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
         * <p/>
         * <p>Note that when committing back to the preferences, all removals
         * are done first, regardless of whether you called remove before
         * or after put methods on this editor.
         *
         * @param key The name of the preference to remove.
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
         * <p/>
         * <p>Note that when two editors are modifying preferences at the same
         * time, the last one to call apply wins.
         * <p/>
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
         * <p/>
         * <p>As {@link SharedPreferences} instances are singletons within
         * a process, it's safe to replace any instance of {@link #commit} with
         * {@link #apply} if you were already ignoring the return value.
         * <p/>
         * <p>You don't need to worry about Android component
         * lifecycles and their interaction with <code>apply()</code>
         * writing to disk.  The framework makes sure in-flight disk
         * writes from <code>apply()</code> complete before switching
         * states.
         * <p/>
         * <p class='note'>The SharedPreferences.Editor interface
         * isn't expected to be implemented directly.  However, if you
         * previously did implement it and are now getting errors
         * about missing <code>apply()</code>, you can simply call
         * {@link #commit} from <code>apply()</code>.
         */
        public void apply() {
            Log.d(TAG, "Apply value,size:" + values.size());
            Log.d(TAG, "Insert uri : " + BASE_URI.buildUpon().path(URI_ADD).build().toString());
            context.getContentResolver().insert(BASE_URI.buildUpon().path(URI_ADD).build(), values);
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
         * <p/>
         * <p>Note that when committing back to the preferences, the clear
         * is done first, regardless of whether you called clear before
         * or after put methods on this editor.
         * <p/>
         * Add：Call content provider method immediately. apply or commit is not required for this case
         * So it's sync method.
         *
         * @return Returns a reference to the same Editor object, so you can
         * chain put calls together.xxxx
         */
        public Editor clear() {
            Log.d(TAG, "Clear uri : " + BASE_URI.buildUpon().path(URI_CLEAR).build().toString());
            context.getContentResolver().delete(BASE_URI.buildUpon().path(URI_CLEAR).build(), null, null);
            return this;
        }

        /**
         * Not implement . DO NOT call any time.
         **/
        @Override
        @Deprecated
        public SharedPreferences.Editor putStringSet(String key, Set<String> values) {
            throw new UnsupportedOperationException();
        }
    }
}
