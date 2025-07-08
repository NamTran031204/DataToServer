package com.example.weatherdata.data.remote

import com.example.weatherdata.domain.model.SensorReading
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import java.text.SimpleDateFormat
import java.util.*

class FirebaseDataService {
    private val database = Firebase.database
    private val dataRef = database.getReference("data-temp-humid")

    // Lấy dữ liệu mới nhất
    fun getLatestReading(onSuccess: (SensorReading?) -> Unit, onError: (String) -> Unit) {
        dataRef.orderByChild("timestamp").limitToLast(1).addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val lastChild = snapshot.children.lastOrNull()
                val reading = lastChild?.let {
                    it.getValue(SensorReading::class.java)?.copy(id = it.key)
                }
                onSuccess(reading)
            }

            override fun onCancelled(error: DatabaseError) {
                onError(error.message)
            }
        })
    }

    // Lấy dữ liệu mới nhất theo thời gian thực
    fun listenForLatestReading(onNewData: (SensorReading?) -> Unit, onError: (String) -> Unit): ValueEventListener {
        val listener = object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val lastChild = snapshot.children.lastOrNull()
                val reading = lastChild?.let {
                    it.getValue(SensorReading::class.java)?.copy(id = it.key)
                }
                onNewData(reading)
            }

            override fun onCancelled(error: DatabaseError) {
                onError(error.message)
            }
        }

        dataRef.orderByChild("timestamp").limitToLast(1).addValueEventListener(listener)
        return listener
    }

    // Lấy dữ liệu trong 1 giờ gần nhất
    fun getReadingsInLastHour(onSuccess: (List<SensorReading>) -> Unit, onError: (String) -> Unit) {
        try {
            // UTC để đảm bảo đồng nhất với timestamp từ ESP32
            val calendar = Calendar.getInstance(TimeZone.getTimeZone("UTC"))
            calendar.add(Calendar.HOUR, -1) // Trừ đi 1 giờ từ thời gian hiện tại

            val oneHourAgo = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'", Locale.getDefault()).apply {
                timeZone = TimeZone.getTimeZone("UTC")
            }.format(calendar.time)

            println("Fetching data from: $oneHourAgo")

            // Query Firebase: Lấy tất cả bản ghi có timestamp >= oneHourAgo
            // orderByChild: Sắp xếp theo trường "timestamp"
            // startAt: Chỉ lấy các bản ghi từ thời điểm oneHourAgo trở đi
            dataRef.orderByChild("timestamp")
                .startAt(oneHourAgo)
                .addListenerForSingleValueEvent(object : ValueEventListener {
                    override fun onDataChange(snapshot: DataSnapshot) {
                        val readings = mutableListOf<SensorReading>()

                        // Duyệt tất cả các bản ghi trả về từ query
                        snapshot.children.forEach { child ->
                            try {
                                // Chuyển đổi dữ liệu JSON thành đối tượng SensorReading
                                val reading = child.getValue(SensorReading::class.java)
                                if (reading != null) {
                                    readings.add(reading.copy(id = child.key))
                                }
                            } catch (e: Exception) {
                                println("Error parsing reading: ${e.message}")
                            }
                        }

                        println("Fetched ${readings.size} readings in the last hour")
                        onSuccess(readings) // Trả về danh sách các bản ghi
                    }

                    override fun onCancelled(error: DatabaseError) {
                        onError(error.message) // Trả về lỗi nếu truy vấn bị hủy
                    }
                })
        } catch (e: Exception) {
            onError("Failed to fetch hourly data: ${e.message}")
        }
    }

    fun removeListener(listener: ValueEventListener) {
        dataRef.removeEventListener(listener)
    }
}