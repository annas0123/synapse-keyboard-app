package com.smafty.synapsekeyboard.data.local.dao;

/**
 * Data Access Object for local theme settings persistence.
 */
@kotlin.Metadata(mv = {1, 9, 0}, k = 1, xi = 48, d1 = {"\u0000\u001a\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0010\u0002\n\u0002\b\u0003\bg\u0018\u00002\u00020\u0001J\u0010\u0010\u0002\u001a\u0004\u0018\u00010\u0003H\u00a7@\u00a2\u0006\u0002\u0010\u0004J\u0016\u0010\u0005\u001a\u00020\u00062\u0006\u0010\u0007\u001a\u00020\u0003H\u00a7@\u00a2\u0006\u0002\u0010\b\u00a8\u0006\t"}, d2 = {"Lcom/smafty/synapsekeyboard/data/local/dao/ThemeSettingsDao;", "", "getThemeSettings", "Lcom/smafty/synapsekeyboard/data/local/entity/ThemeSettingsEntity;", "(Lkotlin/coroutines/Continuation;)Ljava/lang/Object;", "saveThemeSettings", "", "settings", "(Lcom/smafty/synapsekeyboard/data/local/entity/ThemeSettingsEntity;Lkotlin/coroutines/Continuation;)Ljava/lang/Object;", "app_debug"})
@androidx.room.Dao()
public abstract interface ThemeSettingsDao {
    
    @androidx.room.Query(value = "SELECT * FROM theme_settings WHERE id = 1 LIMIT 1")
    @org.jetbrains.annotations.Nullable()
    public abstract java.lang.Object getThemeSettings(@org.jetbrains.annotations.NotNull()
    kotlin.coroutines.Continuation<? super com.smafty.synapsekeyboard.data.local.entity.ThemeSettingsEntity> $completion);
    
    @androidx.room.Insert(onConflict = 1)
    @org.jetbrains.annotations.Nullable()
    public abstract java.lang.Object saveThemeSettings(@org.jetbrains.annotations.NotNull()
    com.smafty.synapsekeyboard.data.local.entity.ThemeSettingsEntity settings, @org.jetbrains.annotations.NotNull()
    kotlin.coroutines.Continuation<? super kotlin.Unit> $completion);
}