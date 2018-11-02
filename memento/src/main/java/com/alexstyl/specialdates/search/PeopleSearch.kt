package com.alexstyl.specialdates.search

import com.alexstyl.specialdates.contact.Contact
import com.alexstyl.specialdates.contact.ContactsProvider
import com.alexstyl.specialdates.events.peopleevents.PeopleEventsProvider
import io.reactivex.Observable

class PeopleSearch(private val contactsProvider: ContactsProvider,
                   private val nameComparator: NameMatcher) {

    fun searchForContacts(searchQuery: String): Observable<Set<Contact>> {
        return Observable.fromCallable {
            contactsProvider
                    .allContacts
                    .filter {
                        nameComparator.match(it.displayName, searchQuery)
                    }
                    .sortedBy { it.givenName }
                    .toSet()
        }
    }
}
