package com.qed7.yana;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.util.LinkedList;
import java.util.List;

/**
 * Created by brettam on 5/13/17.
 */

public class NoteDbHelper extends SQLiteOpenHelper {
    private static final int DATABASE_VERSION = 2;
    private static final String DATABASE_NAME = "note_database";
    private static final String UNOTE_TABLE_NAME = "dictionary";
    private static final String UNOTE_ID = "id";
    private static final String UNOTE_TEXT = "text";

    private static final String UNOTE_TABLE_CREATE =
            "CREATE TABLE " + UNOTE_TABLE_NAME + " (" +
                    UNOTE_ID + " INTEGER PRIMARY KEY, " +
                    UNOTE_TEXT + " TEXT);";

    NoteDbHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(UNOTE_TABLE_CREATE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS "+UNOTE_TABLE_NAME);
        onCreate(db);
    }

    public static class NoteDbWrapper implements NoteDb {
        private final SQLiteDatabase db;
        private NoteDbWrapper(SQLiteDatabase db){
            this.db = db;
        }

        public class UNoteImpl implements UNote{
            private String data;
            private final long id;
            private boolean deleted = false;
            private UNoteImpl(String data, long id){
                this.data = data;
                this.id = id;
            }
            public String getText(){
                if(deleted) throw new IllegalStateException("This note has already been deleted");
                return data;
            }
            public void updateText(String text){
                if(deleted) throw new IllegalStateException("This note has already been deleted");
                ContentValues cv = new ContentValues();
                cv.put(UNOTE_TEXT, text);
                db.update(
                        UNOTE_TABLE_NAME,
                        cv,
                        UNOTE_ID + " LIKE ?",
                        new String[]{ Long.toString(id) }
                );
                data = text;
            }
            public void delete(){
                if(deleted) throw new IllegalStateException("This note has already been deleted");
                db.delete(
                        UNOTE_TABLE_NAME,
                        UNOTE_ID + " LIKE ?",
                        new String[]{  Long.toString(id) }
                );
                data = null;
            }
            @Override
            public String toString(){
                return getText();
            }
        }

        public void close(){
            db.close();
        }

        public List<UNote> getUNotes(){
            Cursor c = db.query(UNOTE_TABLE_NAME,
                    new String[]{UNOTE_ID, UNOTE_TEXT},
                    null, null, null, null, null);
            LinkedList<UNote> notes = new LinkedList<>();
            while(c.moveToNext()){
                long id = c.getLong(c.getColumnIndex(UNOTE_ID));
                String text = c.getString(c.getColumnIndex(UNOTE_TEXT));
                notes.add(new UNoteImpl(text, id));
            }
            return notes;
        }

        public UNote newUNote(){
            ContentValues v = new ContentValues();
            v.put(UNOTE_TEXT, "");
            long newRowId = db.insert(UNOTE_TABLE_NAME, null, v);
            return new UNoteImpl("", newRowId);
        }
    }

    public NoteDbWrapper getDb(){
        return new NoteDbWrapper(getWritableDatabase());
    }
}
