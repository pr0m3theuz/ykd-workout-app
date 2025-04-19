package com.example.myapplication.common.util

import android.util.Log
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.LinkAnnotation
import androidx.compose.ui.text.ParagraphStyle
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.TextLinkStyles
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextIndent
import androidx.compose.ui.unit.sp
import com.example.myapplication.common.domain.model.Quadruple
import java.util.Stack

fun createAnnotatedLinkString(text: String, textLinkStyles: TextLinkStyles?): AnnotatedString {
    data class ListItem(
        val content: String,
        val level: Int,
        val isOrdered: Boolean,
        var startIndex: Int,
        var endIndex: Int,
    )

    val linkPattern = "<a href=\"(.*?)\">(.*?)</a>".toRegex()
    val boldPattern = "<b>(.*?)</b>".toRegex()
    val headerPattern = "<h1>(.*?)</h1>".toRegex()
    val olPattern = "<ol>(.*?)</ol>".toRegex(RegexOption.DOT_MATCHES_ALL)
    val ulPattern = "<ul>(.*?)</ul>".toRegex(RegexOption.DOT_MATCHES_ALL)
    val liPattern = "<li>(.*?)</li>".toRegex()

    var fullText: String
    val fullTextList = mutableListOf<String>()
    val links = mutableListOf<Quadruple<String, String, Int, Int>>()
    val spans = mutableListOf<Quadruple<String, Any, Int, Int>>()
    val listItems = mutableListOf<ListItem>()

    fun processPattern(pattern: Regex, style: SpanStyle, text: String): String {
        var currentIndex = 0
        var updatedText = ""
        Log.d("createAnnotatedLinkString.processPattern Pattern", "Pattern: $pattern")
        pattern.findAll(text, currentIndex).forEach { matchResult ->
            Log.d(
            "createAnnotatedLinkString.processPattern Match",
            "Match Result: ${matchResult.value}"
          )

            val (content) = matchResult.destructured
            Log.d("createAnnotatedLinkString.processPattern Content", "Content: $content")
            val startIndex = matchResult.range.first
            val endIndex = matchResult.range.last + 1

            updatedText += text.substring(currentIndex, startIndex) + content

            spans.add(
                Quadruple(
                    matchResult.groupValues[1],
                    style,
                    updatedText.length - content.length,
                    updatedText.length,
                )
            )
            currentIndex = endIndex
        }
        updatedText += text.substring(currentIndex)
        return updatedText
    }

    fun processLinkPattern(pattern: Regex, text: String): String {
        var currentIndex = 0
        var updatedText = ""
        linkPattern.findAll(text, currentIndex).forEach { matchResult ->
            val (url, linkText) = matchResult.destructured
            val startIndex = matchResult.range.first
            val endIndex = matchResult.range.last + 1

            updatedText += text.substring(currentIndex, startIndex) + linkText
            fullTextList.add(text.substring(currentIndex, startIndex))
            fullTextList.add(linkText)
            links.add(
                Quadruple(
                    url,
                    linkText,
                    updatedText.length - linkText.length,
                    updatedText.length
                )
            )
            currentIndex = endIndex
        }
        updatedText += text.substring(currentIndex)
        return updatedText
    }

    fun processList(pattern: Regex, isOrdered: Boolean, style: Any, text: String): String {
        var currentIndex = 0
        var updatedText = ""
        Log.d("createAnnotatedLinkString Pattern", "Pattern: $pattern")
        pattern.findAll(text, currentIndex).forEach { matchResult ->
            val listContent = matchResult.groupValues[1]
            val startIndex = matchResult.range.first
            val endIndex = matchResult.range.last + 1
            var listText = ""
            updatedText += text.substring(currentIndex, startIndex) + "\n"

            val listStart = updatedText.length

            var itemNumber = 1
            liPattern.findAll(listContent).forEach { liMatch ->
                val itemContent = liMatch.groupValues[1]
                val bulletPoint = if (isOrdered) "${itemNumber++}. " else "• "
                listText += bulletPoint + itemContent + "\n"
                updatedText += bulletPoint + itemContent + "\n"
                //        println("List Full Text: $fullText")
            }

            spans.add(Quadruple(listText, style, listStart, updatedText.length))
            currentIndex = endIndex
        }
        updatedText += text.substring(currentIndex)
        return updatedText
    }

    fun processNestedLists(html: String): String {
        val tagPattern = "<(/?(?:ol|ul|li))[^>]*>|([^<]+)".toRegex()
        val listStack = Stack<Boolean>() // true for ol, false for ul
        val result = StringBuilder()
        var currentLevel = 0
        var orderNumbers = mutableMapOf<Int, Int>()

        fun getPrefix(level: Int, isOrdered: Boolean): String {
            val indent = "".repeat(level)
            return if (isOrdered) {
                val number = orderNumbers.getOrDefault(level, 0) + 1
//        println("getPrefix() level: $level")
//        println("getPrefix() number: $number")
                orderNumbers[level] = number
                "$indent$number. "
            } else {
                "$indent• "
            }
        }

        tagPattern.findAll(html).forEach { matchResult ->
            val (tag, content) = matchResult.destructured
            when {
                tag.startsWith("/") -> {
//          println("Closing tag: $tag")
                    if (tag == "/ol" || tag == "/ul") {
                        listStack.pop()
//            println("Popped listStack: $tag, ${listStack.size}")
                        currentLevel--
//            println("Current Level: $currentLevel")
                        orderNumbers.remove(currentLevel + 1)
                    }
                }

                tag == "ol" || tag == "ul" -> {
//          println("Opening tag: $tag")
                    listStack.push(tag == "ol")
//          println("Pushed listStack: $tag, ${listStack.size}")
                    currentLevel++
//          println("Current Level: $currentLevel")
                }

                tag == "li" -> {
                    // Do nothing, wait for content
                }

                content.isNotBlank() -> {
                    if (listStack.isNotEmpty()) {
                        val start = html.lastIndexOf(content)
                        val end = start + content.length

                        val isOrdered = listStack.peek()
                        val prefix = getPrefix(currentLevel, isOrdered)

                        listItems.add(
                            ListItem(
                                prefix + content.trim(),
                                currentLevel,
                                isOrdered,
                                start,
                                end
                            )
                        )
                        result.append(prefix)
                    }
                    result.append(content.trim()).append("")
                }
            }
        }

//    println("processNestedLists():\n\n" + result.toString().trim())

        return result.toString().trim()
    }

    fullText = processLinkPattern(linkPattern, text)
    fullText = processPattern(boldPattern, SpanStyle(fontWeight = FontWeight.Bold), fullText)
    fullText = processPattern(headerPattern, SpanStyle(fontWeight = FontWeight.Bold), fullText)
    //  fullText =
    //    processList(
    //      olPattern,
    //      true,
    //      ParagraphStyle(textIndent = TextIndent(firstLine = 10.sp, restLine = 24.sp)),
    //      fullText,
    //    )
    //  fullText =
    //    processList(
    //      ulPattern,
    //      false,
    //      ParagraphStyle(textIndent = TextIndent(firstLine = 10.sp, restLine = 18.sp)),
    //      fullText,
    //    )

    fullText = processNestedLists(fullText)
    // fullText += text.substring(currentIndex)

    fun removeHtmlTags(html: String): String {
        val tagPattern = "<[^>]+>".toRegex()
        return html
            .replace(tagPattern, "")
            .replace("&amp;", "&")
            .replace("&lt;", "<")
            .replace("&gt;", ">")
            .replace("&quot;", "\"")
            .replace("&#39;", "'")
    }

    fullText = fullText.replace(Regex("`br`"), "\n")

    spans.forEachIndexed { index, (content, style, startIndex, endIndex) ->
        val start = fullText.lastIndexOf(content)
        val end = start + content.length

        spans[index] = Quadruple(content, style, start, end)
    }

    links.forEachIndexed { index, (url, linkText, startIndex, endIndex) ->
        val start = fullText.lastIndexOf(linkText)
        val end = start + linkText.length

        links[index] = Quadruple(url, linkText, start, end)
    }

    listItems.forEachIndexed { index, item ->
        val start = fullText.lastIndexOf(item.content)
        val end = start + item.content.length

        listItems[index] = ListItem(item.content, item.level, item.isOrdered, start, end)
    }

    val annotatedString = buildAnnotatedString {
        append(fullText)

        spans.forEach { (content, style, startIndex, endIndex) ->
//      println("span content: $content")
//      println("spans Start Index: $startIndex")
//      println("spans End Index: $endIndex")
//      println("spans Start Index <= End Index: ${startIndex <= endIndex}")
            when (style) {
                is SpanStyle -> addStyle(style = style, start = startIndex, end = endIndex)
                is ParagraphStyle -> addStyle(style = style, start = startIndex, end = endIndex)
            }
        }

        links.forEach { (url, linkText, startIndex, endIndex) ->
//      println("links Start Index: $startIndex")
//      println("links End Index: $endIndex")
//      println("links Start Index <= End Index: ${startIndex <= endIndex}")
            addLink(
                url = LinkAnnotation.Url(url = url, styles = textLinkStyles),
                start = startIndex,
                end = endIndex,
            )
            addStringAnnotation(tag = "URL", annotation = url, start = startIndex, end = endIndex)
        }

        listItems.forEach { item ->
//      println("listItems Start Index: ${item.startIndex}")
//      println("listItems End Index: ${item.endIndex}")
//      println("listItems Start Index <= End Index: ${item.startIndex <= item.endIndex}")
            addStyle(
                style =
                ParagraphStyle(
                    textIndent =
                    TextIndent(
                        firstLine = (item.level * if (item.level > 1) 16 else 10).sp,
                        restLine = ((item.level).times(if (item.isOrdered) 24 else 18)).sp,
                    )
                ),
                start = item.startIndex, // if (item.level >= 0) item.startIndex else 0,
                end = item.endIndex,
            )
            // a bold SpanStyle to the bullet points or numbers.
            /*      if (item.isOrdered || item.level > 0) {
              addStyle(
                SpanStyle(fontWeight = FontWeight.Bold),
                start = item.startIndex,
                end = item.endIndex
              )
            }*/
        }
    }

    //  val spannableString = SpannableString(fullText)

    Log.d("createAnnotatedLinkString Full Text", "Full Text:\n\n $fullText\n\n~~~~~~~~~\n\n")

    //  fullTextList.forEachIndexed { index, it -> println("Text $index: $it") }

    Log.d("createAnnotatedLinkString", "\n\n~~~~~~~~~\n\n")

    //  links.forEach { (url, linkText, start, end) ->
    //    println("URL: $url")
    //    println("Link Text: $linkText")
    //    println("Start: $start")
    //    println("End: $end")
    //    /* spannableString.setSpan(
    //        URLSpan(url),
    //        start,
    //        end,
    //        Spanned.SPAN_EXCLUSIVE_EXCLUSIVE
    //    )*/
    //  }

    Log.d("createAnnotatedLinkString annotatedString Result", annotatedString.toString())
    return annotatedString
}
