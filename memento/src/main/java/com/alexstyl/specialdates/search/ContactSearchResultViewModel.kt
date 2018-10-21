package com.alexstyl.specialdates.search

import com.alexstyl.specialdates.contact.Contact
import com.alexstyl.specialdates.contact.ImageURL

data class ContactSearchResultViewModel(val contact: Contact,
                                        val displayName: String,
                                        val contactAvatarURI: ImageURL,
                                        val eventLabel: String,
                                        val eventColor: Int,
                                        val backgroundVariant: Int)
    : SearchResultViewModel
