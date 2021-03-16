package com.example.common.base.extesion

import androidx.annotation.StringDef
import com.example.common.base.extesion.TagName.Companion.PASSWORD_INCORRECT_TAG

class TagException(@TagName val name: String, val message: String?)

@StringDef(PASSWORD_INCORRECT_TAG)
annotation class TagName {
    companion object {
        const val PASSWORD_INCORRECT_TAG = "password_incorrect_tag"
    }
}