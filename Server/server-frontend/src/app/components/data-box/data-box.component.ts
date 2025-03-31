import { Component, Input } from '@angular/core';
import { CommonModule } from '@angular/common';
import { SensorData } from '../../models/sensor-data.model';

@Component({
  selector: 'app-data-box',
  standalone: true,
  imports: [CommonModule],
  templateUrl: './data-box.component.html',
  styleUrls: ['./data-box.component.scss']
})
export class DataBoxComponent {
  @Input() data!: SensorData;
  
  formatTimestamp(timestamp: string): string {
    const date = new Date(timestamp);
    return date.toLocaleString();
  }
}