/*
 * QNotified - An Xposed module for QQ/TIM
 * Copyright (C) 2019-2021 dmca@ioctl.cc
 * https://github.com/ferredoxin/QNotified
 *
 * This software is non-free but opensource software: you can redistribute it
 * and/or modify it under the terms of the GNU Affero General Public License
 * as published by the Free Software Foundation; either
 * version 3 of the License, or any later version and our eula as published
 * by ferredoxin.
 *
 * This software is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * and eula along with this software.  If not, see
 * <https://www.gnu.org/licenses/>
 * <https://github.com/ferredoxin/QNotified/blob/master/LICENSE.md>.
 */

package me.ketal.dispacher

import android.view.ViewGroup
import de.robv.android.xposed.XC_MethodHook
import ltd.nextalone.hook.HideTroopLevel
import ltd.nextalone.util.hookAfter
import ltd.nextalone.util.method
import ltd.nextalone.util.tryOrFalse
import me.singleneuron.qn_kernel.data.MsgRecordData
import nil.nadph.qnotified.base.annotation.FunctionEntry
import nil.nadph.qnotified.hook.CommonDelayableHook
import nil.nadph.qnotified.util.Utils

@FunctionEntry
object BaseBubbleBuilderHook : CommonDelayableHook("__NOT_USED__") {
    //Register your decorator here
    private val decorators = arrayOf<OnBubbleBuilder>(
        HideTroopLevel,
    )
    override fun initOnce() = tryOrFalse {
        "Lcom/tencent/mobileqq/activity/aio/BaseBubbleBuilder;->a(IILcom/tencent/mobileqq/data/ChatMessage;Landroid/view/View;Landroid/view/ViewGroup;Lcom/tencent/mobileqq/activity/aio/OnLongClickAndTouchListener;)Landroid/view/View;"
            .method.hookAfter(this) {
                if (it.result == null) return@hookAfter
                val rootView = it.result as ViewGroup
                val msg = MsgRecordData(it.args[2])
                for (decorator in decorators) {
                    try {
                        decorator.onGetView(rootView, msg, it)
                    } catch (e:Exception) {
                        Utils.log(e)
                    }
                }
            }
    }

    override fun isEnabled() = true

    override fun setEnabled(enabled: Boolean) = Unit
}

interface OnBubbleBuilder {
    fun onGetView(rootView: ViewGroup, chatMessage:MsgRecordData, param: XC_MethodHook.MethodHookParam)
}
