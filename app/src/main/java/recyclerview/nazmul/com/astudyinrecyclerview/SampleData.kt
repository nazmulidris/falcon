/*
 * Copyright 2018 Nazmul Idris. All rights reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package recyclerview.nazmul.com.astudyinrecyclerview

import java.util.*

// Data
val data = listOf(
        "One", "Two", "Three", "Four", "Five", "Six",
        "Seven", "Eight", "Nine", "Ten", "Eleven", "Twelve",
        "Thirteen", "Fourteen", "Fifteen", "Sixteen", "Seventeen",
        "Eighteen", "Nineteen", "Twenty", "Twenty One", "Twenty Two",
        "Twenty Two", "Twenty Three", "Twenty Four", "Twenty Five", "Twenty Six",
        "Twenty Sixteen", "Twenty Fourteen", "Twenty Fifteen", "Twenty Seventeen"
)

val dynamicData = mutableListOf<String>().apply {
    for (i in 1..30) {
        add(genSentence(2 + Random().nextInt(10)))
    }
}

private fun genSentence(maxWords: Int): String {
    with(StringBuilder()) {
        for (i in 1..maxWords) {
            append(genWord(Random().nextInt(20)))
            append(" ")
        }
        return toString()
    }
}

private fun genWord(maxChars: Int): String {
    with(StringBuilder()) {
        for (i in 1..maxChars) {
            append("X")
        }
        return toString()
    }
}
