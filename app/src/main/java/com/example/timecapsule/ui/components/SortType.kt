package com.example.timecapsule.ui.components

// Add SortType enum
enum class SortType(val label: String, val menuText: String) {
    // label: text shown inside FAB when selected, menuText: text shown in dropdown menu
    Newest("Newest", "Newest"),
    Oldest("Oldest", "Oldest"),
    MostForgotten("Forgotten (most > least)", "Most forgotten"),
    LeastForgotten("Forgotten (least > most)", "Least forgotten"),
    LeastRecentlyDug("Recent (least > most)", "Least recently dug"),
    MostRecentlyDug("Recent (most > least)", "Most recently dug"),
    LeastDug("Least dug", "Least dug"),
    MostDug("Most dug", "Most dug");

    companion object {
        val menuOptions = listOf(
            Newest,
            Oldest,
            MostForgotten,
            LeastForgotten,
            LeastRecentlyDug,
            MostRecentlyDug,
            LeastDug,
            MostDug
        )
    }
}