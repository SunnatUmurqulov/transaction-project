package com.company.springdemo

fun Boolean.runIfFalse(func: () -> Unit) {
    if (!this) func()
}