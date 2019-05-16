package com.sun.adsfinder01.data.remote

import com.sun.adsfinder01.data.model.Place
import com.sun.adsfinder01.data.model.Seeker
import com.sun.adsfinder01.data.repository.PlaceDataSource
import com.sun.adsfinder01.data.repository.api.ApiService
import io.reactivex.Single

class PlaceRemoteDataSource(private val apiService: ApiService) : PlaceDataSource {

    override fun getPlaces(author: Int?, placeStatus: String?): Single<List<Place>> {
        return apiService.getPlaces(author, placeStatus).map { it.data }
    }

    override fun savePlace(author: Int?, placeId: Int?): Single<Boolean> {
        return apiService.savePlace(author, placeId).map { it.success }
    }

    override fun removePlace(author: Int?, placeId: Int?): Single<Boolean> {
        return apiService.removePlace(author, placeId).map { it.success }
    }

    override fun findPlaces(author: Int?, seeker: Seeker): Single<List<Place>> {
        return apiService.findPlaces(
            author,
            seeker.lat,
            seeker.lng,
            seeker.posterId,
            seeker.wallId,
            seeker.placeWidth,
            seeker.placeHeight,
            seeker.priceLimit,
            seeker.distance
        ).map { it.data }
    }
}
