package com.pk.signlanguageapp.utils

fun <T> List<T>.sliceLast(n: Int): List<T> {
    return if (this.size >= n) {
        this.subList(this.size - n, this.size)
    } else {
        this
    }
}
