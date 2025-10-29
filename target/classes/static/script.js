const API_BASE = 'http://localhost:8080/api/weather';

// State management
let appState = {
    updateCount: 0,
    observerCount: 0,
    currentStrategy: 'None',
    stationName: 'Weather Station'
};

// Chart data
let temperatureChart = null;
let temperatureData = {
    labels: [],
    temperatures: [],
    cities: [],
    timestamps: []
};

// ==================== NOTIFICATION SYSTEM ====================

function showNotification(message, type = 'info', duration = 4000) {
    const container = document.getElementById('notificationContainer');
    const notification = document.createElement('div');
    notification.className = `notification ${type}`;

    const icons = {
        success: '✅',
        error: '❌',
        info: 'ℹ️'
    };

    notification.innerHTML = `
        <span class="notification-icon">${icons[type]}</span>
        <span class="notification-message">${message}</span>
    `;

    container.appendChild(notification);

    // Auto remove after duration
    setTimeout(() => {
        notification.style.animation = 'slideIn 0.3s ease reverse';
        setTimeout(() => notification.remove(), 300);
    }, duration);
}

// ==================== STATE MANAGEMENT ====================

function updateAppState(newState) {
    appState = { ...appState, ...newState };
    updateUI();
}

function updateUI() {
    // Update header
    document.getElementById('stationName').textContent = appState.stationName;
    document.getElementById('currentStrategy').textContent = appState.currentStrategy;
    document.getElementById('currentStrategyDisplay').textContent = appState.currentStrategy;

    // Update observer stats
    document.getElementById('observerCount').textContent = appState.observerCount;
    document.getElementById('updateCount').textContent = appState.updateCount;
}

// ==================== TEMPERATURE CHART ====================

// Initialize chart
function initializeTemperatureChart() {
    const ctx = document.getElementById('temperatureChart').getContext('2d');

    temperatureChart = new Chart(ctx, {
        type: 'line',
        data: {
            labels: temperatureData.labels,
            datasets: [{
                label: 'Temperature (°C)',
                data: temperatureData.temperatures,
                borderColor: '#e74c3c',
                backgroundColor: 'rgba(231, 76, 60, 0.1)',
                borderWidth: 3,
                fill: true,
                tension: 0.4,
                pointBackgroundColor: '#e74c3c',
                pointBorderColor: '#ffffff',
                pointBorderWidth: 2,
                pointRadius: 5,
                pointHoverRadius: 7
            }]
        },
        options: {
            responsive: true,
            maintainAspectRatio: false,
            plugins: {
                title: {
                    display: true,
                    text: 'Real-time Temperature Monitoring',
                    font: { size: 16 }
                },
                tooltip: {
                    mode: 'index',
                    intersect: false,
                    callbacks: {
                        label: function(context) {
                            const index = context.dataIndex;
                            const city = temperatureData.cities[index];
                            const time = temperatureData.timestamps[index];
                            return [
                                `City: ${city}`,
                                `Temperature: ${context.parsed.y}°C`,
                                `Time: ${time}`
                            ];
                        }
                    }
                },
                legend: {
                    display: true,
                    position: 'top'
                }
            },
            scales: {
                x: {
                    title: {
                        display: true,
                        text: 'Time'
                    },
                    grid: {
                        color: 'rgba(0,0,0,0.1)'
                    }
                },
                y: {
                    title: {
                        display: true,
                        text: 'Temperature (°C)'
                    },
                    min: -10,
                    max: 40,
                    grid: {
                        color: 'rgba(0,0,0,0.1)'
                    }
                }
            },
            interaction: {
                intersect: false,
                mode: 'nearest'
            },
            animation: {
                duration: 1000,
                easing: 'easeOutQuart'
            }
        }
    });

    // Load saved data from localStorage
    loadChartData();
}

// Add new temperature data to chart
function addTemperatureData(city, temperature, timestamp) {
    const selectedCity = document.getElementById('chartCity').value;

    // Filter by selected city
    if (selectedCity !== 'All Cities' && city !== selectedCity) {
        return;
    }

    const timeLabel = new Date(timestamp).toLocaleTimeString();

    // Add new data
    temperatureData.labels.push(timeLabel);
    temperatureData.temperatures.push(temperature);
    temperatureData.cities.push(city);
    temperatureData.timestamps.push(timestamp);

    // Keep only last 20 data points for performance
    const maxDataPoints = 20;
    if (temperatureData.labels.length > maxDataPoints) {
        temperatureData.labels.shift();
        temperatureData.temperatures.shift();
        temperatureData.cities.shift();
        temperatureData.timestamps.shift();
    }

    // Update chart
    if (temperatureChart) {
        temperatureChart.data.labels = temperatureData.labels;
        temperatureChart.data.datasets[0].data = temperatureData.temperatures;
        temperatureChart.update('none');
    }

    // Update statistics
    updateChartStats();

    // Save to localStorage
    saveChartData();
}

// Update chart statistics
function updateChartStats() {
    if (temperatureData.temperatures.length === 0) return;

    const temps = temperatureData.temperatures;
    const current = temps[temps.length - 1];
    const avg = temps.reduce((a, b) => a + b, 0) / temps.length;
    const max = Math.max(...temps);
    const min = Math.min(...temps);

    document.getElementById('currentTemp').textContent = `${current.toFixed(1)}°C`;
    document.getElementById('avgTemp').textContent = `${avg.toFixed(1)}°C`;
    document.getElementById('maxTemp').textContent = `${max.toFixed(1)}°C`;
    document.getElementById('minTemp').textContent = `${min.toFixed(1)}°C`;

    // Color code based on temperature
    const tempElements = {
        current: document.getElementById('currentTemp'),
        avg: document.getElementById('avgTemp'),
        max: document.getElementById('maxTemp'),
        min: document.getElementById('minTemp')
    };

    Object.values(tempElements).forEach(el => {
        const temp = parseFloat(el.textContent);
        el.className = 'stat-number ' + getTempColorClass(temp);
    });
}

// Get color class based on temperature
function getTempColorClass(temp) {
    if (temp >= 30) return 'temp-high';
    if (temp >= 15) return 'temp-medium';
    return 'temp-low';
}

// Clear chart data
function clearChart() {
    temperatureData = {
        labels: [],
        temperatures: [],
        cities: [],
        timestamps: []
    };

    if (temperatureChart) {
        temperatureChart.data.labels = [];
        temperatureChart.data.datasets[0].data = [];
        temperatureChart.update();
    }

    updateChartStats();
    localStorage.removeItem('weatherChartData');
    showNotification('Chart cleared', 'info');
}

// Save chart data to localStorage
function saveChartData() {
    try {
        localStorage.setItem('weatherChartData', JSON.stringify(temperatureData));
    } catch (e) {
        console.warn('Could not save chart data to localStorage');
    }
}

// Load chart data from localStorage
function loadChartData() {
    try {
        const saved = localStorage.getItem('weatherChartData');
        if (saved) {
            const data = JSON.parse(saved);
            temperatureData = data;

            if (temperatureChart && data.temperatures.length > 0) {
                temperatureChart.data.labels = data.labels;
                temperatureChart.data.datasets[0].data = data.temperatures;
                temperatureChart.update();
                updateChartStats();
            }
        }
    } catch (e) {
        console.warn('Could not load chart data from localStorage');
    }
}

// ==================== STRATEGY PATTERN ====================

async function setStrategy() {
    const strategyType = document.getElementById('strategySelect').value;
    const city = document.getElementById('cityInput').value.trim() || 'Astana';

    if (!city) {
        showNotification('Please enter a city name', 'error');
        return;
    }

    try {
        const button = event.target;
        button.classList.add('loading');
        button.disabled = true;

        const response = await fetch(`${API_BASE}/strategy`, {
            method: 'POST',
            headers: { 'Content-Type': 'application/json' },
            body: JSON.stringify({ type: strategyType, city: city })
        });

        const result = await response.json();

        updateAppState({
            currentStrategy: result.strategyClass.replace('Strategy', '')
        });

        showNotification(`Strategy set to: ${result.strategyClass}`, 'success');

    } catch (error) {
        showNotification('Failed to set strategy: ' + error.message, 'error');
    } finally {
        const button = event.target;
        button.classList.remove('loading');
        button.disabled = false;
    }
}

async function updateWeather() {
    try {
        const button = event.target;
        button.classList.add('loading');

        const response = await fetch(`${API_BASE}/update`, {
            method: 'POST',
            headers: { 'Content-Type': 'application/json' }
        });

        const result = await response.json();

        if (result.status === 'success') {
            displayWeatherData(result.data);
            updateAppState({
                updateCount: appState.updateCount + 1,
                currentStrategy: result.strategy
            });
            showNotification(`Weather updated using ${result.strategy} strategy`, 'success');
        }

    } catch (error) {
        showNotification('Failed to update weather: ' + error.message, 'error');
    } finally {
        const button = event.target;
        button.classList.remove('loading');
    }
}

// ==================== FACTORY PATTERN ====================

async function createViaFactory() {
    const type = document.getElementById('factoryType').value;
    const city = document.getElementById('factoryCity').value.trim() || 'Almaty';

    try {
        const response = await fetch(`${API_BASE}/factory/create`, {
            method: 'POST',
            headers: {'Content-Type': 'application/json'},
            body: JSON.stringify({ type, city })
        });

        const result = await response.json();

        document.getElementById('factoryOutput').innerHTML = `
            <div class="pattern-result">
                <div class="info-item">
                    <span class="info-label">Factory:</span>
                    <span class="info-value">${result.factory}</span>
                </div>
                <div class="info-item">
                    <span class="info-label">Created:</span>
                    <span class="info-value">${result.createdStrategy}</span>
                </div>
                <div class="info-item">
                    <span class="info-label">Type:</span>
                    <span class="info-value">${result.type}</span>
                </div>
                <div class="info-item">
                    <span class="info-label">City:</span>
                    <span class="info-value">${result.city}</span>
                </div>
            </div>
        `;

        showNotification('Strategy created via Factory pattern!', 'success');

    } catch (error) {
        showNotification('Factory error: ' + error.message, 'error');
    }
}

async function getFactoryInfo() {
    try {
        const response = await fetch(`${API_BASE}/factory/strategies`);
        const info = await response.json();

        document.getElementById('factoryOutput').innerHTML = `
            <div class="pattern-result">
                <h4>About Factory Pattern</h4>
                <p><strong>Purpose:</strong> ${info.description}</p>
                <p><strong>Method:</strong> <span class="code">${info.factoryMethod}</span></p>
                <p><strong>Available:</strong> ${info.availableStrategies.join(', ')}</p>
            </div>
        `;

    } catch (error) {
        showNotification('Failed to get factory info', 'error');
    }
}

// ==================== OBSERVER PATTERN ====================

async function showObservers() {
    try {
        const response = await fetch(`${API_BASE}/observers`);
        const info = await response.json();

        updateAppState({ observerCount: info.count });

        document.getElementById('observersOutput').innerHTML = `
            <div class="pattern-result">
                <h4>Observer Pattern Info</h4>
                <p><strong>Active Observers:</strong> ${info.count}</p>
                <p><strong>Observer Types:</strong></p>
                <ul style="margin-left: 20px; margin-top: 8px;">
                    ${info.types.map(type => `<li>${type}</li>`).join('')}
                </ul>
                <p style="margin-top: 12px; font-style: italic;">${info.description}</p>
            </div>
        `;

    } catch (error) {
        showNotification('Failed to get observer info', 'error');
    }
}

async function addObserver() {
    try {
        const response = await fetch(`${API_BASE}/observers/add`, {
            method: 'POST',
            headers: {'Content-Type': 'application/json'},
            body: JSON.stringify({ type: 'demo' })
        });

        const result = await response.json();
        showNotification('Demo observer added to system', 'success');
        showObservers(); // Refresh display

    } catch (error) {
        showNotification('Failed to add observer', 'error');
    }
}

// ==================== SINGLETON PATTERN ====================

async function checkSingleton() {
    try {
        const response = await fetch(`${API_BASE}/singleton`);
        const info = await response.json();

        document.getElementById('instanceHash').textContent = info.instanceHash;

        document.getElementById('singletonOutput').innerHTML = `
            <div class="pattern-result">
                <h4>Singleton Verification</h4>
                <p><strong>Pattern:</strong> ${info.pattern}</p>
                <p><strong>Implementation:</strong> <span class="code">${info.implementation}</span></p>
                <p><strong>Station:</strong> ${info.stationName}</p>
                <p><strong>Purpose:</strong> ${info.purpose}</p>
                <p style="margin-top: 12px; font-style: italic;">${info.description}</p>
            </div>
        `;

        showNotification('Singleton instance verified!', 'success');

    } catch (error) {
        showNotification('Failed to verify singleton', 'error');
    }
}

// ==================== WEATHER DISPLAY ====================

function displayWeatherData(data) {
    const weatherDiv = document.getElementById('weatherData');

    if (!data || Object.keys(data).length === 0) {
        weatherDiv.innerHTML = `
            <div class="weather-placeholder">
                <div class="placeholder-icon">🌤️</div>
                <p>No weather data available</p>
                <small>Click "Update Weather" to get data</small>
            </div>
        `;
        return;
    }

    weatherDiv.innerHTML = `
        <div class="weather-data">
            <div class="weather-item">
                <span class="weather-label">📍 City</span>
                <span class="weather-value">${data.city || 'N/A'}</span>
            </div>
            <div class="weather-item">
                <span class="weather-label">🌡️ Temperature</span>
                <span class="weather-value ${getTempColorClass(data.temperature)}">${data.temperature ? data.temperature.toFixed(1) + '°C' : 'N/A'}</span>
            </div>
            <div class="weather-item">
                <span class="weather-label">💧 Humidity</span>
                <span class="weather-value">${data.humidity ? data.humidity.toFixed(0) + '%' : 'N/A'}</span>
            </div>
            <div class="weather-item">
                <span class="weather-label">📊 Pressure</span>
                <span class="weather-value">${data.pressure ? data.pressure.toFixed(0) + ' hPa' : 'N/A'}</span>
            </div>
            <div class="weather-item">
                <span class="weather-label">💨 Wind Speed</span>
                <span class="weather-value">${data.windSpeed ? data.windSpeed.toFixed(1) + ' m/s' : 'N/A'}</span>
            </div>
            <div class="weather-item">
                <span class="weather-label">🕒 Last Update</span>
                <span class="weather-value">${data.timestamp ? new Date(data.timestamp).toLocaleTimeString() : 'N/A'}</span>
            </div>
        </div>
    `;

    // Add data to chart
    if (data.temperature && data.city) {
        addTemperatureData(data.city, data.temperature, data.timestamp);
    }
}

// ==================== SYSTEM STATUS ====================

async function getStatus() {
    try {
        const response = await fetch(`${API_BASE}/status`);
        const status = await response.json();

        updateAppState({
            stationName: status.stationName,
            currentStrategy: status.strategy
        });

        if (status.lastData) {
            displayWeatherData(status.lastData);
        }

        showNotification('System status updated', 'success');

    } catch (error) {
        showNotification('Failed to get system status', 'error');
    }
}

async function showAllPatterns() {
    try {
        const response = await fetch(`${API_BASE}/patterns`);
        const patterns = await response.json();

        const patternsHTML = Object.entries(patterns).map(([name, info]) => `
            <div class="pattern-result">
                <h4>${name}</h4>
                <p><strong>Purpose:</strong> ${info.purpose}</p>
                <p><strong>Classes:</strong> <span class="code">${info.classes}</span></p>
                <p><strong>Usage:</strong> ${info.usage}</p>
            </div>
        `).join('');

        document.getElementById('observersOutput').innerHTML = patternsHTML;
        showNotification('All patterns information loaded', 'info');

    } catch (error) {
        showNotification('Failed to load patterns info', 'error');
    }
}

// ==================== INITIALIZATION ====================

async function initializeApp() {
    try {
        // Initialize chart first
        initializeTemperatureChart();

        // Load initial status
        await getStatus();
        await showObservers();

        // Add event listener for city filter
        document.getElementById('chartCity').addEventListener('change', function() {
            const selectedCity = this.value;
            showNotification(`Now tracking temperature for ${selectedCity}`, 'info');
            // Re-filter existing data
            filterChartDataByCity(selectedCity);
        });

        showNotification('Weather Patterns Demo ready!', 'success');

    } catch (error) {
        showNotification('Failed to initialize app', 'error');
    }
}

// Filter chart data by city
function filterChartDataByCity(city) {
    if (city === 'All Cities') {
        // Show all data
        if (temperatureChart && temperatureData.temperatures.length > 0) {
            temperatureChart.data.labels = temperatureData.labels;
            temperatureChart.data.datasets[0].data = temperatureData.temperatures;
            temperatureChart.update();
        }
    } else {
        // Filter data for specific city
        const filteredData = {
            labels: [],
            temperatures: [],
            cities: [],
            timestamps: []
        };

        temperatureData.cities.forEach((dataCity, index) => {
            if (dataCity === city) {
                filteredData.labels.push(temperatureData.labels[index]);
                filteredData.temperatures.push(temperatureData.temperatures[index]);
                filteredData.cities.push(temperatureData.cities[index]);
                filteredData.timestamps.push(temperatureData.timestamps[index]);
            }
        });

        if (temperatureChart) {
            temperatureChart.data.labels = filteredData.labels;
            temperatureChart.data.datasets[0].data = filteredData.temperatures;
            temperatureChart.update();
        }
    }
}

// Start the application when page loads
document.addEventListener('DOMContentLoaded', initializeApp);

// Add keyboard shortcuts
document.addEventListener('keydown', (e) => {
    if (e.ctrlKey || e.metaKey) {
        switch(e.key) {
            case 'r':
                e.preventDefault();
                updateWeather();
                break;
            case 's':
                e.preventDefault();
                getStatus();
                break;
        }
    }
});