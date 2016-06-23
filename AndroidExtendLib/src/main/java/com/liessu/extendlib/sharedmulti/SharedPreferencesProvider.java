package com.liessu.extendlib.sharedmulti;

import android.content.ContentProvider;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.MatrixCursor;
import android.net.Uri;
import android.os.Build;
import android.os.OperationCanceledException;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;

import java.util.Map;

/**
 * <p>A content provider for SharedPreferences  in multi process .
 * <p>Import SharedPreferencesProvider in your  server process/app , SharedPreferencesResolver in your
 * client process/app . Then , use SharedPreferencesResolver as same as SharedPreferences .
 * <p/>
 * <p>NOTE : In server process/app , use SharedPreferences batter than SharedPreferencesResolver.
 */
public class SharedPreferencesProvider extends ContentProvider
        implements SharedPreferences.OnSharedPreferenceChangeListener {
    //Provider params
    static final String PREFERENCE_AUTHORITY = "com.liessu.extendlib.sharedmulti.SharedPreferencesProvider";
    static final Uri BASE_URI = Uri.parse("content://" + PREFERENCE_AUTHORITY);
    private static final String TAG = "SharedPrefProvider";
    public static String SHARED_FILE_NAME = "SharedMultiPreferences";

    //Type names
    private static final String INT_TYPE = "integer";
    private static final String LONG_TYPE = "long";
    private static final String FLOAT_TYPE = "float";
    private static final String BOOLEAN_TYPE = "boolean";
    private static final String STRING_TYPE = "string";

    //Uris
    private static final String URI_ADD = "add";
    private static final String URI_DEL = "delete/key/*";
    private static final String URI_CLEAR = "clear";
    private static final String URI_UPDATE = "update";
    private static final String URI_QUERY = "query/key/*/default/*/type/*";
    private static final String URI_CHANGE = "change/key/*";

    //Uri matcher
    private static final int MATCH_DATA = 0x01;
    private static final int MATCH_ADD = 0x02;
    private static final int MATCH_DEL = 0x03;
    private static final int MATCH_CLEAR = 0x04;
    private static final int MATCH_UPDATE = 0x05;
    private static final int MATCH_QUERY = 0x06;

    private Context context;
    private UriMatcher uriMatcher;
    private SharedPreferences sharedPreferences;

    /**
     * Receive authority .
     */
    public static String getAuthority() {
        return PREFERENCE_AUTHORITY;
    }

    @Override
    public boolean onCreate() {
        if (uriMatcher == null) {
            context = getContext();
            //Construct UriMatcher
            Log.d(TAG,"Authority is "+PREFERENCE_AUTHORITY);
            uriMatcher = new UriMatcher(UriMatcher.NO_MATCH);
            uriMatcher.addURI(PREFERENCE_AUTHORITY, URI_ADD, MATCH_ADD);
            uriMatcher.addURI(PREFERENCE_AUTHORITY, URI_CLEAR, MATCH_CLEAR);
            uriMatcher.addURI(PREFERENCE_AUTHORITY, URI_UPDATE, MATCH_UPDATE);
            uriMatcher.addURI(PREFERENCE_AUTHORITY, URI_DEL, MATCH_DEL);
            uriMatcher.addURI(PREFERENCE_AUTHORITY, URI_QUERY, MATCH_QUERY);

            // To subscribe to SharedPreferences changes state
            sharedPreferences = context.getSharedPreferences(SHARED_FILE_NAME, Context.MODE_PRIVATE);
            sharedPreferences.registerOnSharedPreferenceChangeListener(this);
        }

        return true;
    }

    /**
     * Handle query requests from clients with support for cancellation.
     * This method can be called from multiple threads, as described in
     * <a href="{@docRoot}guide/topics/fundamentals/processes-and-threads.html#Threads">Processes
     * and Threads</a>.
     *
     * @param uri           The URI to query. This will be the full URI sent by the client;
     *                      if the client is requesting a specific record, the URI will end in a record number
     *                      that the implementation should parse and add to a WHERE or HAVING clause, specifying
     *                      that _id value.
     * @param projection    The list of columns to put into the cursor. If
     *                      {@code null} all columns are included.
     * @param selection     A selection criteria to apply when filtering rows.
     *                      If {@code null} then all rows are included.
     * @param selectionArgs You may include ?s in selection, which will be replaced by
     *                      the values from selectionArgs, in order that they appear in the selection.
     *                      The values will be bound as Strings.
     * @param sortOrder     How the rows in the cursor should be sorted.
     *                      If {@code null} then the provider is free to define the sort order.
     *                      If the operation is canceled, then {@link OperationCanceledException} will be thrown
     *                      when the query is executed.
     * @return a Cursor or {@code null}.
     */
    @Nullable
    @Override
    public Cursor query(@NonNull Uri uri, String[] projection, String selection, String[] selectionArgs, String sortOrder) {
        //  content://PREFERENCE_AUTHORITY/query/key/#/default/#/type/#

        Log.d(TAG, "Query uri : " + uri.toString());
        MatrixCursor cursor;
        switch (uriMatcher.match(uri)) {
            case MATCH_QUERY:
                //Receive query key and type , type is needed.
                final String key = uri.getPathSegments().get(2);
                final String defaultValue = uri.getPathSegments().get(4);
                final String type = uri.getPathSegments().get(6);

                Log.d(TAG, "Query data , key:" + key + " type:" + type);
                cursor = new MatrixCursor(new String[]{key});
                //Shared preferences isn't contain key , return null .
                if (!sharedPreferences.contains(key))
                    return cursor;

                Object object;
                MatrixCursor.RowBuilder rowBuilder = cursor.newRow();
                //Save value in Object instance
                if (STRING_TYPE.equals(type)) {
                    object = sharedPreferences.getString(key, defaultValue);
                } else if (BOOLEAN_TYPE.equals(type)) {
                    object = sharedPreferences.getBoolean(key, Boolean.valueOf(defaultValue)) ? 1 : 0;
                } else if (LONG_TYPE.equals(type)) {
                    object = sharedPreferences.getLong(key, Long.valueOf(defaultValue));
                } else if (INT_TYPE.equals(type)) {
                    object = sharedPreferences.getInt(key, Integer.valueOf(defaultValue));
                } else if (FLOAT_TYPE.equals(type)) {
                    object = sharedPreferences.getFloat(key, Float.valueOf(defaultValue));
                } else {
                    throw new IllegalArgumentException("Unsupported type " + uri);
                }
                rowBuilder.add(object);
                break;
            default:
                throw new IllegalArgumentException("Unsupported uri " + uri);
        }
        return cursor;
    }

    /**
     * Handle requests to insert a new row.
     * As a courtesy, call {@link ContentResolver#notifyChange(android.net.Uri, android.database.ContentObserver) notifyChange()}
     * after inserting.
     * This method can be called from multiple threads, as described in
     * <a href="{@docRoot}guide/topics/fundamentals/processes-and-threads.html#Threads">Processes
     * and Threads</a>.
     *
     * @param uri    The content:// URI of the insertion request. This must not be {@code null}.
     * @param values A set of column_name/value pairs to add to the database.
     *               This must not be {@code null}.
     * @return The URI for the newly inserted item.
     */
    @Nullable
    @Override
    public Uri insert(@NonNull Uri uri, ContentValues values) {
        //content://PREFERENCE_AUTHORITY/add

        Log.d(TAG, "Insert uri : " + uri.toString());
        switch (uriMatcher.match(uri)) {
            case MATCH_ADD:
                SharedPreferences.Editor editor = sharedPreferences.edit();
                //Insert/update all key-value map from ContentValues to SharedPreferences.
                for (Map.Entry<String, Object> entry : values.valueSet()) {
                    final String key = entry.getKey();
                    final Object value = entry.getValue();
                    //if value is null , remove key.
                    if (value == null) {
                        editor.remove(key);
                    } else if (value instanceof String)
                        editor.putString(key, (String) value);
                    else if (value instanceof Boolean)
                        editor.putBoolean(key, (Boolean) value);
                    else if (value instanceof Long)
                        editor.putLong(key, (Long) value);
                    else if (value instanceof Integer)
                        editor.putInt(key, (Integer) value);
                    else if (value instanceof Float)
                        editor.putFloat(key, (Float) value);
                    else {
                        throw new IllegalArgumentException("Unsupported type " + uri);
                    }
                }

                //Judge running Android devices version to apply or commit changes.
                if (Build.VERSION.SDK_INT > Build.VERSION_CODES.FROYO) {
                    editor.apply();
                } else {
                    editor.commit();
                }
                break;
            default:
                throw new IllegalArgumentException("Unsupported uri " + uri);
        }

        return null;
    }

    /**
     * Handle requests to update one or more rows.
     * This method can be called from multiple threads, as described in
     * <a href="{@docRoot}guide/topics/fundamentals/processes-and-threads.html#Threads">Processes
     * and Threads</a>.
     *
     * @param uri       The URI to query. This can potentially have a record ID if this
     *                  is an update request for a specific record.
     * @param values    A set of column_name/value pairs to update in the database.
     *                  This must not be {@code null}.
     * @param selection An optional filter to match rows to update.
     * @return the number of rows affected.
     * @see {@link #insert(Uri, ContentValues)}
     */
    @Override
    public int update(@NonNull Uri uri, ContentValues values, String selection, String[] selectionArgs) {
        //content://PREFERENCE_AUTHORITY/update

        Log.d(TAG, "Update uri : " + uri.toString());
        switch (uriMatcher.match(uri)) {
            case MATCH_UPDATE:
                SharedPreferences.Editor editor = sharedPreferences.edit();
                //Insert/update all key-value map from ContentValues to SharedPreferences.
                for (Map.Entry<String, Object> entry : values.valueSet()) {
                    final String key = entry.getKey();
                    final Object value = entry.getValue();
                    //if value is null , remove key.
                    if (value == null)
                        editor.remove(key);
                    else if (value instanceof String)
                        editor.putString(key, (String) value);
                    else if (value instanceof Boolean)
                        editor.putBoolean(key, (Boolean) value);
                    else if (value instanceof Long)
                        editor.putLong(key, (Long) value);
                    else if (value instanceof Integer)
                        editor.putInt(key, (Integer) value);
                    else if (value instanceof Float)
                        editor.putFloat(key, (Float) value);
                    else {
                        throw new IllegalArgumentException("Unsupported type " + uri);
                    }
                }

                //Judge running Android devices version to apply or commit changes.
                if (Build.VERSION.SDK_INT > Build.VERSION_CODES.FROYO) {
                    editor.apply();
                } else {
                    editor.commit();
                }
                break;
            default:
                throw new IllegalArgumentException("Unsupported uri " + uri);
        }

        return values.size();
    }

    /**
     * Handle requests to delete one or more rows.
     * This method can be called from multiple threads, as described in
     * <a href="{@docRoot}guide/topics/fundamentals/processes-and-threads.html#Threads">Processes
     * and Threads</a>.
     * <p/>
     * <p>The implementation is responsible for parsing out a row ID at the end
     * of the URI, if a specific row is being deleted. That is, the client would
     * pass in <code>content://contacts/people/22</code> and the implementation is
     * responsible for parsing the record number (22) when creating a SQL statement.
     *
     * @param uri       The full URI to query, including a row ID (if a specific record is requested).
     * @param selection An optional restriction to apply to rows when deleting.
     * @return The number of rows affected.
     */
    @Override
    public int delete(@NonNull Uri uri, String selection, String[] selectionArgs) {
        //putXxxx(key,null) also can remove key
        //content://PREFERENCE_AUTHORITY/remove
        //content://PREFERENCE_AUTHORITY/delete/key/#

        Log.d(TAG, "Clear SharedPreferences !");
        switch (uriMatcher.match(uri)) {
            case MATCH_CLEAR://Clear data
                Log.d(TAG, "Clear all data");
                int count = sharedPreferences.getAll().size();
                sharedPreferences.edit().clear().commit();
                return count;
            case MATCH_DEL://Delete one key
                String key = uri.getPathSegments().get(2);
                Log.d(TAG, "Delete data , key:" + key);
                //Shared preferences isn't contain key , return 0 .
                if (!sharedPreferences.contains(key))
                    return 0;

                //Judge running Android devices version to apply or commit changes.
                if (Build.VERSION.SDK_INT > Build.VERSION_CODES.FROYO) {
                    sharedPreferences.edit().remove(key).apply();
                } else {
                    sharedPreferences.edit().remove(key).commit();
                }
                return 1;
            default:
                throw new IllegalArgumentException("Unsupported uri " + uri);
        }

    }

    /**
     * Handle requests for the MIME type of the data at the
     * given URI.  The returned MIME type should start with
     * <code>vnd.android.cursor.item</code> for a single record,
     * or <code>vnd.android.cursor.dir/</code> for multiple items.
     * This method can be called from multiple threads, as described in
     * <a href="{@docRoot}guide/topics/fundamentals/processes-and-threads.html#Threads">Processes
     * and Threads</a>.
     * <p/>
     * <p>Note that there are no permissions needed for an application to
     * access this information; if your content provider requires read and/or
     * write permissions, or is not exported, all applications can still call
     * this method regardless of their access permissions.  This allows them
     * to retrieve the MIME type for a URI when dispatching intents.
     *
     * @param uri the URI to query.
     * @return a MIME type string, or {@code null} if there is no type.
     */
    @Nullable
    @Override
    public String getType(@NonNull Uri uri) {
        return ContentResolver.CURSOR_ITEM_BASE_TYPE + "/vnd." + PREFERENCE_AUTHORITY + ".item";
    }

    /**
     * Called when a shared preference is changed, added, or removed. This
     * may be called even if a preference is set to its existing value.
     * <p/>
     * <p>This callback will be run on your main thread.
     *
     * @param sharedPreferences The {@link SharedPreferences} that received
     *                          the change.
     * @param key               The key of the preference that was changed, added, or
     */
    @Override
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
        //content://PREFERENCE_AUTHORITY/change/key/#

        //Notify shared preferences change to resolver .
        Uri changeUri = BASE_URI.buildUpon().path(getUriPath(URI_CHANGE, key)).build();

        Log.d(TAG, "onSharedPreferenceChanged , changeUri =" + changeUri);
        context.getContentResolver().notifyChange(changeUri, null);
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

}
