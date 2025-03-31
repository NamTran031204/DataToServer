export interface SensorData {
    temperature: number;
    humidity: number;
    timestamp: string;
    deviceId: string;
  }
  
  export enum TimeUnit {
    MINUTE = 1,
    HOUR = 60,
    DAY = 1440 // 24 * 60
  }