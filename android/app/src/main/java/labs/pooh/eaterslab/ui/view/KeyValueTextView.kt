package labs.pooh.eaterslab.ui.view

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.View
import androidx.constraintlayout.widget.ConstraintLayout
import kotlinx.android.synthetic.main.text_view_key_value.view.*
import labs.pooh.eaterslab.R
import kotlin.math.exp


class KeyValueTextView(context: Context, attrs: AttributeSet?, defStyleAttr: Int) : ConstraintLayout(context, attrs, defStyleAttr) {

    val keyString: String
    private val valueString: String
    private val textSize: Float
    private val expandable: Boolean
    private var expanded: Boolean = false

    companion object {
        const val DEFAULT_TEXT_SIZE = 20f
        const val EXPAND_TEXT_PROPORTION = 0.8f
    }

    init {
        val ta = context.obtainStyledAttributes(attrs, R.styleable.KeyValueTextView, 0, 0)
        try {
            keyString = ta.getString(R.styleable.KeyValueTextView_key) ?: context.getString(R.string.key)
            valueString = ta.getString(R.styleable.KeyValueTextView_value) ?: context.getString(R.string.value)
            textSize = ta.getDimension(R.styleable.KeyValueTextView_textSize, DEFAULT_TEXT_SIZE)
            expandable = ta.getBoolean(R.styleable.KeyValueTextView_expandable, false)
        } finally {
            ta.recycle()
        }
        val inflater = context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
        inflater.inflate(R.layout.text_view_key_value, this, true)
    }


    constructor(context: Context, attrs: AttributeSet?) : this(context, attrs, android.R.attr.textViewStyle)

    constructor(context: Context) : this(context, null)

    override fun onFinishInflate() {
        super.onFinishInflate()
        initValues(keyString, valueString)
    }

    private fun initValues(keyString: String, valueString: String) {
        keyText.text = String.format(context.getString(R.string.key_format, keyString))
        valueText.text = valueString
        valueTextExpanded.text = valueString
        keyText.textSize = textSize
        valueText.textSize = textSize
        valueTextExpanded.textSize = textSize * EXPAND_TEXT_PROPORTION

        if (expandable) {
            expandButton.setImageResource(R.drawable.ic_expand_more)
            expandButton.visibility = View.VISIBLE
            expandButton.setOnClickListener { manageExpandableText() }
            keyValueRoot.setOnClickListener { manageExpandableText() }
            valueText.visibility = View.GONE
        }
    }

    private fun manageExpandableText() {
        if (expanded) {
            expandButton.setImageResource(R.drawable.ic_expand_more)
            valueTextExpanded.visibility = View.GONE
            expanded = false
        }
        else {
            expandButton.setImageResource(R.drawable.ic_expand_less)
            valueTextExpanded.visibility = View.VISIBLE
            expanded = true
        }
    }

    var text: String
        get() = valueText.text.toString()
        set(value) {
            valueText.text = value
            valueTextExpanded.text = value
        }

}
