package jp.cheerapps.howmanybiscuits.views

interface BaseView<out T> {
    val presenter: T
}