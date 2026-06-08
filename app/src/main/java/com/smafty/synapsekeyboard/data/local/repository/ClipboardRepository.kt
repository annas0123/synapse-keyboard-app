package com.smafty.synapsekeyboard.data.local.repository

import com.smafty.synapsekeyboard.data.local.dao.ClipboardDao
import com.smafty.synapsekeyboard.data.local.entity.ClipboardEntity
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.withContext

/**
 * ClipboardRepository — the single access point for clipboard history data.
 *
 * All write operations are dispatched to [Dispatchers.IO] as required by architecture rule #2:
 * "Clipboard background capture listener runs on the UI Main Thread of the service.
 *  You must route database insertions to a coroutine with Dispatchers.IO."
 *
 * The [getAllHistoryFlow] is a Room Flow and can be collected on any dispatcher.
 */
class ClipboardRepository(private val dao: ClipboardDao) {

    /** Live stream of all clipboard items — pinned first, then newest first. */
    val allHistoryFlow: Flow<List<ClipboardEntity>> = dao.getAllHistoryFlow()

    /**
     * Safely inserts or de-duplicates a clipboard text entry, then prunes history.
     * Always call from a coroutine; internally switches to [Dispatchers.IO].
     */
    suspend fun addItem(text: String) = withContext(Dispatchers.IO) {
        dao.safeInsertWithDeduplication(text)
    }

    /** Toggles the pinned status of a clipboard entry. */
    suspend fun togglePin(id: Int, currentlyPinned: Boolean) = withContext(Dispatchers.IO) {
        dao.updatePinStatus(id, !currentlyPinned)
    }

    /** Permanently deletes a single clipboard entry by its ID. */
    suspend fun deleteItem(id: Int) = withContext(Dispatchers.IO) {
        dao.deleteById(id)
    }

    /**
     * Clears all unpinned clipboard history.
     * Pinned items are preserved to honour the user's explicit preference.
     */
    suspend fun clearUnpinned() = withContext(Dispatchers.IO) {
        dao.clearUnpinned()
    }
}
