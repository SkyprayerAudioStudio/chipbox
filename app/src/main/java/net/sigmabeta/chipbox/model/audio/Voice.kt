package net.sigmabeta.chipbox.model.audio

import net.sigmabeta.chipbox.model.domain.ListItem

class Voice(val position: Int, val name: String) : ListItem {
    var enabled: Boolean = true

    override fun isTheSameAs(theOther: ListItem?): Boolean {
        if (theOther is Voice) {
            if (theOther.name == this.name) {
                return true
            }
        }

        return false
    }

    override fun hasSameContentAs(theOther: ListItem?): Boolean {
        if (theOther is Voice) {
            if (theOther.enabled == this.enabled) {
                return true
            }
        }

        return false
    }

    override fun getChangeType(theOther: ListItem?): Int {
        if (theOther is Voice) {
            if (theOther.enabled != this.enabled) {
                return CHANGE_VOICE
            }
        }

        return ListItem.CHANGE_ERROR
    }

    companion object {
        val CHANGE_VOICE = 1
    }
}