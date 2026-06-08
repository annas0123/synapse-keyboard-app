package com.smafty.synapsekeyboard.data.local.dao;

@kotlin.Metadata(mv = {1, 9, 0}, k = 1, xi = 48, d1 = {"\u0000,\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0000\n\u0002\u0010\u0002\n\u0002\b\u0003\n\u0002\u0010\u000e\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\u0010 \n\u0002\b\u000b\bg\u0018\u00002\u00020\u0001J\u000e\u0010\u0002\u001a\u00020\u0003H\u00a7@\u00a2\u0006\u0002\u0010\u0004J\u0016\u0010\u0005\u001a\u00020\u00032\u0006\u0010\u0006\u001a\u00020\u0007H\u00a7@\u00a2\u0006\u0002\u0010\bJ\u0018\u0010\t\u001a\u0004\u0018\u00010\n2\u0006\u0010\u0006\u001a\u00020\u0007H\u00a7@\u00a2\u0006\u0002\u0010\bJ\u0014\u0010\u000b\u001a\u000e\u0012\n\u0012\b\u0012\u0004\u0012\u00020\n0\r0\fH\'J.\u0010\u000e\u001a\u00020\u00032\u0006\u0010\u0006\u001a\u00020\u00072\u0006\u0010\u000f\u001a\u00020\u00072\u0006\u0010\u0010\u001a\u00020\u00072\u0006\u0010\u0011\u001a\u00020\u0007H\u0097@\u00a2\u0006\u0002\u0010\u0012J\u0016\u0010\u0013\u001a\u00020\u00032\u0006\u0010\u0014\u001a\u00020\nH\u00a7@\u00a2\u0006\u0002\u0010\u0015J\u000e\u0010\u0016\u001a\u00020\u0003H\u00a7@\u00a2\u0006\u0002\u0010\u0004J\u0016\u0010\u0017\u001a\u00020\u00032\u0006\u0010\u0014\u001a\u00020\nH\u00a7@\u00a2\u0006\u0002\u0010\u0015\u00a8\u0006\u0018"}, d2 = {"Lcom/smafty/synapsekeyboard/data/local/dao/MostUsedPromptDao;", "", "deleteAll", "", "(Lkotlin/coroutines/Continuation;)Ljava/lang/Object;", "deleteById", "promptId", "", "(Ljava/lang/String;Lkotlin/coroutines/Continuation;)Ljava/lang/Object;", "getById", "Lcom/smafty/synapsekeyboard/data/local/entity/MostUsedPromptEntity;", "getTop20PromptsFlow", "Lkotlinx/coroutines/flow/Flow;", "", "incrementPromptUsage", "title", "promptText", "category", "(Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;Lkotlin/coroutines/Continuation;)Ljava/lang/Object;", "insert", "prompt", "(Lcom/smafty/synapsekeyboard/data/local/entity/MostUsedPromptEntity;Lkotlin/coroutines/Continuation;)Ljava/lang/Object;", "pruneBelowTop20", "update", "app_debug"})
@androidx.room.Dao()
public abstract interface MostUsedPromptDao {
    
    /**
     * Returns the Top 20 most frequently used prompts sorted by usage count descending,
     * then by recency. Used to populate the offline AI prompts panel.
     */
    @androidx.room.Query(value = "SELECT * FROM most_used_prompts ORDER BY use_count DESC, timestamp DESC LIMIT 20")
    @org.jetbrains.annotations.NotNull()
    public abstract kotlinx.coroutines.flow.Flow<java.util.List<com.smafty.synapsekeyboard.data.local.entity.MostUsedPromptEntity>> getTop20PromptsFlow();
    
    @androidx.room.Query(value = "SELECT * FROM most_used_prompts WHERE promptId = :promptId LIMIT 1")
    @org.jetbrains.annotations.Nullable()
    public abstract java.lang.Object getById(@org.jetbrains.annotations.NotNull()
    java.lang.String promptId, @org.jetbrains.annotations.NotNull()
    kotlin.coroutines.Continuation<? super com.smafty.synapsekeyboard.data.local.entity.MostUsedPromptEntity> $completion);
    
    @androidx.room.Insert(onConflict = 1)
    @org.jetbrains.annotations.Nullable()
    public abstract java.lang.Object insert(@org.jetbrains.annotations.NotNull()
    com.smafty.synapsekeyboard.data.local.entity.MostUsedPromptEntity prompt, @org.jetbrains.annotations.NotNull()
    kotlin.coroutines.Continuation<? super kotlin.Unit> $completion);
    
    @androidx.room.Update()
    @org.jetbrains.annotations.Nullable()
    public abstract java.lang.Object update(@org.jetbrains.annotations.NotNull()
    com.smafty.synapsekeyboard.data.local.entity.MostUsedPromptEntity prompt, @org.jetbrains.annotations.NotNull()
    kotlin.coroutines.Continuation<? super kotlin.Unit> $completion);
    
    @androidx.room.Query(value = "DELETE FROM most_used_prompts WHERE promptId = :promptId")
    @org.jetbrains.annotations.Nullable()
    public abstract java.lang.Object deleteById(@org.jetbrains.annotations.NotNull()
    java.lang.String promptId, @org.jetbrains.annotations.NotNull()
    kotlin.coroutines.Continuation<? super kotlin.Unit> $completion);
    
    /**
     * Automatically keeps the local database pruned to a strict ceiling of 20 items.
     * Deletes any rows that fall outside the Top 20 usage list.
     */
    @androidx.room.Query(value = "DELETE FROM most_used_prompts WHERE promptId NOT IN (SELECT promptId FROM most_used_prompts ORDER BY use_count DESC, timestamp DESC LIMIT 20)")
    @org.jetbrains.annotations.Nullable()
    public abstract java.lang.Object pruneBelowTop20(@org.jetbrains.annotations.NotNull()
    kotlin.coroutines.Continuation<? super kotlin.Unit> $completion);
    
    @androidx.room.Query(value = "DELETE FROM most_used_prompts")
    @org.jetbrains.annotations.Nullable()
    public abstract java.lang.Object deleteAll(@org.jetbrains.annotations.NotNull()
    kotlin.coroutines.Continuation<? super kotlin.Unit> $completion);
    
    /**
     * Increments usage of a prompt. If the prompt does not exist in local SQLite, inserts it.
     * Automatically prunes the table to keep only the Top 20 offline entries.
     */
    @androidx.room.Transaction()
    @org.jetbrains.annotations.Nullable()
    public abstract java.lang.Object incrementPromptUsage(@org.jetbrains.annotations.NotNull()
    java.lang.String promptId, @org.jetbrains.annotations.NotNull()
    java.lang.String title, @org.jetbrains.annotations.NotNull()
    java.lang.String promptText, @org.jetbrains.annotations.NotNull()
    java.lang.String category, @org.jetbrains.annotations.NotNull()
    kotlin.coroutines.Continuation<? super kotlin.Unit> $completion);
    
    @kotlin.Metadata(mv = {1, 9, 0}, k = 3, xi = 48)
    public static final class DefaultImpls {
        
        /**
         * Increments usage of a prompt. If the prompt does not exist in local SQLite, inserts it.
         * Automatically prunes the table to keep only the Top 20 offline entries.
         */
        @androidx.room.Transaction()
        @org.jetbrains.annotations.Nullable()
        public static java.lang.Object incrementPromptUsage(@org.jetbrains.annotations.NotNull()
        com.smafty.synapsekeyboard.data.local.dao.MostUsedPromptDao $this, @org.jetbrains.annotations.NotNull()
        java.lang.String promptId, @org.jetbrains.annotations.NotNull()
        java.lang.String title, @org.jetbrains.annotations.NotNull()
        java.lang.String promptText, @org.jetbrains.annotations.NotNull()
        java.lang.String category, @org.jetbrains.annotations.NotNull()
        kotlin.coroutines.Continuation<? super kotlin.Unit> $completion) {
            return null;
        }
    }
}