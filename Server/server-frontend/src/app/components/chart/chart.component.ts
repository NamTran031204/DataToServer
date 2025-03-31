import { Component, OnInit, ViewChild, OnDestroy } from '@angular/core';
import { CommonModule } from '@angular/common';
import { Chart, ChartConfiguration, ChartEvent, ChartType } from 'chart.js';
import { BaseChartDirective } from 'ng2-charts';
import { default as Annotation } from 'chartjs-plugin-annotation';
import { Subscription } from 'rxjs';
import { SensorService } from '../../services/sensor.service';
import { SensorData } from '../../models/sensor-data.model';
import { NgChartsModule } from 'ng2-charts';

@Component({
  selector: 'app-chart',
  standalone: true,
  imports: [CommonModule, NgChartsModule],
  templateUrl: './chart.component.html',
  styleUrls: ['./chart.component.scss']
})
export class ChartComponent implements OnInit, OnDestroy {
  @ViewChild(BaseChartDirective) chart?: BaseChartDirective;
  
  private subscription: Subscription = new Subscription();
  
  public lineChartData: ChartConfiguration['data'] = {
    datasets: [
      {
        data: [],
        label: 'Nhiệt độ (°C)',
        backgroundColor: 'rgba(255,0,0,0.3)',
        borderColor: 'red',
        pointBackgroundColor: 'rgba(148,159,177,1)',
        pointBorderColor: '#fff',
        pointHoverBackgroundColor: '#fff',
        pointHoverBorderColor: 'rgba(148,159,177,0.8)',
        fill: 'origin',
        yAxisID: 'y-axis-0'
      },
      {
        data: [],
        label: 'Độ ẩm (%)',
        backgroundColor: 'rgba(0,0,255,0.3)',
        borderColor: 'blue',
        pointBackgroundColor: 'rgba(77,83,96,1)',
        pointBorderColor: '#fff',
        pointHoverBackgroundColor: '#fff',
        pointHoverBorderColor: 'rgba(77,83,96,1)',
        fill: 'origin',
        yAxisID: 'y-axis-1'
      }
    ],
    labels: []
  };

  public lineChartOptions: ChartConfiguration['options'] = {
    elements: {
      line: {
        tension: 0.5
      }
    },
    scales: {
      'y-axis-0': {
        position: 'left',
        min: 0,
        max: 50,
        title: {
          display: true,
          text: 'Nhiệt độ (°C)'
        }
      },
      'y-axis-1': {
        position: 'right',
        min: 0,
        max: 100,
        title: {
          display: true,
          text: 'Độ ẩm (%)'
        },
        grid: {
          drawOnChartArea: false
        }
      },
      x: {
        title: {
          display: true,
          text: 'Thời gian'
        }
      }
    },
    plugins: {
      legend: { display: true },
      tooltip: {
        mode: 'index',
        intersect: false,
      }
    },
    responsive: true,
    maintainAspectRatio: false
  };

  public lineChartType: ChartType = 'line';
  
  constructor(private sensorService: SensorService) {
    Chart.register(Annotation);
  }
  
  ngOnInit(): void {
    this.subscription = this.sensorService.sensorData$.subscribe(data => {
      this.updateChartData(data);
    });
  }
  
  ngOnDestroy(): void {
    this.subscription.unsubscribe();
  }
  
  private updateChartData(data: SensorData[]): void {
    if (!data || data.length === 0) return;
    
    // Sort by timestamp
    const sortedData = [...data].sort((a, b) => 
      new Date(a.timestamp).getTime() - new Date(b.timestamp).getTime()
    );
    
    // Extract data for chart
    const timestamps = sortedData.map(item => {
      const date = new Date(item.timestamp);
      return date.toLocaleTimeString() + '\n' + date.toLocaleDateString();
    });
    const temperatures = sortedData.map(item => item.temperature);
    const humidities = sortedData.map(item => item.humidity);
    
    // Update chart
    this.lineChartData.labels = timestamps;
    this.lineChartData.datasets[0].data = temperatures;
    this.lineChartData.datasets[1].data = humidities;
    
    if (this.chart) {
      this.chart.update();
    }
  }
}