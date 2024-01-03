package com.nkonda.greenthumb.ui.search

import org.junit.Assert.*
import org.junit.Test

class SearchFragmentTest {
    @Test
    fun searchPlantByName_returnsEmptyResults() {
        // Given a valid plant name which has no results
        // When search is called


        // Then assert that no data UI is displayed
    }



    @Test
    fun searchPlantByName_returnsNotFound_whenInvalidInput() {
    }

    @Test
    fun givenEmptyPlantName_whenSearchSoftKeyClicked_thenSearchDoesntStart() {

    }
    @Test
    fun searchPlantByName_failsFastWhenNoNetwork() {

    }

    @Test
    fun givenNetwork_whenConnectivityDrops_thenNoNetworkImageAndMessageAreDisplayed() {

    }

    @Test
    fun givenErrorState_whenSearchIsSuccess_thenErrorStateIsReplaced() {

    }

    @Test
    fun givenNoNetwork_whenConnectivityIsBack_thenNotRunStateIsDisplayed() {

    }

    @Test
    fun givenValidPlant_whenNetworkTimeouts_thenAppropriateErrorIsDisplayed() {

    }

    @Test
    fun givenValidPlant_whenRequestFails_thenAppropriateErrorIsDisplayed() {

    }

    @Test
    fun givenListOfResults_whenListItemSelected_thenNavigatesToDetailsScreen() {

    }
}