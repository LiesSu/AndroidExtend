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
 * client process/app . Then , you will use SharedPreferencesResolver as same as SharedPreferences .
 *
 * <p>NOTE : In server process/app , use SharedPreferences batter than SharedPreferencesResolver.
 */
public class SharedPreferencesProvider extends ContentProvider
        implements SharedPreferences.OnSharedPreferenceChangeListener{
    private static final String TAG = "SharedPrefProvider";
    private static final String INT_TYPE = "integer";
    private static final String LONG_TYPE = "long";
    private static final String FLOAT_TYPE = "float";
    private static final String BOOLEAN_TYPE = "boolean";
    private static final String STRING_TYPE = "string";
    private static final int MATCH_DATA = 0x010000;
    /**Content provider authority**/
    public static String PREFERENCE_AUTHORITY;
    /**The name of  shared preferences file , <i>public static member</i>**/
    public static String SHARED_FILE_NAME = "SharedContent";
    private static UriMatcher uriMatcher;
    private SharedPreferences sharedPreferences;
    private Context context;
    public static Uri BASE_URI;

    public static String getAuthority() {
        return PREFERENCE_AUTHORITY;
    }

    public static void setAuthority(String Authority) {
        PREFERENCE_AUTHORITY = Authority;
    }

    private void onCreate(Context context) {
        this.context = context;
        PREFERENCE_AUTHORITY = getClass().getName();
        BASE_URI = Uri.parse("content://" + PREFERENCE_AUTHORITY);
        uriMatcher = new UriMatcher(UriMatcher.NO_MATCH);
        uriMatcher.addURI(PREFERENCE_AUTHORITY, "*/*", MATCH_DATA);

        sharedPreferences = context.getSharedPreferences(SHARED_FILE_NAME , Context.MODE_PRIVATE);
        sharedPreferences.registerOnSharedPreferenceChangeListener(this);
    }

    @Override
    public boolean onCreate() {
        if (uriMatcher == null) {
            onCreate(getContext());
        }
        return true;
    }


    /**
     * Implement this to handle query requests from clients with support for cancellation.
     * This method can be called from multiple threads, as described in
     * <a href="{@docRoot}guide/topics/fundamentals/processes-and-threads.html#Threads">Processes
     * and Threads</a>.
     * <p>
     * Example client call:<p>
     * <pre>// Request a specific record.
     * Cursor managedCursor = managedQuery(
     ContentUris.withAppendedId(Contacts.People.CONTENT_URI, 2),
     projection,    // Which columns to return.
     null,          // WHERE clause.
     null,          // WHERE clause value substitution
     People.NAME + " ASC");   // Sort order.</pre>
     * Example implementation:<p>
     * <pre>// SQLiteQueryBuilder is a helper class that creates the
     // proper SQL syntax for us.
     SQLiteQueryBuilder qBuilder = new SQLiteQueryBuilder();

     // Set the table we're querying.
     qBuilder.setTables(DATABASE_TABLE_NAME);

     // If the query ends in a specific record number, we're
     // being asked for a specific record, so set the
     // WHERE clause in our query.
     if((URI_MATCHER.match(uri)) == SPECIFIC_MESSAGE){
     qBuilder.appendWhere("_id=" + uri.getPathLeafId());
     }

     // Make the query.
     Cursor c = qBuilder.query(mDb,
     projection,
     selection,
     selectionArgs,
     groupBy,
     having,
     sortOrder);
     c.setNotificationUri(getContext().getContentResolver(), uri);
     return c;</pre>
     * <p>
     * If you implement this method then you must also implement the version of
     * {@link #query(Uri, String[], String, String[], String)} that does not take a cancellation
     * signal to ensure correct operation on older versions of the Android Framework in
     * which the cancellation signal overload was not available.
     *
     * @param uri The URI to query. This will be the full URI sent by the client;
     *      if the client is requesting a specific record, the URI will end in a record number
     *      that the implementation should parse and add to a WHERE or HAVING clause, specifying
     *      that _id value.
     * @param projection The list of columns to put into the cursor. If
     *      {@code null} all columns are included.
     * @param selection A selection criteria to apply when filtering rows.
     *      If {@code null} then all rows are included.
     * @param selectionArgs You may include ?s in selection, which will be replaced by
     *      the values from selectionArgs, in order that they appear in the selection.
     *      The values will be bound as Strings.
     * @param sortOrder How the rows in the cursor should be sorted.
     *      If {@code null} then the provider is free to define the sort order.
     * If the operation is canceled, then {@link OperationCanceledException} will be thrown
     * when the query is executed.
     * @return a Cursor or {@code null}.
     */
    @Nullable
    @Override
    public Cursor query(@NonNull Uri uri, String[] projection, String selection, String[] selectionArgs, String sortOrder) {
        MatrixCursor cursor = null;
        switch (uriMatcher.match(uri)) {
            case MATCH_DATA:
                final String key = uri.getPathSegments().get(0);
                final String type = uri.getPathSegments().get(1);
                cursor = new MatrixCursor(new String[]{key});
                if (!sharedPreferences.contains(key))
                    return cursor;
                MatrixCursor.RowBuilder rowBuilder = cursor.newRow();
                Object object = null;
                if (STRING_TYPE.equals(type)) {
                    object = sharedPreferences.getString(key, null);
                } else if (BOOLEAN_TYPE.equals(type)) {
                    object = sharedPreferences.getBoolean(key, false) ? 1 : 0;
                } else if (LONG_TYPE.equals(type)) {
                    object = sharedPreferences.getLong(key, 0L);
                } else if (INT_TYPE.equals(type)) {
                    object = sharedPreferences.getInt(key, 0);
                } else if (FLOAT_TYPE.equals(type)) {
                    object = sharedPreferences.getFloat(key, 0f);
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
     * Implement this to handle requests for the MIME type of the data at the
     * given URI.  The returned MIME type should start with
     * <code>vnd.android.cursor.item</code> for a single record,
     * or <code>vnd.android.cursor.dir/</code> for multiple items.
     * This method can be called from multiple threads, as described in
     * <a href="{@docRoot}guide/topics/fundamentals/processes-and-threads.html#Threads">Processes
     * and Threads</a>.
     *
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
     * Implement this to handle requests to insert a new row.
     * As a courtesy, call {@link ContentResolver#notifyChange(android.net.Uri ,android.database.ContentObserver) notifyChange()}
     * after inserting.
     * This method can be called from multiple threads, as described in
     * <a href="{@docRoot}guide/topics/fundamentals/processes-and-threads.html#Threads">Processes
     * and Threads</a>.
     * @param uri The content:// URI of the insertion request. This must not be {@code null}.
     * @param values A set of column_name/value pairs to add to the database.
     *     This must not be {@code null}.
     * @return The URI for the newly inserted item.
     */
    @Nullable
    @Override
    public Uri insert(@NonNull Uri uri, ContentValues values) {
        Log.e(TAG , "insert uri : "+uri.toString());
        switch (uriMatcher.match(uri)) {
            case MATCH_DATA:
                SharedPreferences.Editor editor = sharedPreferences.edit();
                for (Map.Entry<String, Object> entry : values.valueSet()) {
                    final Object value = entry.getValue();
                    final String key = entry.getKey();
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
                if (Build.VERSION.SDK_INT > Build.VERSION_CODES.FROYO) {
                    editor.apply();
                } else {
                    editor.commit();
                }
                break;
            default:
                throw new IllegalArgumentException("Unsupported uri " + uri);
        }

        context.getContentResolver().notifyChange(uri , null);
        return null;
    }


    /**
     * Implement this to handle requests to delete one or more rows.
     * The implementation should apply the selection clause when performing
     * deletion, allowing the operation to affect multiple rows in a directory.
     * As a courtesy, call {@link ContentResolver#notifyChange(android.net.Uri ,android.database.ContentObserver) notifyChange()}
     * after deleting.
     * This method can be called from multiple threads, as described in
     * <a href="{@docRoot}guide/topics/fundamentals/processes-and-threads.html#Threads">Processes
     * and Threads</a>.
     *
     * <p>The implementation is responsible for parsing out a row ID at the end
     * of the URI, if a specific row is being deleted. That is, the client would
     * pass in <code>content://contacts/people/22</code> and the implementation is
     * responsible for parsing the record number (22) when creating a SQL statement.
     *
     * @param uri The full URI to query, including a row ID (if a specific record is requested).
     * @param selection An optional restriction to apply to rows when deleting.
     * @return The number of rows affected.
     */
    @Override
    public int delete(@NonNull Uri uri, String selection, String[] selectionArgs) {
        switch (uriMatcher.match(uri)) {
            case MATCH_DATA:
                sharedPreferences.edit().clear().commit();
                break;
            default:
                throw new IllegalArgumentException("Unsupported uri " + uri);
        }

        context.getContentResolver().notifyChange(uri , null);
        return 0;
    }

    /**
     * Implement this to handle requests to update one or more rows.
     * The implementation should update all rows matching the selection
     * to set the columns according to the provided values map.
     * As a courtesy, call {@link ContentResolver#notifyChange(android.net.Uri ,android.database.ContentObserver) notifyChange()}
     * after updating.
     * This method can be called from multiple threads, as described in
     * <a href="{@docRoot}guide/topics/fundamentals/processes-and-threads.html#Threads">Processes
     * and Threads</a>.
     *
     * @param uri The URI to query. This can potentially have a record ID if this
     * is an update request for a specific record.
     * @param values A set of column_name/value pairs to update in the database.
     *     This must not be {@code null}.
     * @param selection An optional filter to match rows to update.
     * @return the number of rows affected.
     */
    @Override
    public int update(@NonNull Uri uri, ContentValues values, String selection, String[] selectionArgs) {
        throw new UnsupportedOperationException();
    }

    /**
     * Called when a shared preference is changed, added, or removed. This
     * may be called even if a preference is set to its existing value.
     * <p>
     * <p>This callback will be run on your main thread.
     *
     * @param sharedPreferences The {@link SharedPreferences} that received
     *                          the change.
     * @param key               The key of the preference that was changed, added, or
     */
    @Override
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
        Log.e(TAG , "onSharedPreferenceChanged , key ="+key);
    }

    private Uri getContentUri(Context context, String key, String type) {
        if (BASE_URI == null) {
            onCreate(context);
        }
        return BASE_URI.buildUpon().appendPath(key).appendPath(type).build();
    }
}
