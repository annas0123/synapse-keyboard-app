# Synapse AI Keyboard - Premium Minimal Redesign Prompt

## Project Context

- **Platform:** Native Android (Kotlin + Jetpack Compose)
- **App Type:** AI-powered Keyboard (IME) + companion app
- **Current State:** Working app with 10 app themes + 15 keyboard themes, glassmorphism effects, hazy blur backgrounds
- **Goal:** Complete frontend rewrite to premium minimal design while keeping ALL backend logic intact

---

## Design Direction

### Reference Style
The design should follow a **premium dark-first aesthetic** similar to the reference screenshot:
- Deep near-black backgrounds (#0A0A0F to #121218)
- Purple/violet accent gradients (subtle, not overwhelming)
- Clean typography with strong hierarchy
- Minimal chrome, maximum content focus
- Solid colors only - NO glassmorphism, NO blur, NO frosted glass effects anywhere

### Core Design Principles
1. **Minimalism** - Remove all visual noise, unnecessary borders, heavy shadows
2. **Premium feel** - Every element should feel expensive and intentional
3. **Two-theme system** - Only Premium Black (default) and Premium White
4. **Consistent language** - All screens must feel like they belong to the same app
5. **Touch-first** - All targets minimum 48dp, thumb-zone optimized

---

## Theme System (Replace Current 10+15 Themes)

### Premium Black Theme (Default)
```
Background:        #0A0A0F (true near-black, OLED optimized)
Surface:           #141420 (cards, panels)
Surface Elevated:  #1C1C2A (elevated cards, active states)
Border:            #2A2A3A (subtle dividers, search bars)
Border Active:     #7C5CFC (focused inputs, active tabs)

Primary:           #7C5CFC (violet)
Primary Gradient:  #7C5CFC → #A78BFA (subtle vertical gradient for CTAs)
Primary Muted:     #7C5CFC20 (hover/pressed states)

Text Primary:      #F0F0F5 (near-white, not pure white)
Text Secondary:    #8888A0 (muted descriptions)
Text Tertiary:     #555570 (placeholders, hints)

Accent:            #A78BFA (lighter violet for highlights)
Success:           #34D399
Error:             #F87171
Warning:           #FBBF24

Keyboard BG:       #0A0A0F (matches app background)
Key Face:          #1A1A28 (default key)
Key Face Pressed:  #2A2A3A (press state)
Key Text:          #E0E0EA
Key Border:        #252535 (very subtle)
Key Gradient:      None (solid colors only on keys)
Toolbar BG:        #0E0E18 (slightly lighter than background)
```

### Premium White Theme
```
Background:        #FAFAFA (warm white, not pure #FFF)
Surface:           #FFFFFF (cards, panels)
Surface Elevated:  #FFFFFF (elevated cards with subtle shadow)
Border:            #E8E8F0 (subtle dividers)
Border Active:     #7C5C5C (focused inputs)

Primary:           #6C47FF (deeper violet for contrast on white)
Primary Gradient:  #6C47FF → #8B6FFF (subtle for CTAs)
Primary Muted:     #6C47FF15 (hover/pressed states)

Text Primary:      #1A1A2E (near-black)
Text Secondary:    #6B6B80 (muted)
Text Tertiary:     #9898B0 (placeholders)

Accent:            #6C47FF
Success:           #10B981
Error:             #EF4444
Warning:           #F59E0B

Keyboard BG:       #F2F2F7
Key Face:          #FFFFFF
Key Face Pressed:  #E8E8F0
Key Text:          #1A1A2E
Key Border:        #DCDCE5
Toolbar BG:        #FAFAFA
```

### Gradient Usage Rules
- **Allowed gradients:** Only on primary CTA buttons (vertical, subtle)
- **Forbidden gradients:** On backgrounds, cards, navigation bars, key faces
- **NO blur anywhere** - All frosted glass, backdrop blur, glassmorphism effects removed from entire app

---

## Screen-by-Screen Redesign

### 1. Splash Screen (`SplashScreen.kt`)
**Keep:** Lottie animation, pulsing glow ring
**Change:**
- Background: #0A0A0F (pure dark)
- Remove any hazy/blurry overlays
- Logo glow should be subtle violet (#7C5CFC at 20% opacity)
- Duration: 1.6s -> 1.2s (faster, snappier)

### 2. Login Screen (`LoginScreen.kt`)
**Keep:** Google sign-in functionality
**Change:**
- Background: #0A0A0F with subtle gradient mesh (very faint violet blobs at corners)
- Remove glassmorphism card effect
- Replace with: Solid surface card (#141420) with 1dp border (#2A2A3A)
- Google button: White background, dark text, rounded corners (12dp max)
- Value proposition items: Simple icon + text, no glass cards
- Typography: "Outfit" font, SemiBold for headings, Regular for body

### 3. Main Dashboard (`MainDashboard.kt`)
**Keep:** 3-tab navigation (Home, Prompts, Settings)
**Change:**
- **Bottom Navigation Bar:**
  - NO blur, NO frosted glass, NO glassmorphism
  - Background: Solid #141420 (black theme) / #FFFFFF (white theme)
  - Top border: 0.5dp #2A2A3A (black) / #E8E8F0 (white)
  - Active tab: Violet pill indicator (#7C5CFC at 15% opacity background), 32dp height, 16dp radius
  - Active icon: #7C5CFC, Inactive icon: #555570
  - Labels: Active = #F0F0F5, Inactive = #555570, 12sp
  - Height: 64dp (compact but touchable)
  - Padding: 8dp horizontal, 8dp vertical (safe area)
- **Tab content:** No transitions, instant switch (feels faster)

### 4. Home Screen (`HomeScreen.kt`)
**Keep:** Keyboard status, energy quota, sync status, stats
**Change:**
- Background: #0A0A0F
- Cards: #141420 background, #2A2A3A border (1dp), 12dp corner radius
- Energy gauge: Circular arc with violet gradient (#7C5CFC → #A78BFA)
- Stats tiles: Minimal, icon + number + label, no heavy backgrounds
- Status indicators: Colored dots (green/red/yellow) with text
- Remove: Any frosted glass effects, heavy shadows

### 5. Prompts Screen (`PromptsScreen.kt`) - MATCHES REFERENCE IMAGE
**Keep:** 3-tab system (Custom, Presets, Most Active), CRUD operations
**Change:**
- Background: #0A0A0F
- **Search bar:** #141420 background, #2A2A3A border, search icon #555570, placeholder #555570
- **Tab pills:**
  - Active: #7C5CFC background, white text, slight shadow
  - Inactive: #1C1C2A background, #8888A0 text
  - Corner radius: 20dp (pill shape)
  - Spacing: 8dp between pills
- **Empty state:**
  - Sparkle icon: #FBBF24 (golden yellow, like reference)
  - Title: "No custom prompts yet" - #F0F0F5, 18sp, SemiBold
  - Subtitle: "Tap + to add your first AI prompt" - #8888A0, 14sp
  - Add button: #7C5CFC background, white text, 12dp radius
- **Prompt cards (when populated):**
  - #141420 background
  - 1dp #2A2A3A border
  - 12dp corner radius
  - Title: #F0F0F5, 16sp, Medium
  - Subtitle: #8888A0, 13sp
  - Trailing: 3-dot menu or edit/delete icons
- **FAB (Floating Action Button):**
  - Position: Bottom right, above nav bar
  - Size: 56dp
  - Background: #7C5CFC solid (no gradient on FAB)
  - Icon: White "+" icon, 24dp
  - Shadow: Subtle violet glow (0, 4dp, 12dp, #7C5CFC40)

### 6. Settings Screen (`SettingsScreen.kt`)
**Keep:** Account, Appearance, AI Engine, Keyboard Management, Help sections
**Change:**
- Background: #0A0A0F
- Section headers: #8888A0, 13sp, uppercase, letter-spacing 0.5dp
- Settings items:
  - Background: #141420
  - Border: 1dp #2A2A3A (bottom only, between items)
  - Icon: #7C5CFC (violet)
  - Title: #F0F0F5, 16sp
  - Subtitle: #8888A0, 13sp
  - Trailing: Chevron or toggle
- Theme picker: Show 2 options (Black/White) with preview circles
- AI Engine selector: Card-based, show model name + cost badge
- Sign-out button: #F87171 text, no background

### 7. Key Sounds Screen (`KeySoundsScreen.kt`)
**Keep:** 9 sound presets with preview
**Change:**
- Background: #0A0A0F
- Sound cards: #141420, left accent strip (4dp wide, #7C5CFC for selected)
- Preview button: Circle, #1C1C2A background, play icon
- Selected state: #7C5CFC border, slightly brighter background
- Master toggle: Standard Material3 switch with violet thumb

### 8. Keyboard Test Screen (`KeyboardTestScreen.kt`)
**Keep:** Test notepad, height scale, theme selector
**Change:**
- Background: #0A0A0F
- Notepad area: #141420, monospace font, #F0F0F5 text
- Controls panel: Collapsible, #1C1C2A background
- Height slider: Violet track, white thumb
- Theme selector: 2 circles (black/white) with checkmark

---

## Keyboard UI Redesign (`KeyboardView.kt`)

### Layout Rules
- **Corner radius:** Near-zero (2dp max on keys, 0dp on keyboard background)
- **Key shape:** Rectangular with very slightly rounded corners
- **Key spacing:** 4dp horizontal, 6dp vertical
- **Key height:** 46dp (standard), adjustable via scale
- **Bottom row:** Special keys slightly wider

### Key Styles
```
Default Key:
  Background: #1A1A28 (black theme) / #FFFFFF (white theme)
  Text: #E0E0EA (black) / #1A1A2E (white)
  Border: 0.5dp #252535 (black) / #DCDCE5 (white)
  Corner radius: 2dp

Pressed Key:
  Background: #2A2A3A (black) / #E8E8F0 (white)
  Scale: 0.95 (quick pop animation)
  Haptic: EFFECT_TICK

Special Key (Shift, Backspace, Enter):
  Background: #1C1C2A (black) / #F0F0F5 (white)
  Icon: #8888A0 (black) / #6B6B80 (white)

Spacebar:
  Background: #1A1A28 (black) / #FFFFFF (white)
  Text: #555570 (placeholder)
  Corner radius: 2dp

Enter/Action Key:
  Background: #7C5CFC (both themes)
  Icon: White
  Corner radius: 2dp
```

### Key Press Animation (Gboard-style)
```
1. Touch down:
   - Key scales to 0.95 (50ms, ease-out)
   - Key background darkens slightly
   - Haptic: EFFECT_TICK
   - Sound: Selected preset

2. Key preview popup:
   - Appears ABOVE the key (offset 8dp up)
   - Larger version of the key (1.5x scale)
   - Same styling, slight elevation shadow
   - Shows character in larger font (24sp)
   - Appears after 80ms hold (not on quick tap)

3. Touch up:
   - Key scales back to 1.0 (100ms, spring animation)
   - Character committed to text field
```

### AI Toolbar
- **Height:** 48dp
- **Background:** #0E0E18 (black) / #F8F8FC (white)
- **Top border:** 0.5dp #2A2A3A
- **AI Icon:** 28dp (LARGER as requested), violet (#7C5CFC), positioned left
- **Model selector:** Text button, #8888A0 text, dropdown arrow
- **Action buttons:** 36dp circles, #1C1C2A background, icons #8888A0
- **AI Output text:** #F0F0F5, 14sp, Regular

### Bottom Row
- **Clipboard button:** Icon + count badge
- **Emoji button:** Icon, same size as other special keys
- **Voice/Input switch:** Right side
- **Settings gear:** Far right, smaller (28dp)

### Navigation Within Keyboard
- **Mode indicator:** Small dot indicators above bottom row
- **Active mode dot:** #7C5CFC, 6dp
- **Inactive dot:** #555570, 4dp

---

## Typography

### Font Family
**Primary:** "Outfit" (Google Fonts)
- Load via `GoogleFont.Provider` (already implemented)
- Weights to use:
  - Regular (400) - Body text, descriptions
  - Medium (500) - Card titles, labels
  - SemiBold (600) - Section headers, emphasis
  - Bold (700) - Screen titles only

### Type Scale
```
Screen Title:     Outfit Bold      24sp  #F0F0F5 / #1A1A2E
Section Header:   Outfit SemiBold  13sp  #8888A0 / #6B6B80 (uppercase, letterSpacing 0.5dp)
Card Title:       Outfit Medium    16sp  #F0F0F5 / #1A1A2E
Card Subtitle:    Outfit Regular   13sp  #8888A0 / #6B6B80
Body:             Outfit Regular   15sp  #F0F0F5 / #1A1A2E
Caption:          Outfit Regular   12sp  #555570 / #9898B0
Button:           Outfit Medium    15sp  #FFFFFF / #FFFFFF
Tab Label:        Outfit Medium    14sp  #8888A0 (inactive) / #F0F0F5 (active)
Keyboard Key:     Outfit Medium    20sp  #E0E0EA / #1A1A2E
Keyboard Special: Outfit Medium    16sp  #8888A0 / #6B6B80
```

---

## Animations & Transitions

### Keep
- Lottie splash animation
- Key press pop/scale animation
- Key preview popup
- Haptic feedback (EFFECT_TICK)
- Sound feedback (presets)

### Modify
- **Key press duration:** Faster (50ms down, 100ms up)
- **Tab switching:** Instant (no fade animation)
- **Screen transitions:** 200ms slide (not 300ms)
- **FAB appearance:** Scale from 0 + fade (200ms)
- **Card appearance:** Staggered fade-in (50ms delay between cards)

### Remove
- ALL blur/frosted glass/glassmorphism effects (including bottom nav bar)
- Heavy spring animations (keep subtle)
- Glow effects (except splash and energy gauge)
- Any backdrop filter effects

---

## Files to Modify (Frontend Only)

### Theme System
1. `ui/theme/Color.kt` - Replace all color tokens with new system
2. `ui/theme/Theme.kt` - Simplify to 2 themes (Black/White)
3. `ui/theme/Type.kt` - Update type scale
4. `ui/theme/ThemeManager.kt` - Reduce to 2 presets

### Keyboard
5. `ui/keyboard/KeyboardView.kt` - Complete rewrite of visual layer
6. `ui/keyboard/KeyboardState.kt` - Add/remove state for new UI
7. `ui/keyboard/KeyboardTheme.kt` - Replace 15 themes with 2

### Screens
8. `ui/screens/SplashScreen.kt` - Update colors, remove blur
9. `ui/screens/LoginScreen.kt` - Redesign with new theme
10. `ui/screens/MainDashboard.kt` - New nav bar design
11. `ui/screens/HomeScreen.kt` - New card design
12. `ui/screens/PromptsScreen.kt` - Match reference image style
13. `ui/screens/SettingsScreen.kt` - New list item design
14. `ui/screens/KeySoundsScreen.kt` - New card design
15. `ui/screens/KeyboardTestScreen.kt` - Update controls

### Assets (Optional)
16. `res/drawable/` - May need new icons (vector drawables)

---

## DO NOT TOUCH (Backend/Logic)

- `SynapseInputMethodService.kt` - Core IME logic
- `auth/AuthManager.kt` - Authentication
- `auth/SupabaseClientProvider.kt` - Backend client
- `data/model/SynapseModel.kt` - AI models
- `data/local/SynapseDatabase.kt` - Room database
- `data/local/dao/*` - All DAOs
- `data/local/entity/*` - All entities
- `data/local/repository/*` - All repositories
- `editor/TextEditorCore.kt` - Text editing
- `engine/OnDemandAiExecutionEngine.kt` - AI engine
- `ui/keyboard/EmojiProvider.kt` - Emoji data
- `ui/keyboard/KeySoundEngine.kt` - Sound generation
- `ui/keyboard/HapticEngine.kt` - Haptic feedback
- `ui/keyboard/KeyboardTheme.kt` - ONLY modify data, not logic
- All `BuildConfig` and API keys

---

## Mobile Design Skill Compliance

### MFRI Assessment
- Platform Clarity: 5 (Android only, clear)
- Interaction Complexity: 3 (keyboard + app, moderate)
- Performance Risk: 4 (IME service, animations)
- Offline Dependence: 2 (mostly works offline)
- Accessibility Risk: 3 (keyboard needs high contrast)
- **MFRI Score: 5 - Moderate** (proceed with validation)

### Anti-Patterns to Avoid
- [ ] No ScrollView for lists (use LazyColumn)
- [ ] No index as key (use stable IDs)
- [ ] No JS-thread animations (Compose animations are native)
- [ ] No console.log in prod (use Timber if needed)
- [ ] No pure white text on pure black (use #F0F0F5 on #0A0A0F)
- [ ] No touch targets < 48dp
- [ ] No gesture-only actions (always provide button alternative)

### Touch Target Checklist
- [ ] All tappable elements ≥ 48dp
- [ ] Primary actions in thumb zone (bottom)
- [ ] Destructive actions require confirmation
- [ ] Loading states for actions > 100ms
- [ ] Error recovery (retry + message)

### Performance Targets
- [ ] 60fps animations (16.67ms per frame)
- [ ] No memory leaks (dispose all controllers)
- [ ] Tested on low-end Android device
- [ ] Cold start < 2s
- [ ] List scroll jank-free

---

## Implementation Order

1. [x] **Phase 1: Theme System** (Color.kt, Theme.kt, ThemeManager.kt)
2. [x] **Phase 2: Keyboard Visual** (KeyboardView.kt, KeyboardTheme.kt)
3. [x] **Phase 3: App Screens** (All screens, one by one)
4. [x] **Phase 4: Animations** (Key press, transitions, polish)
5. [ ] **Phase 5: Testing** (Low-end device, battery, accessibility)

---

## Success Criteria

- [x] Only 2 themes exist (Premium Black, Premium White)
- [x] ZERO blur/haze/glassmorphism effects anywhere in the app
- [x] All screens have consistent visual language
- [x] Keyboard keys are nearly rectangular (2dp radius max)
- [x] AI toolbar icon is 28dp (larger than current)
- [x] Key press feels like Gboard (fast, responsive, pop + glow)
- [ ] App runs at 60fps on mid-range device
- [ ] Battery impact minimal (OLED dark mode)
- [x] All backend functionality preserved
- [x] Touch targets ≥ 48dp everywhere

---

*Generated with mobile-design skill guidelines applied.*
*Reference: Touch Psychology, Color System, Performance, Navigation docs.*
