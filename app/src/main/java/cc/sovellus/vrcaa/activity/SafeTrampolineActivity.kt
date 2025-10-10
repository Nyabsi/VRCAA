package cc.sovellus.vrcaa.activity

import android.app.Activity
import android.os.Bundle

// a trampoline activity might spawn and crash our app unless the user runs:
// `adb shell cmd appwidget remove-all cc.sovellus.vrcaa`
// but since this won't happen, let's mitigate this by forcefully black holing the activity
class SafeTrampolineActivity  : Activity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        finish()
    }
}