package com.example.youtubeclone

import com.example.youtubeclone.Dto.VideosDto
import retrofit2.Call
import retrofit2.http.GET

interface VideoService {

    @GET("v3/306343f7-c935-4f9c-b0eb-5d2d3007011f")
    fun getItemList():Call<VideosDto>
}