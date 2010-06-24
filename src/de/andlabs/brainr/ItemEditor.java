package de.andlabs.brainr;


import android.app.Activity;
import android.content.ComponentName;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CursorTreeAdapter;
import android.widget.EditText;
import android.widget.TextView;

public class ItemEditor extends Activity {
    
    private static final String ORIGINAL_CONTENT = "origContent";

    // Identifiers for our menu items.
    private static final int REVERT_ID = Menu.FIRST;
    private static final int DISCARD_ID = Menu.FIRST + 1;
    private static final int DELETE_ID = Menu.FIRST + 2;

    // The different distinct states the activity can be run in.
    private static final int STATE_EDIT = 0;
    private static final int STATE_INSERT = 1;

    private static final String TAG = "ItemEditor";

    private int mState;
    private Uri mUri;
    private Cursor mCursor;
    private EditText mText;
    private String mOriginalContent;

    private CursorTreeAdapter mAdapter;

    private CardsView mCardsView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        final Intent intent = getIntent();

        final String action = intent.getAction();
        if (Intent.ACTION_EDIT.equals(action)) {
            mState = STATE_EDIT;
            mUri = intent.getData();
            
        } else if (Intent.ACTION_INSERT.equals(action)) {
            
            mState = STATE_INSERT;
            mUri = getContentResolver().insert(intent.getData(), null);

            if (mUri == null) {
                Log.e(TAG, "Failed to insert new note into " + getIntent().getData());
                finish();
                return;
            }

            setResult(RESULT_OK, (new Intent()).setAction(mUri.toString()));

        } else {
            Log.e(TAG, "Unknown action, exiting");
            finish();
            return;
        }

        setContentView(R.layout.item_editor);
        
        mText = (EditText) findViewById(R.id.text);

        mCursor = managedQuery(mUri, new String[] {"_id", "text"}, null, null, null);
        
        mCardsView = new CardsView(this);
        setContentView(mCardsView);

        if (savedInstanceState != null) {
            mOriginalContent = savedInstanceState.getString(ORIGINAL_CONTENT);
        }
        mAdapter = new CursorTreeAdapter(getContentResolver().query(Item.URI, 
                new String[]{"_id", "text"}, null, null, null), this) {
            
            @Override
                    public int getChildrenCount(int groupPosition) {
                        if (super.getChildrenCount(groupPosition) > mCardsView.mChildPosition) {
//                            setChildrenCursor(groupPosition, getContentResolver().query(Uri.withAppendedPath(Item.URI, "/"+groupCursor.getInt(0)+"/translations"), 
//                                                        new String[]{"items._id", "items.text"}, null, null, null));
                        }
                        return super.getChildrenCount(groupPosition);
                    }

            @Override
            protected View newGroupView(Context context, Cursor cursor, boolean isExpanded, ViewGroup parent) {
                View v = getLayoutInflater().inflate(R.layout.item_editor, parent);
                v.setBackgroundColor(Color.BLACK);
                return v;
            }
            
            @Override
            protected void bindGroupView(View view, Context context, Cursor cursor, boolean isExpanded) {
                Log.d(TAG, cursor.getString(1));
                ((TextView)view.findViewById(R.id.text)).setText(cursor.getString(1));
            }
            
            
            @Override
            protected Cursor getChildrenCursor(Cursor groupCursor) {
                return getContentResolver().query(Uri.withAppendedPath(Item.URI, "/"+groupCursor.getInt(0)+"/translations"), 
                        new String[]{"items._id", "items.text"}, null, null, null);
            }
            
            @Override
            protected View newChildView(Context context, Cursor cursor, boolean isLastChild, ViewGroup parent) {
                View v = getLayoutInflater().inflate(R.layout.item_editor, parent);
                v.setBackgroundColor(Color.BLACK);
                return v;
            }
            
            @Override
            protected void bindChildView(View view, Context context, Cursor cursor, boolean isLastChild) {
                ((TextView)view.findViewById(R.id.text)).setText(cursor.getString(1));
            }
        };
        mCardsView.setAdapter(mAdapter);
        //registerForContextMenu(mCardsView);
        mCardsView.setOnCreateContextMenuListener(this);
    }

    @Override
    protected void onResume() {
        super.onResume();

        if (mCursor != null) {

            mCursor.moveToFirst();

            if (mState == STATE_EDIT) {
                setTitle(getText(R.string.title_edit));
            } else if (mState == STATE_INSERT) {
                setTitle(getText(R.string.title_create));
            }

            String text = mCursor.getString(1);
            mText.setTextKeepState(text);
            
            if (mOriginalContent == null) {
                mOriginalContent = text;
            }

        } else {
            setTitle(getText(R.string.error_title));
            mText.setText(getText(R.string.error_message));
        }
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        outState.putString(ORIGINAL_CONTENT, mOriginalContent);
    }
    
    @Override
    protected void onPause() {
        super.onPause();

        if (mCursor != null) {
            String text = mText.getText().toString();
            int length = text.length();

            if (isFinishing() && (length == 0)) {
                setResult(RESULT_CANCELED);
                delete();

            } else {
                ContentValues values = new ContentValues();
                values.put("text", text);
                getContentResolver().update(mUri, values, null, null);
            }
        }
    }
    
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);

        if (mState == STATE_EDIT) {
            menu.add(0, REVERT_ID, 0, R.string.menu_revert)
            .setShortcut('0', 'r')
            .setIcon(android.R.drawable.ic_menu_revert);
            menu.add(0, DELETE_ID, 0, R.string.menu_delete)
            .setShortcut('1', 'd')
            .setIcon(android.R.drawable.ic_menu_delete);

        } else {
            menu.add(0, DISCARD_ID, 0, R.string.menu_discard)
                    .setShortcut('0', 'd')
                    .setIcon(android.R.drawable.ic_menu_delete);
        }
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle all of the possible menu actions.
        switch (item.getItemId()) {
        case DELETE_ID:
            delete();
            finish();
            break;
        case DISCARD_ID:
            cancel();
            break;
        case REVERT_ID:
            cancel();
            break;
        }
        return super.onOptionsItemSelected(item);
    }
    
    private final void cancel() {
        if (mCursor != null) {
            if (mState == STATE_EDIT) {
                mCursor.close();
                mCursor = null;
                ContentValues values = new ContentValues();
                values.put("text", mOriginalContent);
                getContentResolver().update(mUri, values, null, null);
            } else if (mState == STATE_INSERT) {
                delete();
            }
        }
        setResult(RESULT_CANCELED);
        finish();
    }
    
    private final void delete() {
        if (mCursor != null) {
            mCursor.close();
            mCursor = null;
            getContentResolver().delete(mUri, null, null);
            mText.setText("");
        }
    }
}
