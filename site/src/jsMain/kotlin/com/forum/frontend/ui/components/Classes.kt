package com.forum.frontend.ui.components

fun cls(vararg names: String?): Array<String> = names.filterNotNull().toTypedArray()