package com.example.test2.admin

import com.google.firebase.database.IgnoreExtraProperties

@IgnoreExtraProperties
data class LocationLogging (
    var latitude : Double ? = 0.0,
    var longitude : Double ? = 0.0
    )