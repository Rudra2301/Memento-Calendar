package com.alexstyl.specialdates.search

import com.alexstyl.specialdates.contact.Contact
import com.alexstyl.specialdates.contact.ContactSource
import com.alexstyl.specialdates.contact.ContactsProvider
import com.alexstyl.specialdates.contact.ContactsProviderSource
import com.alexstyl.specialdates.contact.DisplayName
import com.alexstyl.specialdates.date.ContactEvent
import com.alexstyl.specialdates.date.Months.JANUARY
import com.alexstyl.specialdates.date.dateOn
import com.alexstyl.specialdates.events.peopleevents.StandardEventType
import org.fest.assertions.api.Assertions.assertThat
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.BDDMockito.given
import org.mockito.Mock
import org.mockito.Mockito
import org.mockito.runners.MockitoJUnitRunner

@RunWith(MockitoJUnitRunner::class)
class PeopleSearchTest {

    private lateinit var search: PeopleSearch
    @Mock
    val mockContactsSource = Mockito.mock(ContactsProviderSource::class.java)

    @Before
    fun setUp() {
        search = PeopleSearch(ContactsProvider(mapOf(Pair(ContactSource.SOURCE_DEVICE, mockContactsSource))), NameMatcher)
        given(mockContactsSource.allContacts).willReturn(
                listOf(
                        aContact.copy(displayName = DisplayName.from("Alex Styl")),
                        aContact.copy(displayName = DisplayName.from("Maria Papadopoulou")),
                        aContact.copy(displayName = DisplayName.from("Mimoza Dereks")))
        )

    }

    @Test
    fun searchingByFirstLetter() {
        val actual = search.searchForContacts("A").blockingFirst()

        assertThat(actual).containsOnly(aContact.copy(displayName = DisplayName.from("Alex Styl")))
    }

    @Test
    fun searchingByLastLetter() {
        val actual = search.searchForContacts("S").blockingFirst()

        assertThat(actual).containsOnly(aContact.copy(displayName = DisplayName.from("Alex Styl")))
    }

    @Test
    fun searchingByFullName() {
        val actual = search.searchForContacts("Alex Styl").blockingFirst()

        assertThat(actual).containsOnly(aContact.copy(displayName = DisplayName.from("Alex Styl")))
    }

    @Test
    fun multipleResults() {
        val actual = search.searchForContacts("M").blockingFirst()

        assertThat(actual).containsAll(listOf(aContact.copy(displayName = DisplayName.from("Maria Papadopoulou")), aContact.copy(displayName = DisplayName.from("Mimoza Dereks"))))
    }

    companion object {
        private val aContact = Contact(0, DisplayName.from("A contact"), "", 0)
    }
}
