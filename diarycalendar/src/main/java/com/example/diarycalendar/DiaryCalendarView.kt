package com.example.diarycalendar

import android.content.Context
import android.graphics.Typeface
import android.util.AttributeSet
import android.view.View
import android.widget.LinearLayout
import android.widget.TextView
import androidx.annotation.ColorRes
import androidx.core.view.isVisible
import com.example.diarycalendar.databinding.ItemCalendarDayBinding
import com.example.diarycalendar.databinding.LayoutBinding
import com.kizitonwose.calendarview.model.CalendarDay
import com.kizitonwose.calendarview.model.DayOwner
import com.kizitonwose.calendarview.ui.DayBinder
import com.kizitonwose.calendarview.ui.ViewContainer
import com.kizitonwose.calendarview.utils.yearMonth
import java.time.DayOfWeek
import java.time.LocalDate
import java.time.YearMonth
import java.time.format.DateTimeFormatter

class DiaryCalendarView
@JvmOverloads
constructor(
    context: Context,
    attrs: AttributeSet,
    defStyleAttr: Int = 0
) : LinearLayout(context, attrs, defStyleAttr) {

    private val binding: LayoutBinding
    private var selectedDate: LocalDate? = null
    private val today = LocalDate.now()

    private var eventDates = listOf<LocalDate>()

    private var onMonthChanged: (month: YearMonth) -> Unit = {}
    private var onDateSelected: (Day: LocalDate) -> Unit = {}

    private val formatter: DateTimeFormatter = DateTimeFormatter.ofPattern("MMMM dd, yyyy")


    fun setEventDates(events: List<LocalDate>) {
        eventDates = events
        binding.calendar.notifyCalendarChanged()
    }

    fun addOnDateSelectedListener(listener: (date: LocalDate) -> Unit) {
        this.onDateSelected = listener
    }

    fun addOnMonthChangedListener(listener: (month: YearMonth) -> Unit) {
        this.onMonthChanged = listener
    }

    fun smoothScrollToToday(){
        binding.calendar.smoothScrollToDate(LocalDate.now())
    }

    init {
        val rootView = inflate(getContext(), R.layout.layout, this)
        binding = LayoutBinding.bind(rootView)

        orientation = VERTICAL
    }

    override fun onFinishInflate() {
        super.onFinishInflate()
        setUpCalendarMonths()
        setUpCalendarDayContainers()
        setUpMonthChangeListener()
    }

    private fun setUpMonthChangeListener() = with(binding) {
        calendar.monthScrollListener = {

            if (it.month == today.month.value) {
                dateText.text = formatter.format(today)
            } else {
                //let's select the first day of the month. This will fire 2 events in very quick succession
                val firstDayOfMonth = LocalDate.of(it.year, it.month, 1)
                selectDate(firstDayOfMonth)
            }

            onMonthChanged(it.yearMonth)
        }
    }

    private fun setUpCalendarMonths() = with(binding) {
        val currentMonth = today.yearMonth
        // TODO: 26/12/20 Replace with the day user started using the app
        val startMonth =
            currentMonth.minusMonths(10)

        calendar.setup(startMonth, currentMonth, DayOfWeek.SUNDAY)
        calendar.scrollToMonth(currentMonth)
    }

    private fun selectDate(date: LocalDate) {
        if (selectedDate != date) {
            val oldDate = selectedDate
            selectedDate = date
            oldDate?.let { binding.calendar.notifyDateChanged(it) }
            binding.calendar.notifyDateChanged(date)

            binding.dateText.text = formatter.format(date)
            onDateSelected(date)
        }
    }


    internal fun TextView.setTextColorRes(@ColorRes color: Int) =
        setTextColor(context.getColor(color))

    private fun setUpCalendarDayContainers() = with(binding) {
        class DayViewContainer(view: View) : ViewContainer(view) {
            lateinit var day: CalendarDay
            val binding = ItemCalendarDayBinding.bind(view)
        }

        calendar.dayBinder = object : DayBinder<DayViewContainer> {
            override fun create(view: View) = DayViewContainer(view)
            override fun bind(container: DayViewContainer, day: CalendarDay) {
                container.binding.root.setOnClickListener {
                    if (day.owner == DayOwner.THIS_MONTH) {
                        selectDate(day.date)
                    }
                }
                container.day = day
                val textView = container.binding.dayText
                val dotView = container.binding.dotView

                textView.text = day.date.dayOfMonth.toString()


                if (day.owner == DayOwner.THIS_MONTH) {
                    textView.visibility = View.VISIBLE
                    dotView.visibility = View.INVISIBLE

                    when (day.date) {
                        today -> {
                            textView.setTextColorRes(R.color.calendar_black)
                            textView.setBackgroundResource(R.drawable.today_date_bg)
                            dotView.visibility = View.INVISIBLE
                        }
                        selectedDate -> {
                            textView.setBackgroundResource(R.drawable.selected_day_bg)
                            dotView.visibility = View.INVISIBLE
                        }
                        else -> {
                            if (eventDates.contains(day.date))
                                textView.setTypeface(textView.typeface, Typeface.BOLD_ITALIC)

                            dotView.isVisible = eventDates.contains(day.date)
                            textView.background = null
                        }
                    }
                } else {
                    textView.visibility = View.INVISIBLE
                    dotView.visibility = View.INVISIBLE
                }
            }
        }
    }
}