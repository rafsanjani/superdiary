package com.foreverrafs.superdiary.design.icons

import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.painter.Painter
import org.jetbrains.compose.resources.DrawableResource
import org.jetbrains.compose.resources.painterResource
import superdiary.design_system.generated.resources.Res
import superdiary.design_system.generated.resources.ic_add
import superdiary.design_system.generated.resources.ic_bar_chart
import superdiary.design_system.generated.resources.ic_check_circle
import superdiary.design_system.generated.resources.ic_close
import superdiary.design_system.generated.resources.ic_delete
import superdiary.design_system.generated.resources.ic_done
import superdiary.design_system.generated.resources.ic_favorite
import superdiary.design_system.generated.resources.ic_favorite_border
import superdiary.design_system.generated.resources.ic_format_bold
import superdiary.design_system.generated.resources.ic_format_italic
import superdiary.design_system.generated.resources.ic_format_list_bulleted
import superdiary.design_system.generated.resources.ic_format_list_numbered
import superdiary.design_system.generated.resources.ic_format_strikethrough
import superdiary.design_system.generated.resources.ic_format_underlined
import superdiary.design_system.generated.resources.ic_lightbulb
import superdiary.design_system.generated.resources.ic_lightbulb_outline
import superdiary.design_system.generated.resources.ic_list
import superdiary.design_system.generated.resources.ic_radio_button_unchecked
import superdiary.design_system.generated.resources.ic_search
import superdiary.design_system.generated.resources.ic_sort
import superdiary.design_system.generated.resources.ic_stacked_bar_chart

enum class SuperDiaryIcon(val res: DrawableResource) {
    Add(Res.drawable.ic_add),
    BarChart(Res.drawable.ic_bar_chart),
    CheckCircle(Res.drawable.ic_check_circle),
    Close(Res.drawable.ic_close),
    Delete(Res.drawable.ic_delete),
    Done(Res.drawable.ic_done),
    Favorite(Res.drawable.ic_favorite),
    FavoriteBorder(Res.drawable.ic_favorite_border),
    FormatBold(Res.drawable.ic_format_bold),
    FormatItalic(Res.drawable.ic_format_italic),
    FormatListBulleted(Res.drawable.ic_format_list_bulleted),
    FormatListNumbered(Res.drawable.ic_format_list_numbered),
    FormatStrikethrough(Res.drawable.ic_format_strikethrough),
    FormatUnderlined(Res.drawable.ic_format_underlined),
    Lightbulb(Res.drawable.ic_lightbulb),
    LightbulbOutline(Res.drawable.ic_lightbulb_outline),
    List(Res.drawable.ic_list),
    RadioButtonUnchecked(Res.drawable.ic_radio_button_unchecked),
    Search(Res.drawable.ic_search),
    Sort(Res.drawable.ic_sort),
    StackedBarChart(Res.drawable.ic_stacked_bar_chart),
    ;

    @Composable
    fun painter(): Painter = painterResource(res)
}
