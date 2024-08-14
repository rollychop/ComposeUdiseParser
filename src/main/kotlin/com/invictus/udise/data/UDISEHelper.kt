package com.invictus.udise.data

import com.invictus.udise.model.SchoolDetailModel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import okhttp3.FormBody
import okhttp3.OkHttpClient
import okhttp3.Request
import org.jsoup.Jsoup
import java.io.File
import java.io.FileOutputStream
import java.io.IOException

object UDISEHelper {

    fun downloadFiles(
        srcHtmlFile: File,
        destinationDir: String
    ): Flow<Float> = flow {
        emit(-1f)
        val doc = Jsoup.parse(
            srcHtmlFile,
            "UTF-8",
            "https://src.udiseplus.gov.in/searchSchool"
        )
        val bodyList = doc.select("form[action=getSchoolDetail][method=post]")
            .map { element ->
                val b = FormBody.Builder()
                element.select("input").forEach {
                    b.add(it.attr("name"), it.attr("value"))
                }
                b.build()
            }

        val client = OkHttpClient()
        File(destinationDir).mkdir()
        bodyList.forEachIndexed { index, formBody ->
            // Create the request with the form body
            val request = Request.Builder()
                .url("https://src.udiseplus.gov.in/searchSchool/getSchoolDetail")
                .post(formBody)
                .build()

            try {
                val outFile = File("$destinationDir/${srcHtmlFile.name}-out-${index + 1}.html")

                if (outFile.exists()) throw IOException("File already exists!!")
                // Execute the request
                val response = client.newCall(request).execute()
                // Handle response as needed
                FileOutputStream(outFile).bufferedWriter().use {
                    it.write(
                        response.body.string().replace(Regex(";jsessionid=\\w+"), "")
                            .replace("card-body collapse", "card-body")
                    )
                }
                val percentage = index.plus(1f).div(bodyList.size)
                emit(percentage)
            } catch (_: IOException) {
            }
        }
        emit(-1f)
    }

    fun parse(htmlFile: File): SchoolDetailModel {
        val udiseSelector =
            "#content-wrap > div > div > div > div.card.mt-2.mb-5 > div.card-block.p-3 > div:nth-child(1) > div:nth-child(2) > div > div.col-7 > span"
        val stateSelector =
            "#content-wrap > div > div > div > div.card.mt-2.mb-5 > div.card-block.p-3 > div:nth-child(3) > div:nth-child(1) > div > div.col-7 > span"
        val blockSelector =
            "#content-wrap > div > div > div > div.card.mt-2.mb-5 > div.card-block.p-3 > div:nth-child(5) > div:nth-child(1) > div > div.col-7 > span"
        val villageSelector =
            "#content-wrap > div > div > div > div.card.mt-2.mb-5 > div.card-block.p-3 > div:nth-child(7) > div:nth-child(1) > div > div.col-7 > span"
        val schoolNameSelector =
            "#content-wrap > div > div > div > div.card.mt-2.mb-5 > div.card-block.p-3 > div:nth-child(1) > div:nth-child(1) > div > div.col-7 > span"
        val pinCodeSelector =
            "#content-wrap > div > div > div > div.card.mt-2.mb-5 > div.card-block.p-3 > div:nth-child(7) > div:nth-child(2) > div > div.col-7 > span"
        val districtSelector =
            "#content-wrap > div > div > div > div.card.mt-2.mb-5 > div.card-block.p-3 > div:nth-child(3) > div:nth-child(2) > div > div.col-7 > span"

        //strengths
        val teacherStrengthSelector = "#collapsethree > div > div > table.table.mt-3 > tbody > tr > td:nth-child(2)"
        val classRoomSelector = "#collapseTwo > div > div > div > div:nth-child(1) > div > div.col-7 > span"
        val otherRoomSelector = "#collapseTwo > div > div > div > div:nth-child(2) > div > div.col-7 > span"
        val prePrimaryStrengthSelector =
            "#collapsethree > div > div > table:nth-child(1) > tbody > tr:nth-child(2) > td:nth-child(2) > span"
        val classOneStrengthSelector =
            "#collapsethree > div > div > table:nth-child(1) > tbody > tr:nth-child(2) > td:nth-child(3) > span"
        val classTwoStrengthSelector =
            "#collapsethree > div > div > table:nth-child(1) > tbody > tr:nth-child(2) > td:nth-child(4) > span"
        val classThreeStrengthSelector =
            "#collapsethree > div > div > table:nth-child(1) > tbody > tr:nth-child(2) > td:nth-child(5) > span"

        val classFourStrengthSelector =
            "#collapsethree > div > div > table:nth-child(1) > tbody > tr:nth-child(2) > td:nth-child(6) > span"
        val classFiveStrengthSelector =
            "#collapsethree > div > div > table:nth-child(1) > tbody > tr:nth-child(2) > td:nth-child(7) > span"

        val classSixStrengthSelector =
            "#collapsethree > div > div > table:nth-child(1) > tbody > tr:nth-child(2) > td:nth-child(8) > span"
        val classSevenStrengthSelector =
            "#collapsethree > div > div > table:nth-child(1) > tbody > tr:nth-child(2) > td:nth-child(9) > span"
        val classEightStrengthSelector =
            "#collapsethree > div > div > table:nth-child(1) > tbody > tr:nth-child(2) > td:nth-child(10) > span"
        val classNineStrengthSelector =
            "#collapsethree > div > div > table:nth-child(1) > tbody > tr:nth-child(2) > td:nth-child(11) > span"
        val classTenStrengthSelector =
            "#collapsethree > div > div > table:nth-child(1) > tbody > tr:nth-child(2) > td:nth-child(12) > span"
        val classElevenStrengthSelector =
            "#collapsethree > div > div > table:nth-child(1) > tbody > tr:nth-child(2) > td:nth-child(13) > span"
        val classTwelveStrengthSelector =
            "#collapsethree > div > div > table:nth-child(1) > tbody > tr:nth-child(2) > td:nth-child(14) > span"

        //facilities
        val buildingStatusSelector =
            "#collapseOne > div > div > div:nth-child(1) > div:nth-child(1) > div > div.col-8 > span"
        val boundaryWallSelector =
            "#collapseOne > div > div > div:nth-child(1) > div:nth-child(2) > div > div.col-5 > span"

        val noOfBoysToiletSelector =
            "#collapseOne > div > div > div:nth-child(3) > div:nth-child(1) > div > div.col-5 > span"
        val noOfGirlsToiletSelector =
            "#collapseOne > div > div > div:nth-child(3) > div:nth-child(2) > div > div.col-5 > span"
        val noOfCWSNToiletsSelector =
            "#collapseOne > div > div > div:nth-child(3) > div:nth-child(3) > div > div.col-5 > span"

        val drinkingWaterSelector =
            "#collapseOne > div > div > div:nth-child(5) > div:nth-child(1) > div > div.col-5 > span"
        val handWashFacilitiesSelector =
            "#collapseOne > div > div > div:nth-child(5) > div:nth-child(2) > div > div.col-5 > span"
        val functionalGeneratorSelector =
            "#collapseOne > div > div > div:nth-child(5) > div:nth-child(3) > div > div.col-5 > span"
        val librarySelector =
            "#collapseOne > div > div > div:nth-child(7) > div:nth-child(1) > div > div.col-5 > span"
        val readingCornerSelector =
            "#collapseOne > div > div > div:nth-child(7) > div:nth-child(2) > div > div.col-5 > span"
        val bookBankSelector =
            "#collapseOne > div > div > div:nth-child(7) > div:nth-child(3) > div > div.col-5 > span"
        val functionalLaptopSelector =
            "#collapseOne > div > div > div:nth-child(9) > div:nth-child(1) > div > div.col-5 > span"
        val functionalDesktopSelector =
            "#collapseOne > div > div > div:nth-child(9) > div:nth-child(2) > div > div.col-5 > span"
        val functionalTabletSelector =
            "#collapseOne > div > div > div:nth-child(9) > div:nth-child(3) > div > div.col-5 > span"
        val functionalScannerSelector =
            "#collapseOne > div > div > div:nth-child(11) > div:nth-child(1) > div > div.col-5 > span"
        val functionalPrinterSelector =
            "#collapseOne > div > div > div:nth-child(11) > div:nth-child(3) > div > div.col-5 > span"
        val functionalWebCamSelector =
            "#collapseOne > div > div > div:nth-child(11) > div:nth-child(3) > div > div.col-5 > span"
        val functionDigiBoardSelector =
            "#collapseOne > div > div > div:nth-child(13) > div:nth-child(1) > div > div.col-5 > span"
        val internetFacilitySelector =
            "#collapseOne > div > div > div:nth-child(13) > div:nth-child(2) > div > div.col-5 > span"

        val document = Jsoup.parse(htmlFile)

        val table = document.select("table").firstOrNull()
        val d: List<String> = (if (table == null) null else listOf(
            document.select(schoolNameSelector).text(),//0
            document.select(udiseSelector).text(),//1
            table.select("td").last()!!.text(),//2
            document.select(stateSelector).text(),//3
            document.select(districtSelector).text(),//4
            document.select(blockSelector).text(),//5
            document.select(villageSelector).text(),//6
            document.select(pinCodeSelector).text(),//7

            document.select(teacherStrengthSelector).text(),//8

            document.select(prePrimaryStrengthSelector).text(),//9
            document.select(classOneStrengthSelector).text(),//10
            document.select(classTwoStrengthSelector).text(),//11
            document.select(classThreeStrengthSelector).text(),//12
            document.select(classFourStrengthSelector).text(),//13
            document.select(classFiveStrengthSelector).text(),//14
            document.select(classSixStrengthSelector).text(),//15
            document.select(classSevenStrengthSelector).text(),//16
            document.select(classEightStrengthSelector).text(),//17
            document.select(classNineStrengthSelector).text(),//18
            document.select(classTenStrengthSelector).text(),//19
            document.select(classElevenStrengthSelector).text(),//20
            document.select(classTwelveStrengthSelector).text(),//21

            document.select(buildingStatusSelector).text(),//22
            document.select(boundaryWallSelector).text(),//23

            document.select(noOfBoysToiletSelector).text(),//24
            document.select(noOfGirlsToiletSelector).text(),//25
            document.select(noOfCWSNToiletsSelector).text(),//26

            document.select(drinkingWaterSelector).text(),//27
            document.select(handWashFacilitiesSelector).text(),//28
            document.select(functionalGeneratorSelector).text(),//29

            document.select(librarySelector).text(),//30
            document.select(readingCornerSelector).text(),//31
            document.select(bookBankSelector).text(),//32

            document.select(functionalLaptopSelector).text(),//33
            document.select(functionalDesktopSelector).text(),//34
            document.select(functionalTabletSelector).text(),//35

            document.select(functionalScannerSelector).text(),//36
            document.select(functionalPrinterSelector).text(),//37
            document.select(functionalWebCamSelector).text(),//38

            document.select(functionDigiBoardSelector).text(),//39
            document.select(internetFacilitySelector).text(),//40,
            document.select(classRoomSelector).text(),//41,
            document.select(otherRoomSelector).text(),//42,

        )) ?: run {
            throw IllegalStateException("Error while parsing school detail $htmlFile")
        }

        return SchoolDetailModel(
            name = d[0],
            udiseCode = d[1],
            strength = d[2].toInt(),
            state = d[3],
            district = d[4],
            block = d[5],
            village = d[6],
            pinCode = d[7].toIntOrNull() ?: 0,
            schoolType = "",
            schCatDesc = "",
            schMgmtcStatedesc = "",
            schMgmtDesc = "",
            schoolStatus = "",
            teacherStrength = d[8].toIntOrNull() ?: 0,
            prePrimaryStrength = d[9].toIntOrNull() ?: 0,
            classOneStrength = d[10].toIntOrNull() ?: 0,
            classTwoStrength = d[11].toIntOrNull() ?: 0,
            classThreeStrength = d[12].toIntOrNull() ?: 0,
            classFourStrength = d[13].toIntOrNull() ?: 0,
            classFiveStrength = d[14].toIntOrNull() ?: 0,
            classSixStrength = d[15].toIntOrNull() ?: 0,
            classSevenStrength = d[16].toIntOrNull() ?: 0,
            classEightStrength = d[17].toIntOrNull() ?: 0,
            classNineStrength = d[18].toIntOrNull() ?: 0,
            classTenStrength = d[19].toIntOrNull() ?: 0,
            classElevenStrength = d[20].toIntOrNull() ?: 0,
            classTwelveStrength = d[21].toIntOrNull() ?: 0,

            buildingStatus = d[22],
            boundaryWall = d[23],

            noOfBoysToilets = d[24].toIntOrNull() ?: 0,
            noOfGirlsToilets = d[25].toIntOrNull() ?: 0,
            noCWSNToilets = d[26].toIntOrNull() ?: 0,

            drinkingWaterAvailability = d[27],
            handWashFacility = d[28],

            functionGenerator = d[29].toIntOrNull() ?: 0,

            library = d[30],
            readingCorner = d[31],
            bookBank = d[32],
            functionalLaptop = d[33].toIntOrNull() ?: 0,
            functionDesktop = d[34].toIntOrNull() ?: 0,
            functionalTablet = d[35].toIntOrNull() ?: 0,
            functionalScanner = d[36].toIntOrNull() ?: 0,
            functionalPrinter = d[37].toIntOrNull() ?: 0,
            functionalWebCam = d[38].toIntOrNull() ?: 0,
            functionalDigiBoards = d[39].toIntOrNull() ?: 0,
            internetFacility = d[40],
            totalClassRooms = d[41].toIntOrNull() ?: 0,
            totalOtherClassRooms = d[42].toIntOrNull() ?: 0,
        )
    }
}
