package com.alexstyl.specialdates.addevent.ui

import android.content.Context
import android.support.transition.TransitionManager
import android.text.TextWatcher
import android.util.AttributeSet
import android.view.View
import android.widget.AdapterView
import android.widget.AutoCompleteTextView
import android.widget.LinearLayout

import com.alexstyl.specialdates.MementoApplication
import com.alexstyl.specialdates.R
import com.alexstyl.specialdates.contact.Contact
import com.alexstyl.specialdates.images.ImageLoader
import com.alexstyl.specialdates.people.PeopleViewModelFactory
import com.alexstyl.specialdates.search.PeopleSearch
import com.novoda.notils.caster.Views
import com.novoda.notils.logger.simple.Log
import com.novoda.notils.meta.AndroidUtils

import javax.inject.Inject

class ContactSuggestionView(context: Context, attrs: AttributeSet) : LinearLayout(context, attrs) {

    private var listener = OnContactSelectedListener.NO_CALLBACKS
    private var autoCompleteView: AutoCompleteTextView? = null


    @Inject lateinit var imageLoader: ImageLoader
    @Inject lateinit var peopleSearch: PeopleSearch
    @Inject lateinit var factory: PeopleViewModelFactory

    init {
        if (!isInEditMode) {
            val applicationModule = (context.applicationContext as MementoApplication).applicationModule
            applicationModule.inject(this)
        }
    }

    override fun onFinishInflate() {
        super.onFinishInflate()
        super.setOrientation(LinearLayout.HORIZONTAL)
        View.inflate(context, R.layout.merge_contact_suggestion_view, this)
        autoCompleteView = Views.findById(this, R.id.contact_suggestion_autocomplete)

        if (isInEditMode) {
            return
        }

        val clearContact = findViewById<View>(R.id.add_event_remove_contact)
        clearContact.setOnClickListener {
            autoCompleteView!!.setText("")
            clearContact.visibility = View.GONE
            listener.onContactCleared()
            autoCompleteView!!.isEnabled = true
            autoCompleteView!!.background.alpha = 255
            autoCompleteView!!.requestFocus()
        }

        val adapter = PeopleFilterableAdapter(imageLoader, factory, peopleSearch)
        autoCompleteView!!.setAdapter(adapter)
        autoCompleteView!!.onItemClickListener = AdapterView.OnItemClickListener { parent, view, position, id ->
            listener.onContactSelected(adapter.getItem(position).contact)
            AndroidUtils.requestHideKeyboard(view.context, view)

            TransitionManager.beginDelayedTransition(this@ContactSuggestionView)
            clearContact.visibility = View.VISIBLE
            autoCompleteView!!.isEnabled = false
            autoCompleteView!!.background.alpha = 0
        }
    }

    fun addTextChangedListener(textWatcher: TextWatcher) {
        autoCompleteView!!.addTextChangedListener(textWatcher)
    }

    fun setOnContactSelectedListener(listener: OnContactSelectedListener) {
        this.listener = listener
    }

    interface OnContactSelectedListener {
        fun onContactSelected(contact: Contact)

        fun onContactCleared()

        companion object {

            val NO_CALLBACKS: OnContactSelectedListener = object : OnContactSelectedListener {
                override fun onContactSelected(contact: Contact) {
                    Log.w("onContactSelected called with no callbacks")
                }

                override fun onContactCleared() {
                    Log.w("onContactCleared without a listener")
                }

            }
        }
    }
}
