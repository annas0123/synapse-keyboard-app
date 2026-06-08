package com.smafty.synapsekeyboard.data.local.dao;

@kotlin.Metadata(mv = {1, 9, 0}, k = 1, xi = 48, d1 = {"\u0000B\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0000\n\u0002\u0010\u0002\n\u0002\b\u0003\n\u0002\u0010\b\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0002\u0010 \n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0010\u000e\n\u0002\b\u0005\n\u0002\u0010\t\n\u0002\b\f\n\u0002\u0010\u000b\n\u0002\b\u0002\bg\u0018\u00002\u00020\u0001J\u000e\u0010\u0002\u001a\u00020\u0003H\u00a7@\u00a2\u0006\u0002\u0010\u0004J\u0016\u0010\u0005\u001a\u00020\u00032\u0006\u0010\u0006\u001a\u00020\u0007H\u00a7@\u00a2\u0006\u0002\u0010\bJ\u0014\u0010\t\u001a\u000e\u0012\n\u0012\b\u0012\u0004\u0012\u00020\f0\u000b0\nH\'J\u0018\u0010\r\u001a\u0004\u0018\u00010\f2\u0006\u0010\u000e\u001a\u00020\u000fH\u00a7@\u00a2\u0006\u0002\u0010\u0010J\u0014\u0010\u0011\u001a\u000e\u0012\n\u0012\b\u0012\u0004\u0012\u00020\f0\u000b0\nH\'J\u001c\u0010\u0012\u001a\u000e\u0012\n\u0012\b\u0012\u0004\u0012\u00020\f0\u000b0\n2\u0006\u0010\u0013\u001a\u00020\u0007H\'J\u0016\u0010\u0014\u001a\u00020\u00152\u0006\u0010\u0016\u001a\u00020\fH\u00a7@\u00a2\u0006\u0002\u0010\u0017J\u0016\u0010\u0018\u001a\u00020\u00032\u0006\u0010\u0019\u001a\u00020\u0007H\u00a7@\u00a2\u0006\u0002\u0010\bJ*\u0010\u001a\u001a\u00020\u00032\u0006\u0010\u001b\u001a\u00020\u000f2\b\b\u0002\u0010\u001c\u001a\u00020\u00072\b\b\u0002\u0010\u001d\u001a\u00020\u0007H\u0097@\u00a2\u0006\u0002\u0010\u001eJ\u0016\u0010\u001f\u001a\u00020\u00032\u0006\u0010\u0016\u001a\u00020\fH\u00a7@\u00a2\u0006\u0002\u0010\u0017J\u001e\u0010 \u001a\u00020\u00032\u0006\u0010\u0006\u001a\u00020\u00072\u0006\u0010!\u001a\u00020\"H\u00a7@\u00a2\u0006\u0002\u0010#\u00a8\u0006$"}, d2 = {"Lcom/smafty/synapsekeyboard/data/local/dao/ClipboardDao;", "", "clearUnpinned", "", "(Lkotlin/coroutines/Continuation;)Ljava/lang/Object;", "deleteById", "id", "", "(ILkotlin/coroutines/Continuation;)Ljava/lang/Object;", "getAllHistoryFlow", "Lkotlinx/coroutines/flow/Flow;", "", "Lcom/smafty/synapsekeyboard/data/local/entity/ClipboardEntity;", "getByContent", "content", "", "(Ljava/lang/String;Lkotlin/coroutines/Continuation;)Ljava/lang/Object;", "getPinnedHistoryFlow", "getUnpinnedHistoryFlow", "limit", "insert", "", "item", "(Lcom/smafty/synapsekeyboard/data/local/entity/ClipboardEntity;Lkotlin/coroutines/Continuation;)Ljava/lang/Object;", "pruneOldUnpinned", "keepLimit", "safeInsertWithDeduplication", "text", "charLimit", "keepHistoryLimit", "(Ljava/lang/String;IILkotlin/coroutines/Continuation;)Ljava/lang/Object;", "update", "updatePinStatus", "isPinned", "", "(IZLkotlin/coroutines/Continuation;)Ljava/lang/Object;", "app_debug"})
@androidx.room.Dao()
public abstract interface ClipboardDao {
    
    /**
     * Returns all clipboard items — pinned first, then sorted newest first.
     */
    @androidx.room.Query(value = "SELECT * FROM clipboard_history ORDER BY is_pinned DESC, timestamp DESC")
    @org.jetbrains.annotations.NotNull()
    public abstract kotlinx.coroutines.flow.Flow<java.util.List<com.smafty.synapsekeyboard.data.local.entity.ClipboardEntity>> getAllHistoryFlow();
    
    /**
     * Returns only unpinned items up to [limit], newest first.
     */
    @androidx.room.Query(value = "SELECT * FROM clipboard_history WHERE is_pinned = 0 ORDER BY timestamp DESC LIMIT :limit")
    @org.jetbrains.annotations.NotNull()
    public abstract kotlinx.coroutines.flow.Flow<java.util.List<com.smafty.synapsekeyboard.data.local.entity.ClipboardEntity>> getUnpinnedHistoryFlow(int limit);
    
    /**
     * Returns only pinned items, newest first.
     */
    @androidx.room.Query(value = "SELECT * FROM clipboard_history WHERE is_pinned = 1 ORDER BY timestamp DESC")
    @org.jetbrains.annotations.NotNull()
    public abstract kotlinx.coroutines.flow.Flow<java.util.List<com.smafty.synapsekeyboard.data.local.entity.ClipboardEntity>> getPinnedHistoryFlow();
    
    /**
     * Finds an existing entry by exact content match.
     */
    @androidx.room.Query(value = "SELECT * FROM clipboard_history WHERE content = :content LIMIT 1")
    @org.jetbrains.annotations.Nullable()
    public abstract java.lang.Object getByContent(@org.jetbrains.annotations.NotNull()
    java.lang.String content, @org.jetbrains.annotations.NotNull()
    kotlin.coroutines.Continuation<? super com.smafty.synapsekeyboard.data.local.entity.ClipboardEntity> $completion);
    
    @androidx.room.Insert(onConflict = 5)
    @org.jetbrains.annotations.Nullable()
    public abstract java.lang.Object insert(@org.jetbrains.annotations.NotNull()
    com.smafty.synapsekeyboard.data.local.entity.ClipboardEntity item, @org.jetbrains.annotations.NotNull()
    kotlin.coroutines.Continuation<? super java.lang.Long> $completion);
    
    @androidx.room.Update()
    @org.jetbrains.annotations.Nullable()
    public abstract java.lang.Object update(@org.jetbrains.annotations.NotNull()
    com.smafty.synapsekeyboard.data.local.entity.ClipboardEntity item, @org.jetbrains.annotations.NotNull()
    kotlin.coroutines.Continuation<? super kotlin.Unit> $completion);
    
    @androidx.room.Query(value = "DELETE FROM clipboard_history WHERE id = :id")
    @org.jetbrains.annotations.Nullable()
    public abstract java.lang.Object deleteById(int id, @org.jetbrains.annotations.NotNull()
    kotlin.coroutines.Continuation<? super kotlin.Unit> $completion);
    
    /**
     * Deletes all unpinned items — used by the "Clear All" action.
     */
    @androidx.room.Query(value = "DELETE FROM clipboard_history WHERE is_pinned = 0")
    @org.jetbrains.annotations.Nullable()
    public abstract java.lang.Object clearUnpinned(@org.jetbrains.annotations.NotNull()
    kotlin.coroutines.Continuation<? super kotlin.Unit> $completion);
    
    @androidx.room.Query(value = "UPDATE clipboard_history SET is_pinned = :isPinned WHERE id = :id")
    @org.jetbrains.annotations.Nullable()
    public abstract java.lang.Object updatePinStatus(int id, boolean isPinned, @org.jetbrains.annotations.NotNull()
    kotlin.coroutines.Continuation<? super kotlin.Unit> $completion);
    
    /**
     * Prunes unpinned history, keeping only the [keepLimit] newest items.
     * Runs automatically after every insert to enforce a soft cap.
     */
    @androidx.room.Query(value = "DELETE FROM clipboard_history WHERE is_pinned = 0 AND id NOT IN (SELECT id FROM clipboard_history WHERE is_pinned = 0 ORDER BY timestamp DESC LIMIT :keepLimit)")
    @org.jetbrains.annotations.Nullable()
    public abstract java.lang.Object pruneOldUnpinned(int keepLimit, @org.jetbrains.annotations.NotNull()
    kotlin.coroutines.Continuation<? super kotlin.Unit> $completion);
    
    /**
     * Inserts text safely with de-duplication:
     * - If the text already exists, its timestamp is bumped to the top (preserving pin state).
     * - If new, inserts a fresh entry and prunes old unpinned items to [keepHistoryLimit].
     */
    @androidx.room.Transaction()
    @org.jetbrains.annotations.Nullable()
    public abstract java.lang.Object safeInsertWithDeduplication(@org.jetbrains.annotations.NotNull()
    java.lang.String text, int charLimit, int keepHistoryLimit, @org.jetbrains.annotations.NotNull()
    kotlin.coroutines.Continuation<? super kotlin.Unit> $completion);
    
    @kotlin.Metadata(mv = {1, 9, 0}, k = 3, xi = 48)
    public static final class DefaultImpls {
        
        /**
         * Inserts text safely with de-duplication:
         * - If the text already exists, its timestamp is bumped to the top (preserving pin state).
         * - If new, inserts a fresh entry and prunes old unpinned items to [keepHistoryLimit].
         */
        @androidx.room.Transaction()
        @org.jetbrains.annotations.Nullable()
        public static java.lang.Object safeInsertWithDeduplication(@org.jetbrains.annotations.NotNull()
        com.smafty.synapsekeyboard.data.local.dao.ClipboardDao $this, @org.jetbrains.annotations.NotNull()
        java.lang.String text, int charLimit, int keepHistoryLimit, @org.jetbrains.annotations.NotNull()
        kotlin.coroutines.Continuation<? super kotlin.Unit> $completion) {
            return null;
        }
    }
}