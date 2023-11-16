package com.example.nimbletechnicaltest

data class Surveys(
    var id: String,
    var type: String,
    var title: String,
    var description: String,
    var survey_type: String,
    var cover_image_url: String,
) {

    override fun toString(): String {
        return "Surveys(id='$id',\n " +
                "type='$type',\n " +
                "title='$title',\n " +
                "description='$description',\n " +
                "survey_type='$survey_type',\n " +
                "cover_image_url='$cover_image_url')\n\n"
    }
}