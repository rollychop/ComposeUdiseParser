package com.invictus.udise.model

data class SchoolDetailModel(
    val name: String,
    val udiseCode: String,
    val strength: Int,
    val state: String,
    val district: String,
    val block: String,
    val village: String,
    val pinCode: Int,
    val schoolType: String,
    val schCatDesc: String,
    val schMgmtcStatedesc: String,
    val schMgmtDesc: String,
    val schoolStatus: String,
    val teacherStrength: Int,
    val prePrimaryStrength: Int,
    val classOneStrength: Int,
    val classTwoStrength: Int,
    val classThreeStrength: Int,
    val classFourStrength: Int,
    val classFiveStrength: Int,
    val classSixStrength: Int,
    val classSevenStrength: Int,
    val classEightStrength: Int,
    val classNineStrength: Int,
    val classTenStrength: Int,
    val classElevenStrength: Int,
    val classTwelveStrength: Int,
    val buildingStatus: String,
    val boundaryWall: String,
    val noOfBoysToilets: Int,
    val noOfGirlsToilets: Int,
    val noCWSNToilets: Int,
    val drinkingWaterAvailability: String,
    val handWashFacility: String,
    val functionGenerator: Int,
    val library: String,
    val readingCorner: String,
    val bookBank: String,
    val functionalLaptop: Int,
    val functionDesktop: Int,
    val functionalTablet: Int,
    val functionalScanner: Int,
    val functionalPrinter: Int,
    val functionalWebCam: Int,
    val functionalDigiBoards: Int,
    val internetFacility: String,
    val totalClassRooms: Int,
    val totalOtherClassRooms: Int,


    ) {
    companion object {
        fun csvHeader() =
            "NAME,UDISE,STRENGTH,STATE,DISTRICT,BLOCK,VILLAGE,PINCODE,TYPE,CATEGORY,NATIONAL_MGT,STATE_MGT,STATUS," +
                    "TEACHER_STRENGTH," +
                    "PRE_PRIMARY,CLASS_ONE,CLASS_TWO,CLASS_THREE,CLASS_FOUR," +
                    "CLASS_FIVE,CLASS_SIX,CLASS_SEVEN,CLASS_EIGHT," +
                    "CLASS_NINE,CLASS_TEN,CLASS_ELEVEN,CLASS_TWELVE," +

                    "TOTAL_CLASS_ROOMS,TOTAL_OTHER_ROOMS," +

                    "BUILDING_STATUS,BOUNDARY_STATUS," +

                    "NO_OF_BOYS_TOILETS,NO_OF_GIRLS_TOILETS,NO_CWSN_TOILETS," +

                    "DRINKING_WATER_AVAILABILITY,HAND_WASH_FACILITY,FUNCTIONAL_GENERATOR," +
                    "LIBRARY,READING_CORNER,BOOK_BANK," +
                    "FUNCTIONAL_LAPTOP,FUNCTIONAL_DESKTOP,FUNCTIONAL_TABLET," +
                    "FUNCTIONAL_SCANNER,FUNCTIONAL_PRINTER,FUNCTIONAL_WEB_CAM," +
                    "FUNCTIONAL_DIGI_BOARD,INTERNET"
    }

    fun toCsvRow() = "${
        name.replace(",", " - ")
    },$udiseCode,$strength,$state,$district,$block,${
        village.replace(",", " - ")
    },$pinCode,$schoolType,$schCatDesc,$schMgmtDesc,$schMgmtcStatedesc,$schoolStatus," +
            "$teacherStrength," +
            "$prePrimaryStrength,$classOneStrength,$classTwoStrength," +
            "$classThreeStrength,$classFourStrength,$classFiveStrength," +
            "$classSixStrength,$classSevenStrength,$classEightStrength," +
            "$classNineStrength,$classTenStrength,$classElevenStrength," +
            "$classTwelveStrength," +

            "$totalClassRooms,$totalOtherClassRooms," +

            "$buildingStatus,$boundaryWall," +

            "$noOfBoysToilets,$noOfGirlsToilets,$noCWSNToilets," +

            "$drinkingWaterAvailability,$handWashFacility,$functionGenerator," +
            "$library,$readingCorner,$bookBank," +
            "$functionalLaptop,$functionDesktop,$functionalTablet," +
            "$functionalScanner,$functionalPrinter,$functionalWebCam," +
            "$functionalDigiBoards,$internetFacility"
}
