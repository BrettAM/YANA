package com.qed7.yana;

import java.util.List;

/**
 * Created by brettam on 5/14/17.
 */

public interface NoteDb {
    public void close();
    public List<UNote> getUNotes();
    public UNote newUNote();
}
