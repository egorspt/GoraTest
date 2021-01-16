package com.app.goratest

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseExpandableListAdapter
import android.widget.TextView

class ListAdapter(
    private val context: Context,
    private val expListTitle: List<User>,
    private val expListDetail: List<Album>
) : BaseExpandableListAdapter() {

    override fun getChild(listPosition: Int, expListPosition: Int): Any {
        return expListDetail.filter { it.userId == listPosition + 1 }[expListPosition].title
    }

    override fun getChildId(listPosition: Int, expandedListPosition: Int): Long {
        return expandedListPosition.toLong()
    }

    override fun getChildView(
        listPosition: Int, expandedListPosition: Int,
        isLastChild: Boolean, convertView: View?, parent: ViewGroup?
    ): View? {
        val expListText = getChild(listPosition, expandedListPosition) as String
        val view = convertView ?: LayoutInflater.from(context).inflate(R.layout.list_child, null, false)
        val expListTextView = view.findViewById(R.id.listTitle) as TextView
        expListTextView.text = expListText
        return view
    }

    override fun getChildrenCount(listPosition: Int): Int {
        return expListDetail.filter { it.userId == listPosition + 1 }.size
    }

    override fun getGroup(listPosition: Int): Any {
        return expListTitle[listPosition].name
    }

    override fun getGroupCount(): Int {
        return expListTitle.size
    }

    override fun getGroupId(listPosition: Int): Long {
        return listPosition.toLong()
    }

    override fun getGroupView(
        listPosition: Int, isExpanded: Boolean,
        convertView: View?, parent: ViewGroup?
    ): View? {
        val listTitle = getGroup(listPosition) as String
        val view = convertView ?: LayoutInflater.from(context).inflate(R.layout.list_group, null, false)
        val listTitleTextView = view.findViewById(R.id.listTitle) as TextView
        listTitleTextView.text = listTitle
        return view
    }

    override fun hasStableIds(): Boolean {
        return false
    }

    override fun isChildSelectable(
        listPosition: Int,
        expandedListPosition: Int
    ): Boolean {
        return true
    }
}