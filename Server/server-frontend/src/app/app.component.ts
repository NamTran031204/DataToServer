import { Component } from '@angular/core';
import { CommonModule } from '@angular/common';
import { RouterOutlet } from '@angular/router';
import { MatToolbarModule } from '@angular/material/toolbar';
import { DataBoxComponent } from './components/data-box/data-box.component';
import { ControlsComponent } from './components/controls/controls.component';
import { ChartComponent } from './components/chart/chart.component';
import { SensorService } from './services/sensor.service';
import { SensorData } from './models/sensor-data.model';

@Component({
  selector: 'app-root',
  standalone: true,
  imports: [
    CommonModule,
    RouterOutlet,
    MatToolbarModule,
    DataBoxComponent,
    ControlsComponent,
    ChartComponent
  ],
  templateUrl: './app.component.html',
  styleUrls: ['./app.component.scss']
})
export class AppComponent {
  title = 'Theo dõi Nhiệt độ và Độ ẩm cho hệ thống nhà kính';
  sensorData: SensorData[] = [];
  
  constructor(private sensorService: SensorService) {
    this.sensorService.sensorData$.subscribe(data => {
      this.sensorData = data;
    });
  }
}