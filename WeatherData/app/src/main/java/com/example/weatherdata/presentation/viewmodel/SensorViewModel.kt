package com.example.weatherdata.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.weatherdata.domain.model.SensorDisplayData
import com.example.weatherdata.domain.model.SensorReading
import com.example.weatherdata.domain.repository.SensorRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class SensorViewModel(private val repository: SensorRepository) : ViewModel() {
    // StateFlow để theo dõi và cập nhật UI khi dữ liệu thay đổi
    private val _sensorData = MutableStateFlow<SensorDisplayData?>(null)
    val sensorData: StateFlow<SensorDisplayData?> = _sensorData.asStateFlow()

    private val _errorMessage = MutableStateFlow<String?>(null)
    val errorMessage: StateFlow<String?> = _errorMessage.asStateFlow()

    private var latestReadingListener: Any? = null

    init {
        fetchData() // Lấy dữ liệu khi ViewModel được khởi tạo
    }

    /**
     * Lấy dữ liệu từ repository và thiết lập listener cho dữ liệu mới nhất
     */
    fun fetchData() {
        viewModelScope.launch {
            // Lắng nghe thay đổi dữ liệu mới nhất theo thời gian thực
            latestReadingListener = repository.listenForLatestReading(
                onNewData = { latestReading ->
                    latestReading?.let {
                        // Khi có dữ liệu mới, tính lại trung bình 1 giờ
                        fetchHourlyAverage(it)
                    }
                },
                onError = { error ->
                    _errorMessage.value = "Error fetching latest data: $error"
                }
            )
        }
    }

    /**
     * Lấy dữ liệu 1 giờ gần nhất để tính trung bình
     */
    private fun fetchHourlyAverage(latestReading: SensorReading) {
        repository.getReadingsInLastHour(
            onSuccess = { readings ->
                // Khi lấy được dữ liệu 1 giờ, tính trung bình
                calculateAverages(readings, latestReading)
            },
            onError = { error ->
                _errorMessage.value = "Error fetching hourly data: $error"

                // Nếu không lấy được dữ liệu 1 giờ, vẫn hiển thị dữ liệu mới nhất
                updateWithLatestOnly(latestReading)
            }
        )
    }

    /**
     * Cập nhật UI chỉ với dữ liệu mới nhất (khi không có dữ liệu 1 giờ)
     */
    private fun updateWithLatestOnly(latestReading: SensorReading) {
        latestReading.temperature?.let { temp ->
            latestReading.humidity?.let { humid ->
                _sensorData.value = SensorDisplayData(
                    currentTemperature = temp,
                    currentHumidity = humid,
                    averageTemperature = temp, // Sử dụng giá trị hiện tại nếu không tính được trung bình
                    averageHumidity = humid    // Sử dụng giá trị hiện tại nếu không tính được trung bình
                )
            }
        }
    }

    /**
     * Tính trung bình dữ liệu nhiệt độ và độ ẩm từ danh sách bản ghi trong 1 giờ
     *
     * Thuật toán:
     * 1. Nếu không có dữ liệu, sử dụng dữ liệu mới nhất
     * 2. Đối với mỗi bản ghi, cộng dồn giá trị nhiệt độ và độ ẩm
     * 3. Tính trung bình bằng cách chia tổng cho số lượng bản ghi
     * 4. Lọc bỏ các giá trị null hoặc không hợp lệ
     */
    private fun calculateAverages(readings: List<SensorReading>, latestReading: SensorReading) {
        if (readings.isEmpty()) {
            // Không có dữ liệu trong 1 giờ -> sử dụng dữ liệu hiện tại
            updateWithLatestOnly(latestReading)
            return
        }

        // Lọc và phân tích dữ liệu nhiệt độ
        val validTemperatures = readings
            .mapNotNull { it.temperature }
            .filter { !it.isNaN() && it > -100 && it < 100 } // Lọc bỏ giá trị không hợp lý

        // Lọc và phân tích dữ liệu độ ẩm
        val validHumidities = readings
            .mapNotNull { it.humidity }
            .filter { !it.isNaN() && it >= 0 && it <= 100 } // Độ ẩm hợp lệ từ 0-100%

        // Tính trung bình nếu có dữ liệu hợp lệ
        val avgTemp = if (validTemperatures.isNotEmpty())
            validTemperatures.sum() / validTemperatures.size else 0f

        val avgHumid = if (validHumidities.isNotEmpty())
            validHumidities.sum() / validHumidities.size else 0f

        // Debug info
        println("Calculated averages from ${validTemperatures.size} temp and ${validHumidities.size} humid readings")
        println("Average temp: $avgTemp, Average humid: $avgHumid")

        // Cập nhật UI với dữ liệu mới nhất và trung bình
        latestReading.temperature?.let { temp ->
            latestReading.humidity?.let { humid ->
                _sensorData.value = SensorDisplayData(
                    currentTemperature = temp,          // Nhiệt độ hiện tại
                    currentHumidity = humid,            // Độ ẩm hiện tại
                    averageTemperature = avgTemp,       // Nhiệt độ trung bình 1 giờ
                    averageHumidity = avgHumid          // Độ ẩm trung bình 1 giờ
                )
            }
        }
    }

    /**
     * Dọn dẹp resource khi ViewModel bị hủy
     */
    override fun onCleared() {
        super.onCleared()
        latestReadingListener?.let {
            repository.removeListener(it)
        }
    }
}