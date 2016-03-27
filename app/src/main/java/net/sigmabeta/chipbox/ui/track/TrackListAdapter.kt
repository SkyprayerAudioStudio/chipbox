package net.sigmabeta.chipbox.ui.track

import android.view.View
import net.sigmabeta.chipbox.R
import net.sigmabeta.chipbox.model.domain.Game
import net.sigmabeta.chipbox.model.domain.Track
import net.sigmabeta.chipbox.ui.BaseArrayAdapter
import net.sigmabeta.chipbox.ui.ItemListView
import java.util.*

class TrackListAdapter(view: ItemListView<TrackViewHolder>, val showArt: Boolean) : BaseArrayAdapter<Track, TrackViewHolder>(view) {
    var playingTrackId: Long? = null
        set (value) {
            field = value
            notifyDataSetChanged()
        }

    var games: HashMap<Long, Game>? = null
        set (value) {
            field = value
            notifyDataSetChanged()
        }

    override fun getLayoutId(): Int {
        return if (showArt)
            R.layout.list_item_track
        else
            R.layout.list_item_track_game
    }

    override fun createViewHolder(view: View): TrackViewHolder {
        return TrackViewHolder(view, this)
    }

    override fun bind(holder: TrackViewHolder, item: Track) {
        holder.bind(item)
    }
}