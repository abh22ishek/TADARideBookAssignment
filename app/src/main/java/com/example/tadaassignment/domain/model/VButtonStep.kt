package com.example.tadaassignment.domain.model

/** The 3 states of the bottom V Button, in order. */
enum class VButtonStep(val label: String) {
    SET_A("Set A"),
    SET_B("Set B"),
    BOOK("Book")
}

fun nextButtonStep(slotA: SafeLocation?, slotB: SafeLocation?): VButtonStep = when {
    slotA == null -> VButtonStep.SET_A
    slotB == null -> VButtonStep.SET_B
    else -> VButtonStep.BOOK
}
