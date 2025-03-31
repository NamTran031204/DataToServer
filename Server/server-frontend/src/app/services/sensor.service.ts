import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable, BehaviorSubject, interval, switchMap, catchError } from 'rxjs';
import { SensorData, TimeUnit } from '../models/sensor-data.model';
import { forkJoin, map } from 'rxjs';

@Injectable({
  providedIn: 'root'
})
export class SensorService {
  private baseUrl = 'http://localhost:8088/api/v1';
  private selectedTimeUnit = new BehaviorSubject<TimeUnit>(TimeUnit.MINUTE);
  private selectedValue = new BehaviorSubject<number>(10);
  
  private sensorData = new BehaviorSubject<SensorData[]>([]);
  public sensorData$ = this.sensorData.asObservable();
  
  constructor(private http: HttpClient) {
    // Refresh data every 1 minutes
    interval(60000).pipe(
      switchMap(() => this.fetchData())
    ).subscribe();
    
    // Initial data fetch
    this.fetchData().subscribe();
  }
  
  setTimeUnit(unit: TimeUnit): void {
    this.selectedTimeUnit.next(unit);
    this.fetchData().subscribe();
  }
  
  setSelectedValue(value: number): void {
    this.selectedValue.next(value);
    this.fetchData().subscribe();
  }
  
  getTimeUnit(): Observable<TimeUnit> {
    return this.selectedTimeUnit.asObservable();
  }
  
  getSelectedValue(): Observable<number> {
    return this.selectedValue.asObservable();
  }
  
  private fetchData(): Observable<SensorData[]> {
    const numberOfSample = this.calculateNumberOfSamples();
    
    const temperatureData$ = this.http.get<any[]>(`${this.baseUrl}/temperature/${numberOfSample}`).pipe(
      catchError(error => {
        console.error('Error fetching temperature data', error);
        return []; // Trả về mảng rỗng nếu có lỗi
      })
    );

    const humidityData$ = this.http.get<any[]>(`${this.baseUrl}/humidity/${numberOfSample}`).pipe(
      catchError(error => {
        console.error('Error fetching humidity data', error);
        return []; // Trả về mảng rỗng nếu có lỗi
      })
    );

    debugger;

    return forkJoin([temperatureData$, humidityData$]).pipe(
      map(([tempData, humidData]) => {
        // Tạo maps cho dữ liệu nhiệt độ và độ ẩm với key là collectedTime
        const tempMap = new Map();
        const humidMap = new Map();
        
        // Lưu tất cả các mốc thời gian (collectedTime) từ cả hai nguồn dữ liệu
        const allTimestamps = new Set<string>();
        
        // Lưu dữ liệu nhiệt độ vào map và thu thập tất cả mốc thời gian
        tempData.forEach(item => {
          tempMap.set(item.collectedTime, item);
          allTimestamps.add(item.collectedTime);
        });
        
        // Lưu dữ liệu độ ẩm vào map và thu thập tất cả mốc thời gian
        humidData.forEach(item => {
          humidMap.set(item.collectedTime, item);
          allTimestamps.add(item.collectedTime);
        });

        // Tạo kết quả từ tất cả các mốc thời gian
        const result: SensorData[] = Array.from(allTimestamps).map(timestamp => {
          const tempItem = tempMap.get(timestamp);
          const humidItem = humidMap.get(timestamp);
        
          return {
            temperature: tempItem ? tempItem.temp : null,
            humidity: humidItem ? humidItem.humid : null,
            timestamp: timestamp,
            deviceId: (tempItem?.id || humidItem?.id || '').toString()
          } as SensorData;
        });
      
        // Sắp xếp kết quả theo thời gian (mới nhất trước)
        result.sort((a, b) => new Date(b.timestamp).getTime() - new Date(a.timestamp).getTime());
        
        // Cập nhật BehaviorSubject
        this.sensorData.next(result);
        
        // Console log để debug
        console.log('Combined data by timestamp:', result);
        
        return result;
      })
    );
  }
  
  public refreshData(): void {
    this.fetchData().subscribe();
  }
  
  private calculateNumberOfSamples(): number {
    return this.selectedValue.value * this.selectedTimeUnit.value;
  }
}