package jp.cheerapps.howmanybiscuits.views.game

import jp.cheerapps.howmanybiscuits.views.BasePresenter
import jp.cheerapps.howmanybiscuits.views.BaseView

interface GameContract {
    interface View : BaseView<Presenter>

    interface Presenter : BasePresenter
}