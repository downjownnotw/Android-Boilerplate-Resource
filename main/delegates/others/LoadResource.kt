
class LoadResource(private val context: Context) {

    fun string(res: Int): String = context.getString(res)
    fun string(res: Int, vararg args: Any?): String = String.format(string(res), *args)
    fun stringStyling(res: Int): CharSequence = context.getText(res)
    fun stringStyling(originalText: String, vararg sentences: String): SpannableStringBuilder {
        val spannableBuilder = SpannableStringBuilder(originalText)
        sentences.forEach {sentence->
            val startIndex = originalText.indexOf(sentence)
            val endIndex = startIndex + sentence.length
            if (startIndex != -1)
                spannableBuilder.setSpan(StyleSpan(BOLD), startIndex, endIndex, SPAN_INCLUSIVE_INCLUSIVE)
        }
        return spannableBuilder
    }
    fun stringStyling(textView: TextView, res: Int, sentences: List<String> = emptyList()){
        val string = if (sentences.isEmpty()) string(res) else string(res, *sentences.toTypedArray())
        return styling(textView, string)
    }

    fun stringStyling(textView: TextView, res: Int, vararg args: Any?){
        val string = String.format(string(res), *args)
        return styling(textView, string)
    }

    private fun styling(textView: TextView, string: String){
        val styling = HtmlCompat.fromHtml(string, HtmlCompat.FROM_HTML_MODE_LEGACY)
        return textView.setText(styling, TextView.BufferType.SPANNABLE)
    }

    fun color(res: Int): Int = ContextCompat.getColor(context, res)

    fun drawable(res: Int): Drawable = ContextCompat.getDrawable(context, res)!!

    fun dimenSize(res: Int): Int = context.resources.getDimensionPixelSize(res)
}
