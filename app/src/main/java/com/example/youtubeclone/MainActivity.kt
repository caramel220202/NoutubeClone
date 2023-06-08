package com.example.youtubeclone

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.youtubeclone.Adapter.VideoAdapter
import com.example.youtubeclone.Dto.VideosDto
import com.example.youtubeclone.databinding.ActivityMainBinding
import retrofit2.*
import retrofit2.converter.gson.GsonConverterFactory

class MainActivity : AppCompatActivity() {
    private val binding : ActivityMainBinding by lazy {
        ActivityMainBinding.inflate(layoutInflater)
    }
    private var pressTime = 0L
    private val adapter = VideoAdapter(callback = {url,title ->
        supportFragmentManager.fragments.find { it is PlayerFragment }?.let {
            (it as PlayerFragment).play(url, title)
        }
    })
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)


        binding.mainRecyclerview.adapter = adapter
        binding.mainRecyclerview.layoutManager = LinearLayoutManager(this)

        supportFragmentManager.beginTransaction()
            .replace(R.id.fragmentContainer,PlayerFragment())
            .commit()

        getVideoListApi()

    }

    private fun getVideoListApi(){
        val retrofit = Retrofit.Builder()
            .baseUrl("https://run.mocky.io/")
            .addConverterFactory(GsonConverterFactory.create())
            .build()

        retrofit.create(VideoService::class.java).also {
            it.getItemList().enqueue(object :Callback<VideosDto>{
                override fun onResponse(call: Call<VideosDto>, response: Response<VideosDto>) {
                if (response.isSuccessful){
                    response.body()?.let {dto ->
                        adapter.submitList(dto.videos)

                    }
                }
                }

                override fun onFailure(call: Call<VideosDto>, t: Throwable) {
                Log.d("testt",t.message.toString())
                }
            })
        }
    }
    override fun onBackPressed() {
        val currentTime = System.currentTimeMillis()
        val indivalTime = currentTime - pressTime

        if (2000 <= indivalTime){
            Toast.makeText(this,"한번더 누르면 앱이 종료됩니다.",Toast.LENGTH_SHORT).show()
            pressTime = currentTime
        }else{
            finishAffinity()
        }

    }
}