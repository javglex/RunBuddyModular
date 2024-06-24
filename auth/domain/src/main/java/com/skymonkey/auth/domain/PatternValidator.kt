package com.skymonkey.auth.domain

/**
 * user data validator helper interface. check if string matches a particular pattern
 */
interface PatternValidator {
    fun matches(value: String): Boolean
}
