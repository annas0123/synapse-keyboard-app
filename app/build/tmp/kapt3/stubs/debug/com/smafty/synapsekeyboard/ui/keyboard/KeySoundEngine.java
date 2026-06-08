package com.smafty.synapsekeyboard.ui.keyboard;

@kotlin.Metadata(mv = {1, 9, 0}, k = 1, xi = 48, d1 = {"\u00006\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0002\b\u0002\n\u0002\u0010\u000e\n\u0000\n\u0002\u0010\b\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0004\n\u0002\u0010\u0017\n\u0002\b\u000b\n\u0002\u0010\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0006\b\u00c7\u0002\u0018\u00002\u00020\u0001B\u0007\b\u0002\u00a2\u0006\u0002\u0010\u0002J\b\u0010\f\u001a\u00020\rH\u0002J\b\u0010\u000e\u001a\u00020\rH\u0002J\b\u0010\u000f\u001a\u00020\rH\u0002J\b\u0010\u0010\u001a\u00020\rH\u0002J\b\u0010\u0011\u001a\u00020\rH\u0002J\u0010\u0010\u0012\u001a\u00020\r2\u0006\u0010\u0013\u001a\u00020\bH\u0002J\b\u0010\u0014\u001a\u00020\rH\u0002J\b\u0010\u0015\u001a\u00020\rH\u0002J\b\u0010\u0016\u001a\u00020\rH\u0002J\b\u0010\u0017\u001a\u00020\rH\u0002J\u000e\u0010\u0018\u001a\u00020\u00192\u0006\u0010\u001a\u001a\u00020\u001bJ\u0006\u0010\u001c\u001a\u00020\u0019J\u0010\u0010\u001d\u001a\u00020\u00192\u0006\u0010\u001e\u001a\u00020\rH\u0002J\u000e\u0010\u001f\u001a\u00020\u00192\u0006\u0010\u0013\u001a\u00020\bJ\u0016\u0010 \u001a\u00020\u00192\u0006\u0010\u001a\u001a\u00020\u001b2\u0006\u0010\u0013\u001a\u00020\bR\u000e\u0010\u0003\u001a\u00020\u0004X\u0082T\u00a2\u0006\u0002\n\u0000R\u000e\u0010\u0005\u001a\u00020\u0006X\u0082T\u00a2\u0006\u0002\n\u0000R\u001e\u0010\t\u001a\u00020\b2\u0006\u0010\u0007\u001a\u00020\b@BX\u0086\u000e\u00a2\u0006\b\n\u0000\u001a\u0004\b\n\u0010\u000b\u00a8\u0006!"}, d2 = {"Lcom/smafty/synapsekeyboard/ui/keyboard/KeySoundEngine;", "", "()V", "PREFS_KEY_SOUND", "", "SAMPLE_RATE", "", "<set-?>", "Lcom/smafty/synapsekeyboard/ui/keyboard/KeySoundPreset;", "activePreset", "getActivePreset", "()Lcom/smafty/synapsekeyboard/ui/keyboard/KeySoundPreset;", "generateBubble", "", "generateCrystalPing", "generateMechanical", "generateModernDigital", "generateNeonZap", "generateSamples", "preset", "generateSoftRubber", "generateTypewriter", "generateWaterDrop", "generateWooden", "loadPreset", "", "prefs", "Landroid/content/SharedPreferences;", "playClick", "playPcmSamples", "samples", "preview", "savePreset", "app_debug"})
public final class KeySoundEngine {
    private static final int SAMPLE_RATE = 44100;
    @org.jetbrains.annotations.NotNull()
    private static final java.lang.String PREFS_KEY_SOUND = "key_sound_preset";
    @org.jetbrains.annotations.NotNull()
    private static com.smafty.synapsekeyboard.ui.keyboard.KeySoundPreset activePreset = com.smafty.synapsekeyboard.ui.keyboard.KeySoundPreset.NONE;
    @org.jetbrains.annotations.NotNull()
    public static final com.smafty.synapsekeyboard.ui.keyboard.KeySoundEngine INSTANCE = null;
    
    private KeySoundEngine() {
        super();
    }
    
    @org.jetbrains.annotations.NotNull()
    public final com.smafty.synapsekeyboard.ui.keyboard.KeySoundPreset getActivePreset() {
        return null;
    }
    
    /**
     * Loads the persisted sound preset from SharedPreferences.
     */
    public final void loadPreset(@org.jetbrains.annotations.NotNull()
    android.content.SharedPreferences prefs) {
    }
    
    /**
     * Saves the selected sound preset to SharedPreferences.
     */
    public final void savePreset(@org.jetbrains.annotations.NotNull()
    android.content.SharedPreferences prefs, @org.jetbrains.annotations.NotNull()
    com.smafty.synapsekeyboard.ui.keyboard.KeySoundPreset preset) {
    }
    
    /**
     * Plays the active key click sound on a background thread.
     * Safe to call from the UI thread — uses a daemon thread internally.
     */
    public final void playClick() {
    }
    
    /**
     * Plays a one-shot preview of any preset (used in Settings).
     */
    public final void preview(@org.jetbrains.annotations.NotNull()
    com.smafty.synapsekeyboard.ui.keyboard.KeySoundPreset preset) {
    }
    
    private final short[] generateSamples(com.smafty.synapsekeyboard.ui.keyboard.KeySoundPreset preset) {
        return null;
    }
    
    /**
     * MODERN DIGITAL: A short, crisp high-frequency click (3.2 kHz)
     * with a fast exponential decay. Similar to Gboard default.
     * Duration: ~30ms
     */
    private final short[] generateModernDigital() {
        return null;
    }
    
    /**
     * MECHANICAL: A two-stage click — a sharp transient (attack) at
     * high amplitude followed by a short resonance tail at lower pitch.
     * Duration: ~55ms
     */
    private final short[] generateMechanical() {
        return null;
    }
    
    /**
     * BUBBLE: A soft, rounded sine chirp that sweeps upward from 500Hz
     * to 1200Hz over 40ms, giving it a pleasant "bloop" feel.
     * Duration: ~40ms
     */
    private final short[] generateBubble() {
        return null;
    }
    
    /**
     * WOODEN: A low-frequency thump with strong harmonic content,
     * emulating a finger tap on a hollow wooden surface.
     * Duration: ~50ms
     */
    private final short[] generateWooden() {
        return null;
    }
    
    /**
     * TYPEWRITER: Classic vintage clatter — a sharp noise burst followed
     * by a metallic ring. Uses band-limited noise + resonant sine.
     * Duration: ~60ms
     */
    private final short[] generateTypewriter() {
        return null;
    }
    
    /**
     * CRYSTAL PING: A clean bell-like tone using additive harmonics.
     * Two partials with different decay rates create a shimmering glass quality.
     * Duration: ~80ms
     */
    private final short[] generateCrystalPing() {
        return null;
    }
    
    /**
     * SOFT RUBBER: A very quiet, dampened tap with a near-silent profile.
     * High decay rate, low amplitude — for quiet environments.
     * Duration: ~25ms
     */
    private final short[] generateSoftRubber() {
        return null;
    }
    
    /**
     * NEON ZAP: An electric retro-synth buzz using a frequency-modulated
     * sawtooth approximation. Drops from 1800Hz to 600Hz over 35ms.
     * Duration: ~35ms
     */
    private final short[] generateNeonZap() {
        return null;
    }
    
    /**
     * WATER DROP: A rising frequency chirp like a water drop hitting a surface.
     * Sweeps from 300Hz up to 2500Hz with a bell-shaped amplitude envelope.
     * Duration: ~50ms
     */
    private final short[] generateWaterDrop() {
        return null;
    }
    
    private final void playPcmSamples(short[] samples) {
    }
}