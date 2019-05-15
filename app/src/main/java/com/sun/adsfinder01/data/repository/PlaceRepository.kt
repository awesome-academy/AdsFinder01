package com.sun.adsfinder01.data.repository

import com.sun.adsfinder01.data.model.Place
import com.sun.adsfinder01.data.remote.PlaceRemoteDataSource
import io.reactivex.Single

class PlaceRepository(private val placeRemoteDataSource: PlaceRemoteDataSource) :
    PlaceDataSource {

    override fun getPlaces(author: Int?, placeStatus: String?): Single<List<Place>> {
        return placeRemoteDataSource.getPlaces(author, placeStatus)
    }
}