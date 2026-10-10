package com.asar.app.data

import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.snapshots.SnapshotStateList
import com.asar.app.data.model.HelpTask
import com.asar.app.data.model.Person

/**
 * In-memory task store backed by Compose snapshot state, so every screen observing
 * [tasks] recomposes automatically when a task changes. No database, no network.
 */
class TaskRepository(me: Person) {

    private val _tasks: SnapshotStateList<HelpTask> =
        mutableStateListOf<HelpTask>().apply {
            addAll(MockData.initialTasks(me))
            addAll(MockData.completedTasks(me))
        }

    val tasks: List<HelpTask> get() = _tasks

    fun find(id: String): HelpTask? = _tasks.firstOrNull { it.id == id }

    fun add(task: HelpTask) {
        _tasks.add(0, task)
    }

    fun update(id: String, transform: (HelpTask) -> HelpTask) {
        val index = _tasks.indexOfFirst { it.id == id }
        if (index >= 0) _tasks[index] = transform(_tasks[index])
    }

    /** Renames the local user everywhere (they may register with a new name). */
    fun replaceCurrentUser(me: Person) {
        for (i in _tasks.indices) {
            val task = _tasks[i]
            _tasks[i] = task.copy(
                requester = if (task.requester.id == me.id) me else task.requester,
                applications = task.applications.map {
                    if (it.volunteer.id == me.id) it.copy(volunteer = me) else it
                },
                reviews = task.reviews.map {
                    if (it.volunteerId == me.id) it.copy(volunteerName = me.name) else it
                },
            )
        }
    }
}
