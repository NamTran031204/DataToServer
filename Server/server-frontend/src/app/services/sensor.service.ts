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
        return []; //trả về mảng rỗng nếu có lỗi
      })
    );

    const humidityData$ = this.http.get<any[]>(`${this.baseUrl}/humidity/${numberOfSample}`).pipe(
      catchError(error => {
        console.error('Error fetching humidity data', error);
        return []; //trả về mảng rỗng nếu có lỗi
      })
    );

    return forkJoin([temperatureData$, humidityData$]).pipe(
      map(([tempData, humidData]) => {
        const tempMap = new Map();
        const humidMap = new Map();
        
        // dùng lưu các collectedTime từ từ temp và humid
        const allTimestamps = new Set<string>();
        
        for (let i = 0; i < tempData.length; i++) {
          const item = tempData[i];
          tempMap.set(item.collectedTime, item);
          allTimestamps.add(item.collectedTime);
        }
        for (let i = 0; i < humidData.length; i++) {
          const item = humidData[i];
          humidMap.set(item.collectedTime, item);
          allTimestamps.add(item.collectedTime);
        }

        // trả về mảng SensorData
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
      
        result.sort((a, b) => new Date(b.timestamp).getTime() - new Date(a.timestamp).getTime());
        
        this.sensorData.next(result);
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