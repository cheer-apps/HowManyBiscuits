package jp.cheerapps.howmanybiscuits

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import jp.cheerapps.howmanybiscuits.extensions.transaction
import jp.cheerapps.howmanybiscuits.views.game.GameFragment

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.main_act)

        if (savedInstanceState == null) {
            supportFragmentManager.transaction {
                add(R.id.container, GameFragment.newInstance())
            }
        }
    }
}
