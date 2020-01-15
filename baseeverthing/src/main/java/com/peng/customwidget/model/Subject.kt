package com.peng.customwidget.model

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

/**
 * 考试题
 */
@Parcelize
data class Subject(
    var id: Int,
    var question: String,
    var solution: String, //选项,用分号分隔，不建议使用而且未使用
    var solutions: List<String>, //选项，正在使用
    var type: Int,  //1多选，2单选，3判断
    var isUse: Int,
    var createDate: String,
    var rightKey: String?, //正确选项,用分号分隔，不建议使用而且未使用
    var rightKeys: List<String>, //正确选项，正在使用
    var analysis: String,
    var isCollection: Int,  //是否已经收藏，1：已收藏，2：未收藏
    var beforeCheck: String?, //用户答案,用分号分隔，不建议使用而且未使用
    var beforeChecks: List<String>?,    //用户的答案
    var beforeCheckIndexes: List<Int>?,    //用户的答案
    var isRight: Int //是否答对，在交卷时上传每道题目的对错情况时使用。1：错误，2：收藏，3：正确
) : Parcelable {
}