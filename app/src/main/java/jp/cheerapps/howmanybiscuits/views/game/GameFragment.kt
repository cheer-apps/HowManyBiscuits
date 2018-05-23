package jp.cheerapps.howmanybiscuits.views.game

import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup

import jp.cheerapps.howmanybiscuits.R


class GameFragment : Fragment(), GameContract.View {
    override val presenter by lazy { GamePresenter(this) }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.game_frag, container, false)
    }

    companion object {
        fun newInstance() = GameFragment()
    }
}
