package com.example.reminds.utils.annotation

import androidx.annotation.StringDef
import com.example.reminds.utils.annotation.TagName.Companion.PASSWORD_INCORRECT_TAG

@StringDef(PASSWORD_INCORRECT_TAG)
annotation class TagName {
    companion object {
        const val PASSWORD_INCORRECT_TAG = "password_incorrect_tag"
    }
}