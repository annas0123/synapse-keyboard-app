package com.smafty.synapsekeyboard.ui.keyboard

enum class EmojiCategory {
    RECENT,
    SMILEYS,
    PEOPLE,
    ANIMALS,
    FOOD,
    TRAVEL,
    ACTIVITIES,
    OBJECTS,
    SYMBOLS,
    FLAGS
}

object EmojiProvider {

    private const val PREFS_KEY_RECENTS = "emoji_recents"
    private const val MAX_RECENTS = 32

    // In-memory LIFO list of recently used emojis (newest first)
    private val recentEmojis: ArrayDeque<String> = ArrayDeque()

    val allCategories: List<EmojiCategory> = EmojiCategory.entries.toList()

    val categoryIcons: Map<EmojiCategory, String> = mapOf(
        EmojiCategory.RECENT to "🕐",
        EmojiCategory.SMILEYS to "😀",
        EmojiCategory.PEOPLE to "👤",
        EmojiCategory.ANIMALS to "🐾",
        EmojiCategory.FOOD to "🍔",
        EmojiCategory.TRAVEL to "✈️",
        EmojiCategory.ACTIVITIES to "⚽",
        EmojiCategory.OBJECTS to "💡",
        EmojiCategory.SYMBOLS to "♾️",
        EmojiCategory.FLAGS to "🏁"
    )

    fun getEmojis(category: EmojiCategory): List<String> = when (category) {
        EmojiCategory.RECENT -> recentEmojis.toList()
        EmojiCategory.SMILEYS -> smileys
        EmojiCategory.PEOPLE -> people
        EmojiCategory.ANIMALS -> animals
        EmojiCategory.FOOD -> food
        EmojiCategory.TRAVEL -> travel
        EmojiCategory.ACTIVITIES -> activities
        EmojiCategory.OBJECTS -> objects
        EmojiCategory.SYMBOLS -> symbols
        EmojiCategory.FLAGS -> flags
    }

    /** Load the persisted recent emojis from SharedPreferences (call on IME start). */
    fun loadRecents(prefs: android.content.SharedPreferences) {
        val raw = prefs.getString(PREFS_KEY_RECENTS, null) ?: return
        recentEmojis.clear()
        raw.split("||").filter { it.isNotBlank() && isEmoji(it) }.take(MAX_RECENTS).forEach {
            recentEmojis.addLast(it)
        }
    }

    /**
     * Record a newly used emoji — LIFO: emoji jumps to position 0.
     * If it was already in the list, it is moved (not duplicated).
     * Persists immediately to SharedPreferences.
     */
    fun recordUsed(emoji: String, prefs: android.content.SharedPreferences) {
        recentEmojis.remove(emoji)          // Remove if already exists (move-to-front)
        recentEmojis.addFirst(emoji)        // Insert at top (LIFO)
        while (recentEmojis.size > MAX_RECENTS) recentEmojis.removeLast()  // Cap at 32
        saveRecents(prefs)
    }

    private fun saveRecents(prefs: android.content.SharedPreferences) {
        prefs.edit()
            .putString(PREFS_KEY_RECENTS, recentEmojis.joinToString("||"))
            .apply()
    }

    private val smileys: List<String> = listOf(
        "😀", "😃", "😄", "😁", "😆", "🥹", "😅", "🤣", "😂", "🙂",
        "🙃", "😉", "😊", "😇", "🥰", "😍", "🤩", "😘", "😗", "☺",
        "😚", "😙", "🥲", "😋", "😛", "😜", "🤪", "😝", "🤑", "🤗",
        "🤭", "🫢", "🫣", "🤫", "🤔", "🫡", "🤐", "🤨", "😐", "😑",
        "😶", "🫥", "😏", "😒", "🙄", "😬", "🤥", "🌚", "🌝", "😌",
        "😔", "😪", "🤤", "😴", "😷", "🤒", "🤕", "🤢", "🤮", "🤧",
        "🥵", "🥶", "🥴", "😵", "🤯", "🤠", "🥳", "🥸", "😎", "🤓",
        "🧐", "😕", "🫤", "😟", "🙁", "😮", "😯", "😲", "😳", "🥺",
        "😦", "😧", "😨", "😰", "😥", "😢", "😭", "😱", "😖", "😣",
        "😞", "😓", "😩", "😫", "🥱", "😤", "😡", "😠", "🤬", "😈",
        "👿", "💀", "☠️", "💩", "🤡", "👹", "👺", "👻", "👽", "👾",
        "🤖", "😺", "😸", "😹", "😻", "😼", "😽", "🙀", "😿", "😾"
    )

    private val people: List<String> = listOf(
        "👋", "🤚", "🖐", "✋", "🖖", "👌", "🤌", "🤏", "✌", "🤞",
        "🫰", "🤟", "🤘", "🤙", "🫵", "🫱", "🫲", "🫳", "🫴", "👈",
        "👉", "👆", "🖕", "👇", "☝", "👍", "👎", "✊", "👊", "🤛",
        "🤜", "👏", "🫶", "🙌", "👐", "🤲", "🤝", "🙏", "✍", "💅",
        "🤳", "💪", "🦾", "🦿", "🦵", "🦶", "👂", "🦻", "👃", "🧠",
        "🫀", "🫁", "🦷", "🦴", "👀", "👁", "👅", "👄", "🫦", "👶",
        "🧒", "👦", "👧", "🧑", "👱", "👨", "🧔", "👩", "🧓", "👴",
        "👵", "👮", "👷", "💂", "🕵", "👩‍⚕️", "👩‍🎓", "👩‍🏫", "👩‍⚖️", "👩‍🌾"
    )

    private val animals: List<String> = listOf(
        "🐶", "🐱", "🐭", "🐹", "🐰", "🦊", "🐻", "🐼", "🐨", "🐯",
        "🦁", "🐮", "🐷", "🐸", "🐵", "🙈", "🙉", "🙊", "🐒", "🐔",
        "🐧", "🐦", "🐤", "🐣", "🦆", "🦅", "🦉", "🦇", "🐺", "🐗",
        "🐴", "🦄", "🐝", "🪱", "🐛", "🦋", "🐌", "🐞", "🐜", "🪰",
        "🪲", "🪳", "🦟", "🦗", "🕷", "🕸", "🦂", "🐢", "🐍", "🦎",
        "🦖", "🦕", "🐙", "🦑", "🦐", "🦞", "🦀", "🐡", "🐠", "🐟",
        "🐬", "🐳", "🐋", "🦈", "🦭", "🐊", "🐅", "🐆", "🦓", "🦍",
        "🦧", "🦣", "🐘", "🦛", "🦏", "🐪", "🐫", "🦒", "🦘", "🦬",
        "🐃", "🐂", "🐄", "🐎", "🐖", "🐏", "🐑", "🦙", "🐐", "🦌",
        "🐕", "🐩", "🦮", "🐈", "🐓", "🦃", "🦤", "🦚", "🦜", "🦢",
        "🦩", "🕊", "🐇", "🦝", "🦨", "🦡", "🦫", "🦦", "🦥", "🐁",
        "🐀", "🐿", "🦔", "🐾", "🐉", "🐲"
    )

    private val food: List<String> = listOf(
        "🍏", "🍎", "🍐", "🍊", "🍋", "🍌", "🍉", "🍇", "🍓", "🫐",
        "🍈", "🍒", "🍑", "🥭", "🍍", "🥥", "🥝", "🍅", "🍆", "🥑",
        "🥦", "🥬", "🥒", "🌶", "🫑", "🌽", "🥕", "🧄", "🧅", "🥔",
        "🍠", "🫘", "🥐", "🥖", "🫓", "🥨", "🧀", "🥚", "🍳", "🧈",
        "🥞", "🧇", "🥓", "🥩", "🍗", "🍖", "🦴", "🌭", "🍔", "🍟",
        "🍕", "🫔", "🌮", "🌯", "🥗", "🥘", "🫕", "🥫", "🍝", "🍜",
        "🍲", "🍛", "🍣", "🍱", "🥟", "🦪", "🍤", "🍙", "🍚", "🍘",
        "🍥", "🥮", "🍡", "🧁", "🍰", "🎂", "🍮", "🍭", "🍬", "🍫",
        "🍿", "🍩", "🍪", "🌰", "🥜", "🍯", "🥛", "🍼", "🫖", "☕",
        "🍵", "🧃", "🥤", "🧋", "🍺", "🍻", "🥂", "🍷", "🍸", "🍹",
        "🧉", "🍾", "🥃", "🍶"
    )

    private val travel: List<String> = listOf(
        "✈️", "🚀", "🚁", "🛸", "🚂", "🚃", "🚄", "🚅", "🚆", "🚇",
        "🚈", "🚉", "🚊", "🚝", "🚞", "🚋", "🚌", "🚍", "🚎", "🚐",
        "🚑", "🚒", "🚓", "🚔", "🚕", "🚖", "🚗", "🚘", "🚙", "🛻",
        "🚚", "🚛", "🚜", "🏎", "🏍", "🛵", "🛺", "🚲", "🛴", "🛹",
        "🛼", "🚏", "🛣", "🛤", "🛢", "⛽", "🛞", "🚨", "🚥", "🚦",
        "🛑", "🚧", "⚓", "🛟", "⛵", "🛶", "🚤", "🛳", "⛴", "🛥",
        "🚢", "🏠", "🏡", "🏘", "🏚", "🏗", "🏭", "🏢", "🏬", "🏣",
        "🏤", "🏥", "🏦", "🏨", "🏩", "💒", "🏛", "⛪", "🕌", "🕍",
        "🛕", "🕋", "⛩", "🌅", "🌄", "🌇", "🌆", "🏙", "🌃", "🌌",
        "🌉", "🌁", "🗾", "🏔", "⛰", "🌋", "🗻", "🏕", "🏖", "🏜",
        "🏝", "🏞"
    )

    private val activities: List<String> = listOf(
        "⚽", "🏀", "🏈", "⚾", "🥎", "🎾", "🏐", "🏉", "🥏", "🎱",
        "🪀", "🏓", "🏸", "🏒", "🏑", "🥍", "🏏", "🪃", "🥅", "⛳",
        "🪁", "🏹", "🎣", "🤿", "🥊", "🥋", "🎽", "🛹", "🛼", "🛷",
        "⛸", "🥌", "🎿", "⛷", "🏂", "🪂", "🏋", "🏌", "🤸", "🤼",
        "🤽", "🤾", "🤺", "⛹", "🧗", "🧘", "🏄", "🏊", "🚣", "🧖",
        "💆", "💇", "🎪", "🎭", "🎨", "🎬", "🎤", "🎧", "🎼", "🎹",
        "🥁", "🪘", "🎷", "🎺", "🎸", "🪕", "🎻", "🎲", "♟", "🎯",
        "🎳", "🎮", "🕹", "🎰", "🧩"
    )

    private val objects: List<String> = listOf(
        "💡", "🔦", "🕯", "🧯", "🛢", "🪫", "💴", "💵", "💶", "💷",
        "🪙", "💰", "💳", "💎", "⚖", "🪜", "🧰", "🪛", "🔧", "🔩",
        "⚙", "🪤", "🧲", "🔫", "💣", "🪓", "🔪", "🗡", "🛡", "🪚",
        "🔑", "🗝", "🚪", "🪑", "🛋", "🛏", "🛌", "🪆", "🧸", "🪅",
        "🖼", "🛍", "🛒", "🎁", "🎈", "🎏", "🎀", "🎊", "🪩", "📱",
        "📲", "💻", "⌨", "🖥", "🖨", "🖱", "🖲", "💾", "💿", "📀",
        "🧮", "📷", "📸", "📹", "🎥", "📽", "🎬", "📺", "📻", "🎙",
        "🎚", "🎛", "🧭", "⏱", "⏲", "⏰", "🕰", "⌛", "⏳", "📡",
        "🔋", "🔌", "🪔"
    )

    private val symbols: List<String> = listOf(
        "❤️", "🧡", "💛", "💚", "💙", "💜", "🖤", "🤍", "🤎", "💔",
        "❣", "💕", "💞", "💓", "💗", "💖", "💘", "💝", "💟", "☮",
        "✝", "☪", "🕉", "☸", "✡", "🔯", "🕎", "☯", "☦", "🛐",
        "⛎", "♈", "♉", "♊", "♋", "♌", "♍", "♎", "♏", "♐",
        "♑", "♒", "♓", "🆔", "⚛", "🉑", "☢", "☣", "📴", "📳",
        "🈶", "🈚", "🈸", "🈺", "🈷", "✴", "🆚", "💮", "🉐", "🈴",
        "🈵", "🈹", "🈲", "🅰", "🅱", "🆎", "🆑", "🅾", "🆘", "❌",
        "⭕", "🛑", "⛔", "📛", "🚫", "💯", "💢", "♨", "🚷", "🚯",
        "🚳", "🚱", "🔞", "📵", "🚭", "❗", "❕", "❓", "❔", "‼",
        "⁉", "🔅", "🔆", "〽", "⚠", "🚸", "🔱", "⚜", "🔰", "♻",
        "✅", "🈯", "💹", "❇", "✳", "❎", "🌐", "💠", "Ⓜ", "🌀",
        "💤", "🏧", "🚾", "♿", "🅿", "🈳", "🈂", "🛗", "🛂", "🛃",
        "🛄", "🛅"
    )

    private val flags: List<String> = listOf(
        "🏳", "🏴", "🏁", "🚩", "🏳️‍🌈", "🏳️‍⚧️",
        "🇺🇸", "🇬🇧", "🇨🇦", "🇦🇺", "🇩🇪", "🇫🇷",
        "🇯🇵", "🇰🇷", "🇨🇳", "🇮🇳", "🇧🇷", "🇲🇽",
        "🇷🇺", "🇮🇹", "🇪🇸", "🇵🇹", "🇳🇱", "🇧🇪",
        "🇨🇭", "🇦🇹", "🇸🇪", "🇳🇴", "🇩🇰", "🇫🇮",
        "🇮🇪", "🇵🇱", "🇨🇿", "🇬🇷", "🇹🇷", "🇪🇬",
        "🇿🇦", "🇳🇬", "🇰🇪", "🇦🇷", "🇨🇴", "🇵🇪",
        "🇻🇪", "🇨🇱", "🇪🇨", "🇵🇭", "🇹🇭", "🇻🇳",
        "🇮🇩", "🇲🇾", "🇸🇬", "🇵🇰", "🇧🇩", "🇱🇰",
        "🇳🇵", "🇦🇫", "🇮🇶", "🇮🇷", "🇸🇦", "🇦🇪",
        "🇶🇦", "🇰🇼", "🇴🇲", "🇯🇴", "🇱🇧", "🇮🇱",
        "🇵🇸"
    )

    private val allEmojisSet: Set<String> by lazy {
        (smileys + people + animals + food + travel + activities + objects + symbols + flags).toSet()
    }

    fun isEmoji(char: String): Boolean {
        return allEmojisSet.contains(char)
    }
}
