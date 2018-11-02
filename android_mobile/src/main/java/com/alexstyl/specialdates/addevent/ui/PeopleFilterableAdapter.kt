package com.alexstyl.specialdates.addevent.ui

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.Filter
import android.widget.Filterable
import android.widget.TextView

import com.alexstyl.specialdates.R
import com.alexstyl.specialdates.images.ImageLoader
import com.alexstyl.specialdates.people.PeopleViewModelFactory
import com.alexstyl.specialdates.people.PersonViewModel
import com.alexstyl.specialdates.search.PeopleSearch
import com.alexstyl.specialdates.ui.widget.ColorImageView

class PeopleFilterableAdapter(
        private val imageLoader: ImageLoader,
        private val viewModelFactory: PeopleViewModelFactory,
        private val peopleProvider: PeopleSearch) : BaseAdapter(), Filterable {

    private val contacts = mutableListOf<PersonViewModel>()


    override fun getView(position: Int, oldView: View?, parent: ViewGroup): View {
        val vh = if (oldView == null) {
            val view = LayoutInflater.from(parent.context).inflate(R.layout.row_contact, parent, false)
            val contactName = view.findViewById<TextView>(R.id.display_name)
            val avatar = view.findViewById<ColorImageView>(R.id.search_result_avatar)
            val viewHolder = ContactViewHolder(view, contactName, avatar, imageLoader)
            view.tag = viewHolder
            viewHolder
        } else {
            oldView.tag as ContactViewHolder
        }

        val viewModel = getItem(position)
        vh.bind(viewModel)

        return vh.view
    }

    private class ContactViewHolder(val view: View, val contactName: TextView, val avatar: ColorImageView, private val imageLoader: ImageLoader) {
        fun bind(viewModel: PersonViewModel) {
            contactName.text = viewModel.personName
            avatar.setCircleColorVariant(viewModel.personId.toInt())
            avatar.setLetter(viewModel.personName)

            imageLoader
                    .load(viewModel.avatarURI)
                    .asCircle()
                    .into(avatar.imageView)
        }
    }

    override fun getCount(): Int {
        return contacts.size
    }

    override fun getItem(position: Int): PersonViewModel {
        return contacts[position]
    }

    override fun getItemId(position: Int): Long {
        return contacts[position].personId
    }

    override fun getFilter(): Filter {
        return _filter
    }

    private val _filter = object : Filter() {

        override fun performFiltering(constraint: CharSequence?): FilterResults {
            val results = peopleProvider
                    .searchForContacts(constraint.toString())
                    .map { it -> it.take(2).map { viewModelFactory.personViewModel(it) } }
                    .blockingFirst()

            return FilterResults().apply {
                values = results
                count = results.size
            }
        }

        override fun publishResults(constraint: CharSequence?, results: FilterResults) {
            displaySuggestions(results.values as List<PersonViewModel>)
        }
    }


    private fun displaySuggestions(contacts: List<PersonViewModel>) {
        this.contacts.clear()
        this.contacts.addAll(contacts)

        notifyDataSetChanged()
    }
}
