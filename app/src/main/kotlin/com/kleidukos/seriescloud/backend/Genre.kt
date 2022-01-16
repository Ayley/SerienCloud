package com.kleidukos.seriescloud.backend

enum class Genre(val genreName: String, val url: String) {

    ADVENTURE("Abenteuer", "genre/abenteuer"),
    ACTION("Action", "genre/action"),
    ANIMATION("Animation", "genre/animation"),
    ANIME("Anime", "genre/anime"),
    COMEDY("Comedy", "genre/comedy"),
    DOCUMENTATION("Dokumentation", "genre/dokumentation"),
    DOCUSOAP("Dokusoap", "genre/dokusoap"),
    DRAMA("Drama", "genre/drama"),
    DRAMEDY("Dramedy", "genre/dramedy"),
    FAMILY("Familie", "genre/familie"),
    FANTASY("Fantasy", "genre/fantasy"),
    HISTORY("History", "genre/history"),
    YOUNG("Jugend", "genre/jugend"),
    HORROR("Horror", "genre/horror"),
    KIDS_SERIES("Kinderserie", "genre/kinderserie"),
    HOSPITAL("Krankenhaus", "genre/krankenhaus"),
    THRILLER("Krimi", "genre/krimi"),
    MYSTERY("Mystery", "genre/mystery"),
    ROMANCE("Romantik", "genre/romanze"),
    SCIFI("Science-Fiction", "genre/science-fiction"),
    SITCOM("Sitcom", "genre/sitcom"),
    TELENOVELA("Telenovela", "genre/telenovela"),
    THRILLER2("Thriller", "genre/thriller"),
    WILD_WEST("Western", "genre/western"),
    CARTOON("Zeichentrick", "genre/zeichentrick"),
    K_DRAMA("K-Drama", "genre/k-drama"),
    REALITY_TV("Reality-Tv", "genre/reality-tv"),
    NETFLIX_ORIGINALS("Netflix-Originals", "genre/netflix-originals"),
    AMAZON_ORIGINALS("Amazon-Originals", "genre/amazon-originals"),

    NONE("NONE","NONE");

    companion object {
        fun byName(string: String): Genre {
            for (genre in values()) {
                if (genre.genreName.equals(string, true)) {
                    return genre
                }
            }
            return null!!
        }
    }
}