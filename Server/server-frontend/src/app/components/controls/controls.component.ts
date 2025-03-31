import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule, ReactiveFormsModule } from '@angular/forms';
import { MatFormFieldModule } from '@angular/material/form-field';
import { MatSelectModule } from '@angular/material/select';
import { MatSliderModule } from '@angular/material/slider';
import { MatButtonModule } from '@angular/material/button';
import { SensorService } from '../../services/sensor.service';
import { TimeUnit } from '../../models/sensor-data.model';

@Component({
  selector: 'app-controls',
  standalone: true,
  imports: [
    CommonModule,
    FormsModule,
    ReactiveFormsModule,
    MatFormFieldModule,
    MatSelectModule,
    MatSliderModule,
    MatButtonModule
  ],
  templateUrl: './controls.component.html',
  styleUrls: ['./controls.component.scss']
})
export class ControlsComponent implements OnInit {
  selectedTimeUnit: TimeUnit = TimeUnit.MINUTE;
  selectedValue = 10;
  
  timeUnits = [
    { value: TimeUnit.MINUTE, viewValue: 'Phút' },
    { value: TimeUnit.HOUR, viewValue: 'Giờ' },
    { value: TimeUnit.DAY, viewValue: 'Ngày' }
  ];
  
  maxValues = {
    [TimeUnit.MINUTE]: 60,
    [TimeUnit.HOUR]: 24,
    [TimeUnit.DAY]: 7
  };
  
  constructor(private sensorService: SensorService) {}
  
  ngOnInit(): void {
    this.sensorService.getTimeUnit().subscribe(unit => {
      this.selectedTimeUnit = unit;
    });
    
    this.sensorService.getSelectedValue().subscribe(value => {
      this.selectedValue = value;
    });
  }
  
  onTimeUnitChange(): void {
    this.sensorService.setTimeUnit(this.selectedTimeUnit);
    
    // Reset the slider value if it exceeds the max for the new unit
    if (this.selectedValue > this.maxValues[this.selectedTimeUnit]) {
      this.selectedValue = this.maxValues[this.selectedTimeUnit];
      this.sensorService.setSelectedValue(this.selectedValue);
    }
  }
  
  onSliderChange(event: any): void {
    this.selectedValue = event.target.value;
    this.sensorService.setSelectedValue(this.selectedValue);
  }
  
  refreshData(): void {
    this.sensorService.refreshData();
  }
  
  get maxValue(): number {
    return this.maxValues[this.selectedTimeUnit];
  }

  getTimeUnitLabel(): string {
    const unit = this.timeUnits.find(u => u.value === this.selectedTimeUnit);
    return unit ? unit.viewValue.toLowerCase() : '';
  }

  displayFn(value: number): string {
    return value.toString();
  }
}